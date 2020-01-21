package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by ht on 2018/1/13.
 */
public class LoadScanResultFragment extends BaseWebViewFragment {

    public static Bundle getArgs(String result) {
            LogUtils.i("webUrl: " + result);
            Bundle bundle = new Bundle();
            bundle.putString(ARGS_STRING_URL, result);
            bundle.putString(ARGS_STRING_TITLE,"华图在线");
            bundle.putBoolean("isShowTitle", true);
            bundle.putBoolean("isShowButton", false);
            bundle.putBoolean("isSupportBack", false);
            return bundle;
    }
}
