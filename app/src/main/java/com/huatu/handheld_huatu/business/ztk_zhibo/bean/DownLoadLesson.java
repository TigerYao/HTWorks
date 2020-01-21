package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 课件Bean Created by DongDong on 2016/4/7.
 */
public class DownLoadLesson implements Serializable {
    public static final String ID = "_id";
    public static final String DOWNLOAD_ID = "download_ID";
    public static final String SUBJECT_ID = "subject_ID";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String IMAGE_PATH = "image_path";
    public static final String PLAY_PATH = "play_path";
    public static final String PLAY_PROGRESS = "play_progress";
    public static final String DOWN_STATUS = "down_status";
    public static final String COURSE_ID = "course_ID";
    public static final String COURSE_NUM = "courseNum";
    public static final String DOWNLOAD_PROGRESS = "download_progress";
    public static final String SPACE = "space";
    public static final String DURATION = "duration";
    public static final String PLAY_DURATION = "play_duration";
    public static final String LESSON = "lesson";
    public static final String VIDEO_CLARITY = "lesson_clarity";
    public static final String ENCRYPT_TYPE = "encrypt_type";
    public static final String SIGNAL_FILE_PATH = "signal_file_path";
    public static final String ROOM_ID = "room_id";
    public static final String SESSION_ID = "session_id";
    public static final String VIDEO_TOKEN = "video_token";
    public static final String PLAYER_TYPE = "player_type";
    public static final String PLAY_PARAMS = "play_params";
    public static final String REVERSE_1 = "reserve1";
    public static final String REVERSE_2 = "reserve2";

    public static final String USER_ID ="user_id";
    public static final String CATALOG_ID ="catalog_id";
    public static final String PARENT_ID ="parent_id";


    private String downloadID;
    private String subject_ID;
    private String subject_name;
    private String image_path;
    private String play_path;
    private int play_progress;
    private int down_status;
    private String domain;
    private String liveID;
    private String password;
    private int courseNum;
    private String course_ID;
    private long space;
    private boolean isSelect;
    private int duration;
    private int play_duration;
    private int lesson;
    private String signalFilePath;
    private int encryptType;//加密类型 0 不加密，1加密
    private int clarity; //视频清晰度 0普清 1高清 2超清 3 720p 4 1080p
    private String videoToken;
    private int playerType;
    private String roomId;
    private String sessionId;
    private String playParams;
    private String reserve1;
    private String reserve2;
    private int downloadProgress;
    private long userId;
    //public int oldDownStatus;

    public int catalogId;
    public int parentId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getPlayParams() {
        return playParams;
    }

    public void setPlayParams(String playParams) {
        this.playParams = playParams;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public String getVideoToken() {
        return videoToken;
    }

    public void setVideoToken(String videoToken) {
        this.videoToken = videoToken;
    }

    public String getSignalFilePath() {
        return signalFilePath;
    }

    public void setSignalFilePath(String signalFilePath) {
        this.signalFilePath = signalFilePath;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public int getClarity() {
        return clarity;
    }

    public void setClarity(int clarity) {
        this.clarity = clarity;
    }

    public int getLesson() {
        return lesson;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPlay_duration() {
        return play_duration;
    }

    public void setPlay_duration(int play_duration) {
        this.play_duration = play_duration;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public String getCourseID() {
        return course_ID;
    }

    public String getRealCourseID() {
        if(TextUtils.isEmpty(course_ID)) return course_ID;
        return course_ID.contains("_") ? course_ID.split("_")[1]:course_ID;
    }

    public void setCourseID(String course_ID) {
        this.course_ID = course_ID;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLiveID() {
        return liveID;
    }

    public void setLiveID(String liveID) {
        this.liveID = liveID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String download_ID) {
        this.downloadID = download_ID;
    }

    public String getSubjectID() {
        return subject_ID;
    }

    public void setSubjectID(String subject_ID) {
        this.subject_ID = subject_ID;
    }

    public String getSubjectName() {
        return subject_name;
    }

    public void setSubjectName(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getImagePath() {
        return image_path;
    }

    public void setImagePath(String image_path) {
        this.image_path = image_path;
    }

    public String getPlayPath() {
        return play_path;
    }

    public void setPlayPath(String play_path) {
        this.play_path = play_path;
    }

    public int getPlayProgress() {
        return play_progress;
    }

    public void setPlayProgress(int play_progress) {
        this.play_progress = play_progress;
    }

    public int getDownStatus() {
        return down_status;
    }

    public void setDownStatus(int down_status) {
        this.down_status = down_status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userid) {
        this.userId = userid;
    }

    public int classId;//不参与数据存储
}
