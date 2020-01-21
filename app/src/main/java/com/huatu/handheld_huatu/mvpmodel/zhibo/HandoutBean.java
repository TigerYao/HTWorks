package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;

import java.util.List;

/**
 * Created by ht on 2016/12/23.
 */
public class HandoutBean {
    public long code;
    public Data data;

    @Override
    public String toString() {
        return "HandoutBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public static class Data {
        public List<Course> course_jiangyi;

        @Override
        public String toString() {
            return "Data{" +
                    "course_jiangyi=" + course_jiangyi +
                    '}';
        }
    }

    public static class Course {
        public String id;
        public String title;
        public String downloadurl;//这个字段去了
        public String downloadUrl;//用这个
        public String pubDate;
        public String fileSize;
        public int    downStatus;
        public String localPath;

        public boolean selected;        // 在下载列表中是否被选中

        @Override
        public String toString() {
            return "Course{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", downloadUrl='" + downloadurl + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    '}';
        }
    }

    public static class CourseGroup {
        public String classId;
        public String title;

        public List<CourseTypeInfo> list;

        public CourseTypeInfo toHeadBean(){
            CourseTypeInfo tmpInfo=new CourseTypeInfo();
            tmpInfo.type=1;
            tmpInfo.title=title;
            tmpInfo.classId=classId;
            return tmpInfo;
        }
     }


    //打平了之后数据
    public static class CourseTypeInfo {

        public String     title;      //": "2016年多省联考申论真题",
        public String     netClassId; // "52128",
        public String     rid;        //": "84610",
        public String     fileUrl;    //": "http://upload.htexam.net/jiangyi/1490769131.pdf",
        public String     createDate; //": "2017-03-29 14:34:54.760",
        public String     fileSize;   //": "1",


        public String classId;
        public int type      ;//1  group 0 child

        public List<CourseTypeInfo> childs;

        public int    downStatus;
        public String localPath;
        public boolean selected;        // 在下载列表中是否被选中

        private boolean isClosed;
        public void setClosed(boolean isclosed){
            isClosed=isclosed;
        }
        public boolean isClosed(){
            return isClosed;
        }
    }





    /*
    public static class CourseInfo {
        public String     title;      //": "2016年多省联考申论真题",
        public String     netClassId; // "52128",
        public String     rid;        //": "84610",
        public String     fileUrl;    //": "http://upload.htexam.net/jiangyi/1490769131.pdf",
        public String     createDate; //": "2017-03-29 14:34:54.760",
        public String     fileSize;   //": "1",
    }
*/
}
