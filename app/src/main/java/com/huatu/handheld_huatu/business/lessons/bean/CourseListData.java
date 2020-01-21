package com.huatu.handheld_huatu.business.lessons.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chq on 2018/11/27.
 */

public class CourseListData {
    public String activeEnd;
    public String activeStart;
    public List<String> activeTag;//活动标签
    public String actualPrice;//实际价格
    public String brief;//描述
    public String classId;//课程ID
    public String collectId;//合集ID
    public String count;//销量
    public String img;//图片
    public String limit;//限购人数
    public String limitType;//限报形式
    public String liveDate;//直播日期（区间）
    public String price;//原价
    public String redEnvelopeId;//红包ID
    //    public int collageActiveId;//拼团ID
    public String collageActiveId;//拼团ID
    public String saleEnd;//距离倒计时结束的时间戳
    public long startTimeStamp;     // 开始时间戳（秒）     首页课程列表、合集、查看更多 走了cdn
    public long stopTimeStamp;      // 结束时间戳（秒）
    public long lSaleEnd;//距离倒计时结束的时间戳
    public String saleStart;//距离倒计时开始的时间戳
    public long lSaleStart;//距离倒计时开始的时间戳
    public String terminedDesc;//	待售描述
    public String timeLength;//	课时描述
    public String title;//	课程标题
    public String collectTag;//	合集标签
    public String collectBrief;//	精品微课合集描述
    //    public int videoType;//	0录播1直播
    public String videoType;//	0录播1直播
    public boolean isCollect;//是否是合集
    public boolean isRushOut;//是否停售
    public boolean isSaleOut;//是否售罄
    public boolean isTermined;//是否待售
    public boolean secondKill;//是否秒杀
    public boolean suit;//是否套餐课
    public boolean isTodayLive;//今日是否有直播
    public ArrayList<TeacherData> teacher;
    public boolean isNew;//是否直接跳转详情页
    public String defaultId;//合集ID

    public class TeacherData {
        public String teacherId;
        public String teacherName;
        public String roundPhoto;
//        id	教师ID	string	@mock=1
//        name	教师名称	string	@mock=顾斐
//        photo	教师头像	string	@mock=http://upload.htexam.com/teacherphoto/1506300446553913.jpg
    }
//    activeEnd		string	@mock=$order('','','')
//    activeStart		string	@mock=$order('','','')
//    activeTag	活动标签	string	@mock=$order('','','')
//    actualPrice	实际价格	string	@mock=$order('10','10','10')
//    brief	描述	string	@mock=$order('讲练结合，名师大咖带你快速吸收和掌握热点理论知识！','讲练结合，名师大咖带你快速吸收和掌握热点理论知识！','讲练结合，名师大咖带你快速吸收和掌握热点理论知识！')
//    classId	课程ID	string	@mock=$order('87016','87015','45926')
//    count	销量	string	@mock=$order('3','3','3')
//    img	图片	string	@mock=$order('http://p.htwx.net/images/course_default.jpg','http://p.htwx.net/images/course_default.jpg','http://p.htwx.net/images/course_default.jpg')
//    isCollect	是否是合集	boolean	@mock=$order(false,false,true)
//    isRushOut	是否停售	boolean	@mock=$order(false,false,false)
//    isSaleOut	是否售罄	boolean	@mock=$order(false,false,false)
//    isTermined	是否待售	boolean	@mock=$order(false,false,false)
//    isTodayLive	今日是否有直播	boolean	@mock=$order(false,false,false)
//    limit	限购人数	string	@mock=$order('0','0','0')
//    limitType	限报形式	string	@mock=$order('0','0','0')
//    liveDate	直播日期（区间）	string	@mock=$order('11月5日','11月5日','11月5日')
//    price	原价	string	@mock=$order('98','98','98')
//    redEnvelopeId	红包ID	string	@mock=$order('137','137','137')
//    saleEnd	距离倒计时结束的时间戳	string	@mock=$order('0','0','0')
//    saleStart	距离倒计时开始的时间戳	string	@mock=$order('0','0','0')
//    secondKill	是否是秒杀	boolean	@mock=$order(false,false,false)
//    suit	是否是套餐课	boolean	@mock=$order(false,false,false)
//    teacher	教师信息	array<object>
//    terminedDesc	待售描述	string	@mock=$order('','','')
//    timeLength	课时	string	@mock=$order('0','0','0')
//    title	课程标题	string	@mock=$order('测试红包领取查询功能003','测试测试测试','测试测试测试')
//    videoType	0录播1直播	string	@mock=$order('0','0','0')
}
