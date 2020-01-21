package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-djd on 2017/9/16.
 *
 */

public class TeacherPingjiaBean implements Serializable {

    /**
     * code : 1000000
     * message : 查询成功
     * data : {"next":1,"result":{"teacherid":"1","teachername":"顾斐","classid":"42613","photo_url":"http://upload.htexam.net/teacherphoto/1204110823361462.jpg","lessionid":"651260","lessiontitle":"【资料分析】第4课:结构阅读","score":"（暂无评分）","star":0,"evaluation":[{"username":"张玉","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 15:49"},{"username":"fanyanyan2011","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 17:51"}]}}
     */

    private int code;
    private String message;
    /**
     * next : 1
     * result : {"teacherid":"1","teachername":"顾斐","classid":"42613","photo_url":"http://upload.htexam.net/teacherphoto/1204110823361462.jpg","lessionid":"651260","lessiontitle":"【资料分析】第4课:结构阅读","score":"（暂无评分）","star":0,"evaluation":[{"username":"张玉","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 15:49"},{"username":"fanyanyan2011","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 17:51"}]}
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int next;
        /**
         * teacherid : 1
         * teachername : 顾斐
         * classid : 42613
         * photo_url : http://upload.htexam.net/teacherphoto/1204110823361462.jpg
         * lessionid : 651260
         * lessiontitle : 【资料分析】第4课:结构阅读
         * score : （暂无评分）
         * star : 0
         * evaluation : [{"username":"张玉","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 15:49"},{"username":"fanyanyan2011","coursescore":10,"courseRemark":"此用户没有填写评论。","star":10,"rateDate":"2014.09.18 17:51"}]
         */

        private ResultBean result;

        public int getNext() {
            return next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        public ResultBean getResult() {
            return result;
        }

        public void setResult(ResultBean result) {
            this.result = result;
        }

        public static class ResultBean {
            private String teacherid;
            private String teachername;
            private String classid;
            private String photo_url;
            private String lessionid;
            private String lessiontitle;
            private String score;
            private int star;
            private int  flag;
            /**
             * username : 张玉
             * coursescore : 10
             * courseRemark : 此用户没有填写评论。
             * star : 10
             * rateDate : 2014.09.18 15:49
             */

            private ArrayList<EvaluationBean> evaluation;

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

            public String getClassid() {
                return classid;
            }

            public void setClassid(String classid) {
                this.classid = classid;
            }

            public String getPhoto_url() {
                return photo_url;
            }

            public void setPhoto_url(String photo_url) {
                this.photo_url = photo_url;
            }

            public String getLessionid() {
                return lessionid;
            }

            public void setLessionid(String lessionid) {
                this.lessionid = lessionid;
            }

            public String getLessiontitle() {
                return lessiontitle;
            }

            public void setLessiontitle(String lessiontitle) {
                this.lessiontitle = lessiontitle;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public int getStar() {
                return star;
            }

            public void setStar(int star) {
                this.star = star;
            }
            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public ArrayList<EvaluationBean> getEvaluation() {
                return evaluation;
            }

            public void setEvaluation(ArrayList<EvaluationBean> evaluation) {
                this.evaluation = evaluation;
            }

            public static class EvaluationBean {
                private String username;
                private int coursescore;
                private String courseRemark;
                private int star;
                private String rateDate;

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }

                public int getCoursescore() {
                    return coursescore;
                }

                public void setCoursescore(int coursescore) {
                    this.coursescore = coursescore;
                }

                public String getCourseRemark() {
                    return courseRemark;
                }

                public void setCourseRemark(String courseRemark) {
                    this.courseRemark = courseRemark;
                }

                public int getStar() {
                    return star;
                }

                public void setStar(int star) {
                    this.star = star;
                }

                public String getRateDate() {
                    return rateDate;
                }

                public void setRateDate(String rateDate) {
                    this.rateDate = rateDate;
                }
            }
        }
    }
}
