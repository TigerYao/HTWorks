package com.huatu.handheld_huatu.business.ztk_vod;

import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;

import java.util.List;

/**
 * Created by cjx on 2018\8\16 0016.
 */

public interface OnCoursePlaylistener {

   void onSelectPlayClick(CourseWareInfo lessionInfo, boolean isRefreshDialog);

    List<CourseWareInfo> getRecordList();
}
