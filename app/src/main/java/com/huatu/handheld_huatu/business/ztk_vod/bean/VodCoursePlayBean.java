package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht-djd on 2017/9/13.
 *
 */
@Deprecated
public class VodCoursePlayBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;

    public static  class DataBean {
        public ArrayList<LessionBean> lession;
        public ArrayList<LessionBean> live;
        public CourseBean course;
        public QQBean QQ;
    }
    public static class LessionBean implements  Serializable{

        public String serviceType; //直播服务类型
        public int isComment;      //是否评价
        public String rid;        //课程id
        public String cc_key;    //cc key
        public int nowTime;       //正在直播时间
        public String timeLength; //课时
        public String password;  //直播加入密码
        public String JoinCode; //直播加入码
        public String teacher;    //课件老师名称
        public String bjyVideoId; //百家云vid
        public String playerType;  //1百家云
        public String bjyRoomId;  //百家云直播房间号
        public String startTime; //直播开始时间
        public float    process;
        public String Title; //课程title
        public String url;        //路径

        public String token;       //百家云视频token
        public String classid; //视听课件课程id
        public int haszhibo;     //是否有直播

        public String bjyCode; //百家云直播加入码
        public String fileSize;  //视频大小

        public String bjySessionId;//百家云回放

        public String cc_vid;    //cc vid
        public String cc_uid;    //cc uid


        public int status;   //课程状态 /*  为2的时候为回放但放在录播里面  -2为录播*/

        public String username;   //用户名


        public int downStatus;//下载状态
        private boolean isSelect;// 是否被选中


        public int    hasTeacher;

       

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getBjyCode() {
            return bjyCode;
        }

        public void setBjyCode(String bjyCode) {
            this.bjyCode = bjyCode;
        }

        public String getBjyVideoId() {
            return bjyVideoId;
        }

        public void setBjyVideoId(String bjyVideoId) {
            this.bjyVideoId = bjyVideoId;
        }

        public String getBjyRoomId() {
            return bjyRoomId;
        }

        public void setBjyRoomId(String bjyRoomId) {
            this.bjyRoomId = bjyRoomId;
        }

        public String getBjySessionId() {
            return bjySessionId;
        }

        public void setBjySessionId(String bjySessionId) {
            this.bjySessionId = bjySessionId;
        }

        public String getPlayerType() {
            return playerType;
        }

        public void setPlayerType(String playerType) {
            this.playerType = playerType;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getIsComment() {
            return isComment;
        }

        public void setIsComment(int isComment) {
            this.isComment = isComment;
        }


        public int getNowTime() {
            return nowTime;
        }

        public void setNowTime(int nowTime) {
            this.nowTime = nowTime;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getJoinCode() {
            return JoinCode;
        }

        public void setJoinCode(String joinCode) {
            JoinCode = joinCode;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getCc_key() {
            return cc_key;
        }

        public void setCc_key(String cc_key) {
            this.cc_key = cc_key;
        }

        public String getCc_uid() {
            return cc_uid;
        }

        public void setCc_uid(String cc_uid) {
            this.cc_uid = cc_uid;
        }

        public String getCc_vid() {
            return cc_vid;
        }

        public void setCc_vid(String cc_vid) {
            this.cc_vid = cc_vid;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public int getHaszhibo() {
            return haszhibo;
        }

        public void setHaszhibo(int haszhibo) {
            this.haszhibo = haszhibo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTimeLength() {
            return timeLength;
        }

        public void setTimeLength(String timeLength) {
            this.timeLength = timeLength;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getDownStatus() {
            return downStatus;
        }

        public void setDownStatus(int downStatus) {
            this.downStatus = downStatus;
        }

        @Override
        public String toString() {
            return "LessionBean{" +
                    "Title='" + Title + '\'' +
                    ", status=" + status +
                    ", JoinCode='" + JoinCode + '\'' +
                    ", serviceType='" + serviceType + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", cc_key='" + cc_key + '\'' +
                    ", cc_uid='" + cc_uid + '\'' +
                    ", cc_vid='" + cc_vid + '\'' +
                    ", fileSize='" + fileSize + '\'' +
                    ", haszhibo=" + haszhibo +
                    ", password='" + password + '\'' +
                    ", timeLength='" + timeLength + '\'' +
                    ", rid='" + rid + '\'' +
                    ", url='" + url + '\'' +
                    ", username='" + username + '\'' +
                    ", nowTime=" + nowTime +
                    ", isComment=" + isComment +
                    ", downStatus=" + downStatus +
                    ", isSelect=" + isSelect +
                    ", bjyCode='" + bjyCode + '\'' +
                    ", bjyVideoId='" + bjyVideoId + '\'' +
                    ", bjyRoomId='" + bjyRoomId + '\'' +
                    ", bjySessionId='" + bjySessionId + '\'' +
                    ", playerType='" + playerType + '\'' +
                    ", token='" + token + '\'' +
                    ", teacher='" + teacher + '\'' +
                    ", classid='" + classid + '\'' +
                    '}';
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean isSelect) {
            this.isSelect = isSelect;
        }
    }
    public static class CourseBean implements Serializable{
        public String NetClassId;
        public String SubjectName;
        public String TeacherDesc;
        public String TimeLength;
        public String TypeName;
        public String scaleimg;
        public String title;
        public int free;

        @Override
        public String toString() {
            return "CourseBean{" +
                    "NetClassId='" + NetClassId + '\'' +
                    ", SubjectName='" + SubjectName + '\'' +
                    ", TeacherDesc='" + TeacherDesc + '\'' +
                    ", TimeLength='" + TimeLength + '\'' +
                    ", TypeName='" + TypeName + '\'' +
                    ", scaleimg='" + scaleimg + '\'' +
                    ", title='" + title + '\'' +
                    ", free=" + free +
                    '}';
        }
    }
    public static class QQBean {
        public String AndroidFunction;
        public String IosFunction;
        public String QQnum;

        @Override
        public String toString() {
            return "QQBean{" +
                    "AndroidFunction='" + AndroidFunction + '\'' +
                    ", IosFunction='" + IosFunction + '\'' +
                    ", QQnum='" + QQnum + '\'' +
                    '}';
        }
    }
}
