package com.huatu.handheld_huatu.business.me.tree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;
import com.huatu.utils.ArrayUtils;

import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public abstract class BasicTreeAdapter extends BaseAdapter {

    protected Context context;

    protected List<TreeViewBean> visiableTrees;     // 可见的tree
    protected List<TreeViewBean> allTrees;          // 所有的tree
    protected LayoutInflater inflater;

    protected int offset = 0;                       // 偏移量，如果listView有Header，就会有 1 的偏移量

    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;


    public interface OnTreeNodeClickListener {
        void onClick(TreeViewBean tree, int position);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }


    public BasicTreeAdapter(ListView listView, Context context, List<TreeViewBean> trees, int def) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        allTrees = TreeShowHelper.sortAllData(trees, def);

        visiableTrees = TreeShowHelper.getVisiableTree(allTrees);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                expandOrCollapse(i - offset);
                if (onTreeNodeClickListener != null) {
                    onTreeNodeClickListener.onClick(visiableTrees.get(i - offset), i);
                }
            }
        });
    }

    protected void expandOrCollapse(int i) {
        if(i<0 || (i>= ArrayUtils.size(visiableTrees)) ) return;

        TreeViewBean tree = visiableTrees.get(i);
        if (tree != null) {
            List<TreeViewBean> children = tree.getChildren();
            if (children != null && children.size() > 0) {
                tree.setExpand(!tree.isExpand());
                visiableTrees = TreeShowHelper.getVisiableTree(allTrees);
                notifyDataSetChanged();
            }
        }
    }


    @Override
    public int getCount() {
        if (visiableTrees != null && visiableTrees.size() > 0)
            return visiableTrees.size();
        else
            return 0;
    }

    public List<TreeViewBean> getVisiableTrees() {
        return visiableTrees;
    }

    @Override
    public Object getItem(int i) {
        return visiableTrees.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TreeViewBean tree = visiableTrees.get(i);
        View converView = getConverView(tree, i, view, viewGroup);

        return converView;
    }

    public abstract View getConverView(TreeViewBean tree, int position, View converView,
                                       ViewGroup parent);
}