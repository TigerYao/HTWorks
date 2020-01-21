package com.huatu.handheld_huatu.business.me.bean;

import java.util.ArrayList;

/**
 * Created by saiyuan on 2018/9/27.
 */

public class RedPacketBean {
    public String redEnvelopePrice;
    public ArrayList<ReceiveInfo> receiveInfo;
    public ArrayList<RedEnvelopeInfo> redEnvelopeInfo;


//    receiveInfo|3		array<object>
//    receivePrice	领取金额	string	@mock=$order('1.1','1.1','1.1')
//    receiveTime	领取时间	string	@mock=$order('2019-01-01 00:00:00','2019-01-01 00:00:00','2019-01-01 00:00:00')
//    redEnvelopeInfo|3		array<object>
//    classTitle	课程标题	string	@mock=$order('我的课程','我的课程','我的课程')
//    id	红包id	number	@mock=$order(1,2,3)
//    receivePrice	领取金额	string	@mock=$order('50','50','50')
//    receiveSum	领取数量	number	@mock=$order(5,5,5)
//    sumNum	总数量	number	@mock=$order(15,15,15)
//    sumPrice	总金额	string	@mock=$order('100','100','100')
//    redEnvelopePrice	提现金额	string	@mock=1.1
}
