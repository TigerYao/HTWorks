package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.essay.bhelper.ScreenshotDetector;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
//import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xing on 2018/4/19.
 */

public abstract class SimpleBaseActivity extends  AppCompatActivity{

        protected String TAG = getClass().getSimpleName();//"SupportBaseActivity";
        protected LayoutInflater mLayoutInflater;
        protected View rootView;
        protected CustomLoadingDialog progressDlg;

        private boolean isSupportFragment = true;
        protected Intent originIntent = null;
        private boolean isSupportProgress = true;
        private CompositeSubscription mCompositeSubscription = null;
        protected  boolean isInitSucc = true;
        protected boolean isDestroyed = false;
        protected boolean isRestoreState;

        private Unbinder mUnbinder;

        protected CompositeSubscription getSubscription(){
            mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
            return mCompositeSubscription;
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(!CommonUtils.isPad(UniApplicationContext.getContext())){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            }
            TAG = this.getClass().getName();
            LogUtils.v(this.getClass().getName() + " onCreate()");
            originIntent = getIntent();
            if(savedInstanceState != null) {
                isRestoreState = true;
                restoreSavedBundle(savedInstanceState);
                LogUtils.v(this.getClass().getName() + " onCreate()" + ", savedInstanceState: " + savedInstanceState.toString());
            }
            int rootViewId = onSetRootViewId();
            if(rootViewId <= 0) {
                ToastUtils.showShort(UniApplicationContext.getContext(), "Please set content view");
                this.finish();
                return;
            }
           // isSupportFragment = setSupportFragment();
            mLayoutInflater = LayoutInflater.from(this);
            rootView = mLayoutInflater.inflate(rootViewId, null, false);
            setContentView(rootView);
            ActivityStack.getInstance().add(this);
            mUnbinder= ButterKnife.bind(this);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            if(isInitSucc) {
                onInitView();
            }
      /*      if (!(this instanceof FeedbackActivity)){
                mScreenshotDetector=new ScreenshotDetector(this);
                mScreenshotDetector.start(this, new ScreenshotDetector.Call() {
                    @Override
                    public void callPath(String path) {

                    }
                });
            }*/
            if(isInitSucc) {
                onRegisterReceiver();
            }
            if(!isInitSucc) {
                finish();
            }
            NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onCreate");
        }

        @Override
        protected void onPostCreate(@Nullable Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            LogUtils.v(this.getClass().getName() + " onPostCreate()");
            if(!isRestoreState) {
                onLoadData();
            }
        }

        abstract protected int onSetRootViewId();
        protected void onInitView(){};
        protected void onRegisterReceiver(){};
        protected void onLoadData(){};


        protected void restoreSavedBundle(Bundle savedInstanceState) {

        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            LogUtils.v(this.getClass().getName() + " onNewIntent()");
            originIntent = intent;
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            LogUtils.v( this.getClass().getName() + " onSaveInstanceState()");
            if(outState != null) {
                LogUtils.v(this.getClass().getName() + " onSaveInstanceState(): " + outState.toString());
            }
        }

        @Override
        protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            LogUtils.v(this.getClass().getName() + " onRestoreInstanceState()");
            if(savedInstanceState != null) {
                LogUtils.v(this.getClass().getName() + " onRestoreInstanceState: " + savedInstanceState.toString());
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            LogUtils.v(this.getClass().getName() + " onStart()");
            UniApplicationContext.isAppInBackground = false;
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            LogUtils.v(this.getClass().getName() + " onRestart()");
        }

        @Override
        protected void onResume() {
            super.onResume();
            MobclickAgent.onPageStart(TAG);
            MobclickAgent.onResume(this);
            NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+"  onResume");
        }

        @Override
        protected void onPause() {
            super.onPause();
            MobclickAgent.onPageEnd(TAG);
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
            isDestroyed = true;
            if(null!=mUnbinder) mUnbinder.unbind();
            super.onDestroy();
            LogUtils.v(this.getClass().getName() + " onDestroy()");
            RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
            if(EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
            ActivityStack.getInstance().remove(this);
            if(progressDlg != null && progressDlg.isShowing()) {
                progressDlg.dismiss();
            }

            NBSAppAgent.leaveBreadcrumb("ztk "+this.getClass().getName()+" onDestroy");
        }

        @Override
        public void finish() {
            super.finish();
            isDestroyed = true;
        }

        public boolean isDestroyed() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return super.isDestroyed();
            }
            return isDestroyed;
        }

        @Override
        public void onAttachFragment(Fragment fragment) {
            super.onAttachFragment(fragment);
            LogUtils.i(fragment.getClass().getSimpleName() + " is attached to Activity");
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(isSupportFragment) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if(fragments != null && fragments.size() > 0) {
                    for(int i = 0; i < fragments.size(); i++) {
                        if(fragments.get(i) != null) {
                            fragments.get(i).onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            }
        }

        @Override
        public void onBackPressed() {
            boolean isConsumed = false;
            if(getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for(Fragment fragment : fragmentList) {
                    if(fragment != null && fragment instanceof BaseFragment && fragment.isAdded() && !fragment.isHidden()) {
                        if(((BaseFragment)fragment).onBackPressed()) {
                            isConsumed = true;
                        }
                    }
                }
            }
            if(!isConsumed) {
                super.onBackPressed();
            }
        }


        public void showProgress() {
            if(!isSupportProgress) {
                return;
            }
            if(!Method.isActivityFinished(this)) {
                if(progressDlg == null) {
                    progressDlg = new CustomLoadingDialog(this);
                }
                progressDlg.show();
            }
        }

        public void hideProgess() {
            if(!isSupportProgress || progressDlg == null) {
                return;
            }
            if(!Method.isActivityFinished(this)) {
                progressDlg.dismiss();
            }
        }

        public boolean isProgressShown() {
            if(isSupportProgress && progressDlg != null) {
                return (progressDlg.isShowing());
            }
            return false;
        }


}
