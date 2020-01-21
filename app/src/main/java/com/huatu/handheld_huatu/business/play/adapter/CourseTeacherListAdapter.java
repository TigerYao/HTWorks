package com.huatu.handheld_huatu.business.play.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.play.fragment.CourseTeacherDetailActivity;
import com.huatu.handheld_huatu.business.play.fragment.CourseTeacherDetailFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.widget.CustomRatingBar;

import java.util.List;

public class CourseTeacherListAdapter extends RecyclerView.Adapter<CourseTeacherListAdapter.MyViewHolder> {
    private Context context;
    private List<CourseTeacherListItemBean> list;
    private int courseType;
    private XiaoNengHomeActivity mActivity;

    public CourseTeacherListAdapter(Context context, List<CourseTeacherListItemBean> list,int courseType,XiaoNengHomeActivity mActivity) {
        this.context = context;
        this.list = list;
        this.courseType = courseType;
        this.mActivity = mActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.course_teacher_list_item_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(CourseTeacherListAdapter.MyViewHolder holder, int position) {
        ImageLoad.displayUserAvater(context,list.get(position).roundPhoto,holder.headView,R.mipmap.user_default_avater);
        holder.teacheName.setText(list.get(position).teacherName);
        setBoldText(holder.teacheName);
        float starNum=(float) list.get(position).star/2;
        holder.ratingBar.setStar(starNum>5 ?5:starNum);
        holder.ratingBar.setClickable(false);
        holder.score.setText(list.get(position).teacherRank+"分");
        holder.subject.setText(list.get(position).SubjectType);
        holder.studentNumber.setText(list.get(position).allStudentNum+"个学生");
        holder.courseSale.setText(list.get(position).payClasses+"个在售课程");
        holder.teacherDscription.setText(list.get(position).Brief);
        if(TextUtils.isEmpty(list.get(position).nickname)) {
            holder.jumpText.setText("查看详细介绍");
        } else {
            holder.jumpText.setText("查看“" + list.get(position).nickname + "”的详细介绍");
        }
        setBoldText(holder.jumpText);
        final String mTeacherId = list.get(position).teacherId;
        final String mNickNmae = list.get(position).nickname;
        final String mTeacherName = list.get(position).teacherName;
        holder.jumpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CourseTeacherDetailFragment.show(mActivity, mTeacherId,mNickNmae,mTeacherName,courseType);
                Intent teIntent = new Intent(context,CourseTeacherDetailActivity.class);
                teIntent.putExtra("teacher_id",mTeacherId);
                teIntent.putExtra("nick_name",mNickNmae);
                teIntent.putExtra("teacher_name",mTeacherName);
                teIntent.putExtra("course_type",courseType);
                teIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(teIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public void setList(List<CourseTeacherListItemBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView headView;
        TextView teacheName;
        CustomRatingBar ratingBar;
        TextView score;
        TextView subject;
        TextView studentNumber;
        TextView courseSale;
        TextView teacherDscription;
        TextView jumpText;
        public MyViewHolder(View view)
        {
            super(view);
            headView = (ImageView) view.findViewById(R.id.course_teacher_list_item_header);
            teacheName = (TextView) view.findViewById(R.id.course_teacher_list_item_name);
            ratingBar = (CustomRatingBar) view.findViewById(R.id.teacher_comment_item_rating);
            score = (TextView) view.findViewById(R.id.course_teacher_list_item_score_tv);
            subject = (TextView) view.findViewById(R.id.course_teacher_list_item_subject_tv);
            studentNumber = (TextView) view.findViewById(R.id.course_teacher_list_item_student_num_tv);
            courseSale = (TextView) view.findViewById(R.id.course_teacher_list_item_lesson_num_tv);
            teacherDscription = (TextView) view.findViewById(R.id.course_teacher_list_item_des_tv);
            jumpText = (TextView) view.findViewById(R.id.course_teacher_list_item_detail_tv);
        }
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
