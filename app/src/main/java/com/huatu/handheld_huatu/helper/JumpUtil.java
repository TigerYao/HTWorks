package com.huatu.handheld_huatu.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.netease.hearttouch.router.method.HTMethodRouter;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *   方法路由
 */

public class JumpUtil {

    private static final String TAG = "JumpUtil";
    private static JumpUtil sInstance = null;

    public static JumpUtil getInstance() {
        if (sInstance == null) {
            synchronized (JumpUtil.class) {
                if (sInstance == null) {
                    sInstance = new JumpUtil();
                }
            }
        }
        return sInstance;
    }


    private JumpUtil() {
    }

 /*   public String formatStr(String url){

       // return URLEncoder.encode(url)
    }*/

    //@HTMethodRouter(url = {"ztk://share"}, needLogin = false)
    public void startShare(Context context, String title) {
        String msg = "jumpA called: str=" + title ;
       // URLDecoder.decode(title)

        Log.i(TAG, msg);
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

/*    @HTMethodRouter(url = {"http://www.you.163.com/jumpB"})
    public static void jumpB(Context context, String str, int i) {
        String msg = "jumpB called: str=" + str + "; i=" + i;
        Log.i(TAG, msg);
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    @HTMethodRouter(url = {"http://www.you.163.com/jumpC"})
    public void jumpC() {
        Log.i(TAG, "jumpC called");
    }*/
}
