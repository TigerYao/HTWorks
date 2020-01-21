package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * HomeFragment Tree知识点
 */
public class HomeTreeBeanNew implements Serializable {
    /**
     * {
     * "id":434,
     * "name":"公文",
     * "qnum":59,
     * "rnum":0,
     * "wnum":4,
     * "unum":0,
     * "times":0,
     * "speed":0,
     * "level":2,
     * "accuracy":0,
     * "children":[
     * <p>
     * ],
     * "unfinishedPracticeId":0
     * }
     */

    private int id;                                 // 类别Id
    private String name;                            // 类别名称
    private int qnum;                               // 总题数
    private int rnum;
    private int wnum;
    private int unum;                               // 未做题数
    private long times;
    private long speed;
    private int level;                              // 当前的级别
    private double accuracy;                        // 正确率
    private ArrayList<HomeTreeBeanNew> children;       //下一级的子Node       先不用
    private long unfinishedPracticeId;              // 未完成的试卷id

    // 自定义变量
    private boolean isExpand;           // 是否展开了
    private HomeTreeBeanNew parent;     // 父类id
    private boolean isLineUp;           // 是否显示上线
    private boolean isLineDown;         // 是否显示下线

    private int checkedState;           // 被选中状态 0、未选中 1、选中 2、半选中（这里是错题本导出使用）
    // 自定义变量

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

    public int getQnum() {
        return qnum;
    }

    public void setQnum(int qnum) {
        this.qnum = qnum;
    }

    public int getRnum() {
        return rnum;
    }

    public void setRnum(int rnum) {
        this.rnum = rnum;
    }

    public int getWnum() {
        return wnum;
    }

    public void setWnum(int wnum) {
        this.wnum = wnum;
    }

    public int getUnum() {
        return unum;
    }

    public void setUnum(int unum) {
        this.unum = unum;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public ArrayList<HomeTreeBeanNew> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<HomeTreeBeanNew> children) {
        this.children = children;
    }

    public long getUnfinishedPracticeId() {
        return unfinishedPracticeId;
    }

    public void setUnfinishedPracticeId(long unfinishedPracticeId) {
        this.unfinishedPracticeId = unfinishedPracticeId;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public HomeTreeBeanNew getParent() {
        return parent;
    }

    public void setParent(HomeTreeBeanNew parent) {
        this.parent = parent;
    }

    public boolean isLineUp() {
        return isLineUp;
    }

    public void setLineUp(boolean lineUp) {
        isLineUp = lineUp;
    }

    public boolean isLineDown() {
        return isLineDown;
    }

    public void setLineDown(boolean lineDown) {
        isLineDown = lineDown;
    }

    public int getCheckedState() {
        return checkedState;
    }

    public void setCheckedState(int checkedState) {
        this.checkedState = checkedState;
    }

    @Override
    public String toString() {
        return "HomeTreeBeanNew{" +
                "isLineUp=" + isLineUp +
                ", isLineDown=" + isLineDown +
                '}';
    }
}
