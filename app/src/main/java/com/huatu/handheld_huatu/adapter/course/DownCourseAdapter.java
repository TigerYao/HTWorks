package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by wangkangfei on 17/8/17.
 */

public class DownCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分

    private List<DownLoadCourse> mListCourse;

    public DownCourseAdapter(Context context, List<DownLoadCourse> cacheCourses) {
        this.mContext = context;
        mListCourse = cacheCourses;
    }

    public void removeAndRefresh(int postion){
        mListCourse.remove(postion);
        this.notifyItemRemoved(postion);
    }

    public void  refresh(List<DownLoadCourse> cacheCourses){
        this.mListCourse.clear();
        mListCourse.addAll(cacheCourses);
        this.notifyDataSetChanged();
    }

    public DownLoadCourse getCurrentItem(int position){
        if(position>=0&&position<ArrayUtils.size(mListCourse))
            return mListCourse.get(position);
        return null;
    }

    OnRecItemClickListener mClickListener;
    public void setOnItemClickListener(OnRecItemClickListener clickListener){
        mClickListener=clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View textView = LayoutInflater.from(mContext).inflate(R.layout.down_course_list_item, parent, false);
        return new PBTextViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         PBTextViewHolder textViewHolder = (PBTextViewHolder) holder;
         textViewHolder.bindUi(mListCourse.get(position));
    }


    @Override
    public int getItemCount() {
        return ArrayUtils.size(mListCourse);
    }

    private class PBTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        ImageView img_offline_course;
        TextView tv_offline_title;
        TextView tv_offline_total;
        TextView tv_offline_num;

        PBTextViewHolder(View itemView) {
            super(itemView);
            this.img_offline_course = (ImageView) itemView
                    .findViewById(R.id.img_offline_course);
            this.tv_offline_title = (TextView) itemView
                    .findViewById(R.id.tv_offline_title);
            this.tv_offline_total = (TextView) itemView
                    .findViewById(R.id.tv_offline_total);
            this.tv_offline_num = (TextView) itemView
                    .findViewById(R.id.tv_offline_num);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this) ;
      }

        @Override
        public  void onClick(View v){
            switch (v.getId()){
                case R.id.whole_content:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_DELETE);
                    break;

            }
        }

        public void bindUi(DownLoadCourse downLoadCourse) {
            int downCourseNum = 0;
         /*   for (int i = 0; i < downLoadCourse.getLessonLists().size(); i++) {
                if (downLoadCourse.getLessonLists().get(i).getDownStatus() == DownLoadStatusEnum.FINISHED.getValue()) {// 2
                    downCourseNum++;
                }
            }*/
            downCourseNum=ArrayUtils.size(downLoadCourse.getLessonLists());
            ImageLoad.displaynoCacheImage(this.img_offline_course.getContext(), R.drawable.icon_default, downLoadCourse.getImagePath(), this.img_offline_course);
            this.tv_offline_title.setText(downLoadCourse.getCourseName());


            if(downLoadCourse.totalSpace==0){
                this.tv_offline_num.setText(ResourceUtils.getString( R.string.down_finished_total,
                        downLoadCourse.getTotalNum()));
            }else {
                this.tv_offline_num.setText(ResourceUtils.getString( R.string.down_finished_total,
                        downLoadCourse.getTotalNum())+"  |  "+ CommonUtils.formatSpaceSize(downLoadCourse.totalSpace));//
            }
            if(downCourseNum<=0){
                this.tv_offline_total.setText("仅缓存了讲义");

            }else {
                this.tv_offline_total
                        .setText(ResourceUtils.getString(
                                R.string.down_finished_num,downCourseNum));
            }
         }
    }


}
