package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;

/**
 * Created by DongDong on 2016/7/4.
 */
public class ArenaErrExportAdapter extends BaseTreeAdapter {

    private int[] check = {R.mipmap.ex_err_0, R.mipmap.ex_err_1, R.mipmap.ex_err_2};

    private static final int CHECK = 0;

    public ArenaErrExportAdapter(Context mContext, BaseTreeAdapter.OnItemClickListener listener) {
        super(mContext, listener);
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TreeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_err_arena_export, viewGroup, false));
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
        holder.tvTitle.setText(tree.getName());

        final int wnum = tree.getWnum();
        String errNum = String.valueOf(wnum);
        SpannableString spannableString = new SpannableString(errNum + "道错题");
        int index = errNum.length();
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.common_style_text_color)), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvErrNum.setText(spannableString);

        holder.tvCheck.setBackgroundResource(check[tree.getCheckedState()]);
    }

    class TreeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rootView;
        View item_line_top;
        View item_line_bottom;
        ImageView item_img_expand;

        TextView tvTitle;

        TextView tvErrNum;

        RelativeLayout rlCheck;
        TextView tvCheck;


        TreeViewHolder(View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.root_view);
            item_line_top = itemView.findViewById(R.id.item_line_top);
            item_line_bottom = itemView.findViewById(R.id.item_line_bottom);
            item_img_expand = itemView.findViewById(R.id.item_img_expand);

            tvTitle = itemView.findViewById(R.id.tv_title);

            tvErrNum = itemView.findViewById(R.id.tv_err_num);

            rlCheck = itemView.findViewById(R.id.rl_check);
            tvCheck = itemView.findViewById(R.id.tv_check);

            // 点击展开
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandOrClose(getAdapterPosition());
                }
            });

            rlCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.doSomeThing(CHECK, getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
