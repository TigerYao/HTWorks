package com.huatu.handheld_huatu.business.ztk_vod;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Property;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.CenterViewStatus;
import com.baijiahulian.player.playerview.IPlayerCenterContact;
import com.baijiahulian.player.utils.Utils;
import com.baijiayun.glide.load.DataSource;
import com.baijiayun.glide.load.engine.GlideException;
import com.baijiayun.glide.request.RequestListener;
import com.baijiayun.glide.request.target.Target;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.SimpleAnimListener;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.BitmapUtil;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.photo.TransitionImageView;
import com.huatu.library.internal.ViewCompat;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanglei on 2016/11/7.
 */
public class BJCenterViewPresenterCopy implements IPlayerCenterContact.CenterView,BJCustomCenterView,View.OnClickListener {


    private static final int CENTER_PAGE_INIT = 0;
    private static final int CENTER_PAGE_FRAME = 1 << 0;
    private static final int CENTER_PAGE_RATE = 1 << 1;
    private static final int CENTER_PAGE_SEGMENTS = 1 << 2;


    public interface OnPlayRateInterceptListener{
        float  getCurrentPlayRate();

        void setCurrentPlayRate(float playRate);

        void switchPlayMode();
    }

    OnPlayRateInterceptListener mOnPlayRateListener;
    public void setOnPlayRateListener(OnPlayRateInterceptListener onPlayRateListener){
        this.mOnPlayRateListener=onPlayRateListener;
    }

    public interface OnOutClickListener{
        void  onViewClick(View v,int type);
    }
    OnOutClickListener mOnOutClickListener;
    public void setOutClickListener(OnOutClickListener OnOutClickListener){
        this.mOnOutClickListener=OnOutClickListener;
    }

    private CenterViewStatus centerViewStatus = CenterViewStatus.NONE;
   // private RecyclerView courseView;
    // private CourseAdapter courseAdapter;

    private RecyclerView rvDefinition;
    private DefinitionAdapter definitionAdapter;

    private int mCenterPageState = CENTER_PAGE_INIT;

    private QueryCopy $;
    private IPlayerCenterContact.IPlayer mPlayer;
    private CenterHandler mHandler;
    private boolean isDialogShowing = false;
    private List<VideoItem.DefinitionItem> definitionItemList;

    private boolean isRightMenuHidden = false;
    protected boolean showJudge = false;
    protected View centerView;
    //BJBottomViewImpl mBJBottomPresenter;
    private long mVideoSize=0;
    private TextView mPlayRateTxt;
    protected ImageView mJudgeImg;

    public void setVideoSize(long videoSize){
        mVideoSize=videoSize;
    }

    public void setRightMenuHidden(boolean rightMenuHidden) {
        isRightMenuHidden = rightMenuHidden;
    }

    public ImageView getCoverImageView(){
        return  (ImageView) $.id(R.id.cover_img).view();
    }

    public void setCoverImageShow(boolean needShow){
        LogUtils.e("setCoverImageShow","setCoverImageShow");
        if(needShow)
            $.id(R.id.cover_img).visible();
        else
            $.id(R.id.cover_img).gone();
    }


    ImageView mBgCoverView;
    ImageView mAudioModeBtn;

    private void showBlurImageBg(final String localCover){
        if(null!=mBgCoverView){
            ImageLoad.displayBlurTransImage(mBgCoverView.getContext(),R.drawable.trans_bg,localCover,mBgCoverView);
        }
    }

