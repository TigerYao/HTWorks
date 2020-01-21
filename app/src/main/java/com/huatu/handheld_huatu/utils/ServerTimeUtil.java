package com.huatu.handheld_huatu.utils;

import android.os.SystemClock;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 获取与服务器相同的时间戳
 * 从服务器获取当前时间，然后与当前开机时间进行差值，
 * 那任意时刻的服务器时间就是当前开机时间加差值，
 * 不可记录Share，因为关机不清，所以要在某些页面进行访问
 */
public class ServerTimeUtil {

    // 单例模式
    private static volatile ServerTimeUtil singleton;

    public static ServerTimeUtil newInstance() {
        if (singleton == null) {
            synchronized (ServerTimeUtil.class) {
                if (singleton == null) {
                    singleton = new ServerTimeUtil();
                }
            }
        }
        return singleton;
    }

    private int repeat;             // 重复请求次数，大于5次就不请求了

    private ServerTimeUtil() {
    }

    private long openDTime = 0;         // 服务器时间戳 - 开机时间戳 = openDTime    那么 服务器时间戳 = 开机时间戳 + openDTime
    private long currentDTime = 0;      // 服务器时间戳 - 手机时间戳 = currentDTime 那么 服务器时间戳 = 手机时间戳 + currentDTime   这个只是以备获取不到开机时长

    public void initServerTime() {
        if (!NetUtil.isConnected()) {
            return;
        }

        if (openDTime > 0) {
            return;
        }

        repeat++;

        OkHttpUtils
                .get()
                .url("https://ns.huatu.com/time")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        repeatInit();
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            BaseResponseModel<Long> r = GsonUtil.getGson().fromJson(response, new TypeToken<BaseResponseModel<Long>>() {
                            }.getType());

                            Long sysTime = r.data;
                            long eRealTime = SystemClock.elapsedRealtime();
                            if (eRealTime > 0) {
                                openDTime = sysTime - eRealTime;
                            }
                            currentDTime = sysTime - System.currentTimeMillis();
                            repeat = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                            repeatInit();
                        }
                    }
                });
    }

    private void repeatInit() {
        if (repeat <= 5) {
            initServerTime();
        } else {
            repeat = 0;
        }
    }

    public long getServerTime() {
        if (openDTime > 0) {
            return SystemClock.elapsedRealtime() + openDTime;
        } else if (currentDTime > 0) {
            return System.currentTimeMillis() + currentDTime;
        } else {
            return System.currentTimeMillis();
        }
    }
}
