package com.huatu.handheld_huatu.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 */
public class EventBusUtil<T> {

    public static <T> void sendMessage(int type,T oEvent) {
        EventBus.getDefault().post(new BaseMessageEvent<T>(
               type,oEvent));
    }

    public static <T> void sendMessage(Object o) {
        EventBus.getDefault().post(o);
    }
}
