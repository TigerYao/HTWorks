package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 学习 --- 最后作业的数据 -- 重组后的新数据
 */
public class SectionExamRecombinBean {

    public static final String CLASS_NAME = "className";
    public static final String STUDY_DETAILS = "details";

    public int classId;
    public String className;
    public int undoCount;

    public String type;  //类别，区分哪一级
    public boolean unfold = true; //true:展开 false:未展开
    public List<SectionExamRecombinBean> child;  //把父节点下的数据，赋予次集合

    //孩子的数据
    public int index;
    public String examName;
    public int examId;
    public String endTime;
    public String startTime;
    public int isAlert;        //已读
    public int syllabusId;
    public int coursewareNum;
    public int status;
    public int isEffective;
    public boolean isExpired;
    public String showTime;
    public int alreadyRead;
}
