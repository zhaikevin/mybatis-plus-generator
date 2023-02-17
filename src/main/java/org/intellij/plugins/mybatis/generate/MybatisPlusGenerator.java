package main.java.org.intellij.plugins.mybatis.generate;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
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

        if (Objects.isNull(psiElement)) {
            result.add("can not generate! \nplease select table");
            return result;
        }
        if (!(psiElement instanceof DbTable)) {
            result.add("can not generate! \nplease select table");
            return result;
        }
        AutoGeneratorExtend autoGeneratorExtend = new AutoGeneratorExtend(psiElement, config);
        GlobalConfig globalConfig = new GlobalConfig()
                .setOutputDir(config.getProjectFolder())
                .setEntityMvnPath(config.getModelMvnPath())
                .setMapperMvnPath(config.getDaoMvnPath())
                .setXmlMvnPath(config.getXmlMvnPath())
                .setAuthor("auto generate")
                .setDateType(DateType.ONLY_DATE)
                .setBaseColumnList(true)
                .setIdType(IdType.AUTO)
                .setBaseResultMap(true)
                .setOpen(false)
                .setSwagger2(config.isSwagger());
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(config.isUseLombokPlugin())
                .setEntityTableFieldAnnotationEnable(true)
                .setEntitySerialVersionUID(config.isSerializable());
        PackageConfig packageConfig = new PackageConfig()
                .setParent(null)
                .setEntity(config.getModelPackage())
                .setMapper(config.getDaoPackage())
                .setXml(config.getXmlPackage());
        TemplateConfig templateConfig = new TemplateConfig()
                .setController(null)
                .setService(null)
                .setServiceImpl(null);
        AbstractTemplateEngine templateEngine = new FreemarkerTemplateEngine();
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        }.setFileCreate((configBuilder, fileType, filePath) -> {
            File file = new File(filePath);
            boolean exist = file.exists();
            if (!exist) {
                file.getParentFile().mkdirs();
                return true;
            }
            switch (fileType) {
                case ENTITY:
                    return config.isOverrideModelFile();
                case MAPPER:
                    return config.isOverrideDaoFile();
                case XML:
                    return config.isOverrideXmlFile();
                default:
                    return configBuilder.getGlobalConfig().isFileOverride();
            }
        });
        autoGeneratorExtend.setGlobalConfig(globalConfig);
        autoGeneratorExtend.setDataSource(getDataSourceConfig(psiElement));
        autoGeneratorExtend.setStrategy(strategyConfig);
        autoGeneratorExtend.setPackageInfo(packageConfig);
        autoGeneratorExtend.setTemplate(templateConfig);
        autoGeneratorExtend.setTemplateEngine(templateEngine);
        autoGeneratorExtend.setCfg(injectionConfig);
        autoGeneratorExtend.execute();
        return result;
    }

    private DataSourceConfig getDataSourceConfig(PsiElement psiElement) {
        RawConnectionConfig connectionConfig = ((DbTable) psiElement).getDataSource().getConnectionConfig();
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(connectionConfig.getUrl());
        dsc.setDriverName(connectionConfig.getDriverClass());
        String driverClass = connectionConfig.getDriverClass();
        DbType dbType;
        if (driverClass.contains("mysql")) {
            dbType = DbType.MYSQL;
        } else if (driverClass.contains("oracle")) {
            dbType = DbType.ORACLE;
        } else if (driverClass.contains("postgresql")) {
            dbType = DbType.POSTGRE_SQL;
        } else if (driverClass.contains("sqlserver")) {
            dbType = DbType.SQL_SERVER;
        } else if (driverClass.contains("mariadb")) {
            dbType = DbType.MARIADB;
        } else {
            dbType = DbType.OTHER;
        }
        dsc.setDbType(dbType);
        return dsc;
    }

}
