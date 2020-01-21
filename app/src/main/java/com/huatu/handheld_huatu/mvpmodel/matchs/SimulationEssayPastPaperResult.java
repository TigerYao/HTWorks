package com.huatu.handheld_huatu.mvpmodel.matchs;

/**
 * Created by ht on 2018/2/8.
 */
public class SimulationEssayPastPaperResult {
    public int correctNum;//已批改次数
    public int correctSum;//练习总次数
    public int limitTime;//答题限时
    public long paperId;//试卷id
    public long courseId;//新增字段。解析课id（大于0才代表有解析课，没有的话返回0）
    public String paperName;//试卷名称
    public int recentStatus;//答题状态
    public int totalCount;//试卷总题目数

//    correctNum	已批改次数	number	@mock=0
//    limitTime	答题限时	number	@mock=1800
//    paperId	试卷id	number	@mock=319
//    paperName	试卷名称	string	@mock=201801016模考测试
//    recentStatus	答题状态	number	@mock=1
//    totalCount     试卷总题目数	number	@mock=1

//    correctSum	练习总次数	number
//    courseId	新增字段。解析课id（大于0才代表有解析课）	number	@mock=$order(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)
}