    public void loadCoverImgage(final String localCover){
       ImageLoad.displayWorkImageListener(getCoverImageView().getContext(), localCover, getCoverImageView(), R.drawable.trans_bg,
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        showBlurImageBg(localCover);
                        return false;
                    }
                });
    }

    public void hideCenterView(){
        try {
            if (mBgCoverView != null && mBgCoverView.getParent() != null)
                ((FrameLayout) centerView).removeView(mBgCoverView);
        }catch (Exception e){}
    }

    public void scaleCenterView(){
        scaleCenterView(false);
    }

    public void scaleCenterView(boolean hasPlayMode){
        final View coverImg= $.id(R.id.cover_img).view();
        if(mBgCoverView==null){
            mBgCoverView=new ImageView(centerView.getContext());
            mAudioModeBtn=new ImageView(centerView.getContext());
            if(centerView instanceof FrameLayout){
                ((FrameLayout)centerView).addView(mBgCoverView,0,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));

                if(hasPlayMode){
                    FrameLayout.LayoutParams modeImgParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                    modeImgParams.gravity= Gravity.BOTTOM;
                    int marginDistance=DensityUtils.dp2px(centerView.getContext(),10);
                    modeImgParams.setMargins(marginDistance,0,0,6*marginDistance);

                    ((FrameLayout)centerView).addView(mAudioModeBtn,modeImgParams);
                    ViewCompat.setBackground(mAudioModeBtn,ResourceUtils.getDrawable(R.drawable.play_mode_selector));

                    if(null!=mPlayRateTxt){
                       FrameLayout.LayoutParams playRateParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                        playRateParams.gravity= Gravity.BOTTOM;
                      //  int marginDistance2=DensityUtils.dp2px(centerView.getContext(),10);
                        playRateParams.setMargins(marginDistance,0,0,11*marginDistance);
                        mPlayRateTxt.setLayoutParams(playRateParams);
                    }

                    mAudioModeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOnPlayRateListener!=null){
                                v.setSelected(!v.isSelected());
                                ToastUtils.showEssayToast(v.isSelected()? "已切换循环播放":"已切换顺序播放");
                                mOnPlayRateListener.switchPlayMode();
                            }
                        }
                    });
                }
            }
            GradientDrawable gradientDrawable = new GradientDrawable();

            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TL_BR);
            gradientDrawable.setColors(new int[]{0xFF4A1F18, 0xFF864329});
            mBgCoverView.setBackground(gradientDrawable);
            mBgCoverView.setScaleType(ImageView.ScaleType.CENTER_CROP);

           if(null!=coverImg.getTag(R.id.reuse_cachetag)){
                 String url=coverImg.getTag(R.id.reuse_cachetag).toString();
                ImageLoad.displayBlurTransImage(coverImg.getContext(),R.drawable.trans_bg,url,mBgCoverView);
            }
         }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(coverImg,"scaleX",1f,0.5f);
        ObjectAnimator  objectAnimator2 = ObjectAnimator.ofFloat(coverImg,"scaleY",1f,0.6f);

        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(
                objectAnimator,
                objectAnimator2);
        animator.setDuration(300);
        animator.setStartDelay(1000);
        final int radius=DensityUtils.dp2px(coverImg.getContext(),15);
        animator.addListener(new SimpleAnimListener(){
             @Override
            public void onAnimationEnd(Animator animation) {
                 BitmapUtil.setClipViewCornerRadius(coverImg,radius);
            }

        });
        animator.start();
    }

    public void showActionPanel(ActionType actionType){
        if(actionType==ActionType.SHOWAUDIOMODE){
            if(null!=mAudioModeBtn){
                mAudioModeBtn.setSelected(!mAudioModeBtn.isSelected());
            }
            return;
        }

        mCenterPageState =actionType==ActionType.SHOWSPEED? CENTER_PAGE_RATE:CENTER_PAGE_FRAME;
        if(mCenterPageState==CENTER_PAGE_FRAME){
            if(ArrayUtils.isEmpty(definitionItemList)){
                return;
            }
            setPageView();
        }else
           setPageView();
    }

    public BJCenterViewPresenterCopy(final View centerView) {
        this.centerView = centerView;
        $ = QueryCopy.with(centerView);
        mPlayRateTxt=(TextView) centerView.findViewById(R.id.play_changeRate_txt);
        mPlayRateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 changeRateAction();
            }
        });
        mJudgeImg = centerView.findViewById(R.id.play_judge_img);
        mJudgeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickJudgeBtn();
            }
        });
        mHandler = new CenterHandler(this);

        initFunctions();
        definitionItemList = new ArrayList<>();
        definitionAdapter = new DefinitionAdapter(centerView.getContext());
        rvDefinition =  (RecyclerView)centerView.findViewById(R.id.rv_bjplayer_center_view_definition);
        rvDefinition.setLayoutManager(new LinearLayoutManager(centerView.getContext()));
        rvDefinition.setAdapter(definitionAdapter);
        definitionAdapter.setOnRvItemClickListener(new OnRvItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                mPlayer.setVideoDefinition(Utils.getVideoDefinitionFromString(definitionItemList.get(index).type));
                definitionAdapter.notifyDataSetChanged();
                onBackTouch();
            }
        });

    }

    @Override
    public void onBind(IPlayerCenterContact.IPlayer player) {
        mPlayer = player;
        setPageView();
    }

    @Override
    public boolean onBackTouch() {
        if (mCenterPageState > CENTER_PAGE_INIT) {
            mCenterPageState = CENTER_PAGE_INIT;
            setPageView();
            return true;
        }
        return false;
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT) {
            onHide();
             //$.id(R.id.bjplayer_center_video_functions_ll).gone();
            if(null!=mAudioModeBtn){
                mAudioModeBtn.setVisibility(View.VISIBLE);
            }
            if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.VISIBLE);
        }else {
            if(null!=mAudioModeBtn){
                mAudioModeBtn.setVisibility(View.GONE);
            }
            $.id(R.id.lock_screen_btn).visible();
            if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.GONE);
        }

    }

    //暂停灰色的蒙层
    public void showPauseStatus(boolean isPaused){
      /*  if(null!=centerView){
            centerView.setBackgroundColor(isPaused? Color.parseColor("#33000000"):Color.TRANSPARENT);
        }*/
    }

    private boolean mIsBottomDragEnd=true;
    @Override
    public void showProgressPercentSlide(int oldProgress,int curProgress,int duration,boolean isDragEnd) {
        if(isDragEnd){
            mIsBottomDragEnd=true;
            //dismissLoading();
            return;
        }
        mIsBottomDragEnd=false;
        centerView.setVisibility(View.VISIBLE);
        // $.id(R.id.bjplayer_center_video_progress_dialog_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();

        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).visible();
        // $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).gone();

        $.id(R.id.bjplayer_center_video_progress_dialog_buttons_ll).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();

        String durationText = Utils.formatDuration(duration);
        String positionText = Utils.formatDuration((int)((float)curProgress)*duration/100  , duration >= 3600);
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).text(String.format("%s/%s", positionText, durationText));

