package com.huatu.handheld_huatu.mvpmodel.matchs;


import java.io.Serializable;

public class SimulationScHistoryTag implements Serializable {


    /**
     * id : 1
     * name : 2018国考行测
     * flag : 0、不是申论  1、是申论
     */

    private int id;
    private String name;
    private int flag;
    private int subject;
    private int category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
