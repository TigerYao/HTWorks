package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.networkbench.agent.impl.NBSAppAgent;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/13.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = getClass().getSimpleName();//"SupportBaseActivity";
    protected LayoutInflater mLayoutInflater;
    protected View rootView;
    protected CustomLoadingDialog progressDlg;

    private boolean isSupportFragment = true;
    protected Intent originIntent = null;
    private boolean isSupportProgress = true;
    protected CompositeSubscription compositeSubscription = null;
    protected boolean isInitSucc = true;
    protected boolean isDestroyed = false;
    protected boolean isRestoreState;


    public boolean canScreenshot() {
        return false;
    }

    public boolean canTransStatusbar() {
        return false;
    }



    protected boolean OverConfiguration(){
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(!CommonUtils.isPad(UniApplicationContext.getContext())){
            return;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonUtils.isPad(UniApplicationContext.getContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }

        if (canTransStatusbar()) {
            QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        }
        TAG = this.getClass().getName();
        LogUtils.v(this.getClass().getName() + " onCreate()");
        originIntent = getIntent();
        if (savedInstanceState != null) {
            isRestoreState = true;
            restoreSavedBundle(savedInstanceState);
            LogUtils.v(this.getClass().getName() + " onCreate()" + ", savedInstanceState: " + savedInstanceState.toString());
        }
        int rootViewId = onSetRootViewId();
        if (rootViewId <= 0) {
            ToastUtils.showShort(UniApplicationContext.getContext(), "Please set content view");
            this.finish();
            return;
        }
        isSupportFragment = setSupportFragment();
        mLayoutInflater = LayoutInflater.from(this);
        rootView = mLayoutInflater.inflate(rootViewId, null, false);
        setContentView(rootView);
        ActivityStack.getInstance().add(this);
        ButterKnife.bind(this);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (isInitSucc) {
            onInitView();
        }

        if (isInitSucc) {
            onRegisterReceiver();
        }
        if (!isInitSucc) {
            finish();
        }
        NBSAppAgent.leaveBreadcrumb("login " + this.getClass().getName() + " onCreate");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onPostCreate()");
        if (!isRestoreState) {
            onLoadData();
        }
    }

    abstract protected int onSetRootViewId();

    protected void onInitView() {
    }

    protected void onRegisterReceiver() {
    }

    protected void onLoadData() {
    }

    /*
    若不想支持Fragment,可以在OnCreate方法中传入false
     */
    abstract public boolean setSupportFragment();

    protected boolean isSupportFragment() {
        return isSupportFragment;
    }

    protected void setSupportProgress(boolean flag) {
        isSupportProgress = flag;
    }

    protected void restoreSavedBundle(Bundle savedInstanceState) {
        LogUtils.v(this.getClass().getName() + " restoreSavedBundle()");
        if (savedInstanceState != null) {
            LogUtils.v(this.getClass().getName() + " restoreSavedBundle(): " + savedInstanceState.toString());
        }
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
        LogUtils.v(this.getClass().getName() + " onSaveInstanceState()");
        if (outState != null) {
            LogUtils.v(this.getClass().getName() + " onSaveInstanceState(): " + outState.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onRestoreInstanceState()");
        if (savedInstanceState != null) {
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
       // MobclickAgent.onPageStart(TAG);      //统计页面
        MobclickAgent.onResume(this);//统计时长
        NBSAppAgent.leaveBreadcrumb("login " + this.getClass().getName() + "  onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  MobclickAgent.onPageEnd(TAG);       //保证 onPageEnd 在onPause 之前调用
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v(this.getClass().getName() + " onStop()");
        NBSAppAgent.leaveBreadcrumb("login " + this.getClass().getName() + " onStop");
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
        LogUtils.v(this.getClass().getName() + " onDestroy()");
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        ActivityStack.getInstance().remove(this);
        if (progressDlg != null && progressDlg.isShowing()) {
            progressDlg.dismiss();
        }
        NBSAppAgent.leaveBreadcrumb("ztk " + this.getClass().getName() + " onDestroy");
    }

    @Override
    public void finish() {
        super.finish();
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
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
        if (isSupportFragment) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (fragments.get(i) != null) {
                        fragments.get(i).onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isSupportFragment) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (fragments.get(i) != null) {
                        fragments.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isConsumed = false;
        if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment && fragment.isAdded() && !fragment.isHidden()) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        isConsumed = true;
                    }
                }
            }
        }
        if (!isConsumed) {
            super.onBackPressed();
        }
    }

    protected abstract int getFragmentContainerId(int clickId);

    public void replaceFragment(Fragment fragment, int clickId, boolean addToBackState) {
        int fragmentContainerId = getFragmentContainerId(clickId);
        if (!isSupportFragment || fragment == null || fragmentContainerId <= 0) {
            return;
        }
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment old = fragmentManager.findFragmentByTag(tag);
//        if(old != null ) {
//            if(!old.isAdded()) {
//                removeAllFragments(fragmentManager, fragmentTransaction);
//                fragmentTransaction.add(fragmentContainerId, old, tag);
//            } else if(old.isHidden()) {
//                fragmentTransaction.show(old);
//            }
//        } else {
        removeAllFragments(fragmentManager, fragmentTransaction);
        fragmentTransaction.add(fragmentContainerId, fragment, tag);
//        }
        if (addToBackState) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void removeAllFragments(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        if (fragmentManager == null || fragmentTransaction == null) {
            return;
        }
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null && fragments.size() > 0) {
            Iterator<Fragment> fragmentIterator = fragments.iterator();
            while (fragmentIterator.hasNext()) {
                Fragment fragment = fragmentIterator.next();
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
            }
        }
    }

    public void removeFragment(String prevFragmentTag, Fragment fragment, int clickId) {
        int fragmentContainerId = getFragmentContainerId(clickId);
        if (!isSupportFragment || fragmentContainerId <= 0) {
            return;
        }
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFragment = null;
        if (!TextUtils.isEmpty(prevFragmentTag)) {
            prevFragment = fragmentManager.findFragmentByTag(prevFragmentTag);
        }
        Fragment old = fragmentManager.findFragmentByTag(tag);
        if (old != null) {
            fragmentTransaction.remove(old);
        } else {
            fragmentTransaction.remove(fragment);
        }
        if (prevFragment != null) {
            if (!prevFragment.isAdded()) {
                fragmentTransaction.add(fragmentContainerId, prevFragment, prevFragmentTag);
            }
            if (prevFragment.isHidden()) {
                fragmentTransaction.show(prevFragment);
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addFragment(String curFragmentTag, Fragment fragment, int clickId, boolean addToBackState) {
        addFragment(curFragmentTag, fragment, clickId, addToBackState, true);
    }

    public void addFragment(String curFragmentTag, Fragment fragment, int clickId, boolean addToBackState, boolean isHidePrev) {
        addFragment(curFragmentTag, fragment, clickId, addToBackState, isHidePrev, true);
    }

    /**
     * @param curFragmentTag 当前显示的Fragment
     * @param fragment       现在要添加的Fragment
     * @param clickId        没用
     * @param addToBackState 是否加入回退栈
     * @param isHidePrev     是否隐藏前一个Fragment
     * @param isUserPrev     是否显示之前已经存在的事例
     */
    public void addFragment(String curFragmentTag, Fragment fragment, int clickId, boolean addToBackState, boolean isHidePrev, boolean isUserPrev) {
        int fragmentContainerId = getFragmentContainerId(clickId);
        if (!isSupportFragment || fragment == null || fragmentContainerId <= 0) {
            return;
        }
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //当前显示的Fragment
        Fragment prevFragment = null;
        if (!TextUtils.isEmpty(curFragmentTag)) {
            prevFragment = fragmentManager.findFragmentByTag(curFragmentTag);
        }

        //上一次显示的fragment
        Fragment old = fragmentManager.findFragmentByTag(tag);
        if (isUserPrev && old != null) {
            if (!old.isAdded()) {
                fragmentTransaction.add(fragmentContainerId, old, tag);
            } else if (old.isHidden()) {
                fragmentTransaction.show(old);
            }
        } else {
            fragmentTransaction.add(fragmentContainerId, fragment, tag);
        }
        if (isHidePrev && prevFragment != null) {
            fragmentTransaction.hide(prevFragment);
        }
        if (addToBackState) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    //弹出当前，跳指定的Fragment
    public void PopShowThatFragment(String curFragmentTag,Fragment fragment){
        addFragment(curFragmentTag, fragment, getFragmentContainerId(0), false, true, true);
    }

    public void AddFragment(String curFragmentTag,Fragment fragment){
        addFragment(curFragmentTag, fragment, getFragmentContainerId(0), false, false, true);
    }

    private boolean showProgress = false;
    public void showProgress(){
        showProgress(false);
    }
    public void showProgress(boolean canCancel) {
        if (!isSupportProgress) {
            return;
        }
        if (!Method.isActivityFinished(this)) {
            showProgress = true;
            if (progressDlg == null) {
                progressDlg = new CustomLoadingDialog(this);
                progressDlg.setCancelable(canCancel);
            }
            // 优化用户体验
            // 延迟100ms显示loading，以免在100ms之内的网络访问还显示loading
            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (showProgress && !Method.isActivityFinished(BaseActivity.this) && progressDlg != null) {
                                progressDlg.show();
                            }
                        }
                    });
                }
            }, 100);
        }
    }

    public void hideProgress() {
        if (!isSupportProgress || progressDlg == null) {
            return;
        }
        showProgress = false;
        if (!Method.isActivityFinished(this)) {
            progressDlg.dismiss();
        }
    }

    public boolean isProgressShown() {
        if (isSupportProgress && progressDlg != null) {
            return (progressDlg.isShowing());
        }
        return false;
    }

    public abstract Serializable getDataFromActivity(String tag);

    public abstract void updateDataFromFragment(String tag, Serializable data);

    public abstract void onFragmentClickEvent(int clickId, Bundle bundle);
}
