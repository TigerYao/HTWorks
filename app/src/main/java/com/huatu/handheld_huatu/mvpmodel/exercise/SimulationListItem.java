package com.huatu.handheld_huatu.mvpmodel.exercise;


import java.io.Serializable;

/**
 * Created by ljzyuhenda on 16/7/19.
 */
public class SimulationListItem implements Serializable {
    /**
     * id	int	试卷id
     * name string 试卷名称
     * currentPracticeId	long	用户未昨晚试卷练习id，-1:试卷已做完 >0:未做完练习id
     * finishCount	int	用户完成该试卷次数
     * userMeta	复合	用户试卷统计
     * paperMeta	复合	试卷统计信息
     * cardCounts	int	答题交卷人数
     */
    //试卷id
    public int id;
    //分数
    public int score;
    //及格线
    public int passScore;
    //考试时间，单位秒
    public int time;
    //试题个数
    public int qcount;
    //难度系数
    public double difficulty;
    //试卷类型2：模拟题，8：估分 9:模考大赛
    public int type;
    //模考估分考试开始时间，毫秒
    public long startTime;
    //模考估分考试结束时间
    public long endTime;
    //活动下线时间
    public long offlineTime;
    //活动上线时间
    public long onlineTime;
    //试卷活动状态 1未开始，2正在进行，3已经结束，4已经下线，5可继续做题，6，可查看报告，7未出报告
    public int status;
    //时间说明文字
    public String descrp;
    //包名页url
    public String url;
    //查看报告的时间标识，1立即查看，2活动结束后查看
    public int lookParseTime;
    //试卷名称
    public String name;
    //年份
    public int year;
    //地区
    public String area;
    //用户试卷统计
    public UserMetaBean userMeta;
    //试卷统计信息
    public PaperMetaBean paperMeta;
    // 大礼包图标地址
    public String iconUrl;

    @Override
    public String toString() {
        return "SimulationListItem{" +
                "id=" + id +
                ", score=" + score +
                ", passScore=" + passScore +
                ", time=" + time +
                ", qcount=" + qcount +
                ", difficulty=" + difficulty +
                ", type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", offlineTime=" + offlineTime +
                ", onlineTime=" + onlineTime +
                ", status=" + status +
                ", descrp='" + descrp + '\'' +
                ", url='" + url + '\'' +
                ", lookParseTime=" + lookParseTime +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", area='" + area + '\'' +
                ", userMeta=" + userMeta +
                ", paperMeta=" + paperMeta +
                ", iconUrl=" + iconUrl +
                '}';
    }
}
