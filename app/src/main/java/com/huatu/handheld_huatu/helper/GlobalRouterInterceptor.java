package com.huatu.handheld_huatu.helper;

import android.content.Intent;

import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.netease.hearttouch.router.HTDroidRouterParams;
import com.netease.hearttouch.router.HTRouterEntry;
import com.netease.hearttouch.router.HTRouterManager;
import com.netease.hearttouch.router.HTUrlEntry;
import com.netease.hearttouch.router.IRouterCall;
import com.netease.hearttouch.router.intercept.IRouterInterceptor;
import com.netease.hearttouch.router.method.HTMethodRouterEntry;

/**
 * Created by Administrator on 2019\9\26 0026.
 */

public class GlobalRouterInterceptor  implements IRouterInterceptor {

    public interface OnLoginResultListener{
        void onLoginFail();
        void onLoginSuccess();
    }

    public static class SimpleLoginListener implements OnLoginResultListener{
        Runnable mDelayAction;
        public SimpleLoginListener(Runnable runnable){
            mDelayAction=runnable;
        }
        public void onLoginFail(){ }

        public void onLoginSuccess(){
            if(mDelayAction!=null)
                mDelayAction.run();
        }
    }


    public static OnLoginResultListener mOnLoginResultListener;

    public static void ClearListener(){
        mOnLoginResultListener=null;
    }

    @Override
    public void intercept(final IRouterCall call) {
        HTDroidRouterParams params = (HTDroidRouterParams) call.getParams();
        HTRouterEntry entry = HTRouterManager.findRouterEntryByUrl(params.url);
        if(entry != null) {
            if (entry.isNeedLogin() && (!SpUtils.getLoginState())) {

                Intent intent = new Intent(Constant.APP_LOGIN_ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ArgConstant.QUICK_LOGIN,true);
                params.getContext().startActivity(intent);
                call.cancel();
            } else {
                call.proceed();
            }
        } else {
           /* HTMethodRouterEntry entry2 = HTRouterManager.findMethodRouterEntryByUrl(params.url);
            if(entry2 != null) {
                call.proceed();
            }*/
            call.cancel();
        }
    }
}


     /*mOnLoginResultListener=new OnLoginResultListener() {
                @Override
                public void onLoginSuccess() {
                    call.proceed();
                }

                @Override
                public void onLoginFail() {
                    call.cancel();
                }
            };*/
          /*  LoginActivity.setOnLoginResultListener(new OnLoginResultListener() {
                @Override
                public void onLoginSuccess() {
                    call.proceed();
                }

                @Override
                public void onLoginFail() {
                    call.cancel();
                }
            });
            LoginActivity.start(params.getContext());*/
      /*      mOnLoginResultListener=  new GlobalRouterInterceptor.SimpleLoginListener(new Runnable() {
                @Override
                public void run() {
                    call.proceed();
                }
            });*/