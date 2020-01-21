package com.huatu.handheld_huatu.business.login;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.event.Event;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.RxBus;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;

import java.io.Serializable;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by ljzyuhenda on 16/7/15.
 */
public class BaseActivityForLoginWRegister extends BaseActivity {

    protected RxBus mRxBus;
    private Subscription subscriptionClose;
    private CustomLoadingDialog mLoadingDialog;

    protected boolean mIsQuickLogin;//游客快速登录

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsQuickLogin=getIntent().getBooleanExtra(ArgConstant.QUICK_LOGIN,false);
        mRxBus = new RxBus();

        subscriptionClose = mRxBus.toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof Event.CloseLoginWRegisterEvent) {
                    finish();
                }
            }
        });
    }

    @Override
    protected int onSetRootViewId() {
        return 0;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscriptionClose.unsubscribe();
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

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new CustomLoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }
}
