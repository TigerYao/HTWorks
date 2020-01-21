package com.huatu.handheld_huatu.business.play.bean;

import java.util.List;

public class HistoryCourseInfo{
    public String classId;
    public String classTitle;
    public String lessonId;
    public String lessonTitle;
    public List<HistoryCourseInfo> childDatas;
    /** 是否展开,  默认展开*/
    private boolean isExpand = false;
    public boolean mIsParent = true;


    /** 返回是否是父节点*/
    public boolean isParent() {
        return mIsParent;
    }

    public boolean isExpand(){
        return isExpand;
    }

    public void onExpand() {
        isExpand = !isExpand;
    }

    public boolean hasChilds(){
        if(getChildDatas() == null || getChildDatas().isEmpty() ){
            return false;
        }
        return true;
    }

    public List<HistoryCourseInfo> getChildDatas() {
        return childDatas;
    }

    public void setChildDatas(List<HistoryCourseInfo> childDatas) {
        this.childDatas = childDatas;
    }

}
