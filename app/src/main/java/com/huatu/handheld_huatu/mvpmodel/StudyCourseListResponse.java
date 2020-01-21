package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cjx on 2018\7\13 0013.
 */

public class StudyCourseListResponse extends BaseListResponse<BuyCourseBean.Study> {

    @SerializedName("data")
    public Data data;


    public List<BuyCourseBean.Study> mCourselist;

    public int mCurrentType=-1;
    public int mRecentStatus=0;

    @Override
    public void clearList() {
        if(mCourselist != null){
            mCurrentType=-1;
            mCourselist.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <RESPONSE extends BaseListResponse> void processData(RESPONSE response) {
        if(mCourselist != null){
              for(BuyCourseBean.Study bean:((StudyCourseListResponse)response).getListResponse()){
                 if(bean.holdType!=mCurrentType){
                     mCurrentType=bean.holdType;
                     BuyCourseBean.Study tmpDto=new BuyCourseBean.Study();
                     tmpDto.holdType=1;
                     tmpDto.classType=mRecentStatus;//复用此字段为 0最近学习与  1最新加入
                     mCourselist.add(tmpDto);
                 }
                 mCourselist.add(bean);
             }
        }
    }

    @Override
    public List<BuyCourseBean.Study> getListResponse() {
        if(data != null&&data.listObject!=null){
            if(data != null&&data.listObject!=null){
                return data.listObject;
            }
         }
        return Collections.emptyList();
    }


    public  static class Data{

       public int current_page;

       @SerializedName("data")
       public List<BuyCourseBean.Study> listObject;
    }
}

