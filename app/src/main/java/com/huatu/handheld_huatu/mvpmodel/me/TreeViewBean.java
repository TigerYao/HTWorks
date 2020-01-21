package com.huatu.handheld_huatu.mvpmodel.me;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public class TreeViewBean {

    private int id;
    private String name;
    private int qnum;
    private int rnum;
    private int wnum;
    private int unum;
    private int times;
    private int speed;
    private int level;
    private List<TreeViewBean> children = new ArrayList<>();
    private int accuracy;
    public boolean isSelected;

    private TreeViewBean father;

    private boolean isEnd = false;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public TreeViewBean getFather() {
        return father;
    }

    public void setFather(TreeViewBean father) {
        this.father = father;
    }

    //判断是否展开
    private boolean isExpand = false;

    public boolean isExpand() {
        return isExpand;
    }

    //判断父节点是否展开
    public boolean isParentExpand() {
        if (level == 0) {
            return false;
        }
        return father.isExpand();
    }

    //判断父节点是否选择
    public boolean isParentSelected() {
        if (level == 0) {
            return false;
        }
        return father.isSelected;
    }

    //判断是否是根节点
    public boolean isRoot() {
        return level == 0;
    }

    //设置展开
    public void setExpand(boolean expand) {
        this.isExpand = expand;
        if (!expand) {
            for (TreeViewBean tree : children) {
                tree.setExpand(expand);
            }
        }
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getUnum() {
        return unum;
    }

    public void setUnum(int unum) {
        this.unum = unum;
    }

    public int getWnum() {
        return wnum;
    }

    public void setWnum(int wnum) {
        this.wnum = wnum;
    }

    public List<TreeViewBean> getChildren() {
        return children;
    }

    public void setChildren(List<TreeViewBean> children) {
        this.children = children;
    }


    @Override
    public String toString() {
        return "TreeViewBean{" +
                "accuracy=" + accuracy +
                ", id=" + id +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", qnum=" + qnum +
                ", rnum=" + rnum +
                ", speed=" + speed +
                ", times=" + times +
                ", unum=" + unum +
                ", wnum=" + wnum +
                ", children=" + children +
                ", father=" + father +
                ", isEnd=" + isEnd +
                ", isExpand=" + isExpand +
                '}';
    }
}
