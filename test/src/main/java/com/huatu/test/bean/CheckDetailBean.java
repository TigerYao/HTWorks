package com.huatu.test.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 批改详情数据结构
 */

public class CheckDetailBean implements Serializable {


    /**
     * addScoreList : [{"score":1,"scorePoint":"丰富","sequenceNumber":2}]
     * answerRequire : 答题要求
     * examScore : 0
     * inputWordNum : 27
     * inputWordNumMax : 300
     * inputWordNumMin : 200
     * limitTime : 30
     * questionBaseId : 8
     * questionDetailId : 101
     * score : 30
     * sort : 0
     * spendTime : 11
     * stem : 题干信息
     * subScoreList : {"score":40301,"scorePoint":"测试内容c6c1","sequenceNumber":10813}
     */

    public String answerRequire;                    // 答题要求。
    public double examScore;                        // 学员得分。
    public int bizStatus;                           // 答题状态，答题状态(0、空白，未开始 1、未完成 2、已交卷 3、已批改)。
    public int inputWordNum;                        // 学员输入字数。
    public int inputWordNumMax;                     // 最大录入字数。
    public int inputWordNumMin;                     // 最小录入字数。
    public int limitTime;                           // 答题限时。
    public long questionBaseId;                     // 题目baseId。
    public long questionDetailId;                   // 题目detailId。
    public double score;                            // 套题总分数。

    public int totalSpendTime;                      // 试卷总用时
    public double totalExamScore;                   // 试卷总得分

    public int sort;                                // 排序
    public int spendTime;                           // 学员得分用时
    public String stem;
    public List<ScoreListEntity> addScoreList;      // 加分项
    public List<ScoreListEntity> subScoreList;      // 减分项

    public String answerComment;                    // 参考答案、标准答案。

    public String topic;                            // 标题
    public String subTopic;                         // 子标题
    public String callName;                         // 称呼

    public String inscribedDate;                    // 落款日期
    public String inscribedName;                    // 落款名称

   // public List<AnswerComment> answerList;          // 答案

    public String difficultGrade;


    public String correctRule;                      // 阅卷规则
    public String analyzeQuestion;                  // 试题分析。
    public String authorityReviews;                 // 答案点评
    public String correctedContent;                 // 批改后的用户答案展示信息,文章写作是没有批改的用户答案
    public int type;                                // 1：概括归纳 2：综合分析 3：解决问题 4：应用文 5：文章写作     大题和小题分两种匹配答案解释模式。应用文是大题，文章写作不匹配，其他都是小题。

    public int correctType;                         // 1、小题批改（词批改） 2、大题批改（句子批改)）

    public long videoId;                            // 视频解析id
    public String token;                            // 视频解析token

    public static class ScoreListEntity implements Serializable {
        /**
         * score : 40301
         * scorePoint : 测试内容c6c1
         * sequenceNumber : 10813
         */

        public double score;                        // 得分
        public String scorePoint;                   // 加、减分点
        public int sequenceNumber;                  // 序号
        public int type;                            // 1、内容得分 2、格式得分 3、减分 4、其他得分

        @Override
        public String toString() {
            return "ScoreListEntity{" +
                    "score=" + score +
                    ", scorePoint='" + scorePoint + '\'' +
                    ", sequenceNumber=" + sequenceNumber +
                    ", type=" + type +
                    '}';
        }
    }
}
