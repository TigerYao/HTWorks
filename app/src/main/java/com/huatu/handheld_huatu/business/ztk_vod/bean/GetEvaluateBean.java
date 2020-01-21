package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/16.
 */

public class GetEvaluateBean implements Serializable{


    /**
     * code : 1000000
     * data : {"_id":{"$id":"59b8d3ed6d3282141b000029"},"classid":"54892","courseRemark":"喜欢听这个老师讲课","coursescore":10,"lessionid":"839887","photo_url":"http://upload.htexam.net/teacherphoto/1454379050.jpg","teacherid":"990","teachername":"欧阳秀","type":2,"userid":"6287644","username":"htwx_6287644"}
     * message :
     */

    private int code;
    /**
     * _id : {"$id":"59b8d3ed6d3282141b000029"}
     * classid : 54892
     * courseRemark : 喜欢听这个老师讲课
     * coursescore : 10
     * lessionid : 839887
     * photo_url : http://upload.htexam.net/teacherphoto/1454379050.jpg
     * teacherid : 990
     * teachername : 欧阳秀
     * type : 2
     * userid : 6287644
     * username : htwx_6287644
     */

    private DataBean data;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * $id : 59b8d3ed6d3282141b000029
         */

        private IdBean _id;
        private String classid;
        private String courseRemark;
        private int coursescore;
        private String lessionid;
        private String photo_url;
        private String teacherid;
        private String teachername;
        private int type;
        private String userid;
        private String username;
        private String lessiontitle;

        public String getLessiontitle() {
            return lessiontitle;
        }

        public void setLessiontitle(String lessiontitle) {
            this.lessiontitle = lessiontitle;
        }

        public IdBean get_id() {
            return _id;
        }

        public void set_id(IdBean _id) {
            this._id = _id;
        }

        public String getClassid() {
            return classid;
        }

        public void setClassid(String classid) {
            this.classid = classid;
        }

        public String getCourseRemark() {
            return courseRemark;
        }

        public void setCourseRemark(String courseRemark) {
            this.courseRemark = courseRemark;
        }

        public int getCoursescore() {
            return coursescore;
        }

        public void setCoursescore(int coursescore) {
            this.coursescore = coursescore;
        }

        public String getLessionid() {
            return lessionid;
        }

        public void setLessionid(String lessionid) {
            this.lessionid = lessionid;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getTeacherid() {
            return teacherid;
        }

        public void setTeacherid(String teacherid) {
            this.teacherid = teacherid;
        }

        public String getTeachername() {
            return teachername;
        }

        public void setTeachername(String teachername) {
            this.teachername = teachername;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public static class IdBean {
            private String $id;

            public String get$id() {
                return $id;
            }

            public void set$id(String $id) {
                this.$id = $id;
            }
        }
    }
}
