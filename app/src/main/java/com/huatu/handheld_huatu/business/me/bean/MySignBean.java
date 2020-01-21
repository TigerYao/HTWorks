package com.huatu.handheld_huatu.business.me.bean;

/**
 * Created by ht on 2017/10/16.
 */
public class MySignBean {
    public int code;
    public String message;
    public MySignData data;
//    1110002	用户会话过期
//    1115106	已签到


    public class MySignData {
        public String bizId;
        public String action;
        public long uid;
        public long timestamp;
        public int gold;
        public int experience;

//        bizId	string	业务id
//        action	string	动作
//        uid	long	用户id
//        timestamp	long	时间戳
//        gold	int	图币
//        experience	int	经验
    }
}