//        String title = delta > 0 ? ("+ " + Utils.formatDuration(delta)) : "" + Utils.formatDuration(delta);
        if (curProgress > oldProgress) {
            $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).enable(false);//.image(R.drawable.bjplayer_ic_kuaijin);
        } else {
            $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).enable(true);//.image(R.drawable.bjplayer_ic_huitui);
        }
        if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.GONE);
        centerViewStatus = CenterViewStatus.FUNCTION;
    }

    @Override
    public void showProgressSlide(int delta) {
        centerView.setVisibility(View.VISIBLE);
       // $.id(R.id.bjplayer_center_video_progress_dialog_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();

        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).visible();
       // $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).gone();

        $.id(R.id.bjplayer_center_video_progress_dialog_buttons_ll).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();

        String durationText = Utils.formatDuration(mPlayer.getDuration());
        String positionText = Utils.formatDuration(mPlayer.getCurrentPosition() + delta, mPlayer.getDuration() >= 3600);
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).text(String.format("%s/%s", positionText, durationText));

//        String title = delta > 0 ? ("+ " + Utils.formatDuration(delta)) : "" + Utils.formatDuration(delta);
        if (delta > 0) {
            $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).enable(false);//.image(R.drawable.bjplayer_ic_kuaijin);
        } else {
            $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).enable(true);//.image(R.drawable.bjplayer_ic_huitui);
        }
        if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.GONE);
        centerViewStatus = CenterViewStatus.FUNCTION;
    }

    @Override
    public void showLoading(String message) {
        centerView.setVisibility(View.VISIBLE);
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_loading_pb).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).text(message);
        $.id(R.id.bjplayer_center_video_progress_dialog_buttons_ll).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.GONE);
        isDialogShowing = true;
        centerViewStatus = CenterViewStatus.LOADING;
    }

    @Override
    public void dismissLoading() {
        if(!mIsBottomDragEnd) return;
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        isDialogShowing = false;

        if (mPlayer != null && mPlayer.getOrientation() == BJPlayerView.VIDEO_ORIENTATION_PORTRAIT){
            if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.VISIBLE);
        }
        centerViewStatus = CenterViewStatus.NONE;
    }

    @Override
    public void showVolumeSlide(int volume, int maxVolume) {
        centerView.setVisibility(View.VISIBLE);
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).visible();

        int value = volume * 100 / maxVolume;
        if (value == 0) {
            $.id(R.id.bjplayer_center_controller_volume_ic_iv).image(R.drawable.bjplayer_ic_volume_off_white);
            $.id(R.id.bjplayer_center_controller_volume_tv).text("off");
        } else {
            $.id(R.id.bjplayer_center_controller_volume_ic_iv).image(R.drawable.bjplayer_ic_volume_up_white);
            $.id(R.id.bjplayer_center_controller_volume_tv).text(value + "%");
        }
        mHandler.sendMsgDismissDialogDelay();
        centerViewStatus = CenterViewStatus.FUNCTION;
    }

    @Override
    public void showBrightnessSlide(int brightness) {
        centerView.setVisibility(View.VISIBLE);
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).visible();

        $.id(R.id.bjplayer_center_controller_volume_ic_iv).image(R.drawable.bjplayer_ic_brightness);
        $.id(R.id.bjplayer_center_controller_volume_tv).text(brightness + "%");
        mHandler.sendMsgDismissDialogDelay();
        centerViewStatus = CenterViewStatus.FUNCTION;
    }

    @Override
    public void showError(int what, int extra) {
        String[] errorTips = $.contentView().getContext().getResources().getStringArray(R.array.bjplayer_error_tips);
        int index = what - 1;
        String error;
        if (what >= 0 && what < errorTips.length) {
            error = errorTips[index];
        } else {
            error = $.contentView().getContext().getString(R.string.bjplayer_error_unknow);
        }
        if(what == 500){
            error = $.contentView().getContext().getString(R.string.bjplayer_network_error);
        }
        showError(what, error);
    }

    @Override
    public void showError(final int code, String message) {
        centerView.setVisibility(View.VISIBLE);
        onHide();
        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).text(message + "[" + code + "]\n");
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_buttons_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_button2_tv).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).text($.contentView().getContext().getString(R.string.bjplayer_video_reload));
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null!=mOnOutClickListener){
                     mOnOutClickListener.onViewClick(v,-1);
                     return;
                }
                dismissLoading();
                if(code == 500){
                    mPlayer.ijkInternalError();
                } else{
                    mPlayer.playVideo();
                }
            }
        });

        isDialogShowing = true;
        centerViewStatus = CenterViewStatus.ERROR;

    }

    @Override
    public void showWarning(String warn) {
        centerView.setVisibility(View.VISIBLE);
        onHide();

        $.id(R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        String tmpStr= ResourceUtils.getString(R.string.bjplayer_play_no_wifi);
        if(tmpStr.equals(warn)){
            $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).text(String.format("提示：即将消耗%s手机流量", CommonUtils.formatSpaceSize(mVideoSize)) + "");
        }
        else
            $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).text(warn + "\n");
        $.id(R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_buttons_ll).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).visible();
        $.id(R.id.bjplayer_center_video_progress_dialog_button2_tv).gone();
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).text($.contentView().getContext().getString(R.string.bjplayer_video_goon));
        $.id(R.id.bjplayer_center_video_progress_dialog_button1_tv).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(null!=mOnOutClickListener){
                    mOnOutClickListener.onViewClick(v,0);
                    return;
                }
                dismissLoading();
                mPlayer.setEnableNetWatcher(false);
                mPlayer.playVideo();
            }
        });
        isDialogShowing = true;
        centerViewStatus = CenterViewStatus.WARNING;
    }

    @Override
    public void onShow() {
        if (mPlayer != null && mPlayer.getOrientation() == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE ) {//&& !isRightMenuHidden
            $.id(R.id.lock_screen_btn).visible();
        } else {
            $.id(R.id.lock_screen_btn).gone();
            if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.VISIBLE);
        }
        if (null != mJudgeImg && showJudge)
            mJudgeImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHide() {
       // $.id(R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
        $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
        $.id(R.id.bjplayer_layout_center_video_functions_frame_ll).gone();

        $.id(R.id.lock_screen_btn).gone();
        if(isRightMenuHidden&&(null!=mPlayRateTxt))  mPlayRateTxt.setVisibility(View.GONE);
        //$.id(R.id.bjplayer_center_video_functions_ll).gone();
        mCenterPageState = CENTER_PAGE_INIT;
        if (showJudge&& mJudgeImg != null && mJudgeImg.getVisibility() == View.VISIBLE)
            mJudgeImg.setVisibility(View.GONE);
    }

    @Override
    public void onVideoInfoLoaded(VideoItem videoItem) {
        if (videoItem == null) {
            return;
        }
        LogUtils.e("definitionItemList", GsonUtil.GsonString(videoItem)+"");
        if (videoItem.definition != null) {
            definitionItemList = videoItem.definition;

            //LogUtils.e("definitionItemList", GsonUtil.GsonString(videoItem));
            definitionAdapter.notifyDataSetChanged();
        }
      /*  if (mPlayer.isPlayLocalVideo()) {
            $.id(R.id.bjplayer_center_video_functions_frame_tv).gone();
        } else {
            $.id(R.id.bjplayer_center_video_functions_frame_tv).visible();
        }*/
        updateDefinition();
    }

    @Override
    public boolean isDialogShowing() {
        return isDialogShowing;
    }

    private void setPageView() {
        switch (mCenterPageState) {
            case CENTER_PAGE_INIT:
                //$.id(R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
                $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
                $.id(R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
                if (mPlayer == null) {
                   // $.id(R.id.bjplayer_center_video_functions_ll).gone();
                }
                if (mPlayer.getOrientation() == BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE ) {
                   // setAnimationVisible(R.id.bjplayer_center_video_functions_ll);
                   // AnimUtils.animHorShow($.id(R.id.bjplayer_center_video_functions_ll).view(),true);
                    if(isRightMenuHidden&&(null!=mPlayRateTxt)) mPlayRateTxt.setVisibility(View.GONE);
                } else {
                   // $.id(R.id.bjplayer_center_video_functions_ll).gone();
                    if(isRightMenuHidden&&(null!=mPlayRateTxt)) mPlayRateTxt.setVisibility(View.VISIBLE);
                }
                mPlayer.showTopAndBottom();
                break;
            case CENTER_PAGE_FRAME://分辨率
               // $.id(R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
                $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
               // setAnimationVisible(R.id.bjplayer_layout_center_video_functions_frame_ll);
                AnimUtils.animHorShow($.id(R.id.bjplayer_layout_center_video_functions_frame_ll).view(),true);
              //  $.id(R.id.bjplayer_center_video_functions_ll).gone();
//                setFocusDefinition();
                if(isRightMenuHidden&&(null!=mPlayRateTxt)) mPlayRateTxt.setVisibility(View.GONE);
                mPlayer.hideTopAndBottom();
                break;
            case CENTER_PAGE_RATE://倍速
              //  $.id(R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
               // setAnimationVisible(R.id.bjplayer_layout_center_video_functions_rate_ll);
                AnimUtils.animHorShow($.id(R.id.bjplayer_layout_center_video_functions_rate_ll).view(),true);
                $.id(R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
               // $.id(R.id.bjplayer_center_video_functions_ll).gone();
                if(isRightMenuHidden&&(null!=mPlayRateTxt)) mPlayRateTxt.setVisibility(View.GONE);
                setFocusRate();
                mPlayer.hideTopAndBottom();
                break;
            case CENTER_PAGE_SEGMENTS://课件列表，已移除
               // setAnimationVisible(R.id.bjplayer_layout_center_video_functions_segments_ll);
                $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
                $.id(R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
               // $.id(R.id.bjplayer_center_video_functions_ll).gone();
                if(isRightMenuHidden&&(null!=mPlayRateTxt)) mPlayRateTxt.setVisibility(View.GONE);
                mPlayer.hideTopAndBottom();
                break;
        }
        updateDefinition();
    }

    /**
     * 更新清晰度，读VideoItem的definition字段,默认是第一个，默认的逻辑在player presenter处理了
     */
    @Override
    public void updateDefinition() {
        if (mPlayer != null) {
            String desTxt="省流";
            switch (mPlayer.getVideoDefinition()) {
                case BJPlayerView.VIDEO_DEFINITION_1080p:
                     desTxt="超高清";
                    break;
                case BJPlayerView.VIDEO_DEFINITION_720p:
                     desTxt="超清";
                    break;
                case BJPlayerView.VIDEO_DEFINITION_SUPER:
                     desTxt="高清";
                    break;
                case BJPlayerView.VIDEO_DEFINITION_HIGH:
                     desTxt="标清";
                    break;
                case BJPlayerView.VIDEO_DEFINITION_STD:
                default:
                    desTxt="省流";
                    break;
            }
            setActionText(ActionType.SHOWDEFINITION,desTxt);
        }
    }

    @Override
    public CenterViewStatus getStatus() {
        return centerViewStatus;
    }

    public void resetPlaySpeed(){
        if(null!=mOnPlayRateListener){    //保持最后的倍速
          /*  setFocusRate();
            if(isRightMenuHidden&&(null!=mPlayRateTxt))
                mPlayRateTxt.setText("1.0x");
            setActionText(ActionType.SHOWSPEED,"1x");*/
            return;
       }
       if(mPlayer.getVideoRateInFloat()!=1) {
           mPlayer.setVideoRate(1f);
           setFocusRate();
           if(isRightMenuHidden&&(null!=mPlayRateTxt))
               mPlayRateTxt.setText("1.0x");
           setActionText(ActionType.SHOWSPEED,"1x");
       }
    }

    //与bottomAction联动
    public void setActionText(BJCustomCenterView.ActionType actionType,String value){  }

    public void changeRateAction(){  }

    public void onClickJudgeBtn(){}

    //右侧倍速切换
    @Override
    public  void onClick(View v){
        if(v.getTag()==null||TextUtils.isEmpty(v.getTag().toString())) return;
        float tmpSpeed= StringUtils.parseFloat(v.getTag().toString());

        if(null!=mOnPlayRateListener){
            mOnPlayRateListener.setCurrentPlayRate(tmpSpeed);
        }else {
            mPlayer.setVideoRate(tmpSpeed);
        }
        if(isRightMenuHidden&&(null!=mPlayRateTxt))
            mPlayRateTxt.setText(v.getTag().toString()+"x");
        setActionText(ActionType.SHOWSPEED,v.getTag().toString()+"x");
        onBackTouch();
    }

    //外部popwindow选中，回调至内部
    public  void changeRate(float rateSpeed){
        if(null!=mPlayer){
             mPlayer.setVideoRate(rateSpeed);
        }
        if(isRightMenuHidden&&(null!=mPlayRateTxt))
            mPlayRateTxt.setText(rateSpeed+"x");
        setActionText(ActionType.SHOWSPEED,rateSpeed+"x");
        onBackTouch();
    }

    private void initFunctions() {
        //set functions clicked
        ViewGroup viewGroup=(ViewGroup) $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).view();
        for(int i=0;i<viewGroup.getChildCount();i++){
            viewGroup.getChildAt(i).setOnClickListener(this);
        }

    }

    float[] rateArr=new float[]{0.5f,0.75f,1f,1.25f,1.5f,1.75f,2f};
    private void setFocusRate() {
        float curRate=1f;
        if(null!=mOnPlayRateListener){
            curRate= mOnPlayRateListener.getCurrentPlayRate();
        } else {
            curRate= mPlayer.getVideoRateInFloat();
        }
        LogUtils.e("setFocusRate",curRate+"");
        ViewGroup viewGroup=(ViewGroup) $.id(R.id.bjplayer_layout_center_video_functions_rate_ll).view();
        for(int i=0;i<viewGroup.getChildCount();i++){
             viewGroup.getChildAt(i).setSelected(rateArr[i]==curRate);
        }
     }

    private static class CenterHandler extends Handler {
        private WeakReference<BJCenterViewPresenterCopy> presenter;

        private CenterHandler(BJCenterViewPresenterCopy presenter) {
            this.presenter = new WeakReference<BJCenterViewPresenterCopy>(presenter);
        }

        private static final int WHAT_DISMISS_DIALOG = 0;

        private void sendMsgDismissDialogDelay() {
            removeMessages(WHAT_DISMISS_DIALOG);
            Message msg = obtainMessage(WHAT_DISMISS_DIALOG);
            sendMessageDelayed(msg, 2000);
        }

        @Override
        public void handleMessage(Message msg) {
            if (presenter.get() == null) return;
            switch (msg.what) {
                case WHAT_DISMISS_DIALOG:
                    presenter.get().dismissLoading();
                    break;

            }
        }
    }

    interface OnRvItemClickListener {
        void onItemClick(View view, int index);
    }

    class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.DefinitionViewHolder> implements View.OnClickListener {
        Context context;
        OnRvItemClickListener onRvItemClickListener = null;
        int mSelectIndex=-1;
        DefinitionAdapter(Context context) {
            this.context = context;
        }

        public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
            this.onRvItemClickListener = onRvItemClickListener;
        }

        @Override
        public DefinitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View definitionView = LayoutInflater.from(context).inflate(R.layout.player_definition_list_item, parent, false);
            definitionView.setOnClickListener(this);
            return new DefinitionViewHolder(definitionView);
        }

        @Override
        public void onBindViewHolder(DefinitionViewHolder holder, int position) {
            holder.itemView.setTag(position);
            String defName = definitionItemList.get(position).name;
            String defType = definitionItemList.get(position).type;

            String defNameDes=defName;
            String defTypeDes=defType;
            if(defType.equals("low")){
                defNameDes="省流";
                defTypeDes="180p";
            }
            else if(defType.equals("high")){
                defNameDes="标清";
                defTypeDes="360p";
            }
            else if(defType.equals("superHD")){
                defNameDes="高清";
                defTypeDes="540p";
            }
            else if(defType.equals("superHD")){
                defNameDes="高清";
                defTypeDes="540p";
            }
            else if(defType.equals("720p")){
                defNameDes="超清";
                defTypeDes="720p";
            }else if(defType.equals("1080p")) {
                defNameDes = "超高清";
                defTypeDes = "1080p";
            }
            holder.tvType.setText(defNameDes);
            holder.tvDefinition.setText(defTypeDes);

            if (Utils.getVideoDefinitionFromInt(mPlayer.getVideoDefinition()).equals(defType)) {
                 holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);
            }
        }

        @Override
        public int getItemCount() {
            return definitionItemList.size();
        }

        @Override
        public void onClick(View v) {
            if (onRvItemClickListener != null) {
                if (v.getTag() != null) {
                    int position = (int) v.getTag();
                    onRvItemClickListener.onItemClick(v, position);
                }
            }
        }

        class DefinitionViewHolder extends RecyclerView.ViewHolder {
            View itemView;
            TextView tvDefinition,tvType;


            public DefinitionViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvDefinition = (TextView) itemView.findViewById(R.id.tv_bjplayer_item_center_definition);
                tvType =(TextView)  itemView.findViewById(R.id.definition_type_txt);
            }
        }
    }


}
