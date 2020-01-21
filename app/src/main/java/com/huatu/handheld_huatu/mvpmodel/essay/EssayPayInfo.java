package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;

public class EssayPayInfo implements Serializable{
	public String orderNum;
	public String moneySum;
	public int xxb;//是否可以用学习币支付1可以0不可以
    public int redirectTo; //redirectTo为1正常跳转，2通过订单号请求订单详情接口跳转到订单详情页面，3弹框提示继续支

	public String description;
	public String title;
    public String notifyUrl;

    public String appId;
    public String nonceStr;
    public String packageValue;
    public String partnerId;
    public String prepayId;
    public String sign;
    public String timestamp;

    public String orderStr;
}
