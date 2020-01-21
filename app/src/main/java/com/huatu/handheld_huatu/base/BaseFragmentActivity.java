/**
 * <pre>
 * Copyright 2014-2019 Soulwolf AppStructure
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.handheld_huatu.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.networkbench.agent.impl.NBSAppAgent;

import com.umeng.analytics.MobclickAgent;


/*import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;*/


/**
 * 全局Activity基类
 * <p/>
 * author : Soulwolf Create by 2015/6/8 10:49 implements FragmentJumpHandler
 * email  : ToakerQin@gmail.com.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected String TAG = "BaseFragmentActivity";
   // SystemBarTintManager mTintManager;

    //private SwipeBackActivityHelper mHelper;
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    protected int getStatusBarColor() {
        return android.R.color.transparent;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG=this.getClass().getName();
        ActivityStack.getInstance().add(this);
      //  MyAppManager.getAppManager().addActivity(this);
   /*     mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            StatusBarUtil.setStatusLight(this);
        }
        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(getStatusBarColor());  //设置上方状态栏透明*/

        //MyActivityManager.getInstance().pushOneActivity(this);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onCreate");
    }

  /*  public void setStatuBarBg(int res) {
        if (null != mTintManager) mTintManager.setStatusBarTintResource(res);
    }*/


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

/*    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        //StatisticsComponent.onResume(BaseFragmentActivity.this.getClass().getSimpleName());
       // MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+"  onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // StatisticsComponent.onPause(this);
       // MobclickAgent.onPageEnd(TAG);
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
        ActivityStack.getInstance().remove(this);
        //MyActivityManager.getInstance().popOneActivity(this);
        super.onDestroy();
        NBSAppAgent.leaveBreadcrumb("ztk "+this.getClass().getName()+" onDestroy");
        //MyAppManager.getAppManager().popActivity();
    }
}
