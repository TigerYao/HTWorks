package com.huatu.handheld_huatu.business.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.AdvertiseHolder;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamAreaActivity;
import com.huatu.handheld_huatu.business.arena.activity.DailySpecialActivity;
import com.huatu.handheld_huatu.business.arena.adapter.HomeAdvAdapter;
import com.huatu.handheld_huatu.business.arena.customview.HomeFragmentTitleView;
import com.huatu.handheld_huatu.business.arena.customview.HomeIconsView;
import com.huatu.handheld_huatu.business.arena.customview.HomeRefreshNestedScrollView;
import com.huatu.handheld_huatu.business.arena.helper.HomeTreeHelper;
import com.huatu.handheld_huatu.business.arena.newtips.NewTipsManager;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.BottomDialog;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.HomeFDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseItem;
import com.huatu.handheld_huatu.mvpmodel.HomeAdvBean;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaVersion;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;
import com.huatu.handheld_huatu.mvppresenter.impl.HomePresenterImpl;
import com.huatu.handheld_huatu.mvpview.HomeView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * @author zhaodongdong
 * 行测主页
 * .header("subject", String.valueOf(SpUtils.getUserSubject()))
 * .header("catgory", String.valueOf(SpUtils.getUserCatgory()))
 */
public class HomeFragment extends BaseFragment implements HomeView, OnItemClickListener {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.home_fragment_title_view_id)
    HomeFragmentTitleView mTitleBar;                        // TitleBar，TiKuFragment中的bar分别包含在行测和申论自己的页面中
    @BindView(R.id.refresh_layout)
    HomeRefreshNestedScrollView refreshLayout;              // 下拉刷新布局，内容在类内部映射出来的，布局是home_scroll_view

    private ConvenientBanner mHomeAdvertise;                // 轮播广告
    private HomeIconsView homeIconsView;                    // RecyclerView，一横排图标

    // 中间可展开的广告
    private View viewDivider;
    private RelativeLayout rlAdv;                           // 广告头布局
    private TextView tvAdvTitle;                            // title
    private ImageView ivAdvMore;
    //    private TextView tvAdvMore;
    private RecyclerView rvAdv;

    // ----- 新的Tree数据
    private TextView tvPractice;                            // 专项练习title
    private RecyclerView rlTree;                            // 题库树
    private HomeTreeHelper homeTreeHelper;                  // 题库知识树展开合并...帮助类
    // ----- 新的Tree数据

    private RelativeLayout rlBottomLine;                    // 底线

    private LinearLayout llNoDataX;                         // 列表中无数据显示

    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;                                  // 全部无数据显示


    private HomePresenterImpl mPresenter;                   // 网络访问

    private BaseListResponseModel<AdvertiseConfig> homeAdvertiseList;               // 轮播广告基础数据
    private List<AdvertiseConfig> mAdvertiseList;                                   // 轮播广告内容
    private List<String> imageUrls = new ArrayList<>();                             // 轮播图Url列表
    private ArrayList<HomeIconBean> icons;                                          // 首页icons

    private HomeAdvBean homeAdvBean;                                                // 中间的广告列表
    private ArrayList<AdvertiseConfig> advList;                                     // 中间的广告列表数据
    private HomeAdvAdapter homeAdvAdapter;                                          // 广告列表adapter

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_home;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMessage(MessageEvent event) {
        if (event.message == MessageEvent.HOME_FRAGMENT_MSG_GET_DAILYINFO) {                                        // 获取每日特训做题页面的列表数据
            mPresenter.getDailyInfo();
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_START_NTEGRATED_APPLICATION) {                   // 跳综合应用
            ArenaExamAreaActivity.newInstance(mActivity, ArenaConstant.START_NTEGRATED_APPLICATION);
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_UPDATE_VIEW) {                       // 切换地区考试类型，刷新数据
            getDataUpdateViews();
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_TREE_DATA_UPDATE_VIEW_REFRESH_ALL) {        // 真题演练 做题交卷/保存 为了保持展开，获取全部数据，解析，然后进行数据比对
            mPresenter.getHomeTreeData();
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_REFRESH_TITLE) {                            // 获取科目信息，刷新title
            updateTitleView();
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA) {                        // 申论下点击题库，刷新模考大赛、小模考角标
            if (homeIconsView != null) {
                ArrayList<TipBean> homeTips = NewTipsManager.newInstance().getHomeTips(SpUtils.getUserCatgory(), SpUtils.getUserSubject());
                homeIconsView.updateViews(homeTips, null);
            }
        }
    }

    @OnClick({R.id.ll_no_data, R.id.ll_no_data_x, R.id.iv_setting})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_no_data:
            case R.id.ll_no_data_x:
                onLoadData();
                break;
