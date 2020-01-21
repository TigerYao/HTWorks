package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht-djd on 2017/9/26.
 */

public class CourseBuyDetailBean implements Serializable{


    /**
     * code : 1000000
     * data : {"classInfo":{"ActualPrice":3180,"TimeLength":"136","Title":"2017年国家公务员考试红领全程套餐A","TypeName":"套餐方案","actualPrice":3180,"isBuy":0,"limitStatus":7,"limitTimes":0,"limitUserCount":0,"rid":"52152","scaleimg":"http://upload.htexam.net/classimg/class/1461910636.jpg","startTime":"1970-01-01 08:00:00","stopTime":"2016-04-26 16:20:59","studyDate":"8月1日-1月1日","timeLength":"136","title":"2017年国家公务员考试红领全程套餐A","total":2995,"typeName":"套餐方案"},"teacherInfo":[{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300453473100.jpg","teacherId":"2","teacherName":"李委明"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300458302951.jpg","teacherId":"5","teacherName":"蔡金龙"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004552994.jpg","teacherId":"40","teacherName":"魏华刚"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005013215711.jpg","teacherId":"81","teacherName":"罗红军"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"92","teacherName":"张明明"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004530013647.jpg","teacherId":"114","teacherName":"郜爽"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300505513345.jpg","teacherId":"153","teacherName":"袁东"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004460613566.jpg","teacherId":"157","teacherName":"刘有珍"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300459143508.jpg","teacherId":"191","teacherName":"刘子丰"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030901072390.jpg","teacherId":"203","teacherName":"韩利亚"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"218","teacherName":"省丽丽"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005050917949.jpg","teacherId":"353","teacherName":"孔令昂"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"428","teacherName":"胡泊"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030912356288.jpg","teacherId":"445","teacherName":"赵寰宇"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005002111436.jpg","teacherId":"453","teacherName":"张蕊"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1458609619.jpg","teacherId":"500","teacherName":"郝曜华"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"516","teacherName":"贺瑞锐"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"588","teacherName":"马宗亮"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"642","teacherName":"赵晶"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1509110328275406.jpg","teacherId":"655","teacherName":"马兰"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030857251248.jpg","teacherId":"717","teacherName":"车春艺"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1509150352152633.jpg","teacherId":"818","teacherName":"程永乐"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1094","teacherName":"华图老师22"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1112","teacherName":"王凌燕"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1117","teacherName":"王鹍 "}]}
     */

    private int code;
    /**
     * classInfo : {"ActualPrice":3180,"TimeLength":"136","Title":"2017年国家公务员考试红领全程套餐A","TypeName":"套餐方案","actualPrice":3180,"isBuy":0,"limitStatus":7,"limitTimes":0,"limitUserCount":0,"rid":"52152","scaleimg":"http://upload.htexam.net/classimg/class/1461910636.jpg","startTime":"1970-01-01 08:00:00","stopTime":"2016-04-26 16:20:59","studyDate":"8月1日-1月1日","timeLength":"136","title":"2017年国家公务员考试红领全程套餐A","total":2995,"typeName":"套餐方案"}
     * teacherInfo : [{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300453473100.jpg","teacherId":"2","teacherName":"李委明"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300458302951.jpg","teacherId":"5","teacherName":"蔡金龙"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004552994.jpg","teacherId":"40","teacherName":"魏华刚"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005013215711.jpg","teacherId":"81","teacherName":"罗红军"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"92","teacherName":"张明明"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004530013647.jpg","teacherId":"114","teacherName":"郜爽"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300505513345.jpg","teacherId":"153","teacherName":"袁东"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063004460613566.jpg","teacherId":"157","teacherName":"刘有珍"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1506300459143508.jpg","teacherId":"191","teacherName":"刘子丰"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030901072390.jpg","teacherId":"203","teacherName":"韩利亚"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"218","teacherName":"省丽丽"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005050917949.jpg","teacherId":"353","teacherName":"孔令昂"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"428","teacherName":"胡泊"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030912356288.jpg","teacherId":"445","teacherName":"赵寰宇"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/15063005002111436.jpg","teacherId":"453","teacherName":"张蕊"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1458609619.jpg","teacherId":"500","teacherName":"郝曜华"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"516","teacherName":"贺瑞锐"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"588","teacherName":"马宗亮"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"642","teacherName":"赵晶"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1509110328275406.jpg","teacherId":"655","teacherName":"马兰"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1507030857251248.jpg","teacherId":"717","teacherName":"车春艺"},{"roundPhoto":"http://upload.htexam.net/teacherphoto/1509150352152633.jpg","teacherId":"818","teacherName":"程永乐"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1094","teacherName":"华图老师22"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1112","teacherName":"王凌燕"},{"roundPhoto":"http://v.huatu.com/images/default_teacher.jpg","teacherId":"1117","teacherName":"王鹍 "}]
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ActualPrice : 3180
         * TimeLength : 136
         * Title : 2017年国家公务员考试红领全程套餐A
         * TypeName : 套餐方案
         * actualPrice : 3180
         * isBuy : 0
         * limitStatus : 7
         * limitTimes : 0
         * limitUserCount : 0
         * rid : 52152
         * scaleimg : http://upload.htexam.net/classimg/class/1461910636.jpg
         * startTime : 1970-01-01 08:00:00
         * stopTime : 2016-04-26 16:20:59
         * studyDate : 8月1日-1月1日
         * timeLength : 136
         * title : 2017年国家公务员考试红领全程套餐A
         * total : 2995
         * typeName : 套餐方案
         */

