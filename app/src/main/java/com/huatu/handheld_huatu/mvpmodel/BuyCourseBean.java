package com.huatu.handheld_huatu.mvpmodel;

import com.huatu.handheld_huatu.ui.recyclerview.LetterSortAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018\7\17 0017.
 */

public class BuyCourseBean {

    public List<BuyCourseBean.Data> top;
    public List<BuyCourseBean.Data> live;
    public List<BuyCourseBean.Data> last;

    public static class Data extends LetterSortAdapter.LetterSortBean {
        public String   classId	;//课程ID	string	@mock=$order('52609','65860','65518','65849','65301','64596','64596')
        public int   classType	;//课程类型,1-直播, 0-录播	string	@mock=$order('0','1','1','1','1','0','0')
        public long     completeTime	;//已观看时间	number	@mock=$order(200,,,,,,200)
        public String   cover	;//封面	string	@mock=$order('http://upload.htexam.net/classimg/class/1520489302.jpg','http://p.htwx.net/images/course_default.jpg','http://upload.htexam.net/classimg/class/1522924598.jpg','http://p.htwx.net/images/course_default.jpg','http://upload.htexam.net/classimg/class/1522581838.jpg','http://upload.htexam.net/classimg/class/1524639150.png','http://upload.htexam.net/classimg/class/1524639150.png')
        public boolean  isDone	;//是否完成	boolean	@mock=$order(false,false,false,false,false,false,false)
        public boolean  isRecord	;//是否有记录	boolean	@mock=$order(true,false,false,false,false,false,false)
        public boolean  isStart	;//课程是否开始	boolean	@mock=$order(true,true,true,true,true,false,false)
        public boolean  isTop	;//是否置顶	boolean	@mock=$order(false,false,false,false,false,false,false)
        public String   lastTime	;//最近学习时间	string	@mock=$order('2018-06-29 17:35:44','undefined','undefined','undefined','undefined','undefined','undefined')
        public long     lessonId	;//课件ID	number	@mock=$order(818098,,,,,,818098)
        public boolean  liveEnd	;//是否结课	boolean	@mock=$order(false,true,true,true,true,false,false)
        public boolean  living	;//是否直播中	boolean	@mock=$order(false,false,false,false,false,false,false)

        public int      todayLive; //今日直播
        public long     order	     ;//第几节	number	@mock=$order(2,1,1,1,1,,2)
        public String   orderId	 ;//订单ID	string	@mock=$order('11586017','11445652','11445654','11445656','11445671','11445672','11445673')
        public String   orderNum;

        public long     rid	     ;//课程ID	number	@mock=$order(52609,65860,65518,65849,65301,64596,64596)
        public float    schedule	     ;//进度(整数)	number	@mock=$order(14,0,0,0,0,,14)
        public String   title	 ;//课程标题	string	@mock=$order('教师资格证导师面试理论课—小学语文试讲+答辩理论课','批改次数测试','2018职业能力测试系统提分班003班（4.16-5.18）','测试直播课','2018山东潍坊事业单位考试系统提分班02班（4.2-5.3）','【2018年公考面试】 河北省结构化面试9小时1对1','【2018年公考面试】 河北省结构化面试9小时1对1')
        public int      totalSchedule	;//总进度	number	@mock=$order(1,,,,,,1)
        public long     totalTime;
        public String   descString;
        public String   protocolUrl;
        public int      oneToOne;
        public int      holdType;
        public  boolean isExpired;    //是否过期, true已过期, false未过期
        public List<Teacher> teacherImg;

        public int NetClassCategoryId;//值为 12，19，21 的时候用教师资格的一对一信息表

        public int newTeacherOneToOne;// 0:旧的一对一 1:新一对一

    }

    public static class Study extends Data{



    }

    public static class Teacher{
         public String roundPhoto;
         public String teacherName;

    }

}
