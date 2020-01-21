package com.huatu.handheld_huatu.business.essay.bean;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;

import java.util.Collections;
import java.util.List;

/**
 * Created by saiyuan on 2018/12/15.
 */

public class EssaySearchListResponse extends BaseListResponse<EssaySearchContent> {
    @SerializedName("data")
    public List<Data> data;

    public static class Data{
        @SerializedName("data")
        public Page data;
    }
    public static class Page{
        @SerializedName("content")
        public List<EssaySearchContent> dynamicsList;
    }
    public List<EssaySearchContent> mCourseList;
    @Override
    public void clearList() {
        if(mCourseList != null){
            mCourseList.clear();
        }
    }

    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if(mCourseList != null){
            mCourseList.addAll(response.getListResponse());
        }
    }

    @Override
    public List<EssaySearchContent> getListResponse() {
        if(data != null&& data.get(0).data.dynamicsList!=null){
            return data.get(0).data.dynamicsList;
        }
        return Collections.emptyList();

    }
}
