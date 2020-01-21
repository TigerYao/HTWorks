package com.huatu.handheld_huatu.business.matches.bean;

/**
 * Created by chq on 2019/1/22.
 */

public class ScHistoryPaperListResult {
    public int answerCount;//答题人数
    public int answerStatus;//0 继续答题 1开始答题
    public int completeCount;//我的作答次数
    public long courseId;//课程ID
    public long matchId;//模考大赛Id
    public String name;//模考大赛名称
    public long practiceId;//练习ID
    public boolean isDownLoaded;
    public boolean hasNew;

    //    answerCount	答题人数	number	@mock=$order(0,2,0,0,0,3,0,0,0,0,0,0,0,0,0,0)
//    answerStatus	0 继续答题 1开始答题	number	@mock=$order(1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1)
//    completeCount	我的作答次数	number	@mock=$order(0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0)
//    courseId	课程ID	number	@mock=$order(87093,132,11,123,11,111,1,87093,1,87093,123,87093,86980,87093,86802,86802)
//    matchId	模考大赛Id	number	@mock=$order(3527932,3528012,3528008,3528007,3528004,3527997,3527993,3527931,3527979,3527926,3527970,3527925,3527959,3527964,3527962,3527963)
//    name	模考大赛名称	string	@mock=$order('2014年-海南分校测试','2010年江苏省公务员考试《行测》真题（C卷）','2009年省市联考（福建/海南/辽宁/重庆/内蒙古）《行测》真题','2010年江苏省公务员考试《行测》真题（C卷）','2007年省公务员考试《行测》真题（甲卷）','中国史','重构模考大赛测试0114','2014年-海南分校测试','模考大赛重构测试20190108','2007年省公务员考试《行测》真题（甲卷）D','2010年江苏省公务员考试《行测》真题（C卷）','2007年省公务员考试《行测》真题（甲卷）C','2007年省公务员考试《行测》真题（甲卷）E','2019国考万人模考第七季-行测','2019国考万人模考第七季-行测','2019国考万人模考第七季-行测——测试')
//    practiceId	练习ID	string	@mock=$order('-1','-1','-1','-1','-1','318402699040040414','-1','-1','-1','-1','-1','-1','-1','-1','-1','-1')

}
