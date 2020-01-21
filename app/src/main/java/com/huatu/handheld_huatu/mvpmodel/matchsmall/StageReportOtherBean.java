package com.huatu.handheld_huatu.mvpmodel.matchsmall;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StageReportOtherBean implements Serializable {

    public ArrayList<Attention> focusList;                      // 需要重点关注列表
    public ArrayList<PointInfo> pointInfo;                      // 选知识点（目前先不做）
    public ScoreTop scoreTop;                                   // 成绩排行（前十名没有自己的时候为11条数据）
    public ArrayList<SocreDistribution> socreDistribution;      // 成绩分布统计信息

    public List<ArenaExamQuestionBean> questionListOverTime;    // 做题超过50s的问题列表
    public List<ArenaExamQuestionBean> questionListHard;        // 难度系数大于0.5的
    public List<ArenaExamQuestionBean> questionListNoDone;      // 没有做的题列表

    public static class Attention implements Serializable {
        public ArrayList<Question> questions;   // 该类型下的试题下标字符串
        public String typeText;                     // 类型值
        public String typeValue;                    // 类型（当该类型下没有值的时候根据此字段判断对应文案）
    }

    public static class Question implements Serializable {
        public ArrayList<Integer> corrects;
        public ArrayList<Integer> questionIds;
        public ArrayList<Integer> questionIndexs;   // 对应类型题的index
    }

    public static class PointInfo implements Serializable {
        public int errorPass50;                     // 错误率超过50的知识点个数
        public ArrayList<Point> pointList;          // 知识点集合
    }

    public static class Point implements Serializable {
        public String errorRate;                    // 错误率
        public String name;                         // 三级知识点名称
        public String teacherName;                  // 教师姓名
        public String yunVideoId;                   // 视频id
        public String yuntoken;                     // 百家云token
    }

    public static class ScoreTop implements Serializable {
        public ArrayList<RankInfo> comprehensiveRank;   // 前十名
        public RankInfo self;                           // 自己
    }

    public static class RankInfo implements Serializable {
        public int expendTime;                      // 答题用时（单位为秒）
        public String icon;                         // 用户头像
        public int rank;                            // 排名
        public int score;                           // 成绩
        public long submitTime;                     // 交卷时间
        public String userName;                     // 用户昵称
        public long userId;                         // 用户id
    }

    public static class SocreDistribution implements Serializable {
        public String beatRatio;                    // 击败比例（只有为自己的时候才有击败比例）
        public int count;                           // 得分人数
        public boolean isSelf;                      // 是否是自己得分
        public int score;                           // 分数
    }
}
