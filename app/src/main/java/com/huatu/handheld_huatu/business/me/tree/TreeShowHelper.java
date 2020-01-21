package com.huatu.handheld_huatu.business.me.tree;

import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public class TreeShowHelper {


    public static List<TreeViewBean> sortAllData(List<TreeViewBean> trees, int def) {
        ArrayList<TreeViewBean> result = new ArrayList<>();

        //将所有的数据转换为list
        //先添加所有level = 0的数据，在添加所有level = 1的数据，最后添加level = 2的数据
        List<TreeViewBean> allTreeList = transformAllTree(trees);

        //获取根节点
        List<TreeViewBean> rootTree = getRootTree(trees);

        //排序以及设置tree之间的关系
        for (TreeViewBean root : rootTree) {
            sortAndRelation(result, root, def, 1);
        }

        return result;
    }

    /**
     * 排序以及设置tree之间的关系
     *
     * @param result  处理后的结果
     * @param root    根tree
     * @param def     默认展开级
     * @param current
     */
    private static void sortAndRelation(ArrayList<TreeViewBean> result, TreeViewBean root, int def, int current) {
        result.add(root);

        if (def >= current) {
            root.setExpand(true);
        }

        List<TreeViewBean> children = root.getChildren();
        if (children != null && children.size() > 0) {
            for (TreeViewBean tree : children) {
                sortAndRelation(result, tree, def, current + 1);
            }
        } else {
            return;
        }
    }

    /**
     * 获取所有可见的tree
     *
     * @param trees
     * @return
     */
    public static List<TreeViewBean> getVisiableTree(List<TreeViewBean> trees) {
        ArrayList<TreeViewBean> visiableTree = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            //level=0 的为根节点，默认展开
            TreeViewBean tree = trees.get(i);
            if (tree.getLevel() == 0) {
                visiableTree.add(tree);
            }
            // 判断tree的父节点是否展开，如果展开，那么显示
            if (tree.isParentExpand()) {
                visiableTree.add(tree);
            }
        }

        return visiableTree;
    }

    private static List<TreeViewBean> getRootTree(List<TreeViewBean> trees) {
        ArrayList<TreeViewBean> rootTree = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            TreeViewBean tree = trees.get(i);
            rootTree.add(tree);
        }
        return rootTree;
    }

    public static List<TreeViewBean> transformAllTree(List<TreeViewBean> trees) {
        ArrayList<TreeViewBean> allTree = new ArrayList<>();
        ArrayList<TreeViewBean> level0 = new ArrayList<>();
        ArrayList<TreeViewBean> level1 = new ArrayList<>();
        ArrayList<TreeViewBean> level2 = new ArrayList<>();

        for (int x = 0; x < trees.size(); x++) {
            //level=0 的节点
            TreeViewBean tree0 = trees.get(x);
            tree0.setLevel(0);
            level0.add(tree0);

            List<TreeViewBean> children1 = tree0.getChildren();
            if (children1 != null && children1.size() > 0) {
                for (int y = 0; y < children1.size(); y++) {
                    //level=1的节点
                    TreeViewBean tree1 = children1.get(y);
                    tree1.setFather(tree0);
                    if (y == children1.size() - 1) {
                        tree1.setEnd(true);
                    } else {
                        tree1.setEnd(false);
                    }
                    tree1.setLevel(1);
                    level1.add(tree1);

                    List<TreeViewBean> children2 = tree1.getChildren();
                    if (children2 != null && children2.size() > 0) {
                        for (int z = 0; z < children2.size(); z++) {
                            //level=2 的节点
                            TreeViewBean tree2 = children2.get(z);
                            tree2.setFather(tree1);
                            if (y == children1.size() - 1 && z == children2.size() - 1) {
                                tree2.setEnd(true);
                            } else {
                                tree2.setEnd(false);
                            }
                            tree2.setLevel(2);
                            level2.add(tree2);
                        }
                    }
                }
            }
        }

        level1.addAll(level2);
        level0.addAll(level1);
        allTree.addAll(level0);

        return allTree;
    }
}
