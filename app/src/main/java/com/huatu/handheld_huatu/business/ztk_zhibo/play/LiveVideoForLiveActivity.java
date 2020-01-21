package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;

import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.ztk_vod.PopCollectListAction;
import com.huatu.handheld_huatu.business.ztk_vod.PopRightActionDialog;
import com.huatu.handheld_huatu.business.ztk_vod.ShareDialogFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.LiveChatExpressBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.AudioStreamReciverHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.ScreenOrientationHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.GestureState;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveTestView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoBottomView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoCenterView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoDragView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoHeaderView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoView;
import com.huatu.handheld_huatu.business.ztk_zhibo.view.LiveChatEditText;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;

import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.LiveLoadingLayout;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;

import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.InputMethodUtils;
import com.huatu.utils.StringUtils;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

public class LiveVideoForLiveActivity extends SimpleBaseActivity implements OnClickListener, GridViewAvatarAdapter.SelectAvatarInterface,
        VideoPlayer.OnVideoPlayListener, VideoPlayer.BaseView {
    private final int PLAY_UNSTART = 0;
    private final int PLAY_PLAYING = 1;
    private final int PLAY_PAUSE = 2;  //对应onResume与onPause
    private final int PLAY_COMPLETED = 3;
    private final int PLAY_STOP = 4;//对应播放器手动停止
    //    private final int PLAY_LOADING = 5;
    @BindView(R.id.edittext_comment_content)
    LiveChatEditText mChatEditText;
    @BindView(R.id.imageview_expression)
    ImageView mImagePeople;
    @BindView(R.id.imageview_add)
    TextView sendbutton;
    @BindView(R.id.live_video_play_container)
    FrameLayout layoutPptView;
    @BindView(R.id.live_video_view_father_layout)
    FrameLayout layoutVideoFatherView;
    @BindView(R.id.video_play_loading_pb_layout)
    LiveLoadingLayout mLayoutLoading;
    @BindView(R.id.live_title_layout)
    LiveVideoHeaderView layoutTitle;
    @BindView(R.id.live_video_play_operation_layout)
    LiveVideoBottomView mVideoBottomLayout;
    @BindView(R.id.layout_live_video_center)
    LiveVideoCenterView mVideoCenterLayout;
    @BindView(R.id.live_video_play_parent)
    LiveVideoView mVideoView;
    @BindView(R.id.video_test_layout)
    LiveTestView mVideoTestView;
    @BindView(R.id.float_video_view)
    LiveVideoDragView mFloatVideoView; //浮动视频
    @BindView(R.id.open_min_video)
    View openMinView;
    @BindView(R.id.close_min_video)
    View closeMinView;

    private FrameLayout mFloatIconLayout;
    private TextView mLimitTips;
    private ImageView mImageCover;
    private LinearLayout ll_send_message;
    private ImageView mCenterPlayBtn;
    private GridView mGridView;
    private FrameLayout viewpager_live_video;
    private View interactiveView;
    private int mMaxChars = 25;
    private boolean isPortrait = true;
    private int mInitPlayIndex = -1;
    private boolean isLiveVideo = false;
    private int playerType = 0;
    private int totalTime = 0;
    private int currentTime = 0;
    private int lastSaveTime = 0;  //用来判断保存的间隔seekto
    private int playingFlag = 0;//0 为开始，1，播放，2 ，暂停
    private boolean isFromOffLine = false;
    private boolean hasVideo = false;
    private String mImgCoverPath;
    private int lastX;
    private int lastY;
    private long mPlayOnlineTime = 0;//播放在线时长
    private boolean isMove;
    private boolean mJudged = false;
    private GestureDetector mDetector;//单击双击手势处理
    private String videoInfoStr;
    private CourseWareInfo mLession;
    private GridViewAvatarAdapter mGridViewAvatarAdapter;
    private String courseId;
    private SharedPreferences sharedPreferences;
    private VideoPlayer mVideoPlayer;
    private OnliveChatPresenter mChatMsgPresenter;
    private PowerManager.WakeLock m_wakeLock;
    private OnVodPlayViewTouchListener onPlayViewTouchListener;
    private AudioStreamReciverHelper mAudioHelper;
    private VideoStatisticsUtil mStatisticsUtil;
    private ScreenOrientationHelper mScreenOrientationHelper;
    private boolean mIsRequestLand;//强制平衡
    private int curseekTime;//拖动时间
    private GestureState mState;//手势操作状态
    private boolean canScroll = true;//是否可操作
    private BgPlayReceiver bgPlayReceiver; //后台播放
    private boolean mIsActivityFront = false;    //判断页面在前台
    private boolean hasCurrentVideoInit = false;//播放出错,sdk内部会自动切换cdn,重新走一次播放逻辑。
    private CourseApiService.BooleanResult mJudegeResult; //是否已评价

    private PopRightActionDialog mActionDialog;

    private PopCollectListAction msgActons;

    //隐藏动画
    private Runnable titleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasVideo)
                return;
            if (!isPortrait && mVideoCenterLayout.isShowLock() && mVideoCenterLayout.mIsLock) {
                mVideoCenterLayout.switchLock();
                return;
            }
            if ((msgActons != null && msgActons.isShowing())) {
                return;
            }
            if (playingFlag != PLAY_UNSTART)
                AnimUtils.animTopShow(layoutTitle, false);
            mVideoCenterLayout.showCenter(false, !isPortrait);
            if (!isLiveVideo || isPortrait) {
                AnimUtils.animBottomHide(mVideoBottomLayout, true);
            }
        }
    };

    public static void startForResult(Activity context, String courseId, int index, boolean isFromOffLine, CourseWareInfo lessionInfo, String imgPath) {
        Intent intent = new Intent(context, LiveVideoForLiveActivity.class);
        intent.putExtra("course_id", courseId);
        intent.putExtra("play_index", index);
        intent.putExtra("from_off_line", isFromOffLine);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra(ArgConstant.BEAN, lessionInfo);
        intent.putExtra("forLandScape", context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        context.startActivityForResult(intent, lessionInfo.videoType == 3 ? 0x00101 : 0x00102);
    }

    public static void startForLandScapeResult(Activity context, String courseId, int index, boolean isFromOffLine, CourseWareInfo lessionInfo, String imgPath, boolean isLand) {
        Intent intent = new Intent(context, LiveVideoForLiveActivity.class);
        intent.putExtra("course_id", courseId);
        intent.putExtra("play_index", index);
        intent.putExtra("from_off_line", isFromOffLine);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra(ArgConstant.BEAN, lessionInfo);
        intent.putExtra("forLandScape", isLand);
        context.startActivityForResult(intent, lessionInfo.videoType == 3 ? 0x00101 : 0x00102);
    }

    public static void start(Context context, String courseId, CourseWareInfo lessionInfo, String imgPath) {
        Intent intent = new Intent(context, LiveVideoForLiveActivity.class);
        intent.putExtra("course_id", courseId);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra(ArgConstant.BEAN, lessionInfo);
        context.startActivity(intent);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_livecopy_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String FRAGMENT_TAGS = "android:support:fragments";
            savedInstanceState.remove(FRAGMENT_TAGS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0+ 打开硬件加速
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        //修复某些机器上surfaceView导致的闪黑屏的bug
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioMethod();
        if (!isLiveVideo)
            register();
    }

    @Override
    protected void onRegisterReceiver() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBlueTooth(BlueToothKeyEvent event) {
        if ((mLession != null) && !isLiveVideo
                && mLayoutLoading.getVisibility() != View.VISIBLE) {
            onClickStartBtn();
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "online:app");
        initData();
        initViews();
        initVideoSensor();
        setViewsState();
        playerType = 1;
        initListener();
        initPlayerAndStart(mInitPlayIndex);
        if (mLession != null) {
            onTeacherNameChange(mLession.teacher);
            mVideoCenterLayout.timeFromBegin(mLession.liveStartTime);
        }
        if (!CommonUtils.isPad(this))
            mScreenOrientationHelper = new ScreenOrientationHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityFront = true;
        setSendLayoutState(isPortrait && isLiveVideo);
        m_wakeLock.acquire();
        if (mScreenOrientationHelper != null)
            mScreenOrientationHelper.postOnStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChatEditText.clearFocus();
        InputMethodUtils.hideMethod(this, mChatEditText);
        setSendLayoutState(isLiveVideo);
        mIsActivityFront = false;
        m_wakeLock.release();
        if (!isLiveVideo)
            LiveVideoNotificationService.notifyAction(this, mLession.title, mImgCoverPath, playingFlag == PLAY_PLAYING ? LiveVideoNotificationService.PLAY_MUSIC : LiveVideoNotificationService.PAUSE_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScreenOrientationHelper != null)
            mScreenOrientationHelper.postOnStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = ll_send_message;
            if (isShouldHideInput(v, ev)) {
                if (isLiveVideo && isPortrait) {
                    Method.hideKeyboard(getCurrentFocus());
                    mGridView.setVisibility(View.GONE);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (mVideoCenterLayout != null && mVideoPlayer != null) && mVideoCenterLayout.onKeyDown(keyCode, event) ? true : super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000 && resultCode == RESULT_OK) {
            mJudegeResult.result = true;
            mVideoCenterLayout.showJudege(false);
            if (mStatisticsUtil != null)
                mStatisticsUtil.onJudege(currentTime / 1000, data.getIntExtra("level", 0));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!isLiveVideo && LiveVideoNotificationService.ACTION.equals(intent.getAction()))
            return;
        initData();
        if (null != mChatMsgPresenter) mChatMsgPresenter.clearMessage();
        if (mVideoPlayer != null) {
            mVideoPlayer.releasePlayer();
            mVideoPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putString("video_info_str", videoInfoStr);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            videoInfoStr = savedInstanceState.getString("video_info_str");
            mInitPlayIndex = savedInstanceState.getInt("play_index", -1);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isPortrait = false;
            mChatMsgPresenter.setIsPortrait(isPortrait);
            layoutTitle.setFullScreen(true);
            mVideoCenterLayout.openLock(false);
            mVideoCenterLayout.showCenter(true, !isPortrait);
            setSendLayoutState(false);
            mVideoBottomLayout.setVisibility(isLiveVideo || !hasVideo ? View.GONE : View.VISIBLE);
            if (isLiveVideo) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoTestView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, 0);
            }
            if(!isLiveVideo && mVideoBottomLayout.getFloatViewLayout() != null)
                mVideoCenterLayout.setFloatView(mVideoBottomLayout.getFloatViewLayout());
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isPortrait = true;
            mChatMsgPresenter.setIsPortrait(isPortrait);
            layoutTitle.setFullScreen(false);
            mVideoCenterLayout.showCenter(true, !isPortrait);
            setSendLayoutState(true);
            mVideoView.closeFull();
            mVideoCenterLayout.setIsExpandChat(false);
            mVideoCenterLayout.setIsExpandVideo(false);
            layoutTitle.setVisibility(View.VISIBLE);
            mVideoBottomLayout.setVisibility(!hasVideo ? View.GONE : View.VISIBLE);
            onClickLockScreenBtn(true);
            if (isLiveVideo) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoTestView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, mVideoCenterLayout.getId());
            }
            // mOnChatAdapter.setIsPortrait(isPortrait);
        }
        if (mVideoPlayer != null) {
            mVideoPlayer.setPortraitMode(isPortrait);
        }
        if (mActionDialog != null && mActionDialog.isShowing())
            mActionDialog.dismiss();
        if (msgActons != null && msgActons.isShowing())
            msgActons.dismiss();
        mVideoCenterLayout.showPP(0);
        mGridView.setVisibility(View.GONE);
        setViewsState();
        mChatMsgPresenter.showOrientationConfig(isPortrait);
        UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
        if (mStatisticsUtil != null)
            mStatisticsUtil.updateViewMode(!isPortrait, mVideoCenterLayout.isVideoExpand(), mVideoCenterLayout.isChatExpand());
        closeMinFloatView(true);
    }

    @Override
    public void finish() {
        if (isLiveVideo) {
            InputMethodUtils.hideMethod(this, mChatEditText);
            mGridView.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.putExtra(ArgConstant.BEAN, mLession);
            setResult(RESULT_OK, intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra(ArgConstant.BEAN, mLession);
            intent.putExtra(ArgConstant.KEY_ID, mLession.coursewareId);
            intent.putExtra(ArgConstant.LOCAL_PATH, playingFlag == PLAY_COMPLETED ? totalTime : currentTime);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (!isLiveVideo)
            LiveVideoNotificationService.notifyAction(LiveVideoForLiveActivity.this, mLession.title, mImgCoverPath, LiveVideoNotificationService.CANCEL_NOTIFY);
        if (null != mChatMsgPresenter) mChatMsgPresenter.onDestory();
        releasePlayer();
        mVideoCenterLayout.unregisterVolumeChangeReceiver(this);
        if (isLiveVideo)
            mVideoTestView.closePage();
        //取消注册的方法
        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
        UniApplicationLike.getApplicationHandler().removeCallbacksAndMessages(null);
        super.onDestroy();
        UMShareAPI.get(this).release();
        ToastUtils.cancle();
        mAudioHelper.unRegister();
        if (bgPlayReceiver != null)
            unregisterReceiver(bgPlayReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_video_pause_btn:
                onClickStartBtn();
                showControlView(true);
                break;
            case R.id.video_lock_btn:
                onClickLockScreenBtn(!mVideoCenterLayout.mIsLock);
                break;
            //点击助教模式
            case R.id.video_play_interaction_btn:
                onClickInteraction();
                mStatisticsUtil.updateViewMode(!isPortrait, mVideoCenterLayout.isVideoExpand(), mVideoCenterLayout.isChatExpand());
                mStatisticsUtil.onVideoOperate(currentTime / 1000, "助教笔记");
                break;
            case R.id.live_title_speed_btn:
                //onClickTitleSpeed();
                if (playingFlag != PLAY_UNSTART)
                    mVideoCenterLayout.showPP(2);
                break;

            case R.id.live_video_full_screen_btn:
                onClickFullScreen();
                break;
            case R.id.live_title_more_btn:
                showTitlePopWindow(v);
                break;
            case R.id.imageview_add:
                String chatText = mChatEditText.getChatText();
                String richText = mChatEditText.getRichText();
                if (mVideoPlayer != null) {
                    mVideoPlayer.sendChatMsg(chatText, richText, false);
                }
                mChatEditText.setText("");
                InputMethodUtils.hideMethod(this, mChatEditText);
                if (!isPortrait)
                    setSendLayoutState(false);
                break;
            case R.id.imageview_expression:
                handleEmojiBtnClick();
                mGridViewAvatarAdapter.setPlayerType(playerType);
                break;
            case R.id.stick_back_btn:
            case R.id.image_live_back:
                onBackPressed();
                break;
            case R.id.rl_start_play_live:
                onClickStartBtn();
                showControlView(true);
                break;
            //点击展开老师视频按钮
            case R.id.live_video_view_close_iv:
                // if (mVideoPlayer.isPlaying())
                if (!mVideoCenterLayout.isVideoExpand()) {
                    closeMinFloatView(false);
                    ((BaiJiaVideoPlayerImpl) getPlayer()).setVideoContainer((FrameLayout) layoutVideoFatherView.findViewById(R.id.video_container));
                }
                onClickExpandVideo();
                mStatisticsUtil.updateViewMode(!isPortrait, mVideoCenterLayout.isVideoExpand(), mVideoCenterLayout.isChatExpand());
                mStatisticsUtil.onVideoOperate(currentTime / 1000, "分屏");
                break;
            case R.id.open_add_chat_img:
                // if (mVideoPlayer.isPlaying())
                onClickAddChat();
                break;
            case R.id.live_bottom_quality:
                if (playingFlag != PLAY_UNSTART)
                    mVideoCenterLayout.showPP(1);
                break;
            case R.id.play_judege_land:
            case R.id.play_judege_txt:
                onJudege();
                break;
            //打开浮动视频
            case R.id.open_min_video:
            case R.id.open_live_video:
                openTeacherMinVideo();
                break;
            case R.id.close_min_video:
                closeMinFloatView(true);
                break;
        }
    }

    @Override
    protected void onLoadData() {
        if ((mLession == null)) return;
        if (!NetUtil.isConnected()) return;
        if (isLocalVideo()) {
            if (mVideoCenterLayout != null)
                mVideoCenterLayout.showJudege(!isLocalVideo());
            return;
        }

        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getCourseLearnPercent(courseId), new NetObjResponse<CourseInfoBean>() {
            @Override
            public void onError(String message, int type) {
            }

            @Override
            public void onSuccess(BaseResponseModel<CourseInfoBean> model) {
                CourseInfoBean courseInfo = model.data;
                mImgCoverPath = courseInfo.scaleimg;
                CourseApiService.getGoldReWard(getSubscription(), courseInfo.isFree);
            }
        });

        final String rid = String.valueOf(mLession.coursewareId);
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().checkLessionEvalute(rid, mLession.id),
                new NetObjResponse<CourseApiService.BooleanResult>() {

                    @Override
                    public void onError(String message, int type) {
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<CourseApiService.BooleanResult> model) {
                        mJudegeResult = model.data;
                        if (mVideoCenterLayout != null)
                            mVideoCenterLayout.showJudege(!mJudegeResult.result && !isLocalVideo());
                    }
                });
    }

    @Override
    public void onTeacherNameChange(String teacherName) {
        if (mVideoView != null)
            mVideoView.setTeacherName(teacherName);
    }

    @Override
    public VideoPlayer getPlayer() {
        return mVideoPlayer;
    }

    @Override
    public boolean getActivityRunningFront() {
        return mIsActivityFront;
    }

    @Override
    public void onCaton(BJPlayerView view) {
        LogUtils.d("onCaton....");
        int gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        ToastUtils.showMessage("", gravity);
    }

    @Override
    public void onCaching(final boolean isCaching) {
        LogUtils.i("BaiJiaVideoPlayerView", "onCaching:" + isCaching);
        Method.runOnUiThread(LiveVideoForLiveActivity.this, new Runnable() {
            @Override
            public void run() {
                if (isCaching) {
                    mLayoutLoading.showLoading();
                } else {
                    mLayoutLoading.hide();
                    if (mImageCover.getVisibility() == View.VISIBLE)
                        mImageCover.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void onReconnecting() {
        LogUtils.i("onReconnecting:");
        ToastUtils.showShort("正在重连...");
    }

    @Override
    public void onPlayResume() {
        if (CommonUtils.isFastDoubleClick()) return;
        mStatisticsUtil.startCollectPlayEvent(currentTime / 1000, totalTime / 1000);
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                playingFlag = PLAY_PLAYING;
                // btnStartBtn.setImageResource(R.drawable.video_pause_icon);
                mVideoBottomLayout.onVideoStateChange(playingFlag);
                mAudioHelper.requestAudioFocus();
                initMinVideo();
            }
        });
        if (!isLiveVideo)
            LiveVideoNotificationService.notifyAction(this, mLession.title, mImgCoverPath, LiveVideoNotificationService.PLAY_MUSIC);

    }

    @Override
    public void onPlayPause(final boolean isError) {
        if (CommonUtils.isFastDoubleClick()) return;
        mStatisticsUtil.onVideoStop(currentTime / 1000);
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                if (playingFlag != PLAY_STOP) {
                    playingFlag = isError ? PLAY_STOP : PLAY_PAUSE;// PLAY_PAUSE;
                }
                // btnStartBtn.setImageResource(R.drawable.video_play_icon);
                mVideoBottomLayout.onVideoStateChange(playingFlag);
            }
        });
        if (!isLiveVideo)
            LiveVideoNotificationService.notifyAction(this, mLession.title, mImgCoverPath, LiveVideoNotificationService.PAUSE_MUSIC);

    }

    @Override
    public void onVideoPrepared(int startTime, int duration) {
        if ((playingFlag == PLAY_PLAYING || playingFlag == PLAY_PAUSE) && mIsActivityFront) {//2
            mVideoPlayer.resume();
            playingFlag = PLAY_PLAYING;//1
            mVideoBottomLayout.onVideoStateChange(playingFlag);
        }
        currentTime = startTime * 1000;
        totalTime = duration * 1000;
        hasVideo = true;
        showControlView(true);
    }

    @Override
    public void onJoinSuccess() {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                if (playingFlag == PLAY_UNSTART)
                    playingFlag = PLAY_PLAYING;
                mVideoCenterLayout.setPlayer(mVideoPlayer);
                hasVideo = isLiveVideo;
                if (isLiveVideo && isPortrait)
                    showControlView(true);
                if (mScreenOrientationHelper != null) {
                    mScreenOrientationHelper.enableSensorOrientation();
                    mScreenOrientationHelper.postOnStart();
                }
                initMinVideo();
                if (isLiveVideo && mLession != null)
                    ServiceProvider.reportLiveRecord(mLession.id, mLession.bjyRoomId, mLession.classId, mLession.coursewareId);
            }
        });
    }

    @Override
    public void onJoinFailed(final String error, final boolean isLivePlay) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                playingFlag = PLAY_UNSTART;
                mVideoPlayer.stop();
                mLayoutLoading.showErrorTip(error);
                AnimUtils.animTopShow(layoutTitle, true);
            }
        });
    }

    @Override
    public void onPlayError(String error, int code) {
        playingFlag = PLAY_PAUSE;
        mVideoPlayer.pause();
        mLayoutLoading.showErrorTip(error);
        AnimUtils.animTopShow(layoutTitle, true);
        if (!isLiveVideo)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isDestroyed())
                        LiveVideoNotificationService.notifyAction(LiveVideoForLiveActivity.this, mLession.title, mImgCoverPath, LiveVideoNotificationService.CANCEL_NOTIFY);
                }
            });
    }

    @Override
    public void onPlayErrorInfo(String error, int code) {//仅统计用
        mStatisticsUtil.onErrorEvent(currentTime / 1000, code, error);
        if (code == 5101 || code == 5102 || code == 5103)
            onRefreshToken();
        if (!isLiveVideo)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isDestroyed())
                        LiveVideoNotificationService.notifyAction(LiveVideoForLiveActivity.this, mLession.title, mImgCoverPath, LiveVideoNotificationService.CANCEL_NOTIFY);
                }
            });

    }

    @Override
    public void onPPtPageChange(int currentPage, int totalPage) {
        mVideoCenterLayout.pptPageChange(currentPage, totalPage);
        mVideoBottomLayout.pptPageChange(currentPage, totalPage);
    }

    @Override
    public void onVideoEnd() {
        mStatisticsUtil.onVideoStop(currentTime / 1000);
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                if (mLession == null)
                    return;
                mJudged = mVideoBottomLayout.mShouldSeek = sharedPreferences.getBoolean(mLession.coursewareId + "judge", false);
                playingFlag = PLAY_COMPLETED;
                mLayoutLoading.hide();
                if (mJudegeResult != null)
                    mJudegeResult.isFinish = 1;
                mVideoBottomLayout.onVideoStateChange(playingFlag);
                savePlayProgress(true, true);
                currentTime = 0;//防止destory时再次提交
                mLession.liveStatus = 2;
                if (!mJudged && !isLocalVideo()) {
                    onJudege();
                }
            }
        });
        if (!isLiveVideo)
            LiveVideoNotificationService.notifyAction(this, mLession.title, mImgCoverPath, LiveVideoNotificationService.PAUSE_MUSIC);

    }

    @Override
    public void selectAvatar(LiveChatExpressBean bean) {
        if (bean == null) {
            return;
        }
        if (playerType == 1) {
            if (mVideoPlayer == null) {
                return;
            }
            mVideoPlayer.sendChatMsg(bean.key, "", true);
            mGridView.setVisibility(View.GONE);
        }

//        else {
//            mChatEditText.insertAvatar(bean.key);
//        }
    }

    @Override
    public void onBackPressed() {
        boolean shouldRateScreen = !isPortrait && CommonUtils.isPad(this) ? !mIsRequestLand : !isPortrait;
        if (shouldRateScreen) {
            if (!closeInteractionInLand()) {
                if (mScreenOrientationHelper != null) {
                    mScreenOrientationHelper.portrait();
                } else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mVideoBottomLayout.showFullScreen(false);
                mVideoCenterLayout.showCenter(true, false);
                openSensor(10000);
            }
            return;
        } else if (isLiveVideo && playingFlag != PLAY_COMPLETED) {
            DialogUtils.onShowConfirmDialog(this, null, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    savePlayProgress(true);
                    mStatisticsUtil.clickBack(currentTime / 1000);
                    finish();
                }
            }, null, "当前教室正在直播，确定要离开吗？", "确定", "继续听课");
            return;
        }
        savePlayProgress(true);
        mStatisticsUtil.clickBack(currentTime / 1000);
        finish();

    }

    @Override
    public void onExpressUpdate(String playerTypeKey, List<LiveChatExpressBean> list) {
        if (mGridViewAvatarAdapter != null) {
            mGridViewAvatarAdapter.onExpressUpdate(playerTypeKey, list);
        }
    }

    @Override
    public void onChatWithPublic() {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                mChatMsgPresenter.fiterTeacherChatMessage();
                mVideoView.setTeacherNots();
            }
        });
    }

    @Override
    public void onRecordVideoInit(int result, final boolean hv, int duration) {
        totalTime = duration;
        mPlayOnlineTime = System.currentTimeMillis();
        if (mVideoPlayer == null) return;
        if (!hasCurrentVideoInit) hasCurrentVideoInit = true;
    }

    //seek完成与updatePostion都调这个方法
    @Override
    public void onSeek(int var1) {
        if (var1 != currentTime && mLayoutLoading.getVisibility() == View.VISIBLE)
            mLayoutLoading.setVisibility(View.GONE);
        currentTime = var1;
//        LogUtils.e("onSeek", currentTime + "," + totalTime);
        savePlayProgress(false);
        setTimeText();
        if (playingFlag == PLAY_COMPLETED && currentTime > 0) {
            playingFlag = PLAY_PLAYING;
            mVideoBottomLayout.onVideoStateChange(playingFlag);
        }
    }

    @Override
    public void onTestOptions(String url, boolean isClose) {
        if (isPortrait) {
            mGridView.setVisibility(View.GONE);
            mChatEditText.requestFocus();
            InputMethodUtils.hideMethod(mChatEditText.getContext(), mChatEditText);
        } else
            mVideoView.closeDialog();

        if (isClose && mVideoTestView.getVisibility() == View.VISIBLE) {
            mVideoTestView.closePage();
        } else if (!isClose) {
            if (mVideoTestView.getVisibility() == View.GONE)
                mVideoTestView.setVisibility(View.VISIBLE);
            mVideoTestView.loadUrl(url);
        }
    }

    public CourseWareInfo getPlayingCourseWare() {
        return mLession;
    }

    //展开收回三屏
    private void onClickExpandVideo() {
        if (mVideoCenterLayout == null || mVideoView == null || mVideoPlayer == null || getPlayer() == null || !hasVideo)
            return;
        mVideoCenterLayout.setIsExpandVideo(!mVideoCenterLayout.isVideoExpand());
        mVideoView.showTeacherVideo(mVideoCenterLayout.isVideoExpand());
    }

    //Token刷新
    private void onRefreshToken() {
        mLayoutLoading.showLoading();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getPlaybackToken(mLession.coursewareId, courseId, mLession.videoType), new NetObjResponse<CourseApiService.DownToken>() {
            @Override
            public void onSuccess(BaseResponseModel<CourseApiService.DownToken> model) {
                mLayoutLoading.hide();
                CourseApiService.DownToken downToken = model.data;
                if (mLession.token == downToken.token) {
                    onError(null, -1);
                    return;
                }
                mLession.token = downToken.token;
                ((BaiJiaVideoPlayerImpl) mVideoPlayer).setCourseInfo(mLession);
                mVideoPlayer.refreshPlay();
            }

            @Override
            public void onError(String message, int type) {
                mLayoutLoading.hide();
                onJoinFailed("token错误，退出重进:5001", isLiveVideo);
            }
        });
    }

    //输入框处理
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null) {
            Rect rect = new Rect();
            //获取输入框当前的location位置
            v.getGlobalVisibleRect(rect);
            int left = rect.left;
            int top = rect.top;
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //初始化浮动视频
    private void initMinVideo() {
        if (mFloatIconLayout == null) {
            mFloatIconLayout = (FrameLayout) findViewById(R.id.float_operation_icon);
            int height = mFloatIconLayout.getLayoutParams().height = mFloatVideoView.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.common_dimens_108dp);//Math.min(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight()) / 2;
            mFloatVideoView.getLayoutParams().width = (int) (height / (0.75f));
            openMinView.setOnClickListener(this);
            closeMinView.setOnClickListener(this);
            openTeacherMinVideo();
        }
    }

    //打开浮动视
    private void openTeacherMinVideo() {
        try {
            if (playerType == PLAY_UNSTART)
                return;
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mFloatVideoView.getLayoutParams();
            if (mFloatVideoView.getVisibility() == View.GONE || mFloatVideoView.getTranslationX() != 0) {
                mFloatVideoView.setTranslationX(0);
                mFloatVideoView.setVisibility(View.VISIBLE);
                if (mVideoCenterLayout.isVideoExpand())
                    onClickExpandVideo();
                mFloatVideoView.setCanMove(true);
                ((BaiJiaVideoPlayerImpl) getPlayer()).setVideoContainer(mFloatVideoView);
                int width = isPortrait ? Math.min(rootView.getWidth(), rootView.getHeight()) : Math.max(rootView.getWidth(), rootView.getHeight());
                int height = isPortrait ? Math.max(rootView.getWidth(), rootView.getHeight()) : Math.min(rootView.getWidth(), rootView.getHeight());
                Rect rect = new Rect(0, 0, width, height);
                if (isPortrait) {
                    rect.top = height - interactiveView.getHeight() + getResources().getDimensionPixelOffset(R.dimen.common_dimens_46dp);
                    layoutParams.topMargin = rect.top;
                    if (isLiveVideo) rect.bottom = height - ll_send_message.getMeasuredHeight();
                    closeMinView.setVisibility(View.VISIBLE);
                    mFloatIconLayout.setVisibility(View.GONE);
                } else {
                    layoutParams.topMargin = 0;
                    closeMinView.setVisibility(View.GONE);
                    mFloatIconLayout.setVisibility(View.GONE);
                    mVideoCenterLayout.setFloatState(true);
                }
                float radio = getPlayer().isVideoPlay ? 0.56f : 0.75f;
                mFloatVideoView.setParentRect(rect,radio);
                mFloatVideoView.getLayoutParams().width = getPlayer().isVideoPlay ? getResources().getDimensionPixelOffset(R.dimen.common_dimens_192dp) : (int) (mFloatVideoView.getLayoutParams().height / (radio));
                layoutParams.leftMargin = width - mFloatVideoView.getLayoutParams().width;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
            } else {
                closeMinFloatView(true);
            }
        } catch (Exception e) {
        }
    }

    //关闭浮动视频
    private void closeMinFloatView(boolean close) {
        if(mFloatVideoView == null || mFloatIconLayout == null)
            return;
        mFloatIconLayout.setVisibility(isPortrait ? View.VISIBLE : View.GONE);
        if(mFloatVideoView.getVisibility() == View.VISIBLE){
        int transX = mFloatVideoView.getLayoutParams().width - getResources().getDimensionPixelOffset(R.dimen.common_25dp);
        int width = isPortrait ? Math.min(rootView.getWidth(), rootView.getHeight()) : Math.max(rootView.getWidth(), rootView.getHeight());
        int height = isPortrait ? Math.max(rootView.getWidth(), rootView.getHeight()) : Math.min(rootView.getWidth(), rootView.getHeight());
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mFloatVideoView.getLayoutParams();
        if (isPortrait) {
            layoutParams.leftMargin = width - mFloatVideoView.getLayoutParams().width;
            layoutParams.topMargin = height - interactiveView.getHeight() + getResources().getDimensionPixelOffset(R.dimen.common_dimens_46dp);
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            mFloatVideoView.setTranslationX(transX);
        } else {
            mVideoCenterLayout.setFloatState(false);
        }
        mFloatVideoView.setVisibility(View.GONE);
        if (mFloatVideoView.getChildCount() > 1)
            mFloatVideoView.removeViewAt(0);
        closeMinView.setVisibility(View.GONE);
        mFloatVideoView.setCanMove(false);
        if (close) ((BaiJiaVideoPlayerImpl) mVideoPlayer).showVideo(false);
        }
    }

    //声音焦点处理
    private void audioMethod() {
        mAudioHelper = new AudioStreamReciverHelper(this);
        mAudioHelper.register();
        mAudioHelper.setListener(new AudioStreamReciverHelper.AudioStreamChangeListener() {
            @Override
            public void handleHeadsetDisconnected() {
                pauseVideo();
            }

            @Override
            public void onPhoneStateChanged() {
            }
        });
        mAudioHelper.requestTheAudioFocus(new AudioStreamReciverHelper.AudioListener() {
            @Override
            public void start() {
                if (mVideoPlayer != null) {
                    if (isLiveVideo)
                        mVideoPlayer.unMute();
                    else if (playingFlag == PLAY_PAUSE) {//2
                        mVideoPlayer.resume();
                        playingFlag = PLAY_PLAYING;//1
                        mVideoBottomLayout.onVideoStateChange(playingFlag);
                    }
                }
            }

            @Override
            public void pause() {
                if (mVideoPlayer != null) {
                    if (isLiveVideo)
                        mVideoPlayer.mute();
                    else if (playingFlag != PLAY_STOP && playingFlag != PLAY_COMPLETED) {
                        playingFlag = PLAY_PAUSE;//2;
                        mVideoPlayer.pause();
                        mVideoBottomLayout.onVideoStateChange(playingFlag);
                    }
                }
            }
        });
    }

    private void initViews() {
        sharedPreferences = getSharedPreferences(UserInfoUtil.userName, MODE_PRIVATE);
        layoutTitle.setTitle(mLession.title);
        interactiveView = getLayoutInflater().inflate(
                R.layout.viewpager_live_video_interactive, null);
        mChatMsgPresenter = new OnliveChatPresenter(interactiveView, this);
        mChatMsgPresenter.shouldShadow(false, true);
        mChatMsgPresenter.setColor("#669DBC", "#4A4A4A", "#FF6D73");
        ll_send_message = (LinearLayout) findViewById(R.id.ll_send_message);
        mGridView = (GridView) findViewById(R.id.gridview_expression);
        mGridViewAvatarAdapter = new GridViewAvatarAdapter(this);
        mGridView.setAdapter(mGridViewAvatarAdapter);
        mLimitTips = (TextView) findViewById(R.id.limit_textsize);
        mCenterPlayBtn = (ImageView) findViewById(R.id.rl_start_play_live);
        mImageCover = (ImageView) findViewById(R.id.image_live_detail);
        viewpager_live_video = (FrameLayout) findViewById(R.id.viewpager_live_video);
        viewpager_live_video.addView(interactiveView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mVideoCenterLayout.setSelectedChange(new LiveVideoCenterView.OnSelectedChangeListener() {
            @Override
            public void onQualityChanged(int position, String text) {
                if (playingFlag != PLAY_UNSTART) {
                    mVideoCenterLayout.showPP(0);
                    String qualityNames[] = text.split(" ");
                    mVideoBottomLayout.updateQuality(qualityNames[0]);
                    mVideoPlayer.setQuality(position);
                    mStatisticsUtil.onVideoOperate(currentTime / 1000, qualityNames[0]);
                }
            }

            @Override
            public void onSpeedChanged(int position, String speed) {
                if (playingFlag != PLAY_UNSTART) {
                    mVideoCenterLayout.showPP(0);
                    mVideoBottomLayout.updateSpeed(speed);
                    mVideoPlayer.setSpeed(position);
                    mStatisticsUtil.onSpeedChanged(currentTime / 1000, speed);
                }
            }

            @Override
            public void onClickMoreMenu(int position) {
                mVideoCenterLayout.showPP(0);
                switch (position) {
                    case 0:
                        ShareDialogFragment fragment = ShareDialogFragment.getInstance(courseId, mImgCoverPath);
                        fragment.show(getSupportFragmentManager(), "share");
                        break;
                    case 1:
                        FeedbackActivity.newInstance(LiveVideoForLiveActivity.this);
                        break;
                }
            }
        });

        mVideoView.setChatList(this);
        setSendLayoutState(isLiveVideo && isPortrait);
        mVideoBottomLayout.isLocalVideo(isLocalVideo() || mLession.videoType != 1);
        ImageLoad.displaynoCacheImage(this, R.drawable.trans_bg, mImgCoverPath, mImageCover);
        mVideoCenterLayout.registerVolumeChangeReceiver(this, isLiveVideo);
        mIsRequestLand = getIntent().getBooleanExtra("forLandScape", false);
        if (isLiveVideo)
            mVideoTestView.initWebview(mLession.coursewareId + "", mLession.bjyRoomId);
    }

    private void initVideoSensor() {
        if (mIsRequestLand || (CommonUtils.isPad(this) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isPortrait = false;
            mChatMsgPresenter.setIsPortrait(isPortrait);
//            isSendLayoutShow = false;
            layoutTitle.setFullScreen(true);
            mVideoCenterLayout.openLock(false);
            mVideoCenterLayout.showCenter(false, !isPortrait);
            setSendLayoutState(false);
            if (isLiveVideo) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoTestView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW, 0);
            }
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        openSensor(1000);
    }

    private void setViewsState() {
        layoutTitle.setVisibility(View.VISIBLE);
        mVideoCenterLayout.showSpeedTextView(false);
        mVideoBottomLayout.showFullScreen(!isPortrait);
        setSendLayoutState(isPortrait && isLiveVideo);
        viewpager_live_video.setVisibility(isPortrait ? View.VISIBLE : View.GONE);
        mChatEditText.setHintTextColor(Color.parseColor(isPortrait ? "#dfdfdf" : "#888888"));
        mChatEditText.setTextColor(Color.parseColor(isPortrait ? "#333333" : "#888888"));
    }

    private void setSendLayoutState(boolean flag) {
//        isSendLayoutShow = flag;
        if (isPortrait) {
            if (isLiveVideo) {
                ll_send_message.setVisibility(View.VISIBLE);
                mChatMsgPresenter.showSendMessageView(true);
//                isSendLayoutShow = true;
            } else {
                ll_send_message.setVisibility(View.GONE);
                mChatMsgPresenter.showSendMessageView(false);
                Method.hideKeyboard(mChatEditText);
                mGridView.setVisibility(View.GONE);
            }
            viewpager_live_video.setVisibility(View.VISIBLE);
        } else {
            mChatMsgPresenter.showSendMessageView(false);
            ll_send_message.setVisibility(View.GONE);
            Method.hideKeyboard(mChatEditText);
            mGridView.setVisibility(View.GONE);
            viewpager_live_video.setVisibility(View.GONE);
        }
    }

    private void startPlayViews() {
        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
        }
        int playIndex = 0;
        initPlayerAndStart(playIndex);
    }

    private void initPlayerAndStart(int playIndex) {
        if (mVideoPlayer != null) {
            mVideoPlayer.releasePlayer();
            mVideoPlayer = null;
        }

        if (mLession.videoType == 2 && mLession.tinyLive == 1) {
            CommonUtils.startLiveRoom(getSubscription(), this, String.valueOf(mLession.classId), String.valueOf(mLession.coursewareId), mLession.parentId, mLession.joinCode, mLession.bjyRoomId, mLession.sign);
            finish();
            return;
        } else if (mLession.videoType == 2 && mLession.liveStatus == 0) {
            playingFlag = PLAY_UNSTART;
            showImageCover();
            ToastUtils.showShort("课程未开始");
            return;
        } else if (mLession.videoType == 2 && mLession.liveStatus == 2) {
            playingFlag = PLAY_UNSTART;
            showImageCover();
            ToastUtils.showShort("直播已结束，等待回放上传");
            return;
        } else {
            initPlayer();
            mVideoPlayer.initPlayerParams(mLession);
            mVideoCenterLayout.setOnTouchListener(onPlayViewTouchListener);
            startPlay(mLession);
        }

    }

    private void startPlay(CourseWareInfo lessionInfo) {
        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
        hasCurrentVideoInit = false;
        layoutTitle.setTitle(StringUtils.valueOf(lessionInfo.title));
        mCenterPlayBtn.setVisibility(View.GONE);
        mImagePeople.setEnabled(true);
        sendbutton.setEnabled(true);
        mChatEditText.setEnabled(true);
        playingFlag = PLAY_PLAYING;// 1;
        totalTime = 0;
        currentTime = 0;
        lastSaveTime = 0;
        hasVideo = false;
        mVideoBottomLayout.onVideoStateChange(playingFlag);
        mVideoBottomLayout.updateTime(0, totalTime);
        setTimeText();
        setSendLayoutState(isLiveVideo);
        mChatEditText.setPlayerType(playerType);
        mChatMsgPresenter.clearMessage();
        mLayoutLoading.showLoading();
        UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
    }

    private void initPlayer() {
        releasePlayer();
        mVideoPlayer = VideoPlayer.getPlayerInstance(playerType, isLiveVideo,
                getSupportFragmentManager(), LiveVideoForLiveActivity.this);
        mVideoPlayer.setIsLive(isLiveVideo);
        mVideoPlayer.initPlayerViews(layoutPptView, layoutVideoFatherView);
    }

    private void initListener() {
        mLayoutLoading.mIsLocal = isLocalVideo();
        mLayoutLoading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected() && !isLocalVideo()) {
                    ToastUtils.showShort("当前无网络，请检查网络!");
                    return;
                }
                if (mVideoPlayer == null || getPlayer() == null)
                    return;
                mLayoutLoading.hide();
                if (isLiveVideo || !hasVideo)
                    mVideoPlayer.refreshPlay();
                else
                    mVideoPlayer.resume();
