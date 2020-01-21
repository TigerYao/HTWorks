package com.huatu.handheld_huatu.mvpmodel.special;

import java.util.List;

/**
 * 提交每日特训设置数据的Bean
 */
public class SettingBean {
    private long id;
    private int number;
    private int questionCount;
    private List<Integer> selects;

    public SettingBean(long id, int number, int questionCount, List<Integer> selects) {
        this.id = id;
        this.number = number;
        this.questionCount = questionCount;
        this.selects = selects;
    }

    @Override
    public String toString() {
        return "SettingBean{" +
                "id=" + id +
                ", number=" + number +
                ", questionCount=" + questionCount +
                ", selects=" + selects +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public List<Integer> getSelects() {
        return selects;
    }

    public void setSelects(List<Integer> selects) {
        this.selects = selects;
    }
}
