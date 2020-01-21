package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.ADelayStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.OnSwitchListener;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

//import com.huatu.handheld_huatu.business.lessons.MySingleTypeCourseFragment;

/**
 * Created by cjx on 2018\6\29 0029.
 */

public class CousreCollectFragment extends ADelayStripTabsFragment<AStripTabsFragment.StripTabItem> {

    private boolean mHasDeleted = false;

    @Override
    public int getContentView() {
        return R.layout.down_finished_ui_twotabs;
    }

    @Override
    protected int delayGenerateTabs() {
        return 100;
    }


    private boolean mHasTypeInit = false;
    private CompositeSubscription mCompositeSubscription;

    public void setHasDelete() {
        mHasDeleted = true;
    }


    /*   public static void lanuch(Context mContext, int pos) {
     *//*    Bundle args = new Bundle();
        args.putInt(SET_INDEX, pos);
        //XLog.e("lanuch", "lanuch" + pos);
        FragmentParameter tmpPar = new FragmentParameter(MyAllCourseFragment.class, args);
        UIJumpHelper.jumpFragment(mContext, tmpPar);*//*
        UIJumpHelper.jumpFragment(mContext, MySingleTypeCourseFragment.class);
    }*/


    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        //  EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);

        getTitleBar().setShadowVisibility(View.GONE);
        setTitle("我的收藏");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewPager != null) viewPager.setScrollable(false);
    }

    MenuItem mRightMenu;

    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
        titleBar.add("编辑", android.R.id.button1);
        mRightMenu = titleBar.findMenuItem(android.R.id.button1);
    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if (menuItem.getId() == android.R.id.button1) {
            if (getCurrentFragment() instanceof OnSwitchListener) {

                int isOldEdit = ((OnSwitchListener) getCurrentFragment()).isEditMode();
                LogUtils.e("onMenuClicked",isOldEdit+"");
                if (isOldEdit == 2) return;

                menuItem.setText((isOldEdit == 0) ? R.string.pickerview_cancel : R.string.edit);
                ((OnSwitchListener) getCurrentFragment()).switchMode();

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        //  EventBus.getDefault().unregister(this);
    }

    @Override
    protected ArrayList<StripTabItem> generateTabs() {
        ArrayList<StripTabItem> items = new ArrayList<StripTabItem>();
        items.add(new StripTabItem(StringUtils.valueOf(0), "课程"));
        items.add(new StripTabItem(StringUtils.valueOf(1), "课件"));
        items.add(new StripTabItem(StringUtils.valueOf(2), "乐见"));
        return items;
    }

    @Override
    protected Fragment newFragment(StripTabItem bean) {
        if (bean != null) {
            if (bean.getType().equals("0"))
                return new CourseCollectListFragment();
            else  if (bean.getType().equals("1"))
                return new CourseWareCollectListFragment();
            else
                return new ArticleCollectListFragment();
        }
        return null;
    }


    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (getCurrentFragment() instanceof OnSwitchListener) {
            int isOldEdit = ((OnSwitchListener) getCurrentFragment()).isEditMode();
            mRightMenu.setText((isOldEdit == 1) ? R.string.pickerview_cancel : R.string.edit);
        }
    }

}
