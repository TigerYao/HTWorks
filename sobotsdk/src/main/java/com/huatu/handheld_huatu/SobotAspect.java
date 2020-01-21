package com.huatu.handheld_huatu;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.huatu.AppContextProvider;
import com.sobot.chat.SobotApi;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by Administrator on 2019\10\10 0010.
 */


@Aspect
public class SobotAspect {

    private static final String TAG = "MethodAspect";
    @After("execution(* com.huatu.handheld_huatu.helper.SobotChatServer.init(..))")
    public void init(JoinPoint joinPoint) {
        Log.e(TAG,"AfterMethodExecution222");

        if(AppContextProvider.INSTANCE!=null){
            com.huatu.autoapi.auto_api.ApiRegister.init();
            SharedPreferences SpUtils= AppContextProvider.INSTANCE.getSharedPreferences("config", Context.MODE_PRIVATE);
            int userID= SpUtils.getInt("User_Id", -1);
            SobotApi.initSobotSDK(AppContextProvider.INSTANCE, StartChatUtil.SOBOT_APP_KEY,userID>0 ?String.valueOf(userID):"");
        }
    }
}
