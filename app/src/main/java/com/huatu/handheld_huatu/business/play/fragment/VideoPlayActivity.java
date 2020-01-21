package com.huatu.handheld_huatu.business.play.fragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseActivity;

import java.io.Serializable;

/**
 * Created by saiyuan on 2018/7/10.
 */

public class VideoPlayActivity extends BaseActivity {
    @Override
    protected int onSetRootViewId() {
        return 0;
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
