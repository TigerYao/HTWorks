package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import java.io.Serializable;

public class PayInfo implements Serializable{
	public String OrderNum;
	public String MoneySum;
	public int xxb;                     //是否可以用学习币支付1可以0不可以
    public int redirectTo;              //redirectTo为1正常跳转，2通过订单号请求订单详情接口跳转到订单详情页面，3弹框提示继续支

	public String description;
	public String title;
    public String notify_url;

    public String aliSign;//支付宝签名
    public String appid;
    public String noncestr;
    public String packageValue;
    public String partnerid;
    public String prepay_id;
    public String sign;
    public String timestamp;
    public int recharge;
    public int experience;

    public long orderId;                // 订单id
    public long redEnvelopeId;          // 红包id


    public String orderStr;

    public AddGroupInfo qqWxGroup;//二维码加群信息

//             appid": "wxd0611111b31aa452",
//            prepay_id: "wx3114222739421747c17bdf6e1148234500",
//            noncestr: "MDAZY0iOx8Gf5Qjg",
//            packageValue: "Sign=WXPay",
//            partnerid: "1310649601",
//            timestamp: 1572502947,
//            sign: "EDEA7B4BF5796DE166E5B587BC015034",
//            orderId: 11887758,
//            orderNum: "HTKC_Oa_2019103114202104107525",
//            redEnvelopeId: 0

//            aliSign: "app_id=2016102402301719&method=alipay.trade.app.pay&format=JSON&charset=utf-8&sign_type=RSA2&version=1.0&return_url=http%3A%2F%2Fm.v.huatu.com%2Fwap%2F%23%2Fclass%2FbuyBack&notify_url=http%3A%2F%2Ftestapi.huatu.com%2Flumenapi%2Fv5%2Fcallback%2Forder%2Fapp_order_alipay&timestamp=2019-10-31+14%3A21%3A27&sign=mSy8rh19excbcuTDsWvkhnYysf%2B2mP3INHKEr8OGbR07HIBTMbTCAbHxNyDmX8gQ0uHHgF9XdokwjoE8%2B6SFXUGqd3YEWFPkgc%2FPAr42EuMnncBScifBj6mnZ1%2Bi9MTD%2BdwkEUaXWKuGx7SQoI82Ayhs2bB22X2TQDzhABeJJeYhdMt1oWWG1dYZSyesI123RF3viXlqprLxMN62MDbbxM%2FNMT5ntSPHHS2yADYG4XmkBFe7QVxcBUqYGPo5fAia3ctLbC4dmqISKpzD21FKEl%2FeYlCr1U7Qsp1NZr9ZtboecOZAPMQxQugxmTyUqFW54aLgAIftKVkMAwkHDiZvLA%3D%3D&biz_content=%7B%22out_trade_no%22%3A%22HTKC_Oa_2019103114202104107525%22%2C%22total_amount%22%3A%222.00%22%2C%22subject%22%3A%22%5Cu534e%5Cu56fe%5Cu5728%5Cu7ebf%5Cu8bfe%5Cu7a0b%5Cu8d2d%5Cu4e70%22%2C%22body%22%3A%22%5Cu534e%5Cu56fe%5Cu5728%5Cu7ebf%5Cu8bfe%5Cu7a0b%5Cu8d2d%5Cu4e70%22%2C%22notify_url%22%3A%22http%3A%5C%2F%5C%2Ftestapi.huatu.com%5C%2Flumenapi%5C%2Fv5%5C%2Fcallback%5C%2Forder%5C%2Fapp_order_alipay%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D",
//            orderNum: "HTKC_Oa_2019103114202104107525",
//            orderId: 11887758,
//            redEnvelopeId: 0

    public class AddGroupInfo implements Serializable{
        public String number;
        public String qrCode;
        public int service;
        public String function;
        public String title;
    }

}
