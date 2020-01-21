package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.lessons.bean.AllCourseData;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on
 */

public class DateLiveListResponse extends BaseResponse {

    public Data data;

    public static class Data{

        @SerializedName("data")
        public List<DateLiveBean> liveData;

        public String  msg;

        public int  type;//:  "未来七天内，没有直播课哦~","type":1,
                         //0:  当天有安排,1:七日内无直播;2七日内有直播;3没有直播,要跳转
        public String day;

    }
}

