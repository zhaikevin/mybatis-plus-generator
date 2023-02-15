package main.java.org.intellij.plugins.mybatis.model;

/**
 * 界面配置
 */
public class Config {

    /**
     * 配置名称
     */
    private String name;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 实体名
     */
    private String modelName;

    /**
     * dao名称
     */
    private String daoName;

    /**
     * dao后缀
     */
    private String daoPostfix;

    /**
     * 工程目录
     */
    private String projectFolder;

    private String modelPackage;
    private String modelTargetFolder;
    private String modelMvnPath;

    private String daoPackage;
    private String daoTargetFolder;
    private String daoMvnPath;

    private String xmlPackage;
    private String xmlMvnPath;

    /**
     * 是否覆盖原xml
     */
    private boolean overrideFile;


    private boolean useLombokPlugin;

    private boolean serializable;

    public boolean isUseLombokPlugin() {
        return useLombokPlugin;
    }

    public void setUseLombokPlugin(boolean useLombokPlugin) {
        this.useLombokPlugin = useLombokPlugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public String getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getModelTargetFolder() {
        return modelTargetFolder;
    }

    public void setModelTargetFolder(String modelTargetFolder) {
        this.modelTargetFolder = modelTargetFolder;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoTargetFolder() {
        return daoTargetFolder;
    }

    public void setDaoTargetFolder(String daoTargetFolder) {
        this.daoTargetFolder = daoTargetFolder;
    }

    public String getXmlPackage() {
        return xmlPackage;
    }

    public void setXmlPackage(String xmlPackage) {
        this.xmlPackage = xmlPackage;
    }


    public String getModelMvnPath() {
        return modelMvnPath;
    }

    public void setModelMvnPath(String modelMvnPath) {
        this.modelMvnPath = modelMvnPath;
    }

    public String getDaoMvnPath() {
        return daoMvnPath;
    }

    public void setDaoMvnPath(String daoMvnPath) {
        this.daoMvnPath = daoMvnPath;
    }

    public String getXmlMvnPath() {
        return xmlMvnPath;
    }

    public void setXmlMvnPath(String xmlMvnPath) {
        this.xmlMvnPath = xmlMvnPath;
    }

    public String getDaoPostfix() {
        return daoPostfix;
    }

    public void setDaoPostfix(String daoPostfix) {
        this.daoPostfix = daoPostfix;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDaoName() {
        return daoName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public boolean isOverrideFile() {
        return overrideFile;
    }

    public void setOverrideFile(boolean overrideFile) {
        this.overrideFile = overrideFile;
    }

    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(boolean serializable) {
        this.serializable = serializable;
    }
}
