package com.huatu.handheld_huatu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by Administrator on 2018\11\7 0007.
 */


public class SendReceiver extends BroadcastReceiver {
    public final static String ACTION_SEND = "pw.msdx.ACTION_SEND";

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_SEND.equals(action)) {
            LogUtils.i("SendReceiver", "send a message");
        }
    }
}