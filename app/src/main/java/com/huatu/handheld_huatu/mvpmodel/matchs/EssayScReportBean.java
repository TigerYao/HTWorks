package com.huatu.handheld_huatu.mvpmodel.matchs;


import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

import java.io.Serializable;
import java.util.List;

public class EssayScReportBean extends RealExamBeans.RealExamBean {
    //申论特有字段
    /**
     * 问题列表
     */
    public List<QuestionListEntity> questionList;
    /**
     * 试卷名称
     */
    public String paperName;
    public int questionCount;
    public double totalScore;


    public static class QuestionListEntity implements Serializable{
        /**
         * examScore : 80.5
         * inputWordNum : 185
         * sort : 1
         * spendTime : 143
         * type : 1
         */

        public double examScore;
        public int inputWordNum;
        public double score;
        public int sort;
        public int spendTime;
        public int type;

        public double getExamScore() {
            return examScore;
        }

        public void setExamScore(double examScore) {
            this.examScore = examScore;
        }

        public int getInputWordNum() {
            return inputWordNum;
        }

        public void setInputWordNum(int inputWordNum) {
            this.inputWordNum = inputWordNum;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getSpendTime() {
            return spendTime;
        }

        public void setSpendTime(int spendTime) {
            this.spendTime = spendTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
