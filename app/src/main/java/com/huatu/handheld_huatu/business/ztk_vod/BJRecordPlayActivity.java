package com.huatu.handheld_huatu.business.ztk_vod;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.MyAddFragmentPagerAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseCatalogFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseHandoutGroupFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseLocallistFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseMoreFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.OnAfterSelectListener;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.OnPlaySelectListener;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.TeacherCourseListFragment;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.WeChatGroupDialogFragment;
import com.huatu.handheld_huatu.business.ztk_vod.view.OnShowInclassListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadListActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseJudgeActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.helper.SmoothCommonNavigatorAdapter;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.SimpleBjPlayerStatusListener;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LastCourseBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.BJPlayerTouchView;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.ScrollLinearLayout;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.music.IMusicService;
import com.huatu.music.bean.Music;
import com.huatu.music.event.MetaChangedEvent;
import com.huatu.music.event.StatusChangedEvent;
import com.huatu.music.player.BgPlayContract;
import com.huatu.music.player.BgPlayPresenter;
import com.huatu.music.player.MusicPlayerService;
import com.huatu.music.player.PlayManager;
import com.huatu.music.utils.LogUtil;
import com.huatu.popup.QuickListAction;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.scrollablelayoutlib.ScrollableLayout;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.umeng.socialize.UMShareAPI;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

import static com.huatu.music.player.PlayManager.mService;

/**
 * Created cjx
 * 百家云录播播放页面
 */

