package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.listener.EventConstant;

import java.util.List;


/**
 * Created by cjx on 2018\7\20 0020.
 * 课程讲义
 */

public class CourseHandoutManageAdapter extends SimpleBaseRecyclerAdapter<DownHandout> {

    private int mActionMode = 0;//正常0，编辑1
    private final int NormalMode = 0;
    private final int EditMode = 1;

    public void setActionMode(boolean isEdit) {
        mActionMode = isEdit ? EditMode : NormalMode;

        this.notifyDataSetChanged();
    }


    public boolean isEditMode() {
        return mActionMode == EditMode;
    }


    public CourseHandoutManageAdapter(Context context, List<DownHandout> items) {
        super(context, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_handoutadmin_list_item, parent, false);
        return new HandoutViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        HandoutViewHolder holderfour = (HandoutViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }


    protected class HandoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle, mFileSizeTxt;
        //ImageView mDownStatusImg;
        CheckBox mChkBox;
        View mRootView;


        HandoutViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            this.mChkBox = (CheckBox) itemView.findViewById(R.id.chk_btn);
            mFileSizeTxt = (TextView) itemView.findViewById(R.id.filesize_txt);
            mChkBox.setOnClickListener(this);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.chk_btn:
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), isEditMode() ? mChkBox : v, EventConstant.EVENT_ALL);
                    break;

                case R.id.delete:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_DELETE);
                    break;
            }
        }

        public void bindUI(DownHandout lessionBean, int pos) {

            mChkBox.setVisibility(mActionMode == EditMode ? View.VISIBLE : View.GONE);
            mChkBox.setChecked(lessionBean.isSelect());
            mTitle.setText(lessionBean.getSubjectName());
            mFileSizeTxt.setText(lessionBean.getReserve1() + "M");


        }
    }


}

