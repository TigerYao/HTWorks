package com.huatu.handheld_huatu.mvpmodel;


import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.me.bean.MyOrderData;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2016/8/2.
 */
public class MyOrderListResponse extends BaseListResponse<MyOrderData.OrderList> {

    @SerializedName("data")
    public MyOrderData data;
/*
    public static class Data{
        @SerializedName("result")
        public List<Lessons> dynamicsList;

        public int next;
    }*/

    public List<MyOrderData.OrderList> mOrderlist;

    @Override
    public void clearList() {
        if(mOrderlist != null){
            mOrderlist.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if(mOrderlist != null){
            mOrderlist.addAll(response.getListResponse());
        }
    }

    @Override
    public List<MyOrderData.OrderList> getListResponse() {
        if(data != null&&data.result!=null){
            return data.result;
        }
        return Collections.emptyList();
    }


}
