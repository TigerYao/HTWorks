package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CheckGoodOrderBean implements Serializable {


    public float total;
    public int payType;
    public long orderId;
    public List<GoodOrderBean> goods=new ArrayList<>();

    public static  class GoodOrderBean implements Serializable{
        public  int count;
        public long goodsId;
    }

}
