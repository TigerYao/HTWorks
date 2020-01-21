package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by cjx on 2018\7\20 0020.
 * 课程讲义
 */

public class MyCourseSearchAdapter extends SimpleBaseRecyclerAdapter<CourseMineBean.ResultBean> {


    public MyCourseSearchAdapter(Context context, List<CourseMineBean.ResultBean> items) {
        super(context, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_mine_item, parent, false);
        return new MyCourseViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        MyCourseViewHolder holderfour = (MyCourseViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }


    protected class MyCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mRootView;


        @BindView(R.id.tv_item_course_mine_title)
        TextView mTitle;

        @BindView(R.id.iv_item_course_mine_gq)
        ImageView mOutDateImg;

        @BindView(R.id.tv_item_course_mine_timelength)
        TextView mTimeLength;

        @BindView(R.id.iv)
        ImageView mIsliveImg;

        @BindView(R.id.iv_item_course_mine_scaleimg)
        ImageView mCoverImg;

        @BindView(R.id.tv_item_course_mine_one_to_one)
        TextView mInfoCard;


        MyCourseViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            ButterKnife.bind(this, itemView);
            mInfoCard.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                    break;
                case R.id.tv_item_course_mine_one_to_one:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_JOIN_IN);
                    break;
            }
        }

        public void bindUI(CourseMineBean.ResultBean mineItem, int pos) {
            //显示数据

            mTitle.setText(mineItem.title);
            //是否过期
            if (mineItem.isStudyExpired == 1) {
                mOutDateImg.setVisibility(View.VISIBLE);
            } else {
                mOutDateImg.setVisibility(View.GONE);
            }
            if (mineItem.courseType == 0) {
                //录播课的购课详情页，和我的课程列表，如果是录播课，不显示时间了，只显示课时哈
                mTimeLength.setText( mineItem.TimeLength);
            } else if (TextUtils.isEmpty(mineItem.endDate)) {
                mTimeLength.setText( mineItem.startDate + " (" + mineItem.TimeLength + ")");
            } else {
                mTimeLength.setText( mineItem.startDate  + "-" + mineItem.endDate + " (" + mineItem.TimeLength + ")");
            }
            if (mineItem.iszhibo == 1) {
                //是否为今日直播课
                mIsliveImg.setVisibility( View.VISIBLE);
            } else {
                mIsliveImg.setVisibility( View.GONE);
            }

            ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,mineItem.scaleimg,mCoverImg);

            if (mineItem.oneToOne == 1) {
                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.main_color)) ;
                mInfoCard.setText("填写信息卡");
            } else if (mineItem.oneToOne == 2) {

                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.course_center_content)) ;
                mInfoCard.setText("查看信息卡");


            } else if (Method.isEqualString("1",mineItem.isMianshou)) {

                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.course_center_content)) ;
                mInfoCard.setText("查看协议");


            } else {
                mInfoCard.setVisibility(View.GONE);
            }
        }
    }


}

