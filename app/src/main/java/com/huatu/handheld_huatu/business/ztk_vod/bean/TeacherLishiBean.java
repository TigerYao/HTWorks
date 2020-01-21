package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-djd on 2017/9/15.
 */

public class TeacherLishiBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public class DataBean {
        public ArrayList<history_course> result;
        public int next;
    }

    public class history_course {
        public String CreateDate;
        public String NetClassId;
        public String avgscore;
        public String rid;
        public String title;
        public int star;

        @Override
        public String toString() {
            return "history_course{" +
                    "CreateDate='" + CreateDate + '\'' +
                    ", NetClassId='" + NetClassId + '\'' +
                    ", avgscore='" + avgscore + '\'' +
                    ", rid='" + rid + '\'' +
                    ", title='" + title + '\'' +
                    ", star=" + star +
                    '}';
        }
    }
}
