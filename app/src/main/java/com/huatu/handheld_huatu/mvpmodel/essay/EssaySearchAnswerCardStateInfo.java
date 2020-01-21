package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;

public class EssaySearchAnswerCardStateInfo extends CheckCountInfo implements Serializable {

    public int lastType;                // 最后一次批改类型 1、智能 2、人工

    public long answerCardId;           // 答题卡Id

    public int correctMode;             // 批改类型 1、智能 2、人工

    public int recentStatus;            // 智能最后状态
    public int manualRecentStatus;      // 人工批改最后状态

    public long otherAnswerCardId;      // 另一张答题卡Id

}
