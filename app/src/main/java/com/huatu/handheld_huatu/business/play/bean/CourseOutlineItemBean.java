package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;
import java.util.List;

public class CourseOutlineItemBean implements Serializable {
    public int classHour;       //课时
    public int coursewareId;    //课件id
    public int hasChildren;     //是否有子节点0否1是
    public int id;              //大纲id
    public int isTrial;         //是否试听0否1是
    public int serialNumber;    //顺序号
    public String studyLength;  //学习时长
    public String teacher;      //主讲老师
    public String title;        //节点名称
    public int type;            //节点类型  0阶段  1课程  2课件
    public String videoLength;  //视频时长or直播日期
    public int videoType;       //课件类型 1点播  2直播  3直播回放 4 阶段测试 5 音频课件
    public int afterCoreseNum;  //课后作业数量
    public String bjyRoomId;    //百家云roomId(直播回放)
    public String bjySessionId; //百家云sessionId(直播回放)
    public int classId;         //课程id（课件所属课程id）
    public String fileSize;     //课件大小
    public int isFinish;        //0未学完1已学完
    public int isStudy;         //0未学习1已学习
    public String joinCode;     //百家云直播code
    public int liveStatus;      //直播状态0直播未开始1直播中2直播已结束
    public int parentId;        //父id
    public int studySchedule;    //已学习课时
    public String studyPercent;  //学习进度%
    public String token;         //百家云播放token
    public String videoId;       //百家云viedeoId(录播)
    public AnswerCardBean answerCard;  //答题卡信息
    public int alreadyStudyTime; //课件已学习的时间（秒）
    public int tinyLive;   //直播小班课 0否 1是
    public boolean isExpand;      //自定义添加是否打开项目
    public int isPlayback;    //是否自动上传回放1：是 0：
    public List<CourseOutlineItemBean> childList;
    public String classCover;//课程封面
    public List<String> teacherIds;
    public List<String> teacherNames;

    public int subjectType;// 课后练习科目类型 默认-1无类型 1行测2申论 number  @mock=-1
//    public int questionType ;//   课后练习试题类型 默认-1无类型 0套题1单题 number  @mock=-1
    public long questBaseId;
    public long paperId;
    public int isFromArgue;

    public boolean hasChilds(){
        if (childList != null && !childList.isEmpty())
            return true;
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CourseOutlineItemBean) {
            CourseOutlineItemBean bean = (CourseOutlineItemBean) obj;
            return (classId == bean.classId && coursewareId == bean.coursewareId && parentId == bean.parentId && id == id);
        }
        return super.equals(obj);
    }

}