        private ClassInfoBean classInfo;
        /**
         * roundPhoto : http://upload.htexam.net/teacherphoto/1506300453473100.jpg
         * teacherId : 2
         * teacherName : 李委明
         */

        private List<TeacherInfoBean> teacherInfo;

        public ClassInfoBean getClassInfo() {
            return classInfo;
        }

        public void setClassInfo(ClassInfoBean classInfo) {
            this.classInfo = classInfo;
        }

        public List<TeacherInfoBean> getTeacherInfo() {
            return teacherInfo;
        }

        public void setTeacherInfo(List<TeacherInfoBean> teacherInfo) {
            this.teacherInfo = teacherInfo;
        }

        public static class ClassInfoBean {
            public String price;
            private int ActualPrice;
            private String TimeLength;
            private String Title;
            private String TypeName;
            private int actualPrice;
            private int isBuy;
            private int limitStatus;
            private int limitTimes;
            private int limitUserCount;
            private String rid;
            private String scaleimg;
            private String startTime;
            private String stopTime;
            private String studyDate;
            private String timeLength;
            private String title;
            private int total;
            private String typeName;
            private int courseType;
            public int isSuit;
            public String isMianshou;
            public boolean hasTrial;
            public int purchasType;
            public int isTermined;
            public String terminedDesc;
            public int isProvincialFaceToFace;
            public String Province;

            public boolean isHasTrial() {
                return hasTrial;
            }

            public void setHasTrial(boolean hasTrial) {
                this.hasTrial = hasTrial;
            }

            public String getIsMianshou() {
                return isMianshou;
            }

            public void setIsMianshou(String isMianshou) {
                this.isMianshou = isMianshou;
            }

            public int getCourseType() {
                return courseType;
            }

            public void setCourseType(int courseType) {
                this.courseType = courseType;
            }

            public int getActualPrice() {
                return ActualPrice;
            }

            public void setActualPrice(int actualPrice) {
                ActualPrice = actualPrice;
            }

            public int getIsBuy() {
                return isBuy;
            }

            public void setIsBuy(int isBuy) {
                this.isBuy = isBuy;
            }

            public int getLimitStatus() {
                return limitStatus;
            }

            public void setLimitStatus(int limitStatus) {
                this.limitStatus = limitStatus;
            }

            public int getLimitTimes() {
                return limitTimes;
            }

            public void setLimitTimes(int limitTimes) {
                this.limitTimes = limitTimes;
            }

            public int getLimitUserCount() {
                return limitUserCount;
            }

            public void setLimitUserCount(int limitUserCount) {
                this.limitUserCount = limitUserCount;
            }

            public String getRid() {
                return rid;
            }

            public void setRid(String rid) {
                this.rid = rid;
            }

            public String getScaleimg() {
                return scaleimg;
            }

            public void setScaleimg(String scaleimg) {
                this.scaleimg = scaleimg;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getStopTime() {
                return stopTime;
            }

            public void setStopTime(String stopTime) {
                this.stopTime = stopTime;
            }

            public String getStudyDate() {
                return studyDate;
            }

            public void setStudyDate(String studyDate) {
                this.studyDate = studyDate;
            }

            public String getTimeLength() {
                return TimeLength;
            }

            public void setTimeLength(String timeLength) {
                TimeLength = timeLength;
            }

            public String getTitle() {
                return Title;
            }

            public void setTitle(String title) {
                Title = title;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public String getTypeName() {
                return TypeName;
            }

            public void setTypeName(String typeName) {
                TypeName = typeName;
            }
        }

        public static class TeacherInfoBean {
            private String roundPhoto;
            private String teacherId;
            private String teacherName;

            public String getRoundPhoto() {
                return roundPhoto;
            }

            public void setRoundPhoto(String roundPhoto) {
                this.roundPhoto = roundPhoto;
            }

            public String getTeacherId() {
                return teacherId;
            }

            public void setTeacherId(String teacherId) {
                this.teacherId = teacherId;
            }

            public String getTeacherName() {
                return teacherName;
            }

            public void setTeacherName(String teacherName) {
                this.teacherName = teacherName;
            }
        }
    }
}
