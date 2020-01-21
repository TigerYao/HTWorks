package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bean.HomeworkSingleListBean;
import com.huatu.handheld_huatu.utils.TimeUtils;

import java.util.ArrayList;

public class HomeworkSingleListAdapter extends RecyclerView.Adapter<HomeworkSingleListAdapter.HomeworkHolder> {

    private Context context;
    private ArrayList<HomeworkSingleListBean.HomeworkSingleBean> data;
    private OnItemClickListener onItemClickListener;

    public HomeworkSingleListAdapter(Context context, ArrayList<HomeworkSingleListBean.HomeworkSingleBean> data, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HomeworkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeworkHolder(LayoutInflater.from(context).inflate(R.layout.homework_single_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkHolder holder, int position) {
        if (data == null || position >= data.size()) {
            return;
        }
        HomeworkSingleListBean.HomeworkSingleBean bean = data.get(position);
        holder.tvTitle.setText("问题" + (position + 1) + "-" + (bean.questionType == 5 ? "文章写作" : "标准答案"));
        holder.tvContent.setText(bean.stem);
        switch (bean.bizStatus) {
            case 0:     // 未开始
                holder.tvSubTitle.setTextColor(Color.parseColor("#9B9B9B"));
                holder.tvSubTitle.setText("未开始");
                break;
            case 1:     // 未提交
                holder.tvSubTitle.setTextColor(Color.parseColor("#5D9AFF"));
                holder.tvSubTitle.setText("未提交");
                break;
            case 3:     // 已批改
                holder.tvSubTitle.setTextColor(Color.parseColor("#DA9922"));
                holder.tvSubTitle.setText("得分" + bean.examScore + "/" + bean.score + " 用时" + TimeUtils.getSecond2MinTime(bean.spendtime) + " " + bean.inputWordNum + "字");
                break;
            case 2:     // 未批改（批改中）
            case 4:
                holder.tvSubTitle.setTextColor(Color.parseColor("#5D9AFF"));
                holder.tvSubTitle.setText("批改中");
                break;
            case 5:     // 被退回
                holder.tvSubTitle.setTextColor(Color.parseColor("#FF3F47"));
                holder.tvSubTitle.setText("被退回");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class HomeworkHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvSubTitle;
        TextView tvContent;

        HomeworkHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubTitle = itemView.findViewById(R.id.tv_subtitle);
            tvContent = itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
