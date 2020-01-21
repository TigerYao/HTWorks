package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cjx on 2018\7\30 0030.
 */

public class CourseWareInfo implements Serializable {


    public String   id	            ;//节点id	number	@mock=1356
    public String   bjyRoomId        ;//String   bjyRoomIdomid	string	@mock=$order('','','','','')
    public String   bjySessionId	 ;//百家云sessionid	string	@mock=$order('','','','','')
    public int      coursewareId	 ;//课件id	number	@mock=$order(917618,917619,917577,917566,917550)
    public String      fileSize	     ;// 课件大小	string	@mock=$order('25.03MB','190MB','197.41MB','2.41MB','1byte')
    public   String   joinCode	     ;//直播课件joincode	string	@mock=$order('','','','','')
    public      int      serialNumber     ;//	序号	number	@mock=$order(1,2,3,4,5)
    public String   title	         ;//课件名称	string	@mock=$order('管理、公文、公基社区知识社区工作知识。。管理、公文、公基社区知识社区工作知识。。管理、公文、公基社区知识社区工作知识。。','专业科目社会工作师考试的课件','大纲测试','wxr0719录播的课件','测试随堂联系')
    public String   token	         ;//点播、回放token	string	@mock=$order('iVNlAJlF3_Ktr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','7TRV1t9oZRGtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','Zd_Y4KyNCM2tr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','NR6rRFpmSmmtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','xFlDMRL2OnWtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA')
    public String   videoId          ;//	百家云videoId

    public int      downStatus  ;// 0 未下载，1下载中，2已完成
    public String   targetPath;
    public String   offSignalFilePath;

    public String   teacher	    ;//主讲老师	string	@mock=
    public int      hasTeacher;     //0无老师1有老师

    public int      videoType	   ;//1点播2直播3直播回放 4为阶段测试 ,5 音频课程	number	@mock=0  添加类型6为离线展示

    public int  afterCoreseNum  ;//	课后练习题数量	number	@mock=0

    public int classId;

    public int  liveStatus	    ;//直播状态0未开始1进行中2已结束	number	@mock=0
    public int liveStart;
    public int tinyLive;
    public int lastStudy;	//是否是最后一次学习的课件0否1是

    public String parentId;//父节点id	number	@mock=0

    public int inPage;
    public String sign ;//sign string 百家云直播签名
    public String liveStartTime; //直播开始时间 秒单位
    public String liveEndTime; //直播结束时间 秒单位

    public int groupId; //分组Id

    public int nextClassNodeId;

    public List<String> teacherIds;
    public List<String> teacherNames;

    public int subjectType;     // 课后练习科目类型 默认-1无类型 1行测2申论

    public int buildType;    //  课后练习试题类型 默认-1无类型 0单题  1  套题

    public String getDownloadId(){
        if(videoType==1||videoType==5)
            return UserInfoUtil.userId+this.videoId;
        else
            return  UserInfoUtil.userId+String.valueOf(this.bjyRoomId) + this.bjySessionId;
    }

    public int getSerialNumber(){
        return serialNumber;
    }

    public void setSerialNumber(int number){
        serialNumber=number;
    }

    public long getfileSize(){
       return StringUtils.parseLong(fileSize);
    }
}
