package com.huatu.handheld_huatu.business.me.bean;

public class RedBagInfo {

    /**
     * "aloneByPrice": 100,
     * "aloneMiniPrice": 10,
     * "aloneNum": 10,
     * "endTime": "2018-09-27 20:52:36",
     * "param": 1,
     */

    public double aloneByPrice;                        // 红包总金额
    public double aloneMiniPrice;                      // 红包最少金额
    public int aloneNum;                            // 红包个数
    public long endTime;                            // 距结束时间
    public String param;                            // 加密参数
    public long redEnvelopeId;                      // 待发红包ID
    public String instruction;                      // 活动详情
}
