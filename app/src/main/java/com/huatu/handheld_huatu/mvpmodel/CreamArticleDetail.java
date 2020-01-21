package com.huatu.handheld_huatu.mvpmodel;

import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;

/**
 * Created by saiyuan on 2019/2/22.
 */

public class CreamArticleDetail {
    public long adClassId;                  // 课程ID
    public long adCollageActiveId;          // 拼团ID
    public String adImg;                    // 广告图
    public String staticPageUrl;            // 地址
    public int goodPost;                    // 点赞
    public int click;                       // 阅读
    public boolean isCollection;            // 是否收藏
    public CourseListData classInfo;
//    adClassId	课程ID	number	@mock=97287
//    adCollageActiveId	拼团ID	number	@mock=0
//    adImg	广告图	string	@mock=http://v.huatu.com/htnews/uploads/allimg/161201/164-1612010U552538-lp.jpg
//    adVideoType	0-录播,1-直播	string	@mock=1
//    click	点击量	number	@mock=38
//    goodPost	好评	number	@mock=0
//    id	文章ID	number	@mock=189667
//    isTop	是否置顶	boolean	@mock=false
//    staticPageUrl	h5地址	string
}
