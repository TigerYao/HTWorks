package com.huatu.handheld_huatu.mvpmodel.exercise;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;

import java.io.Serializable;
import java.util.List;

/**
 * 试卷Bean
 * Created by KaelLi on 2016/7/15.
 */
public class PaperBean implements Serializable {

    public int id;                          // 试卷id
    public String name;                     // 试卷名
    public int year;                        // 试卷年份
    public int area;                        // 试卷地区
    public int time;                        // 答题时间
    public float score;                     // 得分
    public int passScore;                   // 及格线
    public int qcount;                      // 题量
    public double difficulty;               // 难度系数
    public int type;                        // 试卷类型，1真题，2模拟题
    public List<ModuleBean> modules;        // 模块列表
    public List<ModuleBean> wrongModules;   // 模块列表
    public int status;                      // 试卷状态
    public int subject;                     // 科目
    public List<Integer> questions;         // 题号
    public List<ArenaExamQuestionBean> questionBeanList;        // 问题列表
    public List<ArenaExamQuestionBean> wrongQuestionBeanList;   // 错题列表
    public long startTime;                  // （模考）考试开始时间
    public long endTime;                    // （模考）考试结束时间
    public long onlineTime;
    public long offlineTime;

}
