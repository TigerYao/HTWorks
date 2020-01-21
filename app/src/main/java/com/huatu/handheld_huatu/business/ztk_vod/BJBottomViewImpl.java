package com.huatu.handheld_huatu.business.ztk_vod;

import android.content.res.Configuration;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.playerview.IPlayerBottomContact;
import com.baijiahulian.player.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.ztk_vod.view.OnShowInclassListener;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWarePointBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassAnswerCardBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PointSeekBar;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;

import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.internal.ViewCompat;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\7\18 0018.
 */


public class BJBottomViewImpl implements IPlayerBottomContact.BottomView , View.OnClickListener  {
    private IPlayerBottomContact.IPlayer mPlayer;
    private int mDuration,mCurrentPlayPosition,mSeekPostion;
    private PointSeekBar mSeekBar;
    private boolean isSeekBarDraggable = true;
    private TextView videoDuration;
    private TextView playDuration;
    private ImageView image_change_screen;
    private ImageButton btnPlay;
    private TextView mHorTimeText,mInputDanmuText;
    private TextView mChangeSpeedTxt,mChangelessionTxt,mChangeDefinitionTxt;
    private ViewGroup mRootView,mSeekLayout;
    private int mOrientation= BJPlayerView.VIDEO_ORIENTATION_PORTRAIT;
    private OnShowInclassListener mOnShowInclassListener;
    private SparseArray<PointSeekBar.Point> mPointArray=new SparseArray<PointSeekBar.Point>();

    private boolean mIsSeekUserTouch=false;

    OnAudioContractListener mOnAudioContractListener;
    public interface OnAudioContractListener {
         void onPlayPauseClick(View view);

         void onSeekTo(int ms);

         void switchPlayMode();
    }

    public void setOnAudioContractListener(OnAudioContractListener OnAudioContractListener){
        mOnAudioContractListener=OnAudioContractListener;
    }

    public boolean isCurOrientationPortrait(){
        return mOrientation== BJPlayerView.VIDEO_ORIENTATION_PORTRAIT;
    }

    private boolean mIslocal=false;
    private boolean mEnableDanmu=true;


    public void setOnShowInClassListener(OnShowInclassListener onInclassListener){
        mOnShowInclassListener=onInclassListener;
    }

    BJCustomCenterView mCenterPresenter;
    public void setIPlayerCenterContact(BJCustomCenterView centerContact){
        mCenterPresenter=centerContact;
    }

    public void setLayoutShow(boolean needShow){
        mRootView.findViewById(R.id.playerBottomLayout).setVisibility(needShow?View.VISIBLE:View.GONE);
        if(null!=mSeekBar) mSeekBar.setVisibility(needShow?View.VISIBLE:View.GONE);
    }

    public void hideSwitchScreen(){
        if(null!=image_change_screen){
            image_change_screen.setVisibility(View.GONE);
        }
    }

    public void disableDanmuInput(){
        mEnableDanmu=false;
        if(mInputDanmuText!=null) mInputDanmuText.setVisibility(View.GONE);
    }

