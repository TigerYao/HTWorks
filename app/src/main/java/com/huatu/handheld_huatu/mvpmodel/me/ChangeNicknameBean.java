package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 修改昵称后返回的数据
 */
public class ChangeNicknameBean {

    private int code;
    private ChangeData data;

    @Override
    public String toString() {
        return "ChangeNicknameBean{" +
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

    public ChangeData getData() {
        return data;
    }

    public void setData(ChangeData data) {
        this.data = data;
    }

    public class ChangeData {
        private long id;
        private int status;
        private String email;
        private String nick;
        private String uname;
        private long expireTime;
        private String token;
        private int subject;
        private int area;

        @Override
        public String toString() {
            return "ChangeData{" +
                    "id=" + id +
                    ", status=" + status +
                    ", email='" + email + '\'' +
                    ", nick='" + nick + '\'' +
                    ", uname='" + uname + '\'' +
                    ", expireTime=" + expireTime +
                    ", token='" + token + '\'' +
                    ", subject=" + subject +
                    ", area=" + area +
                    '}';
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getSubject() {
            return subject;
        }

        public void setSubject(int subject) {
            this.subject = subject;
        }

        public int getArea() {
            return area;
        }

        public void setArea(int area) {
            this.area = area;
        }
    }
}
