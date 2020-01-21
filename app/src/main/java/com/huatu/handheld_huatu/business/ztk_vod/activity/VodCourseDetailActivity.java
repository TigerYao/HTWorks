package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseActivity;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/5.
 */

public class VodCourseDetailActivity extends  BaseActivity {
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
    /**
     * 实例化
     *
     * @param context
     * @param id
     */
    public static void newIntent(Context context, String id, boolean clearTop) {
        Intent intent = new Intent(context, VodCourseDetailActivity.class);
        intent.putExtra("rid", id);
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }
}
