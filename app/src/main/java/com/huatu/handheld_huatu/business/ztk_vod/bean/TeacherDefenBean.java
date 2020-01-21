package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/14.
 *
 */

public class TeacherDefenBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public class DataBean {
        public String teacherid;
        public String overall_ratings;
        public int star;

        @Override
        public String toString() {
            return "DataBean{" +
                    "teacherid='" + teacherid + '\'' +
                    ", overall_ratings='" + overall_ratings + '\'' +
                    ", star=" + star +
                    '}';
        }
    }
}
