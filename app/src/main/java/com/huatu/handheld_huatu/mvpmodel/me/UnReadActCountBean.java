package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 活动中心未读消息的个数
 */
public class UnReadActCountBean {

    private int code;
    private UnReadActNum data;

    @Override
    public String toString() {
        return "UnReadActCountBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UnReadActNum getData() {
        return data;
    }

    public void setData(UnReadActNum data) {
        this.data = data;
    }

    public class UnReadActNum {
        private int unreadActCount;

        @Override
        public String toString() {
            return "UnReadActNum{" +
                    "unreadActCount=" + unreadActCount +
                    '}';
        }

        public int getUnreadActCount() {
            return unreadActCount;
        }

        public void setUnreadActCount(int unreadActCount) {
            this.unreadActCount = unreadActCount;
        }
    }
}
