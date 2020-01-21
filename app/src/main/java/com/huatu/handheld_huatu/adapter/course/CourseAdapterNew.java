package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.adapter.GroupRecyclerAdapter;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.bean.AllCourseData;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.business.main.MoreCourseListFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.ui.MultTextView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.countdown.CountDownTimers;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapterNew extends GroupRecyclerAdapter<AllCourseData, CourseAdapterNew.GroupHolder, CourseAdapterNew.ChildHolder> {

    private Context mContext;

    public CourseAdapterNew(Context context, List<AllCourseData> groups) {
        super(groups);
        mContext = context;
        mCountDownTask = CountDownTask.create();
        setOnChildClickListener(new OnChildClickListener() {
            @Override
            public void onChildClick(View itemView, int groupPosition, int childPosition) {

                AllCourseData group = getGroup(groupPosition);

                CourseListData mResult = group.data.get(childPosition);

                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络错误，请检查您的网络");
                    return;
                }

                int videoType = 1;
                if (!TextUtils.isEmpty(mResult.videoType)) {
                    videoType = Integer.parseInt(mResult.videoType);
                }
                if (mResult.isCollect && !mResult.isNew) {      // 跳转合集
                    CourseCollectSubsetFragment.show(mContext, mResult.collectId, mResult.title, mResult.title, videoType, "app课程列表页");
                } else if (mResult.secondKill) {                // 跳转秒杀，这个是WebView
                    BaseFrgContainerActivity.newInstance(mContext, SecKillFragment.class.getName(), SecKillFragment.getArgs(mResult.classId, mResult.title, false));
                } else {                                        // 跳转售前
                    int collageActiveId = 0;
                    if (!TextUtils.isEmpty(mResult.collageActiveId)) {
                        collageActiveId = Integer.parseInt(mResult.collageActiveId);
                    }
                    Intent intent = new Intent(mContext, BaseIntroActivity.class);
                    intent.putExtra("NetClassId", mResult.isCollect ? mResult.defaultId : mResult.classId);
                    intent.putExtra("course_type", videoType);
                    intent.putExtra("price", mResult.actualPrice);
                    intent.putExtra("originalprice", mResult.price);
                    intent.putExtra("collageActiveId", collageActiveId);
                    intent.putExtra("saleout", mResult.isSaleOut);
                    intent.putExtra("rushout", mResult.isRushOut);
                    intent.putExtra("daishou", mResult.isTermined);
                    intent.putExtra("from", "app课程列表页");
                    intent.putExtra("isCollect", mResult.isNew);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    protected GroupHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new GroupHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fragment_course_group_new, parent, false));
    }

    @Override
    protected ChildHolder onCreateChildViewHolder(ViewGroup parent) {
        return new ChildHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fragment_course_child_new, parent, false));
    }

    @Override
    protected void onBindGroupViewHolder(GroupHolder holder, int groupPosition) {

        AllCourseData mResult = getGroup(groupPosition);

        if (mResult.img != null) {
            ImageLoad.load(mContext, mResult.img, holder.icon, R.mipmap.mip_course_default);

        }
        if (mResult.title != null) {
            holder.title.setText(mResult.title);
        }
    }

    @Override
    protected void onBindChildViewHolder(ChildHolder holder, int groupPosition, int childPosition) {

        AllCourseData group = getGroup(groupPosition);

        CourseListData child = group.data.get(childPosition);

        if (childPosition == group.data.size() - 1) {
            if (group.more) {
                holder.viewEnd.setVisibility(View.GONE);
                holder.ivMore.setVisibility(View.VISIBLE);
            } else {
                holder.viewEnd.setVisibility(View.VISIBLE);
                holder.ivMore.setVisibility(View.GONE);
            }
        } else {
            holder.viewEnd.setVisibility(View.GONE);
            holder.ivMore.setVisibility(View.GONE);
        }

        if (child != null) {
            // 合集
            if (child.isCollect) {
                holder.iv_divide.setVisibility(View.GONE);
                holder.iv_collect_bottom.setVisibility(View.VISIBLE);
                if (child.collectTag != null) {
                    holder.tv_collect_course_title.setVisibility(View.VISIBLE);
                    holder.tv_collect_course_title.setText(child.collectTag);
                } else {
                    holder.tv_collect_course_title.setVisibility(View.GONE);
                }
            } else {
                holder.tv_collect_course_title.setVisibility(View.GONE);
                holder.iv_divide.setVisibility(View.VISIBLE);
                holder.iv_collect_bottom.setVisibility(View.GONE);
            }
            // 课程标题
            if (!TextUtils.isEmpty(child.title)) {
                holder.tv_each_course_title.setText(Html.fromHtml(child.title));
            } else {
                holder.tv_each_course_title.setText("");
            }
            // 课程简介
            if (!TextUtils.isEmpty(child.brief)) {
                holder.tv_brief.setVisibility(View.VISIBLE);
                holder.tv_brief.setText(Html.fromHtml(child.brief));
            } else {
                holder.tv_brief.setVisibility(View.GONE);
            }
            // 课程时间和课时
            if (!TextUtils.isEmpty(child.timeLength)) {
                holder.tv_duration.setVisibility(View.VISIBLE);
                holder.tv_duration.setText(child.timeLength);
            } else {
                holder.tv_duration.setVisibility(View.GONE);
            }

            // 老师头像
            if (child.teacher != null && child.teacher.size() != 0) {
                int l = child.teacher.size();
                if (child.activeTag != null && child.activeTag.size() == 5 && l > 2) {
                    l = 2;
                } else if (child.activeTag != null && child.activeTag.size() >= 6) {
                    l = 1;
                }
                if (l > 3) {
                    l = 3;
                }
                int size = l;//Math.min(3, ArrayUtils.size(mineItem.teacherImg));
                String[] names = new String[size];
                for (int i = 0; i < 3; i++) {
                    if (i < size) {
                        names[i] = child.teacher.get(i).teacherName;
                        holder.mTeacherAvaters[i].setVisibility(View.VISIBLE);
                        ImageLoad.displaynoCacheUserAvater(mContext, child.teacher.get(i).roundPhoto, holder.mTeacherAvaters[i], R.mipmap.user_default_avater);
                    } else
                        holder.mTeacherAvaters[i].setVisibility(View.GONE);

                }
                holder.mTeacherNameTxt.setNameList(names);
            }
            // 原价格
            if (!TextUtils.isEmpty(child.price)) {
                holder.tv_original_price.setVisibility(View.VISIBLE);
                holder.tv_original_price.setText("¥ " + child.price);
                holder.tv_original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.tv_original_price.setVisibility(View.GONE);
            }

            // 实际价格
            if (child.actualPrice != null) {
                if (child.actualPrice.equals("0")) {
                    holder.tv_real_price.setText("免费");
                    holder.tv_real_price.setTextColor(ContextCompat.getColor(mContext, R.color.green4d));
                } else {
                    holder.tv_real_price.setText("¥ " + child.actualPrice);
                    holder.tv_real_price.setTextColor(ContextCompat.getColor(mContext, R.color.indicator_color));
                }
            }

            // 抢购课
            if (child.isTermined) {
                holder.tv_left_time.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(child.terminedDesc)) {
                    holder.tv_left_time.setText(child.terminedDesc);
                } else {
                    holder.tv_left_time.setText("待售");
                }
            } else if (child.lSaleStart != 0 || child.lSaleEnd != 0) {
                holder.tv_left_time.setVisibility(View.VISIBLE);
                if (child.lSaleStart > 0) {
                    holder.tv_left_time.setTag(R.id.reuse_tag2, "1");
                    startCountDown(child.lSaleStart, holder.itemView, false);
                } else if (child.lSaleEnd > 0) {
                    holder.tv_left_time.setTag(R.id.reuse_tag2, "2");
                    startCountDown(child.lSaleEnd, holder.itemView, true);
                }
            } else {
                cancelCountDown(holder.itemView);
                holder.tv_left_time.setVisibility(View.GONE);
            }

            // 活动标签
            if (child.activeTag != null && child.activeTag.size() != 0) {
                holder.ll_discount_type.setVisibility(View.VISIBLE);
                holder.ll_discount_type.removeAllViews();
                int al = child.activeTag.size();
                if (al > 7) {
                    al = 7;
                }
                for (int i = 0; i < al; i++) {
                    LinearLayout ll_act_tag = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_act_tag, null, false);
                    TextView tv_act_tag = ll_act_tag.findViewById(R.id.tv_discount_type);
                    tv_act_tag.setText(child.activeTag.get(i));
                    holder.ll_discount_type.addView(ll_act_tag);
                }
            } else {
                holder.ll_discount_type.setVisibility(View.GONE);
            }
            // 购买情况
            if (child.isSaleOut) {
                // 售罄
                holder.tv_buy_status.setText("已售罄");
            } else if (child.isRushOut) {
                // 停售
                holder.tv_buy_status.setText("已停售");
            } else if (child.collageActiveId != null && !child.collageActiveId.equals("0")) {
                if (child.count != null && !child.count.equals("0")) {
                    holder.tv_buy_status.setText(child.count + "人已拼");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (child.limitType != null && child.limitType.equals("0")) {
                if (child.count != null && !child.count.equals("0")) {
                    holder.tv_buy_status.setText(child.count + "人已抢");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (child.limitType != null && child.limitType.equals("1")) {
                if (child.limit != null) {
                    holder.tv_buy_status.setText("限招" + child.limit + "人");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else if (child.limitType != null && child.limitType.equals("2")) {
                if (child.limit != null && child.count != null) {
                    int l = Integer.parseInt(child.limit);
                    int c = Integer.parseInt(child.count);
                    holder.tv_buy_status.setText("仅剩" + (l - c) + "个名额");
                } else {
                    holder.tv_buy_status.setText("");
                }
            } else {
                holder.tv_buy_status.setText("");
            }
        }
    }

    @Override
    protected int getChildCount(AllCourseData group) {
        return group.data != null ? group.data.size() : 0;
    }

    class GroupHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;

        GroupHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }

    class ChildHolder extends RecyclerView.ViewHolder {

        TextView tv_collect_course_title;
        TextView tv_each_course_title;
        TextView tv_brief;
        TextView tv_duration;
        TextView tv_left_time;
        TextView tv_real_price;
        TextView tv_original_price;
        TextView tv_buy_status;
        LinearLayout ll_discount_type;

        LinearLayout ll_collect_bottom;
        ImageView iv_collect_bottom;
        View iv_divide;
        View viewEnd;
        ImageView ivMore;

        MultTextView mTeacherNameTxt;
        ImageView[] mTeacherAvaters;

        ChildHolder(View itemView) {
            super(itemView);
            tv_collect_course_title = itemView.findViewById(R.id.tv_collect_course_title);
            tv_each_course_title = itemView.findViewById(R.id.tv_each_course_title);
            tv_brief = itemView.findViewById(R.id.tv_brief);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_left_time = itemView.findViewById(R.id.tv_left_time);
            tv_real_price = itemView.findViewById(R.id.tv_real_price);
            tv_original_price = itemView.findViewById(R.id.tv_original_price);
            tv_buy_status = itemView.findViewById(R.id.tv_buy_status);
            ll_discount_type = itemView.findViewById(R.id.ll_discount_type);
            ll_collect_bottom = itemView.findViewById(R.id.ll_collect_bottom);
            iv_collect_bottom = itemView.findViewById(R.id.iv_collect_bottom);
            iv_divide = itemView.findViewById(R.id.iv_divide);
            viewEnd = itemView.findViewById(R.id.view_end);
            ivMore = itemView.findViewById(R.id.iv_more);

            mTeacherAvaters = new ImageView[3];
            mTeacherAvaters[0] = itemView.findViewById(R.id.teacher_first_view);
            mTeacherAvaters[1] = itemView.findViewById(R.id.teacher_second_view);
            mTeacherAvaters[2] = itemView.findViewById(R.id.teacher_third_view);

            mTeacherNameTxt = itemView.findViewById(R.id.techer_des_txt);

            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Position position = getGroupChildPosition(getAdapterPosition());
                    AllCourseData group = getGroup(position.group);
                    // 查看更多
                    MoreCourseListFragment.launch(mContext, group.typeId, group.title, "");
                }
            });
        }
    }

    private CountDownTask mCountDownTask;

    private final int COUNT_NONE = 0;
    private final int COUNT_ING_STATUS = 1;
    private final int COUNT_PAUSE_STATUS = 2;

    private int isCountStatus = COUNT_NONE;


    private void startCountDown(final long remainTime, View convertView, boolean isStarted) {
        if (mCountDownTask != null) {
            isCountStatus = COUNT_ING_STATUS;
            mCountDownTask.until(convertView, remainTime, 1000, new CountDownTimers.OnCountDownListener() {
                @Override
                public void onTick(View view, long millisUntilFinished) {
                    LogUtils.e("onliveCourseFragment", millisUntilFinished + "");
                    doOnTick(view, millisUntilFinished);
                }

                @Override
                public void onFinish(View view) {
                    doOnFinish(view);
                }
            });
        }
    }

    private void doOnTick(View view, long millisUntilFinished) {
        TextView textView1 = view.findViewById(R.id.tv_left_time);
        boolean keepStarted = "1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;") + DateUtils.getCourseTime(millisUntilFinished / 1000);
        textView1.setText(StringUtils.forHtml(as_detail));
    }

    private void doOnFinish(View view) {
        TextView textView1 = view.findViewById(R.id.tv_left_time);
        boolean keepStarted = "1".equals(textView1.getTag(R.id.reuse_tag2));
        String as_detail = (keepStarted ? "距开抢&#160;" : "距停售&#160;") + "00:00:00";
        textView1.setText(StringUtils.forHtml(as_detail));
    }

    public void clear() {
        update(new ArrayList<AllCourseData>());
    }

    private void cancelCountDown(View view) {
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
}
