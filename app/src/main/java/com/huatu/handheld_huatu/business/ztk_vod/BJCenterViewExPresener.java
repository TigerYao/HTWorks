package com.huatu.handheld_huatu.business.ztk_vod;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.helper.SimpleAnimationListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.countdown.CountDownTimers;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

/**
 * Created by cjx on 2018\11\7 0007.
 */

public  class BJCenterViewExPresener extends BJCenterViewPresenterCopy  {

    private boolean mEnableNetWatcher=true;

    private View mTipLayout;
    CountDownTask mCountDownTask;
    // @BindView(R.id.tv_counttime_time)
    TextView mTvCountTime;

    //@BindView(R.id.tv_counttime_string)
    TextView mTvContTimeDes;

    private final int  COUNTDOWNINTERVAL=1000;

    private final int WAITING=2;
    private final int STARTED=0;
    private final int ONLIVEING=1;
    private final int ONLIVE_END=3;
    private int mStartPlayStatus=STARTED;//0开始(继续) ,1 直播中 红色背景,2  等待 3直播已结束
    private TextView mStartPlayBtn;
    private View mTipContinueView;

    private long mTickStartTime=0;

    public boolean canNetWatcher(){
        return mEnableNetWatcher;
    }

    public void onDestory(){
        setOutClickListener(null);
        if(null!=mCountDownTask)
            mCountDownTask.cancel();
    }

    private OnOutClickListener mOutClickListener=new OnOutClickListener() {
        @Override
        public void onViewClick(View v, int type) {
            //type ==-1 出错，0警告 ,1继续播放
            if(type==0) mEnableNetWatcher=false;
             onPlaybackWarnRetry(type);
        }
    };

    public void onPlaybackWarnRetry(int type){}

    public BJCenterViewExPresener(View centerView) {
        super(centerView);
        mStartPlayBtn=centerView.findViewById(R.id.start_play_btn);
    }

    public void showTrafficWarning(String warn) {
        setOutClickListener(mOutClickListener);
        hideTimeCounter();
        showWarning(warn);
    }

    public void showNoNetWorkWarning(String warn) {
        setOutClickListener(mOutClickListener);
        hideTimeCounter();
        showError(-1,"网络未连接，请检查网络设置");
    }

    private void hideTimeCounter(){
        if(null!=mCountDownTask){
            mCountDownTask.cancel();
        }
        if(null!=mTipLayout) {
            mTipLayout.setVisibility(View.GONE);
        }
    }

