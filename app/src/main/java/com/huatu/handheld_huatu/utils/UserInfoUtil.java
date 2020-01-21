package com.huatu.handheld_huatu.utils;

import android.text.TextUtils;

import com.huatu.handheld_huatu.business.essay.bhelper.DlEssayDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;

/**
 * Created by saiyuan on 2016/12/19.
 */
public class UserInfoUtil {
    public static int userId;
    public static String ucId;

    public static String mId;//教育的UseiD   BM代注册接口http://172.17.17.80:82/index.php?s=/3&page_id=130

    public static String token;             // 用户token,登录状态才有，测试用token：e3bb506247a44689b77db970898c12b7
    public static String userAgent;         // User-Agent 采用系统原始即可,不需做任何处理
    public static String userName;
    private static LiveUserInfo mLiveUserInfo;

    public static void clearLiverUserInfo() {
        mLiveUserInfo = null;
    }

    public static LiveUserInfo getLiveUserInfo() {
        if (null != mLiveUserInfo && (!TextUtils.isEmpty(mLiveUserInfo.userNumber))) {
            return mLiveUserInfo;
        }
        return null;
    }

    //防止多线程重复写出错
    public static void setLiveUserInfo(LiveUserInfo liveUserInfo) {

        if (mLiveUserInfo != null) return;
        if (null == liveUserInfo) return;

        synchronized (UserInfoUtil.class) {
            if (null == mLiveUserInfo) {
                mLiveUserInfo = new LiveUserInfo();
                mLiveUserInfo.userNumber = liveUserInfo.userNumber;
                mLiveUserInfo.userNick = liveUserInfo.userNick;
                mLiveUserInfo.userAvatar = TextUtils.isEmpty(liveUserInfo.userAvatar) ? "" : liveUserInfo.userAvatar;
            }
        }
    }

    public static void init() {
        userAgent = System.getProperty("http.agent");
        token = SpUtils.getToken();
        userId = SpUtils.getUid();
        userName = SpUtils.getUname();
        ucId = SpUtils.getUCenterId();
    }

    public static void setUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean == null || userInfoBean == null) {
            return;
        }
        token = userInfoBean.data.token;
        userId = userInfoBean.data.id;
        ucId = userInfoBean.data.mobile;

        // 储存信息
        SpUtils.setUCenterId(ucId);
        SpUtils.setLoginState(true);
        SpUtils.setNick(userInfoBean.data.nick);
        SpUtils.setToken(userInfoBean.data.token);
        SpUtils.setUid(userInfoBean.data.id);
        SpUtils.setUname(userInfoBean.data.uname);
        SpUtils.setMobile(userInfoBean.data.mobile);
        SpUtils.setEmail(userInfoBean.data.email);
        SpUtils.setAvatar(userInfoBean.data.avatar);
        SpUtils.setUserCatgory(userInfoBean.data.catgory);
        SpUtils.setUserSubject(userInfoBean.data.subject);
        SpUtils.setArea(userInfoBean.data.area);
        SpUtils.setAreaname(userInfoBean.data.areaName);
        SignUpTypeDataCache.getInstance().reSetData();
        DlEssayDataCache.getInstance().reSetData();
    }

    public static void clearUserInfo() {
        token = "";
        userId = -1;
        ucId = "";
        SpUtils.setToken("");
        SpUtils.setUid(-1);
        SpUtils.setUCenterId("");
        SpUtils.setArea(-9);
        SpUtils.setLoginState(false);
        SpUtils.setNick("");
        SpUtils.setUname("");
        SpUtils.setAreaname("");
        SpUtils.setMobile("");
        SpUtils.setEmail("");
        SpUtils.setAvatar("");
        SpUtils.setUserCatgory(-1);
        SpUtils.setUserSubject(-1);
        SpUtils.setHtSubjectList("");
        DlEssayDataCache.getInstance().clearData();
    }
}
