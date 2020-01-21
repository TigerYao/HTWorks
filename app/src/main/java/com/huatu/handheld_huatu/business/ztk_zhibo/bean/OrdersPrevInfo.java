package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import com.huatu.handheld_huatu.business.lessons.bean.CourseSuitData;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolExamUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/22.
 */

public class OrdersPrevInfo {
    public float memDiscount;
    public String actualPrice; //应付价格T
    public String brief;//课程介绍
    public float calcDisCount;//立减金额
    public String classType;//所属班次

    public float price;//课程原价
    public int manjian;
//    public List<AddressInfoBean> address;
    public  AddressInfoBean address;
    public int point;
    public int isLogistics; //是否需要邮寄
    public String title;
//    public String usermoney;
    public List<PoundInfoBean> djq;
    public String scaleimg;
    public String payLimit;
    public String limit;//优惠描述
    public float logisticsCost;//运费
    public String memberDiscount;//会员折扣
    public String moneyDisCount;// 总优惠金额
    public String p;//下单加密字符串
    public String treatyId;//协议ID
    public String treatyUrl;
    public String isMianshou;//面授
//    public int count_essay;  //赠送套题批改次数
//    public int single_count_essay; //赠送单题批改次数
    public String count_essay_end_time;
    public int hasProtocol; //0无协议 1未填写 2已填写
//    public String protocolId;
    public String protocolName;
    public ProtocolExamUserInfo protocolInfo;
//    public int argumentation;//文章写作批改次数
    public List<Exposition> exposition;
    public int payGold; //0不是图币，1，是图币
    public long goldNum;
    public long rid;//课程id
    public ArrayList<TeacherInfo> teacherInfo;
    public String timeLength; //课时描述
    public String videoType;//课程类型
    public class TeacherInfo {
        public String teacherId;
        public String teacherName;
        public String roundPhoto;
    }
}