//                playingFlag = PLAY_PLAYING;
//                mVideoBottomLayout.onVideoStateChange(PLAY_PLAYING);
//                errorCode = -2;
            }
        });
        mVideoCenterLayout.setOnClickListener(this);
        mVideoBottomLayout.setOnClickListener(this);
        layoutTitle.setOnClickListener(this);
        sendbutton.setOnClickListener(this);
        mImagePeople.setOnClickListener(this);
        mCenterPlayBtn.setOnClickListener(this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPortrait) {
                    closeInteractionInLand();
                }
                if (isLiveVideo && isPortrait) {
                    Method.hideKeyboard(getCurrentFocus());
                    mGridView.setVisibility(View.GONE);
                }
            }
        });

        mChatEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mGridView.isShown()) {
                    //lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    // hideEmotionLayout(true);//隐藏表情布局，显示软件盘

                    if (mGridView.isShown()) {
                        mGridView.setVisibility(View.GONE);
                       /* if (showSoftInput) {
                            showSoftInput();
                        }*/
                        mChatEditText.requestFocus();
                        InputMethodUtils.showMethodDelayed(mChatEditText.getContext(), mChatEditText, 0);
                    }
                    //软件盘显示后，释放内容高度
                }
                return false;
            }
        });
        mChatEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendbutton.performClick();
                    return true;
                }
                return false;
            }
        });
        mChatEditText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectionStart = mChatEditText.getSelectionStart();
                selectionEnd = mChatEditText.getSelectionEnd();
                // 先去掉监听器，否则会出现栈溢出
                mChatEditText.removeTextChangedListener(this);
                int length = temp.length();
                if (length > mMaxChars) {
                    editable.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart - 1;
                    mChatEditText.setText(editable);
                    mChatEditText.setSelection(tempSelection);
                    length = mMaxChars;
                }
                mLimitTips.setText(length + "/25");
                mChatEditText.addTextChangedListener(this);
            }
        });
