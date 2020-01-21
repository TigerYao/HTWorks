package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class CollectCourseWareListResponse extends BaseListResponse<CourseWareCollectBean> {

    @SerializedName("data")
    public Data data;

    public List<CourseWareCollectBean> mLessionlist;

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
    public List<CourseWareCollectBean> getListResponse() {
        if(data != null&&data.list!=null){
            UserInfoUtil.setLiveUserInfo(data.userInfo);
            return data.list;
        }
        return Collections.emptyList();
    }

    public  static class Data{


        @SerializedName("data")
        public List<CourseWareCollectBean> list;

        public LiveUserInfo userInfo;
    }
}

