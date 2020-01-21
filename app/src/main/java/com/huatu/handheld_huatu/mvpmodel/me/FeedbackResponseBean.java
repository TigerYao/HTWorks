package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 意见反馈返回数据
 */
public class FeedbackResponseBean {

    public int code;
    public FeedbackData data;

    @Override
    public String toString() {
        return "FeedbackResponseBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    class FeedbackData {
        public String message;

        @Override
        public String toString() {
            return "FeedbackData{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }
}
