package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2019\6\3 0003.
 */

public class BaseStringBean {
    @SerializedName(value ="url",alternate = {"mid"})
    public String url;
}
