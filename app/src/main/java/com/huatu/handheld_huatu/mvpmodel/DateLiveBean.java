package com.huatu.handheld_huatu.mvpmodel;

import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;
import com.huatu.handheld_huatu.ui.StatusbgTextView;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.List;

/**
 * Created by Administrator on 2018\12\6 0006.
 */

public class DateLiveBean {

     public int         classId;//: 65917,
     public int         lessonId;//: 26771,
     public String      title;//: 测试直播教室1206李锐,
     public String      shortTitle;
     public int         videoType;//: 2,
     public int         id;//: 4227767,
     public int         netClassId;//: 65917,
     public String      teacherName;//: 刘有珍,
     public String      openTime;//: 2018-12-06 08:00:00,
     public String      offTime;//: 2018-12-06 23:00:00,
     public String      beginTime;//: 08:30,
     public String      endTime;//: 22:45,
     public String      price;//: 0,
     public int         status;//: 1,
     public String       actionDesc;//: 进入教室


     public String      bjyRoomId;
     public String      bjySessionId;
     public String      token;
     public String      joinCode;
     public String      userNumber;
     public String      userNick;
     public String      userAvatar;
     public String      sign;


     public String      parentId;

     public int         tinyLive;
     public int         countdown;
     public int         groupId;
     public String liveStartTime;
     public String liveEndTime; //直播结束时间 秒单位
    // public int         holdType;
     public List<String> teacherIds;
     public List<String> teacherNames;

     public  CourseWareInfo buildCourseWare(CourseWareInfo mCourseWareInfo){
          DateLiveBean liveBean=this;
          if(liveBean.status== StatusbgTextView.PLAYBACK){
               mCourseWareInfo.videoType=3;
          }else {
               mCourseWareInfo.videoType=2;
               mCourseWareInfo.liveStatus=1;
          }
          mCourseWareInfo.token=liveBean.token;
          mCourseWareInfo.coursewareId=liveBean.lessonId;
          mCourseWareInfo.title=liveBean.title;
          mCourseWareInfo.id=String.valueOf(liveBean.id);
          mCourseWareInfo.bjyRoomId=liveBean.bjyRoomId;
          mCourseWareInfo.bjySessionId=liveBean.bjySessionId;
          mCourseWareInfo.joinCode=liveBean.joinCode;
          mCourseWareInfo.sign=liveBean.sign;
          mCourseWareInfo.tinyLive=liveBean.tinyLive;
          mCourseWareInfo.parentId=liveBean.parentId;
          mCourseWareInfo.classId=liveBean.classId;
          mCourseWareInfo.liveStartTime = liveBean.liveStartTime;
          mCourseWareInfo.liveEndTime = liveBean.liveEndTime;
          mCourseWareInfo.teacher = liveBean.teacherName;
          mCourseWareInfo.groupId=liveBean.groupId;
          mCourseWareInfo.teacherNames = liveBean.teacherNames;
          mCourseWareInfo.teacherIds = liveBean.teacherIds;

          if(UserInfoUtil.getLiveUserInfo()==null){
               LiveUserInfo liveUserInfo = new LiveUserInfo();

                liveUserInfo.userAvatar=liveBean.userAvatar;
                liveUserInfo.userNick=liveBean.userNick;
                liveUserInfo.userNumber=liveBean.userNumber;
               UserInfoUtil.setLiveUserInfo(liveUserInfo);
          }



          return mCourseWareInfo;
     }
}
