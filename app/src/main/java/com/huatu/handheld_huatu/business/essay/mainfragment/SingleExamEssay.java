package com.huatu.handheld_huatu.business.essay.mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.SearchActivity;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.customview.HomeFragmentTitleView;
import com.huatu.handheld_huatu.business.arena.newtips.NewTipsManager;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.business.essay.adapter.EssayHomeAdapter;
import com.huatu.handheld_huatu.business.essay.bean.EssayHomeType;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseTabData;
import com.huatu.handheld_huatu.business.essay.mainfragment.fragment_single.EssayListPaperFragment;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.CustomScrollBar;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 申论主页
 */
public class SingleExamEssay extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SingleExamEssay";

    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.viewpager_tab)
    SmartTabLayout viewPagerTab;

    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<SingleExerciseTabData> mData = new ArrayList<>();
    private ArrayList<EssayHomeType> mEssayHome = new ArrayList<>();
    private HomeFragmentTitleView topActionBar;
    private CustomLoadingDialog mDailyDialog;
    private RecyclerView mRecyclerView;
    private CustomScrollBar customScrollBar;
    private EssayHomeAdapter mAdapter;

    private final String[] homeNames = new String[]{"套题", "文章写作", "模考大赛", "批改记录", "收藏", "下载"};
    private final int[] homeIcons = new int[]{R.drawable.essay_paper, R.drawable.essay_arguement, R.drawable.essay_mokao, R.drawable.essay_correct_record, R.drawable.essay_collection, R.drawable.essay_download};

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMessage(MessageEvent event) {
        if (event.message == MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY) {
            topActionBar.updateViews(1);
        }
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.single_exam_efragment;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initTitleBar();
        initListener();
        initEssayHomeData();
    }

    @Override
    public void onLoadData() {
        refreshTitle();
        getMatchIdForNewTip();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_nodata:
                layout_nodata.setVisibility(View.GONE);
                onLoadData();
                break;
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                viewPagerTab.setVisibility(View.VISIBLE);
                onLoadData();
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                onLoadData();
                break;
        }
    }

    private void initTitleBar() {
        topActionBar = rootView.findViewById(R.id.essay_home_fragment_title_view_id);
        topActionBar.setDividerShow(true);
        topActionBar.updateViews(1);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络设置");
                    return;
                }
                ExamTargetAreaActivity.newIntent(mActivity, null);
            }

            @Override
            public void onRightButton2Click(View view) {
            }

            @Override
            public void onRightButtonClick(View view) {
                Intent intent = new Intent(mActivity, SearchActivity.class);
                intent.putExtra("isFromEssay", true);
                startActivity(intent);
//
            }
        });
    }

    private void initListener() {
        layout_nodata.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
    }

    private void initEssayHomeData() {
        List<EssayHomeType> mDatas = new ArrayList<>();
        for (int i = 0; i < homeIcons.length; i++) {
            EssayHomeType mHomeType = new EssayHomeType();
            mHomeType.name = homeNames[i];
            mHomeType.icon = homeIcons[i];
            mHomeType.targetId = i;
            mDatas.add(mHomeType);
        }
        mEssayHome.addAll(mDatas);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.essay_homef_type_rv);
        customScrollBar = rootView.findViewById(R.id.scroll_bar);
        mRecyclerView.setHasFixedSize(true);//设置固定大小
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        LinearLayoutManager mLayoutManage = new LinearLayoutManager(mActivity);
        mLayoutManage.setOrientation(OrientationHelper.HORIZONTAL);//设置滚动方向，横向滚动
        mRecyclerView.setLayoutManager(mLayoutManage);
        mAdapter = new EssayHomeAdapter(mEssayHome, mActivity, compositeSubscription);
        mRecyclerView.setAdapter(mAdapter);
        customScrollBar.setRecycleView(mRecyclerView);
    }

    private void initViewPager() {
        viewPagerTab.setDividerColors(android.R.color.white);
        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        for (int i = 0; i < title.size(); i++) {
            Bundle ids = new Bundle();
            ids.putInt("TITLE", mData.get(i).id);
            ids.putSerializable("TITLES", mData.get(i));
            pages.add(FragmentPagerItem.of(title.get(i), EssayListPaperFragment.class, ids));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(100);
        viewPagerTab.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SpUtils.setSelectPoint(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int mSel = SpUtils.getSelectPoint();
        if (mSel != -1) {
            viewPager.setCurrentItem(mSel);
        }
    }

    public void refreshTitle() {
        if (!NetUtil.isConnected()) {
            viewPager.setVisibility(View.GONE);
            viewPagerTab.setVisibility(View.GONE);
            layout_net_error.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
            layout_net_unconnected.setVisibility(View.VISIBLE);
            if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
                ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            }
            return;
        } else {
            viewPager.setVisibility(View.VISIBLE);
            viewPagerTab.setVisibility(View.VISIBLE);
            layout_net_unconnected.setVisibility(View.GONE);
        }
        title.clear();
        if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
        }
        ServiceProvider.getSingleExerciseTab(compositeSubscription, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                dismissLoadingDialog();
                if (layout_net_error != null)
                    layout_net_error.setVisibility(View.GONE);
                if (layout_net_unconnected != null)
                    layout_net_unconnected.setVisibility(View.GONE);
                mData = (ArrayList<SingleExerciseTabData>) model.data;
                if (!mData.isEmpty()) {
                    showData();
                } else {
                    if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
                        ToastUtils.showEssayToast("网络不稳定，请稍后点击屏幕重试");
                    }
                    if (layout_nodata != null)
                        layout_nodata.setVisibility(View.VISIBLE);
                    if (layout_net_error != null)
                        layout_net_error.setVisibility(View.GONE);
                    if (layout_net_unconnected != null)
                        layout_net_unconnected.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
                if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
                    ToastUtils.showEssayToast("网络不稳定，请稍后点击屏幕重试");
                }
                if (layout_nodata != null)
                    layout_nodata.setVisibility(View.GONE);
                if (viewPager != null)
                    viewPager.setVisibility(View.GONE);
                if (viewPagerTab != null)
                    viewPagerTab.setVisibility(View.GONE);
                if (layout_net_error != null)
                    layout_net_error.setVisibility(View.VISIBLE);
                if (layout_net_unconnected != null)
                    layout_net_unconnected.setVisibility(View.GONE);
            }
        });

    }

    private void showData() {
        for (int i = 0; i < mData.size(); i++) {
            title.add(mData.get(i).name);
        }
        initViewPager();
        if (layout_nodata != null)
            layout_nodata.setVisibility(View.GONE);
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomLoadingDialog(mActivity);
        }
        mDailyDialog.show();
    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMatchIdForNewTip() {
        if (!NetUtil.isConnected()) {
            return;
        }
        ServiceProvider.getMatchIdForNewTip(compositeSubscription, new NetResponse() {
            @Override
            public void onError(final Throwable e) {

            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                List<TipNewBean> list = model.data;
                if (list != null && list.size() > 0) {
                    for (TipNewBean tipNewBean : list) {
                        if (tipNewBean.match != null && tipNewBean.match.length > 0)
                            tipNewBean.matchReadFlag = new int[tipNewBean.match.length];
                        if (tipNewBean.small != null && tipNewBean.small.length > 0)
                            tipNewBean.smallReadFlag = new int[tipNewBean.small.length];
                    }
                    ArrayList<TipBean> tipBeans = NewTipsManager.newInstance().updateTips(SpUtils.getUserCatgory(), Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS, list);
                    updateNewTips(tipBeans);
                }
            }
        });
    }

    private void updateNewTips(ArrayList<TipBean> tipBeans) {
        if (mEssayHome != null && mEssayHome.size() > 0 && tipBeans != null && tipBeans.size() > 0) {
            TipBean tipBean = tipBeans.get(0);  // 模考大赛
            for (EssayHomeType essayHomeType : mEssayHome) {
                if (essayHomeType.name.equals("模考大赛")) {
                    essayHomeType.tipNum = tipBean.tipNum;
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<TipBean> homeTips = NewTipsManager.newInstance().getHomeTips(SpUtils.getUserCatgory(), Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
        updateNewTips(homeTips);
    }

    @Override
    protected void onSaveState(Bundle savedInstanceState) {
        if (mData != null && mData.size() > 0) {
            savedInstanceState.putSerializable("mData", mData);
        }
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        mData = (ArrayList<SingleExerciseTabData>) savedInstanceState.getSerializable("mData");
        if (mData != null && mData.size() > 0) {
            showData();
        } else {
            onLoadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
