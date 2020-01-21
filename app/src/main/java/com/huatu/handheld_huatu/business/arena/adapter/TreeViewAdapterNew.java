package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.customview.ArenaHomefTreeItemAccuracyView;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;

import java.util.ArrayList;

/**
 * Created by DongDong on 2016/7/4.
 */
public class TreeViewAdapterNew extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<HomeTreeBeanNew> homeTreeList;
    private OnItemClickListener listener;
    private RecyclerView parent;

    public TreeViewAdapterNew(Context mContext, ArrayList<HomeTreeBeanNew> homeTreeList, RecyclerView parent, OnItemClickListener listener) {
        this.mContext = mContext;
        this.homeTreeList = homeTreeList;
        this.parent = parent;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TreeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_tree_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        TreeViewHolder holder = (TreeViewHolder) viewHolder;

        final HomeTreeBeanNew tree = homeTreeList.get(position);

        if (tree.isLineUp()) {
            holder.item_line_top.setVisibility(View.VISIBLE);
        } else {
            holder.item_line_top.setVisibility(View.INVISIBLE);
        }
        if (tree.isLineDown()) {
            holder.item_line_bottom.setVisibility(View.VISIBLE);
        } else {
            holder.item_line_bottom.setVisibility(View.INVISIBLE);
        }
        switch (tree.getLevel()) {
            case 0:
                holder.rootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (tree.isExpand()) {
                    holder.item_img_expand.setImageResource(R.mipmap.tree_indicator1_expand);
                } else {
                    holder.item_img_expand.setImageResource(R.mipmap.tree_indicator1_fold);
                }
                break;
            case 1:
                holder.rootView.setBackgroundColor(Color.parseColor("#FFFAFA"));
                if (tree.isExpand()) {
                    holder.item_img_expand.setImageResource(R.mipmap.tree_indicator2_expand);
                } else {
                    holder.item_img_expand.setImageResource(R.mipmap.tree_indicator2_fold);
                }
                break;
            case 2:
                holder.rootView.setBackgroundColor(Color.parseColor("#FFF3F4"));
                holder.item_img_expand.setImageResource(R.mipmap.tree_indicator3);
                break;
        }
        if (tree.getUnfinishedPracticeId() > 0) {
            holder.item_again_excise.setVisibility(View.VISIBLE);
        } else {
            holder.item_again_excise.setVisibility(View.GONE);
        }
        holder.item_title.setText(tree.getName());
        holder.item_accuracy_view.updateViews(tree.getAccuracy());
        holder.item_scale.setText(mContext.getString(R.string.home_item_scale, tree.getRnum() + tree.getWnum(), tree.getQnum()));
    }

    @Override
    public int getItemCount() {
        return homeTreeList == null ? 0 : homeTreeList.size();
    }

    public HomeTreeBeanNew getItem(int position) {
        if (position < 0 || homeTreeList == null || position >= homeTreeList.size())
            return null;
        return homeTreeList.get(position);
    }

    private class TreeViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootView;
        ImageView item_img_expand;
        ImageView item_start_excise;
        TextView item_title;
        TextView item_scale;
        TextView item_again_excise;
        View item_line_top;
        View item_line_bottom;
        ArenaHomefTreeItemAccuracyView item_accuracy_view;

        TreeViewHolder(View itemView) {
            super(itemView);

            this.rootView = itemView.findViewById(R.id.root_view);
            this.item_img_expand = itemView.findViewById(R.id.item_img_expand);
            this.item_title = itemView.findViewById(R.id.item_title);
            this.item_start_excise = itemView.findViewById(R.id.item_start_excise);
            this.item_accuracy_view = itemView.findViewById(R.id.item_accuracy_view);
            this.item_scale = itemView.findViewById(R.id.item_scale);
            this.item_again_excise = itemView.findViewById(R.id.item_again_excise);
            this.item_line_top = itemView.findViewById(R.id.item_line_top);
            this.item_line_bottom = itemView.findViewById(R.id.item_line_bottom);

            // 点击展开
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.expandOrAnswerItem(getAdapterPosition());
                    }
                }
            });
            // 点击答题
            item_start_excise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.goAnswer(getAdapterPosition());

                }
            });
        }
    }

    public interface OnItemClickListener {

        void expandOrAnswerItem(int position);

        void goAnswer(int position);
    }
}
