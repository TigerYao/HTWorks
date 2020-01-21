package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.HashMap;

public class VideoPlayStaticInfo {

    public int type;
    public long startTime;
    public long endTime;
    public String speed;//速度
    public String clarity;//清晰度
    public int errorId;//错误码
    public String describe;//错误信息
    public int videoType;//视频类型
    public String courseId;
    public int syllabusId;
    public long timestamp;
    public int level;
    private HashMap<String, String> params;

    public VideoPlayStaticInfo(int type, int videoType, String courseId, int syllabusId) {
        this.type = type;
        this.videoType = videoType;
        this.courseId = courseId;
        this.syllabusId = syllabusId;
    }

    public HashMap<String, String> getEventStatiscString() {
        params = new HashMap<>();
        params.put("type", type + "");
        params.put("courseId", courseId);
        params.put("syllabusId", syllabusId + "");
        params.put("videoType", videoType + "");
        params.put("timestamp", (System.currentTimeMillis() / 1000)+"");
        params.put("endTime", endTime+"");
        switch (type) {
            case 1://停止事件
            case 2://快进事件
            case 3://快退事件
                params.put("startTime", startTime+"");
                break;
            case 4://速率
                params.put("speed", speed+"");
                break;
            case 6://清晰度
                params.put("clarity", clarity+"");
                break;
            case 7:
                params.put("level", level+"");
                break;
            case 11://错误
                params.put("errorId", errorId+"");
                params.put("describe", describe);
                break;
        }
        params.put("token", SpUtils.getToken());
        return params;
    }
}
