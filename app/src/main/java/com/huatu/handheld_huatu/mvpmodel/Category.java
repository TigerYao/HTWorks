package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;
import java.util.List;

/**
 * 科目类型，大科目 & 小科目
 */
public class Category implements Serializable {

    /**
     * id : 1
     * name : 公务员
     * childrens : [{"id":1,"name":"公务员行测","childrens":[],"tiku":false}]
     * tiku : true
     */
    public int id;
    public String name;
    public List<Subject> childrens;
    public boolean tiku;

    public static class Subject implements Serializable {

        /**
         * id : 1
         * name : 公务员行测
         * childrens : []
         * tiku : false
         */
        public int id;
        public String name;
        public boolean tiku;
    }
}