//
        mVideoBottomLayout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mState != null) {
                    mState.offset = progress - currentTime;
                    mState.type = GestureState.GESTURE_TYPE_PROGRESS;
                    mState.seekedPosition = progress;
                    mState.duration = totalTime;
                    mVideoCenterLayout.onGestSeeking(mState);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mVideoCenterLayout != null && mVideoPlayer != null) {
                    mState = new GestureState(LiveVideoForLiveActivity.this);
                    mStatisticsUtil.onSeekStart(currentTime / 1000);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogUtils.i("onStopTrackingTouch: ");
                if (mVideoPlayer == null) {
                    return;
                }
                currentTime = seekBar.getProgress();
                savePlayProgress(false);
                mVideoPlayer.seekTo(currentTime);
                mStatisticsUtil.onSeekEnd(currentTime / 1000);
                mVideoCenterLayout.onGestEnd(mState);
                mState = null;
            }
        });
        onPlayViewTouchListener = new OnVodPlayViewTouchListener();
    }

    private void initData() {
        mLession = (CourseWareInfo) originIntent.getSerializableExtra(ArgConstant.BEAN);
        courseId = originIntent.getStringExtra("course_id");
        if (null != mLession) {//通过听云记录最近播放的回放，方便异常定位
            NBSAppAgent.setUserCrashMessage("liveforVideo", mLession.coursewareId + "," + courseId + "," + mLession.bjyRoomId);
        }

        mInitPlayIndex = originIntent.getIntExtra("play_index", -1);
        videoInfoStr = originIntent.getStringExtra("video_info_str");
        isFromOffLine = originIntent.getBooleanExtra("from_off_line", false);
        mImgCoverPath = originIntent.getStringExtra("imgPath");
        // 直播中的底部控制拦 隐藏其他
        if (mLession.videoType == 2 && mLession.liveStatus == 1) {
            mVideoBottomLayout.setLiveStyle();
        }
        if (TextUtils.isEmpty(courseId)) {
            LogUtils.e("courseId is null");
            finish();
            return;
        }
//        liveCount = 0;
        isPortrait = true;
        isLiveVideo = mLession.videoType == 2;//originIntent.getBooleanExtra(ArgConstant.IS_LIVE, false);
        playerType = 0;//
        totalTime = 0;
        currentTime = 0;
        lastSaveTime = 0;

        playingFlag = PLAY_UNSTART;//0;
        mVideoBottomLayout.setVisibility(View.GONE);
        mVideoBottomLayout.onVideoStateChange(playingFlag);
        mVideoCenterLayout.mIsLive = isLiveVideo;
        mStatisticsUtil = new VideoStatisticsUtil(mLession, courseId);
        mVideoCenterLayout.mStatisticsUtil = mStatisticsUtil;

    }

    private void pauseVideo() {
        if (mVideoPlayer != null) {
            if (isLiveVideo)
                mVideoPlayer.mute();
            else {
                mVideoPlayer.pause();
                if (playingFlag != PLAY_STOP && playingFlag != PLAY_COMPLETED)
                    playingFlag = PLAY_PAUSE;//2;
                mVideoBottomLayout.onVideoStateChange(playingFlag);
            }
        }
    }

    private void releasePlayer() {
        if (this.mVideoPlayer != null) {
            this.mVideoPlayer.releasePlayer();
            this.mVideoPlayer = null;
        }
        mVideoBottomLayout.setEnabled(false);
    }

    private void showImageCover() {
        mImageCover.setVisibility(View.VISIBLE);
        mLayoutLoading.setVisibility(View.GONE);
        layoutTitle.setVisibility(View.VISIBLE);
        mVideoBottomLayout.setVisibility(View.GONE);
        if (null != mChatMsgPresenter) mChatMsgPresenter.clearMessage();
    }

    private void onJudege() {
        if (mJudegeResult == null)
            return;
        if (mJudegeResult.isFinish != 1) {
            ToastUtils.showShort("观看完课程可评价");
            return;
        }
        final String rid = String.valueOf(mLession.coursewareId);
        if (!mJudegeResult.result)
            CourseJudgeActivity.newInstance(LiveVideoForLiveActivity.this, String.valueOf(mLession.classId), rid, mLession.parentId);//courseId,
    }

    //表情
    private void handleEmojiBtnClick() {
        if (mGridView.isShown()) {
            mGridView.setVisibility(View.GONE);
            mChatEditText.requestFocus();
            InputMethodUtils.showMethodDelayed(mChatEditText.getContext(), mChatEditText, 0);
        } else {
            //hideSoftInput();
            InputMethodUtils.hideMethod(mChatEditText.getContext(), mChatEditText);
            //mEmotionLayout.getLayoutParams().height = softInputHeight;
            mGridView.setVisibility(View.VISIBLE);

        }
    }

    //初始化横屏个更多按钮
    private void showRightAction(View v) {
        if (mActionDialog == null) {
            mActionDialog = new PopRightActionDialog(v.getContext(), courseId, mImgCoverPath);
            if (isFromOffLine) {
                mActionDialog.setDisableLike();
            }
            //addressDialog.setOnSubItemClickListener(this);
        }
        mActionDialog.show();
    }

    //右上角更多按钮
    private void showTitlePopWindow(View anchor) {

        if (!isPortrait) {
            showRightAction(anchor);// mVideoCenterLayout.showPP(3);
        } else if (msgActons == null) {
            msgActons = new PopCollectListAction(this, R.layout.pop_collection_more_views, R.id.root);

            msgActons.setDistance(DensityUtils.dp2px(this, 20));
            msgActons.setCourseId(courseId);
            if (isFromOffLine) {
                msgActons.setDisableLike();
            }
            msgActons.setAnimStyle(R.style.Animations_PopDownMenu_Right);
            msgActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    switch (position) {
                        case 0:
                            msgActons.dismiss();
                            ShareDialogFragment fragment = ShareDialogFragment.getInstance(courseId, mImgCoverPath);
                            fragment.show(getSupportFragmentManager(), "share");
                            VideoStatisticsUtil.reportShareEvent(courseId, mLession != null ? mLession.coursewareId : -1);
                            break;
                        case 1:
                            msgActons.perfomClick();
                            break;
                        case 2:
                            msgActons.dismiss();
                            FeedbackActivity.newInstance(LiveVideoForLiveActivity.this);
                            break;
                    }
                }
            });
            msgActons.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mCenterPlayBtn.getVisibility() == View.GONE && (playingFlag != PLAY_UNSTART)) {
                        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
                        UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
                    }
                }
            });
            msgActons.show(anchor);
        } else
            msgActons.Reshow(anchor);
    }

    // 想自己添加评论
    private void onClickAddChat() {
        if (CommonUtils.isFastDoubleClick()) return;
        mVideoView.showAddChatDialog(mVideoPlayer);
    }

    //保存进度
    private void savePlayProgress(boolean forceSave) {
        savePlayProgress(forceSave, false);
    }

    //int mCheckTimes=0;
    private void savePlayProgress(boolean forceSave, boolean isFinish) {
        // LogUtils.e("savePlayProgress","1");
        //五分钟定时上报 及 播放结束时上报
        boolean isTimeLowerDiff = Math.abs(currentTime - lastSaveTime) < 300000;//5 minutes
//        LogUtils.e("savePlayProgress", "2");
        if (isTimeLowerDiff && (!forceSave)) {
            return;
        }
        if (!isLiveVideo && (mLession != null)) {

            //回放进度保存
            if (mLession.videoType != 2 && currentTime > 0) {
                if (currentTime == 0 || totalTime == 0) return;
                lastSaveTime = currentTime;
                sharedPreferences.edit().putInt(mLession.coursewareId + "", (isFinish ? totalTime : currentTime)).commit();
                LogUtils.i("currentTime  save: " + currentTime + "  , id=" + mLession.bjyRoomId);
                if (mLession.downStatus == DownBtnLayout.FINISH)
                    SQLiteHelper.getInstance().upDatePlayProgress(mLession.getDownloadId(), (isFinish ? totalTime : currentTime));

                long diffOnlineTime = (System.currentTimeMillis() - mPlayOnlineTime) / 1000;

                ServiceExProvider.visit(CourseApiService.saveRecordCourseProgress(mLession.joinCode, mLession.bjyRoomId, mLession.bjySessionId, mLession.id, (isFinish ? totalTime : currentTime) / 1000, diffOnlineTime, mLession.bjyRoomId, mLession.bjySessionId, totalTime / 1000));
                LogUtils.i("currentTime  save5: " + currentTime + "  , id=" + mLession.coursewareId);
            }
        }
    }

    //处理锁频的事件
    private void onClickLockScreenBtn(boolean isLock) {
        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
        mVideoCenterLayout.openLock(isLock);
        if (isLock && !isPortrait) {
            if (mScreenOrientationHelper != null)
                mScreenOrientationHelper.postOnStop();
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            if (mScreenOrientationHelper != null)
                mScreenOrientationHelper.postOnStart();
            else
                openSensor(1000);
        }
        if (isLock && !isPortrait) {
            layoutTitle.setVisibility(View.GONE);
            mVideoBottomLayout.setVisibility(View.GONE);
            mVideoCenterLayout.showCenter(true, !isPortrait);
            UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
        } else
            showControlView(true);
        // mVideoCenterLayout.showCenter(true, !isPortrait);
    }

    private void onClickStartBtn() {
        if (playingFlag == PLAY_UNSTART) {
            // btnStartBtn.setImageResource(R.drawable.video_pause_icon);

            startPlayViews();
        } else if (playingFlag == PLAY_PLAYING) {
            playingFlag = PLAY_STOP;//PLAY_PAUSE
            // btnStartBtn.setImageResource(R.drawable.video_play_icon);
            mVideoBottomLayout.onVideoStateChange(playingFlag);
            if (mVideoPlayer != null) {
                mVideoPlayer.pause();
            }
            // mCenterPlayBtn.setVisibility(View.VISIBLE);
        } else if (playingFlag == PLAY_STOP || playingFlag == PLAY_PAUSE) {//PLAY_PAUSE
            playingFlag = PLAY_PLAYING;
            // btnStartBtn.setImageResource(R.drawable.video_pause_icon);
            mVideoBottomLayout.onVideoStateChange(playingFlag);
            if (mVideoPlayer != null) {
                mVideoPlayer.resume();
            }
            mCenterPlayBtn.setVisibility(View.GONE);
        } else if (playingFlag == PLAY_COMPLETED) {
            playingFlag = PLAY_PLAYING;
            // btnStartBtn.setImageResource(R.drawable.video_pause_icon);
            mVideoBottomLayout.onVideoStateChange(playingFlag);
            if (mVideoPlayer != null) {
                mVideoPlayer.replay();
            }
        }
    }

    //互动
    private void onClickInteraction() {
        boolean isShow = mVideoCenterLayout.isChatExpand();
        mVideoCenterLayout.setIsExpandChat(!isShow);
        mVideoView.setChatListView(!isShow);
    }

    //全屏
    private void onClickFullScreen() {
        if (CommonUtils.isFastDoubleClick()) return;
        if (isPortrait) {
            if (mScreenOrientationHelper == null)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            else
                mScreenOrientationHelper.landscape();
            mVideoBottomLayout.showFullScreen(true);
            mVideoCenterLayout.showCenter(true, true);
            layoutTitle.setFullScreen(true);
            openSensor(10000);
        }
    }

    //设置进度时间
    private void setTimeText() {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                mVideoBottomLayout.updateTime(currentTime, totalTime);
            }
        });
    }

    private void setTitleBarState() {
        if (!hasVideo)
            return;
        if (!isPortrait && mVideoCenterLayout != null && mVideoCenterLayout.mIsLock) {
            mVideoCenterLayout.switchLock();
            UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
            if (mVideoCenterLayout.isShowLock())
                UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
            return;
        }
        if (layoutTitle == null)
            return;
        if (layoutTitle.getVisibility() == View.VISIBLE) {
            //layoutTitle.setVisibility(View.GONE);
            showControlView(false);
        } else {
            showControlView(true);
        }
    }

    private void showControlView(boolean show) {
        if (show && !isPortrait && mVideoCenterLayout.mIsLock)
            show = false;
        UniApplicationLike.getApplicationHandler().removeCallbacks(titleRunnable);
        if (show) {
            UniApplicationLike.getApplicationHandler().postDelayed(titleRunnable, 3000);
            if (layoutTitle.getVisibility() != View.VISIBLE)
                AnimUtils.animTopShow(layoutTitle, true);
            if (hasVideo && mVideoBottomLayout.getVisibility() != View.VISIBLE && (!isLiveVideo || isPortrait))
                AnimUtils.animBottomShow(mVideoBottomLayout);
            mVideoCenterLayout.showCenter(true, !isPortrait);
        } else {
            if (layoutTitle.getVisibility() != View.VISIBLE || mLayoutLoading.isShowError())
                return;
            AnimUtils.animTopShow(layoutTitle, false);
            mVideoCenterLayout.showCenter(false, !isPortrait);
            if (!isLiveVideo || isPortrait)
                AnimUtils.animBottomHide(mVideoBottomLayout, false);
        }

    }

    private class OnVodPlayViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(playingFlag == PLAY_UNSTART)
                return false;
            if (mVideoView == null || mVideoView.isInterceptTouchEvent(motionEvent) || playingFlag == PLAY_UNSTART) {
                canScroll = false;
            }
            initGestrueDetector(motionEvent);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) motionEvent.getX();
                    lastY = (int) motionEvent.getY();
                    mState = new GestureState(LiveVideoForLiveActivity.this);
                    curseekTime = 0;
                    isMove = false;
                    mStatisticsUtil.onSeekStart(currentTime / 1000);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mState == null || !canScroll || (!isLiveVideo && !hasVideo))
                        break;
                    if (mVideoCenterLayout == null || (mVideoCenterLayout.mIsLock && !isPortrait))
                        break;
                    int currentX = (int) motionEvent.getX();
                    int offX = currentX - lastX;
                    int currentY = (int) motionEvent.getY();
                    int offY = lastY - currentY;
                    boolean isLand = Math.abs(offX) > Math.abs(offY);
                    if (isLand && (isLiveVideo || playingFlag == PLAY_COMPLETED))
                        break;
                    if (isLand && (Math.abs(offX) < 100 || Math.abs(offY) > 75))
                        break;
                    if (!isLand && (Math.abs(offY) < 100 || Math.abs(offX) > 75))
                        break;
                    if (Math.abs(offX) > 100 || Math.abs(offY) > 100) {
                        isMove = true;
                        if (playingFlag != PLAY_UNSTART && currentTime > 0 && isLand) {
                            if (mState.type == -1)
                                mState.type = GestureState.GESTURE_TYPE_PROGRESS;
                        } else if (playingFlag != PLAY_UNSTART && !isLand) {
                            if (mState.type == -1) {
                                boolean isVolume = currentX >= getResources().getDisplayMetrics().widthPixels * 0.5f;
                                mState.type = isVolume ? GestureState.GESTURE_TYPE_VOLUME : GestureState.GESTURE_TYPE_BRIGHTNESS;
                            }
                        } else
                            isMove = false;

                        if (isMove && isLand) {
                            curseekTime = currentTime + (int) (offX * 600 * 1000f / mVideoCenterLayout.getWidth());
                            if (curseekTime > totalTime) {
                                curseekTime = totalTime;
                            } else if (curseekTime < 0) {
                                curseekTime = 0;
                            }
                            mState.duration = totalTime;
                            mState.seekedPosition = curseekTime;
                        }
                        if (isMove) {
                            mState.offset = isLand ? offX : offY;
                            mVideoCenterLayout.onGestSeeking(mState);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    actionUpEvent();
                    canScroll = true;
                    break;
            }
            return true;
        }
    }

    private void actionUpEvent() {
        if (!isMove || mState == null)
            return;
        mVideoCenterLayout.onGestEnd(mState);
        if (mState.type == GestureState.GESTURE_TYPE_PROGRESS) {
            currentTime = curseekTime;
            if (currentTime >= totalTime) {
                currentTime = totalTime;
            }
            if (null != mVideoPlayer) {
                mVideoPlayer.seekTo(currentTime);
                mStatisticsUtil.onSeekEnd(currentTime / 1000);
            }
        }
        mState = null;
        isMove = false;
    }

    //处理单击与双击事件
    private void initGestrueDetector(MotionEvent motionEvent) {
        if (mDetector == null)
            mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (!isMove) {
                        if (!isPortrait) {
                            if (!closeInteractionInLand()) {
                                setTitleBarState();
                            }
                            mVideoCenterLayout.showPP(0);
                        } else {
                            setTitleBarState();
                        }
                    }
                    return super.onSingleTapUp(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (!isMove && !isLiveVideo && hasVideo) {
                        onClickStartBtn();
                        showControlView(true);
                    }
                    return super.onDoubleTap(e);
                }
            });
        mDetector.onTouchEvent(motionEvent);
    }

    private boolean closeInteractionInLand() {
        if (!isPortrait && mVideoView != null) {
            return mVideoView.closeDialog();
        }
        return false;
    }

    private boolean isLocalVideo() {
        return mLession != null && FileUtil.isFileExist(mLession.offSignalFilePath) && FileUtil.isFileExist(mLession.targetPath);
    }

    private void openSensor(long millionTime) {
        if (CommonUtils.isPad(this))
            UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED && !mVideoCenterLayout.mIsLock)
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }, millionTime);
    }

    private void register() {
        if (isLiveVideo)
            return;
        IntentFilter intentFilter = new IntentFilter(LiveVideoNotificationService.ACTION);
        bgPlayReceiver = new BgPlayReceiver();
        registerReceiver(bgPlayReceiver, intentFilter);
    }

    class BgPlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LiveVideoNotificationService.ACTION.equals(action)) {
                int event = intent.getIntExtra("operation", -1);
                if (event == -1)
                    return;
                if (playingFlag == PLAY_PAUSE) {//2
                    mVideoPlayer.resume();
                    playingFlag = PLAY_PLAYING;//1
                    mVideoBottomLayout.onVideoStateChange(playingFlag);
                } else {
                    playingFlag = PLAY_PAUSE;//2;
                    mVideoPlayer.pause();
                    mVideoBottomLayout.onVideoStateChange(playingFlag);
                }
            }
        }
    }

}
