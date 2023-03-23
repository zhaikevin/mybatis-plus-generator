package main.java.org.intellij.plugins.mybatis.engine;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import main.java.org.intellij.plugins.mybatis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.generator.config.ConstVal.TEMPLATE_XML;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/2/20 16:03
 */
public class TemplateEngineDecorator extends AbstractTemplateEngine {

    private AbstractTemplateEngine engine;

    public TemplateEngineDecorator(AbstractTemplateEngine engine) {
        super();
        this.engine = engine;
    }

    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        engine.init(configBuilder);
        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        File file = new File(outputFile);
        if (file.exists() && templatePath.startsWith(TEMPLATE_XML)) {
            xmlOverWrite(objectMap, outputFile);
        } else {
            engine.writer(objectMap, templatePath, outputFile);
        }
    }

    @Override
    public String templateFilePath(String filePath) {
        return engine.templateFilePath(filePath);
    }

    private void xmlOverWrite(Map<String, Object> objectMap, String outputFile) throws Exception {
        File file = new File(outputFile);
        Document doc = parseXmlFromFile(file);
        baseColumnListOverWrite(objectMap, doc);
        baseResultMapOverWrite(objectMap, doc);
        xmlToFile(doc, file);
    }

    private Document parseXmlFromFile(File file) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private void xmlToFile(Document doc, File file) throws Exception {
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        FileOutputStream os = new FileOutputStream(file);
        XMLSerializer serializer = new XMLSerializer(os, format);
        serializer.serialize(doc);
    }

    private void baseColumnListOverWrite(Map<String, Object> objectMap, Document doc) {
        TableInfo tableInfo = (TableInfo) objectMap.get("table");
        NodeList sqls = doc.getElementsByTagName("sql");
        for (int i = 0; i < sqls.getLength(); i++) {
            Element element = (Element) sqls.item(i);
            String id = element.getAttribute("id");
            if (!StringUtils.isEmpty(id) && id.equals("Base_Column_List")) {
                StringBuilder sb = new StringBuilder();
                if (CollectionUtils.isNotEmpty(tableInfo.getCommonFields())) {
                    for (TableField field : tableInfo.getCommonFields()) {
                        sb.append(field.getName()).append(",");
                    }
                }
                sb.append(tableInfo.getFieldNames());
                while (element.getFirstChild() != null) {
                    element.removeChild(element.getFirstChild());
                }
                Text text = doc.createTextNode(sb.toString());
                element.appendChild(text);
            }
        }
    }

    private void baseResultMapOverWrite(Map<String, Object> objectMap, Document doc) {
        TableInfo tableInfo = (TableInfo) objectMap.get("table");
        NodeList maps = doc.getElementsByTagName("resultMap");
        for (int i = 0; i < maps.getLength(); i++) {
            Element element = (Element) maps.item(i);
            String id = element.getAttribute("id");
            if (!StringUtils.isEmpty(id) && id.equals("BaseResultMap")) {
                while (element.getFirstChild() != null) {
                    element.removeChild(element.getFirstChild());
                }
                for (TableField field : tableInfo.getFields()) {
                    if (field.isKeyFlag()) {
                        Element idElement = doc.createElement("id");
                        idElement.setAttribute("column", field.getName());
                        idElement.setAttribute("jdbcType", field.getType());
                        idElement.setAttribute("property", field.getPropertyName());
                        element.appendChild(idElement);
                    }
                }
                List<TableField> fieldList = new ArrayList<>();
                fieldList.addAll(tableInfo.getCommonFields());
                for (TableField field : tableInfo.getFields()) {
                    if (!field.isKeyFlag()) {
                        fieldList.add(field);
                    }
                }
                for (TableField field : fieldList) {
                    Element columnElement = doc.createElement("result");
                    columnElement.setAttribute("column", field.getName());
                    columnElement.setAttribute("jdbcType", field.getType());
                    columnElement.setAttribute("property", field.getPropertyName());
                    element.appendChild(columnElement);
                }
            }
        }
    }
}
