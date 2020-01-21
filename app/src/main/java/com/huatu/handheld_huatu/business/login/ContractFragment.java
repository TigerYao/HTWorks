package com.huatu.handheld_huatu.business.login;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by ht on 2017/10/20.
 */
public class ContractFragment extends BaseWebViewFragment{

    // 服务协议
    public static Bundle getServiceAgreementArgs() {
        String webUrl = RetrofitManager.getInstance().getBaseUrl() +"pc/agreement/index.html";
        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, "用户服务协议");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }

    // 隐私政策
    public static Bundle getPrivacyPolicyArgs() {
        String webUrl = "http://m.v.huatu.com/z/agree/secret.html";
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, "隐私政策");
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }

}
