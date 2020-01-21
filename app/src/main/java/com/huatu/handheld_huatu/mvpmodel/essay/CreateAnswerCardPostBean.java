package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;


public class CreateAnswerCardPostBean implements Serializable {

    public int correctMode;         // 批改方式 1、智能 2、人工
    public long paperBaseId;
    public long questionBaseId;
    public int type;                // 课后题：0、单题 1、套题 2、文章写作 其他：0、单题 1、套题
    public int terminal;            // 设备类型 android 1

    // 以下是申论课后作业创建答题卡需要的字段
    public int courseType;          // 1、录播 2、直播 3、直播回放
    public long courseId;           // 课程Id
    public long courseWareId;       // 课件Id
    public long syllabusId;         // 大纲Id

}
