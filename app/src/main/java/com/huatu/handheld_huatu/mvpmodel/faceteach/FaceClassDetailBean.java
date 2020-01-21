package com.huatu.handheld_huatu.mvpmodel.faceteach;

import java.util.ArrayList;

public class FaceClassDetailBean {
    /**
     * {
     *         "list": [
     *             {
     *                 "bb": "只含有\"协议班\"并且协议都相同，开课日期不同",
     *                 "xfprice": "3",
     *                 "refundrule": "笔试协议",
     *                 "kksj": "2019-07-01",
     *                 "sksj": "2019-07-01至2019-09-30 (1天2晚)",
     *                 "kcdd": "成都",
     *                 "refundtext": "笔试未通过退2",
     *                 "zstj": "否",
     *                 "bc": "GBGJSCA020028CCC",
     *                 "aid": "4872207",
     *                 "goodsid": "3580",
     *                 "courseid": "1465",
     *                 "title": "111111",
     *                 "fb": "成都分部",
     *                 "shengfen": "全国",
     *                 "ifwb": "1",
     *                 "canyin": "1",
     *                 "hasElectronicProtocol": "0",
     *                 "course_text": "asdfgh",
     *                 "days": "67天后开课",
     *                 "dzqz_viewurl": "",
     *                 "alipay_account": "sc_sccw@163.com",
     *                 "pid": "2088801077583036"
     *             },
     *         ],
     *         "ntalker_id": "kf_10092_1513819620894",
     *         "city_tel": "400619999"
     *     }
     */

    public String ntalker_id;               // 小能客服ID
    public String city_tel;                 // 客服电话
    public ArrayList<FaceClassBean> list;   // 课程数据

    public static class FaceClassBean{
        public String aid;              // 课程aid，下单使用
        public String bb;               // 产品名称

        public String refundrule;       // 是否协议
        public String refundtext;       // 协议具体描述

        public String xfprice;          // 价格

        public String bx;               // 班型
        public String fxid;             // 分校id
        public String zwbh;             // 职位保护 0、无 1、有

        public String kksj;             // 开课时间
        public String sksj;             // 上课周期描述

        public String kcdd;             // 上课地点
        public String zstj;             // 住宿条件
        public String bc;               // 班次
        public String goodsid;          // 产品ID
        public String courseid;         // 课程ID
        public String title;            // 标题
        public String fb;               // 所属分部
        public String shengfen;         // 所属省份
        public String ifwb;             // 是否允许网报
        public String canyin;           // 餐饮 0-无 1-有
        public String hasElectronicProtocol;    // 是否电子协议 0-无 1-有
        public String course_text;      // 课程介绍
        public String days;             // 距离开课时间
        public String dzqz_viewurl;     // 电子协议预览地址
        public String alipay_account;   // 支付宝账号
        public String pid;              // 支付宝PID
    }
}
