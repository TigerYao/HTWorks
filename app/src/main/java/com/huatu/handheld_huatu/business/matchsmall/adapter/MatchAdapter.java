package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallMatchBean;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SmallMatchBean> datas;
    private int requestType;
    private OnItemClickListener listener;

    public MatchAdapter(Context mContext, List<SmallMatchBean> mSimulationContestDetailBeans, int requestType, OnItemClickListener listener) {
        this.mContext = mContext;
        this.datas = mSimulationContestDetailBeans;
        this.requestType = requestType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == 0) {
            return new ScHolder(LayoutInflater.from(mContext).inflate(R.layout.small_match_item, viewGroup, false));
        } else {
            return new ScDescribeHolder(LayoutInflater.from(mContext).inflate(R.layout.sc_main_describe_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 1) {            // 描述
            ScDescribeHolder holder = (ScDescribeHolder) viewHolder;
            if (!StringUtils.isEmpty(datas.get(0).description)) {
                if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {
                    holder.tvDescribe.setText("模考说明：\n" + datas.get(0).description);
                } else {
                    holder.tvDescribe.setText("测验说明：\n" + datas.get(0).description);
                }
            } else {
                holder.tvDescribe.setText("");
            }
        } else {                            // 模考
            ScHolder holder = (ScHolder) viewHolder;

            if (datas == null || position >= datas.size()) return;

            SmallMatchBean bean = datas.get(position);

            holder.tvNum.setText(bean.joinCount + "");
            holder.tvTitle.setText(bean.name);
            if (bean.startTime > 0) {
                holder.llTime.setVisibility(View.VISIBLE);
                String time = TimeUtils.getFormatData("yyyy.MM.dd", bean.startTime) + "-" + TimeUtils.getFormatData("yyyy.MM.dd", bean.endTime);
                holder.tvTime.setText(time);
            } else {
                holder.llTime.setVisibility(View.GONE);
            }
            holder.tvPoint.setText(bean.pointsName);
            String describe = "本次测验共有<font color=\"#EC74A0\">" + bean.qcount + "道题</font>，标准用时<font color=\"#EC74A0\">" + bean.limitTime / 60 + "分钟</font>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvDescribe.setText(Html.fromHtml(describe, Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvDescribe.setText(Html.fromHtml(describe));
            }

            // 2、开始考试 5、继续考试 6、查看报告
            switch (bean.status) {
                case 2:
                    holder.btnGo.setText("开始考试");
                    holder.btnGo.setBackgroundResource(R.drawable.sc_sign_up_btn_bg);
                    break;
                case 5:
                    holder.btnGo.setText("继续考试");
                    holder.btnGo.setBackgroundResource(R.drawable.sc_sign_up_btn_bg);
                    break;
                case 6:
                    holder.btnGo.setText("查看报告");
                    holder.btnGo.setBackgroundResource(R.drawable.sc_sign_up_has_report_btn_bg);
                    break;
            }

            if (bean.courseId > 0 && !StringUtils.isEmpty(bean.courseName)) {
                holder.tvClassTitle.setText(bean.courseName);
                holder.tvClassDescribe.setText(bean.courseInfo);
            } else {
                holder.rlClass.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == datas.size()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return (datas != null && datas.size() > 0) ? datas.size() + 1 : 0;
    }

    class ScHolder extends RecyclerView.ViewHolder {

        TextView tvNum;             // 考试人数

        TextView tvTitle;           // 名称
        LinearLayout llTime;        // 时间段
        TextView tvTime;            // 时间
        TextView tvPoint;           // 知识点
        TextView tvDescribe;        // 考试描述

        AppCompatButton btnGo;      // 开始考试

        RelativeLayout rlClass;     // 底部课程布局
        TextView tvClassTitle;      // 课程名称
        TextView tvClassDescribe;   // 课程描述
        ImageView ivClass;          // 课程点击图片

        ScHolder(View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tv_num);

            tvTitle = itemView.findViewById(R.id.tv_title);
            llTime = itemView.findViewById(R.id.ll_time);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPoint = itemView.findViewById(R.id.tv_point);
            tvDescribe = itemView.findViewById(R.id.tv_describe);

            btnGo = itemView.findViewById(R.id.btn_go);

            rlClass = itemView.findViewById(R.id.rl_class);
            tvClassTitle = itemView.findViewById(R.id.tv_class_title);
            tvClassDescribe = itemView.findViewById(R.id.tv_class_describe);
            ivClass = itemView.findViewById(R.id.iv_class);

            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.GO_ANSWER, getAdapterPosition());
                    }
                }
            });

            ivClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.CLASS, getAdapterPosition());
                    }
                }
            });
        }
    }


    class ScDescribeHolder extends RecyclerView.ViewHolder {

        TextView tvDescribe;

        ScDescribeHolder(View itemView) {
            super(itemView);
            tvDescribe = itemView.findViewById(R.id.tv_describe);
        }
    }

    public interface OnItemClickListener {

        int GO_ANSWER = 0;
        int CLASS = 1;

        void onClick(int type, int position);
    }
}
