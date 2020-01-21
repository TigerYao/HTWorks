package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class CoursewareDownResponse extends BaseListResponse<CourseWareInfo> {

    @SerializedName("data")
    public CourseWareBean data;


    public List<CourseWareBean> mCourselist;

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
    public List<CourseWareInfo> getListResponse() {
        if(data != null&&data.list!=null){
              return data.list;
        }
        return Collections.emptyList();
    }

}

