package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.util.List;

/**
 *
 *  audioId	名师之声音频ID	number
 *     audioToken	名师之声百家云token	string
 *     avgScore	班级平均分	number
 *     avgSpendTime	班级平均耗时	number
 *     correctDate	批改日期	string
 *     correctMode	批改模式（1智能批改2人工批改3智能转人工）	number
 *     correctNum		number
 *     examScore	学员得分	number
 *     feedBackContent	学员评价内容	string
 *     feedBackStar	学员评价星级	number
 *     feedBackStatus	是否评价0未评价 1已经评价	number
 *     maxScore	班级最高分	number
 *     paperId	试卷ID	number
 *     questionCount	试题数量	number
 *     questionVOList	答题情况	array<object>
 *         examScore	每个试题得分	number
 *         inputWordNum	输入字数	number
 *         questionBaseId	题目id	number
 *         score	题目满分	number
 *         sort	题目序号	number
 *         spendTime	题目类型名称	number
 *         type	题目类型	number
 *         typeName	题目类型名称	string
 *     reCorrectStatus	是否已经二次批改（0 尚未再次作答1二次作答）	number
 *     remarkList	综合评价	array<object>
 *         content	评语内容	string
 *         sort	评语排序	number
 *     score	试卷总分	number
 *     spendTime	总耗时	number
 *     submitTime	交卷时间	string
 *     totalRank	我的排名	number
 *     unfinishedCount	未完成试题数目	number
 *     userScoreRankList	优秀成绩排名信息	array<object>
 *         examScore	得分	number
 *         rank	排名	number
 *         spendTime	用时	number
 *         submitTime	提交时间	string
 *         userName	学员用户名	string
 *
 */
public class ExerciseReportBean {
    public int avgSpendTime;
    public List<UserScoreRank> userScoreRankList;
    public float maxScore;
    public long questionBaseId;
    public long audioId;
    public long otherAnswerCardId;
    public int otherAnswerBizStatus;//0未开始(1, "未完成")(3,已批改）2，4,"批改中"),5被退回
    public String audioToken;
    public int questionDetailId;
    public int feedBackStatus;
    public int correctMode;
    public String submitTime;
    public int feedBackStar;
    public int correctNum;
    public String feedBackContent;
    public List<RemarkList> remarkList;
    public float avgScore;
    public int reCorrectStatus;
    public int totalRank;
    public String correctDate;
    public float examScore;
    public float score;
    public List<QuestionVO> questionVOList;
    public int questionCount;
    public int unfinishedCount;
    public int spendTime;
    public long paperId;


    public class UserScoreRank {
        public int spendTime;
        public int rank;
        public float examScore;
        public String submitTime;
        public String userName;
        public String avatar;
    }

    public class RemarkList {
        public int labelId;
        public int sort;
        public int commentId;
        public int templateId;
        public String content;
        public int labelType;
    }

    public class QuestionVO {
        public double examScore;
        public int inputWordNum;
        public double score;
        public int sort;
        public int spendTime;
        public int type;
        public String typeName;
    }
}



