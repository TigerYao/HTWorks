package com.huatu.handheld_huatu.mvpmodel.arena;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2016/10/17.
 */
public class ArenaExamQuestionBean implements Serializable {
    public int code;
    public String message;
    public int id;                                              // 试题id
    public String name;                                         // 显示名称 （专项练习、智能刷题、模考大赛...）
    public String categoryName;                                 // 所属类型 （常识判断、判断推理、言语理解与表达...）
    public int type;                                            // 试题类型：单选99|多选100|不定项101|对错题109|复合题105
    public String from;                                         // 来源
    public String material;                                     // 材料
    public int year;                                            // 试题年份
    public int area;                                            // 试题区域
    public int status;                                          // 状态 BB102 审核标示,有效标识
    public int mode;                                            // 试题模式：真题1|模拟题2
    public int subject;                                         // 科目
    public String stem;                                         // 题干
    public int answer;                                          // 标准答案
    public String analysis;                                     // 解析
    public String extend;                                       // 扩展
    public float score;                                         // 分数
    public int difficult;                                       // 难度系数
    public int parent;                                          // 所属复合题id
    public List<QuestionOption> questionOptions;                // 选项列表
    public List<KnowledgePoint> knowledgePointsList;            // 知识点列表,最多3个
    public int usedTime;                                        // 答题使用时间
    public boolean isFaverated;                                 // whether is faverated
    public boolean isSingleChoice;                              // 只有一个答案
    public boolean isSubmitted;                                 // 提交过
    public int userAnswer;                                      // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
    public int isCorrect;                                       // 是否正确 00表示未做答 1:正确 2:错误
    public int index;                                           // 该题目在试卷中的位置，主要用于错题列表
    public int doubt;                                           // 1:用户对该试题有疑问, 0:没有疑问
    public int recommendedTime;                                 // 推荐用时
    public ExerciseBeans.Meta meta;

    public List<Integer> points;                                // 知识点id
    public List<String> pointsName;                             // 知识点名称列表,跟知识点一一对应,最多3个
    public List<String> materials;                              // 主观题的材料
    public String require;                                      // 主观题的题目要求
    public String scoreExplain;                                 // 主观题的赋分说明
    public String referAnalysis;                                // 主观题的参考解析，作为参考答案
    public String answerRequire;                                // 主观题的答题要求
    public String examPoint;                                    // 主观题的审题要求
    public String solvingIdea;                                  // 主观题的解题思路
    public String teachType;                                    // 教研题型,会有null情况或者空字符串

    public int seeType;                                         // 自定义字段。查看模式，为了背题模式，根据这个字段判断怎么显示。把判断requestType改成判断这个字段

    public boolean isMaterial;                                  // 是否是材料题
    public ArrayList<ArenaExamQuestionBean> questions;          // 如果是材料题，这里存放材料下的问题list，一个或多个

    public ArrayList<MarkInfo> materialMark;                    // 这是材料里的高亮
    public ArrayList<MarkInfo> questionMark;                    // 这是题干里的高亮

    public static class QuestionOption implements Serializable {
        public String optionDes;
        public boolean isSelected = false;
        public boolean isDeleted = false;
    }

    public static class KnowledgePoint implements Serializable {
        public int id;
        public String name;
    }
}
