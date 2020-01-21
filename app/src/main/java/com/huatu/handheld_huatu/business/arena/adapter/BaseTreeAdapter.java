package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DongDong on 2016/7/4.
 */
public abstract class BaseTreeAdapter extends RecyclerView.Adapter {

    protected Context mContext;
    protected OnItemClickListener listener;

    ArrayList<HomeTreeBeanNew> homeTreeList = new ArrayList<>();                        // 主页下列表展示的内容
    private SparseArray<ArrayList<HomeTreeBeanNew>> homeTreeCatch = new SparseArray<>();        // 缓存的内容 key:parentId value:childList

    BaseTreeAdapter(Context mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public void setData(ArrayList<HomeTreeBeanNew> originTreeList) {
        homeTreeList.clear();
        homeTreeList.addAll(originTreeList);
        recursionHandleTreeData(originTreeList);
        notifyDataSetChanged();
    }

    public HomeTreeBeanNew getItem(int position) {
        if (position < 0 || homeTreeList == null || position >= homeTreeList.size())
            return null;
        return homeTreeList.get(position);
    }

    @Override
    public int getItemCount() {
        return homeTreeList == null ? 0 : homeTreeList.size();
    }

    /**
     * 展开或关闭节点
     */
    void expandOrClose(int position) {
        HomeTreeBeanNew homeTreeBeanNew = homeTreeList.get(position);
        if (homeTreeBeanNew == null) return;
        if (homeTreeBeanNew.getLevel() != 2) {  // 如果不是2，才判断
            if (homeTreeBeanNew.isExpand()) {        // 如果展开了就收起
                closeTreeChild(position);
            } else {                                 // 如果没展开就展开
                ArrayList<HomeTreeBeanNew> homeTreeBeanNews = homeTreeCatch.get(homeTreeBeanNew.getId());
                if (homeTreeBeanNews != null) {             // 本地有
                    expendTreeChild(homeTreeBeanNew.getId(), homeTreeBeanNews);
                }
            }
        }
    }

    // 展开
    private void expendTreeChild(int parentId, ArrayList<HomeTreeBeanNew> homeTreeBeanNews) {
        for (int i = 0; i < homeTreeList.size(); i++) {
            HomeTreeBeanNew homeTreeBeanNew = homeTreeList.get(i);
            if (homeTreeBeanNew.getId() == parentId) {
                homeTreeBeanNew.setExpand(true);
                homeTreeList.addAll(i + 1, homeTreeBeanNews);
                handleTreeLine();
                notifyItemChanged(i);
                notifyItemRangeInserted(i + 1, homeTreeBeanNews.size());
                break;
            }
        }
    }

    // 收起
    private void closeTreeChild(int position) {

        HomeTreeBeanNew homeTreeBeanNew = homeTreeList.get(position);
        homeTreeBeanNew.setExpand(false);

        ArrayList<HomeTreeBeanNew> deleteTree = new ArrayList<>();
        for (int i = position + 1; i < homeTreeList.size(); i++) {
            HomeTreeBeanNew treeBeanNew = homeTreeList.get(i);
            if (treeBeanNew.getLevel() > homeTreeBeanNew.getLevel()) {
                deleteTree.add(treeBeanNew);
            } else {
                break;
            }
        }
        for (HomeTreeBeanNew treeBeanNew : deleteTree) {
            treeBeanNew.setExpand(false);
        }
        homeTreeList.removeAll(deleteTree);
        handleTreeLine();
        notifyItemChanged(position);
        notifyItemRangeRemoved(position + 1, deleteTree.size());
    }

    // 处理知识树列表数据，是否显示上下线
    private void handleTreeLine() {
        int size = homeTreeList.size();
        for (int i = 0; i < size; i++) {
            HomeTreeBeanNew bean = homeTreeList.get(i);
            if (i == size - 1) {     // 最后一个
                if (bean.getLevel() == 0) {     // 如果是第一个上面没线
                    bean.setLineUp(false);
                } else {                        // 否则上面一定有线
                    bean.setLineUp(true);
                }
                bean.setLineDown(false);        // 下面一定没线
            } else {
                HomeTreeBeanNew beanAfter = homeTreeList.get(i + 1);
                if (bean.getLevel() == 0) {
                    bean.setLineUp(false);
                    if (beanAfter.getLevel() == 0) {
                        bean.setLineDown(false);
                    } else {
                        bean.setLineDown(true);
                    }
                } else {
                    bean.setLineUp(true);
                    if (beanAfter.getLevel() == 0) {
                        bean.setLineDown(false);
                    } else {
                        bean.setLineDown(true);
                    }
                }
            }
        }
    }

    /**
     * 递归调用，把数据放进homeTreeCatch
     */
    private void recursionHandleTreeData(List<HomeTreeBeanNew> tree) {
        if (tree == null || tree.size() == 0) {
            return;
        }
        for (HomeTreeBeanNew treeBean : tree) {
            if (treeBean.getChildren() != null && treeBean.getChildren().size() > 0) {
                // 把子节点都打上父类id
                for (HomeTreeBeanNew child : treeBean.getChildren()) {
                    child.setParent(treeBean);
                }
                // key:parentId value:childList
                homeTreeCatch.put(treeBean.getId(), treeBean.getChildren());
                // 递归
                recursionHandleTreeData(treeBean.getChildren());
            }
        }
    }

    public interface OnItemClickListener {
        void doSomeThing(int type, HomeTreeBeanNew beanNew);
    }
}
