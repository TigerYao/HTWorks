package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.TeacherTimeTable;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * Created by cjx on 2018\7\20 0020.
 *
 */

public class CourseTeacherLessionsAdapter extends SimpleBaseRecyclerAdapter<TeacherTimeTable.TeacherTimeTypeInfo> {
    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public CourseTeacherLessionsAdapter(Context context, List<TeacherTimeTable.TeacherTimeTypeInfo> items) {
        super(context, items);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
      /*  if(ArrayUtils.isEmpty(mItems)) return 1;
        return 0;  //1  讲义0  头部*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0){

            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_teacher_lession_list_item, parent, false);
            return new CourseTitleViewHolder(collectionView);
        }else {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_teacher_time_list_item, parent, false);
            return new TimeViewHolder(collectionView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType=getItemViewType(position);
        if(viewType==0){
            CourseTitleViewHolder holderHead = (CourseTitleViewHolder) holder;
            holderHead.bindUI(mItems.get(position), position);

        }
        else {
            TimeViewHolder holderfour = (TimeViewHolder) holder;

            String preLessionLabelId=position<=0?"":mItems.get(position-1).lessonTableDetailId;
            holderfour.bindUI(mItems.get(position), position,preLessionLabelId);
        }
    }

    protected class CourseTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle,mAddressTxt;
        ImageView mOpenImg;
        View mRootView;

        CourseTitleViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mAddressTxt = (TextView) itemView.findViewById(R.id.add_address_address_tv);
            mOpenImg = (ImageView) itemView.findViewById(R.id.right_open_img);

            mOpenImg.setOnClickListener(this);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), mOpenImg, EventConstant.EVENT_MORE);
                    break;
                case R.id.right_open_img:

                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), mOpenImg, EventConstant.EVENT_MORE);
                    break;
            }
        }

        public void bindUI(TeacherTimeTable.TeacherTimeTypeInfo lessionBean, int pos){
             mTitle.setText(lessionBean.stageName+"-"+lessionBean.subjectName);
           // mCourseNum.setText(String.valueOf(lessionBean.classHour+"课时"));
            mOpenImg.setVisibility(View.VISIBLE);
            mOpenImg.setRotation(lessionBean.isClosed()?0:180);

        }
    }


    protected class TimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTimeGroupTxt,mClassTimeTxt,mTeacherTxt,mRoomTxt;
        //ImageView mDownStatusImg;

        View mRootView;


        TimeViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mTimeGroupTxt = (TextView) itemView.findViewById(R.id.time_group_txt);
            mClassTimeTxt = (TextView) itemView.findViewById(R.id.time_class_txt);
            mTeacherTxt=(TextView)itemView.findViewById(R.id.techer_des_txt) ;
            mRoomTxt=(TextView)itemView.findViewById(R.id.room_des_txt) ;
            // itemView.findViewById(R.id.whole_content).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                    break;
                case R.id.down_status_layout:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_JOIN_IN);
                    break;
            }
        }

        public void bindUI(TeacherTimeTable.TeacherTimeTypeInfo lessionBean, int pos,String prelessonTableDetailId) {

            if(lessionBean.lessonTableDetailId.equals(prelessonTableDetailId)){
               mTimeGroupTxt.setVisibility(View.GONE);
            }else {
                mTimeGroupTxt.setVisibility(View.VISIBLE);
                mTimeGroupTxt.setText(dateSdf.format(lessionBean.lessonDate));
            }

            mTeacherTxt.setText("主讲："+lessionBean.teacherName);
            mClassTimeTxt.setText(timeSdf.format(lessionBean.startTime)+"-"+timeSdf.format(lessionBean.endTime));
        }
    }


}

