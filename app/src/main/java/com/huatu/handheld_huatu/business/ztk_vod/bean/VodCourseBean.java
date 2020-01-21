package com.huatu.handheld_huatu.business.ztk_vod.bean;



import com.huatu.handheld_huatu.business.lessons.bean.BaseCourseListItemBean;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-djd on 2017/9/4.
 *
 */

@Deprecated
public class VodCourseBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public class DataBean {
        public ArrayList<ResultBean> result;
        public int next;
    }

    public static Lessons convertToLessons(ResultBean resultBean) {
        BaseCourseListItemBean itemBean = resultBean;
       // itemBean.ActualPrice = resultBean.actualPrice;
        //itemBean.Price = resultBean.price;
       // itemBean.CourseLength = resultBean.timeLength + "课时";
        itemBean.rid = resultBean.netClassId;
        itemBean.NetClassId = resultBean.netClassId;
        Lessons lesson = new Lessons(itemBean);
        return lesson;
    }

    public class ResultBean extends BaseCourseListItemBean {
        //  public String rid;              // 网课id   ps:同上，不知道为什么不同接口用的字段名不一样，电子书和图书推荐视频课程列表用此字段
        public String typeName;         // 类型
        public String subjectName;      // 科目(申论、行测、面试等)
        public int maxPrice;
        public int minPrice;
        public String youHuiPrice;      // 优惠幅度
        public String hits;             // 点击量(热度)
        public String monthCourse;      // 是否包月卡课程
        public String isSuit;           //是否是套餐课
        public int  status; //录播课程状态
        public String collectTitle ;//合集标题
        public String collectShorTitle; //合集短标题
        public int isActive;         // 是否有活动
        public String activeStart;
        public String activeEnd;
        public String isClass;          // 是否是套餐课
        public int isNormal;       //是否抢购课

       // public String price;            // 原价
       // public String actualPrice;      // 实际售价
       // public String timeLength;       // 课时
         public String netClassId;       // 网课id

        @Override
        public String toString() {
            return "ResultBean{" +
                    ", typeName='" + typeName + '\'' +
                    ", subjectName='" + subjectName + '\'' +
                    ", maxPrice=" + maxPrice +
                    ", minPrice=" + minPrice +
                    ", youHuiPrice='" + youHuiPrice + '\'' +
                    ", hits='" + hits + '\'' +
                    ", monthCourse='" + monthCourse + '\'' +
                    ", isSuit='" + isSuit + '\'' +
                    ", status=" + status +
                    ", collectTitle='" + collectTitle + '\'' +
                    ", collectShorTitle='" + collectShorTitle + '\'' +
                    ", isActive=" + isActive +
                    ", activeStart='" + activeStart + '\'' +
                    ", activeEnd='" + activeEnd + '\'' +
                    ", isClass='" + isClass + '\'' +
                    ", isNormal=" + isNormal +
                    '}';
        }
    }
}
