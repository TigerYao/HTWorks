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
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.utils.ArrayUtils;

import java.util.List;


/**
 * Created by cjx on 2018\7\20 0020.
 * 课程讲义
 */

public class CourseHandoutGroupAdapter extends SimpleBaseRecyclerAdapter<HandoutBean.CourseTypeInfo> {

    public CourseHandoutGroupAdapter(Context context, List<HandoutBean.CourseTypeInfo> items) {
        super(context, items);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
      /*  if(ArrayUtils.isEmpty(mItems)) return 1;
        return 0;  //0  讲义1  头部*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==1){

            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_middle_list_item, parent, false);
            return new CourseNumViewHolder(collectionView);
        }else {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_handout_list_item, parent, false);
            return new HandoutViewHolder(collectionView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType=getItemViewType(position);
        if(viewType==1){
            CourseNumViewHolder holderHead = (CourseNumViewHolder) holder;
            holderHead.bindUI(mItems.get(position), position);

        }
        else {
            HandoutViewHolder holderfour = (HandoutViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }
    }

    protected class CourseNumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle,mCourseNum;
        ImageView mOpenImg;
        View mRootView;

        CourseNumViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mCourseNum = (TextView) itemView.findViewById(R.id.course_num_txt);
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

        public void bindUI(HandoutBean.CourseTypeInfo lessionBean, int pos){
            mTitle.setText(lessionBean.title);
           // mCourseNum.setText(String.valueOf(lessionBean.classHour+"课时"));


            mOpenImg.setVisibility(View.VISIBLE);
            mOpenImg.setRotation(lessionBean.isClosed()?0:180);

        }
    }


    protected class HandoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle,mFileSizeTxt;
        //ImageView mDownStatusImg;

        View mRootView;
        DownBtnLayout mDownLayout;

        HandoutViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mDownLayout = (DownBtnLayout) itemView.findViewById(R.id.down_status_layout);
            mFileSizeTxt=(TextView)itemView.findViewById(R.id.filesize_txt) ;
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            mDownLayout.findViewById(R.id.down_status_layout).setOnClickListener(this);
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

        public void bindUI(HandoutBean.CourseTypeInfo lessionBean, int pos) {
            mTitle.setText(lessionBean.title);
            mFileSizeTxt.setText(lessionBean.fileSize+"M");
            if(lessionBean.downStatus==1) mDownLayout.setStatus(DownBtnLayout.DOWNLOADING);
            else if(lessionBean.downStatus==2) mDownLayout.setStatus(DownBtnLayout.FINISH);
            else mDownLayout.setStatus(DownBtnLayout.NORMAL);

        }
    }


}

