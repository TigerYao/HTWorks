package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class BuyCourseListResponse extends BaseListResponse<BuyCourseBean.Data> {

    @SerializedName("data")
    public Data data;


    public List<BuyCourseBean.Data> mCourselist;

    @Override
    public void clearList() {
        if(mCourselist != null){

            mCourselist.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if(mCourselist != null){

            mCourselist.addAll(response.getListResponse());
        }
    }

    @Override
    public List<BuyCourseBean.Data> getListResponse() {
        if(data != null&&data.listObject!=null){
             return data.listObject;
        }
        return Collections.emptyList();
    }


    public  static class Data{

        public int current_page;

        @SerializedName("data")
        public List<BuyCourseBean.Data> listObject;
    }
/*
    public  static class Data{

       public int current_page;

       @SerializedName("data")
       public BuyCourseBean listObject;
    }*/
}

