package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-ldc on 2017/10/3.
 */

public class MiaoShaBean implements Serializable{
    public String userName;
    public String nickName;
    public String userFace;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    @Override
    public String toString() {
        return "MiaoShaBean{" +
                "userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userFace='" + userFace + '\'' +
                '}';
    }
}
