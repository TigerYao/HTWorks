package com.huatu.handheld_huatu.helper;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018\5\7 0007.
 */

public class WeakRefHandler extends Handler {

    WeakReference<Activity> mActivity;
    public WeakRefHandler(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    public boolean checkReference(){
        Activity theActivity = mActivity.get();
        if(null==theActivity||theActivity.isFinishing()) return false;
        return true;
    }
}
