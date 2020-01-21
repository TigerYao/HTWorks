package com.huatu.handheld_huatu.business.matches.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.matches.fragment.ScReportArenaFragmentNew;
import com.huatu.handheld_huatu.business.matches.fragment.ScReportEssayFragment;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class ScReportActivity extends BaseActivity implements NightSwitchInterface, TextSizeSwitchInterface {

    @BindView(R.id.view_statue)
    View viewStatue;
    @BindView(R.id.ll_title)
    RelativeLayout llTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private int tag;                                        // 1、行测 2、申论
    private Bundle arg;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_sc_report;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 0) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        ArenaViewSettingManager.getInstance().registerTextSizeSwitcher(this);
    }

    @Override
    protected void onInitView() {

        QMUIStatusBarHelper.setStatusBarDarkMode(ScReportActivity.this);

        tag = originIntent.getIntExtra("tag", 1);
        arg = originIntent.getBundleExtra("arg");

        initViewPager();

    }

    private void initViewPager() {
        FragmentPagerItems pages = new FragmentPagerItems(this);
        if (tag == 1) {  // 行测模考报告
            pages.add(FragmentPagerItem.of("行测", ScReportArenaFragmentNew.class, arg));
        } else {         // 申论模考报告
            pages.add(FragmentPagerItem.of("申论", ScReportEssayFragment.class, arg));
        }
        FragmentPagerItemAdapter pagerAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onLoadData() {

    }

    @OnClick(R.id.iv_back)
    public void onClickBack() {
        finish();
    }

    @OnClick(R.id.iv_share)
    public void onClickShare() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            return;
        }
        ArenaExamMessageEvent event = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE);
        Bundle bundle = new Bundle();
        bundle.putInt("share_type", 3);
        bundle.putInt("typeSc", tag);               // 行测 or 申论
        event.extraBundle = bundle;
        EventBus.getDefault().post(event);          // EventBus是为了上报分享渠道

        if (tag == 1) {
            shareArena();
        } else {
            shareEssay();
        }
    }

    private void shareArena() {
        showProgress();
        ServiceProvider.getScArenaReportShare(compositeSubscription, arg.getLong("practice_id"), new NetResponse() {

            @Override
            public void onError(final Throwable e) {
                hideProgress();
                CommonUtils.showToast("获取分享数据失败");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                if (model.data != null) {
                    ShareInfoBean share = (ShareInfoBean) model.data;

                    String desc = share.desc;
                    String title = share.title;
                    String url = share.url;
                    String id = share.id;
                    ShareUtil.test(ScReportActivity.this, id, desc, title, url);
                }
            }
        });
    }

    private void shareEssay() {
        showProgress();
        ServiceProvider.getScReportUrl(compositeSubscription, arg.getLong("essay_paperId"), 2, new NetResponse() {

            @Override
            public void onError(final Throwable e) {
                hideProgress();
                CommonUtils.showToast("获取分享数据失败");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                if (model.data != null) {
                    ShareInfoBean share = (ShareInfoBean) model.data;

                    String desc = share.desc;
                    String title = share.title;
                    String url = share.url;
                    String id = share.id;
                    ShareUtil.test(ScReportActivity.this, id, desc, title, url);
                }
            }
        });
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    public void nightSwitch() {
        recreate();
    }

    @Override
    public void sizeSwitch() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
        ArenaViewSettingManager.getInstance().unRegisterTextSizeSwitcher(this);
    }
}
