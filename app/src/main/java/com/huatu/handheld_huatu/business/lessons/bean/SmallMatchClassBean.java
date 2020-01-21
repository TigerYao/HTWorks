package com.huatu.handheld_huatu.business.lessons.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/27
 */
public class SmallMatchClassBean {

        /**
         * current_page : 1
         * data : [{"classId":"65271","title":"事业单位公基万人模考解析峰会（4.28-12.22）","cateId":"8","teacherDesc":"杨翠艳、李建英、孔令昂","actualPrice":"0","price":"","img":"http://upload.htexam.net/classimg/class/1524226027.jpg","timeLength":"4月28日-12月22日 共38课时","isTermined":false,"terminedDesc":"","isSuit":false,"secondKill":false,"brief":"","videoType":"1","redEnvelopeId":"","courseType":"1","isCollect":false,"limit":"0","id":"65271","activeTag":[],"collageActiveId":0,"count":9330,"teacher":[{"teacherId":"999","teacherName":"杨翠艳","roundPhoto":"http://upload.htexam.com/teacherphoto/1509504617.jpg"},{"teacherId":"628","teacherName":"李建英","roundPhoto":"http://upload.htexam.com/teacherphoto/1509451304.jpg"},{"teacherId":"353","teacherName":"孔令昂","roundPhoto":"http://upload.htexam.com/teacherphoto/15063005050917949.jpg"}],"limitType":0,"saleStart":0,"saleEnd":0,"isRushOut":false,"isSaleOut":false},{"classId":"86980","title":"1对1课程666","cateId":"1","teacherDesc":"李委明","actualPrice":"2","price":"9999","img":"http://p.htwx.net/images/course_default.jpg","timeLength":"10月29日","isTermined":false,"terminedDesc":"","isSuit":false,"secondKill":false,"brief":"","videoType":"1","redEnvelopeId":"","courseType":"0","isCollect":false,"limit":"100","id":"86980","activeTag":[],"collageActiveId":0,"count":8,"teacher":[{"teacherId":"2","teacherName":"李委明","roundPhoto":"http://upload.htexam.com/teacherphoto/1506300453473100.jpg"}],"limitType":"0","saleStart":0,"saleEnd":0,"isRushOut":false,"startTimeStamp":1540801800,"isSaleOut":false},{"classId":"97238","title":"课程B---AB优惠","cateId":"1","teacherDesc":"于洪泽","actualPrice":"2","price":"222","img":"http://p.htwx.net/images/course_default.jpg","timeLength":"12月28日","isTermined":false,"terminedDesc":"","isSuit":false,"secondKill":false,"brief":"","videoType":"1","redEnvelopeId":"","courseType":"0","isCollect":false,"limit":"0","id":"97238","activeTag":[],"collageActiveId":0,"count":2,"teacher":[{"teacherId":"16","teacherName":"于洪泽","roundPhoto":"http://upload.htexam.com/teacherphoto/1507030908596236.jpg"}],"limitType":0,"saleStart":0,"saleEnd":0,"isRushOut":false,"isSaleOut":false},{"classId":"97287","title":"测试限招","cateId":"1","teacherDesc":"顾斐、刘有珍","actualPrice":"50","price":"100","img":"http://p.htwx.net/images/course_default.jpg","timeLength":"1月13日","isTermined":false,"terminedDesc":"","isSuit":false,"secondKill":false,"brief":"","videoType":"1","redEnvelopeId":"","courseType":"0","isCollect":false,"limit":"10","id":"97287","activeTag":[],"collageActiveId":0,"count":9,"teacher":[{"teacherId":"1","teacherName":"顾斐","roundPhoto":"http://upload.htexam.com/teacherphoto/1506300446553913.jpg"},{"teacherId":"157","teacherName":"刘有珍","roundPhoto":"http://upload.htexam.com/teacherphoto/1509453292.jpg"}],"limitType":"1","saleStart":0,"saleEnd":0,"isRushOut":false,"isSaleOut":false}]
         * from : 0
         * last_page : 1
         * next_page_url : null
         * path : /
         * per_page : 20
         * prev_page_url : null
         * to : 8
         * total : 8
         */

        @SerializedName("current_page")
        public int currentPage;
        @SerializedName("from")
        public int from;
        @SerializedName("last_page")
        public int lastPage;
        @SerializedName("next_page_url")
        public Object nextPageUrl;
        @SerializedName("path")
        public String path;
        @SerializedName("per_page")
        public int perPage;
        @SerializedName("prev_page_url")
        public Object prevPageUrl;
        @SerializedName("to")
        public int to;
        @SerializedName("total")
        public int total;
        @SerializedName("data")
        public List<CourseListData> courseListData;

}
