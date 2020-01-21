package com.huatu.handheld_huatu.mvpmodel;


import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2016/8/2.
 */
public class MyCourseListResponse extends BaseListResponse<CourseMineBean.ResultBean> {

    @SerializedName("data")
    public CourseMineBean data;
/*
    public static class Data{
        @SerializedName("result")
        public List<Lessons> dynamicsList;

        public int next;
    }*/

    public List<CourseMineBean.ResultBean> mLessionlist;

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
    public List<CourseMineBean.ResultBean> getListResponse() {
        if(data != null&&data.result!=null){
            return data.result;
        }
        return Collections.emptyList();
    }


}
