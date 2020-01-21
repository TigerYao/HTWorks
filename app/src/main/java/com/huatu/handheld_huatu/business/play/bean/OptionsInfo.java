package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;

public class OptionsInfo implements Serializable {
    public String name;
    public int id;
    public boolean disable;
    public boolean chose;
    public String price;
    public String actualPrice;
    public String buyNum;
    public String title;
    public boolean iso2o;
    public int goodsId;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof OptionsInfo) {
            OptionsInfo info = (OptionsInfo) obj;
            return id == info.id;
        }
        return super.equals(obj);
    }
}