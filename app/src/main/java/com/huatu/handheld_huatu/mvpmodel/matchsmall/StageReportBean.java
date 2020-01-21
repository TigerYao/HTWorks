package com.huatu.handheld_huatu.mvpmodel.matchsmall;

import com.huatu.handheld_huatu.mvpmodel.exercise.PointBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

import java.io.Serializable;
import java.util.List;

public class StageReportBean extends RealExamBeans.RealExamBean {

    public String typeInfo;                     // 练习类型，阶段测试

    public boolean isViewAllReport;             // 是否可以查看所有报告，true可以，false不可以

    public int unum;                            // 未做题量
    public int rnum;                            // 正确题数目
    public int wnum;                            // 正确题数目
    public int timesCount;                      // 答题总耗时

    public float accuracy;                      // 做题正确率
    public int averageTime;                     // 平局每道题用时

    public float userScore;                     // 用户得分
    public int rank;                            // 排名

    public double maxScore;                     // 最高得分
    public double averageScore;                 // 平均得分
    public float beatRate;                      // 击败比例

    public long submitTime;                     // 交卷时间
    public long reportTime;                     // 报告统计时间

    public int startTimeIsEffective;            // 是否有时间限制，0、无效 1、有效 无时间限制就不显示顶部的提示
    public int submitCount;                     // 交卷人数
    public int submitSort;                      // 交卷顺序排名

    public TeacherRemark teacherRemark;         // 老师评语

    public List<Integer> questions;             // 题id

    public List<PointBean> questionPointTrees;  // 知识点

    public static class TeacherRemark implements Serializable {
        public int pointCount;          // 知识点条数
        public int knowCount;           // 知识点了解条数
        public String knowName;         // 了解的知识点名称
        public int understandCount;     // 知识点理解条数
        public String understandName;   // 理解的知识点名称
        public int knowWellCount;       // 知识点掌握条数
        public String knowWellName;     // 掌握的知识点名称
        public int elasticCount;        // 知识点灵活运用的条数
        public String elasticName;      // 灵活运用知识点名称
        public String teacherName;      // 老师名称
    }

}
