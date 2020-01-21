package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class PurchaseCourseListResponse extends BaseListResponse<PurchasedCourseBean.Data> {

    @SerializedName("data")
    public PurchasedCourseBean data;

    public List<PurchasedCourseBean.Data> mLessionlist;

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
    public List<PurchasedCourseBean.Data> getListResponse() {
        if(data != null&&data.list!=null){
            UserInfoUtil.setLiveUserInfo(data.userInfo);
            return data.list;
        }
        return Collections.emptyList();
    }


}

