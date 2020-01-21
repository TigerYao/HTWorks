package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dongd on 2016/11/16.
 */

public class HomeConfig implements Serializable {

    public int unreadMsgCount;
    public int unreadActCount;

    public HomeConfig(int unRead){
        unreadMsgCount=unRead;
    }

}
