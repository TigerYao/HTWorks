package com.huatu.handheld_huatu.business.me.bean;

/**
 * 我的账户bean
 * Created by ht on 2017/9/7.
 */

public class MyAccountBean {
    public MyAccountData data;
    public String message;
    public int code;


    public class MyAccountData {
        public UserCountre userCountres;
        public String explain;

        public class UserCountre {
            public String UserPoint;
            public long UserMoney;
        }
    }
}
