package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-dj on 2017/9/5.
 */

public class TeacherListBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public class DataBean {
        public ArrayList<ResultBean> result;
    }

    public class ResultBean {
        public String TeacherId;
        public String TeacherName;
        public String goodat;
        public String roundPhoto;
        public String style;
    }

}
