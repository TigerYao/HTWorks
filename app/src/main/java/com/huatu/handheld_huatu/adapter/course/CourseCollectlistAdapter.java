package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.CourseCollectBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.MultTagView;
import com.huatu.handheld_huatu.ui.MultTextView;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.ArrayUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by cjx on 2018\7\20 0020.
 *
 */

public class CourseCollectlistAdapter extends SimpleBaseRecyclerAdapter<CourseCollectBean> {

    private int mActionMode=0;//正常0，编辑1
    private final int NormalMode=0;
    private final int EditMode=1;

    public void setActionMode(boolean isEdit){
        mActionMode=isEdit? EditMode:NormalMode;
         this.notifyDataSetChanged();
    }

    public boolean isEditMode(){
        return mActionMode==EditMode;
    }

    public CourseCollectlistAdapter(Context context, List<CourseCollectBean> items) {
        super(context, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_collect_list_item, parent, false);
        return new ViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ViewHolder holderfour = (ViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }


    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.chk_btn)
        CheckBox mCheckbox;

        @BindView(R.id.tv_time)
        TextView mTimeTxt;

        @BindView(R.id.tv_item_course_mine_title)
        TextView mTitleTxt;

        @BindView(R.id.tv_person_rush)
        TextView mBuyNumTxt;

        @BindView(R.id.tv_price)
        TextView mPriceTxt;

        @BindView(R.id.tv_free)
        TextView mFreeTxt;

        @BindView(R.id.techer_des_txt)
        MultTextView  mTeacherDes;

        @BindView(R.id.tv_tag)
        MultTagView mTagView;

        ImageView[] mTeacherAvaters;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mTeacherAvaters=new ImageView[3];
            mTeacherAvaters[0]=itemView.findViewById(R.id.teacher_first_view);
            mTeacherAvaters[1]=itemView.findViewById(R.id.teacher_second_view);
            mTeacherAvaters[2]=itemView.findViewById(R.id.teacher_third_view);

            itemView.findViewById(R.id.whole_content).setOnClickListener(this);

            itemView.findViewById(R.id.delete).setOnClickListener(this);
            mCheckbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chk_btn:
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),isEditMode()?mCheckbox:v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_DELETE);
                    break;
            }
        }

        public void bindUI(CourseCollectBean lessionBean, int pos) {

            mCheckbox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
            mCheckbox.setChecked(lessionBean.isSelect());
            mTimeTxt.setText(lessionBean.liveDate+(lessionBean.timeLength>0 ? String.format(" (%d课时)",lessionBean.timeLength):""));
            mTitleTxt.setText(lessionBean.title);
            mBuyNumTxt.setText(lessionBean.description);
             if(lessionBean.actualPrice<=0){
                mPriceTxt.setVisibility(View.GONE);
                mFreeTxt.setVisibility(View.VISIBLE);
            }
             else {
                mPriceTxt.setText("¥"+String.valueOf(lessionBean.actualPrice));
                mPriceTxt.setVisibility(View.VISIBLE);
                mFreeTxt.setVisibility(View.GONE);
            }
            int size=Math.min(3, ArrayUtils.size(lessionBean.teacherInfo));
            String[] names=new String[size];
            for(int i=0;i<3;i++){
                if(i<size){
                    names[i]=lessionBean.teacherInfo.get(i).teacherName;
                    mTeacherAvaters[i].setVisibility(View.VISIBLE);
                    ImageLoad.displaynoCacheUserAvater(mContext,lessionBean.teacherInfo.get(i).roundPhoto,mTeacherAvaters[i],R.mipmap.user_default_avater);
                }else
                    mTeacherAvaters[i].setVisibility(View.GONE);

            }
            mTeacherDes.setNameList(names);

            //String[] strings = new String[ArrayUtils.size(lessionBean.activeTag)];
            //lessionBean.activeTag.toArray(strings);
            mTagView.setNameList(lessionBean.activeTag);
        }
    }


}

