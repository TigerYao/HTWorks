package com.huatu.handheld_huatu.business.essay.bean;

/**
 * Created by ht on 2017/11/29.
 */
public class MyCheckResult {
    public long answerId;                   // 答题卡id
    public long areaId;                     // 地区ID
    public String areaName;                 // 地区名称
    public String correctDate;              // 批改时间
    public float examScore;                 // 学员得分
    public long paperId;                    // 试卷id
    public long questionBaseId;             // 试题id
    public String paperName;                // 试卷名称
    public float score;                     // 总分
    public String stem;                     // 题干信息
    public long questionDetailId;
    public int bizStatus;                   // 批改状态，(0 空白，未开始 1未完成 2 待批改 3已批改4 批改中 5被驳回）

    public String correctMemo;              // 被退回的描述
    public int correctMode;                 // 批改模式 1 智能批改，2人工批改 ,3智能转人工
    public boolean videoAnalyzeFlag;        // 是否有视频解析
    public boolean paperReportFlag;         // 是否有报告

    public long similarId;                  // 单题组id

    public int questionType;                // 单题类型（检查批改次数使用）

    public int paperType;                   // 0、模考卷 1、真题卷  （模考不能人工批改）

    public String clickContent;             //人工批改的大致批改时间
}
//    data		object
//    next	是否有下一页数据	number	@mock=0
//        result		array<object>
//answerId	答题卡id	number	@mock=$order(2,3)
//        areaId	地区ID（type=0时，有该字段）	number	@mock=$order(0,0)
//        areaName	地区名称（type=0时，有该字段）	number
//        correctDate	批改时间	string	2017-11-27 13:58:33
//        examScore	学员得分	number	@mock=$order(71,0)
//        paperId	试卷id（type=1时，有该字段）	number	@mock=$order(3,3)
//        paperName	试卷名称（type=1时，有该字段）	string	@mock=$order('2017年422联考','2017年422联考')
//        questionBaseId	试题id（type=0时，有该字段）	number	@mock=$order(0,0)
//        score	总分	number	@mock=$order(150,150)
//        stem	题干信息（type=0时，有该字段）