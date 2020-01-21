package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2019\6\3 0003.
 */

public class FaceAreaBean {

    @SerializedName(value ="areaid",alternate = {"catid"})
    public String areaid;

    @SerializedName(value ="areaname",alternate = {"catname"})
    public String areaname;

    public List<FaceAreaBean> child;


}
