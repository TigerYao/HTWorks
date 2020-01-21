package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2018\8\4 0004.
 */


public  class SimpleListResponse<MODE> extends BaseListResponse<MODE>{

    public List<MODE> mAdapterList;

    @SerializedName(value ="data",alternate = {"list"})
    public List<MODE> list;//json 对应list         @SerializedName(value ="ActualPrice",alternate = {"actualPrice"})

    @Override
    public void clearList(){
        if(mAdapterList!=null) mAdapterList.clear();
    }

    @Override
    protected  <RESPONSE extends BaseListResponse> void processData(RESPONSE response){
        if(mAdapterList != null){
              mAdapterList.addAll(response.getListResponse());
        }
    }

    @Override
    public  List<MODE> getListResponse(){
        if(list != null){
            return list;
        }
        return Collections.emptyList();
    }
}