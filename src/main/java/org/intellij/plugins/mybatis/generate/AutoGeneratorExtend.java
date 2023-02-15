package main.java.org.intellij.plugins.mybatis.generate;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.model.DasTypedObject;
import com.intellij.database.model.MultiRef;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import main.java.org.intellij.plugins.mybatis.model.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/2/9 17:25
 */
public class AutoGeneratorExtend extends AutoGenerator {

    private PsiElement psiElement;

    private Config config;//界面默认配置

    public AutoGeneratorExtend(PsiElement psiElement, Config config) {
        super();
        this.psiElement = psiElement;
        this.config = config;
    }

    public List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        List<TableInfo> tableInfos = new ArrayList<>();
        TableInfo tableInfo = new TableInfo();
        DbTable currentTable = (DbTable) psiElement;
        tableInfo.setName(currentTable.getName());
        tableInfo.setComment(currentTable.getComment());
        processTable(tableInfo, config, currentTable);
        tableInfos.add(tableInfo);
        return tableInfos;
    }

    private TableInfo processTable(TableInfo tableInfo, ConfigBuilder configBuilder, DbTable currentTable) {
        tableInfo.setEntityName(config.getModelName());
        tableInfo.setMapperName(config.getDaoName());
        tableInfo.setXmlName(config.getDaoName());
        tableInfo.setConvert(true);
        if (null != configBuilder.getGlobalConfig().getIdType()) {
            // 指定需要 IdType 场景
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.IdType.class.getCanonicalName());
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.TableId.class.getCanonicalName());
        }
        convertTableFields(tableInfo, configBuilder, currentTable);
        return tableInfo;
    }

    private TableInfo convertTableFields(TableInfo tableInfo, ConfigBuilder configBuilder, DbTable currentTable) {
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(currentTable);
        for (DasColumn column : columns) {
            TableField field = new TableField();
            field.setName(column.getName());
            field.setType(column.getDataType().typeName.toUpperCase());
            field.setPropertyName(configBuilder.getStrategyConfig(), processName(column.getName(), configBuilder.getStrategyConfig()));
            field.setColumnType(super.getDataSource().getTypeConvert().processTypeConvert(configBuilder.getGlobalConfig(), field.getType()));
            field.setComment(column.getComment());
            field.setKeyFlag(false);
            if (configBuilder.getStrategyConfig().includeSuperEntityColumns(field.getName())) {
                // 跳过公共字段
                commonFieldList.add(field);
                continue;
            }
            fieldList.add(field);
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
        setPrimaryKey(tableInfo, currentTable);
        return tableInfo;
    }

    private TableInfo setPrimaryKey(TableInfo tableInfo, DbTable currentTable) {
        DasTableKey primaryKey = DasUtil.getPrimaryKey(currentTable);
        List<TableField> fieldList = tableInfo.getFields();
        if (primaryKey != null) {
            MultiRef<? extends DasTypedObject> columnsRef = primaryKey.getColumnsRef();
            MultiRef.It<? extends DasTypedObject> iterate = columnsRef.iterate();
            while (iterate.hasNext()) {
                String columnName = iterate.next();
                for (TableField tableField : fieldList) {
                    if (columnName.equals(tableField.getName())) {
                        tableField.setKeyFlag(true);
                        break;
                    }
                }
            }
        }
        return tableInfo;
    }

    /**
     * 处理字段名称
     * @return 根据策略返回处理后的名称
     */
    private String processName(String name, StrategyConfig strategyConfig) {
        return processName(name, strategyConfig.getNaming(), strategyConfig.getFieldPrefix());
    }


    /**
     * 处理表/字段名称
     * @param name     ignore
     * @param strategy ignore
     * @param prefix   ignore
     * @return 根据策略返回处理后的名称
     */
    private String processName(String name, NamingStrategy strategy, String[] prefix) {
        boolean removePrefix = false;
        if (prefix != null && prefix.length != 0) {
            removePrefix = true;
        }
        String propertyName;
        if (removePrefix) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }

}
