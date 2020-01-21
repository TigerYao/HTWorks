package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class CourseHandoutListResponse extends BaseListResponse<HandoutBean.Course> {

    @SerializedName("data")
    public HandoutBean.Data data;


    public List<HandoutBean.Course> mLessionlist;

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
    public List<HandoutBean.Course> getListResponse() {
        if(data != null&&data.course_jiangyi!=null){
            return data.course_jiangyi;
        }
        return Collections.emptyList();
    }


}

