package com.huatu.handheld_huatu.business.me.bean;

import com.huatu.handheld_huatu.utils.TimeUtils;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by chq on 2018/7/28.
 */

public class MyEssayOrderData implements Serializable {
    public int next;
    public ArrayList<EssayOrderList> result;
    public int totalPage;    //总页数
    public int total;    //总记录数


    public class EssayOrderList implements Serializable {
        public int bizStatus;    //订单状态
        public String bizStatusName;//订单状态名称
        public String createTime;    //下单时间
        public ArrayList<GoodList> goodsList; //商品列表
        public long id; //订单
        public String orderNumStr;    //订单编号
        public String payTime;    //支付时间
        public int payType;    //支付类型
        public int realMoney;    //实际支付金额（单位：分）
        public int totalMoney;    //订单总金额（单位：分）
        public long userId;    //用户id
        public long leftTime;    //距离订单关闭的时间
        public String memo;

        public class GoodList implements Serializable {
            public int count;//商品数量
            public long goodsId;//商品id
            public String name; //商品名称
            public int correctMode;//批改模式（2人工批改 1智能批改）
            public long expireDate;//商品有效期
            public int expireFlag;  // 0 是无有效期 1时存在有效期

            public String getGoodTitle() {
                return (correctMode == 2 ? "人工批改--" : "智能批改--") + name + "  x" + count;
            }
            public String getGoodTitle2() {
                return (correctMode == 2 ? "人工批改 (" : "智能批改 (") + name + ") x" + count;
            }

            public String getExp() {
                if (expireDate > 0 || expireFlag == 1)
                    return "有效期" + expireDate + "天";
                else
                    return "";
            }
        }
    }

}
