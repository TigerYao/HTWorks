package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.utils.ArrayUtils;

import java.util.List;

/**
 * Created by yaohu on 2018/8/25.
 */

public class LastCourseBean {
    public int isFirstStudy = -1; //是否是第一次学习0否1是
    public List<CourseWareInfo> list;

    public List<CourseWareInfo> lastStudy;

    public LiveUserInfo userInfo;

    public CourseWareInfo getLastCouseInfo(){

        if(ArrayUtils.isEmpty(lastStudy)){
            if (!ArrayUtils.isEmpty(list)) {
                for (CourseWareInfo info : list) {
                    if (info.lastStudy == 1)
                        return info;
                }
                return list.get(0);
             }
            return null;
        }
        return lastStudy.get(0);
    }

    public CourseWareInfo getOnLiveCourseInfo(){
        if(!ArrayUtils.isEmpty(lastStudy)){
              //有直播的课
                if (!ArrayUtils.isEmpty(list)) {
                    for (CourseWareInfo info : list) {
                        if (info.videoType == 2&&(info.liveStatus == 0||info.liveStatus == 1))
                            return info;
                    }
                    return null;
                }
                return null;
        }
        return null;
    }
}
