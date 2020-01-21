package com.huatu.handheld_huatu.business.faceteach.bean;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2018\7\13 0013.
 */

public class FaceOrderListResponse extends BaseListResponse<FaceOrderBean> {

    @SerializedName("data")
    public List<FaceOrderBean>  data;

    public List<FaceOrderBean>  mLessionlist;

    @Override
    public void clearList() {
        if(mLessionlist != null){
            mLessionlist.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if(mLessionlist != null){
            mLessionlist.addAll(response.getListResponse());
        }
    }

    @Override
    public List<FaceOrderBean> getListResponse() {
        if(data != null){
            return data;
        }
        return Collections.emptyList();
    }

    @Override
    public int getCode(){
        return (code==0||code==-3)? ApiErrorCode.ERROR_SUCCESS:code;
    }//-3为空数据
}

