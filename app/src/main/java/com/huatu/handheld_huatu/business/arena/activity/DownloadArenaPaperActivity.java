package com.huatu.handheld_huatu.business.arena.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.fragment.DownloadChildBaseFragment;
import com.huatu.handheld_huatu.business.arena.fragment.DownloadErrorFragment;
import com.huatu.handheld_huatu.business.arena.fragment.DownloadPaperFragment;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.NoScrollViewPager;
import com.netease.hearttouch.router.HTRouter;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 行测试卷下载页面 & 错题导出页面
 * 为了便于操作，ViewPager里放了个两个View，而不是Fragment。第一个是试卷下载，第二个是错题导出的试卷。
 */
@HTRouter(url = {"ztk://arena/download"}, needLogin = true)
public class DownloadArenaPaperActivity extends BaseActivity {

    @BindView(R.id.tv_right)
    TextView tvRight;                       // 编辑按钮

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;                    // tab
    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;            // 左边的是试卷下载，右边的是导出错题的下载

    @BindView(R.id.tv_store)
    TextView tvStore;                       // 当前手机剩余村粗数
    @BindView(R.id.rl_delete)
    RelativeLayout rlDelete;                // 全选、删除布局

    @BindView(R.id.tv_select_all)
    TextView tvSelectAll;                   // 全选按钮

    @BindView(R.id.rl_tip)
    public RelativeLayout rl_tip;           // tip

    private int curSubject;                 // 当前科目，是从上一个页面传过来的
    private int showIndex;                  // 应该显示第几个页面，0、试卷下载 1、错题导出下载

    private FragmentPagerItemAdapter pagerItemAdapter;

    public static boolean isEdit = false;       // 是否是编辑状态，用于在Adapter中判断显示状态

    private DecimalFormat df = new DecimalFormat("0.0");            // 取所有整数部分

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_down;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(DownloadArenaPaperActivity.this);

        curSubject = originIntent.getIntExtra("curSubject", -1);
        showIndex = originIntent.getIntExtra("showIndex", 0);

        // 获取手机剩余存储时间
        float totalAvailableSpace = ((float) FileUtil.getAvailableSpaceSize()) / 1024;      // 单位M
        String sizeDes = "";

        if (totalAvailableSpace > 1024) {
            totalAvailableSpace = totalAvailableSpace / 1024;
            sizeDes = df.format(totalAvailableSpace) + "G";
        } else {
            sizeDes = df.format(totalAvailableSpace) + "M";
        }

        tvStore.setText(sizeDes);

        initPagerView();
    }

    @Override
    public void onLoadData() {

    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.tv_select_all, R.id.tv_delete, R.id.rl_tip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                onClickEdit();
                break;
            case R.id.tv_select_all:
                selectAll();
                break;
            case R.id.tv_delete:
                deletePapers();
                break;
            case R.id.rl_tip:
                rl_tip.setVisibility(View.GONE);
                SpUtils.setSharePaperTipShow(false);
                break;
        }
    }

    private void initPagerView() {

        FragmentPagerItems pages = new FragmentPagerItems(this);

        Bundle arg = new Bundle();
        arg.putInt("curSubject", curSubject);
        pages.add(FragmentPagerItem.of("试卷下载", DownloadPaperFragment.class, arg));
        pages.add(FragmentPagerItem.of("错题下载", DownloadErrorFragment.class, arg));

        pagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setOffscreenPageLimit(100);
        viewPager.setScrollEnable(false);
        viewPager.setAdapter(pagerItemAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(showIndex);
    }

    private void onClickEdit() {
        DownloadChildBaseFragment currentFragment = getCurrentFragment();
        if (currentFragment == null) return;
        currentFragment.editItem(rlDelete.getVisibility() != View.VISIBLE);

        tvSelectAll.setText("全选");

        if (rlDelete.getVisibility() == View.VISIBLE) {         // 完成
            rlDelete.setVisibility(View.GONE);
            tvRight.setText("编辑");
            isEdit = false;
            setTabClickable(true);
        } else {                                                // 编辑
            rlDelete.setVisibility(View.VISIBLE);
            tvRight.setText("完成");
            isEdit = true;
            setTabClickable(false);
        }
    }

    private void setTabClickable(boolean clickable) {
        LinearLayout children = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < children.getChildCount(); i++) {
            View child = children.getChildAt(i);
            if (child != null) {
                child.setClickable(clickable);
            }
        }
    }

    private void selectAll() {
        boolean select;
        if (tvSelectAll.getText().toString().equals("全选")) {
            tvSelectAll.setText("取消全选");
            select = true;
        } else {
            tvSelectAll.setText("全选");
            select = false;
        }

        DownloadChildBaseFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.selectAll(select);
        }
    }

    private void deletePapers() {
        DownloadChildBaseFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.delete();
        }
    }

    private DownloadChildBaseFragment getCurrentFragment() {
        int currentItem = viewPager.getCurrentItem();
        Fragment page = pagerItemAdapter.getPage(currentItem);
        if (page == null) return null;
        return (DownloadChildBaseFragment) page;
    }

    public void resetTab() {
        setTabClickable(true);
        rlDelete.setVisibility(View.GONE);
        tvRight.setText("编辑");
        isEdit = false;
        hideProgress();
        ToastUtil.showToast("删除成功");
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
    protected void onDestroy() {
        super.onDestroy();
        isEdit = false;
    }
}
