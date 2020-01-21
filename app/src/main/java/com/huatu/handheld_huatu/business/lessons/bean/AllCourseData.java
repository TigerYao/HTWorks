package com.huatu.handheld_huatu.business.lessons.bean;

import java.util.ArrayList;

/**
 * Created by chq on 2018/11/27.
 */

public class AllCourseData {
    public boolean more;
    public String title;
    public String typeId;
    public String img;
    public ArrayList<CourseListData>  data;
}
//    data		array<object>
//    more	是否有更多	boolean	@mock=$order(true,true)
//        title	分类标题	string	@mock=$order('推荐课程','公开课')
//        typeId	分类ID	string	@mock=$order('1','2')