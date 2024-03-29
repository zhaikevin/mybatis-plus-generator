package main.java.org.intellij.plugins.mybatis.ui;

import com.google.common.base.Joiner;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.containers.ContainerUtil;
import main.java.org.intellij.plugins.mybatis.generate.MybatisPlusGenerator;
import main.java.org.intellij.plugins.mybatis.model.Config;
import main.java.org.intellij.plugins.mybatis.model.PackageNode;
import main.java.org.intellij.plugins.mybatis.model.TableInfo;
import main.java.org.intellij.plugins.mybatis.utils.JTextFieldHintListener;
import main.java.org.intellij.plugins.mybatis.utils.StringUtils;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/2/8 14:36
 */
public class MybatisPlusGeneratorMainUI extends JFrame {

    private static final Logger LOG = Logger.getInstance(MybatisPlusGeneratorMainUI.class);

    private AnActionEvent anActionEvent;
    private Project project;
    private PsiElement[] psiElements;

    private JPanel contentPane = new JBPanel<>();
    private JButton buttonOK = new JButton("ok");
    private JButton buttonCancel = new JButton("cancel");

    private JTextField tableNameField = new JTextField(10);
    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JTextField daoPostfixField = new JTextField(10);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField daoNameField = new JTextField(10);
    private JTextField modelNameField = new JTextField(10);
    private JTextField keyField = new JTextField(10);
    private JTextField tableNamePrefixField = new JTextField(10);

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private JTextField modelMvnField = new JBTextField(15);
    private JTextField daoMvnField = new JBTextField(15);
    private JTextField xmlMvnField = new JBTextField(15);

    private JCheckBox overrideModelFileBox = new JCheckBox("是否覆盖原Model文件");
    private JCheckBox useLombokBox = new JCheckBox("是否使用lombok");
    private JCheckBox serializableBox = new JCheckBox("是否实现序列化接口");
    private JCheckBox useSwaggerBox = new JCheckBox("是否加swagger");
    private JCheckBox overrideDaoFileBox = new JCheckBox("是否覆盖原Dao文件");
    private JCheckBox overrideXmlFileBox = new JCheckBox("是否覆盖原Xml文件");

    public MybatisPlusGeneratorMainUI(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
    }

    public void draw() {
        setTitle("MyBatis Plus Generate Tool");
        setPreferredSize(new Dimension(800, 600));//设置大小
        setLocation(120, 100);
        pack();
        setVisible(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String modelName = StringUtils.dbStringToCamelStyle(tableName);
        String primaryKey = "";
        if (tableInfo.getPrimaryKeys().size() > 0) {
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }
        String projectFolder = project.getBasePath();
        boolean multiTable;
        if (psiElements.length > 1) {//多表时，只使用默认配置
            multiTable = true;
        } else {
            multiTable = false;
        }


        /**
         * table setting
         */
        JPanel tableNameFieldPanel = new JPanel();
        tableNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel tablejLabel = new JLabel("table  name:");
        tablejLabel.setSize(new Dimension(20, 30));
        tableNameFieldPanel.add(tablejLabel);
        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
        } else {
            tableNameField.setText(tableName);
            tableNameField.setEnabled(false);
        }
        tableNameFieldPanel.add(tableNameField);

        JPanel keyFieldPanel = new JPanel();
        keyFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        keyFieldPanel.add(new JLabel("primary key:"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg:primary key"));
        } else {
            keyField.setText(primaryKey);
            keyField.setEnabled(false);
        }
        keyFieldPanel.add(keyField);

        JPanel tableNamePrefixPanel = new JPanel();
        tableNamePrefixPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        tableNamePrefixPanel.add(new JLabel("table name prefix:"));
        tableNamePrefixField.addFocusListener(new JTextFieldHintListener(tableNamePrefixField, "eg:tb_"));
        tableNamePrefixPanel.add(tableNamePrefixField);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel.setBorder(BorderFactory.createTitledBorder("table setting"));
        if (psiElements.length > 1) {
            tablePanel.add(tableNamePrefixPanel);
        } else {
            tablePanel.add(tableNameFieldPanel);
            tablePanel.add(keyFieldPanel);
        }


        /**
         * project panel
         */
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        String lastPath = projectFolder.substring(projectFolder.lastIndexOf("/") + 1);
        String subFolderPath = projectFolder + "/" + lastPath + "-server";
        File file = new File(subFolderPath);
        if (file.exists()) {
            projectFolderBtn.setText(subFolderPath);
        } else {
            projectFolderBtn.setText(projectFolder);
        }
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);


        /**
         * model setting
         */
        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPanel.setBorder(BorderFactory.createTitledBorder("model setting"));
        if (!multiTable) {
            JPanel modelNameFieldPanel = new JPanel();
            modelNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            modelNameFieldPanel.add(new JLabel("file:"));
            modelNameField.setText(modelName);
            modelNameFieldPanel.add(modelNameField);
            modelPanel.add(modelNameFieldPanel);
        }

        PackageNode packageNode = getPackageNode();
        String modelPackage = getPackage(packageNode, "model");
        JBLabel labelLeft4 = new JBLabel("package:");
        modelPanel.add(labelLeft4);
        modelPackageField.setText(modelPackage);
        modelPanel.add(modelPackageField);
        JButton modelPackageFieldBtn = new JButton("...");
        modelPackageFieldBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("chooser model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
            MybatisPlusGeneratorMainUI.this.toFront();
        });
        modelPanel.add(modelPackageFieldBtn);
        modelPanel.add(new JLabel("path:"));
        modelMvnField.setText("src/main/java");
        modelPanel.add(modelMvnField);


