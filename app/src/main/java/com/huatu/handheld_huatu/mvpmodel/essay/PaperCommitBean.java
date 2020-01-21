package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.List;


public class PaperCommitBean implements Serializable {

    public long answerCardId;                       // 答题卡Id
    public List<PaperAnsContentBean> answerList;    // 问题
    public int lastIndex;                           // 最后一题
    public long paperBaseId;                        // 试卷的Id，单题为0
    public int saveType;                            // 0、保存 1、提交
    public int type;                                // 0、单题 1、套题
    public int terminal;
    public int spendTime;                           // 总时间
    public int unfinishedCount;                     // 未完成数量

    public int correctMode;                         // 批改类型 1、智能 2、人工 3、智能转人工
    public int delayStatus;                         // 是否顺延 0、不顺延 1、顺延 就是老师是否饱和的试卷

    public static class PaperAnsContentBean implements Serializable {

        public String content;          // 答案内容
        public int inputWordNum;        // 字数
        public long questionBaseId;     // 问题Id
        public long questionDetailId;   // 问题Id
        public long answerId;           // 单问题的answerCardId
        public int spendTime;           // 单题花费时间
        public String fileName;

    }
}
