package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

/**
 * Created by ht on 2017/10/16.
 */

public class LevelConditionFragment extends BaseWebViewFragment {
    @Override
    protected void onInitView() {
        super.onInitView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void onRightClickBtn() {
        super.onRightClickBtn();
        if (!NetUtil.isConnected()){
            ToastUtils.showShort("网络错误，请检查网络");
            return;
        }
        BaseFrgContainerActivity.newInstance(mActivity,
                LevelPrivilegeExplainFragment.class.getName(),
                LevelPrivilegeExplainFragment.getArgs());
    }

    @Override
    protected boolean dealOverrideUrl(String dealUrl) {
        if (!NetUtil.isConnected()){
            ToastUtils.showShort("网络错误，请检查网络");
            return false;
        }
        if (TextUtils.isEmpty(dealUrl)) {
            return false;
        }
        boolean isDeal = super.dealOverrideUrl(dealUrl);
        if (!isDeal && dealUrl.contains(RetrofitManager.getInstance().getBaseUrl() + "pc/reward/task.html")) {
            BaseFrgContainerActivity.newInstance(mActivity,
                    EverydayTaskFragment.class.getName(),
                    EverydayTaskFragment.getArgs());
            return true;
        } else if (!isDeal && dealUrl.contains(RetrofitManager.getInstance().getBaseUrl() + "pc/reward/detail.html")) {
            BaseFrgContainerActivity.newInstance(mActivity,
                    LevelInitialExplainFragment.class.getName(),
                    LevelInitialExplainFragment.getArgs());
            return true;

        }
        return false;
    }


    public static Bundle getArgs() {
        String webUrl = RetrofitManager.getInstance().getBaseUrl() + "pc/reward/index.html?token=" + UserInfoUtil.token;
        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, "我的等级");
        bundle.putString(ARGS_STRING_RIGHT, "等级特权");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }
}
