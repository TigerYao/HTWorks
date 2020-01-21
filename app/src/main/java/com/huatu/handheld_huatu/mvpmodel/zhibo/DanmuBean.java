package com.huatu.handheld_huatu.mvpmodel.zhibo;

import java.util.List;

/**
 * Created by cjx on 2018\7\30 0030.
 */

public class DanmuBean {

    public String current_page;
    public List<Data> data;


    public static class Data {
        public String background;
        public String content;
        public String rateDate;
        public String userId;
        public String userName;
        public float    videoNode;


    }

}
