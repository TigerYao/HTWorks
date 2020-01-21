package com.huatu.handheld_huatu.utils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DongDong on 2016/7/4.
 */
public class TreeHelper {
    /**
     * 设置图标
     *
     * @param tree tree
     */
    private static void setTreeIcon(HomeTreeBean tree) {
        switch (tree.getLevel()) {
            case 0:
                if (tree.isLeaf()) {
                    tree.setIcon(R.mipmap.tree_indicator1_none);
                    tree.setLineMode(0);
                } else {
                    if (tree.isExpand()) {
                        tree.setIcon(R.mipmap.tree_indicator1_expand);
                        tree.setLineMode(3);
                    } else {
                        tree.setIcon(R.mipmap.tree_indicator1_fold);
                        tree.setLineMode(0);
                    }
                }
                break;
            case 1:
                if (tree.isLeaf()) {
                    if (tree.isLast()) {
                        tree.setLineMode(2);
                    } else {
                        tree.setLineMode(1);
                    }
                    tree.setIcon(R.mipmap.tree_indicator2_none);
                } else {
                    if (tree.isExpand()) {
                        tree.setIcon(R.mipmap.tree_indicator2_expand);
                        tree.setLineMode(1);
                    } else {
                        if (tree.isLast()) {
                            tree.setLineMode(2);
                        } else {
                            tree.setLineMode(1);
                        }
                        tree.setIcon(R.mipmap.tree_indicator2_fold);
                    }
                }
                break;
            case 2:
                if (tree.isLast()) {
                    tree.setLineMode(2);
                } else {
                    tree.setLineMode(1);
                }
                tree.setIcon(R.mipmap.tree_indicator3);
                break;
        }
    }

    /**
     * 重组数据
     *
     * @param datas List<HomeTreeBean>
     * @return List<HomeTreeBean>
     */
    private static List<HomeTreeBean> resetTreeDatas(List<HomeTreeBean> datas) {
        for (int i = 0; i < datas.size(); i++) {
            HomeTreeBean tree1 = datas.get(i);
            if (tree1.getLevel() == 0) {
                if (!tree1.isLeaf()) {
                    for (int j = 0; j < tree1.getChildren().size(); j++) {
                        HomeTreeBean tree2 = tree1.getChildren().get(j);
                        if (tree2.getLevel() == 1) {
                            tree2.setFather(tree1);
                            if (j == tree1.getChildren().size() - 1) {
                                tree2.setLast(true);
                            }
                            if (!tree2.isLeaf()) {
                                for (int k = 0; k < tree2.getChildren().size(); k++) {
                                    HomeTreeBean tree3 = tree2.getChildren().get(k);
                                    if (tree3.getLevel() == 2) {
                                        tree3.setFather(tree2);
                                        if (k == tree2.getChildren().size() - 1 && tree2.isLast()) {
                                            tree3.setLast(true);
                                        }
                                    } else {
                                        return null;
                                    }
                                }
                            }
                        } else {
                            return null;
                        }
                    }
                }
                tree1.setLast(true);
            } else {
                return null;
            }
        }
        return datas;
    }

    /**
     * 获取根节点数据
     *
     * @param datas List<HomeTreeBean>
     * @return List<HomeTreeBean>
     */
    private static List<HomeTreeBean> getTreeRoot(List<HomeTreeBean> datas) {
        List<HomeTreeBean> root = new ArrayList<>();
        for (HomeTreeBean tree : datas) {
            if (tree.isRoot())
                root.add(tree);
        }
        return root;
    }

    /**
     * 传入我们的普通bean，转化为我们排序后的Node
     *
     * @param datas              List<HomeTreeBean>
     * @param defaultExpandLevel defaultExpandLevel
     * @return List<HomeTreeBean>
     */
    public static List<HomeTreeBean> getSortedTrees(List<HomeTreeBean> datas, int defaultExpandLevel) {
        List<HomeTreeBean> result = new ArrayList<>();
        // 将用户数据转化为List<HomeTreeBean>
        List<HomeTreeBean> trees = resetTreeDatas(datas);
        // 拿到根节点
        List<HomeTreeBean> rootTrees = getTreeRoot(trees);
        // 排序以及设置tree间关系
        for (HomeTreeBean tree : rootTrees) {
            addTree(result, tree, defaultExpandLevel, 1);
        }
        return result;
    }

    private static void addTree(List<HomeTreeBean> result, HomeTreeBean tree, int defaultExpandLevel, int currentLevel) {
        result.add(tree);
        if (defaultExpandLevel >= currentLevel) {
            tree.setExpand(true);
        }

        if (tree.isLeaf())
            return;
        for (int i = 0; i < tree.getChildren().size(); i++) {
            addTree(result, tree.getChildren().get(i), defaultExpandLevel,
                    currentLevel + 1);
        }
    }

    /**
     * 过滤出所有可见的Node
     *
     * @param trees List<HomeTreeBean>
     * @return List<HomeTreeBean>
     */
    public static List<HomeTreeBean> filterVisibleTree(List<HomeTreeBean> trees) {
        List<HomeTreeBean> result = new ArrayList<>();

        for (HomeTreeBean tree : trees) {
            // 如果为跟节点，或者上层目录为展开状态
            if (tree.isRoot() || tree.isParentExpand()) {
                setTreeIcon(tree);
                result.add(tree);
            }
        }
        return result;
    }
}
