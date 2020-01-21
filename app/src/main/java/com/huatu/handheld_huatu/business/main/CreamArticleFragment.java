package com.huatu.handheld_huatu.business.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleTabData;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.netease.hearttouch.router.HTRouter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by saiyuan on 2019/2/19.ztk://exam/articles
 * 备考精华（乐见）
 */
@HTRouter(url = {"ztk://exam/articles{fragment2}"},  needLogin = false)
public class CreamArticleFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.cream_article_viewpager)
    ViewPager viewPager;
    @BindView(R.id.cream_viewpager_tab)
    SmartTabLayout viewPagerTab;
    @BindView(R.id.rl_smart_tab)
    RelativeLayout rl_smart_tab;
    private TopActionBar topActionBar;
    private List<String> title = new ArrayList<>();
    private List<CreamArticleTabData> mData=new ArrayList<>();
    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_cream_article_layout;
    }

    @Override
    protected void onInitView() {
        initTitleBar();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        refreshTitle();
        initListener();

    }

    private void initListener() {
        layout_net_unconnected.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
        layout_nodata.setOnClickListener(this);
    }

    private void refreshTitle() {
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
        mActivity.showProgress();
        ServiceProvider.getCreamArticleTab(compositeSubscription,new NetResponse(){
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mActivity.hideProgress();
                if (model!=null&&model.data!=null){
                    mData=model.data;
                }
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
                mActivity.hideProgress();
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

    private void initViewPager() {
        viewPagerTab.setDividerColors(android.R.color.white);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager != null) {
            List<Fragment> fragmentList = fragmentManager.getFragments();
            if (fragmentList != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentList) {
                    if (fragment != null && fragment.getClass().getSimpleName().equals("CreamArticleListFragment")) {
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
            ids.putInt("ID", mData.get(i).type);
            pages.add(FragmentPagerItem.of(title.get(i), CreamArticleListFragment.class, ids));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
    }

    private void initTitleBar() {
        topActionBar = (TopActionBar) rootView.findViewById(R.id.fragment_title_bar);
        topActionBar.setTitle("乐见");
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);

    }
}
