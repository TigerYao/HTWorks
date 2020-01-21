package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by saiyuan on 2018/8/24.
 */

public class CourseContractFragment extends BaseWebViewFragment {
    public static Bundle getArgs( String Url) {
//        String webUrl = Url;
//        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, Url);
        bundle.putString(ARGS_STRING_TITLE, "查看协议");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putBoolean("isFromOrder", true);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }
}
