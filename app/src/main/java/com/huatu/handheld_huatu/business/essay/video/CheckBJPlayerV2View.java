package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ContentFrameLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

import com.baijiahulian.player.mediaplayer.IMediaPlayer;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.examfragment.OnSwitchListener;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;

/**
 * Created by cjx on 2019\7\11 0011.
 */


public class CheckBJPlayerV2View extends CheckBJPlayerView {
    /**
     * 控制View的状态
     */
    private int mControViewVisible = GONE;
    /**
     * 播放器布局参数
     **/
    private ViewGroup.LayoutParams mLayoutParams;
    /**
     * 在父容器中的索引
     **/
    private int mIndexInParent;
    /**
     * 播放器父容器
     **/
    private ViewGroup mParent;
    /**
     * 全屏容器ID
     **/
    private int fullScreenContainerID = NO_ID;

    private Drawable mBgTransDrawable, mCoverBgDrawable;

    private boolean mAutoScrollBottom = true;

    public void disableAutoScrollBottom() {
        mAutoScrollBottom = false;
    }

    public interface onStartPlayListener {
        void onVideoPlayClick();
    }

    public void setOnStartPlayListener(onStartPlayListener startPlayListener) {
        if (null != mBJBottomViewController) {
            mBJBottomViewController.setOnStartPlayListener(startPlayListener);
        }
    }

    public CheckBJPlayerV2View(Context context) {
        this(context, null);
    }

    public CheckBJPlayerV2View(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CheckBJPlayerV2View(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mBgTransDrawable = new ColorDrawable(Color.TRANSPARENT);
        mCoverBgDrawable = ResourceUtils.getDrawable(R.mipmap.check_video_cover);
    }

    private BJCheckVideoControlV2BottomView mBJBottomViewController;


    //ImageView mCoverImage;
    @Override
    public void onPrepared(IMediaPlayer var1) {
        super.onPrepared(var1);

        this.getCenterView().setBackground(mBgTransDrawable);
        this.getBottomView().setVisibility(VISIBLE);
    }

    public void rePlayVideo() {
        if (null != mCoverBgDrawable) {
            this.getCenterView().setBackground(mCoverBgDrawable);
        }
        this.getBottomView().setVisibility(GONE);
        super.playVideo();
    }

    public void rePlayVideo(int var1) {
        if (null != mCoverBgDrawable) {
            this.getCenterView().setBackground(mCoverBgDrawable);
        }
        this.getBottomView().setVisibility(GONE);
        super.playVideo(var1);
    }

    @Override
    protected void initController() {
        // 以下三个方法分别设置底部、顶部和中部界面
        mBJTopController = new BJCheckVideoControlTopView(this.getTopView(), this);
        this.setTopPresenter(mBJTopController);

        mBJCenterController = new BJCheckVideoControlCenterView(this.getCenterView(), this);
        // this.getCenterView().setBackground(ResourceUtils.getDrawable(R.mipmap.check_video_cover));
     /*   mCoverImage=new ImageView(getContext());
        mCoverImage.setClickable(true);
        mCoverImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mCoverImage.setBackground(ResourceUtils.getDrawable(R.mipmap.check_video_cover));
        ((ViewGroup)this.getCenterView()).addView(mCoverImage,0,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
       */

        this.setCenterPresenter(mBJCenterController);

        mBJBottomViewController = new BJCheckVideoControlV2BottomView(this.getBottomView(), this);
        this.getBottomView().setVisibility(GONE);
        this.setBottomPresenter(mBJBottomViewController);
        OnSwitchListener onSwitchListener = new OnSwitchListener() {
            @Override
            public void onSwitch() {
                if (!isFullScreen) {
                    startWindowFullscreen();
                    if (CommonUtils.isPad(getContext())) {

                        Configuration mConfiguration = getContext().getResources().getConfiguration();       // 获取设置的配置信息
                        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            setPlayerControlConfig(Configuration.ORIENTATION_LANDSCAPE);
                        }
                    }
                }
            }

            @Override
            public void onBackPressed() {
                if (isFullScreen) {
                    quitWindowFullscreen();

                    if (CommonUtils.isPad(getContext())) {
                        Configuration mConfiguration = getContext().getResources().getConfiguration();       // 获取设置的配置信息
                        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            setPlayerControlConfig(Configuration.ORIENTATION_PORTRAIT);
                        }
                    }
                }
                //switchOrientation(true);
            }
        };

