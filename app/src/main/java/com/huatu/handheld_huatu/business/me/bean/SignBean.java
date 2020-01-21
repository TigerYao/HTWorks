package com.huatu.handheld_huatu.business.me.bean;

/**
 * Created by ht on 2017/10/16.
 */
public class SignBean {
    public int code;
    public String message;
    public SignData data;
//    1110002	用户会话过期
//    1115107	未签到


    private class SignData {
        public long uid;
        public long signTime;
        public int type;
//        uid	long	用户id
//        signTime	long	时间戳
//        type	int	签到类型
    }
}
