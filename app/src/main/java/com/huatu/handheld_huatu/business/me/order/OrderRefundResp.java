package com.huatu.handheld_huatu.business.me.order;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiyuan on 2017/10/1.
 */

public class OrderRefundResp implements Serializable {
    public String AddTime;
    public String Remark;
    public List<String> Title;
    public int status;
    public float total;
    public String reason;
}
