package main.java.org.intellij.plugins.mybatis.ui;

import com.google.common.base.Joiner;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import main.java.org.intellij.plugins.mybatis.generate.MybatisPlusGenerator;
import main.java.org.intellij.plugins.mybatis.model.Config;
import main.java.org.intellij.plugins.mybatis.model.TableInfo;
import main.java.org.intellij.plugins.mybatis.utils.JTextFieldHintListener;
import main.java.org.intellij.plugins.mybatis.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/2/8 14:36
 */
public class MybatisPlusGeneratorMainUI extends JFrame {

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

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private JTextField modelMvnField = new JBTextField(15);
    private JTextField daoMvnField = new JBTextField(15);
    private JTextField xmlMvnField = new JBTextField(15);


    public MybatisPlusGeneratorMainUI(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        setTitle("MyBatis Plus Generate Tool");
        setPreferredSize(new Dimension(1200, 700));//设置大小
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
        }
        tableNameFieldPanel.add(tableNameField);

        JPanel keyFieldPanel = new JPanel();
        keyFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        keyFieldPanel.add(new JLabel("主键（选填）:"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg:primary key"));
        } else {
            keyField.setText(primaryKey);
        }
        keyFieldPanel.add(keyField);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel.setBorder(BorderFactory.createTitledBorder("table setting"));
        tablePanel.add(tableNameFieldPanel);
        tablePanel.add(keyFieldPanel);


        /**
         * project panel
         */
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        projectFolderBtn.setText(projectFolder);
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
        JBLabel labelLeft4 = new JBLabel("package:");
        modelPanel.add(labelLeft4);
        modelPackageField.setText("generate");
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


        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        daoPackageField.setText("generate");
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
        xmlPackageField.setText("generator");
        xmlMapperPanel.add(xmlPackageField);
        xmlMapperPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlMapperPanel.add(xmlMvnField);

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setBorder(new EmptyBorder(10, 30, 5, 40));
        if (!multiTable) {
            mainPanel.add(tablePanel);
        }
        mainPanel.add(projectFolderPanel);
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);


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

                result = new MybatisPlusGenerator(generator_config).execute(anActionEvent, psiElements[0]);
            } else {

                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
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
                    result = new MybatisPlusGenerator(generator_config).execute(anActionEvent, psiElement);
                }

            }
            if (!result.isEmpty()) {
                Messages.showMessageDialog(Joiner.on("\n").join(result), "warnning", Messages.getWarningIcon());
            }

        } catch (Exception e1) {
            Messages.showMessageDialog(e1.getMessage(), "error", Messages.getErrorIcon());
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
