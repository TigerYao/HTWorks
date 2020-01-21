package com.huatu.handheld_huatu.business.play.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseDetailListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomHeadView;
import com.huatu.widget.CustomRatingBar;

/**
 * 老师介绍页(弃用)
 */

public class CourseTeacherListFragment extends BaseDetailListFragment {
    private String courseId;
    private int courseType;

    public static CourseTeacherListFragment getInstance(String classID,int classTYPE) {
        Bundle ids=new Bundle();
        ids.putString("course_id",classID);
        ids.putInt("course_type",classTYPE);
        CourseTeacherListFragment tempCourseTeacherListFragment = new CourseTeacherListFragment();
        tempCourseTeacherListFragment.setArguments(ids);
        return tempCourseTeacherListFragment;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        courseId = args.getString("course_id");
        courseType = args.getInt("course_type",0);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        listView.setHeaderDividersEnabled(false);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);

        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.setDividerHeight(DisplayUtil.dp2px(15));
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<CourseTeacherListItemBean>(
                dataList, R.layout.course_teacher_list_item_layout) {
            @Override
            public void convert(ViewHolder holder, final CourseTeacherListItemBean item, int position) {
                CustomHeadView headView = holder.getView(R.id.course_teacher_list_item_header);
                headView.setEdgingColor(Color.TRANSPARENT);
                headView.setHeadUrl(item.roundPhoto);
                TextView teacheName = holder.getView(R.id.course_teacher_list_item_name);
                teacheName.setText(item.teacherName);
                setBoldText(teacheName);
//                holder.setText(R.id.course_teacher_list_item_name, item.teacherName);
                CustomRatingBar ratingBar = holder.getView(R.id.teacher_comment_item_rating);
                float starNum=(float) item.star/2;
                ratingBar.setStar(starNum>5 ?5:starNum);
                ratingBar.setClickable(false);
                holder.setText(R.id.course_teacher_list_item_score_tv, item.teacherRank + "分");
                holder.setText(R.id.course_teacher_list_item_subject_tv, item.SubjectType);
                holder.setText(R.id.course_teacher_list_item_student_num_tv, item.allStudentNum + "个学生");
                holder.setText(R.id.course_teacher_list_item_lesson_num_tv, item.payClasses + "个在售课程");
                holder.setText(R.id.course_teacher_list_item_des_tv, item.Brief);
                TextView teacheDetailJump = holder.getView(R.id.course_teacher_list_item_detail_tv);
                if(TextUtils.isEmpty(item.nickname)) {
//                    holder.setText(R.id.course_teacher_list_item_detail_tv,
//                            "查看"+item.teacherName+"详细介绍");
                    teacheDetailJump.setText("查看详细介绍");
                } else {
//                    holder.setText(R.id.course_teacher_list_item_detail_tv,
//                            "查看“" + item.nickname + "”的详细介绍");
                    teacheDetailJump.setText("查看“" + item.nickname + "”的详细介绍");
                }
                setBoldText(teacheDetailJump);
//                holder.setViewOnClickListener(R.id.course_teacher_list_item_detail_tv, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CourseTeacherDetailFragment.show(mActivity, item.teacherId,item.nickname,item.teacherName,courseType);
//                    }
//                });
                teacheDetailJump.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CourseTeacherDetailFragment.show(mActivity, item.teacherId,item.nickname,item.teacherName,courseType);
                    }
                });
            }
        };
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return false;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected void onLoadData() {
        showProgressBar();
        ServiceProvider.getCourseTeacherList(compositeSubscription, courseId, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
//                CourseTeacherListFragment.this.onLoadDataFailed();
                listView.setVisibility(View.GONE);
                noServer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                dismissProgressBar();
                CourseTeacherListFragment.this.onSuccess(model.data, true);
            }
        });
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
