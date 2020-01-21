package com.huatu.handheld_huatu.base;

/**
 * Created by saiyuan on 2016/11/18.
 */

public class ApiErrorCode {
    //请求成功
    public static final int ERROR_SUCCESS = 1000000;
    //请求成功
    public static final int ERROR_INVALID_DATA = 1098031;
    //用户会话过期,同时http状态码为401
    public static final int ERROR_SESSION_TIMEOUT = 1110002;
    //用户在其他设备登录,同一账户在另外一个设备登录,当前设备返回这个错误,同时http状态码为401
    public static final int ERROR_TOKEN_CONFLICT = 1110004;
    //没有设置过每日特训内容，进入到设置页面
    public static final int ERROR_NOT_SETTING_SPECIAL = 10041004;
}
