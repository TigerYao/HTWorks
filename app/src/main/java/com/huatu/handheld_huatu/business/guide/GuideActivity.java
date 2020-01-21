package com.huatu.handheld_huatu.business.guide;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.GuidePageAdapter;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.QbInitCallback;
import com.huatu.handheld_huatu.listener.OnRecyclerViewItemClickListener;
import com.huatu.handheld_huatu.utils.DepthPageTransformer;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
//import com.huatu.handheld_huatu.utils.ScreenUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CircleIndicator;
import com.tencent.smtt.sdk.QbSdk;

import java.io.Serializable;

/**
 * @author zhaodongdong
 */
public class GuideActivity extends BaseActivity implements OnRecyclerViewItemClickListener {

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void onInitView() {
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_guide);

        final CircleIndicator mIndicator = (CircleIndicator) rootView.findViewById(R.id.indicator_guide);
        mIndicator.setVisibility(View.GONE);
        viewPager.setAdapter(new GuidePageAdapter(this, this));
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        mIndicator.setViewPager(viewPager);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

//      viewPager.setOnTouchListener(new View.OnTouchListener() {
//          @Override
//          public boolean onTouch(View v, MotionEvent event) {
//              return false;
//          }
//      });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            float mStartX;
            float mEndX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = event.getX();
                        Log.i("onTouch", "startx: " + mStartX);
                        break;
                    case MotionEvent.ACTION_UP:
                        mEndX = event.getX();
                        int with = DisplayUtil.getScreenWidth();//(GuideActivity.this);
                        if (mIndicator.getCurrentViewPositon() ==4 && mStartX - mEndX > (with / 5)) {
                            turnToLoginOrMainTab();
                        }
                        break;

                    default:

                }
                return false;
            }
        });
        QbSdk.initX5Environment(UniApplicationContext.getContext(), new QbInitCallback());
    }


    private void turnToLoginOrMainTab() {
        PrefStore.setGuideFirstRun();
        if (SpUtils.getLoginState()) {
            MainTabActivity.newIntent(GuideActivity.this);
        } else {
            AppContextProvider.addFlags(AppContextProvider.GUESTSPLASH);
            LoginByPasswordActivity.newIntent(GuideActivity.this);
        }
        finish();
    }


    @Override
    public void onItemClick(int type, int position) {
        turnToLoginOrMainTab();
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
}
