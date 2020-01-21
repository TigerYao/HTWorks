package com.huatu.handheld_huatu.business.me.tree;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;

import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public class TreeShowAdapter extends BasicTreeAdapter {


    public TreeShowAdapter(ListView listView, Context context, List<TreeViewBean> trees, int def) {
        super(listView, context, trees, def);
    }

    @Override
    public View getConverView(final TreeViewBean tree, int position, View converView, ViewGroup parent) {
        Holder holder;
        if (converView == null) {
            holder = new Holder();

            converView = inflater.inflate(R.layout.item_collection_tree, parent, false);

            holder.text_name = (TextView) converView.findViewById(R.id.text_name);
//            holder.text_look = (TextView) converView.findViewById(R.id.text_look);
//            holder.text_redo = (TextView) converView.findViewById(R.id.text_redo);
            holder.image_top = (ImageView) converView.findViewById(R.id.image_top);
            holder.image_select = (ImageView) converView.findViewById(R.id.image_select);
            holder.image_bottom = (ImageView) converView.findViewById(R.id.image_bottom);
            holder.view_line = converView.findViewById(R.id.view_line);

            converView.setTag(holder);
        } else {
            holder = (Holder) converView.getTag();
        }
        holder.text_name.setText(tree.getName());

        int level = tree.getLevel();
        boolean expand = tree.isExpand();
        List<TreeViewBean> children = tree.getChildren();
        boolean end = tree.isEnd();
        if (level == 0) {
            if (expand) {
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator1_expand);
                holder.image_top.setVisibility(View.INVISIBLE);
                holder.image_bottom.setVisibility(View.VISIBLE);
            } else {
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator1_fold);
                holder.image_top.setVisibility(View.INVISIBLE);
                holder.image_bottom.setVisibility(View.INVISIBLE);
            }
        } else if (level == 1) {
            holder.image_top.setVisibility(View.VISIBLE);
            if (expand) {
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator2_expand);
                holder.image_bottom.setVisibility(View.VISIBLE);
            } else {
                if (end) {
                    holder.image_bottom.setVisibility(View.INVISIBLE);
                } else {
                    holder.image_bottom.setVisibility(View.VISIBLE);
                }
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator2_fold);
            }
        } else if (level == 2) {
            holder.image_top.setVisibility(View.VISIBLE);
            holder.image_select.setBackgroundResource(R.mipmap.tree_indicator3);
            if (end) {
                holder.image_bottom.setVisibility(View.INVISIBLE);
            } else {
                holder.image_bottom.setVisibility(View.VISIBLE);
            }
        }

        if (level == 0) {
            if (position == 0) {
                holder.view_line.setVisibility(View.INVISIBLE);
            } else {
                holder.view_line.setVisibility(View.VISIBLE);
            }
        } else {
            holder.view_line.setVisibility(View.INVISIBLE);
        }

        holder.text_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, tree.getName() + tree.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.text_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, tree.getName() + tree.getId(), Toast.LENGTH_SHORT).show();
            }
        });


        return converView;
    }


    class Holder {
        TextView text_name;
        ImageView image_top;
        ImageView image_select;
        ImageView image_bottom;
        View view_line;
        TextView text_look;
        TextView text_redo;
    }
}
