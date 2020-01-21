package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-djd on 2017/9/15.
 *
 */

public class TeacherDetailListBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public class DataBean {
        public ArrayList<ResultBean> result;
        public int next;
    }

    public class ResultBean {
        public int finalPrice;
        public String ActualPrice;
        public String buyNum;
        public String Price;
        public String TeacherDesc;
        public String Title;
        public String limitNum;
        public String rid;
        public String scaleimg;
        public String TimeLength;

        @Override
        public String toString() {
            return "ResultBean{" +
                    "finalPrice=" + finalPrice +
                    ", ActualPrice='" + ActualPrice + '\'' +
                    ", buyNum='" + buyNum + '\'' +
                    ", Price='" + Price + '\'' +
                    ", TeacherDesc='" + TeacherDesc + '\'' +
                    ", Title='" + Title + '\'' +
                    ", limitNum='" + limitNum + '\'' +
                    ", rid='" + rid + '\'' +
                    ", scaleimg='" + scaleimg + '\'' +
                    ", TimeLength='" + TimeLength + '\'' +
                    '}';
        }
    }
}
