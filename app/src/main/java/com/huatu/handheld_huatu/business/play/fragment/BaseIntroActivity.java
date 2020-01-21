package com.huatu.handheld_huatu.business.play.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.ProvinceFaceToFaceFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.play.bean.CourseActDetailBean;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineItemBean;
import com.huatu.handheld_huatu.business.play.bean.OptionsInfo;
import com.huatu.handheld_huatu.business.play.event.VideoInfoMessageEvent;
import com.huatu.handheld_huatu.business.ztk_vod.BJBottomViewImpl;
import com.huatu.handheld_huatu.business.ztk_vod.BJCenterViewExPresener;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.MusicPlayContract;
import com.huatu.handheld_huatu.business.ztk_vod.MusicPlayPresenter;
import com.huatu.handheld_huatu.business.ztk_vod.PopRightActionDialog;
import com.huatu.handheld_huatu.business.ztk_vod.ShareDialogFragment;
import com.huatu.handheld_huatu.business.ztk_vod.highmianshou.HighMianShouActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmOrderFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.SimpleBjPlayerStatusListener;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.BJPlayerExView;
import com.huatu.handheld_huatu.ui.ShadowDrawable;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.WXUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomTTFTypeFaceSpan;
import com.huatu.handheld_huatu.view.RoundBackgroundColorSpan;
import com.huatu.music.IMusicService;
import com.huatu.music.bean.Music;
import com.huatu.music.event.StatusChangedEvent;
import com.huatu.music.player.MusicPlayerService;
import com.huatu.music.player.PlayManager;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.netease.hearttouch.router.HTRouter;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.socialize.UMShareAPI;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static com.huatu.music.player.PlayManager.mService;

/**
 * 新的售前详情页
 */
@HTRouter(url = {"ztk://course/detail"}, needLogin = false)
public class BaseIntroActivity extends XiaoNengHomeActivity implements View.OnClickListener {
    private final String TAG = "httpBaseIntroActivity";
    @BindView(R.id.root_view)
    View mRootView;
    @BindView(R.id.buy_button)
    LinearLayout buy_layout;
    @BindView(R.id.kefu)
    TextView mKefu;
    @BindView(R.id.buy_text)
    TextView mBuy;
    @BindView(R.id.video_show)
    RelativeLayout video_show_layout;
    @BindView(R.id.presale_video_tab_back)
    ImageView videoBack;
    @BindView(R.id.presale_show_more)
    ImageView mShare;
    @BindView(R.id.play_cover)
    ImageView playBackground;
    @BindView(R.id.presale_magic_indicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.presale_viewpager)
    ViewPager mMagicViewPager;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activte_start_time)
    TextView mActiviteTime;
    @BindView(R.id.redbag_tip)
    TextView mRedbagTip;
    @BindView(R.id.redbag_icon)
    ImageView mRedbagIcon;
    @BindView(R.id.redbag_tip_layout)
    View mRedbagLayout;
    @BindView(R.id.bottom_show)
    View mBottomView;
    @BindView(R.id.buy_price)
    TextView mBuyPrice;
    @BindView(R.id.collection)
    TextView mCollectionView;

    //活动布局
    @BindView(R.id.course_active_vs)
    ViewStub mCourseActiveVs;
    private LinearLayout detailShowLayout;
    private TextView hintFront;
    private TextView detailLimitText;

    //拼团
    @BindView(R.id.group_buy_layout)
    ViewStub mGroupStupView;
    private View mSingleBuy;
    private View mGoupBuyBtn;
    private TextView mGroupBuyPrice;
    private TextView mGroupBuyEndTime;
    private TextView mSinglePrice;
    private View mGroupParentLayout;

    //视频信息显示
    @BindView(R.id.video_info_vs)
    ViewStub mVideoPlayeInforVs;
    private View mVideoPlayerView;
    private View showPlayBtn;//播放按钮
    private TextView mStartFreeBtn;//免费试听按钮
    private TextView mobile_text;//消耗流量提示

    //播放器
    @BindView(R.id.video_player_vs)
    ViewStub mVideoPlayerVs;
    private BJPlayerExView mBjPlayerView;

    //课程集合选择
    @BindView(R.id.multi_option_layout)
    ViewStub mMultiOptionLayout;
    private View mMultiOptionView;
    private  View mShowOptions;
    private TextView mChosedTv;
    private ViewGroup fullScreenView, miniScreenView;

    private final String[] tabNames = new String[]{"课程详情", "课程大纲", "老师介绍", "课程评价"};
    private String classId;
    private int courseType;
    private int isSaleOut; //1售罄
    private int isRushOut; //1停售
    private int isDaishou; ////是否待售  0否 1是
    private String classPrice;
    private String originalPrice;
    private int isBuy;
    private CompositeSubscription mCompositeSubscription;
    private long bjyVideoId;
    private String bjyVideoToken;
    private int mCollageActiveId;//小程序页面id
    private boolean isProvincialFaceToFace = false;//是否为省考高端面授一对一
    private boolean isMianshou = false; //是否高端面授课程
    private int realEndTime; //活动结束时间，倒计时用
    private int realStartTime; //活动开始时间,倒计时用
    private int realGroupBuyTime;//拼团结束倒计时;
    protected CustomConfirmDialog confirmDialog;
    public int province;  //城市id
