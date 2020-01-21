package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/12/13.
 *
 */

public class MianShouInfoBean implements Serializable{
    public long code;
    public DataBean data;
    public String message;
    public static class DataBean {
        public String SN;//报名序列号
        public String admission_student;//准考证号
        public String identifyID;//身份证号
        public String 	nickName;//考生姓名
        public String phone;//手机号
        public String rid;//课程id
        public String sex;//性别
        public String studentScore;//综合得分
    }
}