        /**
         * dao setting
         */
        JPanel daoPanel = new JPanel();
        daoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoPanel.setBorder(BorderFactory.createTitledBorder("dao setting"));


        if (multiTable) { //多表
            daoPostfixField.setText("Mapper");
            daoPanel.add(new JLabel("dao postfix:"));
            daoPanel.add(daoPostfixField);
        } else {//单表
            daoNameField.setText(modelName + "Mapper");
            daoPanel.add(new JLabel("name:"));
            daoPanel.add(daoNameField);
        }

        String daoPackage = getPackage(packageNode, "dao");
        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        daoPackageField.setText(daoPackage);
        daoPanel.add(daoPackageField);
        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
            MybatisPlusGeneratorMainUI.this.toFront();
        });
        daoPanel.add(packageBtn2);
        daoPanel.add(new JLabel("path:"));
        daoMvnField.setText("src/main/java");
        daoPanel.add(daoMvnField);


        /**
         * xml mapper setting
         */
        JPanel xmlMapperPanel = new JPanel();
        xmlMapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlMapperPanel.setBorder(BorderFactory.createTitledBorder("xml mapper setting"));
        JLabel labelLeft6 = new JLabel("package:");
        xmlMapperPanel.add(labelLeft6);
        xmlPackageField.setText("mapper");
        xmlMapperPanel.add(xmlPackageField);
        xmlMapperPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlMapperPanel.add(xmlMvnField);

        /**
         * options
         */
        JBPanel optionsPanel = new JBPanel(new GridLayout(2, 5, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("options"));
        overrideModelFileBox.setSelected(true);
        overrideXmlFileBox.setSelected(true);
        serializableBox.setSelected(true);
        optionsPanel.add(overrideModelFileBox);
        optionsPanel.add(overrideDaoFileBox);
        optionsPanel.add(overrideXmlFileBox);
        optionsPanel.add(useLombokBox);
        optionsPanel.add(serializableBox);
        optionsPanel.add(useSwaggerBox);


        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setBorder(new EmptyBorder(10, 30, 5, 40));
        mainPanel.add(tablePanel);
        mainPanel.add(projectFolderPanel);
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);
        mainPanel.add(optionsPanel);

        JPanel paneBottom = new JPanel();//确认和取消按钮
        paneBottom.setLayout(new FlowLayout(2));
        paneBottom.add(buttonOK);
        paneBottom.add(buttonCancel);

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(paneBottom, BorderLayout.SOUTH);
        setContentPane(contentPane);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            dispose();
            List<String> result = new ArrayList<>();
            if (psiElements.length == 1) {
                Config generator_config = new Config();
                generator_config.setName(tableNameField.getText());
                generator_config.setTableName(tableNameField.getText());
                generator_config.setProjectFolder(projectFolderBtn.getText());

                generator_config.setModelPackage(modelPackageField.getText());
                generator_config.setDaoPackage(daoPackageField.getText());
                generator_config.setXmlPackage(xmlPackageField.getText());
                generator_config.setDaoName(daoNameField.getText());
                generator_config.setModelName(modelNameField.getText());
                generator_config.setPrimaryKey(keyField.getText());

                generator_config.setModelMvnPath(modelMvnField.getText());
                generator_config.setDaoMvnPath(daoMvnField.getText());
                generator_config.setXmlMvnPath(xmlMvnField.getText());

                generator_config.setOverrideModelFile(overrideModelFileBox.getSelectedObjects() != null);
                generator_config.setOverrideDaoFile(overrideDaoFileBox.getSelectedObjects() != null);
                generator_config.setOverrideXmlFile(overrideXmlFileBox.getSelectedObjects() != null);
                generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
                generator_config.setSerializable(serializableBox.getSelectedObjects() != null);
                generator_config.setSwagger(useSwaggerBox.getSelectedObjects() != null);
                result = new MybatisPlusGenerator(generator_config).execute(anActionEvent, psiElements[0]);
            } else {

                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String prefix = tableNamePrefixField.getText();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName.startsWith(prefix) ?
                            tableName.substring(tableName.indexOf(prefix) + prefix.length()) : tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }
                    Config generator_config = new Config();
                    generator_config.setName(tableName);
                    generator_config.setTableName(tableName);
                    generator_config.setProjectFolder(projectFolderBtn.getText());

                    generator_config.setModelPackage(modelPackageField.getText());
                    generator_config.setDaoPackage(daoPackageField.getText());
                    generator_config.setXmlPackage(xmlPackageField.getText());
                    generator_config.setDaoName(modelName + daoPostfixField.getText());
                    generator_config.setModelName(modelName);
                    generator_config.setPrimaryKey(primaryKey);

                    generator_config.setModelMvnPath(modelMvnField.getText());
                    generator_config.setDaoMvnPath(daoMvnField.getText());
                    generator_config.setXmlMvnPath(xmlMvnField.getText());

                    generator_config.setOverrideModelFile(overrideModelFileBox.getSelectedObjects() != null);
                    generator_config.setOverrideDaoFile(overrideDaoFileBox.getSelectedObjects() != null);
                    generator_config.setOverrideXmlFile(overrideXmlFileBox.getSelectedObjects() != null);
                    generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);
                    generator_config.setSerializable(serializableBox.getSelectedObjects() != null);
                    generator_config.setSwagger(useSwaggerBox.getSelectedObjects() != null);

                    result = new MybatisPlusGenerator(generator_config).execute(anActionEvent, psiElement);
                }

            }
            if (!result.isEmpty()) {
                Messages.showMessageDialog(Joiner.on("\n").join(result), "warnning", Messages.getWarningIcon());
            }

        } catch (Exception e) {
            LOG.error("generate failed:", e);
            Messages.showMessageDialog(e.getMessage(), "error", Messages.getErrorIcon());
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    private String getPackage(PackageNode rootNode, String packageName) {
        PackageNode root = rootNode;
        while (root.getChildren().size() == 1) {
            root = root.getChildren().get(0);
        }
        for (PackageNode packageNode : root.getChildren()) {
            if (packageNode.getPsiPackage().getName().equals(packageName)) {
                return packageNode.getPsiPackage().getQualifiedName();
            }
        }
        return root.getPsiPackage().getQualifiedName();
    }

    private PackageNode getPackageNode() {
        PsiManager psiManager = PsiManager.getInstance(project);
        FileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        List<PsiPackage> list = new ArrayList<>();
        fileIndex.iterateContent((fileOrDir) -> {
            if (fileOrDir.isDirectory() && fileIndex.isUnderSourceRootOfType(fileOrDir,
                    ContainerUtil.newHashSet(new JavaSourceRootType[]{JavaSourceRootType.SOURCE}))) {
                PsiDirectory psiDirectory = psiManager.findDirectory(fileOrDir);
                PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
                if (aPackage != null) {
                    System.out.println(aPackage.getQualifiedName());
                    list.add(aPackage);
                }
            }
            return true;
        });
        PsiPackage root = null;
        for (PsiPackage psiPackage : list) {
            if (psiPackage.getParentPackage() == null) {
                root = psiPackage;
                break;
            }
        }
        PackageNode rootNode = new PackageNode(root);
        getSubNode(rootNode, list);
        return rootNode;
    }

    private void getSubNode(PackageNode parentNode, List<PsiPackage> list) {
        for (PsiPackage psiPackage : list) {
            if (parentNode.getPsiPackage().equals(psiPackage.getParentPackage())) {
                PackageNode node = new PackageNode(psiPackage, parentNode);
                parentNode.addChild(node);
                getSubNode(node, list);
            }
        }
    }

}
