package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-ldc on 2017/12/13.
 */

public class HighendInfoBean implements Serializable{
    public long code;
    public DataBean data;
    public String message;
    public static class DataBean {
        public String tips;
        public String department;//报考部门
        public String departCode;//部门代码
        public String position;//报考职位名称
        public String positionCode;//职位代码
        public String mianshiScore;//最低面试分数
    }

}
