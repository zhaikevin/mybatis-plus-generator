package main.java.org.intellij.plugins.mybatis.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import main.java.org.intellij.plugins.mybatis.ui.MybatisPlusGeneratorMainUI;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/2/8 14:05
 */
public class MybatisPlusGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
                return;
            }
        }
        MybatisPlusGeneratorMainUI ui = new MybatisPlusGeneratorMainUI(e);
        ui.draw();
    }
}
