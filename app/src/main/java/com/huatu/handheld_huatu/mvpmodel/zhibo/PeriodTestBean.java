package com.huatu.handheld_huatu.mvpmodel.zhibo;

import java.util.List;

/**
 * Created by cjx on 2018\8\3 0003.
 */

public class PeriodTestBean {


    public List<String> answers       ;//	用户答案	array<string>	@mock=$order('0','0','0','0','0','0','0','0','0','0','0')

    public List<Integer>   corrects;//	正确	array<number>	@mock=$order(0,0,0,0,0,0,0,0,0,0,0)

    public String practiceId	    ;//答题卡ID	string	@mock=2001288796888367104

    public String name	    ;//答题卡名称	string	@mock=课中练习
    public Paper  paper ;

    public long  remainingTime		;//number	@mock=-1

    public List<Integer> times;

    public int status ;//

    public static class Paper{

        public String name;
        public int   catgory		;//number	@mock=1

        public int   difficulty	    ;//难度	number	@mock=5
      //  modules	模块
      //  name	名称	string	@mock=课中练习
         public int   qcount	    ;//试题总数量	number	@mock=11
      //   questions	试题ID信息	array<number>	@mock=$order(21935406,21935408,21935409,21935407,21935408,21935409,21935426,21935408,21935409,21935426,21935426)
      //  subject		number	@mock=1
      //public int  type	类型，类型 1-课中练习 2-课后练习	number	@mock=1
    }

}
