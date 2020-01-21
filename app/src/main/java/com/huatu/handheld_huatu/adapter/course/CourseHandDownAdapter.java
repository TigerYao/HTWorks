package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.List;


/**
 * 课程讲义下载页面Adapter
 */

public class CourseHandDownAdapter extends SimpleBaseRecyclerAdapter<HandoutBean.Course> {


    public CourseHandDownAdapter(Context context, List<HandoutBean.Course> items) {
        super(context, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_hand_down_item, parent, false);
        return new HandoutViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        HandoutViewHolder holderfour = (HandoutViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }


    protected class HandoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SwipeItemLayout swipeItemLayout;
        View mRootView;
        CheckBox checkBox;
        TextView tvIndex;
        TextView mTitle, mFileSizeTxt;
        LinearLayout llShare;
        TextView tvShare;

        HandoutViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView.findViewById(R.id.whole_content);
            swipeItemLayout = itemView.findViewById(R.id.swipe);
            checkBox = itemView.findViewById(R.id.cb_view);
            tvIndex = itemView.findViewById(R.id.tv_index);
            mTitle = itemView.findViewById(R.id.title_name_txt);
            llShare = itemView.findViewById(R.id.ll_share);
            tvShare = itemView.findViewById(R.id.tv_share);
            mFileSizeTxt = itemView.findViewById(R.id.filesize_txt);
            mRootView.setOnClickListener(this);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), null, EventConstant.SELECT_LESSON);
                }
            });
            llShare.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), null, EventConstant.SELECT_LESSON);
                    break;
                case R.id.ll_share:
                    onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), null, EventConstant.DOWN_SHARE_LESSON);
                    swipeItemLayout.close();
                    break;
            }
        }

        public void bindUI(HandoutBean.Course lessionBean, int pos) {
            tvIndex.setText(pos + 1 + "");
            mRootView.setOnClickListener(this);
            checkBox.setChecked(lessionBean.selected);
            mTitle.setText(lessionBean.title);
            if (lessionBean.downStatus == DownBtnLayout.DOWNLOADING) {                  // 正在下载
                checkBox.setVisibility(View.INVISIBLE);
                mFileSizeTxt.setTextColor(Color.parseColor("#FF777C"));
                mFileSizeTxt.setText("正在下载");
                tvShare.setText("正在下载");
            } else if (lessionBean.downStatus == DownBtnLayout.FINISH) {           // 下载完成
                checkBox.setVisibility(View.INVISIBLE);
                mFileSizeTxt.setText("下载完成");
                tvShare.setText("分享课件");
            } else {                                            // 显示文件大小
                checkBox.setVisibility(View.VISIBLE);
                mFileSizeTxt.setText(lessionBean.fileSize + "M");
                tvShare.setText("下载课件");
            }
        }
    }
}

