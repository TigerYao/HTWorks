package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;

/**
 * desc:HomeReport
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class HomeReport implements Serializable {
    //试题概述
    public QuestionSummary questionSummary;
    //知识点概述
    public PointSummary pointSummary;
    //能力概述
    public PowerSummary powerSummary;
}
