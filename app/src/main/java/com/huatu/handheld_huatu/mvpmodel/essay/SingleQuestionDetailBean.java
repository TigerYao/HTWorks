package com.huatu.handheld_huatu.mvpmodel.essay;

import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.AnswerImage;

import java.io.Serializable;
import java.util.List;

/**
 * 申论答题问题
 */
public class SingleQuestionDetailBean implements Serializable {

    /**
     * {
     * "imgList":[
     * {
     * "content":"测试内容150z",
     * "id":"测试内容nx94",
     * "imgUrl":"测试内容v94i",
     * "sort":27672
     * }
     * ],
     * "answerCardId":11,
     * "answerComment":"测试内容4x2v",
     * "answerRequire":"答题要求",
     * "callName":"测试内容1n0n",
     * "content":"测试发展 答题人性化测试管理打印。转变规则服务丰富开始，结束终于凄凄切切 ",
     * "examScore":0,
     * "inputWordNum":0,
     * "inputWordNumMax":300,
     * "inputWordNumMin":200,
     * "inscribedDate":"测试内容76p5",
     * "inscribedName":1,
     * "limitTime":25,
     * "questionBaseId":7,
     * "questionDetailId":101,
     * "score":30,
     * "sort":777,
     * "spendTime":11,
     * "stem":"题干信息",
     * "subTopic":1,
     * "topic":10523
     * }
     */

    //single
    public long answerCardId;           // 答题卡Id
    public String answerRequire;        // 答题要求（问题内容）
    public String content;              // 自己的答案
    public int inputWordNumMax;
    public int commitWordNumMax;
    public int inputWordNumMin;
    public int limitTime;
    public int point;
    public long questionBaseId;         // 问题id
    public long questionDetailId;
    public int questionType;            // 5、文章写作 其他是标准答案
    public int spendTime;
    public String fileName;
    public String stem;                 // title

    public List<AnswerComment> answerList;

    public int type;  //1：概括归纳 2：综合分析 3：解决问题 4：应用文 5：文章写作

    public String callName;
    public String inscribedDate;
    public String inscribedName;

    public List<AnswerImage> userMeta;              // 人工批改图片信息
    //public int convertCount;//转了几次
    public int correctMode;                         // 批改类型 1、智能 2、人工
    public int otherAnswerCardId;                   // 其他未完成批改的答题卡id

    public int sort;
    public double score;
    public int inputWordNum;
    public double examScore;

    public boolean isExp = true;
}
