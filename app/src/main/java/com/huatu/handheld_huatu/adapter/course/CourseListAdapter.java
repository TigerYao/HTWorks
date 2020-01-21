package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.countdown.CountDownTimers;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CircleImageView;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chq on 2018/12/4.
 */

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {
    public Context mContext;
    public List<CourseListData> mData = new ArrayList<>();
    private CountDownTask mCountDownTask;
    private final int COUNTDOWNINTERVAL = 1000;

    private final int COUNT_NONE = 0;
    private final int COUNT_ING_STATUS = 1;
    private final int COUNT_PAUSE_STATUS = 2;

    private int isCountStatus = COUNT_NONE;
    private String keyWord;
    private int currentPage;
    private boolean isFromSearch=false;
    private String pageSource;

    public CourseListAdapter(Context context, List<CourseListData> mCourseList) {
        mContext = context;
        mData = mCourseList;
        mCountDownTask = CountDownTask.create();
    }

    public CourseListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setAdapterData(List<CourseListData> mCourseList){
        mData = mCourseList;
    }

    public void setPageSource(String pageSource) {
        this.pageSource = pageSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_list, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CourseListData mResult = mData.get(position);
        if (mResult != null) {
            //合集
            if (mResult.isCollect) {
                holder.ll_collect_bottom.setVisibility(View.VISIBLE);
                holder.iv_divide.setVisibility(View.GONE);
                if (mResult.collectTag != null) {
                    holder.tv_collect_course_title.setVisibility(View.VISIBLE);
                    holder.tv_collect_course_title.setText(mResult.collectTag);
                } else {
                    holder.tv_collect_course_title.setVisibility(View.GONE);
                }
            } else {
                holder.tv_collect_course_title.setVisibility(View.GONE);
                holder.iv_divide.setVisibility(View.VISIBLE);
                holder.ll_collect_bottom.setVisibility(View.GONE);
            }
            //课程标题
            if (!TextUtils.isEmpty(mResult.title)) {
                holder.tv_each_course_title.setText(Html.fromHtml(mResult.title));
            } else {
                holder.tv_each_course_title.setText("");
            }
            //课程简介
            if (!TextUtils.isEmpty(mResult.brief)) {
                holder.tv_brief.setVisibility(View.VISIBLE);
                holder.tv_brief.setText(Html.fromHtml(mResult.brief));
            } else {
                holder.tv_brief.setVisibility(View.GONE);
            }
            //课程时间和课时
            if (!TextUtils.isEmpty(mResult.timeLength)) {
                holder.tv_duration.setVisibility(View.VISIBLE);
                holder.tv_duration.setText(mResult.timeLength);
            } else {
                holder.tv_duration.setVisibility(View.GONE);
            }

            //老师头像
            if (mResult.teacher != null && mResult.teacher.size() != 0) {
                holder.ll_teacher.removeAllViews();
                int l = mResult.teacher.size();
                if (mResult.activeTag != null && mResult.activeTag.size() == 5 && l > 2) {
                    l = 2;
                } else if (mResult.activeTag != null && mResult.activeTag.size() >= 6) {
                    l = 1;
                }
                if (l > 3) {
                    l = 3;
                }
                for (int i = 0; i < l; i++) {
                    LinearLayout ll_teacher = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_teacher2, null, false);
                     ImageView imageView = ll_teacher.findViewById(R.id.civ_teacher_photo);
                    TextView textView = ll_teacher.findViewById(R.id.tv_teacher_name);
                   // Glide.with(UniApplicationContext.getContext()).load(mResult.teacher.get(i).roundPhoto).into(imageView);

                    ImageLoad.displaynoCacheUserAvater(mContext,mResult.teacher.get(i).roundPhoto,imageView,R.mipmap.user_default_avater);
                    textView.setText(mResult.teacher.get(i).teacherName);
                    holder.ll_teacher.addView(ll_teacher);
                }
            }
            //原价格
            if (!TextUtils.isEmpty(mResult.price)) {
                holder.tv_original_price.setVisibility(View.VISIBLE);
                holder.tv_original_price.setText("¥ "+mResult.price);
                holder.tv_original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.tv_original_price.setVisibility(View.GONE);
            }

            //实际价格
            if (mResult.actualPrice != null) {
                if (mResult.actualPrice.equals("0")) {
                    holder.tv_real_price.setText("免费");
                    holder.tv_real_price.setTextColor(ContextCompat.getColor(mContext, R.color.green4d));
                } else {
                    holder.tv_real_price.setText("¥ " + mResult.actualPrice);
                    holder.tv_real_price.setTextColor(ContextCompat.getColor(mContext, R.color.indicator_color));
                }
            }

            //抢购课
            if (mResult.isTermined) {
                holder.tv_left_time.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mResult.terminedDesc)) {
                    holder.tv_left_time.setText(mResult.terminedDesc);
                } else {
                    holder.tv_left_time.setText("待售");
                }
            } else if (mResult.lSaleStart != 0 || mResult.lSaleEnd != 0) {
                holder.tv_left_time.setVisibility(View.VISIBLE);
                if (mResult.lSaleStart > 0) {
                    holder.tv_left_time.setTag(R.id.reuse_tag2, "1");
                    startCountDown(position, mResult.lSaleStart, holder.itemView, false);
                } else if (mResult.lSaleEnd > 0) {
                    holder.tv_left_time.setTag(R.id.reuse_tag2, "2");
                    startCountDown(position, mResult.lSaleEnd, holder.itemView, true);
                }
            } else {
                cancelCountDown(position, 0, holder.itemView);
                holder.tv_left_time.setVisibility(View.GONE);
            }

            //活动标签
            if (mResult.activeTag != null && mResult.activeTag.size() != 0) {
                holder.ll_discount_type.setVisibility(View.VISIBLE);
                holder.ll_discount_type.removeAllViews();
                int al = mResult.activeTag.size();
                if (al > 7) {
                    al = 7;
                }
                for (int i = 0; i < al; i++) {
                    LinearLayout ll_act_tag = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_act_tag, null, false);
                    TextView tv_act_tag = ll_act_tag.findViewById(R.id.tv_discount_type);
                    tv_act_tag.setText(mResult.activeTag.get(i));
                    holder.ll_discount_type.addView(ll_act_tag);
                }
            } else {
                holder.ll_discount_type.setVisibility(View.GONE);
            }
            //购买情况
            if (mResult.isSaleOut) {
                //售罄
                holder.tv_buy_status.setText("已售罄");
            } else if (mResult.isRushOut) {
                //停售
                holder.tv_buy_status.setText("已停售");
            } else if (mResult.collageActiveId != null && !mResult.collageActiveId.equals("0")) {
                if (mResult.count != null && !mResult.count.equals("0")) {
                    holder.tv_buy_status.setText(mResult.count + "人已拼");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (mResult.limitType != null && mResult.limitType.equals("0")) {
                if (mResult.count != null && !mResult.count.equals("0")) {
                    holder.tv_buy_status.setText(mResult.count + "人已抢");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (mResult.limitType != null && mResult.limitType.equals("1")) {
                if (mResult.limit != null) {
                    holder.tv_buy_status.setText("限招" + mResult.limit + "人");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (mResult.limitType != null && mResult.limitType.equals("2")) {
                if (mResult.limit != null && mResult.count != null) {
                    int l = Integer.parseInt(mResult.limit);
                    int c = Integer.parseInt(mResult.count);
                    holder.tv_buy_status.setText("仅剩" + (l - c) + "个名额");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else {
                holder.tv_buy_status.setText("");
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.isConnected()) {
                    if (isFromSearch){
                        boolean isFree=false;
                    float price=0;
                    float actualPrice=0;
                    List<String> ids=new ArrayList<>();
                    List<String> names=new ArrayList<>();
                    if (mResult.actualPrice!=null&&mResult.actualPrice.equals("0")) {
                        isFree=true;
                    }
                    if (mResult.teacher!=null && mResult.teacher.size()!=0){
                        for (int i = 0; i < mResult.teacher.size(); i++) {
                            ids.add(mResult.teacher.get(i).teacherId);
                            names.add(mResult.teacher.get(i).teacherName);
                        }
                    }
                    if (!TextUtils.isEmpty(mResult.price)){
                        price=Float.parseFloat(mResult.price);
                    }else {
                        if (!TextUtils.isEmpty(mResult.actualPrice)){
                            price=Float.parseFloat(mResult.actualPrice);
                        }
                    }
                    if (!TextUtils.isEmpty(mResult.actualPrice)){
                        actualPrice=Float.parseFloat(mResult.actualPrice);
                    }
                    StudyCourseStatistic.clickCourseSearchResult(keyWord,position+"",currentPage,mResult.classId,mResult.title,mResult.suit,mResult.isCollect,
                            isFree,ids,names,SpUtils.getSelectedCategoryName(),price,actualPrice,actualPrice);
                    }
                    int videoType = 1;
                    if (!TextUtils.isEmpty(mResult.videoType)) {
                        videoType = Integer.parseInt(mResult.videoType);
                    }
                    if (mResult.isCollect && !mResult.isNew) {
                        CourseCollectSubsetFragment.show(mContext,
                                mResult.collectId, mResult.title, mResult.title, videoType, pageSource);
                    } else if (mResult.secondKill) {
                        BaseFrgContainerActivity.newInstance(mContext,
                                SecKillFragment.class.getName(),
                                SecKillFragment.getArgs(mResult.classId, mResult.title, false));
                    } else {
                        int collageActiveId = 0;
                        if (!TextUtils.isEmpty(mResult.collageActiveId)) {
                            collageActiveId = Integer.parseInt(mResult.collageActiveId);
                        }
                        Intent intent = new Intent(mContext, BaseIntroActivity.class);
                        intent.putExtra("rid", mResult.isCollect ? mResult.defaultId : mResult.classId);
                        intent.putExtra("course_type", videoType);
                        intent.putExtra("price", mResult.actualPrice);
                        intent.putExtra("originalprice", mResult.price);
                        intent.putExtra("collageActiveId", collageActiveId);
                        intent.putExtra("saleout", mResult.isSaleOut);
                        intent.putExtra("rushout", mResult.isRushOut);
                        intent.putExtra("daishou", mResult.isTermined);
                        intent.putExtra("from", pageSource);
                        intent.putExtra("isCollect", mResult.isNew);
                        mContext.startActivity(intent);
                    }
                } else {
                    ToastUtils.showShort("网络错误，请检查您的网络");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<CourseListData> mCourseList) {
        mData.clear();
        mData.addAll(mCourseList);
        notifyDataSetChanged();
    }

    public void clearAndRefresh() {
        mData.clear();
        notifyDataSetChanged();
    }

    private void startCountDown(final int position, final long remainTime, View convertView, boolean isStarted) {
        //大于一天时间直接返回，不定时
//        long diffTime= remainTime- CountDownTask.elapsedRealtime();//毫秒级
//        if(diffTime>86400000) {
//            TextView textView1 =convertView.findViewById(R.id.item_shop_tv_end_time);
//            String as_detail =(!isStarted? "距开抢&#160;":"距停售&#160;")+DateUtils.getCourseTime(diffTime/1000);
//            textView1.setText(StringUtils.forHtml(as_detail));
//            cancelCountDown(position,0,convertView);
//            return;
//        }
        if (mCountDownTask != null) {
            isCountStatus = COUNT_ING_STATUS;
            mCountDownTask.until(convertView, remainTime, COUNTDOWNINTERVAL, new CountDownTimers.OnCountDownListener() {
                @Override
                public void onTick(View view, long millisUntilFinished) {
                    LogUtils.e("onliveCourseFragment", millisUntilFinished + "");
                    doOnTick(position, view, millisUntilFinished);
                }

                @Override
                public void onFinish(View view) {
                    doOnFinish(position, view);
                }
            });
        }
    }

    private void doOnTick(int position, View view, long millisUntilFinished) {
        TextView textView1 = view.findViewById(R.id.tv_left_time);
        boolean keepStarted = "1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;") + DateUtils.getCourseTime(millisUntilFinished / 1000);
        textView1.setText(StringUtils.forHtml(as_detail));
    }

    private void doOnFinish(int position, View view) {
        TextView textView1 = view.findViewById(R.id.tv_left_time);
        boolean keepStarted = "1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;")
                + "00:00:00";
        textView1.setText(StringUtils.forHtml(as_detail));
    }

    private void cancelCountDown(int position, long millisUntilFinished, View view) {
        if (mCountDownTask != null && (isCountStatus == COUNT_ING_STATUS)) {
            mCountDownTask.cancel(view);
        }
    }


    public void pauseTicks() {
        if (isCountStatus == COUNT_ING_STATUS) {
            isCountStatus = COUNT_PAUSE_STATUS;
            mCountDownTask.cancel();
        }
    }

    public void clearCountDownTask() {
        isCountStatus = COUNT_NONE;
        if (null != mCountDownTask) {
            mCountDownTask.cancel();
        }
    }

    public void checkPauseStatus() {
        if (isCountStatus == COUNT_PAUSE_STATUS) {
            this.notifyDataSetChanged();
        }
    }

    public void clear() {
        if (mData != null) mData.clear();
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setFromSearch(boolean fromSearch) {
        isFromSearch = fromSearch;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_collect_course_title)
        TextView tv_collect_course_title;
        @BindView(R.id.tv_each_course_title)
        TextView tv_each_course_title;
        @BindView(R.id.tv_brief)
        TextView tv_brief;
        @BindView(R.id.tv_duration)
        TextView tv_duration;
        @BindView(R.id.tv_left_time)
        TextView tv_left_time;
        @BindView(R.id.tv_real_price)
        TextView tv_real_price;
        @BindView(R.id.tv_original_price)
        TextView tv_original_price;
        @BindView(R.id.tv_buy_status)
        TextView tv_buy_status;
        @BindView(R.id.ll_discount_type)
        LinearLayout ll_discount_type;
        @BindView(R.id.ll_teacher)
        LinearLayout ll_teacher;
        @BindView(R.id.ll_collect_bottom)
        LinearLayout ll_collect_bottom;
        @BindView(R.id.iv_divide)
        View iv_divide;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
