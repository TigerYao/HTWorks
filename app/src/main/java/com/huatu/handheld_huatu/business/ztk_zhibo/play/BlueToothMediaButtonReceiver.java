package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.huatu.handheld_huatu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by saiyuan on 2017/12/21.
 */

public class BlueToothMediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent==null||keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_HEADSETHOOK or KEYCODE_MEDIA_PLAY_PAUSE");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_MEDIA_PLAY");
                    EventBus.getDefault().post(new BlueToothKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_MEDIA_PAUSE");
                    EventBus.getDefault().post(new BlueToothKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE));
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_MEDIA_STOP");
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_MEDIA_NEXT");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    LogUtils.i("keyEvent.getKeyCode: KEYCODE_MEDIA_PREVIOUS");
                    break;
            }
        }
    }
}
