package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class WarpListResponse<MODE> extends BaseListResponse<MODE>{

    public List<MODE> mAdapterList;

    public Data<MODE> data;

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
        if(data != null&&data.list!=null){
            return data.list;
        }
        return Collections.emptyList();
    }

    public static class Data<MODE>{
        @SerializedName(value ="data",alternate = {"list"})
        public List<MODE> list;

        public int total;
    }
}