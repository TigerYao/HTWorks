package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;
import com.huatu.handheld_huatu.business.me.bean.MyOrderData;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018\7\13 0013.
 */

public class PurchasedCourseBean implements Serializable {

    public int next;

    public int currentPage;
    public List<PurchasedCourseBean.Data> list;
    public LiveUserInfo userInfo;
    public String netClassName;//课程名称，埋点用




 /*   public String   bjyRoomId        ;//String   bjyRoomIdomid	string	@mock=$order('','','','','')
    public String   bjySessionId	;//百家云sessionid	string	@mock=$order('','','','','')
    public long     coursewareId	;//课件id	number	@mock=$order(917618,917619,917577,917566,917550)
    public String   fileSize	        ;// 课件大小	string	@mock=$order('25.03MB','190MB','197.41MB','2.41MB','1byte')
    public String   joinCode	         ;//直播课件joincode	string	@mock=$order('','','','','')
    public String   serialNumber      ;//	序号	number	@mock=$order(1,2,3,4,5)
    public String   title	         ;//课件名称	string	@mock=$order('管理、公文、公基社区知识社区工作知识。。管理、公文、公基社区知识社区工作知识。。管理、公文、公基社区知识社区工作知识。。','专业科目社会工作师考试的课件','大纲测试','wxr0719录播的课件','测试随堂联系')
    public String   token	        ;//点播、回放token	string	@mock=$order('iVNlAJlF3_Ktr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','7TRV1t9oZRGtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','Zd_Y4KyNCM2tr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','NR6rRFpmSmmtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA','xFlDMRL2OnWtr7zMVFWLSlQCYlzIRZcbBmqdq8vc2wQ00hM2ayCxeA')
    public String   videoId          ;//	百家云videoId*/

    public static class Data extends CourseWareInfo implements Serializable {
    //    public String  id	            ;//节点id	number	@mock=1356
       // public int   serialNumber	;//顺序号	number	@mock=1
       // public String  title	    ;//节点名称	string	@mock=修改测试阶段1
        public int     type	            ;//0  阶段1课程2课件	number	@mock=0
        public String     isTrial	        ;//试听课件0否1是	number	@mock=0
        public int classCardId;
       // public String    coursewareId	;//课件id	number	@mock=0

       // public String     hasChildren	    ;//是否有子节点0无1有	number	@mock=1
       // public String  token	    ;//百家云播放token	string	@mock=
       // public String  videoId	    ;//百家云viedeoId(点播)	string	@mock=
       // public String  bjyRoomId	;//百家云roomId(直播回放)	string	@mock=
        //public String  bjySessionId	;//百家云sessionId（直播回放）	string	@mock=
       // public String  fileSize     ;//	课件大小	string	@mock=
        public String  studyLength	;//学习时长	string	@mock=
        public int  classHour	    ;//学时	number	@mock=0

        public String videoLength	;//直播日期/视频时长	string	@mock=


        public int   isStudy	    ;//0未开始学习1已学习	number	@mock=0

        public int   isFinish	    ;//0未学完1已学完	number	@mock=0
        public String   studySchedule	;//已学习的课时	number	@mock=0

       // public String  studyPercent	;//学习进度百分比	string	@mock=未开始学习

        public AnswerCard answerCard;
        //public String joinCode;

        public int   alreadyStudyTime;
        public int   coursewareTimeLength;
        public int   isPlayback;   //是否自动上传回放  1：是 0：否 number

        public String stageName;
        public int    stageNodeId;


        public int studyReport         ; //是否有学习报告1是0否 number  @mock=0

        public int reportStatus        ;//  学习报告状态，默认为-1，0未生成，1已生成

        //  public int classExercisesNum   ;//随堂练习题数量 number  @mock=0

        public int  testStatus       ;//阶段测试状态默认为-1，为-1，你们不处理，其他的 阶段测试状态 1未开始 2开始考试 5继续考试  6查看报告

        public int   isEffective     ;//阶段测试时间是否有效0否1是

        public int   isExpired;      // 是否过期
        public List<Data> childs;

        private boolean isClosed;
        public void setClosed(boolean isclosed){
             isClosed=isclosed;
        }

        public boolean isClosed(){
            return isClosed;
        }


     }

     public static class AnswerCard implements Serializable{
         public long id;
         public int wcount;   //错误数量
         public int rcount;

        // @SerializedName(value ="ucount",alternate = {"fcount"})

         public int ucount;    //未做数量
         public int fcount;   // 申论多题已完成
         public int status;   //1：创建，2：未做完，3：已结束

         public double examScore;
         public double score;
     }


/*   申论的status


 UNFINISHED(1),        //, "未完成"
    COMMIT(2),           //, "已交卷"
    CORRECT(3),          //, "已批改"
    INIT(0),             //, "空白"
    CORRECTING(4),       //,"批改中"
    CORRECT_RETURN(5);   //,"被退回"*/
     //{"type":2,"status":0,"wcount":0,"ucount":0,"rcount":0,"qcount":1,"id":0,"correctNum":0,"examScore":0.0,"score":0.0,"similarId":0,"questionType":0}
}
