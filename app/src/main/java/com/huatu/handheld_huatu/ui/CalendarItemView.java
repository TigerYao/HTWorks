package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.mvpmodel.DateLiveBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.widget.CenterAlignImageSpan;


/**
 * Created by cjx on 2018\12\6 0006.
 */

public class CalendarItemView extends FrameLayout implements View.OnClickListener {

    TextView mTimeView,mTitleView,mTeacherView;
    StatusbgTextView  mActionView;



    public CalendarItemView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public CalendarItemView(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public CalendarItemView(Context var1, AttributeSet var2, int var3){
        super(var1,var2,var3);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTimeView=(TextView)this.getChildAt(0);
        ViewGroup tmpViewGroup=((ViewGroup)this.getChildAt(1));

        mTitleView= (TextView) tmpViewGroup.getChildAt(0);
        mTeacherView= (TextView) tmpViewGroup.getChildAt(1);
        mActionView = (StatusbgTextView)this.getChildAt(2);
        this.mActionView.setOnClickListener(this);
    }

    private String mNetClassId;
    private int    mLocationNodeId;
    private int    mLessionId,mStatus;
    private CourseWareInfo mCourseWareInfo;

    private void buildCourseWare(DateLiveBean liveBean){
        if(null==mCourseWareInfo){
            mCourseWareInfo=new CourseWareInfo();
        }
        liveBean.buildCourseWare(mCourseWareInfo);
    }

    @Override
    public void onClick(View v){

        if(null!=mCourseWareInfo&&(mCourseWareInfo.tinyLive==1)){
            CommonUtils.startLiveRoom(RxUtils.getNewCompositeSubIfUnsubscribed(null), v.getContext(),  String.valueOf(mCourseWareInfo.classId), mCourseWareInfo.coursewareId+"", mCourseWareInfo.parentId, mCourseWareInfo.joinCode, mCourseWareInfo.bjyRoomId, mCourseWareInfo.sign);
//            LiveRoomActivity.start(v.getContext(), String.valueOf(mNetClassId), mCourseWareInfo.classId,mCourseWareInfo.parentId,mCourseWareInfo.coursewareId , 1, mCourseWareInfo.joinCode);
            return;
        }
        if(mStatus== StatusbgTextView.END||mStatus== StatusbgTextView.WAITING){
            Intent intent = new Intent(v.getContext(), BJRecordPlayActivity.class);
            intent.putExtra("classid", String.valueOf(mNetClassId));
            intent.putExtra(ArgConstant.FROM_ACTION,mLocationNodeId);
            intent.putExtra(ArgConstant.LESSION_ID,mLessionId);
            intent.putExtra(ArgConstant.TYPE, 1);
            v.getContext().startActivity(intent);

        }else {
             LiveVideoForLiveActivity.start(getContext(), mNetClassId, mCourseWareInfo, "");
        }
     }

    public void showUI(DateLiveBean liveBean){

        mNetClassId=String.valueOf(liveBean.netClassId);
        mLocationNodeId=liveBean.id;
        mLessionId=liveBean.lessonId;
        mStatus=liveBean.status;
        buildCourseWare(liveBean);
        mTimeView.setText(String.valueOf(liveBean.beginTime));
        mTitleView.setText(String.valueOf(liveBean.title));
        mTeacherView.setText(String.valueOf(liveBean.teacherName));


        //;//1点播2直播3直播回放
      /*  if(liveBean.videoType==2){
            mActionView.setStatus(liveBean.status);

            String tmpStr="[img] "+liveBean.actionDesc;
            SpannableStringBuilder builder = new SpannableStringBuilder(tmpStr);


            //得到drawable对象，即所要插入的图片
            Drawable d = ResourceUtils.getDrawable(liveBean.status==StatusbgTextView.ONLIVEING?R.mipmap.calendar_onlive_icon:R.mipmap.calendar_live_wait_icon);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

            //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

            CenterAlignImageSpan span = new CenterAlignImageSpan(d);


            int Startindex2=tmpStr.indexOf("[img]");
            builder.setSpan(span, tmpStr.indexOf("[img]"), Startindex2+"[img]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mActionView.setText(builder);
            //    mActionView.setCompoundDrawablesWithIntrinsicBounds( ResourceUtils.getDrawable(liveBean.status==0?R.mipmap.calendar_live_wait_icon:R.mipmap.calendar_onlive_icon),null,null,null);
        }else if(liveBean.videoType==3){
            mActionView.setStatus(StatusbgTextView.PLAYBACK);
            mActionView.setText(liveBean.actionDesc);
           // mActionView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
*/
        mActionView.setTextColor(liveBean.status==StatusbgTextView.END ? 0XFF9B9B9B: Color.WHITE);
        if(liveBean.status==StatusbgTextView.END){
            mActionView.setText(liveBean.actionDesc);
            mActionView.setStatus(StatusbgTextView.END);
        }else if(liveBean.status==StatusbgTextView.WAITING||(liveBean.status==StatusbgTextView.ONLIVEING)){
            String tmpStr="[img] "+liveBean.actionDesc;
            SpannableStringBuilder builder = new SpannableStringBuilder(tmpStr);


            //得到drawable对象，即所要插入的图片
            Drawable d = ResourceUtils.getDrawable(liveBean.status==StatusbgTextView.ONLIVEING?R.mipmap.calendar_onlive_icon:R.mipmap.calendar_live_wait_icon);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

            //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

            CenterAlignImageSpan span = new CenterAlignImageSpan(d);


            int Startindex2=tmpStr.indexOf("[img]");
            builder.setSpan(span, tmpStr.indexOf("[img]"), Startindex2+"[img]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mActionView.setText(builder);
            mActionView.setStatus(liveBean.status);
        }else {

            mActionView.setText(liveBean.actionDesc);
            mActionView.setStatus(StatusbgTextView.PLAYBACK);
        }

    }
}
