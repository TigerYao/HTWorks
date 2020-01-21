package com.huatu.handheld_huatu.business.me.bean;

import java.util.ArrayList;

/**
 * Created by acage on 2018/9/28.
 */

public class RedPacketDetailBean {
    public String classTitle;//课程标题
    public String describe;//领取描述
    public String phone;
    public String receiveMoney;//个人领取金额
    public String receivePrice;//红包领取金额
    public String sumPrice;//总金额
    public int sumNum;//总数量
    public int status;//1：正常 2：退班 3：转班
    public int receiveSum;//领取数量
    public long surplusTime;//剩余时间
    public ArrayList<ReceiveDetail> receiveInfo;

//    classTitle	课程标题	string	@mock=
//    describe	领取描述	string	@mock=只差一点点，大红包就是你的啦~
//    phone	手机号	string	@mock=1
//    receiveInfo|2		array<object>
//    receiveMoney	个人领取金额	string	@mock=0
//    receivePrice	红包领取金额	string	@mock=0.00
//    receiveSum	领取数量	number	@mock=0
//    sumNum	总数量	number	@mock=10
//    sumPrice	总金额	string	@mock=1.00
//status	红包状态	number	1：正常 2：退班 3：转班
//    surplusTime	剩余时间	number	@mock=0
}
