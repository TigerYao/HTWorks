package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.lessons.bean.AllCourseData;

import java.util.Collections;
import java.util.List;

/**
 * Created by saiyuan on 2018/11/29.
 */

public class AllCourseListResponse extends BaseListResponse <AllCourseData>{
    @SerializedName("data")
    public List<AllCourseData> data;

//    public static class Data{
//        @SerializedName("data")
//        public List<AllCourseData> data;
//
//    }

    public List<AllCourseData> mLessionlist;

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
    public List<AllCourseData> getListResponse() {
        if(data != null){
            return data;
        }
        return Collections.emptyList();
    }


}
