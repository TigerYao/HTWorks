package com.huatu.handheld_huatu.mvpmodel.faceteach;

import java.io.Serializable;

public class FaceGoodsDetailBean implements Serializable {
    /**
     *     "aid": "1185472",
     *     "title": "导师决胜班",
     *     "kcdd": "兴义市",
     *     "bc": "TMGYJS31708",
     *     "bb": "导师决胜班",
     *     "km": "申论批改五篇",
     *     "bx": "教师招聘",
     *     "zsrs": "25",
     *     "xs": "77",
     *     "bmdz": "兴义市桔山大道兴义商城a座六楼6-15",
     *     "sksj": "2017年11月01日、11月07日(上午、下午、晚上)(上午：9:00-12:00下午：14:30-17:30 晚上：19:00-21:00)",
     *     "zwbh": "1",
     *     "sqfx": "贵州分校",
     *     "ssfb": "北京总部",
     *     "phone": "08593116855",
     *     "fx_zuobiao": "106.695328,26.571574",
     *     "alipay_account": "gz_xycw@163.com",
     *     "pid": "2088211351700264",
     *     "wx_appid": "",
     *     "wx_partnerid": "",
     *     "customer_branch":"kf_10092_1513751987334";
     *     "zsflag": "2",
     *     "zs_price": "5980",
     *     "no_zs_price": 5380,
     *     "yh_price": "0",
     *     "intro": "http://XXX.com/html/notice.html",
     *     "buynotice": "http://XXX.com/html/notice.html",
     *     "zs":"600",
     *     "shengfen":"贵州省"
     *     "thumb":"http://app.huatu.com/upload/mall_default_htcourse_big.png",
     *     "goodsid": "58359",
     *     "hasElectronicProtocol": "1",
     *     "dzqz_viewurl": "http://test.bm.huatu.com/member/getPreviewProtocolUrlBeforeBuy.php?goodsId=58359"
     */

    public String aid;                  // 课程aid
    public String title;                // 课程名称
    public String kcdd;                 // 上课地点
    public String bc;                   // 课程班号
    public String bb;                   // 课程班别
    public String km;                   // 科目
    public String bx;                   // 课程班型
    public String zsrs;                 // 限招人数
    public String xs;                   // 课程学时
    public String bmdz;                 // 报名地址

    public String kksj;                 // 开课时间
    public String sksj;                 // 上课安排

    public String lx;                   // 协议类型
    public String zsjzprice;            // 协议具体描述

    public String zwbh;                 // 是否职位保护，1-职位保护 0-不保护
    public String sqfx;                 // 所属分校
    public String ssfb;                 // 所属分部
    public String phone;                // 客服电话
    public String fx_zuobiao;           // 分校地址坐标
    public String alipay_account;       // 支付宝账号
    public String pid;                  // 支付宝PID
    public String wx_appid;             // 微信appid
    public String wx_partnerid;         // 微信partnerid
    public String customer_branch;      // 小能客服ID
    public String zsflag;               // 住宿标志 0-强制不住宿 价格显示zs_price，文字显示不含住宿 1-强制住宿 价格显示zs_price，文字显示含住宿 2-可选择 默认显示zs_price，文字显示含住宿 3-住宿已满 价格显示no_zs_price，文字显示不含住宿
    public String zs_price;             // 住宿价格
    public String no_zs_price;          // 不住宿价格
    public String yh_price;             // 优惠后价格
    public String intro;                // 课程介绍 H5 URL地址
    public String buynotice;            // 购买须知 H5 URL地址
    public String zs;                   // 住宿费用
    public String shengfen;             // 所在省份
    public String thumb;                // 面授封面图片
    public String goodsid;              // 商品对应的产品ID
    public String hasElectronicProtocol;// 是否有电子签章，0-没有 1-有
    public String dzqz_viewurl;         // 电子签章的预览地址
}
