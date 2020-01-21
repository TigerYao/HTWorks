package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;
import java.util.List;

public class CourseActDetailBean implements Serializable {
    public String aloneByPrice;
    public List<ActDetailInfo> data;

    public CourseActDetailBean(String aloneByPrice, List<ActDetailInfo> data) {
        this.aloneByPrice = aloneByPrice;
        this.data = data;
    }
}
