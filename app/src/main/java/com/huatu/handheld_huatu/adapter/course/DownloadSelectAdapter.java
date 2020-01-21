package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;

import java.util.List;

/**
 * Created by cjx on 2018\8\4 0004.
 */

public class DownloadSelectAdapter extends SimpleBaseRecyclerAdapter<DownLoadLesson> {


    public static boolean isCoursewareDownloaded(DownLoadLesson courseware) {
        if(courseware.getDownStatus() == DownLoadStatusEnum.FINISHED.getValue()) {//2
            return true;
        }
        return false;
    }

    public static boolean isCoursewareDownloading(DownLoadLesson courseware) {
        if(courseware.getDownStatus() ==DownLoadStatusEnum.INIT.getValue()               // -2
                || courseware.getDownStatus() == DownLoadStatusEnum.PREPARE.getValue()   //-1
                || courseware.getDownStatus() == DownLoadStatusEnum.START.getValue()     // 1
                || courseware.getDownStatus() == DownLoadStatusEnum.STOP.getValue()      //4
                || courseware.getDownStatus() == DownLoadStatusEnum.ERROR.getValue()) {  //3
            return true;
        }
        return false;
    }

    public DownloadSelectAdapter(Context context, List<DownLoadLesson> items) {
        super(context, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View collectionView = LayoutInflater.from(mContext).inflate(R.layout.list_item_course_download_layout, parent, false);
        return new  ViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ViewHolder holderfour = (ViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }


    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;
        ImageView mDownStatusImg;

        ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_record_lesson_name);
            mDownStatusImg = (ImageView) itemView.findViewById(R.id.iv_record_status);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);

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

        public void bindUI(DownLoadLesson item, int position) {


            int curPostion=position+1;
            Spannable span = new SpannableString(curPostion+"  "+item.getSubjectName());
            span.setSpan(new RelativeSizeSpan(1.2f), 0, curPostion<10 ?1:2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mTitle.setText(span);

            if (isCoursewareDownloaded(item)) {
               // holder.setViewVisibility(R.id.iv_record_status, View.INVISIBLE);
                mDownStatusImg.setVisibility( View.INVISIBLE);
                //holder.setImageResource(R.id.iv_record_status, R.drawable.record_down_play);

            } else if (isCoursewareDownloading(item)) {
                //holder.setViewVisibility(R.id.iv_record_status,View.INVISIBLE);
                mDownStatusImg.setVisibility( View.INVISIBLE);
                //holder.setImageResource(R.id.iv_record_status, R.drawable.record_down_unplay);

            } else if(item.isSelect()) {
                //holder.setViewVisibility(R.id.iv_record_status,View.VISIBLE);

                mDownStatusImg.setVisibility( View.VISIBLE);
                mDownStatusImg.setImageResource(R.mipmap.down_check_icon);

            } else {


                mDownStatusImg.setVisibility( View.VISIBLE);
                mDownStatusImg.setImageResource(R.mipmap.down_uncheck_icon);

            }

        }
    }
}
