package main.java.org.intellij.plugins.mybatis.generate;


import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import main.java.org.intellij.plugins.mybatis.model.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * 生成mybatis相关代码
 */
public class MybatisPlusGenerator {
    private String currentDbName;
    private Project project;
    private Config config;//界面默认配置

    public MybatisPlusGenerator(Config config) {
        this.config = config;
    }

    /**
     * 自动生成的主逻辑
     * @param anActionEvent
     * @throws Exception
     */
    public List<String> execute(final AnActionEvent anActionEvent, PsiElement psiElement) throws Exception {
        List<String> result = new ArrayList<>();
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        if (Objects.isNull(psiElement)) {
            result.add("can not generate! \nplease select table");
            return result;
        }
        if (!(psiElement instanceof DbTable)) {
            result.add("can not generate! \nplease select table");
            return result;
        }
        return result;
    }

    /**
     * 创建所需目录
     * @param config
     */
    private void createFolderForNeed(Config config) {

        String modelMvnPath = config.getModelMvnPath();
        String daoMvnPath = config.getDaoMvnPath();
        String xmlMvnPath = config.getXmlMvnPath();

        String modelPath = config.getProjectFolder() + "/" + modelMvnPath + "/";
        String daoPath = config.getProjectFolder() + "/" + daoMvnPath + "/";
        String xmlPath = config.getProjectFolder() + "/" + xmlMvnPath + "/";

        File modelFile = new File(modelPath);
        if (!modelFile.exists() && !modelFile.isDirectory()) {
            modelFile.mkdirs();
        }

        File daoFile = new File(daoPath);
        if (!daoFile.exists() && !daoFile.isDirectory()) {
            daoFile.mkdirs();
        }

        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists() && !xmlFile.isDirectory()) {
            xmlFile.mkdirs();
        }

    }

}
