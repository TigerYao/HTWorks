package com.huatu.handheld_huatu.business.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.ADelayStripTwoTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTwoTabsFragment;
import com.huatu.handheld_huatu.business.lessons.ShoppingFragmentV2;

import com.huatu.handheld_huatu.ui.ViewPagerWrapper;
import com.huatu.utils.StringUtils;
import com.huatu.widget.CirclePointImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018\6\13 0013.
 */

public class HomeTabsFragment extends ADelayStripTwoTabsFragment<AStripTwoTabsFragment.StripTabItem> implements View.OnClickListener {


    View mTopBar;
    ViewPagerWrapper mPageView;

    CirclePointImageView mMsgPointView;
// AppBarLayout mAppBarLayout;
    @Override
    protected String configLastPositionKey() {
        return "HomeTopicProdsFragment";
    }

/*    @Override
    public int getContentView() {
        return R.layout.home_product_ui_style_tabs2;
    }*/

    @Override
    public int getContentView() {
        return R.layout.home_subtab_ui_layout;
    }

    @Override
    protected int delayGenerateTabs() {
        return 100;
    }

    @Override
    public void onDestroy() {
      //  EventBus.getDefault().unregister(this);
        super.onDestroy();

        // XLog.d("onVideo", "onVideoDestroy");

    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
//        EventBus.getDefault().register(this);
        setCurrentPosition(1);
    }


  /*  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NewMessageEvent e) {
        XLog.e("onEventMainThread", "onEventMainThread,");
        if (null != mMsgPointView) {
            mMsgPointView.setDotsVisibility(e.needShow);
            mMsgPointView.invalidate();
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMsgPointView = findViewById(R.id.home_message_img);
        mMsgPointView.setOnClickListener(this);

    /*    int oldUnReadCount = UserManager.getInstance().getunReadMsgCount();
        if (oldUnReadCount > 0) mMsgPointView.setShowDots(true);
        mTopBar = findViewById(R.id.top_bar_layout);
        //mTopBar.setAlpha(0.8f);
        mPageView = this.findViewById(R.id.xi_activity_type_view_pager);
        mPageView.setScrollable(true);*/

        findViewById(R.id.icon_left).setOnClickListener(this);
        // mAppBarLayout =(AppBarLayout)this.findViewById(R.id.main_appbar);
        //mAppBarLayout.addOnOffsetChangedListener(this);
    }

 /*   @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        XLog.e("onOffsetChanged", verticalOffset + "");
        *//*if(getCurrentFragment()!=null&&getCurrentFragment() instanceof OnSetCanPullListener) {
           ((OnSetCanPullListener)getCurrentFragment()).setCanPull(verticalOffset==0);
        }*//*
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.icon_right:
                CommonUtils.startActivity(getContext(), MipcaActivity.class);
                // if (!UIHelper.checkLogin(getActivity())) return;
                // CommonUtils.jumpFragment(getContext(), ShopCartFragment.class);
                break;
            case R.id.home_message_img:
                if (!UIHelper.checkLogin(getActivity())) return;
                CommonUtils.startActivity(getActivity(), ImConversationActivity.class);
                break;*/
        }
    }


    @Override
    protected ArrayList<StripTabItem> generateTabs() {
        ArrayList<StripTabItem> items = new ArrayList<StripTabItem>();
        items.add(new StripTabItem(StringUtils.valueOf(1), "推荐"));
        items.add(new StripTabItem(StringUtils.valueOf(2), "直播课"));
        items.add(new StripTabItem(StringUtils.valueOf(3), "录播课"));

        return items;
    }

    @Override
    protected Fragment newFragment(StripTabItem bean) {

        if (bean != null) {
            if (bean.getType().equals("1")) {
//                return new DesignerVideosFragment();
                return ShoppingFragmentV2.newInstance();
            } else if (bean.getType().equals("2"))
                return ShoppingFragmentV2.newInstance();

            else if (bean.getType().equals("3"))
                return ShoppingFragmentV2.newInstance();


        }
        return null;
    }

  /*  public void checkShowTopBar(boolean needShow) {
        if (null != mTopBar) mTopBar.setVisibility(needShow ? View.VISIBLE : View.GONE);

        mPageView.setScrollable(needShow);
        if (!getActivity().isFinishing() && (getActivity() instanceof MainTabActivity)) {
            ((MainTabActivity) getActivity()).showBottomBar(needShow);
        }
    }*/

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser){
           Fragment curFrag= getCurrentFragment();
            if(null!=curFrag&&(curFrag instanceof DesignerVideosFragment)){
              ((DesignerVideosFragment)curFrag).doVideoPause();
            }
        }
       // XLog.d("onVideo", "f_onUserVisible" + isVisibleToUser);
    }*/


}