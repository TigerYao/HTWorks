package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 试题Bean
 * Created by KaelLi on 2016/7/6.
 */
public class ExerciseBeans {
    public List<ExerciseBean> data;
    public String code;

    public class ExerciseBean implements Serializable{
        public int id;                  // 试题id
        public int type;                // 试题类型：单选99|多选100|不定项101|对错题109|复合题105
        public String from;             // 来源
        public String material;         // 材料
        public int year;                // 试题年份
        public int area;                // 试题区域
        public int status;              // 状态 BB102 审核标示,有效标识
        public int mode;                // 试题模式：真题1|模拟题2
        public int subject;             // 科目
        public String stem;             // 题干
        public int answer;              // 标准答案
        public List<String> choices;    // 选项列表
        public String analysis;         // 解析
        public String extend;           // 拓展
        public float score;             // 分数
        public int difficult;           // 难度系数
        public List<Integer> points;    // 知识点id
        public List<String> pointsName; // 知识点名称列表,跟知识点一一对应,最多3个
        public int parent;              // 所属复合题id
        public int recommendedTime;     // 推荐用时
        public Meta meta;

        public ArrayList<PointsList> pointList;    // 知识点

        public List<String> materials;  // 主观题的材料
        public String require;          // 主观题的题目要求
        public String scoreExplain;     // 主观题的赋分说明
        public String referAnalysis;    // 主观题的参考解析，作为参考答案
        public String answerRequire;    // 主观题的答题要求
        public String examPoint;        // 主观题的审题要求
        public String solvingIdea;      // 主观题的解题思路
        public String teachType;        // 教研题型,会有null情况或者空字符串

    }

    public static class PointsList implements Serializable{
        public List<Integer> points;    // 知识点id
        public List<String> pointsName; // 知识点名称列表,跟知识点一一对应,最多3个
    }

    public static class Meta implements Serializable {
        public List<Integer> answers;
        public int avgTime;
        public int count;
        public List<Integer> counts;
        public List<Integer> percents;
        public int rindex;
        public int yc;
    }
}
