package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;
import java.util.List;

/**
 * 知识点Bean
 * Created by KaelLi on 2016/7/15.
 */
public class PointBean implements Serializable {
    public int id;
    public String name;
    public int parent;
    public int qnum;
    public int rnum;
    public int wnum;
    public int unum;
    public int times;
    public int speed;
    public int level;
    public double accuracy;
    public List<PointBean> children;
}