//    public String actualPrice; //实际价格
    public String classScaleimg;
    public boolean isQiangGou; //是否抢购课
    public boolean isActive; //是否活动
    private String NetClassId, mNetClassId, msgNetClassId, Srid, mRid, rid;
    private boolean mToHome, isJpush;
    private long videoTrilSize;
    private boolean isTrailPlay;
    private boolean isGroupBuy = false;
    private boolean isGroupBuyEnd = false;
    private boolean isBuyed = false;//是否已拼团
    private int activityClassId;
    private boolean canNotBuy = false;
    private FragmentPagerItemAdapter mFragmentPagerItemAdapter;
    private int mNaviTitleSize = 16;
    private CourseDetailBean.FilterOptions filterList;
    private OptionsInfo mSelectedOption;
    private MusicPlayPresenter mMusicPresenter;
    private int currentPlayType = -1;
    private AudioManager mAudioManager;
    private boolean mIsCollect = false; //是否是合集课程
    private String title, describe;
    private long realSystemTime;
    private CourseDetailBean courseDetailBean;//CourseActDetailBean courseActDetailBean;
    private QuickListAction shareActons;

    private String pageSource;
    private int cateId;
    private PopRightActionDialog mActionDialog;
    private PlayManager.ServiceToken mToken;
    private CourseOutlineItemBean mCurrentItemBean;

    private List<CourseOutlineItemBean> audioList;
    private int currentPostion = -1;
    private VideoStatisticsUtil mStatisticsUtils;
    private List<Music> musicList = new ArrayList<>();

    private int olderOrientation = -1;

    private BJBottomViewImpl mBjBottomViewPresenter;
    private BJCenterViewExPresener mBjCenterViewPresenter;

    public static void newIntent(Context context, String NetClassId) {
        Intent intent = new Intent(context, BaseIntroActivity.class);
        intent.putExtra("NetClassId", NetClassId);
        context.startActivity(intent);
    }

    public static void newIntent(Context context, String NetClassId, int collageActivityId) {
        Intent intent = new Intent(context, BaseIntroActivity.class);
        intent.putExtra("NetClassId", NetClassId);
        intent.putExtra("collageActivityId", collageActivityId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String FRAGMENT_TAGS = "android:support:fragments";
            savedInstanceState.remove(FRAGMENT_TAGS);
        }
        QMUIStatusBarHelper.translucent(this);
        initParameters();
//        initview();
        setOnlistener();
        EventBus.getDefault().register(this);
        initMagicViewPager();
        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{Color.parseColor("#4C231C"), Color.parseColor("#A07B74")});
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        mRootView.setBackground(gradientDrawable);
        getCourseAuditionList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.outline.trial.video");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBjPlayerView != null && mBjPlayerView.getVisibility() == View.VISIBLE) {
            mBjPlayerView.onResume();
        }

        if (isGroupBuy && realEndTime > 0) {
            handler.removeCallbacksAndMessages(null);
            obtainEndTime();
        }

        olderOrientation = getResources().getConfiguration().orientation;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBjPlayerView != null && mBjPlayerView.isPlaying()) {
            mBjPlayerView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (olderOrientation == Configuration.ORIENTATION_LANDSCAPE && fullScreenView != null && fullScreenView.getVisibility() == View.VISIBLE) {
            if (!CommonUtils.isPad(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fixPlayWindows(false);
            } else {
                setFullOrSmall(false);
                fixPlayWindows(true);
            }
            return;
        }
        if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(this);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != olderOrientation) {
            try {
                olderOrientation = newConfig.orientation;
                CoordinatorLayout.Behavior behavior =
                        ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
                if (behavior instanceof AppBarLayout.Behavior) {
                    AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                    int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
                    if (topAndBottomOffset == 0)
                        mAppBarLayout.setExpanded(true);
                    else if (Math.abs(topAndBottomOffset) >= mAppBarLayout.getTotalScrollRange())
                        mAppBarLayout.setExpanded(false);
                }
                View tmpContainer = this.findViewById(R.id.video_show);
                LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) tmpContainer.getLayoutParams();
                int distanceHeight = (int) (DensityUtils.getScreenWidth(this) * 0.56);
                curParams.height = distanceHeight;
                tmpContainer.setLayoutParams(curParams);
                if (mBjPlayerView.getVisibility() == View.VISIBLE) {
                    setfullScreenVideo();
                }
                if (mActionDialog != null && mActionDialog.isShowing())
                    mActionDialog.dismiss();
                if (shareActons != null && shareActons.isShowing())
                    shareActons.dismiss();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAudioManager != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    mAudioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    mAudioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        if (mBjPlayerView != null) {
            mBjPlayerView.onDestroy();
            mBjPlayerView = null;
        }
        if (null != mMusicPresenter) {
            mMusicPresenter.detachView();
            mMusicPresenter = null;
        }
        super.onDestroy();
        UMShareAPI.get(this).release();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        if (confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
        UniApplicationLike.getApplicationHandler().removeCallbacksAndMessages(null);
        ToastUtils.cancle();
        if (mToken != null) {
            PlayManager.unbindFromService(mToken);
            mToken = null;

            final Intent i = new Intent(this, MusicPlayerService.class);
            i.setAction(MusicPlayerService.SERVICE_CMD);
            i.putExtra(MusicPlayerService.CMD_NAME, MusicPlayerService.CMD_CLEAR);
            this.startService(i);
        }

    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_base_intro_layout;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    public int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kefu:
                startChat(title, classPrice, describe, "http://v.huatu.com/cla/class_detail_" + classId + ".htm", classScaleimg);
                StudyCourseStatistic.consultServer(classId);
                break;
            case R.id.buy_button:
            case R.id.buy_text:
                onClickBuy();
                break;
            case R.id.presale_video_tab_back:
                if (isJpush || (mToHome && !ActivityStack.getInstance().hasRootActivity())) {
                    Intent intent = new Intent(BaseIntroActivity.this,
                            MainTabActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.single_buy_button:
                onClickBuy();
                break;
            case R.id.group_buy_button:
                onClickGroupBuy();
                break;
            case R.id.collection:
                onClickCollectionView();
                break;
            case R.id.select_options:
                createFilterDialog(false);
                break;
        }
    }

    @Override
    public void customChatParam() {
        switch (cateId) {
            case 1008:
            case 1013:
            case 1000:
                customGroupId = HUAHUA_ROBOT_GROUPID;
                break;
            case 1001:
                customGroupId = ZAIZAI_ROBOT_GROUPID;
                break;
        }
        mTitleName = "课程详情页";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VideoInfoMessageEvent event) {
        if (event.type == VideoInfoMessageEvent.VideoInfo_CourseDetailBean) {
            courseDetailBean = event.mCourseDetailBean;
            initCourseInfo(courseDetailBean);
            initVideoInfo(courseDetailBean);
            showRedBagActView(courseDetailBean.aloneByPrice);
            initview();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePlayStatus(StatusChangedEvent event) {

        LogUtils.e("onUpdatePlayStatus", GsonUtil.GsonString(event));
        boolean isLastPlaying = false;
        if (null != mBjBottomViewPresenter) {
            isLastPlaying = mBjBottomViewPresenter.isPlaying();
            mBjBottomViewPresenter.setIsPlaying(event.misPlaying);
        }
        if (null != mBjCenterViewPresenter) {
            //playPauseView.setLoading(!event.isPrepared)
            if (event.fromWating > 0) {
                if (event.fromWating == 1)
                    mBjCenterViewPresenter.showLoading("");
                else
                    mBjCenterViewPresenter.dismissLoading();
            } else {
                if (!event.misPrepared) {
                    mBjCenterViewPresenter.showLoading("");
                } else {
                    mBjCenterViewPresenter.dismissLoading();
                }
            }
        }
    }

    @LoginTrace(type = 0)
    private void onClickCollectionView() {
        if (CommonUtils.isFastDoubleClick()) return;
        final boolean isSelected = mCollectionView.isSelected();
        Subscriber<DeleteResponseBean> subscriber = new Subscriber<DeleteResponseBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showMessage("请求出错，稍后重试！");
                    }
                });
            }

            @Override
            public void onNext(final DeleteResponseBean deleteResponseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (deleteResponseBean.code != 1000000) {
                            ToastUtils.showMessage(isSelected ? "取消失败" : "收藏失败");
                        } else {
                            ToastUtils.showMessage(isSelected ? "取消成功" : "收藏成功");
                            PrefStore.putSettingInt("user_collection_count_change", 1);
                            mCollectionView.setSelected(!isSelected);
                        }
                    }
                });
            }
        };
        if (isSelected) {
            ServiceProvider.cancelCourseCollection(mCompositeSubscription, subscriber, classId);
        } else
            ServiceProvider.addCourseCollection(mCompositeSubscription, classId, subscriber);
    }

    @LoginTrace(type = 0)
    private void onClickBuy() {
        if (canNotBuy && isBuy != 1)
            return;
        if (isBuy == 1) {//已购买可以学习
            startBJYMediaPlay(classId);
        } else if (!isGroupBuy && (isBuy == 1 || realStartTime > 0)) {
            //已购买不做任何操作
            //据开枪不做任何操作
        } else if (isMianshou) {
            //高端面试课
            showHighMianShouDialog();
        } else if (isProvincialFaceToFace) {
            Bundle arg = new Bundle();
            arg.putString("course_id", classId);
            arg.putString("province_id", String.valueOf(province));
            arg.putString("origin_price", originalPrice);
            arg.putString("cur_price", classPrice);
            ProvinceFaceToFaceFragment.show(BaseIntroActivity.this, arg);
        } else if (classPrice != null && classPrice.equals("0") && isSaleOut != 1 && isRushOut != 1) {
            addFreeCourse();
        } else if (isSaleOut != 1 && isRushOut != 1 && isDaishou != 1) {

            ConfirmOrderFragment.newInstance(BaseIntroActivity.this,  new CourseActDetailBean(courseDetailBean.aloneByPrice, courseDetailBean.activityList),courseDetailBean != null && courseDetailBean.iso2o && mSelectedOption != null ? mSelectedOption.id : 0 ,
                    classId, pageSource);
//            finish();
        }
    }

    //集合选择
    public void onSelected(OptionsInfo selectedOption, int position, boolean seleted) {
        mSelectedOption = selectedOption;
        mSelectedOption.iso2o = courseDetailBean.iso2o;
        if (Utils.isEmptyOrNull(mChosedTv.getText().toString()) || seleted)
            mChosedTv.setText(selectedOption.name);
        if (courseDetailBean.iso2o || classId.equals(selectedOption.id + "") || !seleted)
            return;
        mMagicViewPager.setCurrentItem(0);
        reset();
        classId = selectedOption.id + "";
        getCourseAuditionList();
        ((CourseJudgeFragment) mFragmentPagerItemAdapter.getPage(3)).reLoad(classId, courseType);
        ((CourseTeacherFragment) mFragmentPagerItemAdapter.getPage(2)).reLoadData(classId, courseType);
        ((CourseOutlineFragment) mFragmentPagerItemAdapter.getPage(1)).reLoadData(classId);
        ((CourseDetailFragment) mFragmentPagerItemAdapter.getPage(0)).reLoad(classId, courseType, mCollageActiveId);

    }

    private void dealVideoPlayer() {
        try {
            Button mStartPlayBtn = (Button) mBjPlayerView.findViewById(R.id.start_play_btn);
            ImageView imageViewBack = (ImageView) mBjPlayerView.findViewById(R.id.rl_back);
            ImageView imageViewIvt = (ImageView) mBjPlayerView.findViewById(R.id.iv_titlebt);
            mStartPlayBtn.getLayoutParams().height = 0;
            imageViewBack.getLayoutParams().height = 0;
            imageViewIvt.getLayoutParams().height = 0;
            mBjPlayerView.findViewById(R.id.lock_screen_btn).getLayoutParams().height = 0;
            mBjPlayerView.findViewById(R.id.textView_TitleBar_Info).getLayoutParams().height = 0;
            mBjPlayerView.findViewById(R.id.changeSpeed_txt).getLayoutParams().height = 0;
            mBjPlayerView.findViewById(R.id.changeDefinition_txt).getLayoutParams().height = 0;
            mBjPlayerView.findViewById(R.id.rl_speed).getLayoutParams().height = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVideoInfo(CourseDetailBean event) {
        classPrice = event.actualPrice;
        courseType = event.isLive;
        cateId = event.cateNu;
        title = event.classTitle;
        describe = event.classTitle;
        if (event.activityType == 21) {
            if (event.activityClassId != 0)
                activityClassId = event.activityClassId;
            isGroupBuy = true;
        } else {
            isGroupBuy = false;
        }
        isRushOut = event.isRushOut;
        isSaleOut = event.isSaleOut;
        if (isSaleOut == 1 || isRushOut == 1) {
            canNotBuy = true;
        } else
            canNotBuy = false;
        isBuy = event.isBuy;

        ImageLoad.load(this, event.scaleimg, playBackground);
        //视频相关处理
        if (!TextUtils.isEmpty(event.videoId)) {
            //有视频可播放
            inflateVideoView();
            bjyVideoId = StringUtils.parseLong(event.videoId);
            bjyVideoToken = event.token;
            showPlayBtn();
            showPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPlayType = 1;
                    startTrailPlay(false);

                }
            });
            if (NetUtil.isConnected() && !NetUtil.isWifi()) {
                mobile_text.setVisibility(View.VISIBLE);
                mobile_text.setText("提示：即将消耗" + event.videoSize + "手机流量");
            }
        }

        mCollectionView.setSelected(event.isCollect == 1);
        if (isGroupBuy) {
            refreshGroupBuyView(event);
        } else if (!isGroupBuy && event.isRushClass == 1) {
            isQiangGou = true;
            realStartTime = event.saleStart;
            if (realStartTime > 0) {
                inflateCourseActiveVs();
                hintFront.setText("距开抢");
                setStartTime();
                mBuy.setText("即将开抢");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
                return;
            } else if (event.saleEnd > 0) {
                inflateCourseActiveVs();
                realEndTime = event.saleEnd;
                hintFront.setText("距停售");
                String time = secondToTime(realEndTime);
                setLimitTimeTextStyle("剩" + time + " 停售", time);
                // detailLimitText.setText("剩" + secondToTime(realEndTime) + "停售");
                setEndTime();
            } else if (detailShowLayout != null)
                detailShowLayout.setVisibility(View.GONE);
        } else if (event.isDiscount == 0) {
            //无活动
            if (detailShowLayout != null && detailShowLayout.getVisibility() == View.VISIBLE)
                detailShowLayout.setVisibility(View.GONE);
        } else if (!isGroupBuy && event.isDiscount == 1 && !Utils.isEmptyOrNull(event.activityEndTime)) {
            inflateCourseActiveVs();
            isActive = true;
            //有活动
            detailShowLayout.setVisibility(View.VISIBLE);
            hintFront.setText("限时特惠");
            String endTime = event.activityEndTime;
            LogUtils.d(TAG, "end time is :" + endTime);
            try {
                realEndTime = Integer.valueOf(endTime).intValue();
                LogUtils.d(TAG, "get real end time is :" + realEndTime);
                if (realEndTime > 0) {
                    String time = secondToTime(realEndTime);
                    setLimitTimeTextStyle("剩" + time + " 恢复原价", time);
                    //  detailLimitText.setText("剩" + secondToTime(realEndTime) + "恢复原价");
                    setEndTime();
                } else {
                    detailShowLayout.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(TAG, e.toString());
                LogUtils.d(TAG, "can not change string to long time...");
            }
        }
        if (isGroupBuy && !canNotBuy) {
            initGroupBuyView(event);
        }
        if (mMultiOptionView == null && ((event.filterList != null && mIsCollect) || (event.iso2o && event.o2oFilterList != null))) {
            filterList = event.iso2o ? event.o2oFilterList : event.filterList;
            initMultiOption();
            createFilterDialog(true);
        }
    }

    private void refreshGroupBuyView(CourseDetailBean event) {
        try {
//            mActiviteTime.setVisibility(View.VISIBLE);
            detailShowLayout.setVisibility(View.GONE);
            LogUtils.d(TAG, "get real end time is :" + realEndTime);
            if (event.collageStatus != 1)
                mActiviteTime.setVisibility(View.GONE);
            else {
                if (event.isCollage == 1) {
                    realGroupBuyTime = Integer.valueOf(event.autoCancelAt).intValue();
//                    mActiviteTime.setText("剩余" + secondToTime(realGroupBuyTime));
                    String time = secondToTime(realGroupBuyTime);
                    setActiveTimeTextStyle("剩余" + time, time);
                    setGroupBuyime();
                } else if (!TextUtils.isEmpty(event.activityStartTime) && !TextUtils.equals("0", event.activityStartTime)) {
                    realStartTime = Integer.valueOf(event.activityStartTime).intValue();
//                    mActiviteTime.setText("距离活动开始还有" + secondToTime(realStartTime));
                    String time = secondToTime(realStartTime);
                    setActiveTimeTextStyle("距离活动开始还有" + time, time);
                    setStartTime();
                } else if (!TextUtils.isEmpty(event.activityEndTime) && !TextUtils.equals("0", event.activityEndTime)) {
                    realEndTime = Integer.valueOf(event.activityEndTime).intValue();
//                    mActiviteTime.setText("距离活动结束还有" + secondToTime(realEndTime));
                    String time = secondToTime(realEndTime);
                    setActiveTimeTextStyle("距离活动结束还有" + time, time);
                    setEndTime();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, e.toString());
            LogUtils.d(TAG, "can not change string to long time...");
        }
    }

    private void initCourseInfo(CourseDetailBean event) {
        if (event.isProvincialFaceToFace == 1) {
            isProvincialFaceToFace = true;
        }
        if (event.isMianshou == 1) {
            isMianshou = true;
        }
        province = event.province;
//        if (!isGroupBuy) {
//            actualPrice = event.actualPrice;
//        }
        classScaleimg = event.scaleimg;
//        classPrice = actualPrice;
        originalPrice = String.valueOf(event.price);
    }

    private void initGroupBuyView(CourseDetailBean infoBean) {
        initGroupLayout();
        buy_layout.setVisibility(View.GONE);
        mGoupBuyBtn.setVisibility(View.VISIBLE);
        isBuy = infoBean.isBuy;
        if (isBuy == 1 || infoBean.isCollage == 2) {
            canNotBuy = true;
            isGroupBuyEnd = true;
            mSingleBuy.setVisibility(View.GONE);
            mBuyPrice.setVisibility(View.VISIBLE);
            mGroupBuyEndTime.setVisibility(View.GONE);
            buy_layout.setVisibility(View.VISIBLE);
            mBuy.setText("立即学习");
            buy_layout.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
            mGoupBuyBtn.setVisibility(View.GONE);
        } else if (infoBean.collageStatus != 1) {
            isGroupBuyEnd = true;
            mSingleBuy.setVisibility(View.GONE);
            mBuyPrice.setVisibility(View.VISIBLE);
            mGroupBuyPrice.setText("拼团已结束");
            mGroupBuyEndTime.setVisibility(View.VISIBLE);
            mGroupBuyEndTime.setText("去看看其他活动吧");
            mGoupBuyBtn.setOnClickListener(null);
            mGoupBuyBtn.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
        } else if (infoBean.isCollage == 2) {
            isGroupBuyEnd = true;
            isBuyed = true;
            mSingleBuy.setVisibility(View.VISIBLE);
            mGroupBuyPrice.setText("拼团成功");
            mGroupBuyEndTime.setVisibility(View.VISIBLE);
            mBuyPrice.setVisibility(View.GONE);
            mGroupBuyEndTime.setText("去看看其他活动吧");
            mGoupBuyBtn.setOnClickListener(null);
            mGoupBuyBtn.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
        } else if (infoBean.isCollage == 0) {
            isBuyed = false;
            mGoupBuyBtn.setBackgroundResource(realStartTime > 0 ? R.drawable.shape_gray_groupbuy_bg : R.drawable.shape_red_rightcorner_bg);
            mSingleBuy.setVisibility(View.VISIBLE);
            mBuyPrice.setVisibility(View.GONE);
            mGroupBuyEndTime.setVisibility(View.VISIBLE);
            mGroupBuyPrice.setText("￥" + infoBean.collagePrice);
            mGroupBuyEndTime.setText("发起拼团");
            mGoupBuyBtn.setOnClickListener(realStartTime > 0 ? null : this);
        } else if (infoBean.isCollage == 1) {
            isBuyed = true;
            mGoupBuyBtn.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
            mSingleBuy.setVisibility(View.VISIBLE);
            mBuyPrice.setVisibility(View.GONE);
            mGroupBuyPrice.setText("还差" + infoBean.surplusNumber + "人拼团成功");
            mGroupBuyEndTime.setText("剩余" + secondToTime(realStartTime));
            mGoupBuyBtn.setOnClickListener(this);
        }
        mSinglePrice.setText("¥ " + infoBean.actualPrice);
    }

    private void setStartTime() {
        if (realSystemTime == 0) {
            realSystemTime = System.currentTimeMillis() / 1000 + realStartTime;
        } else {
            realStartTime = (int) (realSystemTime - System.currentTimeMillis() / 1000);
        }
        if (realStartTime <= 0) {
            realSystemTime = 0;
            obtainEndTime();
            return;
        }
        if (isGroupBuy) {
            String time = secondToTime(realStartTime);
            setActiveTimeTextStyle("距离活动开始还有" + time, time);
        } else {
            String time = secondToTime(realStartTime);
            setLimitTimeTextStyle("剩" + time + " 开抢", time);
        }
        Message message = handler.obtainMessage();
        message.what = 99999;
        message.obj = realStartTime;
        handler.sendMessageDelayed(message, 1000);
    }

    private void setEndTime() {
        if (realSystemTime == 0) {
            realSystemTime = System.currentTimeMillis() / 1000 + realEndTime;
        } else {
            realEndTime = (int) (realSystemTime - System.currentTimeMillis() / 1000);
        }
        if (realEndTime <= 0) {
            realSystemTime = 0;
            obtainEndTime();
            return;
        }
        if (isGroupBuy) {
            String time = secondToTime(realEndTime);
            setActiveTimeTextStyle("距离活动结束还有" + time, time);
        }
        if (!isGroupBuy && isActive) {
            String time = secondToTime(realEndTime);
            setLimitTimeTextStyle("剩" + time + " 恢复原价", time);
        } else if (!isGroupBuy && isQiangGou) {
            String time = secondToTime(realEndTime);
            setLimitTimeTextStyle("剩" + time + " 停售", time);
        }
        Message message = handler.obtainMessage();
        message.what = 88888;
        message.obj = realEndTime;
        handler.sendMessageDelayed(message, 1000);
    }

    private void setGroupBuyime() {
        if (realSystemTime == 0) {
            realSystemTime = System.currentTimeMillis() / 1000 + realGroupBuyTime;
        } else {
            realGroupBuyTime = (int) (realSystemTime - System.currentTimeMillis() / 1000);
        }
        if (realGroupBuyTime <= 0) {
            realSystemTime = 0;
            obtainEndTime();
            return;
        }
        mGroupBuyEndTime.setText("剩余" + secondToTime(realGroupBuyTime));
        Message message = handler.obtainMessage();
        message.what = 66666;
        message.obj = realGroupBuyTime;
        handler.sendMessageDelayed(message, 1000);
    }

    private String secondToTime(long second) {
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second / 60;            //转换分钟
        second = second % 60;                //剩余秒数
        if (isGroupBuy) {
            String times = days > 0 ? days + "天  " : "";
            times += hours > 0 ? (hours < 10 ? "0" : "") + hours : "00";
            times += ":" + (minutes > 0 ? (minutes < 10 ? "0" : "") + minutes : "00");
            times += ":" + (second > 0 ? (second < 10 ? "0" : "") + second : "00");
            return times;
        }
        if (days > 0) {
            return days + "天";
        } else if (hours > 0) {
            return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (second < 10 ? "0" : "") + second;
        } else if (minutes > 0) {
            return "00:" + (minutes < 10 ? "0" : "") + minutes + ":" + (second < 10 ? "0" : "") + second;
        } else if (second > 0) {
            return "00:00:" + (second < 10 ? "0" : "") + second;
        } else {
            return "00:00:00";
        }
    }

    private void obtainEndTime() {
        ServiceProvider.getCourseDetailInfo(mCompositeSubscription, classId, mCollageActiveId, mIsCollect ? 1 : 0, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, "on error  get course info...");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                CourseDetailBean aa = (CourseDetailBean) model.data;
                initCourseInfo(aa);
                initVideoInfo(aa);
            }
        });
    }

    private void showSharePopWindow(View anchor) {
        if (olderOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mActionDialog == null) {
                mActionDialog = new PopRightActionDialog(anchor.getContext(), classId, classScaleimg);
                mActionDialog.setDisableLike();
            }
            mActionDialog.show();
        } else if (shareActons == null) {
            shareActons = new QuickListAction(this, R.layout.pop_product_more_views, R.id.root);
            shareActons.setAnimStyle(R.style.Animations_PopDownMenu_Right);
            shareActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    switch (position) {
                        case 0:
                            getSendClass();
                            shareActons.dismiss();
                            break;
                        case 1:
                            if (!CommonUtils.checkLogin(BaseIntroActivity.this)) {
                                return;
                            }
                            FeedbackActivity.newInstance(BaseIntroActivity.this);
                            shareActons.dismiss();
                            break;
                    }
                }
            });
            shareActons.show(anchor);
        } else
            shareActons.show(anchor);
    }

    private void getSendClass() {
        int coursewareId = mCurrentItemBean != null ? mCurrentItemBean.coursewareId : 0;//课件id
        int syllabusId = mCurrentItemBean != null ? mCurrentItemBean.id : 0;//大纲id
        int type = mCurrentItemBean != null ? mCurrentItemBean.videoType : -1;
        ShareDialogFragment fragment = ShareDialogFragment.getInstance(classId, coursewareId, classScaleimg, syllabusId + "", type);
        fragment.show(getSupportFragmentManager(), "share");
        VideoStatisticsUtil.reportShareEvent(classId, coursewareId);
    }

    private void startTrailPlay(boolean reset) {
        if (TextUtils.isEmpty(bjyVideoToken)) {
            ToastUtils.showMessage("无效token值，播放错误");
            return;
        }
        inflateVideoPlayerView();
        if (mBjPlayerView != null && mBjPlayerView.getVideoItem() != null) {
            mBjPlayerView.setVisibility(View.VISIBLE);
            if (mBjPlayerView.getVideoItem().videoId == bjyVideoId)
                mBjPlayerView.onResume();
            else {
                float rateFloat = mBjPlayerView.getVideoRateInFloat();
                rateFloat = rateFloat <= 0 ? 1 : rateFloat;
//                resetPlayerView(reset);
                mBjPlayerView.setVideoId(bjyVideoId, bjyVideoToken);
                mBjPlayerView.playVideo();
                mBjPlayerView.setVideoRate(rateFloat);
            }
            return;
        }
        resetPlayerView(reset);
        initPlayerView();
        if (isTrailPlay) {
            mBjPlayerView.setVideoSize(videoTrilSize);
        }
        mBjPlayerView.setVideoId(bjyVideoId, bjyVideoToken);
        mBjPlayerView.playVideo();
        setfullScreenVideo();
        if (CommonUtils.isPad(this))
            UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 10000);
    }

    private void fixPlayWindows(boolean isScroll) {
        View mAppBarChildAt = mAppBarLayout.getChildAt(0);
        AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams)
                mAppBarChildAt.getLayoutParams();
        mAppBarParams.setScrollFlags(isScroll ? AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED : 0);
    }

    private void initview() {
        boolean isFree = Utils.isEmptyOrNull(classPrice) || classPrice.equals("0");
        if (!isGroupBuy) {
            if (isBuy == 1) {
                mBuy.setText("立即学习");
                buy_layout.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
            } else if (isDaishou == 1) {
                mBuy.setText("即将开售");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
            } else if (isSaleOut == 1) {
                mBuy.setText("已售罄");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
            } else if (isRushOut == 1) {
                mBuy.setText("已停售");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
            } else if (isFree) {
                if (courseDetailBean != null && courseDetailBean.isLive == 1) {
                    mBuy.setText("立即报名");
                } else {
                    mBuy.setText("加入学习");
                }
                buy_layout.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
            } else if (!isFree) {
                mBuy.setText("立即购买");
                buy_layout.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
            }
        } else if (canNotBuy) {
            if (isSaleOut == 1) {
                mBuy.setText("已售罄");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
            } else if (isRushOut == 1) {
                mBuy.setText("已停售");
                buy_layout.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
            }
        }
        try {
            View tmpContainer = this.findViewById(R.id.video_show);
            LinearLayout.LayoutParams curParams = (LinearLayout.LayoutParams) tmpContainer.getLayoutParams();
            int distanceHeight = (int) (DensityUtils.getScreenWidth(this) * 0.56);
            curParams.height = distanceHeight;
            tmpContainer.setLayoutParams(curParams);
        } catch (Exception e) {
        }
//        boolean isFree = TextUtils.isEmpty(actualPrice) || TextUtils.equals("0", actualPrice) || (TextUtils.isDigitsOnly(actualPrice) && Float.parseFloat(actualPrice) == 0f);
        String buyPriceValue = isFree ? "免费" : "¥" + classPrice;
        mBuyPrice.setText(buyPriceValue);
        mBuyPrice.setTextColor(isFree ? Color.parseColor("#49CF9E") : Color.parseColor("#ffff3f47"));
        ShadowDrawable.setShadowDrawable(mBottomView, Color.parseColor("#ffffff"), DensityUtils.dp2px(this, 8),
                Color.parseColor("#66000000"), DensityUtils.dp2px(this, 8), 0, 0, ShadowDrawable.SHAPE_ROUND_TOP);
    }

    private void setOnlistener() {
        mKefu.setOnClickListener(this);
        mBuy.setOnClickListener(this);
        buy_layout.setOnClickListener(this);
        videoBack.setOnClickListener(this);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharePopWindow(v);
            }
        });
        mCollectionView.setOnClickListener(this);
    }

    private void initParameters() {
        Intent bIntent = getIntent();
        mCollageActiveId = bIntent.getIntExtra("collageActiveId", 0);
        isBuyed = bIntent.getIntExtra("collageIsBuy", 0) == 1;
        courseType = bIntent.getIntExtra("course_type", 0);
        classPrice = bIntent.getStringExtra("price");
        originalPrice = bIntent.getStringExtra("originalprice");
        try {
            isSaleOut = bIntent.getBooleanExtra("saleout", false) ? 1 : 0;
            isRushOut = bIntent.getBooleanExtra("rushout", false) ? 1 : 0;
            isDaishou = bIntent.getBooleanExtra("daishou", false) ? 1 : 0;
        } catch (Exception e) {
        }
        if (isRushOut == 1 || isSaleOut == 1) {
            canNotBuy = true;
        }
        NetClassId = bIntent.getStringExtra("NetClassId");
        msgNetClassId = bIntent.getStringExtra("msgNetClassId");
        mNetClassId = bIntent.getStringExtra("mNetClassId");
        Srid = bIntent.getStringExtra("Srid");
        rid = bIntent.getStringExtra("rid");
        pageSource = bIntent.getStringExtra("from");
        mToHome = bIntent.getBooleanExtra("toHome", false);
        isJpush = bIntent.getBooleanExtra("isJpush", false);
        mIsCollect = bIntent.getBooleanExtra("isCollect", false);
        Uri uri = bIntent.getData();
        if (uri != null) {
            try {
                mToHome = StringUtils.parseInt(uri.getQueryParameter("toHome")) == 1;
                rid = NetClassId = uri.getQueryParameter("classId");
                String curCourseType = uri.getQueryParameter("course_type");
                if (!TextUtils.isEmpty(curCourseType)) {
                    courseType = Integer.parseInt(uri.getQueryParameter("course_type"));
                } else {
                    curCourseType = uri.getQueryParameter("isLive");
                    courseType = StringUtils.parseInt(curCourseType);
                }
                mCollageActiveId = Integer.parseInt(uri.getQueryParameter("collageActiveId"));
                pageSource = uri.getQueryParameter("from");
                ;
            } catch (Exception e) {
            }
        }
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        if (rid != null) {
            classId = rid;
        } else if (msgNetClassId != null) {
            classId = msgNetClassId;
        } else if (mNetClassId != null) {
            classId = mNetClassId;
        }  else if (Srid != null) {
            classId = Srid;
        } else if (NetClassId != null) {
            classId = NetClassId;
        }
//        actualPrice = classPrice;
        isGroupBuy = mCollageActiveId > 0;
        LogUtils.d(TAG, "classid is :" + classId);

        if (TextUtils.isEmpty(pageSource))
            pageSource = "app其他界面";
        if (!TextUtils.isEmpty(classId))
            StudyCourseStatistic.clickCourse(pageSource, classId);
    }

    private void initMagicViewPager() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabNames.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(tabNames[index]);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mNaviTitleSize);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isDestroyed())
                            mMagicViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
                indicator.setLineHeight(UIUtil.dip2px(context, 4));
                indicator.setYOffset(DensityUtils.dp2px(context, 10));
                indicator.setColors(Color.parseColor("#FF6D73"));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);

        FragmentPagerItems pages = new FragmentPagerItems(getApplicationContext());
        for (int i = 0; i < tabNames.length; i++) {
            Bundle ids = new Bundle();
            ids.putString("course_id", classId);
            ids.putInt("course_type", courseType);
            ids.putInt("activity_id", mCollageActiveId);
            ids.putBoolean("isBuy", isBuy == 1);
            ids.putBoolean("selectCollect", mIsCollect);
            if (i == 0) {
                pages.add(FragmentPagerItem.of(tabNames[i], CourseDetailFragment.class, ids));
            } else if (i == 1) {
                pages.add(FragmentPagerItem.of(tabNames[i], CourseOutlineFragment.class, ids));
            } else if (i == 2) {
                pages.add(FragmentPagerItem.of(tabNames[i], CourseTeacherFragment.class, ids));
            } else if (i == 3) {
                pages.add(FragmentPagerItem.of(tabNames[i], CourseJudgeFragment.class, ids));
            }
        }
        mFragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        mMagicViewPager.setAdapter(mFragmentPagerItemAdapter);
        mMagicViewPager.setOffscreenPageLimit(4);
        mMagicViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mMagicIndicator.onPageSelected(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
        mMagicViewPager.setCurrentItem(0);
        if (audioList != null && !audioList.isEmpty()) {
            ((CourseOutlineFragment) mFragmentPagerItemAdapter.getPage(1)).addTryListenerItem(audioList);
        }
    }

    private void onClickGroupBuy() {
        if (isGroupBuyEnd || !isGroupBuy)
            return;
        if (isBuyed) {
            ToastUtils.showMessage("您已参加过拼团");
            return;
        }

        if (mCollageActiveId > 0)
            WXUtils.appToWxApp(this, mCollageActiveId + "", activityClassId + "", courseType);

    }

    private void addFreeCourse() {
        showProgress();
        ServiceProvider.payFreeOrder(mCompositeSubscription, classId, pageSource, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgess();
                //BJY录播
                startBJYMediaPlay(classId);
                isBuy = 1;
                mBuy.setText("立即学习");
            }

            @Override
            public void onError(Throwable e) {
                hideProgess();
                if (e != null && e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    ToastUtils.showEssayToast(apiException.getErrorMsg());
                }
            }
        });
    }

    private void startBJYMediaPlay(String courseId) {
        Intent intent = new Intent(BaseIntroActivity.this, BJRecordPlayActivity.class);
        intent.putExtra("classid", courseId);
        intent.putExtra(ArgConstant.TYPE, courseType);
        startActivity(intent);
//        finish();
    }

    private void showHighMianShouDialog() {
        if (confirmDialog == null) {
            confirmDialog = DialogUtils.createExitConfirmDialog(BaseIntroActivity.this, null,
                    "请如实填写您的考试信息，通过审核后方可报名。", "取消", "填写信息");
            confirmDialog.setContentGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HighMianShouActivity.newIntent(BaseIntroActivity.this, mSelectedOption.id, classId);
                finish();
            }
        });
        confirmDialog.show();
    }

    private void showRedBagActView(String aloneByPrice) {
        if (aloneByPrice == null || TextUtils.isEmpty(aloneByPrice) || TextUtils.equals("0", aloneByPrice))
            return;
        boolean isEmpty = TextUtils.isEmpty(originalPrice) || TextUtils.equals("0", originalPrice)
                || TextUtils.isEmpty(classPrice) || TextUtils.equals("0", classPrice);
        int price = isEmpty ? 0 : (Integer.valueOf(originalPrice) - Integer.valueOf(classPrice));
        String reducePrice = price > 0 ? price + "元" : null;
        setRedBagPrice(reducePrice, aloneByPrice + "元");
    }

    private void setRedBagPrice(String reducePrice, String price) {
        if (TextUtils.isEmpty(price))
            return;
        mRedbagLayout.setBackgroundResource(isGroupBuy && !canNotBuy ? R.drawable.redbag_float_tip_bg : R.drawable.redbag_float_tip_right_bg);
        mRedbagLayout.setVisibility(View.VISIBLE);
        boolean isEmpty = TextUtils.isEmpty(reducePrice);
        if (!isEmpty)
            reducePrice = "立减" + reducePrice;
        String tips = (!isEmpty ? (reducePrice + " +") : "") + price + " 助学现金红包分享特权";
        SpannableStringBuilder sb = new SpannableStringBuilder(tips);
        int startIndex = tips.lastIndexOf(isEmpty ? price : reducePrice);
        int endIndex = startIndex + (isEmpty ? price.length() : reducePrice.length()) + 1;
        sb.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan span = new StyleSpan(Typeface.BOLD);
        sb.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRedbagTip.setText(sb);
        AnimUtils.startShakeByPropertyAnim(mRedbagIcon, 30f, 2000);
        AnimUtils.startTopDownAnim(mRedbagLayout, -getResources().getDimensionPixelOffset(R.dimen.common_20dp), 2000);
    }

    private void getCourseAuditionList() {
        ServiceProvider.getAuditionList(mCompositeSubscription, classId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                audioList = (List<CourseOutlineItemBean>) model.data;
                if (audioList != null && !audioList.isEmpty()) {
                    inflateVideoView();
                    showPlayBtn();
                    final CourseOutlineItemBean bean = audioList.get(0);
                    mStartFreeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mMagicViewPager.setCurrentItem(1);
                            if (bean == null)
                                return;
                            playVideoByPostion(bean);
                            currentPostion = 0;
                        }
                    });
                    if (mFragmentPagerItemAdapter != null && mFragmentPagerItemAdapter.getPage(1) != null)
                        ((CourseOutlineFragment) mFragmentPagerItemAdapter.getPage(1)).addTryListenerItem(audioList);
                }
            }
        });
    }

    private void showPlayBtn() {
        boolean hasAudio = audioList != null && !audioList.isEmpty();
        if (bjyVideoId > 0) {
            showPlayBtn.setVisibility(View.VISIBLE);
            if (hasAudio) {
                mStartFreeBtn.setVisibility(View.VISIBLE);
                mStartFreeBtn.setBackground(null);
                mStartFreeBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.double_arrow_right, 0);
            }
            playBackground.setColorFilter(getResources().getColor(R.color.common_crop_color_translucent_black_66));
        } else if (hasAudio) {
            mStartFreeBtn.setVisibility(View.VISIBLE);
            mStartFreeBtn.setBackgroundResource(R.drawable.play_btn_play_free);
            mStartFreeBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            playBackground.setColorFilter(getResources().getColor(R.color.common_crop_color_translucent_black_66));
        }

    }

    private void playVideoByPostion(final CourseOutlineItemBean bean) {
        if (bean == null)
            return;
        mCurrentItemBean = bean;
        CourseWareInfo courseWareInfo = new CourseWareInfo();
        courseWareInfo.id = bean.id + "";
        courseWareInfo.classId = bean.classId;
        courseWareInfo.coursewareId = bean.coursewareId;
        courseWareInfo.videoType = bean.videoType;
        courseWareInfo.teacher = bean.teacher;
        courseWareInfo.bjyRoomId = bean.bjyRoomId;
        courseWareInfo.token = bean.token;
        courseWareInfo.bjySessionId = bean.bjySessionId;
        courseWareInfo.title = bean.title;
        courseWareInfo.videoId = bean.videoId;
        courseWareInfo.teacherIds = bean.teacherIds;
        courseWareInfo.teacherNames = bean.teacherNames;
        courseWareInfo.teacher = bean.teacher;
        inflateVideoPlayerView();
        if (bean.videoType == 1) {
            mStatisticsUtils = new VideoStatisticsUtil(courseWareInfo, bean.classId + "");
            bjyVideoId = Long.parseLong(bean.videoId);
            bjyVideoToken = bean.token;
            videoTrilSize = Long.parseLong(bean.fileSize);
            isTrailPlay = true;
            startTrailPlay(bean.videoType != currentPlayType);
            mBjPlayerView.start();
            mBjCenterViewPresenter.setCoverImageShow(false);
        } else {
            if (mBjPlayerView.getVisibility() == View.VISIBLE) {
                playBackground.setVisibility(View.VISIBLE);
                bjyVideoId = 0;
                showPlayBtn();
                mBjPlayerView.setVisibility(View.GONE);
            }

            if (bean.videoType == 5) {
                startPlayMusic(courseWareInfo);

            } else {
                if (!CommonUtils.checkLogin(this)) {
                    return;
                }
                LiveVideoForLiveActivity.startForResult(this, classId, 0, false, courseWareInfo, classScaleimg);
            }

        }
        currentPlayType = bean.videoType;
    }

    private void createMusicList() {
        int index = 0;
        if (!PlayManager.getPlayList().isEmpty() && mCurrentItemBean != null) {
            Music curDto = covertMusicBean(mCurrentItemBean);
            index = PlayManager.getPlayList().indexOf(curDto);
            if (index > -1) {
                PlayManager.play(index, musicList, String.valueOf(classId));
                return;
            }
        }

        boolean hasFind = false;
        for (CourseOutlineItemBean bean : audioList) {
            if (bean.videoType != 5)
                continue;
            Music curDto = covertMusicBean(bean);
            musicList.add(curDto);
            if (mCurrentItemBean != null && mCurrentItemBean.coursewareId == bean.coursewareId) {
                hasFind = true;
            } else if (!hasFind)
                index += 1;
        }
        if (musicList.size() == 0 && mCurrentItemBean != null && mCurrentItemBean.videoType == 5) {
            PlayManager.playOnline(covertMusicBean(mCurrentItemBean));
        } else
            PlayManager.play(index, musicList, String.valueOf(classId));
    }

    private Music covertMusicBean(CourseOutlineItemBean bean) {
        CourseWareInfo courseWareInfo = new CourseWareInfo();
        courseWareInfo.id = bean.id + "";
        courseWareInfo.classId = bean.classId;
        courseWareInfo.coursewareId = bean.coursewareId;
        courseWareInfo.videoType = bean.videoType;
        courseWareInfo.teacher = bean.teacher;
        courseWareInfo.bjyRoomId = bean.bjyRoomId;
        courseWareInfo.token = bean.token;
        courseWareInfo.bjySessionId = bean.bjySessionId;
        courseWareInfo.title = bean.title;
        courseWareInfo.videoId = bean.videoId;
        courseWareInfo.teacherIds = bean.teacherIds;
        courseWareInfo.teacherNames = bean.teacherNames;
        courseWareInfo.teacher = bean.teacher;
        Music curDto = CourseDataConverter.convertCoursewareToMusic(courseWareInfo, null);
        curDto.coverUri = classScaleimg;
        return curDto;
    }

    private void setLimitTimeTextStyle(String content, String time) {
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        if (time.contains("天")) {
            String strTime = time.replace("天", "");
            int start = content.indexOf(strTime);
            style.setSpan(new RoundBackgroundColorSpan(this, Color.WHITE, Color.RED), start, start + strTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String times[] = time.split(":");
            int strStart = -1;
            for (int i = 0; i < times.length; i++) {
                String strTime = times[i];
                int start = 0;
                if (strStart == -1) {
                    start = content.indexOf(strTime);
                } else
                    start = content.substring(strStart).indexOf(strTime) + strStart;
                strStart = start + strTime.length();
                style.setSpan(new RoundBackgroundColorSpan(this, Color.WHITE, Color.RED), start, start + strTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.replace(strStart, strStart + 1, i == 0 ? "时" : i == 1 ? "分" : "秒");
            }
        }
        detailLimitText.setText(style);
    }

    private void setActiveTimeTextStyle(String content, String time) {
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        int start = content.indexOf(time);
        if (time.contains("天")) {
            int dayStart = content.indexOf("天");
            style.setSpan(new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", this), start, dayStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            time = time.substring(time.indexOf("天") + 1);
            start = content.indexOf(time);
        }
        style.setSpan(new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", this), start, start + time.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActiviteTime.setText(style);
    }

    private void setfullScreenVideo() {
        if (olderOrientation == -1)
            return;
        boolean isFull = olderOrientation == Configuration.ORIENTATION_LANDSCAPE;
        if (fullScreenView == null)
            fullScreenView = findViewById(R.id.fullvideoview);
        if (miniScreenView == null)
            miniScreenView = findViewById(R.id.mini_video_screen);
        mAppBarLayout.setExpanded(true, false);
        if (!isFull) fixPlayWindows(false);
//        if ((isFull && fullScreenView.getVisibility() == View.VISIBLE) || (!isFull && fullScreenView.getVisibility() == View.GONE))
//            return;
        setFullOrSmall(isFull);
    }

    private void setFullOrSmall(boolean isFull) {
        if (mBjPlayerView.getParent() != null) {
            ViewGroup viewParent = (ViewGroup) mBjPlayerView.getParent();
            if (isFull && viewParent.getId() == R.id.fullvideoview)
                return;
            if (!isFull && viewParent.getId() == R.id.mini_video_screen)
                return;
            viewParent.removeView(mBjPlayerView);
        }

        ImageView fullScreen = (ImageView) mBjPlayerView.findViewById(R.id.image_change_screen);
        if (isFull) {
            miniScreenView.removeView(mBjPlayerView);
            fullScreenView.removeAllViews();
            fullScreenView.addView(mBjPlayerView);
            fullScreen.setVisibility(View.GONE);
        } else {
            fullScreenView.removeAllViews();
            miniScreenView.removeView(mBjPlayerView);
            miniScreenView.addView(mBjPlayerView, 0);
            fullScreen.setVisibility(View.VISIBLE);
            if (CommonUtils.isPad(getBaseContext())) {
                fullScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        } else setfullScreenVideo();
                        UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            }
                        }, 8000);
                    }
                });
            }
        }
        fullScreenView.setVisibility(isFull ? View.VISIBLE : View.GONE);
    }

    private void initPlayerView() {
        if (mBjBottomViewPresenter == null) {
            mBjPlayerView.initBJYPlayer();
            mBjPlayerView.setCoverImg(classScaleimg);
            mBjBottomViewPresenter = (BJBottomViewImpl) mBjPlayerView.getBottomViewPresenter();
            if (mBjPlayerView.getTopView() != null)
                mBjPlayerView.getTopView().setVisibility(View.GONE);
            mBjCenterViewPresenter = (BJCenterViewExPresener) mBjPlayerView.getBjCenterViewPresenter();
            mBjCenterViewPresenter.setRightMenuHidden(true);
            mBjCenterViewPresenter.showLoading("");
            mCoordinatorLayout.scrollTo(0, 0);
            mBjPlayerView.setVisibility(View.VISIBLE);
            mBjPlayerView.resetStatus();
            fixPlayWindows(false);
        }

        if (mBjPlayerView.getVisibility() == View.GONE)
            mBjPlayerView.setVisibility(View.VISIBLE);
        if (showPlayBtn.getVisibility() == View.VISIBLE) {
            mobile_text.setVisibility(View.GONE);
            playBackground.setVisibility(View.GONE);
            showPlayBtn.setVisibility(View.GONE);
            if (detailShowLayout != null)
                detailShowLayout.setVisibility(View.GONE);
            mActiviteTime.setVisibility(View.GONE);
        }
        if (mAudioManager == null)
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    private void resetPlayerView(boolean reset) {
        if (mBjPlayerView == null)
            return;
        if (playBackground.getVisibility() == View.GONE) {
            playBackground.setVisibility(View.VISIBLE);
            playBackground.clearColorFilter();
        }
        if (reset) {
            if (mToken != null) {
                PlayManager.clearQueue();
                PlayManager.unbindFromService(mToken);
                mToken = null;
                PlayManager.mService = null;
            }
            if (mBjBottomViewPresenter != null) {
                mBjBottomViewPresenter.resetPoint();
                mBjCenterViewPresenter.resetCoverStatus();
                mBjCenterViewPresenter.resetPlaySpeed();
                mBjCenterViewPresenter.onDestory();
                mBjPlayerView.resetStatus();
                mBjCenterViewPresenter.hideCenterView();
                mBjBottomViewPresenter.setOnAudioContractListener(null);
                mBjBottomViewPresenter = null;
            }
            mBjPlayerView.setVisibility(View.GONE);
        }
    }

    private void startPlayMusic(final CourseWareInfo lessionInfo) {
        resetPlayerView(currentPlayType != lessionInfo.videoType);
        initPlayerView();
//        mBjPlayerView.showTopAndBottom();
//        mBjBottomViewPresenter.showChangeScreen(false);
        if (null == mToken) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mToken = PlayManager.bindToService(this, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    mService = IMusicService.Stub.asInterface(iBinder);
                    LogUtils.d("playIndex.....createMusicList.");
                    createMusicList();
//                    Music bean = CourseDataConverter.convertCoursewareToMusic(lessionInfo, null);
//                    PlayManager.playOnline(bean);
                    mBjBottomViewPresenter.setLayoutShow(true);
                    mBjCenterViewPresenter.setCoverImageShow(true);
                    mBjCenterViewPresenter.scaleCenterView();
                    mBjBottomViewPresenter.hideSwitchScreen();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    mService = null;
                    mToken = null;
                }
            });
            mMusicPresenter = new MusicPlayPresenter();
            mMusicPresenter.attachView(new MusicPlayContract.View() {
                @Override
                public void updateProgress(long progress, long max) {
                    if (mBjBottomViewPresenter != null) {
                        mBjBottomViewPresenter.setDuration((int) max);
                        mBjBottomViewPresenter.setCurrentPosition((int) progress);
                    }
                }
            });
            if (null != mBjBottomViewPresenter) {
                mBjBottomViewPresenter.hideSwitchScreen();
                mBjBottomViewPresenter.setOnAudioContractListener(new BJBottomViewImpl.OnAudioContractListener() {
                    @Override
                    public void onPlayPauseClick(View view) {
                        if ((null != mToken) && (null != PlayManager.mService)) {
                            PlayManager.playPause();
                        }
                    }

                    @Override
                    public void onSeekTo(int ms) {
                        if ((null != mToken) && (null != PlayManager.mService)) {
                            PlayManager.seekTo(ms);
                        }
                    }

                    @Override
                    public void switchPlayMode() {
                        if ((null != mToken) && (null != PlayManager.mService)) {
                            PlayManager.setLoopMode(1);
                        }
                    }
                });
            }
        } else {
            Music bean = CourseDataConverter.convertCoursewareToMusic(lessionInfo, null);
            int playIndex = PlayManager.getPlayList() != null ? PlayManager.getPlayList().indexOf(bean) : -1;
            if (playIndex > -1) {
                PlayManager.play(playIndex, 0);
                LogUtils.d("playIndex......");
            } else {
                PlayManager.playOnline(bean);
            }
        }
    }

    private void createFilterDialog(boolean init) {
        if (CommonUtils.isFastDoubleClick())
            return;
        if (mShowOptions.getVisibility() != View.VISIBLE) {
            ((ViewGroup.MarginLayoutParams) mMagicViewPager.getLayoutParams()).bottomMargin = DisplayUtil.dp2px(40);
            mShowOptions.setVisibility(View.VISIBLE);
        }
        CollectFilterDialogFragment filterDialogFragment =  /*CollectFilterDialogFragment.getInstance(courseDetailBean); */init ? CollectFilterDialogFragment.getInstance(courseDetailBean) : CollectFilterDialogFragment.getInstance(mSelectedOption, filterList);
        filterDialogFragment.showNow(getSupportFragmentManager(), "collect");
    }

    private void reset() {
        resetPlayerView(true);
        mRedbagLayout.setVisibility(View.GONE);
        playBackground.setImageResource(R.mipmap.play_default_cover);
        if (mVideoPlayerView != null) {
            showPlayBtn.setVisibility(View.GONE);
            mStartFreeBtn.setVisibility(View.GONE);
            mobile_text.setVisibility(View.GONE);
        }
        bjyVideoId = 0;
        bjyVideoToken = null;
        isBuy = 0;
        classPrice = null;
        originalPrice = null;
        activityClassId = mCollageActiveId = 0;
        realGroupBuyTime = realStartTime = realEndTime = 0;
        isBuyed = isGroupBuy = isGroupBuyEnd = false;
        currentPlayType = -1;
        title = null;
        describe = null;
        handler.removeCallbacksAndMessages(null);
        classScaleimg = null;
        fixPlayWindows(true);
        mAppBarLayout.setExpanded(true, false);
        mCoordinatorLayout.scrollTo(0, 0);
        if (mGroupParentLayout != null)
            mGroupParentLayout.setVisibility(View.GONE);
        buy_layout.setVisibility(View.VISIBLE);
        mStatisticsUtils = null;
    }

    //ViewStub
    private void initGroupLayout() {
        if (mGroupParentLayout == null) {
            mGroupParentLayout = mGroupStupView.inflate();
            mSingleBuy = mGroupParentLayout.findViewById(R.id.single_buy_button);
            mGoupBuyBtn = mGroupParentLayout.findViewById(R.id.group_buy_button);
            mGroupBuyPrice = mGroupParentLayout.findViewById(R.id.group_buy_price);
            mGroupBuyEndTime = mGroupParentLayout.findViewById(R.id.group_buy_endTime);
            mSinglePrice = mGroupParentLayout.findViewById(R.id.personal_buy_price);
            mSingleBuy.setOnClickListener(this);
        } else if (mGroupParentLayout.getVisibility() == View.GONE)
            mGroupParentLayout.setVisibility(View.VISIBLE);
    }

    private void initMultiOption() {
        if (mMultiOptionView == null) {
            mMultiOptionView = mMultiOptionLayout.inflate();
            mChosedTv = mMultiOptionView.findViewById(R.id.selected_tv);
            mShowOptions = mMultiOptionView.findViewById(R.id.select_options);
            mShowOptions.setOnClickListener(this);
        } else if (mMultiOptionView.getVisibility() == View.GONE)
            mMultiOptionView.setVisibility(View.VISIBLE);
    }

    private void inflateVideoView() {
        if (mVideoPlayerView == null) {
            mVideoPlayerView = mVideoPlayeInforVs.inflate();
            showPlayBtn = mVideoPlayerView.findViewById(R.id.v_play);
            mStartFreeBtn = mVideoPlayerView.findViewById(R.id.start_free_play_btn);
            mobile_text = mVideoPlayerView.findViewById(R.id.mobile_text);
        } else if (mVideoPlayerView.getVisibility() == View.GONE)
            mVideoPlayerView.setVisibility(View.VISIBLE);
    }

    private void inflateVideoPlayerView() {
        if (miniScreenView == null) {
            miniScreenView = (ViewGroup) mVideoPlayerVs.inflate();
            mBjPlayerView = miniScreenView.findViewById(R.id.presale_videoview);
            dealVideoPlayer();
            mBjPlayerView.setBjPlayerStatusListener(new SimpleBjPlayerStatusListener() {
                @Override
                public String getVideoTokenWhenInvalid() {
                    return null;
                }

                @Override
                public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {
                    super.onPlayCompleted(playerView, item, nextSection);
                    if (mStatisticsUtils != null)
                        mStatisticsUtils.onVideoFinish(playerView.getCurrentPosition());
                    if (currentPostion < audioList.size() - 1) {
                        currentPostion += 1;
                        playVideoByPostion(audioList.get(currentPostion));
                    }
                }

                @Override
                public void onPause(BJPlayerView playerView) {
                    if (mStatisticsUtils != null) {
//                        if (playerView.isPlaying())
                        mStatisticsUtils.onVideoStop(playerView.getCurrentPosition());
//                        else
//                            mStatisticsUtils.startCollectPlayEvent(playerView.getCurrentPosition(), playerView.getDuration());
                    }
                    super.onPause(playerView);
                }

                @Override
                public void onVideoPrepared(BJPlayerView playerView) {
                    if (mStatisticsUtils != null)
                        mStatisticsUtils.startCollectPlayEvent(playerView.getCurrentPosition(), playerView.getDuration());
                    super.onVideoPrepared(playerView);
                }

            });

        } else if (miniScreenView.getVisibility() == View.GONE)
            miniScreenView.setVisibility(View.VISIBLE);

        if(detailShowLayout != null && detailShowLayout.getVisibility() == View.VISIBLE)
            detailShowLayout.setVisibility(View.GONE);
    }

    private void inflateCourseActiveVs() {
        if (detailShowLayout == null) {
            detailShowLayout = (LinearLayout) mCourseActiveVs.inflate();
            hintFront = detailShowLayout.findViewById(R.id.onsale_hint_text);
            detailLimitText = detailShowLayout.findViewById(R.id.onsal_hint_real_info);
        } else if (detailShowLayout.getVisibility() == View.GONE)
            detailShowLayout.setVisibility(View.VISIBLE);
    }

    //更新时间
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 66666:
                    if (isGroupBuy && isBuyed && !isGroupBuyEnd) {
                        setGroupBuyime();
//
                    }
                    break;
                case 88888:
                    setEndTime();
