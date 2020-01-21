package com.huatu;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2019\10\12 0012.
 */

public class AppContextProvider {
    public static Context INSTANCE;

    public static int SYSFLAG=0;

    public static final int GUESTSPLASH=1;       //游客splash点击广告

    public static final int BANNRCOURSETYPE=1<<2;//点击首页轮播图切课程科目

    public static final int WEBVIEW_REFRESHTYPE=1<<3;//点击首页轮播图切课程科目

    public static void  addFlags(int flag){
        SYSFLAG |= flag;
    }

    public static void removeFlag(int flag){
        SYSFLAG &= ~flag;
    }


    public static boolean hasFlag(int flag){
        return (SYSFLAG & flag)>0;
    }

}
