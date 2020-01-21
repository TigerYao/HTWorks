package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseDetailFragment extends Fragment {
    protected XiaoNengHomeActivity mActivity;
    protected View rootView;
    protected LayoutInflater mLayoutInflater;
    protected String TAG = this.getClass().getSimpleName();
    protected Bundle args;
    public abstract int onSetRootViewId();
    public int requestType;
    protected boolean isViewCreated = false;
    protected boolean isRestore = false;
    public static int REQUEST_FRAGMENT_RESULT = 4772;
    protected CompositeSubscription compositeSubscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onCreate()");
        if(savedInstanceState != null) {
            isRestore = true;
            restoreSavedBundle(savedInstanceState);
            LogUtils.v( " onCreate(), savedInstanceState: " + savedInstanceState.toString());
        }
        this.mActivity = (XiaoNengHomeActivity) getActivity();
        args = getArguments();
        if(args == null) {
            args = new Bundle();
        }
        LogUtils.v(this.getClass().getName() + " getArguments: " + args.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.v(this.getClass().getName() + " onCreateView()");
        mLayoutInflater = inflater;
        rootView = mLayoutInflater.inflate(onSetRootViewId(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);// Restore State Here
        LogUtils.v(this.getClass().getName() + " onActivityCreated()");
        if (!restoreStateFromArguments()) {
            // First Time, Initialize something here
            onLoadData();
        }
    }

    protected Serializable getDataFromActivity(String tag) {
        if(mActivity != null && !mActivity.isFinishing()) {
            return mActivity.getDataFromActivity(tag);
        }
        return null;
    }

    public boolean onBackPressed() {
        boolean isConsumed = false;
        if(getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            List<Fragment> fragmentList = getChildFragmentManager().getFragments();
            for(Fragment fragment : fragmentList) {
                if(fragment != null && fragment instanceof BaseFragment && fragment.isAdded() && !fragment.isHidden()) {
                    if(((BaseFragment)fragment).onBackPressed()) {
                        isConsumed = true;
                    }
                }
            }
        }
        if(!isConsumed && this.getTargetFragment() != null) {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
            isConsumed = true;
        }
        return isConsumed;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if(fragments != null && fragments.size() > 0) {
            for(int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i) != null) {
                    fragments.get(i).onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onViewCreated()");
        onInitView();
        onInitListener();
        isViewCreated = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        LogUtils.v(this.getClass().getName() + " onSaveInstanceState()");
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            LogUtils.v(this.getClass().getName() + " onViewStateRestored: " + savedInstanceState.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.v(this.getClass().getName() + " onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.v(this.getClass().getName() + " onResume()");
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.v(this.getClass().getName() + " onPause()");
        onVisibilityChangedToUser(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onVisibilityChangedToUser(getUserVisibleHint());
        }
    }

    public void onVisibilityChangedToUser(boolean isVisibleToUser){
        if(isVisibleToUser){
            if(TAG != null){
                MobclickAgent.onPageStart(TAG);
                LogUtils.i("UmengPageTrack", TAG + " - display - ");
            }
        }else{
            if(TAG != null){
                MobclickAgent.onPageEnd(TAG);
                LogUtils.w("UmengPageTrack", TAG + " - hidden - ");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.v(this.getClass().getName() + " onDestroyView()");
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        LogUtils.v(this.getClass().getName() + " onDestroy()");
    }

    private void saveStateToArguments() {
        Bundle savedState = new Bundle();
        if (getView() != null) {
            onSaveState(savedState);
        }
        if (savedState != null) {
            if (args != null) {
                args.putBundle("internalSavedViewState" + this.getClass().getSimpleName(), savedState);
            }
            LogUtils.v(this.getClass().getName() + " saveStateToArguments: " + savedState.toString());
        }
    }

    private boolean restoreStateFromArguments() {
        Bundle savedState = args.getBundle("internalSavedViewState" + this.getClass().getSimpleName());
        if (savedState != null) {
            onRestoreState(savedState);
            LogUtils.v(this.getClass().getName() + " restoreStateFromArguments: " + savedState.toString());
            return true;
        }
        return false;
    }

    protected void onSaveState(Bundle savedInstanceState) {
    }

    protected void onRestoreState(Bundle savedInstanceState) {
    }
    protected void restoreSavedBundle(Bundle savedInstanceState) {

    }

    protected void onInitView() {
    }

    protected void onInitListener() {
    }

    protected void onLoadData() {
    }

    public void startFragmentForResult(BaseDetailFragment toFragment) {
        startFragmentForResult(toFragment, REQUEST_FRAGMENT_RESULT);
    }

    public void startFragmentForResult(BaseDetailFragment toFragment, int requestCode) {
        if(toFragment != null) {
            toFragment.setTargetFragment(this, requestCode);
            mActivity.addFragment(this.getClass().getSimpleName(), toFragment,
                    mActivity.getFragmentContainerId(0), true, true);
        }
    }

    public void setResultForTargetFrg(int resultCode) {
        setResultForTargetFrg(resultCode, null);
    }

    public void setResultForTargetFrg(int resultCode, Intent intent) {
        if(this.getTargetFragment() != null) {
            this.getTargetFragment().onActivityResult(
                    this.getTargetRequestCode(),
                    resultCode, intent);
            this.mActivity.getSupportFragmentManager().popBackStackImmediate();
        } else {
            mActivity.setResult(resultCode, intent);
            mActivity.finish();
        }
    }
}
