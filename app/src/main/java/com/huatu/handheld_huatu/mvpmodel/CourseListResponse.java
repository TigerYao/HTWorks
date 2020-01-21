package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;

import java.util.Collections;
import java.util.List;

/**
 * Created by chq on 2018/12/5.
 */

public class CourseListResponse extends BaseListResponse<CourseListData> {
    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("data")
        public List<CourseListData> dynamicsList;
        public int totalPage;
    }

    public List<CourseListData> mCourseList;

    @Override
    public void clearList() {
        if (mCourseList != null) {
            mCourseList.clear();
        }
    }

    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if (mCourseList != null) {
            mCourseList.addAll(response.getListResponse());
        }
    }

    @Override
    public List<CourseListData> getListResponse() {
        if (data != null && data.dynamicsList != null) {
            return data.dynamicsList;
        }
        return Collections.emptyList();

    }
}