//            case R.id.iv_class:                     // 精品微课
//                ExperienceCourseFragment.launch(mActivity);
//                break;
//            case R.id.iv_news:                      // 备考精华
//                BaseFrgContainerActivity.newInstance(mActivity, CreamArticleFragment.class.getName(), null);
//                break;
            case R.id.iv_setting:                   // 点击专项练习的设置
                showBottomSetting();
                break;
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();

        compositeSubscription = new CompositeSubscription();
        mPresenter = new HomePresenterImpl(compositeSubscription, this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // 获取服务器时间并储存
        ServerTimeUtil.newInstance().initServerTime();

        NestedScrollView scrollView = refreshLayout.getScrollView();
        mHomeAdvertise = scrollView.findViewById(R.id.home_advertise);
        homeIconsView = scrollView.findViewById(R.id.homef_paper_type_view);
        viewDivider = scrollView.findViewById(R.id.view_divider);
        rlAdv = scrollView.findViewById(R.id.rl_adv);
        tvAdvTitle = scrollView.findViewById(R.id.tv_adv_title);
        ivAdvMore = scrollView.findViewById(R.id.iv_adv_arrow);
//        tvAdvMore = scrollView.findViewById(R.id.tv_adv_more);
        rvAdv = scrollView.findViewById(R.id.rv_adv);
        rlBottomLine = scrollView.findViewById(R.id.rl_line);
        llNoDataX = scrollView.findViewById(R.id.ll_no_data_x);
        tvPractice = scrollView.findViewById(R.id.tv_practice);
        rlTree = scrollView.findViewById(R.id.rl_tree);

        // 广告轮播图，根据手机还是pad，改变高度。
        if (mHomeAdvertise != null) {
            int height;
            if (CommonUtils.isPad(mActivity)) {                 // pad固定高度250dp
                height = DisplayUtil.dp2px(250);
            } else {  //int) (((double) width) * 0.45)
                height = (int) (Math.min(DensityUtils.getScreenWidth(mActivity), DensityUtils.getScreenHeight(mActivity)) * 0.45);
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHomeAdvertise.getLayoutParams();
            params.height = height;
            mHomeAdvertise.setLayoutParams(params);
        }

        homeTreeHelper = new HomeTreeHelper(mActivity, mPresenter, rlTree, llNoDataX, rlBottomLine, llNoData);

        refreshLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<NestedScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
                onLoadData();
                refreshLayout.onRefreshComplete();
            }
        });

        updateTitleView();
        updateIconView();
        initAdvListView();
    }

    @Override
    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        SignUpTypeDataCache.getInstance().getCategoryListNet(0, compositeSubscription, new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_REFRESH_TITLE));
        mPresenter.getHomeTreeDataById(0);
        mPresenter.getHomeAdvertise();
        mPresenter.getMatchIdForNewTip();
        mPresenter.getHomeIconsView();
        mPresenter.getHomeAdvList();
        if ((SignUpTypeDataCache.getInstance().getSignUpType() == Type.SignUpType.CIVIL_SERVANT || SignUpTypeDataCache.getInstance().getSignUpType() == Type.SignUpType.PUBLIC_INSTITUTION)
                && (SpUtils.isSimulationContest() || ArenaDataCache.getInstance().isFirstLoginIn)) {
            SpUtils.setIsSimulationContest(false);
            ArenaDataCache.getInstance().isFirstLoginIn = false;
        }
    }

    /**
     * 切换地区考试类型，刷新数据
     */
    public void getDataUpdateViews() {
        homeTreeHelper.clearData(1);
        onLoadData();
        updateTitleView();
        if (icons != null) {
            icons.clear();
        }
        updateIconView();
    }

    private void updateTitleView() {
        mTitleBar.updateViews(0);
    }

    private void updateIconView() {
        homeIconsView.updateViews(null, icons);
    }

    // 初始化可点击展开的广告列表
    private void initAdvListView() {
        advList = new ArrayList<>();

        rvAdv.setNestedScrollingEnabled(false);
        rvAdv.setLayoutManager(new GridLayoutManager(mActivity, 3));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(200);
        animator.setRemoveDuration(200);
        rvAdv.setItemAnimator(animator);

        homeAdvAdapter = new HomeAdvAdapter(mActivity, advList, compositeSubscription);

        rvAdv.setAdapter(homeAdvAdapter);
    }

    //-------------------------------------HomeView

    /**
     * HomeView
     * 广告轮播内容数据回调
     */
    @Override
    public void updateAdvertise(BaseListResponseModel<AdvertiseConfig> list) {
        homeAdvertiseList = list;
        if (homeAdvertiseList == null || homeAdvertiseList.code != 1000000
                || homeAdvertiseList.data == null || homeAdvertiseList.data.size() <= 0) {
            if (mHomeAdvertise != null) {
                mHomeAdvertise.setVisibility(View.GONE);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_NO_LOOPER));
            }
            return;
        }
        if (mHomeAdvertise != null) {
            mHomeAdvertise.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_HAS_LOOPER));

        }
        imageUrls.clear();
        mAdvertiseList = homeAdvertiseList.data;
        for (int i = 0; i < homeAdvertiseList.data.size(); i++) {
            AdvertiseItem params = homeAdvertiseList.data.get(i).params;
            imageUrls.add(params.image);
            HomeFDataCache.getInstance().setScAdvertiseConfig(homeAdvertiseList.data.get(i));
        }

        mHomeAdvertise.setPages(new CBViewHolderCreator<AdvertiseHolder>() {
            @Override
            public AdvertiseHolder createHolder() {
                return new AdvertiseHolder();
            }
        }, imageUrls)
                .setOnItemClickListener(this)
                .setPointViewVisible(true)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setPageIndicator(new int[]{R.mipmap.indicator_looper_solid, R.mipmap.indicator_looper_stroke})
                .setCanLoop(true);
        mHomeAdvertise.setScrollDuration(600);
        if (!mHomeAdvertise.isTurning()) {
            mHomeAdvertise.startTurning(3000);
        }
    }

    /**
     * 轮播广告的跳转
     */
    @Override
    public void onItemClick(int position) {
        if (mAdvertiseList == null || mAdvertiseList.size() == 0) return;
        AdvertiseConfig advertiseConfig = mAdvertiseList.get(position);
        if (advertiseConfig.params == null) return;
        AdvertiseItem params = advertiseConfig.params;
        StudyCourseStatistic.sendHomeBanner(params.id + "", params.title, Type.getCategory(SpUtils.getUserCatgory()));

        MatchCacheData.getInstance().matchPageFrom = "app首页轮播图";
        ZtkSchemeTargetStartTo.startTo(mActivity, params, advertiseConfig.target, false, compositeSubscription);
    }

    /**
     * HomeView
     * 树数据回调
     * 专项练习过后，保存/交卷，都需要获取全部树形结构，然后解析，比对，更新主要数据。
     * 1、递归遍历tree把    id-HomeTreeBeanNew                      存入 treeBeanMap结构，用于更新数据
     * 2、递归遍历把        parentId-ArrayList<HomeTreeBeanNew>     存入 homeTreeCatch，作为缓存数据
     * <p>
     * 注：SparseArray是Android中代替HashMap的key-value结构，性能更好，但是key智能为int。
     */
    @Override
    public void updateTreePoint(BaseListResponseModel<HomeTreeBeanNew> data) {
        homeTreeHelper.updateTreePoint(data);
    }

    /**
     * 获取到了icon按钮
     */
    @Override
    public void updateHomeIcons(ArrayList<HomeIconBean> icons) {
        this.icons = icons;
        ArrayList<TipBean> homeTips = NewTipsManager.newInstance().getHomeTips(SpUtils.getUserCatgory(), SpUtils.getUserSubject());
        homeIconsView.updateViews(homeTips, icons);
    }

    /**
     * 获取当前模考大赛、小模考的id，用于显示红色角标数量
     */
    @Override
    public void updateNewTips(BaseListResponseModel<TipNewBean> data) {
        List<TipNewBean> list = data.data;
        if (list != null && list.size() > 0) {
            for (TipNewBean tipNewBean : list) {
                if (tipNewBean.match != null && tipNewBean.match.length > 0)
                    tipNewBean.matchReadFlag = new int[tipNewBean.match.length];
                if (tipNewBean.small != null && tipNewBean.small.length > 0)
                    tipNewBean.smallReadFlag = new int[tipNewBean.small.length];
            }
            ArrayList<TipBean> tipBeans = NewTipsManager.newInstance().updateTips(SpUtils.getUserCatgory(), SpUtils.getUserSubject(), list);
            homeIconsView.updateViews(tipBeans, null);
        }
    }

    @Override
    public void updateTreePointById(int parentId, BaseListResponseModel<HomeTreeBeanNew> list) {
        homeTreeHelper.updateTreePoint(parentId, list);
    }

    @Override
    public void getTreePointFail() {
        llNoDataX.setVisibility(View.VISIBLE);
        rlBottomLine.setVisibility(View.GONE);
    }

    @Override
    public void dispatchDaily(DailySpecialBean dailySpecialBean) {
        if (dailySpecialBean != null) {
            Intent intent = new Intent(mActivity, DailySpecialActivity.class);
            intent.putExtra("daily_special_data_bean", dailySpecialBean);
            startActivity(intent);
        } else {
            ToastUtils.showShort("获取数据失败~");
        }
    }

    /**
     * 喜闻、乐见等广告内容
     */
    @Override
    public void updateHomeAdvList(HomeAdvBean homeAdvBean) {

        tvPractice.setText("为你推荐");
        tvAdvTitle.setText("专项练习");

        if (homeAdvBean == null) {
            viewDivider.setVisibility(View.GONE);
            rlAdv.setVisibility(View.GONE);
            rvAdv.setVisibility(View.GONE);
            return;
        }

        this.homeAdvBean = homeAdvBean;

        HomeAdvBean.HomeAdvTitle customizeDoc = homeAdvBean.customizeDoc;
        if (customizeDoc != null) {
            tvPractice.setText(!StringUtils.isEmpty(customizeDoc.document) ? customizeDoc.document : customizeDoc.defaultDoc);
        }

        HomeAdvBean.HomeAdvTitle homeOperateDoc = homeAdvBean.homeOperateDoc;
        if (homeOperateDoc != null) {
            tvAdvTitle.setText(!StringUtils.isEmpty(homeOperateDoc.document) ? homeOperateDoc.document : homeOperateDoc.defaultDoc);
        }

        advList.clear();
        if (homeAdvBean.messageList != null) {
            advList.addAll(homeAdvBean.messageList);
        }
        homeAdvAdapter.notifyDataSetChanged();

        viewDivider.setVisibility(View.VISIBLE);
        rlAdv.setVisibility(View.VISIBLE);
        rvAdv.setVisibility(View.VISIBLE);

//        tvAdvMore.setVisibility(View.VISIBLE);
        ivAdvMore.setVisibility(View.VISIBLE);

        rlAdv.setOnClickListener(null);

        if (advList.size() == 0) {                  // 没有广告
            viewDivider.setVisibility(View.GONE);
            rlAdv.setVisibility(View.GONE);
            rvAdv.setVisibility(View.GONE);
        } else if (advList.size() <= 3) {           // 不可展开
//            tvAdvMore.setVisibility(View.GONE);
            ivAdvMore.setVisibility(View.GONE);
        } else {                                    // 可展开
            homeAdvAdapter.setCount(3);
            homeAdvAdapter.notifyDataSetChanged();
            rlAdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (advList.size() > 3 && homeAdvAdapter.getCount() == 3) {     // 展开
                        ivAdvMore.setRotation(0);
                        homeAdvAdapter.setCount(0);
                        homeAdvAdapter.notifyItemRangeInserted(3, advList.size());
                    } else {                                                        // 收起
                        ivAdvMore.setRotation(180);
                        homeAdvAdapter.setCount(3);
                        homeAdvAdapter.notifyItemRangeRemoved(3, advList.size());
                    }
                }
            });
        }
    }

    @Override
    public void refreshRealExamVersion(BaseResponseModel<ExamAreaVersion> realExamVersion) {

    }

    @Override
    public void showProgressBar() {
        if (SpUtils.getUserSubject() != Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
            mActivity.showProgress();
        }
    }

    @Override
    public void dismissProgressBar() {
        mActivity.hideProgress();
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onLoadDataFailed() {

    }
    //-------------------------------------HomeView

    private int selectCount = 0;        // 做题数量
    private int doMode = 0;             // 0、做题 1、背题
    private GridLayout glCount;
    private GridLayout glMode;
    private BottomDialog bottomDialog;

    /**
     * 做题设置
     */
    private void showBottomSetting() {

        selectCount = SpUtils.getHomeQuestionSize();
        doMode = SpUtils.getHomeQuestionMode();

        if (bottomDialog == null) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.home_bottom_setting_layout, null);

            bottomDialog = new BottomDialog(mActivity, view, true, true);

            glCount = view.findViewById(R.id.gl_count);
            glMode = view.findViewById(R.id.gl_mode);

            TextView tvClear = view.findViewById(R.id.tv_clear);
            ImageView ivClose = view.findViewById(R.id.iv_close);
            TextView tvSave = view.findViewById(R.id.tv_save);

            tvClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearAllRecord();
                }
            });

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomDialog.dismiss();
                }
            });

            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpUtils.setHomeQuestionSize(selectCount);
                    SpUtils.setHomeQuestionMode(doMode);
                    bottomDialog.dismiss();
                    ToastUtils.showShort("设置成功");
                    // 设置做题模式成功后，要刷新知识树信息
                    reFreshTreeData();
                    StudyCourseStatistic.doExercisesSetting("专项练习", String.valueOf(selectCount), doMode == 0 ? "做题模式" : "背题模式");
                }
            });

            final int countCount = glCount.getChildCount();
            for (int i = 0; i < countCount; i++) {
                RadioButton button = (RadioButton) glCount.getChildAt(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCount = Integer.valueOf((String) v.getTag());
                        for (int i = 0; i < countCount; i++) {
                            View child = glCount.getChildAt(i);
                            if (!child.getTag().equals(String.valueOf(selectCount))) {
                                ((RadioButton) child).setChecked(false);
                            }
                        }
                    }
                });
            }

            int styleCount = glMode.getChildCount();
            for (int i = 0; i < styleCount; i++) {
                RadioButton button = (RadioButton) glMode.getChildAt(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doMode = Integer.valueOf((String) v.getTag());
                        if (doMode == 0) {
                            RadioButton button = glMode.findViewWithTag("1");
                            button.setChecked(false);
                        } else {
                            RadioButton button = glMode.findViewWithTag("0");
                            button.setChecked(false);
                        }
                    }
                });
            }
        }

        final int countCount = glCount.getChildCount();
        for (int i = 0; i < countCount; i++) {
            RadioButton button = (RadioButton) glCount.getChildAt(i);
            if (button.getTag().equals(String.valueOf(selectCount))) {
                button.setChecked(true);
            } else {
                button.setChecked(false);
            }
        }

        int styleCount = glMode.getChildCount();
        for (int i = 0; i < styleCount; i++) {
            RadioButton button = (RadioButton) glMode.getChildAt(i);
            if (button.getTag().equals(String.valueOf(doMode))) {
                button.setChecked(true);
            } else {
                button.setChecked(false);
            }
        }

        bottomDialog.show();
    }

    private CustomConfirmDialog exitConfirmDialog;

    private String msg = "请慎重选择清空重做，您确认后会导致清除您的错题本，也会重置您的评估报告，但会为您保留历史做题记录和收藏记录，确认要重置吗？";

    // 清空所有做题记录
    private void clearAllRecord() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        if (exitConfirmDialog != null) {
            showClearDialog();
            return;
        }
        showProgressBar();
        ServiceProvider.getClearRecordMsg(compositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                dismissProgressBar();
                showClearDialog();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                dismissProgressBar();
                ConfirmCodeBean.ConfirmCode confirmCode = (ConfirmCodeBean.ConfirmCode) model.data;
                msg = confirmCode.message;
                showClearDialog();
            }
        });
    }

    private void showClearDialog() {
        if (exitConfirmDialog == null) {
            exitConfirmDialog = DialogUtils.createDialog(mActivity, "提示", msg);
            exitConfirmDialog.setTitleColor(Color.parseColor("#ff3f47"));
            exitConfirmDialog.setNegativeButton("取消", null);
            exitConfirmDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressBar();
                    ServiceProvider.getClearRecord(compositeSubscription, new NetResponse() {
                        @Override
                        public void onError(Throwable e) {
                            dismissProgressBar();
                            if (e instanceof ApiException) {
                                ToastUtils.showShort(((ApiException) e).getErrorMsg());
                            } else {
                                ToastUtils.showShort("清空失败，请稍后再试");
                            }
                        }

                        @Override
                        public void onSuccess(BaseResponseModel model) {
                            dismissProgressBar();
                            ConfirmCodeBean.ConfirmCode confirmCode = (ConfirmCodeBean.ConfirmCode) model.data;
                            ToastUtils.showShort(confirmCode.message);
                            bottomDialog.dismiss();
                            compositeSubscription.add(Observable.timer(2, TimeUnit.SECONDS).subscribe(new LPErrorPrintSubscriber<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    mPresenter.getHomeTreeDataById(0);
                                }
                            }));
                        }
                    });
                }
            });
            exitConfirmDialog.setCancelBtnVisibility(true);
        }
        exitConfirmDialog.show();
    }

    /**
     * 修改做题方式，更新题库树
     */
    private void reFreshTreeData() {
        homeTreeHelper.clearData(0);
        mPresenter.getHomeTreeDataById(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHomeAdvertise != null && mHomeAdvertise.isTurning()) {
            mHomeAdvertise.stopTurning();
        }
    }

    boolean isStop;

    @Override
    public void onStop() {
        super.onStop();
        if (ArenaDataCache.getInstance().onclick_ZtkSchemeTargetStartTo) {
            isStop = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ArenaDataCache.getInstance().onclick_ZtkSchemeTargetStartTo) {
            if (isStop) {
                isStop = false;
                ArenaDataCache.getInstance().onclick_ZtkSchemeTargetStartTo = false;
                if (mPresenter != null) {
                    mPresenter.getHomeAdvertise();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mHomeAdvertise != null && homeAdvertiseList != null && homeAdvertiseList.data != null && homeAdvertiseList.data.size() > 1) {
            mHomeAdvertise.startTurning(3000);
        }

        if (homeIconsView != null) {
            ArrayList<TipBean> homeTips = NewTipsManager.newInstance().getHomeTips(SpUtils.getUserCatgory(), SpUtils.getUserSubject());
            homeIconsView.updateViews(homeTips, null);
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser) {
            if (mTitleBar != null) {
                mTitleBar.clearView();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mTitleBar != null) {
                mTitleBar.clearView();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (homeIconsView != null) {
            ArrayList<TipBean> homeTips = NewTipsManager.newInstance().getHomeTips(SpUtils.getUserCatgory(), SpUtils.getUserSubject());
            homeIconsView.updateViews(homeTips, null);
        }
        if (mHomeAdvertise != null) {
            mHomeAdvertise.setcurrentitem(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 系统回收的时候，系统存储状态
     */
    @Override
    protected void onSaveState(Bundle outState) {
        if (homeAdvertiseList != null) {
            outState.putSerializable("adv_data", homeAdvertiseList);
        }
        if (icons != null) {
            outState.putSerializable("icons", icons);
        }
        if (homeAdvBean != null) {
            outState.putSerializable("homeAdvBean", homeAdvBean);
        }
    }

    /**
     * 获取销毁后存储的数据
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        homeAdvertiseList = (BaseListResponseModel<AdvertiseConfig>) savedInstanceState.getSerializable("adv_data");
        icons = (ArrayList<HomeIconBean>) savedInstanceState.getSerializable("icons");
        homeAdvBean = (HomeAdvBean) savedInstanceState.getSerializable("homeAdvBean");
        updateViews();
    }

    /**
     * 这只是为了页面回收更新View
     */
    private void updateViews() {
        mPresenter.getHomeTreeDataById(0);
        if (homeAdvertiseList != null) {
            updateAdvertise(homeAdvertiseList);
        } else {
            mPresenter.getHomeAdvertise();
        }
        updateTitleView();
        updateIconView();
        if (homeAdvBean != null) {
            updateHomeAdvList(homeAdvBean);
        } else {
            mPresenter.getHomeAdvList();
        }
    }
}