//
                    break;
                case 99999:
                    setStartTime();
//
                    break;
            }
        }
    };

    //点击试听接收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || Utils.isEmptyOrNull(intent.getAction()))
                return;
            if (intent.getAction().equals("action.outline.trial.video")) {
                CourseOutlineItemBean bean = (CourseOutlineItemBean) intent.getSerializableExtra("CourseBean");
                if (audioList == null || bean == null)
                    return;
//                bean.videoType = currentPlayType == 1 ? 5 : 1;
                playVideoByPostion(bean);
                int position = audioList.indexOf(bean);
                if (position != -1)
                    currentPostion = position;
            }
        }
    };

    //appBar大小变化
    private AppBarLayout.OnOffsetChangedListener mAppBarStateChangeListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            int padding = (int) Math.abs(i * 100f / appBarLayout.getTotalScrollRange());
            ((CommonNavigator) mMagicIndicator.getNavigator()).setPadding(padding, 0, padding, 0);
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT && mNaviTitleSize == 16)
                return;
            int size = olderOrientation == 2 ? 16 : 16 - (int) Math.abs(i * 4f / appBarLayout.getTotalScrollRange());
            if (size != mNaviTitleSize) {
                mNaviTitleSize = size;
                ((CommonNavigator) mMagicIndicator.getNavigator()).getAdapter().notifyDataSetChanged();
            }
        }
    };
}