    public void showJudgeImg(boolean show){
        showJudge = show;
        if(show){
            if(mJudgeImg != null && mJudgeImg.getVisibility() == View.GONE)
                AnimUtils.AlphaShow(mJudgeImg,true);
                //mJudgeImg.setVisibility(View.VISIBLE);
        }
        else{
           if(mJudgeImg != null && mJudgeImg.getVisibility() == View.VISIBLE)
               AnimUtils.AlphaShow(mJudgeImg,false);
               // mJudgeImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(final int code, String message){
        super.showError(code,message);
        if(null!=mStartPlayBtn) mStartPlayBtn.setVisibility(View.GONE);
    }

    @Override
    public void showWarning(String warn) {
        super.showWarning(warn);
        if(null!=mStartPlayBtn) mStartPlayBtn.setVisibility(View.GONE);
    }

    @Override
    public void resetPlaySpeed(){
        setOutClickListener(null);
        super.resetPlaySpeed();
    }

    public void resetCoverStatus(){
        setCoverImageShow(true);
        dismissLoading();
    }

    private void switchBtn(int curStatus,boolean needShow){
        if(mStartPlayStatus!=curStatus){
            mStartPlayStatus=curStatus;
            int bgRes=0;
            int rightDrawable=0;
            if(mStartPlayStatus==WAITING){
                bgRes=R.drawable.play_btn_bg_selector;
                rightDrawable=R.drawable.ic_loading_wait;
                mStartPlayBtn.setVisibility(View.VISIBLE);
                mStartPlayBtn.setText("等待直播");
             }else if(mStartPlayStatus==ONLIVEING){
                mStartPlayBtn.setVisibility(View.VISIBLE);
                mStartPlayBtn.setText("直播中，立即观看");
                bgRes=R.drawable.play_btn_playing;
                rightDrawable=R.drawable.videoicon;
            }
            else if(mStartPlayStatus==STARTED){
                mStartPlayBtn.setVisibility(needShow? View.VISIBLE:View.GONE);
                mStartPlayBtn.setText("继续学习");
                bgRes=R.drawable.play_btn_bg_selector;
                rightDrawable=R.drawable.videoicon;
            }else if(mStartPlayStatus==ONLIVE_END){
                mStartPlayBtn.setVisibility(View.GONE);
            }
            if((bgRes!=0)&&(rightDrawable!=0)){
                mStartPlayBtn.setBackgroundResource(bgRes);
                Drawable drawable = ResourceUtils.getDrawable(rightDrawable);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                mStartPlayBtn.setCompoundDrawables(null, null, drawable, null);
            }
         }
    }

    public void tipContinueLearn(String title,boolean needShow){
         if(needShow){
            if(null==mTipContinueView){
                LayoutInflater factory = LayoutInflater.from(centerView.getContext());
                mTipContinueView=factory.inflate(R.layout.player_continuelearn_tip,(ViewGroup) centerView,false);
                ((TextView)mTipContinueView).setText("继续上次课程："+title);
                ((ViewGroup)centerView).addView(mTipContinueView);
                mTipContinueView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if("1".equals(v.getTag(R.id.reuse_tag2))){
                            return;
                        }
                        boolean onlyRemove="2".equals(v.getTag(R.id.reuse_tag2));
                        v.setTag(R.id.reuse_tag2,"1");//正在移除View
                         Animation slideOutAnimation = AnimationUtils.loadAnimation(centerView.getContext(), R.anim.input_method_exit);
                        slideOutAnimation.setAnimationListener(new SimpleAnimationListener(){
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                try{
                                    v.setVisibility(View.GONE);
                                    //https://www.jianshu.com/p/2722c99ab06d
                                    UniApplicationLike.getApplicationHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(null!=centerView){
                                                ((ViewGroup) centerView).removeView(v);
                                            }
                                         }
                                    });

                                    mTipContinueView=null;
                                }catch (Exception e){
                                    LogUtils.e(e.getMessage());
                                }
                            }
                        });
                        v.startAnimation(slideOutAnimation);
                        if(!onlyRemove)
                          onPlaybackWarnRetry(1);
                    }
                });
            }
            Animation slideInAnimation = AnimationUtils.loadAnimation(centerView.getContext(), R.anim.input_method_enter);
             mTipContinueView.startAnimation(slideInAnimation);
        }
        else {
               try{
                    if(null!=mTipContinueView&&(mTipContinueView.getParent()!=null)) {
                        if ("1".equals(mTipContinueView.getTag(R.id.reuse_tag2))) {
                            return;
                        }
                        mTipContinueView.setTag(R.id.reuse_tag2, "2");
                        mTipContinueView.performClick();
                    }
                }catch (Exception e){
                   LogUtils.e(e.getMessage());
               }
         }
    }

    public boolean setStartPlaybtnStatus(CourseWareInfo lessionInfo,boolean needShow ){
        if(mTickStartTime==0){
            mTickStartTime=CountDownTask.elapsedRealtime();//毫秒级
        }
        if (lessionInfo.videoType == 2) {
            if (lessionInfo.liveStatus == 0) { // 未开始
                // 显示直播未开始倒计时
                if(null!=mCountDownTask){
                    mCountDownTask.cancel();
                }
                switchBtn(WAITING,true);
                if(null==mTipLayout){
                    LayoutInflater factory = LayoutInflater.from(centerView.getContext());
                    mTipLayout=factory.inflate(R.layout.player_center_time_tip,(ViewGroup) centerView,false);
                    ((ViewGroup)centerView).addView(mTipLayout);
                    mTvCountTime=mTipLayout.findViewById(R.id.tv_counttime_time);
                    mTvContTimeDes=mTipLayout.findViewById(R.id.tv_counttime_string);
                }
                else {
                    mTipLayout.setVisibility(View.VISIBLE);
                }
                if (lessionInfo.liveStart >= 24 * 60 * 60) {
                    mTvContTimeDes.setText("距离直播开始还有：");
                    (mTvCountTime).setText(DateUtils.getCaculteTime(lessionInfo.liveStart));
                } else {
                    mTvContTimeDes.setText("倒计时：");
                    long diffTime=lessionInfo.liveStart*1000-(CountDownTask.elapsedRealtime()-mTickStartTime);
                    if(diffTime<=0){
                        hideTimeCounter();
                        lessionInfo.liveStatus=1;
                        switchBtn(ONLIVEING,true);
                        return true;
                    }
                    if(mCountDownTask ==null){
                        mCountDownTask = CountDownTask.create();
                    }

                    LogUtils.e("setStartPlaybtnStatus",lessionInfo.liveStart+","+diffTime);
                    mCountDownTask.until(mTvCountTime,CountDownTask.elapsedRealtime()+diffTime,COUNTDOWNINTERVAL,new CountDownTimers.OnCountDownListener() {
                        @Override
                        public void onTick(View view, long millisUntilFinished) {
                            ((TextView)view).setText(DateUtils.getCaculteTime(millisUntilFinished / 1000));
                        }
                        @Override
                        public void onFinish(View view) {
                            ((TextView)view).setText("直播已开始");
                            if(null!=mTipLayout) {
                                mTipLayout.setVisibility(View.GONE);
                            }
                            switchBtn(ONLIVEING,true);
                        }
                    });
                }

                ToastUtils.showShortToast(null,"直播未开始");
               // Snackbar.make(mStartPlayBtn,"直播未开始",Snackbar.LENGTH_SHORT).show();
                return false;
            } else if (lessionInfo.liveStatus == 1) { // 直播中
                hideTimeCounter();
                // 显示直播中状态
                switchBtn(ONLIVEING,true);
                return true;

            } else if (lessionInfo.liveStatus == 2) { // 已结束
                // 提交直播已结束提示
                hideTimeCounter();
                switchBtn(ONLIVE_END,false);
                if(null!=mStartPlayBtn&&(mStartPlayBtn.getVisibility()==View.VISIBLE)){
                    mStartPlayBtn.setVisibility(View.GONE);
                }
                ToastUtils.showShortToast(null,"直播已结束");
                return false;
            }
        }
        else {
            hideTimeCounter();
            switchBtn(STARTED,needShow);
            return true;
        }
        return true;
    }
}
