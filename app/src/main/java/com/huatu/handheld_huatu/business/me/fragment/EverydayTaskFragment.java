package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

/**
 * Created by ht on 2017/10/16.
 */

public class EverydayTaskFragment extends BaseWebViewFragment {
    public static Bundle getArgs() {
        String webUrl = RetrofitManager.getInstance().getBaseUrl() +"pc/reward/task.html?token="+ UserInfoUtil.token;
        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, "成长值任务");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }
}
