package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;

/**
 * @author zhaodongdong
 */
public class AdvertiseConfig implements Serializable {
    public String target;
    public AdvertiseItem params;//图片信息


    public String toUriString(){
        return target+"?"+params.formatString();
    }
}
