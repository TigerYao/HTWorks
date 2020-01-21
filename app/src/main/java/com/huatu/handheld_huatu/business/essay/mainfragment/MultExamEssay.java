package com.huatu.handheld_huatu.business.essay.mainfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseTabData;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.cusview.ProvinceLView;
import com.huatu.handheld_huatu.business.essay.mainfragment.fragment_mult.MultEssayListPaperFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 套题
 */
public class MultExamEssay extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MultExamEssay";

    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.mult_viewpager)
    ViewPager viewPager;
    @BindView(R.id.mult_viewpager_tab)
    SmartTabLayout viewPagerTab;
    @BindView(R.id.rl_smart_tab)
    RelativeLayout rl_smart_tab;

    private TopActionBar topActionBar;
    private ProvinceLView mprovinceview;
    private ImageView show_province_iv;
    private List<String> title = new ArrayList<>();
    private List<MultiExerciseTabData> mData = new ArrayList<>();
    private int selPos;
    private CustomLoadingDialog mDailyDialog;
    private long areaId;
    private boolean toHome;

    @Override
    public int onSetRootViewId() {
        return R.layout.mult_exam_efragment;
    }

    @Override
    protected void onInitView() {
        initTitleBar();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        refreshTitle();
        show_province_iv = (ImageView) rootView.findViewById(R.id.show_province_iv);
        initListener();
        mprovinceview = (ProvinceLView) rootView.findViewById(R.id.province_view);
        areaId = args.getLong("areaId");
        toHome = args.getBoolean("toHome", false);
    }

    private void initListener() {
        show_province_iv.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
        layout_nodata.setOnClickListener(this);

    }

    private void initViewPager() {
//        viewPager = (ViewPager) rootView.findViewById(R.id.mult_viewpager);
//        viewPagerTab = (SmartTabLayout) rootView.findViewById(R.id.mult_viewpager_tab);
        viewPagerTab.setDividerColors(android.R.color.white);

        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            if (fragmentList != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentList) {
                    if (fragment != null && fragment.getClass().getSimpleName().equals("MultEssayListPaperFragment")) {
                        ft.remove(fragment);
                    }
                }
                ft.commit();
                ft = null;
                fragmentManager.executePendingTransactions();
            }
        }

        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        for (int i = 0; i < title.size(); i++) {
            Bundle ids = new Bundle();
            ids.putLong("ID", mData.get(i).id);
            pages.add(FragmentPagerItem.of(title.get(i), MultEssayListPaperFragment.class, ids));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(0);
        if (title != null) {
            EssayExamDataCache.getInstance().titleArea = title.get(0);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                StudyCourseStatistic.clickStatistic("题库->申论", "套题", title.get(position));
                selPos = position;
                SpUtils.setMultSelectPoint(position);
                if (title != null) {
                    EssayExamDataCache.getInstance().titleArea = title.get(selPos);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).id == areaId) {
                viewPager.setCurrentItem(i);
            }
        }
    }

    private void initTitleBar() {
        topActionBar = (TopActionBar) rootView.findViewById(R.id.fragment_title_bar);
        topActionBar.setTitle("套题");
//        topActionBar.showButtonText("我的批改", TopActionBar.RIGHT_AREA, R.color.text_color_light);
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (toHome) {
                    MainTabActivity.newIntent(mActivity);
                }
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
//                Toast.makeText(mActivity, "我的批改", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EssayExamDataCache.getInstance().titleArea = null;
        } else {
            if (title != null && selPos < title.size()) {
                EssayExamDataCache.getInstance().titleArea = title.get(selPos);
            }
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.show_province_iv:
                mprovinceview.setVisibility(View.VISIBLE);
                if (title != null && title.size() > selPos) {
                    mprovinceview.refreshView(title, selPos, "请选择地区", title.get(selPos), new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            viewPager.setCurrentItem(position);
                            mprovinceview.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                refreshTitle();
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                refreshTitle();
                break;
            case R.id.layout_nodata:
                layout_nodata.setVisibility(View.GONE);
                refreshTitle();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (toHome) {
            MainTabActivity.newIntent(mActivity);
        }
        mActivity.finish();
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
//        Ntalker.getInstance().logout();
    }

    public void refreshTitle() {
        if (!NetUtil.isConnected()) {
            if (viewPager != null) {
                viewPager.setVisibility(View.GONE);
            }
            if (rl_smart_tab != null) {
                rl_smart_tab.setVisibility(View.GONE);
            }
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.VISIBLE);
            }
            if (layout_net_error != null) {
                layout_net_error.setVisibility(View.GONE);
            }
            if (layout_nodata != null) {
                layout_nodata.setVisibility(View.GONE);
            }
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        } else {
            if (viewPager != null) {
                viewPager.setVisibility(View.VISIBLE);
            }
            if (rl_smart_tab != null) {
                rl_smart_tab.setVisibility(View.VISIBLE);
            }
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.GONE);
            }
        }
        title.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingDialog();
            }
        });
//        mActivity.showProgress();
        ServiceProvider.getMultiExerciseTab(compositeSubscription, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
//                mActivity.hideProgress();
                dismissLoadingDialog();
                mData = model.data;
                if (!mData.isEmpty()) {
                    for (int i = 0; i < mData.size(); i++) {
                        title.add(mData.get(i).name);
                    }
                    if (rl_smart_tab != null) {
                        rl_smart_tab.setVisibility(View.VISIBLE);
                    }
                    if (viewPager != null) {
                        viewPager.setVisibility(View.VISIBLE);
                    }
                    initViewPager();
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.GONE);
                    }
                } else {
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    }
                    if (rl_smart_tab != null) {
                        rl_smart_tab.setVisibility(View.GONE);
                    }
                    if (viewPager != null) {
                        viewPager.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
//                mActivity.hideProgress();
                dismissLoadingDialog();
                ToastUtils.showEssayToast("网络不稳定，请稍后重试");
                if (layout_net_error != null) {
                    layout_net_error.setVisibility(View.VISIBLE);
                }
                if (layout_net_unconnected != null) {
                    layout_net_unconnected.setVisibility(View.GONE);
                }
                if (layout_nodata != null) {
                    layout_nodata.setVisibility(View.GONE);
                }
                if (viewPager != null) {
                    viewPager.setVisibility(View.GONE);
                }
                if (rl_smart_tab != null) {
                    rl_smart_tab.setVisibility(View.GONE);
                }
            }
        });

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
            Log.e("AlertDialog  Exception:", e.getMessage() + "");
        }

    }

    public static MultExamEssay newInstance() {
        return new MultExamEssay();
    }

}
