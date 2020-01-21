package com.huatu.handheld_huatu.mvpmodel.matchs;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * 新的行测模考大赛列表数据，使用的时候，对应成旧的结构，和申论一样，统一使用
 */
public class SimulationScDetailBeanNew implements Serializable {

    public int next;
    public int total;
    public int size;
    public int page;
    public ArrayList<ScInfo> list;

    public static class ScInfo implements Serializable {
        /**
         * courseId : 62142
         * courseInfo : 深度解析：考试当天21:00准时开讲！
         * endTime : 1514367000000
         * enrollCount : 0
         * essayEndTime : 1514381400000
         * essayPaperId : 1000
         * essayStartTime : 1514380800000
         * instruction : 考试说明:
         * 1. 开考前5分钟可提前进入考场查看题目，开考30分钟后则无法报名和进入考试 。
         * 2. 开始答题后不可暂停计时，如需完全退出可直接提交试卷;考试结束自动交卷。
         * 3. 分享“报名成功”截图至微博并@华图网校即有机会免费获得本期模考直播解析课。
         * name : test007
         * paperId : 3526552
         * stage : 1
         * startTime : 1514366400000
         * status : 5
         * subject : 1
         * tag : 1
         * timeInfo : 考试时间：2017年12月27日（周三）17:20-17:30
         * userMeta : 1
         */

        public CourseInfo courseInfo;

        public long endTime;
        public int enrollCount;
        public int enrollFlag;
        public long essayEndTime;
        public int essayPaperId;
        public long essayStartTime;
        public int flag;                                            // 0、所有报告都没有 1、只有行测报告 2、只有申论报告 3、行测申论报告都有
        public String instruction;
        public String name;
        public int matchId;
        public int stage;
        public long startTime;
        public String iconUrl;                                      // 礼包图片地址

        public ArrayList<SimulationScDetailBean.Area> areaList;     // 地区信息

        public int enterLimitTime;                                  // 行测可体检进入考场时间（单位：分） 120 -- 考试开始前120分钟之内可以进入考场
        /*
            status
            1：未报名
            2：已报名
            3：开始考试-置灰-不可用
            4：开始考试
            5：无法考试
            6：可查看报告
            7：未出报告
            8：未交卷，可以继续做题
            9：未报名且错过报名

            status = 1 未报名
            status = 2 stage = 1 已报名
            status = 3 stage = 1 开始行测考试 (置灰)        还不能进入
            status = 4 stage = 1 开始行测考试（置红）        可以进入考试
            status = 5 stage = 1 开始行测考试（置灰）        时间错过
            status = 6 stage = 1 查看行测报告
            status = 7 stage = 1 查看行测报告（置灰）
            status = 8 stage = 1 继续考试（行测）

            status = 3 stage = 2 开始申论考试(置灰)         还未开始
            status = 4 stage = 2 开始申论考试（置红）       进入考试
            status = 5 stage = 2 开始申论考试（置灰）       时间错过
            status = 6 stage = 2 查看申论报告
            status = 7 stage = 2 查看申论报告（置灰）
            status = 8 stage = 2 继续考试（申论）

            status = 9 停止报名
         */
        public int status;
        public int subject;
        /*
        1:'2018国考行测'
        2:'2018省考行测'
        3:,'2018省考申论'
         */
        public int tag;
        public String timeInfo;
        public UserMetaEntity userMeta;

        public static class CourseInfo implements Serializable {
            public int classId;
            public String courseTitle;
            public String liveDate;
        }

        public static class UserMetaEntity implements Serializable {
            public String id;
            public int userId;
            public int positionId;
            public String positionName;
            public int matchId;
            public int positionCount;
            public long practiceId;
            public int schoolId;
            public String schoolName;
        }
    }
}