    @Override
    public   void onClick(View v){
        switch (v.getId()){
            case R.id.changeSpeed_txt:
                if(mCenterPresenter!=null)
                    mCenterPresenter.showActionPanel(BJCustomCenterView.ActionType.SHOWSPEED);
                break;
            case R.id.changeDefinition_txt:
                if(mCenterPresenter!=null&&(!mIslocal))
                    mCenterPresenter.showActionPanel(BJCustomCenterView.ActionType.SHOWDEFINITION);
                break;
        }
    }
    TextView  mPlayModeView ;
    public void showAudioPlayMode(boolean isPad){

        int unit= DensityUtils.dp2px(mRootView.getContext(),1);
        mPlayModeView=new TextView(mRootView.getContext());
        mPlayModeView.setText("顺序播放");
        mPlayModeView.setTextColor(Color.WHITE);
        mPlayModeView.setTextSize(13);
         RelativeLayout.LayoutParams tmpParam=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        tmpParam.rightMargin=(isPad?25:20)*unit;
        tmpParam.addRule(RelativeLayout.CENTER_VERTICAL);
       // tmpParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
         tmpParam.addRule(RelativeLayout.LEFT_OF,R.id.changeLession_txt);

        mPlayModeView.setPadding(5*unit,3*unit,5*unit,3*unit);
        mPlayModeView.setGravity(Gravity.CENTER_VERTICAL);

        ViewCompat.setBackground(mPlayModeView, ResourceUtils.getDrawable(R.drawable.play_mode_landscape_selector));
        mPlayModeView.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(R.drawable.play_mode_land_selector),null,null,null);
        mPlayModeView.setCompoundDrawablePadding(2*unit);
        if(isPad){
            mPlayModeView.setVisibility(View.GONE);
        }
        this.mSeekLayout.addView(mPlayModeView,4,tmpParam);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnAudioContractListener!=null){
                    v.setSelected(!v.isSelected());
                    ((TextView)v).setText(v.isSelected()?"循环播放":"顺序播放");
                    ToastUtils.showEssayToast(v.isSelected()? "已切换循环播放":"已切换顺序播放");
                    if(mCenterPresenter!=null)
                        mCenterPresenter.showActionPanel(BJCustomCenterView.ActionType.SHOWAUDIOMODE);
                    mOnAudioContractListener.switchPlayMode();
                }
            }
        });
    }

    public BJBottomViewImpl(View bottomView,boolean islocal) {
        this.mIslocal=islocal;
        this.mRootView=(ViewGroup) bottomView;
        this.mSeekLayout=(ViewGroup)bottomView.findViewById(R.id.seekLayout);

        mInputDanmuText=(TextView)bottomView.findViewById(R.id.danmu_input_btn);

        RelativeLayout.LayoutParams inputParm=(RelativeLayout.LayoutParams)mInputDanmuText.getLayoutParams();

        int screenHeight=Math.max(DisplayUtil.getScreenWidth(),DisplayUtil.getScreenHeight());
        int leftDistance=screenHeight-DensityUtils.dp2px(bottomView.getContext(),320);
        inputParm.leftMargin=leftDistance/2;
        mInputDanmuText.setLayoutParams(inputParm);

        videoDuration = (TextView) bottomView.findViewById(R.id.videoDuration);
        playDuration = (TextView) bottomView.findViewById(R.id.playDuration);
        image_change_screen = (ImageView) bottomView.findViewById(R.id.image_change_screen);
        mSeekBar = (PointSeekBar) bottomView.findViewById(R.id.skbProgress);
        btnPlay = (ImageButton) bottomView.findViewById(R.id.btnPlay);
        mHorTimeText=(TextView) bottomView.findViewById(R.id.horPlayTime_txt);

        mChangeSpeedTxt=(TextView) bottomView.findViewById(R.id.changeSpeed_txt);
        mChangeSpeedTxt.setOnClickListener(this);
        mChangelessionTxt=(TextView) bottomView.findViewById(R.id.changeLession_txt);
        mChangelessionTxt.setVisibility(View.GONE);
        mChangeDefinitionTxt=(TextView) bottomView.findViewById(R.id.changeDefinition_txt);
        mChangeDefinitionTxt.setOnClickListener(this);

        updateVideoProgress();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("btnPlay", "btnPlay");
/*
               */
                if(null!=mOnAudioContractListener){
                    mOnAudioContractListener.onPlayPauseClick(view);
                    return;
                }
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.pauseVideo();
                    } else {
                       /* if (mSeekPostion != -1) {
                            mPlayer.seekVideo(mSeekPostion);
                            LogUtils.e("seekVideo",mSeekPostion+"");
                        }*/
                        mPlayer.playVideo();
                    }
                }
            }
        });
        mSeekBar.setOnPopUplistener(new PointSeekBar.onPopupListener() {
            @Override
            public void onViewPopUp(View view, PointSeekBar.Point value) {
                showLeanPopWindow(view, value);
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean userTouch;
            int oldProgress=0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //LogUtils.e("seekbar1",progress+","+fromUser);
                if(fromUser){
                    if(mCenterPresenter != null) {
                        mCenterPresenter.showProgressPercentSlide(oldProgress,progress,mDuration,false);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSeekUserTouch= userTouch = true;
                oldProgress=seekBar.getProgress();
               // LogUtils.e("seekbar",seekBar.getProgress()+"");
                onStartSeek(oldProgress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (userTouch) {

                    if(null!=mOnAudioContractListener){
                        int pos = seekBar.getProgress() * mDuration / 100;
                        if(mCenterPresenter != null) {
                            mCenterPresenter.showProgressPercentSlide(0,0,mDuration,true);
                        }
                        mSeekPostion=pos;
                        mOnAudioContractListener.onSeekTo(pos);
                        mIsSeekUserTouch= userTouch = false;
                        oldProgress=0;
                        return;
                    }
                    if(mCenterPresenter != null) {
                        mCenterPresenter.showProgressPercentSlide(0,0,mDuration,true);
                    }
                    int pos = seekBar.getProgress() * mDuration / 100;
                    mSeekPostion=pos;
                    mPlayer.seekVideo(pos);
                    Log.e("position", pos + "sss");
                }
                mIsSeekUserTouch= userTouch = false;
                oldProgress=0;
            }
        });

        image_change_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer != null) {
                    mPlayer.switchOrientation();
                }
            }
        });
    }

    public void setFullScreenClickListener(final View.OnClickListener onClickListener){
       // mOrientation=isPortrait? BJPlayerView.VIDEO_ORIENTATION_PORTRAIT:BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE;
        image_change_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)
                    onClickListener.onClick(v);
            }
        });

    }

    ToolTipsManager mToolTipsManager;
    private void showTip(final  View mTextView, ViewGroup rootLayout,String message){
        if(null==mToolTipsManager){
            mToolTipsManager = new ToolTipsManager(new ToolTipsManager.TipListener() {
                @Override
                public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
                    mToolTipsManager.findAndDismiss(mTextView);
                }
            },0.7f);
        }
        // message="注册就送图币";
        mToolTipsManager.findAndDismiss(mTextView);
        ToolTip.Builder builder;
        builder = new ToolTip.Builder(mTextView.getContext(), mTextView, rootLayout, message, ToolTip.POSITION_ABOVE);
        builder.setAlign(ToolTip.ALIGN_LEFT);
        // builder.setGravity(ToolTip.GRAVITY_CENTER);
        builder.setTextAppearance(R.style.TooltipStyle);
        builder.setBackgroundColor(0xFFFFE0AB);
        builder.setOffsetX(DensityUtils.dp2px(mTextView.getContext(),10));
        //  builder.setTypeface(mCustomFont);
        mToolTipsManager.show(builder.build());
        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null==mToolTipsManager) mToolTipsManager.findAndDismiss(mTextView);
            }
        },4000);
    }

    public void showCaton(){
        if(mOrientation == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE)
           showTip(mChangeDefinitionTxt,(ViewGroup) mRootView.getParent(),"当前网络较差,换个网络试试");
        else {
            showTip(image_change_screen,(ViewGroup) mRootView.getParent(),"当前网络较差,换个网络试试");

        }
    }

    public void showChangeScreen(boolean show){
        if (show && image_change_screen.getVisibility() != View.VISIBLE)
            image_change_screen.setVisibility(View.VISIBLE);
        else if(!show && image_change_screen.getVisibility() == View.VISIBLE)
             image_change_screen.setVisibility(View.GONE);
    }

    @Override
    public void onBind(IPlayerBottomContact.IPlayer iPlayer) {
        mPlayer = iPlayer;
        setOrientation(mPlayer.getOrientation());
        setIsPlaying(mPlayer.isPlaying());
        Log.e("onBind", "onBind");
    }

    @Override
    public void setDuration(int duration) {
         mDuration = duration;
       // mPlayOnlineTime=System.currentTimeMillis();
        updateVideoProgress();
    }

    //点击跳转时，停留点的时间，如果不处理，关闭答题时，会弹两次
    int mClikJumpPoint=-1;
    @Override
    public void setCurrentPosition(int position) {

        LogUtils.e("setCurrentPosition",position+"");
         mCurrentPlayPosition = position;
        //savePlayProgress(currenttTitlePosition,false);
        updateVideoProgress();
        if(position==mClikJumpPoint){//会出现两次
            //mClikJumpPoint=-1;
            return;
        }else {
            if(mClikJumpPoint!=-1){
                mClikJumpPoint=-1;
            }
        }
        PointSeekBar.Point curPoint=  mPointArray.get(position);
        if(null!=curPoint&&(TextUtils.isEmpty(curPoint.userAnswer)||("0".equals(curPoint.userAnswer)))){
            mSelectPoint=curPoint;
            showInclassPoint(false);
        }
    }

    public boolean refreshCurPositionAnswareCard(AnswerCardBean singleCardBean, CourseWareInfo selCourseWare, CourseInfoBean courseInfo){

        if(null!=mSeekBar){
            List<PointSeekBar.Point> learnPoints= mSeekBar.getAllPoint();
            if(!ArrayUtils.isEmpty(learnPoints)){

                boolean isAllFinish=true;
                PointSeekBar.Point curPoint=null;
                for(PointSeekBar.Point point:learnPoints){
                   if(point.id==singleCardBean.questionId){
                      /*  public int answer;
                        public int correct;
                        public int time;
                        */
                        point.userAnswer=String.valueOf(singleCardBean.answer);
                        point.correct=singleCardBean.correct;
                        point.time=singleCardBean.time;
                        curPoint=point;
                    }
                    if(point.correct==0) isAllFinish=false;
                    if((null!=curPoint)&&(!isAllFinish)) {//提前结束loop   测试题目一样,会导致后面没有设值
                       break;
                    }
                }
                if(isAllFinish){//提交答题卡

                    List<AnswerCardBean> list = new ArrayList<>();
                    for(PointSeekBar.Point point:learnPoints){
                        list.add(createAnswerCardBean(point));
                    }
                    JsonArray jsonArray = GsonUtil.getGson().toJsonTree(list,new TypeToken<List<AnswerCardBean>>(){}.getType()).getAsJsonArray();
                    ServiceExProvider.visit(CourseApiService.getApi().submitAnswerCard(mPracticesId, jsonArray));
                }

                if((null!=selCourseWare)&&(null!=courseInfo)){
                    StudyCourseStatistic.sendInclassWorkReport(courseInfo.courseId+"",courseInfo.title,String.valueOf(selCourseWare.classId)
                            ,selCourseWare.title,curPoint,isAllFinish,ArrayUtils.size(learnPoints));
                }

                return isAllFinish;
            }
        }
        return false;
    }


    private   AnswerCardBean createAnswerCardBean(PointSeekBar.Point bean) {
        AnswerCardBean answerCardBean = new AnswerCardBean();
        answerCardBean.questionId = (int)bean.id;
        answerCardBean.answer =StringUtils.parseInt(bean.userAnswer) ;
        answerCardBean.time = bean.time;
        answerCardBean.correct = bean.correct;
        answerCardBean.doubt = 0;
        if (answerCardBean.answer != 0 && answerCardBean.correct != 0 && answerCardBean.time <= 0) {
            answerCardBean.time = 1;
        }
        return answerCardBean;
    }

    private boolean mIsPlaying;
    public boolean isPlaying() {
        return mIsPlaying;
    }
    @Override
    public void setIsPlaying(boolean isPlaying) {

        Log.e("setIsPlaying", "setIsPlaying");
        if (isPlaying) {
            btnPlay.setImageResource(R.drawable.btn_video_small_pause);
        } else {

            btnPlay.setImageResource(R.drawable.btn_video_small_play);
        }
        mIsPlaying=isPlaying;
        if(null!=mCenterPresenter) mCenterPresenter.showPauseStatus(isPlaying ?false:true);
    }

    @Override
    public void setOrientation(int orientation) {
        LogUtils.e("setOrientation",orientation+"");
        if (orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT) {
            image_change_screen.setVisibility(View.VISIBLE);
            mHorTimeText.setVisibility(View.GONE);
            if(null!=mPlayModeView){
                mPlayModeView.setVisibility(View.GONE);
            }
            //listview_ml.setVisibility(View.GONE);
        } else {
            if(null!=mPlayModeView){
                mPlayModeView.setVisibility(View.VISIBLE);
            }
            image_change_screen.setVisibility(View.GONE);
            mHorTimeText.setVisibility(View.VISIBLE);
            String positionText = Utils.formatDuration(mCurrentPlayPosition, mDuration >= 3600);
            String durationText = Utils.formatDuration(mDuration);
            mHorTimeText.setText(positionText+"/"+durationText);

         }
        if(mEnableDanmu){
             mInputDanmuText.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.GONE:View.VISIBLE);
        }
        mChangeSpeedTxt.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.GONE:View.VISIBLE);

        if("1".equals(mChangelessionTxt.getTag())){
            mChangelessionTxt.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.GONE:View.VISIBLE);
        }
        mChangeDefinitionTxt.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.GONE:View.VISIBLE);

        videoDuration.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.VISIBLE:View.GONE);
        playDuration.setVisibility(orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT?View.VISIBLE:View.GONE);
        if(mOrientation!=orientation) {

            mOrientation=orientation;
            if(mOrientation == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE){
                mSeekLayout.removeView(mSeekBar);
                if(mSeekBar.getParent()!=null&&(mSeekBar.getParent() instanceof ViewGroup)){
                     ((ViewGroup) mSeekBar.getParent()).removeView(mSeekBar);
                }
                mRootView.addView(mSeekBar,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }else {
                mRootView.removeView(mSeekBar);

                RelativeLayout.LayoutParams tmpParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                tmpParams.leftMargin= DensityUtils.dp2px(mSeekBar.getContext(),34);
                tmpParams.rightMargin=DensityUtils.dp2px(mSeekBar.getContext(),34);
                tmpParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

                if(mSeekBar.getParent()!=null&&(mSeekBar.getParent() instanceof ViewGroup)){
                    ((ViewGroup) mSeekBar.getParent()).removeView(mSeekBar);
                }
                mSeekLayout.addView(mSeekBar,tmpParams);
            }

        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        // 只有 100ms 的 buf, ui 上根本看不出来
//        mSeekBar.setSecondaryProgress(mDuration == 0 ? 0 : mSeekBar.getProgress() + percent * 100 / mDuration);
    }

    @Override
    public void setSeekBarDraggable(boolean canDrag) {
        this.isSeekBarDraggable = canDrag;
    }

    private void updateVideoProgress() {
        String durationText = Utils.formatDuration(mDuration);
        String positionText = Utils.formatDuration(mCurrentPlayPosition, mDuration >= 3600);

        if(mPlayer==null||(mPlayer.getOrientation()==BJPlayerView.VIDEO_ORIENTATION_PORTRAIT)){
            videoDuration.setText(durationText);
            playDuration.setText(positionText);
        }
        else {
            mHorTimeText.setText(positionText+"/"+durationText);
        }
        if(!mIsSeekUserTouch)
           mSeekBar.setProgress(mDuration == 0 ? 0 : mCurrentPlayPosition * 100 / mDuration);
         Log.e("updateVideoProgress", "updateVideoProgress" + "``" + durationText + "``" + positionText);

    }

    public void setOnLessionClickListener(final View.OnClickListener listener) {
        mChangelessionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
              }
        });
        mChangelessionTxt.setTag("1");
    }


    public void setActionText(BJCustomCenterView.ActionType actionType,String txtValue){
        if(actionType==BJCustomCenterView.ActionType.SHOWSPEED){
            mChangeSpeedTxt.setText(txtValue);
        }else if(actionType==BJCustomCenterView.ActionType.SHOWAUDIOMODE){
            if(null!=mPlayModeView){
                mPlayModeView.setSelected(!mPlayModeView.isSelected());
                mPlayModeView.setText(mPlayModeView.isSelected()?"循环播放":"顺序播放");
            }
        }
        else  {
           mChangeDefinitionTxt.setText(txtValue);
        }
    }

    public void resetPoint(){
        mPointArray.clear();
        mSeekBar.setDotsVisibility(false,null);
    }

    public void onStartSeek(int position){

    }

    public void showLearnPoint(CourseWareInfo selCourseWare,CompositeSubscription comSubScrip){
        ServiceExProvider.visit(comSubScrip, CourseApiService.getApi().createPracticesInfo(selCourseWare.coursewareId,selCourseWare.videoType),//selCourseWare.videoType
                new NetObjResponse<InClassAnswerCardBean>() {
                    @Override
                    public void onError(String message, int type) {  }

                    @Override
                    public void onSuccess(BaseResponseModel<InClassAnswerCardBean> model) {
                        mPracticesId=model.data.id;
                        if(mDuration<=0) return;

                        if(mSeekBar!=null&&model.data.paper!=null ){
                            List<InClassAnswerCardBean.BreakPoint> tmpPoints=  model.data.paper.breakPointInfoList;
                            if(ArrayUtils.isEmpty(tmpPoints)) return;
                            List<PointSeekBar.Point> tmpList=new ArrayList<>();

                            List<String> tmpAnsw=model.data.answers;
                            List<Integer> tmpTimes=model.data.times;

                            List<Integer> tmpCorrects=model.data.corrects;
                            int i=0;
                            for(InClassAnswerCardBean.BreakPoint bean:model.data.paper.breakPointInfoList){
                                if(!ArrayUtils.isEmpty(bean.questionInfoList)){

                                    PointSeekBar.Point tmpPoint=new PointSeekBar.Point(bean.position/mDuration,DensityUtils.dp2px(mSeekBar.getContext(),8),
                                            StringUtils.parseLong(bean.questionInfoList.get(0)),bean.pointName);

                                    String curResult= ArrayUtils.getAtIndex(tmpAnsw,i);
                                    if(TextUtils.isEmpty(curResult)||"0".equals(curResult))
                                        curResult="";
                                    tmpPoint.userAnswer=curResult;

                                    // 添加时间与正确字段
                                    Integer curTime=ArrayUtils.getAtIndex(tmpTimes,i);
                                    tmpPoint.time=(curTime!=null) ? curTime.intValue():0;

                                    Integer curCorrect=ArrayUtils.getAtIndex(tmpCorrects,i);
                                    tmpPoint.correct=(curCorrect!=null) ? curCorrect.intValue():0;

                                    tmpList.add(tmpPoint);
                                    mPointArray.put((int)bean.position,tmpPoint);
                                    i++;
                                }
                            }
                            mSeekBar.setDotsVisibility(true,tmpList);
                            LogUtils.e("showLearnPoint", GsonUtil.GsonString(tmpList));
                        }
                    }
        });
     }

     private void showInclassPoint(boolean needSeek){
         if(mSelectPoint==null) return;
         if(mPlayer!=null&&mPlayer.isPlaying()) {
             mPlayer.pauseVideo();//.onPause();
             if(needSeek){
                 int pointTime=0;
                 for(int i=0;i<mPointArray.size();i++){
                     PointSeekBar.Point curPoint= mPointArray.valueAt(i);
                     if(curPoint==mSelectPoint){
                         pointTime = mPointArray.keyAt(i);
                         break;
                     }
                 }
                 mPlayer.seekVideo(pointTime);
                 mSeekBar.setProgress(mDuration == 0 ? 0 : pointTime * 100 / mDuration);
                 mClikJumpPoint=pointTime;
                 LogUtils.e("setCurrentPosition2",mClikJumpPoint+"");
             }
         }
         boolean canJump=false;
         if(CommonUtils.isPad(mSeekBar.getContext())){

             boolean isLandScape= mSeekBar.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
             canJump=isLandScape;
         }
         if ((!canJump)&&mOrientation== BJPlayerView.VIDEO_ORIENTATION_PORTRAIT) {
             if (mPlayer != null) {
                 mPlayer.switchOrientation();
                 mSeekBar.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         if(mOnShowInclassListener!=null)
                             mOnShowInclassListener.showInclassFragment(true,mSelectPoint.id,mPracticesId+"_"+mSelectPoint.userAnswer);

                     }
                 },600);
                 return;
             }
         }
         if(mOnShowInclassListener!=null)
             mOnShowInclassListener.showInclassFragment(true,mSelectPoint.id,mPracticesId+"_"+mSelectPoint.userAnswer);
     }

    private String mPracticesId;//答题卡id
    List<CourseWarePointBean> mCourseWareInfo;
    QuickListAction mLearnActons;
    PointSeekBar.Point mSelectPoint;

    private Runnable mPopdiss=new Runnable() {
        @Override
        public void run() {
            if(mLearnActons!=null&&mLearnActons.isShowing()){
                mLearnActons.dismiss();
            }
        }
    };
    private void showLeanPopWindow(View anchor,  PointSeekBar.Point point) {
        boolean isFirst=false;
        mSelectPoint=point;
        if (mLearnActons == null) {
            isFirst=true;
            mLearnActons = new QuickListAction(anchor.getContext(), R.layout.play_pop_learn_layout, R.id.root);
            mLearnActons.setAnimStyle(R.style.Animations_PopUpMenu_Center);

            ((TextView)mLearnActons.getRootView().findViewById(R.id.txt_content)).setText(point.title);
            mLearnActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    switch (position) {
                        case 0:
                            mLearnActons.dismiss();
                            if(null!=mSeekBar) {
                                mSeekBar.removeCallbacks(mPopdiss);
                            }
                            showInclassPoint(true);
                            break;
                    }
                }
            });

        }
       ((TextView)mLearnActons.getRootView().findViewById(R.id.txt_content)).setText(point.title);
        mLearnActons.popupAtPoint(anchor,(int)(point.X*(mSeekBar.getAvailableWidth())+mSeekBar.getPaddingLeft()+mSeekBar.getAdjustDistance()),DensityUtils.dp2px(anchor.getContext(),10),isFirst);

        if(null!=mSeekBar){
            mSeekBar.removeCallbacks(mPopdiss);
            mSeekBar.postDelayed(mPopdiss,6000);
        }


    }

}