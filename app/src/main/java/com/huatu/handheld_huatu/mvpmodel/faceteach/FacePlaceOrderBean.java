package com.huatu.handheld_huatu.mvpmodel.faceteach;

import java.io.Serializable;

public class FacePlaceOrderBean implements Serializable {
    /**
     * "oid": "APK1509437766HT489",
     * "priceCount": "5380",
     * "cartcount": "1",
     * "title": "导师决胜状元班",
     * "kcdd": "芜湖市",
     * "sksj": "2017年11月09号开始上课（4天4晚赠住宿）",
     * "alipay_account": "zxwhcw@huatu.com",
     * "pid": "2088521164990447",
     * "wx_appid":"",
     * "wx_partnerid":""
     */

    public String oid;              // 订单编号
    public String priceCount;       // 订单价格
    public String cartcount;        // 购买数量
    public String title;            // 课程名称
    public String kcdd;             // 上课地点
    public String sksj;             // 上课时间
    public String alipay_account;   // 支付宝账号
    public String pid;              // 支付宝PID
    public String wx_appid;         // 微信appid
    public String wx_partnerid;     // 微信partnerid
}
