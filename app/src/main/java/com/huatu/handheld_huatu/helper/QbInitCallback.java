package com.huatu.handheld_huatu.helper;

import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by Administrator on 2019\2\18 0018.
 */

public class QbInitCallback implements QbSdk.PreInitCallback {

    public void onCoreInitFinished(){}

    public void onViewInitFinished(boolean var1){
        CommonUtils.hasX5nited=true;
    }
}
