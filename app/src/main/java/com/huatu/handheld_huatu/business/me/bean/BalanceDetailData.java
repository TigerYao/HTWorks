package com.huatu.handheld_huatu.business.me.bean;

import java.util.ArrayList;

/**
 * Created by ht on 2017/9/9.
 */
public class BalanceDetailData {
    public int next;
    //收入明细
    public ArrayList<RechargeRes>  rechargeRes;
    //课程支出
    public ArrayList<ConsumeRes>  consumeRes;
    //申论,导错题支出
    public ArrayList<BalanceDetailResult> result;



    public class ConsumeRes {
        public String MoneyReceipt;//消费的金额
        public String PayDate;//消费发生时间
        public String consumeLog;//消费事由
    }
    public class RechargeRes {
        public String OrderDate;//充值发生时间
        public String Amount;//充值额
        public int origin;//
        public String actionDetail;//充值来源
    }
}