        mBJTopController.setOnSwitchListener(onSwitchListener);
        mBJBottomViewController.setOnSwitchListener(onSwitchListener);
        initSpeedShowListener();
        this.setVideoEdgePaddingColor(getResources().getColor(android.R.color.black));
        this.enableVolumeGesture(false);
        this.enableBrightnessGesture(false);
        this.enableSeekGesture(false);
        this.setEnableNetWatcher(false);

    }

    @Override
    public void setPlayerControlConfig(int orientation) {
        mBJTopController.setOrientation(orientation);
        mBJCenterController.setOrientation(orientation);
        mBJBottomViewController.setOrientation(orientation);
    }


    protected ViewGroup getFullscreenContainer() {
        return null;
    }

    public void startWindowFullscreen() {
        startWindowFullscreen(true);
    }

    /**
     * 进入全屏状态
     */
    public void startWindowFullscreen(boolean switchScreen) {
        isFullScreen = true;
        mControViewVisible = GONE;
        //StatusBarUtil.hideStatusBar((Activity) getContext());

        ViewGroup fullScreenContainer;
        if (getFullscreenContainer() != null) {
            fullScreenContainer = getFullscreenContainer();
        } else {
            fullScreenContainer = new FrameLayout(getContext());
        }

        //记录信息
        mParent = ((ViewGroup) getParent());
        mIndexInParent = mParent.indexOfChild(this);
        mLayoutParams = getLayoutParams();

        //将播放器从当前容器中移出
        mParent.removeView(this);
        /*设置可见属性为GONE，避免当前父布局为ConstraintLayout时removeView并没有移除
           mVariableDimensionsWidgets集合中对应DXVideoPlayer的ConstraintWidget，所导致onMeasure中出现的
           LayoutParams类型转换错误异常*/
        mParent.setVisibility(GONE);

        //将全屏播放器放到全屏容器之中
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        fullScreenContainer.addView(this, 0, layoutParams);

        //如果容器没有id，设置一个ID给容器
        fullScreenContainerID = fullScreenContainer.getId();
        if (fullScreenContainerID == NO_ID) {
            fullScreenContainerID = R.id.fullscreen_container_id;
            fullScreenContainer.setId(fullScreenContainerID);
        }

        //将全屏容器添加到contentView中
        ViewGroup contentView = getContentView();
        FrameLayout.LayoutParams contentViewLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
        contentView.addView(fullScreenContainer, contentViewLp);
        this.enableVolumeGesture(true);
        this.enableBrightnessGesture(true);
        this.enableSeekGesture(true);

        /*if(CommonUtils.isPad(getContext())){
            Activity var1=CommonUtils.getActivityFromView(this);
            if(var1 instanceof Activity) {
                Activity var2 = (Activity)var1;
                if(this.mVideoOrientation == 0) {
                    var2.getWindow().addFlags(1024);
                    var2.setRequestedOrientation(0);
                } else {
                    var2.getWindow().clearFlags(1024);
                    var2.setRequestedOrientation(1);
                }
             }

        }else*/
        {
            if (switchScreen) {
                this.switchOrientation();
            }

        }

        // ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        /****/
//        FrameLayout frameLayout = (FrameLayout)getParent();
//        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //根据视频宽高大小判断是否旋转
     /*   if (textureWidth > textureHeight) {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
    }

    /**
     * 退出全屏状态
     */
    public void quitWindowFullscreen() {
        isFullScreen = false;
        mControViewVisible = GONE;
        //  StatusBarUtil.hideStatusBar((Activity) getContext());

        //获取全屏容器
        ViewGroup fullScreenContainer = (ViewGroup) getContentView().findViewById(fullScreenContainerID);

        //移除全屏容器
        if (fullScreenContainer != null) {
            fullScreenContainer.removeView(this);
            getContentView().removeView(fullScreenContainer);
        }

        //将播放器添加到原来的容器中
        mParent.addView(this, mIndexInParent, mLayoutParams);
        mParent.setVisibility(VISIBLE);

//        /****/
//        FrameLayout frameLayout = (FrameLayout)getParent();
//        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(getContext(), 200)));

        // 不支持手势
        this.enableVolumeGesture(false);
        this.enableBrightnessGesture(false);
        this.enableSeekGesture(false);
        //竖屏
        //((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.switchOrientation();
        if (mAutoScrollBottom) {
            ViewParent parent = mParent.getParent();
            while ((!(parent instanceof NestedScrollView))) {
                parent = parent.getParent();
                if (parent instanceof ContentFrameLayout) {
                    break;
                }
            }
            if (parent instanceof NestedScrollView) {
                LogUtils.e("NestedScrollView", "NestedScrollView");
                final NestedScrollView scrollView = (NestedScrollView) parent;
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 500);
            }
        }
    }

    private ViewGroup getContentView() {

        return (ViewGroup) (CommonUtils.getActivityFromView(this)).findViewById(Window.ID_ANDROID_CONTENT);
    }
}