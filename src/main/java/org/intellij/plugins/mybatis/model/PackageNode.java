package main.java.org.intellij.plugins.mybatis.model;

import com.intellij.psi.PsiPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: zhaijizhong
 * @Date: 2023/3/31 17:18
 */
public class PackageNode {

    private PsiPackage psiPackage;

    private PackageNode parent;

    private List<PackageNode> children = new ArrayList<>();

    public void addChild(PackageNode child) {
        children.add(child);
    }

    public PsiPackage getPsiPackage() {
        return psiPackage;
    }

    public void setPsiPackage(PsiPackage psiPackage) {
        this.psiPackage = psiPackage;
    }

    public PackageNode getParent() {
        return parent;
    }

    public void setParent(PackageNode parent) {
        this.parent = parent;
    }

    public List<PackageNode> getChildren() {
        return children;
    }

    public void setChildren(List<PackageNode> children) {
        this.children = children;
    }

    public PackageNode(PsiPackage psiPackage) {
        this.psiPackage = psiPackage;
    }

    public PackageNode(PsiPackage psiPackage, PackageNode parent) {
        this.psiPackage = psiPackage;
        this.parent = parent;
    }
}
