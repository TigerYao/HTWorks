package com.huatu.handheld_huatu.business.matches.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.matches.customview.ShakeImageView;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;
import com.huatu.handheld_huatu.utils.CalendarReminderUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

public class ScListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<SimulationScDetailBean> datas;
    private OnItemClickListener listener;

    public ScListAdapter(Context mContext, ArrayList<SimulationScDetailBean> mSimulationContestDetailBeans, OnItemClickListener listener) {
        this.mContext = mContext;
        this.datas = mSimulationContestDetailBeans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == 0) {
            return new ScHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_simulation_contest_mcard_new_layout, viewGroup, false));
        } else {
            return new ScDescribeHolder(LayoutInflater.from(mContext).inflate(R.layout.sc_main_describe_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 1) {            // 描述
            ScDescribeHolder holder = (ScDescribeHolder) viewHolder;

            holder.tvDescribe.setText(datas.get(0).instruction);
        } else {                            // 模考
            ScHolder holder = (ScHolder) viewHolder;

            if (datas == null || position >= datas.size()) return;
            SimulationScDetailBean bean = datas.get(position);

            if (bean != null) {
                holder.tvScTitle.setText(bean.name);
                holder.tvScSignupNum.setText(bean.enrollCount + "");
                holder.tvScExamTime.setText(bean.timeInfo);
                holder.tvScParseTime.setText(bean.courseInfo);
                String iconUrl = bean.iconUrl;
                if (!StringUtils.isEmpty(iconUrl)) {
                    holder.ivShake.setVisibility(View.VISIBLE);
                    ImageLoad.load(mContext, iconUrl.trim(), holder.ivShake);
                    holder.ivShake.startAnim();
                } else {
                    holder.ivShake.setVisibility(View.GONE);
                }

                int stage = bean.stage;

                String subjectStr = "";
                int type = SignUpTypeDataCache.getInstance().getSignUpType();
                if (type == Type.SignUpType.CIVIL_SERVANT) {
                    if (stage == 1) {
                        subjectStr = "行测";
                    } else if (stage == 2) {
                        subjectStr = "申论";
                    }
                }

                int status = bean.status;
                holder.tvScSignUp.setTextColor(mContext.getResources().getColor(R.color.white));
                // 3、距开始考试一个小时,按钮置灰-不可用 4、距开始考试 5、分钟,按钮可点击 5、考试已开始30分钟,无法做题 6、可查看报告 7、未出报告
                if (status == 1) {              // 报名，判断时间是否允许
                    holder.tvScSignUp.setText("我要报名");
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_btn_sign_red));
                } else if (status == 2) {
                    holder.tvScSignUp.setText("报名成功");
                    holder.tvScSignUp.setEnabled(false);
                    holder.tvScSignUp.setTextColor(Color.parseColor("#FF6D73"));
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_success_btn_bg));
                } else if (status == 3) {       // 开始考试，但是还不能进入
                    holder.tvScSignUp.setText("开始" + subjectStr + "考试");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_gray_btn_bg));
                } else if (status == 4) {       // 开始测试
                    holder.tvScSignUp.setText("开始" + subjectStr + "考试");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_btn_bg));
                } else if (status == 5) {       // 无法考试，时间错过
                    holder.tvScSignUp.setText("开始" + subjectStr + "考试");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_gray_btn_bg));
                } else if (status == 6) {       // 去行测考试页查看报告
                    holder.tvScSignUp.setText("查看" + subjectStr + "报告");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_has_report_btn_bg));
                } else if (status == 7) {       // 未出报告
                    holder.tvScSignUp.setText("查看" + subjectStr + "报告");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_gray_btn_bg));
                } else if (status == 8) {       // 继续考试
                    holder.tvScSignUp.setText("继续考试");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_btn_bg));
                } else if (status == 9) {       // 停止报名
                    holder.tvScSignUp.setText("停止报名");
                    holder.tvScSignUp.setEnabled(true);
                    holder.tvScSignUp.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_sign_up_gray_btn_bg));
                } else {
                    holder.tvScSignUp.setText(status + "");
                }

