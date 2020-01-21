package com.huatu.handheld_huatu.mvpmodel.zhibo;

import java.util.List;

/**
 * Created by cjx on 2018\8\3 0003.
 */

public class InClassAnswerCardBean {


    public List<String> answers       ;//	用户答案	array<string>	@mock=$order('0','0','0','0','0','0','0','0','0','0','0')
    public int  catgory		;//number	@mock=1
    public List<Integer>   corrects;//	正确	array<number>	@mock=$order(0,0,0,0,0,0,0,0,0,0,0)
    public long createTime	;//创建时间	number	@mock=1533109815681
   // public int difficulty	;//难度	number	@mock=5
   // doubts	是否有疑问	array<number>	@mock=$order(0,0,0,0,0,0,0,0,0,0,0)
    public long   expendTime		;//number	@mock=0
    public String id	    ;//答题卡ID	string	@mock=2001288796888367104
    public int    lastIndex	;//上次答题位置	number	@mock=0
    public String name	    ;//答题卡名称	string	@mock=课中练习
    public Paper  paper ;

   // points
    public int   rcount	    ;//正确数量	number	@mock=0
    public long  recommendedTime		;//number	@mock=860
    public long  remainingTime		;//number	@mock=-1
   //  public int   score	    ;//分数	number	@mock=0
    public int   speed		;//number	@mock=0
    public int   status		;//number	@mock=1
    public int   subject		;//number	@mock=1
    public int   terminal		;//number	@mock=1
    public List<Integer> times		;//array<number>	@mock=$order(0,0,0,0,0,0,0,0,0,0,0)
    public int   type		;//number	@mock=16
    public int   ucount	    ;//未答数量	number	@mock=11
    public long  userId	    ;//用户ID	number	@mock=233906500
    public int   wcount	    ;//错误数量	number	@mock=0


    public static class Paper{

        public List<BreakPoint> breakPointInfoList;

        public int   catgory		;//number	@mock=1
        public int   courseId	    ;//课件ID	number	@mock=2333
        public int   courseType	    ;// 课件类型	number	@mock=2
       // public int   difficulty	    ;//难度	number	@mock=5
      //  modules	模块
      //  name	名称	string	@mock=课中练习
         public int   qcount	    ;//试题总数量	number	@mock=11
      //   questions	试题ID信息	array<number>	@mock=$order(21935406,21935408,21935409,21935407,21935408,21935409,21935426,21935408,21935409,21935426,21935426)
      //  subject		number	@mock=1
      //public int  type	类型，类型 1-课中练习 2-课后练习	number	@mock=1
    }

    public static class BreakPoint{

        public  long          id ;//	ID	number	@mock=$order(78,79,80,81)
        public  String        pointName	;//断点名称	string	@mock=$order('节点3','节点4','节点5','节点6')
        public  float         position	;//位置	number	@mock=$order(4,5,70,74)
        public  List<String>  questionInfoList	;//该断点下的所有试题IID	array<number>	@mock=$order(21935406,21935408,21935409)
        public  int           sort	  ;//排列序号
    }
}
