package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cjx on 2018\7\19 0019.
 */

public abstract class BaseDialogActivity extends Activity {

    private Unbinder mUnbinder;
    protected abstract int getContentViewId() ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        //ActivityStack.getInstance().add(this);
        mUnbinder= ButterKnife.bind(this);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onCreate");
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+"  onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v(this.getClass().getName() + " onStop()");
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onStop");
    }

    @Override
    protected void onDestroy() {
        if(null!=mUnbinder) mUnbinder.unbind();
        super.onDestroy();
        LogUtils.v(this.getClass().getName() + " onDestroy()");
        //ActivityStack.getInstance().remove(this);
        NBSAppAgent.leaveBreadcrumb("ztk "+this.getClass().getName()+" onDestroy");
    }
}
