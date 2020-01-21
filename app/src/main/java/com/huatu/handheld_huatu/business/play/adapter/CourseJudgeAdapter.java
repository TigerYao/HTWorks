package com.huatu.handheld_huatu.business.play.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.widget.CustomRatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2018/7/10.
 */

public class CourseJudgeAdapter extends CommonAdapter<CourseTeacherJudgeItemBean> {
    private final List<CourseTeacherJudgeItemBean> judgeList = new ArrayList<>();
    public CourseJudgeAdapter() {
        super();
        mData = judgeList;
        layoutId = R.layout.course_teacher_judge_item_layout;
    }

    public CourseJudgeAdapter(List<CourseTeacherJudgeItemBean> data, int layoutId) {
        super(data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder,  CourseTeacherJudgeItemBean item, int position) {
        ImageView headView = holder.getView(R.id.teacher_judge_item_header);
        ImageLoad.displayUserAvater(mContext,item.userFace,headView,R.mipmap.user_default_avater);
        holder.setText(R.id.teacher_judge_item_name, item.userName);
        if (!TextUtils.isEmpty(item.periods)) {
            holder.setText(R.id.teacher_judge_item_section, item.periods+"期");
        } else {
            View divider = holder.getView(R.id.divider_flag);
            divider.setVisibility(View.GONE);
        }
        CustomRatingBar ratingBar = holder.getView(R.id.teacher_judge_item_rating);
        ratingBar.setStar((float)item.star/2);
        ratingBar.setClickable(false);
        holder.setText(R.id.teacher_judge_item_content, item.courseRemark);
        holder.setText(R.id.teacher_judge_item_time, item.rateDate);
        holder.setText(R.id.teacher_judge_item_course, item.lessonTitle);
//        holder.getConvertView().setOnClickListener(new BaseRecyclerAdapter.OnClickListener() {
//            @Override
//            public void onClick(int position, long itemId) {
//                Intent intent = new Intent(UniApplicationContext.getContext(), VideoPlayActivity.class);
//                intent.putExtra("course_id", item.lessonId);
//                UniApplicationContext.getContext().startActivity(intent);
//            }
//        });
    }
}
