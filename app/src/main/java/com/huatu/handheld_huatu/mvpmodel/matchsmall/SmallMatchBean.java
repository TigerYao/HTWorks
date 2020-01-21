package com.huatu.handheld_huatu.mvpmodel.matchsmall;

import java.io.Serializable;

public class SmallMatchBean implements Serializable {
    /**
     * "courseId": 1234,
     * "courseIds": "1234,123,12（ 相关接口文档： http://rap.ztk.com/workspace/myWorkspace.do?projectId=9#2827）",
     * "courseInfo": "解析课程说明信息",
     * "courseName": "解析课名称",
     * "description": "考试说明什么的,没有~",
     * "endTime": 1550592000000,
     * "idStr": "-1",
     * "joinCount": 5,
     * "limitTime": 7200,
     * "name": "教育知识与能力",
     * "paperId": 3528076,
     * "pointsName": "知识点1，知识点2，知识点3，知识点4",
     * "practiceId": -1,
     * "qcount": 10,
     * "startTime": 1550505600000,
     * "status": 2
     */

    public String courseIds;        // 课程合计Ids

    public long courseId;           // 课程Id
    public String courseInfo;       // 解析课程说明信息
    public String courseName;       // 解析课程名称

    public String name;             // 考试名称
    public String idStr;            // 模考id
    public String description;      // 考试说明
    public long startTime;          // 开始时间
    public long endTime;            // 结束时间
    public int qcount;              // 试题数量
    public long limitTime;          // 考试时长
    public String pointsName;       // 知识点
    public int status;              // 按钮状态，考试状态
    public long practiceId;         // 答题卡Id（现在不需要了）
    public int joinCount;           // 参加人数
    public long paperId;            // 试卷Id
}
