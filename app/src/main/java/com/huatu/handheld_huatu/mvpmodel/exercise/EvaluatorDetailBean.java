package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.util.List;

/**
 * Created by ljzyuhenda on 16/7/23.
 */
public class EvaluatorDetailBean {
    public String code;
    public EvaluatorData data;

    public class EvaluatorData {
        public List<ModuleSummaryBean> moduleSummary;
        public ForeCastBean forecast;
        public PowerSummaryBean powerSummary;
        public MonthSummary monthSummary;
    }


    /**
     * "powerSummary": {//能力评估
     * "uid": 12252065,
     * "subject": 1,
     * "score": 20,//预测分
     * "avg": 24.2,//全站平均分
     * "beat": 12.3 //击败率
     * }
     */
    public class PowerSummaryBean {
        public String uid;
        public String subject;
        public int score;
        public String avg;
        public float beat;
    }

    public class MonthSummary {
        public String id;
        public long uid;
        public int subject;
        public int parcticeCount;
        public int dayCount;
        public double average;
        public int times;
        public int rcount;
        public int wcount;
        public int speed;
    }

    public class ForeCastBean {
        public String[] categories;
        public List<SeriesBean> series;
    }

    public class SeriesBean {
        public String name;//日期
        public float[] data;//平局分,我的分
    }

    /**
     * {
     * "uid": 12252065,
     * "subject": 1,
     * "moduleId": 642,
     * "moduleName": “常识判断”,
     * "score": 79
     * }
     */
    public class ModuleSummaryBean {
        public String uid;
        public String subject;
        public String moduleId;
        public String moduleName;
        public float score;
    }
}
