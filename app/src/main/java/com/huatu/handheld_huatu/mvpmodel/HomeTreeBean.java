package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * desc:HomeTreeBean
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class HomeTreeBean implements Serializable {
    private int id;
    //根pid为0
    private int parent = 0;
    private String name;
    //总题数
    private int qnum;
    //未做题数
    private int unum;
    //正确率
    private double accuracy;
    private int rnum;
    private int wnum;
    private long times;
    private long speed;
    //当前的级别
    private int level;
    //下一级的子Node
    private List<HomeTreeBean> children = new ArrayList<HomeTreeBean>();
    //父Node
    private HomeTreeBean father;

    //0为无线，1为全有，2为上方有，3为下方有
    private int lineMode = 0;

    //是否展开
    private boolean isExpand = false;

    private int icon;
    //是否是最后一个
    private boolean isLast = false;

    private long  unfinishedPracticeId;

    public HomeTreeBean(int id, int parent, String name, int qnum, int unum, double accuracy, int rnum, int wnum, long times, long speed, int level,long  unfinishedPracticeId, List<HomeTreeBean> children) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.qnum = qnum;
        this.unum = unum;
        this.accuracy = accuracy;
        this.rnum = rnum;
        this.wnum = wnum;
        this.times = times;
        this.speed = speed;
        this.level = level;
        this.unfinishedPracticeId = unfinishedPracticeId;
        if (children != null) {
            this.children = children;
        }
    }

    /**
     * 是否为跟节点
     *
     * @return boolean
     */
    public boolean isRoot() {
        return father == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return boolean
     */
    public boolean isParentExpand() {
        if (father == null)
            return false;
        return father.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return boolean
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 设置展开
     *
     * @param isExpand boolean
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (HomeTreeBean node : children) {
                node.setExpand(isExpand);
            }
        }
    }

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

    public int getUnum() {
        return unum;
    }

    public void setUnum(int unum) {
        this.unum = unum;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
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

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public List<HomeTreeBean> getChildren() {
        return children;
    }

    public void setChildren(List<HomeTreeBean> children) {
        this.children = children;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public HomeTreeBean getFather() {
        return father;
    }

    public void setFather(HomeTreeBean father) {
        this.father = father;
    }

    public int getLineMode() {
        return lineMode;
    }

    public void setLineMode(int lineMode) {
        this.lineMode = lineMode;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public long getUnfinishedPracticeId() {
        return unfinishedPracticeId;
    }

    public void setUnfinishedPracticeId(long unfinishedPracticeId) {
        this.unfinishedPracticeId = unfinishedPracticeId;
    }
}
