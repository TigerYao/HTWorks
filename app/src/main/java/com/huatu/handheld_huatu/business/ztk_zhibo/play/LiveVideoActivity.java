//package com.huatu.handheld_huatu.business.ztk_zhibo.play;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.Drawable;
//import android.media.AudioManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Message;
//import android.os.PowerManager;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.AccelerateInterpolator;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.huatu.handheld_huatu.R;
//import com.huatu.handheld_huatu.base.BaseListResponseModel;
//import com.huatu.handheld_huatu.base.BaseResponseModel;
//import com.huatu.handheld_huatu.base.NetObjResponse;
//import com.huatu.handheld_huatu.base.NetResponse;
//import com.huatu.handheld_huatu.base.SimpleBaseActivity;
//import com.huatu.handheld_huatu.base.adapter.MyCustomFragmentPagerAdapter;
//import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
//import com.huatu.handheld_huatu.business.me.FeedbackActivity;
//import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
//import com.huatu.handheld_huatu.business.ztk_vod.OnCoursePlaylistener;
//import com.huatu.handheld_huatu.business.ztk_vod.ShareDialogFragment;
//import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseCatalogFragment;
//import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseHandoutFragment;
//import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseLocallistFragment;
//import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseMoreFragment;
//import com.huatu.handheld_huatu.business.ztk_vod.fragment.OnPlaySelectListener;
//import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
//import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadListActivity;
//import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoCenterView;
//import com.huatu.handheld_huatu.business.ztk_zhibo.play.view.LiveVideoHeaderView;
//import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
//import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
//import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
//import com.huatu.handheld_huatu.mvpmodel.zhibo.LastCourseBean;
//import com.huatu.handheld_huatu.network.ServiceExProvider;
//import com.huatu.handheld_huatu.network.ServiceProvider;
//import com.huatu.handheld_huatu.network.api.CourseApiService;
//import com.huatu.handheld_huatu.ui.DownBtnLayout;
//import com.huatu.handheld_huatu.ui.ProgressTextView;
//import com.huatu.handheld_huatu.utils.ArgConstant;
//import com.huatu.handheld_huatu.utils.CommonUtils;
//import com.huatu.handheld_huatu.utils.ImageLoad;
//import com.huatu.handheld_huatu.utils.LogUtils;
//import com.huatu.handheld_huatu.utils.Method;
//import com.huatu.handheld_huatu.utils.NetUtil;
//import com.huatu.handheld_huatu.utils.ToastUtils;
//import com.huatu.handheld_huatu.utils.UserInfoUtil;
//import com.huatu.popup.QuickListAction;
//import com.huatu.scrollablelayoutlib.ScrollableHelper;
//import com.huatu.scrollablelayoutlib.ScrollableLayout;
//import com.huatu.utils.ArrayUtils;
//import com.huatu.utils.DensityUtils;
//import com.huatu.utils.StringUtils;
//import com.huatu.widget.IncreaseProgressBar;
//import com.umeng.socialize.UMShareAPI;
//
//import net.lucode.hackware.magicindicator.MagicIndicator;
//import net.lucode.hackware.magicindicator.buildins.UIUtil;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
//import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import rx.functions.Action1;
//
//@Deprecated
//public class LiveVideoActivity extends SimpleBaseActivity implements OnClickListener,
//        OnCoursePlaylistener {
//    private PowerManager.WakeLock m_wakeLock;
//
////    @BindView(R.id.video_play_loading_pb_layout)
////    LiveLoadingLayout mLayoutLoading;
//
//    @BindView(R.id.live_title_layout)
//    LiveVideoHeaderView layoutTitle;
//
//    @BindView(R.id.layout_live_video_center)
//    LiveVideoCenterView mVideoCenterLayout;
//
////    @BindView(R.id.live_title_tv)
////    TextView textView_TitleBar_Info;
//    @BindView(R.id.start_play_btn)
//    Button mStartPlayBtn;
//    @BindView(R.id.ll_start_play_btn)
//    RelativeLayout mLLStartBtn;
//    @BindView(R.id.ll_counttime_bottom)
//    RelativeLayout mLlCountLayout;
//    @BindView(R.id.tv_counttime_time)
//    TextView mTvCountTime;
//    @BindView(R.id.rl_start_play_live)
//    ImageView mIvPlayIcon;
//    @BindView(R.id.tv_counttime_string)
//    TextView mTvContTimeDes;
//    @BindView(R.id.tv_tips_datas)
//    TextView mTvTips;
//    ImageView mImageCover;
//
//    private ImageView mCenterPlayBtn;
//    CourseCatalogFragment mCataLogFragment;
//    OnPlaySelectListener mSelectListener;
//    private String mCourseId;
//    private String mImgCoverPath;
//
//    private int lastPlayTime = 0;  //用来记录最后的播放位置
//    private SharedPreferences sharedPreferences;
//    private boolean mIsLocalVideo = false;
//    private List<CourseWareInfo> mRecordLessionlist;
//    private CourseInfoBean mRecordCourseInfo;
//    private CountDownHandler mCountHandler;
//    private static final int TIME_INTERVAL = 1000;
//    private CourseWareInfo mCurrentLesson;
//    private boolean mShowNoNet = false;
//    private boolean isFromOffLine;
//
//    private boolean mHasPlay=false;//是否有点击观看课程
//    private boolean mStartForResult=false;
//
//
//    public static void lanuchForLocal(Context context, int position, String courseId, DownLoadLesson cuLession) {
//        Intent intent = new Intent(context, LiveVideoActivity.class);
//        CourseWareInfo info = new CourseWareInfo();
//        info.offSignalFilePath = cuLession.getSignalFilePath();
//        info.targetPath = cuLession.getPlayPath();
//        info.title = cuLession.getSubjectName();
//        info.downStatus = 2;
//        info.bjyRoomId = cuLession.getRoomId();
//        info.bjySessionId = cuLession.getSessionId();
//        info.token = cuLession.getVideoToken();
//        info.videoType =3;// cuLession.getPlayerType();此处类型要转换
//        info.liveStatus = 1;
//        info.coursewareId = Integer.parseInt(cuLession.getSubjectID());
//        String imgPath = cuLession.getImagePath();
//        intent.putExtra(ArgConstant.BEAN, info);
//        intent.putExtra("course_id", courseId);
//        intent.putExtra("from_off_line", true);
//        intent.putExtra("play_index", position);
//        intent.putExtra("imgPath", imgPath);
//        intent.putExtra(ArgConstant.IS_LOCAL_VIDEO, true);
//        context.startActivity(intent);
//    }
//    @Override
//    protected int onSetRootViewId() {
//        return R.layout.activity_live_video;
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (savedInstanceState != null) {
//            String FRAGMENT_TAGS = "android:support:fragments";
//            savedInstanceState.remove(FRAGMENT_TAGS);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0+ 打开硬件加速
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
//                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
//        //修复某些机器上surfaceView导致的闪黑屏的bug
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        super.onCreate(savedInstanceState);
//        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        if (!EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().register(this);
//        }
//    }
//
//    @Subscribe
//    public void onDownloadFinish(CourseWareInfo info){
//        if (info == null)
//            return;
//        if (mCurrentLesson != null && mCurrentLesson.coursewareId == info.coursewareId) {
//            mCurrentLesson = info;
//            isFromOffLine = info.downStatus == DownBtnLayout.FINISH;
//        }
//    }
//
//    @Override
//    protected void onInitView() {
//        super.onInitView();
//        //  setSupportProgress(true);
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        m_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "cn");
//        initData();
//        initViews();
//        initView();
//        initListener();
//        if (!NetUtil.isConnected() && !mIsLocalVideo)
//            showNoNetWrok();
//        else if (mCurrentLesson != null){
//            onSelectPlayClick(mCurrentLesson, false);
//        }else
//            getLastLession();
//    }
//
//    private void initData() {
//        mStartForResult=getIntent().getBooleanExtra(ArgConstant.FOR_RESUTL,false);
//        mCourseId = originIntent.getStringExtra("course_id");
//        //videoInfoStr = originIntent.getStringExtra("video_info_str");
//        isFromOffLine = originIntent.getBooleanExtra("from_off_line", false);
//        if (TextUtils.isEmpty(mCourseId)) {
//            LogUtils.e("courseId is null");
//            finish();
//            return;
//        }
//        mIsLocalVideo = getIntent().getBooleanExtra(ArgConstant.IS_LOCAL_VIDEO, false);
//        mCurrentLesson = (CourseWareInfo) getIntent().getSerializableExtra(ArgConstant.BEAN);
//        mImgCoverPath = originIntent.getStringExtra("imgPath");
//        if (isFromOffLine && mCurrentLesson != null)
//            startToRealLiveActivity(mCurrentLesson);
//
//    }
//
//    private CourseWareInfo mLastWareInfo;
//
//    public void getLastLession() {
//        ServiceProvider.getLastPlay(getSubscription(), mCourseId, new NetResponse() {
//            @Override
//            public void onSuccess(BaseResponseModel model) {
//                LastCourseBean lastCourseBean = (LastCourseBean) model.data;
//                mLastWareInfo = lastCourseBean.getLastCouseInfo();
//                onSelectPlayClick(mLastWareInfo, false);
////                super.onSuccess(model);
//                LogUtils.d("LiveVideoAc", model.data.toString());
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//
//            @Override
//            public void onListSuccess(BaseListResponseModel model) {
//                super.onListSuccess(model);
//            }
//        });
//    }
//
//    class CountDownHandler extends android.os.Handler {
//        private WeakReference<Activity> weakReference;
//        private int status;
//
//        public CountDownHandler(WeakReference<Activity> weakReference, int status) {
//            this.weakReference = weakReference;
//            this.status = status;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Activity activity = weakReference.get();
//            if (activity == null || activity.isFinishing() || msg.what != 1021) {
//                removeCallbacksAndMessages(null);
//                return;
//            }
//            CourseWareInfo info = (CourseWareInfo) msg.obj;
//            if (info.videoType == 2 && info.liveStatus == 0) {
//                LogUtils.d("BJRecordPlayActivity", "length=" + info.liveStart);
//                if (status == 1 && info.liveStart <= 24 * 60 * 60) {
//                    status = 0;
//                    if (mTvContTimeDes != null)
//                        mTvContTimeDes.setText("倒计时：");
//                }
//
//                if (mTvCountTime != null)
//                    mTvCountTime.setText(getCaculteTime(info.liveStart));
//                if (info.liveStart == 0) {
//                    info.liveStatus = 1;
//                    mCurrentLesson = info;
//                    removeMessages(1021);
//                    setLiveAlreadyStatus(info);
//                    return;
//                }
//                info.liveStart = info.liveStart - 1;//(info.liveStart > 24 * 60 * 60 ? 60 : 1);
//                Message newMsg = obtainMessage();
//                newMsg.obj = info;
//                newMsg.what = 1021;
//                sendMessageDelayed(newMsg, 1000);
//            }
//        }
//    }
//
//    private String getCaculteTime(int currentTime) {
//        int day = currentTime / (60 * 60 * 24);
//        int hour = (currentTime % (60 * 60 * 24)) / (60 * 60);
//        int minuteTime =  day > 0 ? (currentTime + 60) : currentTime;
//        int minute = (minuteTime % (60 * 60)) / 60;
//        int second = (currentTime % 60);
//        String time = (hour >= 10 ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
//        if (day > 0) {
//            time = day + "天" + (hour >= 10 ? hour : "0" + hour) + "小时" + (minute >= 10 ? minute : "0" + minute) + "分";
//        }
//        return time;
//    }
//
//    // 设置直播中状态
//    private void setLiveAlreadyStatus(CourseWareInfo courseWareInfo) {
////        textView_TitleBar_Info.setVisibility(View.VISIBLE);
////        textView_TitleBar_Info.setText(courseWareInfo.title);
//        mLLStartBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setText("直播中，立即观看");
//        mStartPlayBtn.setBackgroundResource(R.drawable.play_btn_playing);
//        Drawable drawable = getResources().getDrawable(R.drawable.videoicon);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        mStartPlayBtn.setCompoundDrawables(null, null, drawable, null);
//        mLlCountLayout.setVisibility(View.GONE);
//        layoutTitle.setVisibility(View.VISIBLE);
//
//    }
//
//    // 设置继续播放状态
//    private void setLiveContinueStatus(boolean isContinue, Activity activity, CourseWareInfo courseWareInfo) {
////        textView_TitleBar_Info.setVisibility(View.VISIBLE);
////        textView_TitleBar_Info.setText(courseWareInfo.title);
//        mLLStartBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setText(isContinue ? "继续学习" : "开始学习");
//        mStartPlayBtn.setBackgroundResource(R.drawable.play_btn_bg_selector);
//        Drawable drawable = activity.getResources().getDrawable(R.drawable.videoicon);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        mStartPlayBtn.setCompoundDrawables(null, null, drawable, null);
//        layoutTitle.setVisibility(View.VISIBLE);
//        showWarn(courseWareInfo);
//    }
//
//
//    // 设置直播状态,如果是直播是倒计时状态
//    private void setLiveStatus(CourseWareInfo courseWareInfo) {
//        if (mCurrentLesson != null && !mShowNoNet && TextUtils.equals(courseWareInfo.id, mCurrentLesson.id))
//            return;
//        if (mCurrentLesson != null && mLastWareInfo != null && TextUtils.equals(mLastWareInfo.id, courseWareInfo.id)) {
//            courseWareInfo.liveStart = Math.min(mLastWareInfo.liveStart, courseWareInfo.liveStart);
//        }
//        if (mCountHandler != null)
//            mCountHandler.removeMessages(1021);
//        mCurrentLesson = courseWareInfo;
//        LogUtils.e("BJRecordPlayActivity", "liveStatus");
////        textView_TitleBar_Info.setVisibility(View.VISIBLE);
////        textView_TitleBar_Info.setText(courseWareInfo.title);
//        mLlCountLayout.setVisibility(View.VISIBLE);
//        mTvCountTime.setVisibility(View.VISIBLE);
//        mTvTips.setVisibility(View.GONE);
//        int status = 0;
//        mLLStartBtn.setVisibility(View.VISIBLE);
//        mTvContTimeDes.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setText("等待直播");
//        mStartPlayBtn.setBackgroundResource(R.drawable.play_btn_bg_selector);
//        Drawable drawable = getResources().getDrawable(R.drawable.ic_loading_wait);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        mStartPlayBtn.setCompoundDrawables(null, null, drawable, null);
//        if (courseWareInfo.liveStart >= 24 * 60 * 60) {
//            status = 1;
//            mTvContTimeDes.setText("距离直播开始还有：");
//        } else {
//            status = 0;
//            mTvContTimeDes.setText("倒计时：");
//        }
//        mTvCountTime.setText(getCaculteTime(courseWareInfo.liveStart));
//        if (mCountHandler == null) {
//            mCountHandler = new CountDownHandler(new WeakReference<Activity>(this), status);
//        }
//        Message msg = mCountHandler.obtainMessage();
//        msg.what = 1021;
//        msg.obj = courseWareInfo;
//        mCountHandler.sendMessage(msg);
//        layoutTitle.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public List<CourseWareInfo> getRecordList() {
//        if (mRecordLessionlist == null) {
//            mRecordLessionlist = new ArrayList<>();
//        }
//        return mRecordLessionlist;
//    }
//
//    public CourseInfoBean getCourseInfo() {
//        return mRecordCourseInfo;
//    }
//
//
//    @Override
//    protected void onLoadData() {
//        super.onLoadData();
//        //获取数据
//        initCourseSylData();
//    }
//
//
//    private void bindUIData(CourseInfoBean dataInfo) {
//        try {
//            ((TextView) this.findViewById(R.id.course_title_txt)).setText(dataInfo.title);
//          /*  if(mBjCenterViewPresenter!=null)
//                ImageLoad.displaynoCacheImage(this,R.drawable.trans_bg,mCourseCover,mBjCenterViewPresenter.getCoverImageView());*/
//            if (TextUtils.isEmpty(dataInfo.androidFunction)) {
//                this.findViewById(R.id.add_qq_btn).setVisibility(View.GONE);
//            }
//            int percent = StringUtils.parseInt(dataInfo.schedule.replace("%", ""));
//            ((ProgressTextView) this.findViewById(R.id.txt_progress)).setTextProgress(String.format("已直播%d", percent) + "%", percent);
//            ((IncreaseProgressBar) this.findViewById(R.id.learn_progressbar)).setCurProgress2(percent);
//            ImageLoad.displaynoCacheImage(this,R.drawable.trans_bg, mImgCoverPath, mImageCover);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void initCourseSylData() {
//        if (TextUtils.isEmpty(mCourseId)) {
//            return;
//        }
//        showProgress();
//        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getCourseLearnPercent(mCourseId), new NetObjResponse<CourseInfoBean>() {
//            @Override
//            public void onError(String message, int type) {
//                hideProgess();
//                ToastUtils.showMessage(message);
//            }
//
//            @Override
//            public void onSuccess(BaseResponseModel<CourseInfoBean> model) {
//                hideProgess();
//                mRecordCourseInfo = model.data;
//                mImgCoverPath = mRecordCourseInfo.scaleimg;
//                bindUIData(model.data);
//
//            }
//        });
//        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getAllCourseWare(StringUtils.parseLong(mCourseId), 1, 100),
//                new NetObjResponse<CourseWareBean>() {
//                    @Override
//                    public void onError(String message, int type) {
//                        // hideProgress();
//                        ToastUtils.showMessage(message);
//                    }
//
//                    @Override
//                    public void onSuccess(BaseResponseModel<CourseWareBean> model) {
//                        //  hideProgress();
//                        mRecordLessionlist = model.data.list;
//                    }
//                });
//    }
//
//    private void initViews() {
//        sharedPreferences = getSharedPreferences(UserInfoUtil.userName, MODE_PRIVATE);
//        mCenterPlayBtn = (ImageView) findViewById(R.id.rl_start_play_live);
//        mImageCover = (ImageView) findViewById(R.id.image_live_detail);
//        mIvPlayIcon.setVisibility(View.GONE);
//        mImageCover.setBackgroundResource(R.mipmap.play_default_cover);
//    }
//
//    private void initView() {
//        this.findViewById(R.id.filter_img_btn).setOnClickListener(this);
//        this.findViewById(R.id.down_img_btn).setOnClickListener(this);
//        this.findViewById(R.id.add_qq_btn).setOnClickListener(this);
//        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
//        ScrollableLayout mScrollLayout = (ScrollableLayout) this.findViewById(R.id.scrollableLayout);
//        MagicIndicator pagerSlidingTabStrip = (MagicIndicator) this.findViewById(R.id.magic_indicator2);
//        initMagicIndicator2(viewPager, pagerSlidingTabStrip, mScrollLayout);
//    }
//
//    private void initMagicIndicator2(final ViewPager viewPager, final MagicIndicator magicIndicator, final ScrollableLayout mScrollLayout) {
//        final String[] mDataList = new String[]{"目录", "讲义", "更多"};//, "讲义", "更多"
//        //MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator2);
//        magicIndicator.setBackgroundColor(Color.WHITE);
//        CommonNavigator commonNavigator = new CommonNavigator(this);
//        commonNavigator.setAdjustMode(true);
//        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return mDataList.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, final int index) {
//                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
//                simplePagerTitleView.setText(mDataList[index]);
//                simplePagerTitleView.setTextSize(16);
//                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
//                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
//                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        viewPager.setCurrentItem(index);
//                    }
//                });
//                return simplePagerTitleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
//                indicator.setStartInterpolator(new AccelerateInterpolator());
//                indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
//                indicator.setLineHeight(UIUtil.dip2px(context, 4));
//                indicator.setYOffset(DensityUtils.dp2px(context, 10));
//                indicator.setColors(Color.parseColor("#FF6D73"));
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(commonNavigator);
//        final ArrayList<Fragment> fragmentList = new ArrayList<>();
//
//        if (mIsLocalVideo) {
//            CourseLocallistFragment courseLocallistFragment = CourseLocallistFragment.getInstance(mCourseId,mCurrentLesson == null ? 0 : mCurrentLesson.coursewareId);
//            fragmentList.add(courseLocallistFragment);
//            mSelectListener = courseLocallistFragment;
//        } else {
//            mSelectListener = mCataLogFragment = CourseCatalogFragment.getInstance(mCourseId, mCurrentLesson == null ? 0 : mCurrentLesson.coursewareId);
//            fragmentList.add(mCataLogFragment);
//        }
//        fragmentList.add(CourseHandoutFragment.getInstance(mCourseId, mIsLocalVideo));
//        fragmentList.add(CourseMoreFragment.getInstance(mCourseId, 1));
//        //  fragmentList.add(CourseCatalogFragment.getInstance(classid));
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                magicIndicator.onPageSelected(position);
//                mScrollLayout.getHelper().setCurrentScrollableContainer((ScrollableHelper.ScrollableContainer) fragmentList.get(position));
//                if (position >= 1) {
//                    Fragment curFrag = fragmentList.get(position);
//                    if (curFrag instanceof IPageStripTabInitData)
//                        ((IPageStripTabInitData) curFrag).onStripTabRequestData();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                magicIndicator.onPageScrollStateChanged(state);
//            }
//        });
//        viewPager.setAdapter(new MyCustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, java.util.Arrays.asList(mDataList)));
//        mScrollLayout.getHelper().setCurrentScrollableContainer((ScrollableHelper.ScrollableContainer) fragmentList.get(0));
//        viewPager.setCurrentItem(0);
//    }
//
//    private void startToRealLiveActivity(CourseWareInfo lessionInfo) {
//       mHasPlay=true;
//        if(mRecordCourseInfo!=null){
//           CourseApiService.getGoldReWard(getSubscription(),mRecordCourseInfo.isFree);
//        }
//        if (lessionInfo == null)
//            return;
//        if(lessionInfo.videoType== 1){
//            if(mRecordCourseInfo==null){
//                return;
//            }
//            BJRecordPlayActivity.lanuchForOnlive(this,mCourseId,mRecordCourseInfo.title,mRecordCourseInfo.scaleimg,lessionInfo, true);
//            this.finish();
//            return;
//        }else if (lessionInfo.tinyLive == 1) {
//            CommonUtils.startLiveRoom(getSubscription(),this,  String.valueOf(lessionInfo.classId), lessionInfo.coursewareId+"", lessionInfo.parentId,lessionInfo.joinCode, lessionInfo.bjyRoomId, lessionInfo.sign);
////            LiveRoomActivity.lanuchForResult(this, mCourseId, lessionInfo.classId,lessionInfo.parentId,lessionInfo.coursewareId, 1, lessionInfo.joinCode);
//        } else {
//           LiveVideoForLiveActivity.startForResult(this, mCourseId, getRecordList().indexOf(mCurrentLesson), isFromOffLine, mCurrentLesson, mImgCoverPath);
//        }
//    }
//
//    // 移除状态
//    private void setInitStatus() {
//        mStartPlayBtn.setVisibility(View.GONE);
//        mLlCountLayout.setVisibility(View.GONE);
//        mLLStartBtn.setVisibility(View.GONE);
////        textView_TitleBar_Info.setVisibility(View.GONE);
//        if (mCountHandler != null) {
//            mCountHandler.removeMessages(1021);
//            mCountHandler.removeCallbacksAndMessages(null);
//        }
//    }
//
//
//    public void onClickStartPlay(CourseWareInfo lessionInfo) {
//        if (!NetUtil.isConnected()&& !isFromOffLine) {
//            ToastUtils.showMessage("您无法连接到网络， 请确认网络链接后重试");
//            return;
//        }
//
//        if (mShowNoNet) {
//            if (mCurrentLesson == null)
//                getLastLession();
//            else
//                onSelectPlayClick(mCurrentLesson, false);
//            return;
//        }
//        if (lessionInfo == null)
//            return;
//        //通过界面的点击的事件，直接进行跳转
//        if (lessionInfo.videoType == 2) {
//            if (lessionInfo.liveStatus == 1) {
//                startToRealLiveActivity(lessionInfo);
//            }
//        } else if (lessionInfo.videoType == 3 || lessionInfo.videoType == 1) {
//            startToRealLiveActivity(lessionInfo);
//        }
//
//    }
//
//    //1点播2直播3直播回放
//    @Override
//    public void onSelectPlayClick(CourseWareInfo lessionInfo, boolean isRefreshDialog) {
//        if (lessionInfo == null)
//            return;
//
//        if (mSelectListener != null)
//            mSelectListener.onSelectChange(lessionInfo.coursewareId,0);
//        isFromOffLine = lessionInfo.downStatus== DownBtnLayout.FINISH;
//        if (!NetUtil.isConnected() && !isFromOffLine) {
//            showNoNetWrok();
//            return;
//        }
//
//        // 直播回放进行跳转
//        if (lessionInfo.videoType == 3 || lessionInfo.videoType == 1) {
//            lastPlayTime = sharedPreferences.getInt(lessionInfo.coursewareId + "", 0);
//            setLiveContinueStatus(lastPlayTime > 0 || lessionInfo.lastStudy == 1, this, lessionInfo);
//            if (isRefreshDialog) {
//                mCurrentLesson = lessionInfo;
//                mShowNoNet = false;
//                startToRealLiveActivity(lessionInfo);
//                return;
//            }
//        } else if (lessionInfo.videoType == 2) {
//            if (lessionInfo.liveStatus == 0) { // 未开始
//                // 显示直播未开始倒计时
//                setLiveStatus(lessionInfo);
//                ToastUtils.showShort("直播未开始");
//            } else if (lessionInfo.liveStatus == 1) { // 直播中
//                // 显示直播中状态
//                setLiveAlreadyStatus(lessionInfo);
//                if (isRefreshDialog) {
//                    mCurrentLesson = lessionInfo;
//                    mShowNoNet = false;
//                    startToRealLiveActivity(lessionInfo);
//                    return;
//                }
//            } else if (lessionInfo.liveStatus == 2) { // 已结束
//                // 提交直播已结束提示
//                setInitStatus();
////                textView_TitleBar_Info.setText(lessionInfo.title);
//                ToastUtils.showShort("直播已结束");
//            }
//        } else {
//            // 回复初始状态
//            setInitStatus();
////            layoutTitle.setTitle(StringUtils.valueOf(lessionInfo.title));
//            mCenterPlayBtn.setVisibility(View.GONE);
//        }
//        mShowNoNet = false;
//        if (mCurrentLesson == null || !TextUtils.equals(lessionInfo.id, mCurrentLesson.id))
//            mCurrentLesson = lessionInfo;
//
//    }
//
//
//    private void initListener() {
//        mVideoCenterLayout.setOnClickListener(this);
//        layoutTitle.setOnClickListener(this);
//        mLLStartBtn.setOnClickListener(this);
//        mStartPlayBtn.setOnClickListener(this);
//        mCenterPlayBtn.setOnClickListener(this);
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        m_wakeLock.release();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        m_wakeLock.acquire();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.live_title_more_btn:
//                showTitlePopWindow(v);
//                break;
//
//            case R.id.image_live_back:
//                onClickBack();
//                break;
//            case R.id.ll_start_play_btn:
//            case R.id.start_play_btn:
//                onClickStartPlay(mCurrentLesson);
//                break;
//
//            case R.id.add_qq_btn:
//                if (mRecordCourseInfo != null && !TextUtils.isEmpty(mRecordCourseInfo.androidFunction)) {
//                    Method.joinQQGroup(mRecordCourseInfo.androidFunction);
//                }
//                break;
//            case R.id.filter_img_btn:
//                showFilterPopWindow(v);
//                break;
//
//            case R.id.down_img_btn:
//                if ((!mIsLocalVideo) && ArrayUtils.isEmpty(mRecordLessionlist)) {
//                    ToastUtils.showShortToast(R.string.no_lessiondown_tip);
//                    return;
//                }
//                CommonUtils.checkPowerAndTraffic(this, new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        DownLoadListActivity.lanuch(LiveVideoActivity.this, mRecordCourseInfo,0);
//                    }
//                });
//
//                break;
//        }
//    }
//
//    QuickListAction msgFilterActons;
//
//    private void showFilterPopWindow(View anchor) {
//        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
//        if(viewPager!=null&&(viewPager.getCurrentItem()!=0)){
//            viewPager.setCurrentItem(0,false);
//        }
//        if (msgFilterActons == null) {
//            msgFilterActons = new QuickListAction(this, R.layout.course_record_filter_popup, R.id.root);
//            msgFilterActons.setForceOnBottom();
//            msgFilterActons.getRootView().findViewById(R.id.pop_menu_upreport).setVisibility(View.GONE);
//
//            msgFilterActons.setAnimStyle(R.style.Animations_PopDownMenu_Center);
//            msgFilterActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
//                @Override
//                public void onItemViewClick(int position, View view) {
//                    switch (position) {
//                        case 0:
//                            msgFilterActons.dismiss();
//                            view.setSelected(!view.isSelected());
//                            if (mCataLogFragment != null && mCataLogFragment.isAdded()) {
//                                mCataLogFragment.showAfterClass(view.isSelected());
//                            }
//                            break;
//                        case 1:
//                            msgFilterActons.dismiss();
//                            view.setSelected(!view.isSelected());
//                            if (mCataLogFragment != null && mCataLogFragment.isAdded()) {
//                                mCataLogFragment.showFilterLocal(view.isSelected());
//                            }
//                            break;
//                    }
//                }
//            });
//
//            msgFilterActons.show(anchor);
//        } else
//            msgFilterActons.Reshow(anchor);
//    }
//
//
//    QuickListAction msgActons;
//
//    private void showTitlePopWindow(View anchor) {
//        if (msgActons == null) {
//            msgActons = new QuickListAction(this, R.layout.pop_product_more_views, R.id.root);
//            msgActons.setAnimStyle(R.style.Animations_PopDownMenu_Right);
//            msgActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
//                @Override
//                public void onItemViewClick(int position, View view) {
//                    switch (position) {
//                        case 0:
//                            msgActons.dismiss();
//                            ShareDialogFragment fragment = ShareDialogFragment.getInstance(mCourseId, "");
//                            fragment.show(getSupportFragmentManager(), "share");
//                            break;
//                        case 1:
//                            msgActons.dismiss();
//                            FeedbackActivity.newInstance(LiveVideoActivity.this);
//                            break;
//                    }
//                }
//            });
//            msgActons.show(anchor);
//        } else
//            msgActons.Reshow(anchor);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (requestCode == 0x00102 && resultCode == RESULT_OK){
//            mCurrentLesson = (CourseWareInfo) intent.getSerializableExtra(ArgConstant.BEAN);
//            if (mCurrentLesson != null) {
//                onSelectPlayClick(mCurrentLesson, false);
//                mSelectListener.onSelectChange(mCurrentLesson.coursewareId,0);
//            }
//            onLoadData();
//        }else if(requestCode == 0x00101 && resultCode == RESULT_OK){
//            int courseWareId= intent.getIntExtra(ArgConstant.KEY_ID, 0);
//            int hasPlayTime =intent.getIntExtra(ArgConstant.LOCAL_PATH, 0);
//            LogUtils.e("test",hasPlayTime+"");
//            if(mSelectListener!=null)
//                  mSelectListener.onSelectChange(courseWareId,hasPlayTime/1000);
//        }
//        else
//            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        onClickBack();
//    }
//
//    public void onClickBack() {
//        if(mStartForResult&&mHasPlay){
//            setResult(Activity.RESULT_OK);
//        }
//        finish();
//    }
//
//    public void showWarn(CourseWareInfo info) {
//        if (!NetUtil.isWifi() && !isFromOffLine) {
//            mLlCountLayout.setVisibility(View.VISIBLE);
//            mTvTips.setVisibility(View.VISIBLE);
//            mTvCountTime.setVisibility(View.GONE);
//            mTvContTimeDes.setVisibility(View.GONE);
//            String netDatas = (info.getfileSize() / 1024 / 1024) + "M";
//            mTvTips.setText(NetUtil.isConnected() ? "提示：即将消耗" + netDatas + "手机流量" : "您无法连接到网络， 请确认网络链接后重试");
//        } else {
//            mLlCountLayout.setVisibility(View.GONE);
//            mTvTips.setText("");
//        }
//    }
//
//    public void showNoNetWrok() {
//        mLLStartBtn.setVisibility(View.VISIBLE);
//        mStartPlayBtn.setVisibility(View.VISIBLE);
//        mLlCountLayout.setVisibility(View.VISIBLE);
//        mTvTips.setVisibility(View.VISIBLE);
//        mTvCountTime.setVisibility(View.GONE);
//        mTvContTimeDes.setVisibility(View.GONE);
//        mTvTips.setText("您无法连接到网络， 请确认网络链接后重试");
//        mStartPlayBtn.setText("点击刷新");
//        mStartPlayBtn.setBackgroundResource(R.drawable.play_btn_playing);
//        Drawable drawable = getResources().getDrawable(R.drawable.icon_redown);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        mStartPlayBtn.setCompoundDrawables(null, null, drawable, null);
//        mShowNoNet = true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        UMShareAPI.get(this).release();
//        if (mCountHandler != null) {
//            mCountHandler.removeMessages(1021);
//            mCountHandler.removeCallbacksAndMessages(null);
//            mCountHandler = null;
//        }
//        ToastUtils.cancle();
//    }
//}
