package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.MultiItemTypeSupport;
import com.huatu.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by xing on 2018/3/29.
 */

public  class OnliveScheduleAdapter extends CommonAdapter<VideoBean.LiveCourse> implements View.OnClickListener {

    private int playIndex = -1;

    private Context mContext;
    int scheduleTextColorRed = Color.parseColor("#e9304e");
    int scheduleTextColorWhite = Color.parseColor("#ffffff");
    boolean isOfflineFirst=false;

    public int getPlayIndex(){
        return  playIndex;
    }
    public int getOffPlayIndex(){
        return isOfflineFirst? -1: playIndex;
    }

    public void playIndexNext(){
        playIndex++;

    }

    public boolean isPlayIndexValid() {
        if (playIndex < 0 || playIndex >= getCount()) {
            return false;
        }
        return true;
    }

    public void setInitPlayPostion(int playPostion){
        if(playPostion>-1){
            playIndex=playPostion;
            isOfflineFirst=true;
        }
     }

    public void setPlayPostion(int playPostion){
        if(isOfflineFirst) isOfflineFirst=false;
        playIndex=playPostion;
        this.notifyDataSetChanged();
    }

    public void setPlayFirstPostion(){
        if(isOfflineFirst) isOfflineFirst=false;
        if (playIndex < 0) {
            playIndex = 0;
        }
    }
    public void setIsPortrait(boolean isPortrait){
         ((MultiItemTypeSupportEx)this.mMultiItemSupport).setIsPortrait(isPortrait);
    }

    OnRecItemClickListener mClickListener;
    public void setOnItemClickListener(OnRecItemClickListener clickListener){
        mClickListener=clickListener;
    }

    public boolean isPortrait(){ return ((MultiItemTypeSupportEx)this.mMultiItemSupport).getIsPortrait();}

    public interface MultiItemTypeSupportEx<T> extends MultiItemTypeSupport<T>{
        boolean getIsPortrait();
        void setIsPortrait(boolean isPortrait);
    }

    public OnliveScheduleAdapter(List<VideoBean.LiveCourse> data,Context context) {
        super(data, new MultiItemTypeSupportEx<VideoBean.LiveCourse>() {
            private boolean mIsPortrait=true;

            @Override
            public void setIsPortrait(boolean isPortrait){
                mIsPortrait=isPortrait;
            }

            @Override
            public boolean getIsPortrait(){return  mIsPortrait;}

            @Override
            public int getLayoutId(int position, VideoBean.LiveCourse liveCourse) {
                if(mIsPortrait) {
                    return R.layout.item_list_schedule_portrait;
                } else {
                    return R.layout.item_list_sche;
                }
            }
            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public int getItemViewType(int position, VideoBean.LiveCourse liveCourse) {
                if(mIsPortrait) {
                    return 1;
                }
                return 0;
            }
        });
        mContext=context;
    }
    DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分
    //DecimalFormat df = new DecimalFormat("#");

    private String getLearningState(float progress){

        LogUtils.e("getLearningState",progress+"");
        if(progress<0.1)        return "未学习";
        else if(progress<100&&progress>=99.9) return "已学99.9%";
        else if(progress<100)  return "已学"+ df.format(progress)+"%";
        else if(progress>=100) return "已学完";
        return "已学完";
    }

    @Override
    public void convert(ViewHolder holder, final VideoBean.LiveCourse item, final int position) {
        holder.setText(R.id.textview_class_name, item.Title);
        String strTime = DateUtils.getStrTime(item.startTime);
        if(isPortrait()) {
            if (playIndex == position) {
                holder.setTextColorRes(R.id.textview_class_name, R.color.main_color);
            } else {
                holder.setTextColorRes(R.id.textview_class_name, R.color.gray_333333);
            }
            if (item.status == 1) {
                holder.setText(R.id.textview_class_time, strTime);
                holder.setViewVisibility(R.id.textview_class_living_tv, View.VISIBLE);
                holder.setText(R.id.textview_class_living_tv, "[直播中]");
            }
            else if (item.status == -1) {
                holder.setText(R.id.textview_class_time, strTime);
                holder.setViewVisibility(R.id.textview_class_living_tv, View.GONE);
                //holder.setText(R.id.textview_class_living_tv, "[直播]");
            }else {
                holder.setText(R.id.textview_class_time, getLearningState(item.process));
                holder.setViewVisibility(R.id.textview_class_living_tv, View.GONE);
            }

            holder.setText(R.id.live_video_item_teacher_tv, item.teacher);
        } else {
               holder.setText(R.id.textview_class_time, strTime);
                if (item.status == 1) {
                    holder.setViewVisibility(R.id.textview_class_living_tv, View.VISIBLE);
                    holder.setText(R.id.textview_class_living_tv, "[直播中]");
                }
                else if(item.status == -1){
                    holder.setViewVisibility(R.id.textview_class_living_tv, View.GONE);
                    //holder.setText(R.id.textview_class_living_tv, "[直播]");
                }
                else {
                    holder.setViewVisibility(R.id.textview_class_living_tv, View.GONE);
                }

            if (playIndex == position) {
                holder.setTextColor(R.id.textview_class_name, scheduleTextColorRed);
            } else {
                holder.setTextColor(R.id.textview_class_name, scheduleTextColorWhite);
            }
            holder.setText(R.id.textview_class_teacher_tv, item.teacher);
        }
        holder.setViewOnClickListener(R.id.live_video_item_judge_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.status == -1 ) {
                    ToastUtils.showShort("直播未开始，不能评价");
                    return;
                }
                if(!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络错误，请检查您的网络");
                    return;
                }
                if(null!=mClickListener)  mClickListener.onItemClick(position,v, EventConstant.EVENT_COMMENT);
                //CourseEvaluateActivity.newIntent(mContext, courseId, item.rid);
            }
        });
       // holder.getConvertView().setTag(R.id.txtATag,String.valueOf(position));
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //playIndex = position;
                if(null!=mClickListener)  mClickListener.onItemClick(position,v, EventConstant.EVENT_ALL);
              /*  savePlayProgress();
                playIndex = position;
                playingFlag = 1;
                btnStartBtn.setImageResource(R.drawable.video_pause_icon);
                startPlayViews(false);*/
            }
        });
    }

    @Override
   public void onClick(View v){
        if(v.getTag(R.id.txtATag)!=null){

            int pos= StringUtils.parseInt(v.getTag(R.id.txtATag).toString());

        }
   }
}