//             如果 enrollFlag == 1，那么报名不选择地区，默认全国（-9）
//             不显示地区
                if (bean.enrollFlag == 1) {
                    holder.tvScSignUpJobRlayout.setVisibility(View.GONE);
                } else {
                    if (bean.status > 1 && bean.status < 9) {
                        SimulationScDetailBean.UserMetaEntity mUserMetaEntity = bean.userMeta;
                        if (mUserMetaEntity != null) {
                            holder.tvScSignUpJobRlayout.setVisibility(View.VISIBLE);
                            holder.tvScSignUpJob.setText(mUserMetaEntity.positionName);
                            if (bean.areaList != null && bean.areaList.size() > 1) {
                                holder.ivPosition.setVisibility(View.VISIBLE);
                            } else {
                                holder.ivPosition.setVisibility(View.GONE);
                            }
                        } else {
                            holder.tvScSignUpJobRlayout.setVisibility(View.GONE);
                        }
                    } else {
                        holder.tvScSignUpJobRlayout.setVisibility(View.GONE);
                    }
                }
                ((ViewGroup.MarginLayoutParams) holder.tvScSignUp.getLayoutParams()).bottomMargin = DisplayUtil.dp2px(30);
                if (status > 1 && status < 5 && !bean.isAddCalender) {
                    String coursedetail = bean.name + "\n" + bean.timeInfo;
                    long startTime = bean.stage == 2 ? bean.essayStartTime : bean.startTime;
                    boolean isCanAddTime = startTime - System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime > 10 * 60 * 1000;

                    boolean isAdded = CalendarReminderUtils.queryCalendarEvent((Activity) mContext, coursedetail, startTime + "");
                    bean.isAddCalender = isAdded;
                    if (!isAdded && isCanAddTime) {
                        holder.addCalendarView.setVisibility(View.VISIBLE);
                        ((ViewGroup.MarginLayoutParams) holder.tvScSignUp.getLayoutParams()).bottomMargin = DisplayUtil.dp2px(15);
                    } else {
                        holder.addCalendarView.setVisibility(View.GONE);
                    }
                } else if (holder.addCalendarView.getVisibility() == View.VISIBLE)
                    holder.addCalendarView.setVisibility(View.GONE);
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

    public SimulationScDetailBean getItem(int position) {
        if (position > -1 && position < datas.size())
            return datas.get(position);
        else
            return null;
    }

    @Override
    public int getItemCount() {
        return (datas != null && datas.size() > 0) ? datas.size() + 1 : 0;
    }

    class ScHolder extends RecyclerView.ViewHolder {

        TextView tvScTitle;                                 // title
        TextView tvScSignupNum;                             // 已报名人数
        TextView tvHasPeo;                                  // 人报名
        TextView tvScExamTime;                              // 考试时间
        TextView tvScParseTime;                             // 深度解析时间
        TextView tvScParseBuy;                              // 点击购买
        TextView tvScSignUp;                                // 报名按钮
        ShakeImageView ivShake;                             // 晃动的礼包图片

        RelativeLayout tvScSignUpJobRlayout;                // 地区相关，点击报名
        TextView tvScSignUpJob;                             // 地区
        ImageView ivPosition;                               // 地区选择箭头

        TextView addCalendarView;                           // 添加日历

        ScHolder(View itemView) {
            super(itemView);
            tvScTitle = itemView.findViewById(R.id.tv_sc_title);
            tvScSignupNum = itemView.findViewById(R.id.tv_sc_sign_up_num);
            tvHasPeo = itemView.findViewById(R.id.tv_has_peo);
            tvScExamTime = itemView.findViewById(R.id.tv_sc_exam_time);
            tvScParseTime = itemView.findViewById(R.id.tv_sc_parse_time);
            tvScParseBuy = itemView.findViewById(R.id.tv_sc_parse_buy);
            tvScSignUp = itemView.findViewById(R.id.tv_sc_sign_up);
            ivShake = itemView.findViewById(R.id.iv_shake);
            tvScSignUpJobRlayout = itemView.findViewById(R.id.tv_sc_sign_up_job_rlayout);
            tvScSignUpJob = itemView.findViewById(R.id.tv_sc_sign_up_job);
            ivPosition = itemView.findViewById(R.id.iv_position);
            addCalendarView = itemView.findViewById(R.id.add_calend);

            tvScSignUpJobRlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.AREA, getAdapterPosition());
                    }
                }
            });

            tvScSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.SIGN, getAdapterPosition());
                    }
                }
            });

            tvScParseBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.CLASS, getAdapterPosition());
                    }
                }
            });

            ivShake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.GIFT, getAdapterPosition());
                    }
                }
            });

            addCalendarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(OnItemClickListener.ADDCALEND, getAdapterPosition());
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

        int AREA = 0;
        int SIGN = 1;
        int CLASS = 2;
        int GIFT = 3;
        int ADDCALEND = 4;

        void onClick(int type, int position);
    }
}
