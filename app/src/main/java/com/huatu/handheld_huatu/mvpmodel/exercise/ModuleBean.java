package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;

/**
 * 题型模块Bean
 * Created by KaelLi on 2016/7/15.
 */
public class ModuleBean implements Serializable {
    public int category;            // 模块id
    public String name;             // 模块名称
    public int qcount;              // 该模块题量
}
