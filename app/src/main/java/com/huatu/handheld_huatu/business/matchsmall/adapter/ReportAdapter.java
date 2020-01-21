package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallReportListBean;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SmallReportListBean.SmallReportBean> datas;
    private OnItemClickListener listener;

    public ReportAdapter(Context mContext, List<SmallReportListBean.SmallReportBean> mSimulationContestDetailBeans, OnItemClickListener listener) {
        this.mContext = mContext;
        this.datas = mSimulationContestDetailBeans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ScHolder(LayoutInflater.from(mContext).inflate(R.layout.small_match_report_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ScHolder holder = (ScHolder) viewHolder;
        SmallReportListBean.SmallReportBean bean = datas.get(position);
        holder.tvTitle.setText(bean.name);
        String describe = "<font color=\"#EC74A0\">" + bean.qcount + "</font>道题      " +
                "<font color=\"#EC74A0\">" + bean.submitCount + "</font>人参加      " +
                "已击败<font color=\"#EC74A0\">" + bean.beatRate + "%</font>的考生";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvDescribe.setText(Html.fromHtml(describe, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvDescribe.setText(Html.fromHtml(describe));
        }
    }

    @Override
    public int getItemCount() {
        return (datas != null && datas.size() > 0) ? datas.size() : 0;
    }

    class ScHolder extends RecyclerView.ViewHolder {

        ImageView ivReport;             // 查看报告
        TextView tvTitle;               // 名称
        TextView tvDescribe;            // 描述

        ScHolder(View itemView) {
            super(itemView);

            ivReport = itemView.findViewById(R.id.iv_report);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescribe = itemView.findViewById(R.id.tv_describe);

            ivReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.GO_REPORT, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {

        int GO_REPORT = 0;

        void onClick(int type, int position);
    }
}
