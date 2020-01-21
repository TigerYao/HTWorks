package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.ui.recyclerview.LetterSortAdapter;
import com.huatu.widget.IncreaseProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2018\7\19 0019.
 */

public class PlayLessionAdapter extends SimpleBaseRecyclerAdapter<CourseWareInfo> {

    private final  int yellowColor= Color.parseColor("#FFCA0E");
    //选中的课件id
    private int mSelectId=0;
    public void setSelectId(int selId){
        if(mSelectId==selId) return;
        mSelectId=selId;
        this.notifyDataSetChanged();
    }

    private boolean isSameSelectId(int postion){
        return getItem(postion).coursewareId==mSelectId;
    }

    public PlayLessionAdapter(Context context, List<CourseWareInfo> items) {
        super(context,items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View collectionView = LayoutInflater.from(mContext).inflate(R.layout.play_course_list_item, parent, false);
        return new ViewHolderTwo(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolderTwo holderfour = (ViewHolderTwo) holder;
        holderfour.bindUI(mItems.get(position),position);
    }


    protected class ViewHolderTwo extends LetterSortAdapter.ViewHolder implements View.OnClickListener {

        TextView mTitle;
        ImageView mSelImg;
        View mRootView;

        ViewHolderTwo(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_txt);
            mSelImg = (ImageView) itemView.findViewById(R.id.sel_img);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if(isSameSelectId(getAdapterPosition())) return;
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);

                    setSelectId(getItem(getAdapterPosition()).coursewareId);
                    break;

            }
        }

        public void bindUI(CourseWareInfo lessionBean,int pos){

            boolean isselected=mSelectId==lessionBean.coursewareId;
            mRootView.setSelected(isselected);
            mSelImg.setVisibility(isselected ?View.VISIBLE:View.INVISIBLE);
            mTitle.setText(lessionBean.getSerialNumber()+" "+lessionBean.title);
            mTitle.setTextColor(isselected? yellowColor:Color.WHITE);
        }
    }
}
