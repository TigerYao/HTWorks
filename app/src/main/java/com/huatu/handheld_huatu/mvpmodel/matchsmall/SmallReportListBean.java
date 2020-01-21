package com.huatu.handheld_huatu.mvpmodel.matchsmall;

import java.io.Serializable;
import java.util.List;

public class SmallReportListBean implements Serializable {

    public int next;
    public List<SmallReportBean> result;
    public int total;
    public int totalPage;

    public static class SmallReportBean implements Serializable {
        /**
         * "beatRate": 0,
         * "idStr": "111111111111",
         * "name": "小模考测试-1",
         * "practiceId": 111111111111,
         * "qcount": 20,
         * "submitCount": 100
         */

        public int beatRate;            // 击败比例
        public String idStr;            // 小模考id
        public String name;             // 小模考名称
        public long practiceId;         // （答题卡Id）
        public int qcount;              // 题量
        public int submitCount;         // 提交次数
    }
}
