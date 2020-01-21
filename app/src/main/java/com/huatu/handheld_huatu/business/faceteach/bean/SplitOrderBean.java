package com.huatu.handheld_huatu.business.faceteach.bean;

import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;

public class SplitOrderBean {
    /**
     * {
     * "sonoid": "S-P1433843333RN455_1",
     * "sonprice": "0.10",
     * "sonstate": "1",
     * "sontime": "2015-06-15 15:04:53",
     * "sonpaytime": null
     * },
     */
    public String sonoid;
    public String sonprice;
    public String sonstate;// -1、正在编辑 0、未支付 1、已支付
    public String sontime;
    public String sonpaytime;

    @Override
    public String toString() {
        return sonprice;
    }

    public int getState() {
        if (!Utils.isEmptyOrNull(sonstate))
            return Integer.parseInt(sonstate);
        return -1;
    }

    public float getPrice(){
        if(!Utils.isEmptyOrNull(sonprice))
            return Float.parseFloat(sonprice);
        return 0.0f;
    }
}
