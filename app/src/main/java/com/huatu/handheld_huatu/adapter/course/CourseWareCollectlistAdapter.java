package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Typeface;
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
import com.huatu.handheld_huatu.mvpmodel.CourseWareCollectBean;
import com.huatu.handheld_huatu.ui.MultTagView;
import com.huatu.handheld_huatu.ui.MultTextView;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by cjx on 2018\7\20 0020.
 *
 */

public class CourseWareCollectlistAdapter extends SimpleBaseRecyclerAdapter<CourseWareCollectBean> {

    private int mActionMode=0;//正常0，编辑1
    private final int NormalMode=0;
    private final int EditMode=1;

    private int offdistance=0;

    public void setActionMode(boolean isEdit){
        mActionMode=isEdit? EditMode:NormalMode;
         this.notifyDataSetChanged();
    }

    public boolean isEditMode(){
        return mActionMode==EditMode;
    }

    public CourseWareCollectlistAdapter(Context context, List<CourseWareCollectBean> items) {
        super(context, items);
        offdistance= DensityUtils.dp2px(context,4);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View collectionView = LayoutInflater.from(mContext).inflate(R.layout.courseware_collect_list_item, parent, false);
        return new ViewHolder(collectionView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ViewHolder holderfour = (ViewHolder) holder;
        holderfour.bindUI(mItems.get(position), position);
    }

   Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL );
    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.chk_btn)
        CheckBox mCheckbox;

        @BindView(R.id.tv_time)
        TextView mTimeTxt;

        @BindView(R.id.tv_item_course_mine_title)
        TextView mTitleTxt;

        @BindView(R.id.techer_des_txt)
        TextView mTecherNameTxt;

        @BindView(R.id.tv_progress)
        TextView mProgressTxt;

        @BindView(R.id.circleProgressBar)
        QMUIProgressBar mProgressBar;

        @BindView(R.id.content_txt)
        TextView mClassTitleTxt;

        @BindView(R.id.techer_img)
        ImageView mTeacherAvater;



        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);
            mCheckbox.setOnClickListener(this);
            mProgressBar.setXoffset(0);
            mProgressBar.setYoffset(offdistance);
            mProgressBar.setTypeface(font);

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

        public void bindUI(CourseWareCollectBean lessionBean, int pos) {

            mTitleTxt.setText(lessionBean.title);

            mCheckbox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
            mCheckbox.setChecked(lessionBean.isSelect());

            mClassTitleTxt.setText(lessionBean.classTitle);
            mProgressBar.setProgressColor(0xFFFF6D73);

            if(lessionBean.videoType==2){

                if(lessionBean.liveStatus==0||lessionBean.liveStatus==2){
                    mProgressBar.setYoffset(0);
                    mProgressBar.setXoffset(0);

                    // 直播状态0未开始1进行中2已结束
                    boolean lookFinished=lessionBean.liveStatus==2;
                    mProgressBar.setTextColor(lookFinished?0xFFFF6D73:  0xFF49CF9E);//#
                    mProgressBar.setText(lookFinished?"已结束":"未开始");

                    if(lookFinished)
                        mProgressBar.setProgress(100);
                    else
                        mProgressBar.setProgress(0,false);
                    mProgressTxt.setText("");

                }else {
                    mProgressBar.setYoffset(0);
                    mProgressBar.setXoffset(0);
                    // 直播状态0未开始1进行中2已结束
                    mProgressBar.setProgressColor(0xFF49CF9E);
                    mProgressBar.setTextColor(  0xFF49CF9E);//#49CF9E
                    mProgressBar.setText( "直播中");
                    mProgressBar.setCurProgress( 75);
                    mProgressTxt.setText("");
                }

            }else {
                if(lessionBean.schedule==0){
                    mProgressBar.setYoffset(0);
                    mProgressBar.setXoffset(0);

                    mProgressBar.setTextColor(0xFF9B9B9B);
                    mProgressBar.setText("未观看");
                    mProgressBar.setProgress(0,false);
                    mProgressTxt.setText("");

                }else {
                    mProgressBar.setYoffset(offdistance);
                    boolean lookFinished=lessionBean.schedule>=100;
                    mProgressBar.setTextColor(lookFinished? 0xFFFF6D73: 0xFF4A4A4A);
                    mProgressBar.setText(lookFinished?"已看完":"观看至");

                    mProgressBar.setCurProgress(lookFinished?100:lessionBean.schedule);
                    mProgressTxt.setText((lookFinished?100:lessionBean.schedule)+"%");
                }

            }

            mTimeTxt.setText(lessionBean.dateDesc);

            int size=Math.min(1, ArrayUtils.size(lessionBean.teacherInfo));

            for(int i=0;i<size;i++){
                 mTecherNameTxt.setText(lessionBean.teacherInfo.get(i).teacherName);;
                ImageLoad.displaynoCacheUserAvater(mContext,lessionBean.teacherInfo.get(i).roundPhoto,mTeacherAvater,R.mipmap.user_default_avater);

            }

        }
    }


}

