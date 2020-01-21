package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.event.me.MeMsgMessageEvent;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

public class CourseCardFragment extends BaseWebViewFragment {
    @Override
    protected void onInitView() {
        super.onInitView();
       // compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }
    @Override
    protected boolean dealOverrideUrl(String dealUrl) {
        LogUtils.i("CourseCardFragment-dealUrl: " + dealUrl);
        if (!NetUtil.isConnected()){
            ToastUtils.showShort("网络错误，请检查网络");
            return false;
        }
        if (TextUtils.isEmpty(dealUrl)) {
            return false;
        }
        boolean isDeal = super.dealOverrideUrl(dealUrl);
       // LogUtils.i("CourseCardFragment-dealUrl: " + dealUrl);
        if (!isDeal && dealUrl.contains("ztk://course/study/home")) {
            //跳学习列表测试环境
            MainTabActivity.newIntent(getActivity(),2);
            return true;
        }
        return false;
    }
    public static Bundle getArgs() {
        String userName= SpUtils.getUname();
        String webUrl = null;
        if (userName!=null){
            if (RetrofitManager.getInstance().getBaseUrl().equals(RetrofitManager.BASE_URL)){
                webUrl = "http://m.v.huatu.com/?#/courseCard?userName=" + userName;
            }else{
                webUrl = "http://testm.v.huatu.com/wap/?#/courseCard?userName=" + userName;
            }
        }
        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, "课程卡");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }
}
