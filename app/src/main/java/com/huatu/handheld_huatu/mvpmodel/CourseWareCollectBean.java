package com.huatu.handheld_huatu.mvpmodel;

import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;

import java.util.List;

/**
 * Created by cjx on 2018\11\8 0008.
 */

public class CourseWareCollectBean {

   public String id;      // 8362054,
   public int         netClassId;// 97353,
    public int         classId;// 97353,
    public int         coursewareId;// 948773,
    public String        title;// 阶段测试A,
    public int        parentId;// 0,
    public int        videoType;// 1,
    public int        type;// 2,
    public int          classHour;// 7,
    public  List<Teacher>     teacherInfo;// [],
   /*         nodeIndex;// [
            0
            ],*/
    public int        schedule;// 6,
    public String        dateDesc;// 视频 - 07分钟00秒,
    public int        alreadyStudyTime;// 21,
    public String       coursewareTimeLength;// 402,
    public int        isFinish;// 0,
    public int        isStudy;// 1,
    public String        scheduleDesc;// 观看至6%,
    public String        token;// XRXLLRLZc347H_Jmfnz5AtnblVvSzQJ69CcGLN-5817XbjKcuxoyejTSEzZrILF4,
    public String        videoId;// 16454522,
    public int        hasTeacher;// 1,
    public String        pubDate;// 2019-04-04 14:59:08,
    public boolean       isExpired;// false,
    public String        classTitle;// 录播阶段测试A

    public String   bjyRoomId        ;//String   bjyRoomIdomid	string	@mock=$order('','','','','')
    public String   bjySessionId	 ;//百家云sessionid	string	@mock=$order('','','','','')

    public String sign ;//sign string 百家云直播签名
    public String liveStartTime; //直播开始时间 秒单位
    public String liveEndTime; //直播结束时间 秒单位
    public int liveStatus;
    public String joinCode;
    public int isPlayback; //1有回放未上传，0没有回放

    public int groupId; //分组Id

    public int nextClassNodeId;

    public List<String> teacherIds;
    public List<String> teacherNames;

    public String classCover;

    public static class Teacher{

        public String teacherName;
        public String roundPhoto;
    }

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
