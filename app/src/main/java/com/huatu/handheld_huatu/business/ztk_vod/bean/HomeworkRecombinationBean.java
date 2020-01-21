package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 学习 --- 最后作业的数据 -- 重组后的新数据
 */
public class HomeworkRecombinationBean {

    public static final String CLASS_NAME = "className";
    public static final String STUDY_DETAILS = "details";

    public String type;  //类别，区分哪一级
    public boolean unfold = true; //true:展开 false:未展开
    public List<HomeworkRecombinationBean> child;  //把父节点下的数据，赋予次集合

    public String courseTitle;
    public String correctMemo;
    public long courseId;
    public int undoCount;

    //下一级的数据
    public int index;
    public String courseWareTitle;
    public String questionTitle;//单题或者试卷的标题
    public String questionIds;
    public String areaName;
    public long questionBaseId;//单题id或者试卷id
    public long paperId;//单题id或者试卷id
    public int questionType;//0标准答案 1套题 2文章写作
    public int buildType;//0标准答案 1套题 2文章写作
    public long courseWareId;
    public int videoType;
    public String videoLength;
    public int serialNumber;
    public long answerCardId;
    public long syllabusId;
    public int isAlert;

    public int status;
    public int wcount;
    public int ucount;
    public int rcount;
    public int qcount;
    public int categoryId;
    public int fcount;

}
