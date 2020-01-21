package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.List;

/**
 * 申论答题试卷
 */
public class PaperQuestionDetailBean implements Serializable {

    public int spendTime;
    public EssayPaperEntity essayPaper;
    public List<SingleQuestionDetailBean> essayQuestions;

    public static class EssayPaperEntity implements Serializable {

        public long answerCardId;       // 答题卡Id
        public long paperId;            // 试卷Id
        public int limitTime;           // 总答题时间
        public int remainTime;          // 剩余时间
        public int score;               // 得分
        public String paperName;        // 试卷名称
        public int correctNum;
        public int recentStatus;
        public int saveType;
        public int unfinishedCount;
        public int lastIndex;
        public int spendTime;

        public long startTime;          // 模考大赛开始时间
        public long endTime;            // 模考大赛结束时间

        public int correctMode;                         // 批改类型 1、智能 2、人工
        public int otherAnswerCardId;                   // 其他未完

    }
}
