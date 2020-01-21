package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.networkbench.agent.impl.NBSAppAgent;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.umeng.analytics.MobclickAgent;

import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

//import me.yokeyword.fragmentation.SupportFragment;

/**
 * 展示自定制的MySupportActivity，不继承SupportActivity
 * Created by YoKey on 17/6/24.
 */
public class MyReuseSupportActivity extends AppCompatActivity implements ISupportActivity {
    final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this,false);
    protected String TAG = "MyReuseSupportActivity";
    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    private FragmentParameter mFragmentParameter;

    private int mTranslucentFlags=0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // XLog.e("curPar", "curPar");
        outState.putParcelable("curPar", mFragmentParameter);
        outState.getInt("translucentFlag",mTranslucentFlags);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 获取页面切换的参数封装对象
        mFragmentParameter = savedInstanceState == null ? FragmentParameter.deserialization(getIntent())
                : (FragmentParameter) savedInstanceState.getParcelable("curPar");

        mTranslucentFlags=savedInstanceState==null?getIntent().getIntExtra(ArgConstant.TYPE,0)
                                         :savedInstanceState.getInt("translucentFlag");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //修复某些机器上surfaceView导致的闪黑屏的bug
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        if(!CommonUtils.isPad(UniApplicationContext.getContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
        if(null==mFragmentParameter){
            this.finish();
        }
        if ((mTranslucentFlags & 0X00000001)>0) {
            QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
         }
         if((mTranslucentFlags & 0X00000002)>0){
             QMUIStatusBarHelper.setStatusBarLightMode(this);
         }
        TAG=this.getClass().getName();
        mDelegate.onCreate(savedInstanceState);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onCreate");

        setContentView(R.layout.comm_root_layout);
       // MySupportFragment fragment = findFragment(ChoosePayWithdrawalFragment.class);

        FragmentManager fm = this.getSupportFragmentManager();
        MySupportFragment fragment = (MySupportFragment) fm.findFragmentByTag(mFragmentParameter.getTag());
        if (fragment == null) {
            fragment = (MySupportFragment) Fragment.instantiate(this, mFragmentParameter.getFragmentClassName(), mFragmentParameter.getParams());
            loadRootFragment(R.id.fl_container, fragment);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDelegate.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //StatisticsComponent.onResume(BaseFragmentActivity.this.getClass().getSimpleName());
        MobclickAgent.onPageStart(TAG);
        //MobclickAgent.onResume(this);
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+"  onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // StatisticsComponent.onPause(this);
        MobclickAgent.onPageEnd(TAG);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v(this.getClass().getName() + " onStop()");
        NBSAppAgent.leaveBreadcrumb("login "+this.getClass().getName()+" onStop");
    }


    @Override
    protected void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
        NBSAppAgent.leaveBreadcrumb("ztk "+this.getClass().getName()+" onDestroy");
    }

    /**
     * Note： return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    /**
     * 不建议复写该方法,请使用 {@link #onBackPressedSupport} 代替
     */
    @Override
    final public void onBackPressed() {
        mDelegate.onBackPressed();
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     * <p/>
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面的事务全部执行后 执行该Action
     */
    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    /****************************************以下为可选方法(Optional methods)******************************************************/

    // 选择性拓展其他方法

    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    public void start(ISupportFragment toFragment) {
        mDelegate.start(toFragment);
    }

    /**
     * @param launchMode Same as Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {@link MySupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
     *
     * @see #popTo(Class, boolean)
     * +
     * @see #start(ISupportFragment)
     */
    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    /**
     * Pop the fragment.
     */
 /*   public void pop() {
        mDelegate.pop();
    }*/

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 得到位于栈顶Fragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }
}