public class BJRecordPlayActivity extends SimpleBaseActivity implements View.OnClickListener,OnShowInclassListener
        ,DanmakuInputDialogBuilder.OnDanmaSendListener,AfterclassPracticeDialog.onAfterPracticeListener ,OnCoursePlaylistener
        ,CatalogFilterPopup.OnCatalogFilterChange, ServiceConnection,MusicPlayContract.View ,PlayStatistPresenter.View  {

    private final static String GROUPNAME="record_play";

    @BindView(R.id.videoView)
    BJPlayerTouchView mPlayerView;

    @BindView(R.id.rootView)
    ScrollLinearLayout mScrollContainer;

    @BindView(R.id.scrollableLayout)
    ScrollableLayout mScrollableLayout;

    Button mStartPlayBtn;

    //课程 id title img
    private String mCourseId, mCourseName,mCourseCover;
   // private String mLocalPlayPath;

    //录播播放位置
    private int   mCurrentPlayIndex=-1;


    //退出时保存的播放进度
    private int  mLastSaveTime;


    private boolean isQuickPlay;
    private boolean mIsLocalVideo=false;
    private int mCourseWareId=0;

    private List<CourseWareInfo> mRecordLessionlist;
    private CourseInfoBean  mRecordCourseInfo;

    AudioManager mAudioManager;
    BJCenterViewExPresener mBjCenterViewPresenter;
    BJBottomViewImpl  mBjBottomViewPresenter;
    BJTopViewImpl     mBjTopViewPresenter;

    MusicPlayPresenter mMusicPresenter;
    BgPlayPresenter    mBgPlayPresenter;

    private CourseWareInfo mPlayingCourseWare;

    @Override
    public CourseWareInfo getPlayingCourseWare(){

        //有直播存在
        if(null!=mOnLiveCourseWare) return mOnLiveCourseWare;
        return mPlayingCourseWare;
    }
    private CourseWareInfo mOnLiveCourseWare;//直播或者等待的课件

    private boolean mDialogSelectChange=false;
    private CourseCatalogFragment mCataLogFragment;
    private boolean mHasLastPlay=false;
    private boolean mIsPlayFinished=false;
    private boolean mHasPlay=false;//是否已播放
    private boolean mStartForResult=false;

    private SharedPreferences mReadPrefStore;
    private VideoStatisticsUtil mStatisticsUtil;

    PlayStatistPresenter mPlayStatPresenter;

    public Boolean mIsPause = true;

    private boolean mToHome=false;

    public List<SyllabusClassesBean> mFilterSyllabusClass;
    private ContentObserver mVolumeObserver;
    //ScreenRotateUtil mScreenRoateUtil;
/*
    public static void lanuchForLocal(Context context,  String courseId,String title,String scaleimg,DownLoadLesson cuLession) {
        Intent intent = new Intent(context, BJRecordPlayActivity.class);
        intent.putExtra(ArgConstant.LOCAL_PATH, cuLession.getPlayPath());
        intent.putExtra(ArgConstant.KEY_TITLE,cuLession.getSubjectName());
        intent.putExtra(ArgConstant.KEY_ID,cuLession.getSubjectID());
        intent.putExtra(ArgConstant.IS_LOCAL_VIDEO,true);
        intent.putExtra(ArgConstant.VIDEO_ID,cuLession.getRoomId());

        intent.putExtra("classid", courseId);
        intent.putExtra(ArgConstant.COURSE_NAME, title);
        intent.putExtra(ArgConstant.COURSE_IMG, scaleimg);
        //intent.putExtra("isPlay", true);
        context.startActivity(intent);
    }*/

    public static void lanuchForLocal(Context context,  String courseId,String title,String scaleimg, DownLoadLesson cuLession,boolean isPlayback) {
        CourseWareInfo info= CourseDataConverter.convertLocalLessonToCourseware(cuLession,isPlayback);
        if (info.videoType == 3 && CommonUtils.isPad(context)){
            LiveVideoForLiveActivity.start(context, courseId, info, scaleimg);
        }else {
            Intent intent = new Intent(context,CommonUtils.isPad(context)?BjPadCachePlayActivity.class: BJRecordPlayActivity.class);
            intent.putExtra(ArgConstant.BEAN, info);
            intent.putExtra("classid", courseId);
            intent.putExtra(ArgConstant.COURSE_NAME, title);
            intent.putExtra(ArgConstant.COURSE_IMG, scaleimg);
            intent.putExtra(ArgConstant.IS_LOCAL_VIDEO, true);
            context.startActivity(intent);
        }
    }


    public static void lanuchForOnlive(Context context,  String courseId,String title,String scaleimg,CourseWareInfo cuLession, boolean isAutoPlay) {
        Intent intent = new Intent(context, BJRecordPlayActivity.class);

        intent.putExtra(ArgConstant.BEAN,cuLession);
        intent.putExtra("isPlay", isAutoPlay);
        intent.putExtra("classid", courseId);

        intent.putExtra(ArgConstant.COURSE_NAME, title);
        intent.putExtra(ArgConstant.COURSE_IMG, scaleimg);

        if(isAutoPlay&&(cuLession!=null)){
           intent.putExtra(ArgConstant.FROM_ACTION,StringUtils.parseInt(cuLession.id));         //用于分页查找第几页
           intent.putExtra(ArgConstant.LESSION_ID,cuLession.coursewareId);//用于选中当前播放课件
        }

        intent.putExtra(ArgConstant.TYPE, 1);
        //intent.putExtra("isPlay", true);
        context.startActivity(intent);
    }

    public static void lanuchForOnlive(Context context,  String courseId,String title,String scaleimg,CourseWareInfo cuLession,  boolean isAutoPlay, int lastPlayTime) {
        Intent intent = new Intent(context, BJRecordPlayActivity.class);

        intent.putExtra(ArgConstant.BEAN,cuLession);
        intent.putExtra("isPlay", isAutoPlay);
        intent.putExtra("classid", courseId);
        intent.putExtra("lastPlayTime", lastPlayTime);

        intent.putExtra(ArgConstant.COURSE_NAME, title);
        intent.putExtra(ArgConstant.COURSE_IMG, scaleimg);

        if(isAutoPlay&&(cuLession!=null)){
            intent.putExtra(ArgConstant.FROM_ACTION,StringUtils.parseInt(cuLession.id));         //用于分页查找第几页
            intent.putExtra(ArgConstant.LESSION_ID,cuLession.coursewareId);//用于选中当前播放课件
        }

        intent.putExtra(ArgConstant.TYPE, 1);
        //intent.putExtra("isPlay", true);
        context.startActivity(intent);
    }

    public List<CourseWareInfo> getRecordList(){
         if(mRecordLessionlist==null){
            mRecordLessionlist=new ArrayList<>();
        }
        return mRecordLessionlist;
    }

    public CourseInfoBean getCourseInfo(){
         return  mRecordCourseInfo;
    }

    @Override
    protected int onSetRootViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //修复某些机器上surfaceView导致的闪黑屏的bug
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return R.layout.player_bjyvod_layout;
    }

    private ImageView mLockScreenBtn;

    private int mCourseType=0;
    private int mInitLocationNodeId=0;//从日历跳转过来的的节点id

    //private boolean mIsOnlyMusic=false; //是否是音频

    //取当前播放位置
    private int getCurrentPlayPositon(){
        if(null!=mPlayStatPresenter){
           return mPlayStatPresenter.getCurPlayTime();
        }
        return (int)getDanmaCurrentTime();
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        EventBus.getDefault().register(this);
        mPlayStatPresenter=new PlayStatistPresenter(this);
        //mScreenRoateUtil=new ScreenRotateUtil(this);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mReadPrefStore=PrefStore.userReadPreference();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCourseId = getIntent().getStringExtra("classid");
        mToHome= getIntent().getBooleanExtra(ArgConstant.TO_HOME,false);
       // mCourseId="65917";
        mInitLocationNodeId=getIntent().getIntExtra(ArgConstant.FROM_ACTION,0);
        if(mInitLocationNodeId>0){
            mCourseWareId=getIntent().getIntExtra(ArgConstant.LESSION_ID,0);
        }
        mCourseType= getIntent().getIntExtra(ArgConstant.TYPE,0);

        //mCurrentPlayIndex = getIntent().getIntExtra("current", 0);
        isQuickPlay = getIntent().getBooleanExtra("isPlay", false);

        mCourseName =  getIntent().getStringExtra(ArgConstant.COURSE_NAME);
        mCourseCover = getIntent().getStringExtra(ArgConstant.COURSE_IMG);

        mStartForResult=getIntent().getBooleanExtra(ArgConstant.FOR_RESUTL,false);
        mIsLocalVideo=getIntent().getBooleanExtra(ArgConstant.IS_LOCAL_VIDEO,false);
        if(mIsLocalVideo||isQuickPlay){
            CourseWareInfo tmpCourseWare=(CourseWareInfo)getIntent().getSerializableExtra(ArgConstant.BEAN);
            if(null!=tmpCourseWare){
                mPlayingCourseWare=tmpCourseWare;
                mCourseWareId  = mPlayingCourseWare.coursewareId;
              //  mIsOnlyMusic=tmpCourseWare.videoType==5;
                if(mIsLocalVideo){
                    mHasLastPlay=true;
                }
            }
        }
        mLastSaveTime = mReadPrefStore.getInt(String.valueOf(mCourseWareId), 0);
        int lastPlayTime = getIntent().getIntExtra("lastPlayTime", -1);
        if (lastPlayTime != -1 &&  lastPlayTime != mLastSaveTime)
            mLastSaveTime = lastPlayTime;
        mRecordCourseInfo=new CourseInfoBean(mCourseName,StringUtils.parseLong(mCourseId),mCourseCover);
        if(mIsLocalVideo && mPlayingCourseWare != null){
             if(mPlayingCourseWare.videoType==3)
               startToRealLiveActivity(mPlayingCourseWare);
        }

        Log.e("BJYshuju", mCurrentPlayIndex + "ssss" + isQuickPlay + "ssss" + mCourseId);
        //初始化界面
        initView();
        //初始化百家云播放器
        initBJYPlayer();
       // loadTestData();
        String from = getIntent().getStringExtra("from");
        if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(mCourseId))
            StudyCourseStatistic.clickCourse(from, mCourseId);
    }

    private void initView() {

       // this.findViewById(R.id.filter_img_btn).setOnClickListener(this);
        this.findViewById(R.id.down_img_btn).setOnClickListener(this);
        this.findViewById(R.id.add_qq_btn).setOnClickListener(this);
       // this.findViewById(R.id.onetone_btn).setOnClickListener(this);

        LinearLayout.LayoutParams tmpParams= (LinearLayout.LayoutParams)mPlayerView.getLayoutParams();

        int screeenWitdh=Math.min(DensityUtils.getScreenWidth(this),DensityUtils.getScreenHeight(this));
        final int distanceHeight=(int)(screeenWitdh*0.56);
        tmpParams.height= distanceHeight;
        mPlayerView.setLayoutParams(tmpParams);
        mPlayerView.setFixedVideoHeight(new Integer(distanceHeight));

        mScrollContainer.setTopDistance(distanceHeight);
        mScrollContainer.setOnPulltoUpListener(new ScrollLinearLayout.OnPulltoUpListener(){
            @Override
            public  void onPulltoUpEvent(boolean isExpland){
                LogUtils.e("onPulltoUpEvent",isExpland+"");
                if(!isExpland){
                    showTopTitleBar( true);
                    if(null!=mScrollableLayout){

                        mScrollableLayout.setCanScroll(false);
                        int endHeight= rootView.getHeight()-DensityUtils.dp2px(BJRecordPlayActivity.this,45);
                        if(mScrollableLayout.getScrollY()!=0){
                            mScrollableLayout.showExpland();
                        }
                        mScrollableLayout.getLayoutParams().height = endHeight;
                        mScrollableLayout.requestLayout();

                    }
                }else {
                    if(mTopTitleBar!=null){
                        mTopTitleBar.setVisibility(View.GONE);
                    }
                    mScrollableLayout.setCanScroll(true);
                   /* final int distanceHeight=(int)(DensityUtils.getScreenWidth(BJRecordPlayActivity.this)*0.56);*/

                    int screeenWitdh=Math.min(DensityUtils.getScreenWidth(BJRecordPlayActivity.this),DensityUtils.getScreenHeight(BJRecordPlayActivity.this));
                    final int distanceHeight=(int)(screeenWitdh*0.56);
                    int finalHeight= rootView.getHeight()-distanceHeight;

                    mScrollableLayout.getLayoutParams().height = finalHeight;
                    mScrollableLayout.requestLayout();
               }
            }
        });

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        ScrollableLayout mScrollLayout = (ScrollableLayout) this.findViewById(R.id.scrollableLayout);
        MagicIndicator pagerSlidingTabStrip = (MagicIndicator) this.findViewById(R.id.magic_indicator2);
        initMagicIndicator2(viewPager, pagerSlidingTabStrip, mScrollLayout);
    }

    private void adJustHeight(boolean addheight){
        int distanceHeight=0;
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            distanceHeight=(int)(DensityUtils.getScreenHeight(this)*0.56)+(addheight?DensityUtils.dp2px(this,20):0);
        }
        else{
            distanceHeight=(int)(DensityUtils.getScreenWidth(this)*0.56);
        }
        final int tmpDistance=distanceHeight;
        LogUtils.e("adJustHeight",distanceHeight+"");
        mScrollContainer.post(new Runnable() {
            @Override
            public void run() {
                if(mScrollableLayout!=null){
                    mScrollContainer.setTopDistance(tmpDistance+mScrollableLayout.getMaxY()-DensityUtils.dp2px(BJRecordPlayActivity.this,45));
                }
            }
        });
    }

    private int mCurrentPageIndex=0;
    private void initMagicIndicator2(final ViewPager viewPager, final MagicIndicator magicIndicator, final ScrollableLayout mScrollLayout) {
        final String[] mDataList = new String[]{"目录","讲义","更多"};//, "讲义", "更多"
        //MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator2);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new SmoothCommonNavigatorAdapter(mDataList,viewPager));
        magicIndicator.setNavigator(commonNavigator);
        final ArrayList<Fragment> fragmentList = new ArrayList<>();
        if(mIsLocalVideo) {
            fragmentList.add(CourseLocallistFragment.getInstance(mCourseId,mCourseWareId));
        }else {
            mCataLogFragment=CourseCatalogFragment.getInstance(mCourseId,mCourseWareId,mInitLocationNodeId,isQuickPlay);
            fragmentList.add(mCataLogFragment);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPageIndex=position;
                magicIndicator.onPageSelected(position);
                ScrollableHelper.ScrollableContainer scrollableContainer=(ScrollableHelper.ScrollableContainer) fragmentList.get(position);

                mScrollLayout.getHelper().setCurrentScrollableContainer(scrollableContainer);
                mScrollContainer.setCurrentScrollableContainer(scrollableContainer);
                if (position >= 1) {
                    Fragment curFrag = fragmentList.get(position);
                    if (curFrag instanceof IPageStripTabInitData)
                        ((IPageStripTabInitData) curFrag).onStripTabRequestData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });

        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("目录");
        viewPager.setAdapter(new MyAddFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList));
        mScrollLayout.getHelper().setCurrentScrollableContainer((ScrollableHelper.ScrollableContainer) fragmentList.get(0));
        mScrollContainer.setCurrentScrollableContainer(((ScrollableHelper.ScrollableContainer) fragmentList.get(0)));
        viewPager.setCurrentItem(0);

    }

    public void  checkLoadCourseInfo(){
        if(mRecordCourseInfo==null||TextUtils.isEmpty(mRecordCourseInfo.title)
                ||TextUtils.isEmpty(mRecordCourseInfo.scaleimg)){
            initCourseSylData(false,true);
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        //获取数据
        if(mIsLocalVideo||isQuickPlay){
            try{
               // ((TextView)this.findViewById(R.id.course_title_txt)).setText(mCourseName);
               // this.findViewById(R.id.filter_img_btn).setVisibility(View.GONE);
                if(mBjCenterViewPresenter!=null){
                    mBjCenterViewPresenter.loadCoverImgage(mCourseCover);
                }
                /*
                ((ProgressTextView)this.findViewById(R.id.txt_progress)).setTextProgress("已学30%",30);
                ((IncreaseProgressBar)this.findViewById(R.id.learn_progressbar)).setCurProgress2(30);*/
                initCourseSylData(true,false);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
            initCourseSylData(false,false);
    }

    public void setPointStatus(AnswerCardBean singleCardBean){
        if(null!=mBjBottomViewPresenter){
            mBjBottomViewPresenter.refreshCurPositionAnswareCard(singleCardBean,mPlayingCourseWare,mRecordCourseInfo);
        }
    }

    private void addTeacherTab(boolean isO2o,String goodsId){
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        final PagerAdapter tmpAdapter= viewPager.getAdapter();
        if(null!=tmpAdapter){
            if(isO2o){
                MagicIndicator magicIndicator = (MagicIndicator) this.findViewById(R.id.magic_indicator2);
                if(null!=magicIndicator.getNavigator()){
                    CommonNavigator NavAdapter=(CommonNavigator)magicIndicator.getNavigator();
                    ((SmoothCommonNavigatorAdapter)NavAdapter.getAdapter()).setDataRefresh(new String[]{"目录","双师课表","讲义","更多"});
                }
            }
            ArrayList<Fragment> fragmentList = new ArrayList<>();
            if(isO2o){
               fragmentList.add(TeacherCourseListFragment.getInstance(goodsId,mIsLocalVideo));
            }
            fragmentList.add(CourseHandoutGroupFragment.getInstance(mCourseId,mIsLocalVideo));
            fragmentList.add(CourseMoreFragment.getInstance(mCourseId,mCourseType));
            MyAddFragmentPagerAdapter fragmentPagerAdapter=(MyAddFragmentPagerAdapter)tmpAdapter;

            String[] titles=isO2o? new String[]{"双师课表","讲义","更多"}:new String[]{"讲义","更多"};
            fragmentPagerAdapter.addList(fragmentList,java.util.Arrays.asList(titles));
       }
    }

    private void initCourseSylData(final boolean isLocal,boolean isRetry) {
        if (TextUtils.isEmpty(mCourseId)) {
            return;
        }
        if((!isLocal)&&(!isQuickPlay)) showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getCourseLearnPercent(mCourseId), new NetObjResponse<CourseInfoBean>() {
            @Override
            public void onError(String message, int type) {
                if((!isLocal)&&(!isQuickPlay)){
                    hideProgess();
                    ToastUtils.showMessage(message);
                }
                adJustHeight(false);
            }

            @Override
            public void onSuccess(BaseResponseModel<CourseInfoBean> model) {
                if((!isLocal)&&(!isQuickPlay)) hideProgess();
                mRecordCourseInfo=model.data;
                bindUIData(model.data);
                addTeacherTab(model.data.iso2o,model.data.goodsId);
            }
        });
        if(isLocal){   return;   }
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getAllCourseWare(StringUtils.parseLong(mCourseId), 1, 20),
                new NetObjResponse<CourseWareBean>() {
                    @Override
                    public void onError(String message, int type) {
                       // hideProgress();
                        ToastUtils.showMessage(message);
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<CourseWareBean> model) {
                      //  hideProgress();
                        mRecordLessionlist = model.data.list;
                     }
                });

        if(isQuickPlay||isRetry) return;
        ServiceProvider.getLastPlay(getSubscription(), mCourseId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                LastCourseBean lastCourseBean = (LastCourseBean) model.data;
              //  UserInfoUtil.liveUserInfo = lastCourseBean.userInfo;

                UserInfoUtil.setLiveUserInfo(lastCourseBean.userInfo);
                if(mInitLocationNodeId>0){
                    return;
                }
                mHasLastPlay=lastCourseBean.isFirstStudy==0;
                mPlayingCourseWare = lastCourseBean.getLastCouseInfo();

                if((mPlayingCourseWare!=null)&&(mPlayingCourseWare.videoType==5)){
                    if(null!=mBjTopViewPresenter){//影藏弹幕按钮
                        mBjTopViewPresenter.hideDanmu();
                    }
                }
                mOnLiveCourseWare=lastCourseBean.getOnLiveCourseInfo();
                if(mStartPlayBtn!=null&&(mOnLiveCourseWare!=null)){
                     if(null!=mBjCenterViewPresenter){
                        if(mHasLastPlay&&(null!=mPlayerView)){
                            mPlayerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(null!=mBjCenterViewPresenter)
                                       mBjCenterViewPresenter.tipContinueLearn(mPlayingCourseWare.title,true);
                                }
                            },300);
                        }
                        mBjCenterViewPresenter.setStartPlaybtnStatus(mOnLiveCourseWare,mHasLastPlay);
                    }

                   /* mStartPlayBtn.postDelayed(new Runnable() {   //选中直播的课件
                        @Override
                        public void run() {
                            setCatalogSelected(mOnLiveCourseWare,0);
                        }
                    },100);*/
                    if(null!=mPlayingCourseWare){
                        mStartPlayBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setCatalogSelected(mPlayingCourseWare,0);
                            }
                        },100);
                    }
                    mCourseWareId=mPlayingCourseWare.coursewareId;
                }
                else if(mStartPlayBtn!=null&&mHasLastPlay&&(mPlayingCourseWare!=null)){
                    if (mCataLogFragment != null && mCataLogFragment.getDownLoadMap() != null) {
                        DownLoadLesson downloadlesson = mCataLogFragment.getDownLoadMap().get(mPlayingCourseWare.coursewareId);
                        if (downloadlesson != null) {
                            mPlayingCourseWare.downStatus = downloadlesson.getDownStatus();
                            mPlayingCourseWare.targetPath = downloadlesson.getPlayPath();
                            mPlayingCourseWare.offSignalFilePath = downloadlesson.getSignalFilePath();
                        }
                    }
                    mStartPlayBtn.setText("继续学习");
                    if(null!=mBjCenterViewPresenter){
                       // mBjCenterViewPresenter.tipContinueLearn(mPlayingCourseWare.title,true);
                        mBjCenterViewPresenter.setStartPlaybtnStatus(mPlayingCourseWare,mHasLastPlay);
                    }
                     mStartPlayBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setCatalogSelected(mPlayingCourseWare,0);
                        }
                    },100);
                    mCourseWareId=mPlayingCourseWare.coursewareId;
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
            }
        });
    }

    public void setLocationCourseWare(CourseWareInfo currentCourseWare){
        mPlayingCourseWare=currentCourseWare;
        if(isQuickPlay||mIsLocalVideo){
            initMusicData();
        }
    }

    @Override
    public void onAfterViewClick(boolean isStart){
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment =fm.findFragmentByTag("目录");
        if((fragment!=null)&&fragment.isAdded()&&(mPlayingCourseWare!=null)){
             if(isStart&&(fragment instanceof OnAfterSelectListener))
                ((OnAfterSelectListener)fragment).onAfterViewClick(isStart,mPlayingCourseWare);
             else {
                 if(!ArrayUtils.isEmpty(mRecordLessionlist)){
                     int findIndex=-1;
                     for(int i=0;i<mRecordLessionlist.size();i++ ){
                         if(mRecordLessionlist.get(i).coursewareId==mPlayingCourseWare.coursewareId){
                             findIndex=i;
                             break;
                         }
                     }
                     findIndex=findIndex+1;
                     if(findIndex<ArrayUtils.size(mRecordLessionlist)){
                        CourseWareInfo tmpObj= mRecordLessionlist.get(findIndex);
                        onSelectPlayClick(tmpObj,true);
                        if(fragment instanceof OnPlaySelectListener)
                             ((OnPlaySelectListener)fragment).onSelectChange(tmpObj.coursewareId,0);
                     }else {
                         ToastUtils.showShort("没有下一个视频了");
                     }
                 }
             }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==2000){//评论
                if (resultCode == RESULT_OK && mJudgeResult != null){
                    mJudgeResult.result = true;
                    if(null!=mBjCenterViewPresenter)
                        mBjCenterViewPresenter.showJudgeImg(false);
                    if (mStatisticsUtil != null)
                        mStatisticsUtil.onJudege((int)getDanmaCurrentTime(), data.getIntExtra("level", 0));
                }
                if(mPlayingCourseWare!=null)
                     showAfterclassTip(mPlayingCourseWare.afterCoreseNum);

            }else if(requestCode == 0x00101){//回放
                int courseWareId= data.getIntExtra(ArgConstant.KEY_ID, 0);
                int hasPlayTime =data.getIntExtra(ArgConstant.LOCAL_PATH, 0);
                LogUtils.e("test",hasPlayTime+"");
                if(mPlayingCourseWare!=null&&(courseWareId==mPlayingCourseWare.coursewareId)){

                    setCatalogSelected(mPlayingCourseWare,hasPlayTime/1000);

                }
             }else if(requestCode == 0x00102){//直播
                 //checkLearnReportStatus(mPlayingCourseWare);
             }
             else
                UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }else if(requestCode==2000&&(resultCode==Activity.RESULT_CANCELED)){
             if(mPlayingCourseWare!=null)
                showAfterclassTip(mPlayingCourseWare.afterCoreseNum);
        }
     }

    AfterclassPracticeDialog mAfterClassPractDialog;
    private void showAfterclassTip(int afterCourseNum){
        if(afterCourseNum<=0) return;
        if(mAfterClassPractDialog==null){

            mAfterClassPractDialog = new AfterclassPracticeDialog(this,BJRecordPlayActivity.this);
            mAfterClassPractDialog.create(R.style.QMUI_TipDialog,this.getResources().getConfiguration().orientation,afterCourseNum).show();
        }else {
            //mDanMuDialogbuilder.setLessionId(mPlayingCourseWare.coursewareId);
            mAfterClassPractDialog.show(this.getResources().getConfiguration().orientation,afterCourseNum);
        }
    }

    private void bindUIData(CourseInfoBean dataInfo){
         try{

             if(mBjCenterViewPresenter!=null){
                 String scaleImg=TextUtils.isEmpty(mCourseCover) ? dataInfo.scaleimg:mCourseCover;
                 mCourseCover=scaleImg;
                 ImageLoad.displaynoCacheImage(this,R.drawable.trans_bg,scaleImg,mBjCenterViewPresenter.getCoverImageView());
             }
             if(dataInfo.service==0){
                 this.findViewById(R.id.add_qq_btn).setVisibility(View.GONE);
             }
             boolean addHeight=false;
             /* if(mIsLocalVideo&&(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)){

                 Rect rect = new Rect();
                // tmpTitleView.getPaint().getTextBounds(dataInfo.title,0,dataInfo.title.length(), rect);
                 //LogUtils.e("getTextBounds",rect.width()+","+DensityUtils.getScreenHeight(this));
                 if(DensityUtils.getScreenHeight(this)<(rect.width()+(DensityUtils.dp2px(this,30))))
                     addHeight=true;
             }
             adJustHeight(addHeight);*/
             adJustHeight(addHeight);
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    private void setPlayerViewListener() {

        mPlayerView.setPlayerTapListener(new BJPlayerView.OnPlayerTapListener() {
            @Override
            public void onSingleTapUp(MotionEvent motionEvent) {
               /* if (listview_ml.getVisibility() == View.VISIBLE) {
                    listview_ml.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onDoubleTap(MotionEvent motionEvent) {
                if (mPlayerView != null) {
                    if (mPlayerView.isPlaying()) {
                        mPlayerView.pauseVideo();
                        //  btnPlay.setImageResource(R.mipmap.vod_play_icon);
                    } else {
                        mPlayerView.playVideo();
                        //  btnPlay.setImageResource(R.mipmap.vod_pause_icon);
                    }
                }
            }
        });

        mPlayerView.setOnPlayerViewListener(new SimpleBjPlayerStatusListener() {

        /*    @Override
            public void onCaton(BJPlayerView bjPlayerView) {
               if(null!=mBjBottomViewPresenter){
                    mBjBottomViewPresenter.showCaton();
                }
            }*/
            @Override
            public void onVideoPrepared(BJPlayerView playerView) {
                super.onVideoPrepared(playerView);
                if(null!=mBgPlayPresenter){
                    mBgPlayPresenter.onVideoPrepared();
                }
                mIsPlayFinished=false;
                mHasPlay=true;
                //TODO: 视频信息初始化结束
                 LogUtils.e("onVideoPrepared,"+mPlayerView.getVideoDefinition());
                 if(mBjCenterViewPresenter!=null) mBjCenterViewPresenter.setCoverImageShow(false);
                 if(mBjBottomViewPresenter!=null) {
                     mBjBottomViewPresenter.setLayoutShow(true);
                     if(!mIsLocalVideo)
                         mBjBottomViewPresenter.showLearnPoint(mPlayingCourseWare,getSubscription());
                 }
                 if(mBjTopViewPresenter!=null){//&&(!mIsLocalVideo)
                     mBjTopViewPresenter.loadInitDanMu(mCourseId,mPlayingCourseWare,getSubscription());
                 }
                 if(!mIsLocalVideo)
                    getJudgeInfo();

                 if(null==mStatisticsUtil){
                     mStatisticsUtil = new VideoStatisticsUtil(mPlayingCourseWare, mCourseId);
                 }else {
                     mStatisticsUtil.reBindData(mPlayingCourseWare);
                 }
                 mStatisticsUtil.startCollectPlayEvent(mPlayerView.getCurrentPosition(), mPlayerView.getDuration());
            }

            @Override
            public String getVideoTokenWhenInvalid() {
                if(mPlayingCourseWare==null) return "";
                return mPlayingCourseWare.token;
            }

            @Override
            public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {
                //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
                if(null!=mBgPlayPresenter){
                    mBgPlayPresenter.onPlayEnd();
                }

                if(null!=mScrollContainer){
                    mScrollContainer.setPlayStatus(false);
                }
                if(mBjBottomViewPresenter!=null) {
                    mBjBottomViewPresenter.setCurrentPosition(playerView.getDuration());
                }
                if (mStatisticsUtil != null)
                    mStatisticsUtil.onVideoStop(getCurrentPlayPositon());
                mIsPlayFinished=true;
                //更新大纲列表学习进度
                if(mPlayingCourseWare!=null)   {
                    setCatalogSelected(mPlayingCourseWare,-1);
                }

                if(mPlayingCourseWare==null||mIsLocalVideo) return;
                final  int courseWareid=mPlayingCourseWare.coursewareId;
                final int afterCourseNum=mPlayingCourseWare.afterCoreseNum;
                //checkLearnReportStatus(mPlayingCourseWare);

                if (!PrefStore.userReadPreference().getBoolean(courseWareid + "judge", false) && mJudgeResult != null) {
                    mJudgeResult.isFinish = 1;
                    judgeCourse();
                }else {
                    showAfterclassTip(afterCourseNum);
                }

                LogUtils.e("onPlayCompleted", "onPlayCompleted");
            }
            @Override
            public void onPause(BJPlayerView playerView) {
                 super.onPause(playerView);
                if(null!=mBgPlayPresenter){
                    mBgPlayPresenter.onPause();
                }

                 if(null!=mScrollContainer){
                    mScrollContainer.setPlayStatus(false);
                 }
                 if(mBjTopViewPresenter!=null) mBjTopViewPresenter.pause();
                if (mStatisticsUtil != null)
                    mStatisticsUtil.onVideoStop(getCurrentPlayPositon());
            }

            @Override
            public void onPlay(BJPlayerView playerView) {
                super.onPlay(playerView);
                if(null!=mBgPlayPresenter){
                    mBgPlayPresenter.onPlay();
                }
                if(null!=mScrollContainer){
                    mScrollContainer.setPlayStatus(true);
                }
                if(mBjTopViewPresenter!=null) mBjTopViewPresenter.resume();
                if (mStatisticsUtil != null)
                    mStatisticsUtil.startCollectPlayEvent(getCurrentPlayPositon(), playerView.getDuration());
            }

            @Override
            public void onSeekComplete(BJPlayerView playerView, int position) {
                //TODO: 拖动进度条
                LogUtils.e("onSeekComplet",position+"");
                //if(mBjTopViewPresenter!=null) mBjTopViewPresenter.start(position,true);
                if(mBjTopViewPresenter!=null) mBjTopViewPresenter.start(position*1000,true);
                if (mStatisticsUtil != null){
                    mStatisticsUtil.onSeekEnd(position);
                }
            }

            @Override
            public void onError(BJPlayerView playerView, int code) {
                super.onError(playerView, code);
                if(null!=mScrollContainer){
                    mScrollContainer.setPlayStatus(true);
                }
                if (code == -2) {
                    playerView.setEnableNetWatcher(false);
                }else {
                    if (mStatisticsUtil != null)
                        mStatisticsUtil.onErrorEvent((int)getDanmaCurrentTime(), code, "发生错误");
                }
            }

            @Override
            public void onVideoDefinition(BJPlayerView playerView, int definition) {
                super.onVideoDefinition(playerView, definition);
                if (mStatisticsUtil != null)
                    mStatisticsUtil.onClariyChanged((int)getDanmaCurrentTime(), definition);
            }

            @Override
            public void onSpeedUp(BJPlayerView playerView, float speedUp) {
                super.onSpeedUp(playerView, speedUp);
                if (mStatisticsUtil != null)
                    mStatisticsUtil.onSpeedChanged(getCurrentPlayPositon(), speedUp+"x");
            }
        });
    }

    public void changeRate(float rateSpeed){
        if(PlayManager.mService!=null&&(mToken!=null)){
             LogUtils.e("getPlayRate0",rateSpeed+"");
            PlayManager.setPlayRate(rateSpeed);
             LogUtils.e("getPlayRate",PlayManager.getPlayRate()+"");
        }
        if(null!=mBjCenterViewPresenter)
            mBjCenterViewPresenter.changeRate(rateSpeed);
    }

    @Override
    public  float getDanmaCurrentTime(){
        if((mToken!=null)&&(mService!=null)){
            return PlayManager.getCurrentPosition();
        }
        if(mPlayerView!=null){
            return mPlayerView.getCurrentPosition();
        }
        return  0f;
     }
    @Override
    public void  onAddSingleDanmaMessage(int textColor,String content){
         if(mBjTopViewPresenter!=null)
            mBjTopViewPresenter.sendDanma(textColor,content);
    }

    Runnable mDelayRunable=new Runnable() {
        @Override
        public void run() {
            if(mLockScreenBtn!=null) mLockScreenBtn.setVisibility(View.GONE);
        }
    };

    DanmakuInputDialogBuilder mDanMuDialogbuilder;
    PopRightCourseListDialog mCourseListDialog;
    private void initBJYPlayer() {
        //以下三个方法分别设置底部、顶部和中部界面
        mBjBottomViewPresenter = new BJBottomViewImpl(mPlayerView.getBottomView(),mIsLocalVideo) {
            @Override
            public void setDuration(int duration) {
                super.setDuration(duration);
                if(null!=mPlayStatPresenter){
                    mPlayStatPresenter.setDuration(duration);
                }
            }

            @Override
            public void setCurrentPosition(int position) {
                super.setCurrentPosition(position);
                if(null!=mPlayStatPresenter){
                    mPlayStatPresenter.setCurrentPosition(position);
                }
            }

            @Override
            public void onStartSeek(int position) {
                super.onStartSeek(position);
                if (mStatisticsUtil != null)
                    mStatisticsUtil.onSeekStart(getCurrentPlayPositon());
            }
        };

        if (CommonUtils.isPad(this)) {
            mBjBottomViewPresenter.setFullScreenClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLandScape= getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                    if(!isLandScape){
                        if(null!=mPlayerView) mPlayerView.switchOrientation();
                    }else {
                        if(null!=mBjBottomViewPresenter){
                            mBjBottomViewPresenter.setOrientation(BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE);
                        }
                        if (mPlayerView != null) {
                            //mPlayerView.onConfigurationChanged(getResources().getConfiguration());
                            android.view.ViewGroup.LayoutParams var1 = mPlayerView.getLayoutParams();
                            var1.height = -1;
                            var1.width = -1;
                            mPlayerView.setLayoutParams(var1);
                        }
                   }
                }
            });
        }
        mBjBottomViewPresenter.setOnLessionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCourseListDialog==null){
                    mCourseListDialog=new PopRightCourseListDialog(v.getContext(),StringUtils.parseLong(mCourseId),getSubscription());
                    mCourseListDialog.builder().setOnSubItemClickListener(BJRecordPlayActivity.this);
                    mCourseListDialog.setCourseWarelist(mRecordLessionlist);

                   if(mPlayingCourseWare!=null)
                       mCourseListDialog.setCurrentSelectId(mPlayingCourseWare.coursewareId);
               }
                if(mPlayingCourseWare!=null){
                    mCourseListDialog.setDelaySelectId(mPlayingCourseWare.coursewareId);
                }
                mCourseListDialog.show();
            }
        });

        mBjBottomViewPresenter.setOnShowInClassListener(this);
        mPlayerView.setBottomPresenter(mBjBottomViewPresenter);
        mBjTopViewPresenter = new BJTopViewImpl(mPlayerView.getTopView(),mPlayerView,true) {
            @Override
            public void setTitle(String title) {
                if (isQuickPlay) {
                    if (!TextUtils.isEmpty(mCourseName))
                        title = mCourseName;
                } else {
                    if (mPlayingCourseWare!=null) {
                         title=mPlayingCourseWare.title;
                   }
                }
                LogUtils.e("setTitle", title + "");
                super.setTitle(title);
            }
        };
        mBjTopViewPresenter.setCourseId(mCourseId,mCourseCover,mIsLocalVideo);
        mBjTopViewPresenter.setPortraitPopListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharePopWindow(v);
            }
        });

        mPlayerView.setTopPresenter(mBjTopViewPresenter);
        mPlayerView.getTopViewPresenter().setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBjCenterViewPresenter = new BJCenterViewExPresener(mPlayerView.getCenterView()){
            @Override
            public void onPlaybackWarnRetry(int type){
                if(mPlayingCourseWare!=null){

                    if(type==1){//继续播放的点击触发     //type ==-1 出错，0警告 ,1继续播放
                /*        setCatalogSelected(mPlayingCourseWare,0);
                        mOnLiveCourseWare=null;*/

                        CourseWareInfo curWareInfo;
                        boolean isSamePage=true;
                        if(mOnLiveCourseWare!=null){
                            curWareInfo=mOnLiveCourseWare;
                            mOnLiveCourseWare=null;
                            mHasLastPlay=true;
                            if(mPlayingCourseWare!=null){
                                if(mPlayingCourseWare.inPage!=curWareInfo.inPage){
                                    isSamePage=false;
                                }
                            }
                        }
                        //
                        // setCatalogSelected(mPlayingCourseWare,isSamePage?0:-StringUtils.parseInt(mPlayingCourseWare.id));
                        //onSelectPlayClick(curWareInfo,true);
                     }
                    onSelectPlayClick(mPlayingCourseWare,true);
                }
            }

            @Override
            public void setActionText(BJCustomCenterView.ActionType actionType,String value){
                 if(mBjBottomViewPresenter!=null)
                    mBjBottomViewPresenter.setActionText(actionType,value);
            }

            @Override
            public void changeRateAction(){
                if(PlayManager.mService!=null&&(mToken!=null)){
                    if(PlayManager.isPause()||PlayManager.isPlaying()){
                        float playRate=PlayManager.getPlayRate();

                        LogUtils.e("rateplay3",playRate+"");
                        PlayRateDialogFragment ratefragment=PlayRateDialogFragment.getInstance(playRate);
                        ratefragment.show(getSupportFragmentManager(), "rateplay");
                    }
                    return;
                }
                if(mPlayerView!=null&&mPlayerView.isPlayStateNormal()){
                    float playRate=mPlayerView!=null?mPlayerView.getVideoRateInFloat():1f;
                    PlayRateDialogFragment ratefragment=PlayRateDialogFragment.getInstance(playRate);
                    ratefragment.show(getSupportFragmentManager(), "rateplay");
               }
            }

            @Override
            public void onClickJudgeBtn() {
                super.onClickJudgeBtn();
                judgeCourse();
            }

            @Override
            public void showError(int code, String message) {
                if (code == 5101 || code == 5102 || code == 5103){
                    onRefreshToken();
                    return;
                }
                super.showError(code, message);
            }

            @Override
            public void showProgressSlide(int delta) {
                super.showProgressSlide(delta);
                if(mStatisticsUtil != null)
                    mStatisticsUtil.onSeekStart(getCurrentPlayPositon());
            }
        };
        mBjCenterViewPresenter.setRightMenuHidden(true);

        mPlayerView.setCenterPresenter(mBjCenterViewPresenter);
        mBjBottomViewPresenter.setIPlayerCenterContact(mBjCenterViewPresenter);

        mStartPlayBtn=(Button) mPlayerView.findViewById(R.id.start_play_btn);
        mStartPlayBtn.setOnClickListener(this);
        mLockScreenBtn=(ImageView) mPlayerView.findViewById(R.id.lock_screen_btn);
        //mPlayerView.setMemoryPlayEnable(true);
        mPlayerView.setInterceptTouch(new BJPlayerTouchView.OnInterceptListener(){
            @Override
            public void onInterceptClick(View view){
                if(mLockScreenBtn!=null){
                    mPlayerView.removeCallbacks(mDelayRunable);
                    mLockScreenBtn.setVisibility(View.VISIBLE);
                    mPlayerView.postDelayed(mDelayRunable,3000);
                 }
            }
        });
        mPlayerView.findViewById(R.id.lock_screen_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOldLocked= "0".equals(v.getTag()) ? false:true;
                v.setSelected(!isOldLocked);
                v.setTag(isOldLocked ? "0":"1");
                if(!isOldLocked) {
                    mPlayerView.hideTopAndBottom();
                 } else {
                    mPlayerView.showTopAndBottom();
                }
                mPlayerView.setInterceptTouch(!isOldLocked);
                if(!isOldLocked){
                    mPlayerView.postDelayed(mDelayRunable,3000);
                }
                else
                    mPlayerView.removeCallbacks(mDelayRunable);
            }
        });
        if(!mIsLocalVideo){
            mPlayerView.findViewById(R.id.danmu_input_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mPlayingCourseWare==null||mPlayingCourseWare.coursewareId==0) return;
                    if(mIsPlayFinished){
                        ToastUtils.showShort("播放完成暂不能输入弹幕");
                        return;
                    }
                    if(mDanMuDialogbuilder==null){
                        mDanMuDialogbuilder = new DanmakuInputDialogBuilder(v.getContext(),BJRecordPlayActivity.this);
                        mDanMuDialogbuilder.setOnDanmaSendListener(BJRecordPlayActivity.this);
                        mDanMuDialogbuilder.setCourseId(mPlayingCourseWare.classId)
                                .setLessionId(mPlayingCourseWare.coursewareId).create(R.style.QMUI_TipDialog).show();
                    }else {
                        mDanMuDialogbuilder.setCourseId(mPlayingCourseWare.classId);
                        mDanMuDialogbuilder.setLessionId(mPlayingCourseWare.coursewareId);
                        mDanMuDialogbuilder.show();
                    }

                }
            });
        }else {
            mBjBottomViewPresenter.disableDanmuInput();
        }

        //回调接口为播放器状态改变之后向上层app的通知，可以在每个回调方法中实现自己的业务逻辑
        setPlayerViewListener();
        //初始化partnerId，第一个参数换成您的partnerId
        mPlayerView.initPartner(Constant.BAIJIAYNN_PARTNER_KEY, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        //设置片头
        mPlayerView.setHeadTailPlayMethod(BJPlayerView.HEAD_TAIL_PLAY_NONE);

        //第一个参数为百家云后台配置的视频id，第二个参数为视频token
        // playerView.setVideoId(videoId, videoToken);
        if (mIsLocalVideo&& (mPlayingCourseWare!=null)) {
             if(mPlayingCourseWare.videoType==1||mPlayingCourseWare.videoType==5){

                if(mPlayingCourseWare.videoType==1){
                     mStartPlayBtn.setVisibility(View.GONE);
                     mPlayerView.setVideoPath(mPlayingCourseWare.targetPath);
                     mPlayerView.playVideo(mLastSaveTime);
                }else if(mPlayingCourseWare.videoType==5) {
                     startPlayMusic(mPlayingCourseWare);
                }

                if(CommonUtils.isPad(this)){
                   // boolean isReversal=false;
                    if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        this.getWindow().addFlags(1024);//全屏
                     //   isReversal=ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT== this.getRequestedOrientation();

                      //  LogUtils.e("curOrientation",this.getRequestedOrientation());
                        if(null!=mScrollContainer){
                            mScrollContainer.setLandScape(true);
                        }
                        if (mPlayerView != null) {
                            mPlayerView.onConfigurationChanged(this.getResources().getConfiguration());
                        }
                    }else {
                        mPlayerView.switchOrientation();
                    }
                    registerScreenRoate();
                   // LogUtils.e("curOrientation",isReversal+"  /");
                    //setRequestedOrientation(isReversal? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//固定方向
                }
                else
                    mPlayerView.switchOrientation();
            }

       }else if(isQuickPlay&&(mPlayingCourseWare!=null)) {
            mStartPlayBtn.setVisibility(View.GONE);
            if(mBjCenterViewPresenter!=null) {
                mBjCenterViewPresenter.setVideoSize(mPlayingCourseWare.getfileSize());
            }
            if(mPlayingCourseWare.videoType==5) {
                startPlayMusic(mPlayingCourseWare);
            }else {
                mPlayerView.setVideoId(StringUtils.parseLong(mPlayingCourseWare.videoId), mPlayingCourseWare.token);
                mPlayerView.playVideo(mLastSaveTime);
            }

        }else if(CommonUtils.isPad(this)){
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

               /* if (mPlayerView != null) {
                    mPlayerView.onConfigurationChanged(this.getResources().getConfiguration());
                }*/
                if(null!=mScrollContainer){
                   // boolean isLandscape=this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                   mScrollContainer.setLandScape(true);
                }

            }
        }
    }

    protected void registerScreenRoate(){}

    FrameLayout mFragmentContainer;
    @Override
    public void showInclassFragment(boolean isShow,long pointId,String practiceId){
        if(mFragmentContainer==null){
            mFragmentContainer=new FrameLayout(this);
            mFragmentContainer.setId(R.id.scrollview);
            mPlayerView.addView(mFragmentContainer,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        }
        if(isShow){
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment =fm.findFragmentByTag("figure_action_fag");
            if (fragment == null||!fragment.isAdded()){
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.v_fragment_enter, R.anim.v_fragment_pop_exit,
                       R.anim.v_fragment_pop_enter, R.anim.v_fragment_exit);
                ft.replace(R.id.scrollview,  InclassPracticeFragment.getInstance(pointId,practiceId), "figure_action_fag");
                ft.addToBackStack("figure_action_fag");
                ft.commitAllowingStateLoss();
            }
        }
        else{
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack();
            if(mPlayerView!=null){
                mPlayerView.playVideo();
            }
        }
     }

    @Override
    public void onCatalogSelect(String teacherIds,String teacherNames,String classNodeId,int type){
        if (mCataLogFragment != null && mCataLogFragment.isAdded()) {
            //mCataLogFragment.showAfterClass(view.isSelected());
            mCataLogFragment.showFilter(teacherIds,teacherNames,classNodeId,type);
        }
    }

    PopCollectListAction shareActons;
    private void showSharePopWindow(View anchor) {
//        if(mPlayingCourseWare == null)
//            return;
        if (shareActons == null) {
            /*shareActons = new QuickListAction(this, R.layout.course_player_right_popup, R.id.root);
            shareActons.setAnimStyle(R.style.Animations_PopDownMenu_Right);*/
            shareActons = new PopCollectListAction(this, R.layout.pop_collection_more_views, R.id.root);
            shareActons.setDistance(DensityUtils.dp2px(this,20));
            shareActons.setCourseId(mCourseId);
            if(mIsLocalVideo){
                shareActons.setDisableLike();
            }
            shareActons.setAnimStyle(R.style.Animations_PopDownMenu_Right);
            shareActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    switch (position) {
                        case 0:
                            shareActons.dismiss();
                            ShareDialogFragment fragment=  ShareDialogFragment.getInstance(mCourseId,
                                    mPlayingCourseWare == null ? 0 : mPlayingCourseWare.coursewareId ,
                                    mCourseCover,
                                    mPlayingCourseWare == null ? "" : mPlayingCourseWare.id,
                                    mToken != null && mPlayingCourseWare != null ? mPlayingCourseWare.videoType : -1);
                            // fragment.setTargetFragment(MainFragment.this, REQUEST_CODE);
                            fragment.show(getSupportFragmentManager(), "share");
                            //UIJumpHelper.showWebCourse(LiveVideoActivity.this, courseId);
                            VideoStatisticsUtil.reportShareEvent(mCourseId, mCourseWareId);
                            break;
                        case 1:
                            shareActons.perfomClick();
                            break;
                        case 2:
                            /*if (qqInfoBean != null && !TextUtils.isEmpty(qqInfoBean.AndroidFunction)) {
                                Method.joinQQGroup(qqInfoBean.AndroidFunction);
                                msgActons.dismiss();
                            }*/
                            FeedbackActivity.newInstance(BJRecordPlayActivity.this);
                            shareActons.dismiss();
                             break;
                    }
                }
            });
            shareActons.show(anchor);
        } else
            shareActons.Reshow(anchor);
    }

    private View mTopTitleBar;
    private void showTopTitleBar(boolean isShow){
        if(isShow){
            if(mTopTitleBar==null){
                ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView();
                LayoutInflater factory = LayoutInflater.from(this);
                mTopTitleBar= factory.inflate(R.layout.player_bjy_title_layout,(ViewGroup) decorView,false);
                decorView.addView(mTopTitleBar);
                mTopTitleBar.findViewById(R.id.back_iv_btn).setOnClickListener(this);

            }

            String courseName=mCourseName;
            if(TextUtils.isEmpty(courseName)){
                if(mRecordCourseInfo!=null&&(!TextUtils.isEmpty(mRecordCourseInfo.title))){
                    courseName=mRecordCourseInfo.title;
                }
            }
            if(!TextUtils.isEmpty(courseName)){
                ((TextView)mTopTitleBar.findViewById(R.id.title_name_txt)).setText(String.valueOf(courseName));
            }
            AnimUtils.AlphaTagShow(mTopTitleBar,true);
         }else {
            if(mTopTitleBar!=null){
                AnimUtils.AlphaTagShow(mTopTitleBar,false);
            }
            if(null!=mScrollableLayout)
                mScrollableLayout.setCanScroll(true);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hide_back:
                onBackPressed();
                break;

            case R.id.start_play_btn:
                if (Method.isListEmpty(mRecordLessionlist)) {
                   ToastUtils.showShort("暂无课表");
                   return;
                }
                CourseWareInfo curWareInfo;
                boolean isSamePage=true;
                if(mOnLiveCourseWare!=null){
                    curWareInfo=mOnLiveCourseWare;
                    mOnLiveCourseWare=null;
                    mHasLastPlay=true;
                    if(mPlayingCourseWare!=null){
                        if(mPlayingCourseWare.inPage!=curWareInfo.inPage){
                            isSamePage=false;
                         }
                    }
                }
                else if((mPlayingCourseWare!=null)){
                    curWareInfo=mPlayingCourseWare;
                }else {
                    int curIndex=mCurrentPlayIndex == -1? 0:mCurrentPlayIndex;
                    curWareInfo= ArrayUtils.getAtIndex(mRecordLessionlist,curIndex);
                }
                if(curWareInfo!=null)  {
                  //  setCatalogSelected(curWareInfo,isSamePage?0:-10000);
                    setCatalogSelected(curWareInfo,0);
                    onSelectPlayClick(curWareInfo,true);
                }
                else  ToastUtils.showShort("暂无课表");

                break;
            case R.id.add_qq_btn:
                if (mRecordCourseInfo != null ) {
                    if(mRecordCourseInfo.service==1&& !TextUtils.isEmpty(mRecordCourseInfo.androidFunction))
                         Method.joinQQGroup(mRecordCourseInfo.androidFunction);
                    else if(mRecordCourseInfo.service==2) {

                        WeChatGroupDialogFragment ratefragment =  WeChatGroupDialogFragment.getInstance(mRecordCourseInfo.wechatNumber,mRecordCourseInfo.wechatQrCode);
                        ratefragment.show(this.getSupportFragmentManager(), "wechat_add_group");
                    }
               }
               break;
       /*     case R.id.onetone_btn:
                if(mRecordCourseInfo != null && !TextUtils.isEmpty(mRecordCourseInfo.orderNum)) {
                   OneToOnCourseInfoFragment.lanuch(this,mCourseId,mRecordCourseInfo.title,mRecordCourseInfo.orderNum);
                }
                break;*/
         /*    case R.id.filter_img_btn:
                 showFilterPopWindow(view);
                break;*/
            case R.id.back_iv_btn:
                if(CommonUtils.isFastDoubleClick())  return;
                if((null== mTopTitleBar)||null== mTopTitleBar.getTag()||("0".equals(mTopTitleBar.getTag()))){
                    return;
                }
                showTopTitleBar(false);
                if(mScrollContainer!=null)
                    mScrollContainer.switchExpland();

                final int distanceHeight=(int)(DensityUtils.getScreenWidth(this)*0.56);
                int finalHeight= rootView.getHeight()-distanceHeight;


                mScrollableLayout.getLayoutParams().height = finalHeight;
                mScrollableLayout.requestLayout();

              /*  LogUtils.e("finalHeight",mScrollableLayout.getHeight()+"_"+finalHeight);
                ExpandCollapseAnimation animation = new ExpandCollapseAnimation(mScrollableLayout, mScrollableLayout.getHeight(),finalHeight);

                mScrollableLayout.clearAnimation();
                mScrollableLayout.startAnimation(animation);*/
               break;
             case R.id.down_img_btn:
                if ((!mIsLocalVideo)&&ArrayUtils.isEmpty(mRecordLessionlist)) {
                    ToastUtils.showShortToast(R.string.no_lessiondown_tip);
                    return;
                }
                CommonUtils.checkPowerAndTraffic(this, new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        DownLoadListActivity.lanuch(BJRecordPlayActivity.this, mRecordCourseInfo,mCurrentPageIndex==1?1:0 );
                    }
                });
                break;
        }
    }

    private void startToRealLiveActivity(CourseWareInfo lessionInfo) {
        mHasPlay=true;
        /*if(mRecordCourseInfo!=null){
            CourseApiService.getGoldReWard(getSubscription(),mRecordCourseInfo.isFree);
        }*/
        if (lessionInfo == null)
            return;
        if(null!=mPlayStatPresenter){
            mPlayStatPresenter.reset();
        }
         if(null!=mStartPlayBtn)
            mStartPlayBtn.setVisibility(View.VISIBLE);
        mPlayerView.pauseVideo();
        if(null!=mBjCenterViewPresenter)
            mBjBottomViewPresenter.setLayoutShow(false);



        if(null!=mBjCenterViewPresenter){
            mBjCenterViewPresenter.resetCoverStatus();
            boolean flag= mBjCenterViewPresenter.setStartPlaybtnStatus(lessionInfo,true);
            if(!flag) return;
        }

        if(lessionInfo.videoType== 1){
            if(mRecordCourseInfo==null){
                return;
            }
            BJRecordPlayActivity.lanuchForOnlive(this,mCourseId,mRecordCourseInfo.title,mRecordCourseInfo.scaleimg,lessionInfo, true);
            this.finish();
            return;
        }else if (lessionInfo.tinyLive == 1) {
            CommonUtils.startLiveRoom(getSubscription(),this,  String.valueOf(lessionInfo.classId), lessionInfo.coursewareId+"", lessionInfo.parentId,lessionInfo.joinCode, lessionInfo.bjyRoomId, lessionInfo.sign);
//            LiveRoomActivity.lanuchForResult(this, mCourseId, lessionInfo.classId,lessionInfo.parentId,lessionInfo.coursewareId , 1, lessionInfo.joinCode);
        } else {

            boolean isLandscape=this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || mIsLocalVideo;
            LiveVideoForLiveActivity.startForLandScapeResult(this, mCourseId, getRecordList().indexOf(lessionInfo), mIsLocalVideo, lessionInfo, mCourseCover,isLandscape);

            if(null!=mPlayerView)
                mPlayerView.getTopView().setVisibility(View.VISIBLE);

            if(null!=mCourseListDialog){
                mCourseListDialog.checkHasClose();
            }
//            if(null!=mPlayerView&&(mPlayerView.getOrientation()==BJPlayerView.VIDEO_ORIENTATION_LANDSCAPE)){
//                    mPlayerView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPlayerView.switchOrientation();
//                        }
//                    },500);
//             }

        }
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = IMusicService.Stub.asInterface(iBinder);
        initMusiclistener();
        if((!isQuickPlay)&&(!mIsLocalVideo)){
            initMusicData();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
         if((null!=componentName)&&("clearBinding".equals(componentName.getClassName()))){
             LogUtil.e("onServiceDisconnected","clearBinding");
             if (mToken != null) {
                 PlayManager.unbindFromService(mToken);
                 mToken = null;
             }
             if(null!=mMusicPresenter){
                 mMusicPresenter.detachView();
                 mMusicPresenter=null;
             }
             mHasUnBindServer=true;
             return;
         }
        mService = null;
        mToken=null;
    }

    private void initMusiclistener(){
        if(null!=mBjTopViewPresenter){
            mBjTopViewPresenter.hideDanmu();
        }
        if(null!=mBjBottomViewPresenter){
            mBjBottomViewPresenter.hideSwitchScreen();
            mBjBottomViewPresenter.setOnAudioContractListener(new BJBottomViewImpl.OnAudioContractListener() {
                @Override
                public void onPlayPauseClick(View view) {
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                        PlayManager.playPause();
                    }
                }

                @Override
                public void onSeekTo(int ms) {
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                        PlayManager.seekTo(ms);
                    }
                }

                @Override
                public void switchPlayMode(){
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                        PlayManager.setLoopMode(1);
                    }
                }

            });
        }
        if(null!=mBjCenterViewPresenter){
            mBjCenterViewPresenter.setOnPlayRateListener(new BJCenterViewPresenterCopy.OnPlayRateInterceptListener() {
                @Override
                public float getCurrentPlayRate() {
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                         return PlayManager.getPlayRate();
                    }
                    return 1f;
                }

                @Override
                public void setCurrentPlayRate(float playRate) {
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                        PlayManager.setPlayRate(playRate);
                    }
                }

                @Override
                public void switchPlayMode() {
                    if((null!=mToken)&&(null!= PlayManager.mService)){
                        PlayManager.setLoopMode(1);
                        if(mBjBottomViewPresenter!=null)
                            mBjBottomViewPresenter.setActionText(BJCustomCenterView.ActionType.SHOWAUDIOMODE,"");
                    }
                }
            });
        }
    }

    private void initMusicData(){
        if(null!=mPlayingCourseWare){
            if(null!=mStartPlayBtn){
                mStartPlayBtn.setVisibility(View.GONE);
            }
            if(mBjBottomViewPresenter!=null) {
                mBjBottomViewPresenter.setLayoutShow(true);
            }
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment =fm.findFragmentByTag("目录");
            if((fragment!=null)&&fragment.isAdded()&&(!fragment.isDetached())){

                LogUtil.e("initMusicData","test0");
                if(fragment instanceof OnPlaySelectListener){

                    List<PurchasedCourseBean.Data> tmplist= ((OnPlaySelectListener)fragment).getCurrentCourseList();
                    if(ArrayUtils.isEmpty(tmplist)) return;
                    List<Music> musicList=new ArrayList<>();
                    int index=0;
                    boolean hasFind=false;
                    for(PurchasedCourseBean.Data bean:tmplist){
                        Music curDto =CourseDataConverter.convertCoursewareToMusic(bean,mRecordCourseInfo);
                        if(curDto!=null){
                            musicList.add(curDto);
                            if(!hasFind){
                                if(bean.coursewareId==mPlayingCourseWare.coursewareId){
                                    hasFind=true;
                                    curDto.trackNumber=mLastSaveTime;
                                 }else {
                                    index++;
                                }
                            }
                        }
                    }
                    PlayManager.play( index,musicList,String.valueOf(mCourseId));
                }
            }
         }
    }

    private void startPlayMusic(CourseWareInfo lessionInfo){
        if(null==mToken){
            //处理同一页面打开多次的问题，先解除之前的绑定
            PlayManager.unbindFromService(GROUPNAME);
            mToken = PlayManager.bindToService(this, this,GROUPNAME);
            mMusicPresenter=new MusicPlayPresenter();
            mMusicPresenter.attachView(this);
            if(null!=mBjCenterViewPresenter){
                mBjCenterViewPresenter.scaleCenterView(!mIsLocalVideo);
            }
            boolean isPad=CommonUtils.isPadv2(BJRecordPlayActivity.this);
            if(null!=mBjBottomViewPresenter&&(mIsLocalVideo||isPad)){
                 mBjBottomViewPresenter.showAudioPlayMode(isPad);
            }
        }else {
            if(null!=mStartPlayBtn){
                mStartPlayBtn.setVisibility(View.GONE);
            }
            if(mBjBottomViewPresenter!=null) {
                mBjBottomViewPresenter.setLayoutShow(true);
            }
            List<Music> musicList= PlayManager.getPlayList();
            if(!ArrayUtils.isEmpty(musicList)){
                int index=0;
                for(int i=0;i<musicList.size();i++){
                    if(musicList.get(i).mid.equals(lessionInfo.coursewareId+"")){
                       break;
                    }
                    index++;
                }
                PlayManager.play(index,mLastSaveTime);
            }
         }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePlayStatus(StatusChangedEvent event) {
        if(mHasUnBindServer) return;
        LogUtils.e("onUpdatePlayStatus", GsonUtil.GsonString(event));
        if(!mIsPause) {
            if (null != mBjBottomViewPresenter) {
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
        }else {
            mStatusChangEvent=event;
        }
    }

    private CourseWareInfo mMusicCourseWareInfo=new CourseWareInfo();
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMetaChangedEvent(MetaChangedEvent event) {
        //
        if(mHasUnBindServer) return;
        if(null==event)      return;
        if(!mCourseId.equals(event.mMusic.albumId)){
             return;
        }
        if(null!=mPlayStatPresenter){
            mPlayStatPresenter.reset();
        }
        mPlayingCourseWare= CourseDataConverter.convertMusicToCourseware(event.mMusic,mMusicCourseWareInfo);
        //后台播放时尽量不操作Ui
        if(!mIsPause){
            setCatalogSelected(mPlayingCourseWare,0);
            if(!mIsLocalVideo)
                getJudgeInfo();
         }else {
            mHasRefreshSelect=true;
        }
     }

    @Override
    protected void onStart() {
        super.onStart();
        mIsPause = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsPause = true;
    }

    @Override
    public void updateProgress(long progress,long max){
         LogUtils.e("updateProgress",progress+","+max);
         if (!mIsPause) {
            if(mBjBottomViewPresenter!=null) {
                mBjBottomViewPresenter.setDuration((int)max);
                mBjBottomViewPresenter.setCurrentPosition((int)progress);
            }
         }else {//切到后台，调用进度代码，不刷新UI
              if(null!=mPlayStatPresenter){
                 mPlayStatPresenter.setDuration((int)max);
                 mPlayStatPresenter.setCurrentPosition((int)progress);
             }
         }
    }

    private boolean isMusicServerStarted(){
         return  ((null!=mToken)&&(null!= PlayManager.mService));
    }

    //1点播2直播3直播回放
    @Override
    public void onSelectPlayClick(CourseWareInfo lessionInfo,boolean isRefreshDialog){
        if(null!=mOnLiveCourseWare){
            mOnLiveCourseWare=null;
        }
        if(mBjCenterViewPresenter!=null) {//结束 直播时的继续学习
            mBjCenterViewPresenter.tipContinueLearn("",false);
        }

        if (mStatisticsUtil != null) {
            mStatisticsUtil.onVideoFinish(getCurrentPlayPositon());
            mStatisticsUtil = null;
        }

        boolean isAudioType=lessionInfo.videoType==5;
        if(lessionInfo.videoType== 2||lessionInfo.videoType==3||isAudioType){
            this.mPlayingCourseWare=lessionInfo;
            boolean needJump= lessionInfo.downStatus == DownBtnLayout.FINISH;
            if(needJump){
                if(isAudioType){
                    playSelect(lessionInfo,isRefreshDialog);
                }else {
                    startToRealLiveActivity(lessionInfo);
                }
                return;
            }
            if (!mIsLocalVideo) {
                if (!NetUtil.isConnected()) {
                    if(!isAudioType){   mPlayerView.pauseVideo();  }
                    else {
                        if(isMusicServerStarted()){ PlayManager.stop();}
                    }
                    if (null != mBjCenterViewPresenter) {

                        mStartPlayBtn.setVisibility(View.GONE);
                        mBjCenterViewPresenter.showNoNetWorkWarning("网络未连接，请检查网络设置");
                        mBjCenterViewPresenter.setCoverImageShow(true);

                        if(null!=mBjCenterViewPresenter)
                            mBjBottomViewPresenter.setLayoutShow(false);

                        if(null!=mScrollContainer){
                            mScrollContainer.setPlayStatus(true);
                            if(!mScrollContainer.isExpland()&&(null!=mTopTitleBar)){
                                mTopTitleBar.findViewById(R.id.back_iv_btn).performClick();
                            }
                        }
                        if(null!=mCourseListDialog){
                            mCourseListDialog.checkHasClose();
                        }
                        if(null!=mPlayerView)
                            mPlayerView.getTopView().setVisibility(View.VISIBLE);
                        return;
                    }
                 }
                 if(!NetUtil.isWifi()){
                     if(!isAudioType) {   mPlayerView.pauseVideo(); }
                     else {
                         if(isMusicServerStarted()){ PlayManager.stop();}
                     }
                    boolean isOnliveing=lessionInfo.videoType==3||(mPlayingCourseWare.videoType==2&&mPlayingCourseWare.liveStatus == 1)
                            ||isAudioType;
                     if (null != mBjCenterViewPresenter&&(mBjCenterViewPresenter.canNetWatcher())&&isOnliveing) {
                         mBjCenterViewPresenter.setVideoSize(lessionInfo.getfileSize());
                         mBjCenterViewPresenter.showTrafficWarning(lessionInfo.videoType==2 ? "您正在使用非WiFi网络,播放将产生流量费"
                                                                                            : "您正在使用非WiFi网络\n播放将产生流量费");
                         mBjCenterViewPresenter.setCoverImageShow(true);
                         if(null!=mBjCenterViewPresenter)
                             mBjBottomViewPresenter.setLayoutShow(false);

                        if(null!=mScrollContainer){
                             mScrollContainer.setPlayStatus(true);
                             if(!mScrollContainer.isExpland()&&(null!=mTopTitleBar)){
                                mTopTitleBar.findViewById(R.id.back_iv_btn).performClick();
                             }
                         }

                         if(null!=mCourseListDialog){
                             mCourseListDialog.checkHasClose();
                         }
                         if(null!=mPlayerView)
                             mPlayerView.getTopView().setVisibility(View.VISIBLE);
                         return;
                     }
                }
            }
            if(isAudioType){
                playSelect(lessionInfo,isRefreshDialog);
            }else {
                startToRealLiveActivity(lessionInfo);
            }
            return;
        }
        playSelect(lessionInfo,isRefreshDialog);
    }

    private void bindBgPlayPresent(){
        Music curDto =CourseDataConverter.convertCoursewareToMusic(mPlayingCourseWare,mRecordCourseInfo,false);
        if(null==mBgPlayPresenter){
            mBgPlayPresenter=new BgPlayPresenter();
            mBgPlayPresenter.attachView(new BgPlayContract.BaseView() {
                @Override
                public long getCurrentPosition() {
                    if(null!=mPlayerView){
                        return   mPlayerView.getCurrentPosition();
                    }
                    return 0;
                }

                @Override
                public boolean isPlaying() {
                    if(null!=mPlayerView){
                      return   mPlayerView.isPlaying();
                    }
                    return false;
                }

                @Override
                public void doPauseAction() {
                    if(null!=mPlayerView){
                        mPlayerView.pauseVideo();
                    }
                }

                @Override
                public void doResumeAction() {
                    if(null!=mPlayerView){
                        mPlayerView.playVideo();
                    }
                }

                @Override
                public Context getContext() {
                    return BJRecordPlayActivity.this;
                }
            });
        }
        mBgPlayPresenter.start(curDto);
    }

    private void playSelect(CourseWareInfo lessionInfo,boolean isRefreshDialog){

        if(null!=mScrollContainer){
            mScrollContainer.setPlayStatus(true);
            if(!mScrollContainer.isExpland()&&(null!=mTopTitleBar)){
                mTopTitleBar.findViewById(R.id.back_iv_btn).performClick();
            }
        }
        if(mRecordCourseInfo!=null){
            CourseApiService.getGoldReWard(getSubscription(),mRecordCourseInfo.isFree);
        }
        mIsPlayFinished=false;
        if(!isRefreshDialog){//来自于dailog的选择改变
            mDialogSelectChange=true;
        }
        if(mBjCenterViewPresenter!=null) {
            mBjCenterViewPresenter.resetPlaySpeed();
            mBjCenterViewPresenter.setStartPlaybtnStatus(lessionInfo,false);
            mBjCenterViewPresenter.setVideoSize(lessionInfo.getfileSize());
        }
        if(mBjBottomViewPresenter!=null) mBjBottomViewPresenter.resetPoint();
        if(mPlayingCourseWare!=lessionInfo) {
            if(mPlayerView.isPlaying())  mPlayerView.pauseVideo();
            if(null!=mPlayStatPresenter){
                mPlayStatPresenter.saveCurrentPlayProgress();
            }
             //如果上次的播放的不为空,更新其时长
            if(null!=mPlayingCourseWare){

            }
        }

        this.mPlayingCourseWare=lessionInfo;
        if(mCourseListDialog!=null&&isRefreshDialog){
            mCourseListDialog.setCurrentSelectId(lessionInfo.coursewareId);
        }

        if(null!=mPlayStatPresenter){
            mPlayStatPresenter.reset();
        }
        //先本地再网络
        mLastSaveTime=  mReadPrefStore.getInt(String.valueOf(mPlayingCourseWare.coursewareId), 0);;
        if(mPlayingCourseWare instanceof PurchasedCourseBean.Data){
            PurchasedCourseBean.Data tmpCourse=(PurchasedCourseBean.Data)mPlayingCourseWare;
            if(tmpCourse.alreadyStudyTime!=tmpCourse.coursewareTimeLength){
                mLastSaveTime=tmpCourse.alreadyStudyTime;
            }else
                mLastSaveTime=0;
        }

        if (mPlayingCourseWare.videoType == 5) {//音频
            startPlayMusic(lessionInfo);
            if(mPlayerView.getTopViewPresenter()!=null){
                mPlayerView.getTopViewPresenter().setTitle("");
            }
            return;
        }
        bindBgPlayPresent();
        if(mIsLocalVideo||mPlayingCourseWare.downStatus== DownBtnLayout.FINISH){
            mStartPlayBtn.setVisibility(View.GONE);
            mPlayerView.setVideoPath(lessionInfo.targetPath);
        }else{
            //第一个参数为百家云后台配置的视频id，第二个参数为视频token
            if (!TextUtils.isEmpty(lessionInfo.videoId) &&
                    !TextUtils.isEmpty(lessionInfo.token)) {
                //播放视频
                mStartPlayBtn.setVisibility(View.GONE);
                mPlayerView.setVideoId(StringUtils.parseLong(lessionInfo.videoId),lessionInfo.token);
            }else {
                ToastUtils.showShort("视频暂时无法播放，请联系客服");
                return;
            }
        }
        if(mPlayerView.getTopViewPresenter()!=null){
            mPlayerView.getTopViewPresenter().setTitle("");
        }
        mPlayerView.playVideo(mLastSaveTime);

    }

    //选中播放
    private void setCatalogSelected(CourseWareInfo currentCourseWare,int hasPlayTime){
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment =fm.findFragmentByTag("目录");
        if((fragment!=null)&&fragment.isAdded()&&(!fragment.isDetached())&&(currentCourseWare!=null)){
            if(fragment instanceof OnPlaySelectListener){
               ((OnPlaySelectListener)fragment).onSelectChange(currentCourseWare.coursewareId,hasPlayTime);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPlayerView != null) {
            mPlayerView.onConfigurationChanged(newConfig);
        }
        if (mDialogSelectChange&&this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setCatalogSelected(mPlayingCourseWare,0);
        }
        if(null!=mScrollContainer){
            boolean isLandscape=this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            mScrollContainer.setLandScape(isLandscape);
        }

        if (mStatisticsUtil != null){
            mStatisticsUtil.updateViewMode(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, false, false);
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.e("onBackPressed",getSupportFragmentManager().getBackStackEntryCount()+"");
        if(getSupportFragmentManager().getBackStackEntryCount()>=1){
            showInclassFragment(false,0,"");
            return;
        }
        if(mLockScreenBtn!=null&&(null!=mPlayerView)){
            if("1".equals(mLockScreenBtn.getTag())){
                mPlayerView.removeCallbacks(mDelayRunable);
                mLockScreenBtn.setVisibility(View.VISIBLE);
                mPlayerView.postDelayed(mDelayRunable,3000);
                return;
            }else {
                mPlayerView.removeCallbacks(mDelayRunable);
            }
        }

        //pad横屏时，非缓存返回键收起视频区域
        if(!mIsLocalVideo&&(CommonUtils.isPad(this))){
            boolean isLandScape= getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            if(isLandScape){
                if(null!=mBjBottomViewPresenter&&(!mBjBottomViewPresenter.isCurOrientationPortrait())){
                    mBjBottomViewPresenter.setOrientation( BJPlayerView.VIDEO_ORIENTATION_PORTRAIT);

                    if(null!=mPlayerView){
                        //mPlayerView.onConfigurationChanged();
                        int screeenWitdh=Math.min(DensityUtils.getScreenWidth(this),DensityUtils.getScreenHeight(this));
                        final int distanceHeight=(int)(screeenWitdh*0.56);
                        android.view.ViewGroup.LayoutParams   var1 = mPlayerView.getLayoutParams();
                        var1.width = -1;
                        var1.height = distanceHeight;
                        mPlayerView.setLayoutParams(var1);
                    }
                    return;
                }
             }
        }
        // 缓存或者pad时返回键直接返回
        if(mIsLocalVideo||CommonUtils.isPad(this)){
            if(mStartForResult&&mHasPlay){
                setResult(Activity.RESULT_OK);
            }
            super.onBackPressed();
            return;
        }
        //正常的手机返回
        if ((!mPlayerView.onBackPressed() )) {
            if(mStartForResult&&mHasPlay){
                setResult(Activity.RESULT_OK);
            }
            if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(this);
            }
            super.onBackPressed();
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        } else {
             finish();
        }
    }

    //是否解除了绑定 音频播放服务
    private boolean mHasUnBindServer=false;

    //是否刷新了课件列表中的选中(后台播放时尽量不操作Ui)
    private boolean mHasRefreshSelect=false;

    private StatusChangedEvent mStatusChangEvent;
    private boolean mPlayingPause=false;
    @Override
    protected void onResume() {
        super.onResume();
        if(mHasUnBindServer){
            mHasUnBindServer=false;
            if(null!=mBjBottomViewPresenter){
                mBjBottomViewPresenter.setLayoutShow(false);
            }
        }
        if(null!=mStatusChangEvent){
            onUpdatePlayStatus(mStatusChangEvent);
            mStatusChangEvent=null;
        }
        if(mHasRefreshSelect){
            mHasRefreshSelect=false;
            setCatalogSelected(mPlayingCourseWare,0);
            if(!mIsLocalVideo)
                getJudgeInfo();
        }
        //音量监听
        volumeChangeListener();
        //随堂练习已加载
        if(mFragmentContainer!=null&&mFragmentContainer.getChildCount()>0){
            return;
        }
        if (mPlayerView != null&&(mPlayingCourseWare!=null)&&(mPlayingCourseWare.videoType==1)) {
          //  boolean oldflag= mPlayerView.isFromBackground();
           /* if(mPlayingPause){
                mPlayerView.onResume();
                mPlayingPause=false;
            }*/
             /* if(oldflag){
                if(null!=mBjCenterViewPresenter)
                    mBjCenterViewPresenter.setCoverImageShow(false);
            }*/
           // mScreenRoateUtil.start(this);
        }

  /*      if (mBjTopViewPresenter != null ) {
            mBjTopViewPresenter.resume();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFragmentContainer!=null&&mFragmentContainer.getChildCount()>0){
            return;
        }
        if (mPlayerView != null&&(mPlayingCourseWare!=null)&&(mPlayingCourseWare.videoType==1)) {
           /* if(mPlayerView.isPlaying()){
                mPlayingPause=true;
                mPlayerView.onPause();
            }*/

            //此处解决onPause时黑色屏的问题，暂时注掉
           /*    if(null!=mBjCenterViewPresenter)
                mBjCenterViewPresenter.setCoverImageShow(true);
            if(null!=mBjBottomViewPresenter)
                mBjBottomViewPresenter.setLayoutShow(false);*/
           // mScreenRoateUtil.stop();*/
        }
        if (mVolumeObserver != null)
            UniApplicationContext.getContext().getContentResolver().unregisterContentObserver(mVolumeObserver);
     }


    private PlayManager.ServiceToken mToken;

    public PlayManager.ServiceToken getServiceToken(){
        return mToken;

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(null!=mBgPlayPresenter){
            mBgPlayPresenter.detachView();
            mBgPlayPresenter=null;
        }
        if(null!=mPlayStatPresenter){
            mPlayStatPresenter.saveCurrentPlayProgress();
            mPlayStatPresenter.detactView();
            mPlayStatPresenter=null;
        }
        if(null!=mMusicPresenter){
             mMusicPresenter.detachView();
        }
        if (mPlayerView != null) {
            mPlayerView.removeCallbacks(mDelayRunable);
            mPlayerView.setInterceptTouch(null);
            mPlayerView.setPlayerTapListener(null);
            mPlayerView.setOnPlayerViewListener(null);
            if(null!=mBjBottomViewPresenter) {
                mBjBottomViewPresenter.setIPlayerCenterContact(null);
                mBjBottomViewPresenter.setOnShowInClassListener(null);
                mBjBottomViewPresenter=null;
            }
            if(null!=mBjCenterViewPresenter){
                mBjCenterViewPresenter.onDestory();
                mBjCenterViewPresenter=null;
            }

            mPlayerView.onDestroy();
        }
        if (mBjTopViewPresenter != null ) {
            mBjTopViewPresenter.destory();
            mBjTopViewPresenter=null;
        }
        if(mDanMuDialogbuilder!=null){
            mDanMuDialogbuilder.setOnDanmaSendListener(null);
            mDanMuDialogbuilder.destory();
        }
        if(mAfterClassPractDialog!=null){
            mAfterClassPractDialog.destory();
        }
        super.onDestroy();
        if(null!=mCourseListDialog){
            mCourseListDialog.destory();
        }
        if (mToken != null) {
            PlayManager.unbindFromService(mToken);
            mToken = null;

            final Intent i = new Intent(this, MusicPlayerService.class);
            i.setAction(MusicPlayerService.SERVICE_CMD);
            i.putExtra(MusicPlayerService.CMD_NAME, MusicPlayerService.CMD_CLEAR);
            this.startService(i);
        }

        UMShareAPI.get(this).release();
        ToastUtils.cancle();
    }


    @Override
    public void finish() {
        super.finish();
        if (mStatisticsUtil != null)
            mStatisticsUtil.onVideoStop(getCurrentPlayPositon());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAudioManager == null) return super.onKeyDown(keyCode, event);
        ;
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                try {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_RAISE, 0);
                    return super.onKeyDown(keyCode, event);
                } catch (Exception e) {
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                try {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_RAISE, 0);
                    return super.onKeyDown(keyCode, event);
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private CourseApiService.BooleanResult mJudgeResult;

    private void getJudgeInfo(){
        if (mPlayingCourseWare == null) return;
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().checkLessionEvalute(String.valueOf(mPlayingCourseWare.coursewareId), mPlayingCourseWare.id),
                new NetObjResponse<CourseApiService.BooleanResult>() {
                    @Override
                    public void onError(String message, int type) { }

                    @Override
                    public void onSuccess(BaseResponseModel<CourseApiService.BooleanResult> model) {
                        mJudgeResult = model.data;
                        if (mBjCenterViewPresenter != null && mJudgeResult != null)
                            mBjCenterViewPresenter.showJudgeImg(!mJudgeResult.result);
                    }
                });
    }

    private void judgeCourse(){

        if (mJudgeResult == null || mPlayingCourseWare == null)
            return;
          String courseWareid=String.valueOf(mPlayingCourseWare.coursewareId);
         int afterCourseNum=mPlayingCourseWare.afterCoreseNum;
         String parentId=mPlayingCourseWare.parentId;
         String classId=String.valueOf(mPlayingCourseWare.classId);
        if (mJudgeResult.isFinish != 1){
            ToastUtils.showShort("观看完课程可评价");
            return;
        }
        if (!mJudgeResult.result)
            CourseJudgeActivity.newInstance(BJRecordPlayActivity.this, classId, courseWareid,parentId);
        else if(null==mToken){
            showAfterclassTip(afterCourseNum);
        }
    }

    public void onRefreshToken(){
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getPlaybackToken(mPlayingCourseWare.coursewareId, mCourseId, mPlayingCourseWare.videoType), new NetObjResponse<CourseApiService.DownToken>() {
            @Override
            public void onSuccess(BaseResponseModel<CourseApiService.DownToken> model) {
                CourseApiService.DownToken downToken = model.data;
                if(TextUtils.equals(mPlayingCourseWare.token, downToken.token) || TextUtils.isEmpty(downToken.token)){
                    onError(null, -1);
                    return;
                }
                mPlayingCourseWare.token = downToken.token;
                mPlayerView.setVideoId(StringUtils.parseLong(mPlayingCourseWare.videoId), mPlayingCourseWare.token);
                mPlayerView.playVideo(mLastSaveTime);
            }

            @Override
            public void onError(String message, int type) {
                mBjCenterViewPresenter.showError(5001,"token请求失败");
            }
        });
    }

     // SQLiteHelper.getInstance().upDatePlayProgress(course.JoinCode, (isFinish?totalTime:currentTime));

    private void volumeChangeListener(){
        mVolumeObserver = new ContentObserver(UniApplicationLike.getApplicationHandler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange);
               if (uri != null && uri.toString().contains("volume_music_speaker") && mStatisticsUtil != null){
                    int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (mStatisticsUtil != null && mPlayerView != null)
                        mStatisticsUtil.onVideoOperate(getCurrentPlayPositon(), String.valueOf(volume));
                }
                LogUtils.d("Volume", "Uri = " + uri);
            }
        };
        UniApplicationContext.getContext().getContentResolver().registerContentObserver(Uri.parse("content://settings/system/volume_music_speaker"), false, mVolumeObserver);
    }


    //1点播2直播3直播回放
    private void checkLearnReportStatus(CourseWareInfo playingCourseWare){
        if(null==playingCourseWare) return;
        if(playingCourseWare.videoType!=1&&playingCourseWare.videoType!=2){
           return;
        }
        int videoType= playingCourseWare.videoType;
        String syllabusIdId= playingCourseWare.id;
        int courseWareId= playingCourseWare.coursewareId;

        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment =fm.findFragmentByTag("目录");
        if((fragment!=null)&&fragment.isAdded()&&(!fragment.isDetached())){
            if(fragment instanceof CourseCatalogFragment){
                ((CourseCatalogFragment)fragment).refreshLearnReportStatus(videoType,syllabusIdId,courseWareId);
            }
        }
    }
}
