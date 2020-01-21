package com.huatu.handheld_huatu.mvpmodel.matchs;

import java.io.Serializable;

/**
 * 模考大赛首页，获取顶部切换Tab
 */
public class MatchTabBean implements Serializable {

    public int flag;            // 1、标识当前科目 2、标识其他科目
    public int id;              // 科目Id
    public String name;         // 科目名称

    public int tipNum;          // 角标
}
