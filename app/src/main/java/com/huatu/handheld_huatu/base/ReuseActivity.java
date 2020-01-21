package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.CommonUtils;



/*import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;*/

/**
 * 重用Activity  implements SwipeBackActivityBase
 */
public class ReuseActivity extends BaseFragmentActivity {

    static final boolean DEBUG = false;

    static final String LOG_TAG = "ReuseActivity:";

    TitleBar mTitleBar;

    private ReuseActivityHelper helper;

    private AbsFragment mCurrentFragment;

    private FragmentParameter mFragmentParameter;

    private LayoutInflater mInflater;

    private ViewGroup mTitleGroup;

    private boolean isDestory;


    protected boolean enableBackAnim() { return true;}


    //private SwipeBackActivityHelper mHelper;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // XLog.e("curPar", "curPar");
        outState.putParcelable("curPar", mFragmentParameter);
    }

    protected int getContentViewId(){
        return R.layout.activity_reuse_layout;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 获取页面切换的参数封装对象
        mFragmentParameter = savedInstanceState == null ? FragmentParameter.deserialization(getIntent())
                : (FragmentParameter) savedInstanceState.getParcelable("curPar");

       // if (savedInstanceState != null) XLog.e("ReuseActivity_savedInstanceState", "curPar");
        if (mFragmentParameter != null) {
            helper = new ReuseActivityHelper(this);
        }
        super.onCreate(savedInstanceState);
        if(!CommonUtils.isPad(UniApplicationContext.getContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
     /*    mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();*/


        //setContentView(R.layout.activity_reuse_layout);
        setContentView(getContentViewId());
        mInflater = LayoutInflater.from(this);
        mTitleGroup = (ViewGroup) findViewById(R.id.xi_toolbar_group);
        mTitleBar = (TitleBar) findViewById(R.id.xi_toolbar);
        // 绑定Fragment
        if (helper != null && mFragmentParameter != null) {
           if(enableBackAnim())   overridePendingTransition(mFragmentParameter.mAnimationDefaultRes[0], mFragmentParameter.mAnimationDefaultRes[1]);
            mCurrentFragment = helper.ensureFragment(mFragmentParameter);
            // 初始化 TitleBar
            mTitleBar.setOnTitleBarMenuClickListener(mCurrentFragment);
            if (mCurrentFragment.attachTitleBar(mInflater, mTitleGroup)) {
                mTitleBar.setTitleVisibility(View.GONE);
            } else {
                mTitleBar.setTitleVisibility(View.VISIBLE);
            }
        }
    }



    @Override
    public void setTitle(CharSequence title) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(titleId);
        }
    }

    public void setTitle(CharSequence text, int imageId) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(text, imageId);
        }
    }

    public void setTitleTextColor(int color) {
        if (mTitleBar != null) {
            mTitleBar.setTitleTextColor(color);
        }
    }

    /**
     * 设置标题的背景颜色
     * create on Baron
     */
    public void setTitleBackground(int color) {
        if (mTitleBar != null) {
            mTitleBar.setBackgroundColor(color);
        }
    }

    /**
     * 设置分割线状态
     *
     * @param
     */
    public void setShadowVisibility(int visibility) {
        if (mTitleBar != null) {
            mTitleBar.setShadowVisibility(visibility);
        }
    }

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void finish() {
        setMDestory(true);
        if (mFragmentParameter != null && mFragmentParameter.mResultCode != FragmentParameter.NO_RESULT_CODE) {
            setResult(mFragmentParameter.getResultCode(), mFragmentParameter.getResultParams());
        }
        super.finish();
        if(enableBackAnim()){
            if (mFragmentParameter != null) {
                overridePendingTransition(mFragmentParameter.mAnimationDefaultRes[2], mFragmentParameter.mAnimationDefaultRes[3]);
            }
        }
        else
            overridePendingTransition(0, 0);
       /* if (mFragmentParameter != null) {
            overridePendingTransition(mFragmentParameter.mAnimationDefaultRes[2], mFragmentParameter.mAnimationDefaultRes[3]);
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mCurrentFragment != null) {
            mCurrentFragment.onBackPressed();
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mCurrentFragment != null) {
            return mCurrentFragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    public FragmentParameter getFragmentParameter() {
        return mFragmentParameter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode < (1 << 16) && getCurrentFragment() != null)//activity之间的传值也传给fragment
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);

    }

    public boolean mIsDestoryed() {
        return isDestory;
    }

    public void setMDestory(boolean destory) {
        this.isDestory = destory;
    }

    @Override
    public void onDestroy() {
        isDestory = true;
        super.onDestroy();
    }



}
