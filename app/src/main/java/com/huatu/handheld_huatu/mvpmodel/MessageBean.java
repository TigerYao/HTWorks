package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by ljzyuhenda on 16/7/21.
 */
public class MessageBean {
    public String code;
    public MessageBeanResult data;

    public class MessageBeanResult{
        public long cursor;
        public long total;
        public List<MessageBeanData> resutls;
    }

    /**
     * id	long	消息id
     * uid	long	信息所属用户
     * titile	string	消息标题
     * content	string	消息内容
     * type	int	消息类型 1:系统消息 3:个人消息
     * status	int	消息状态，新消息（未读消息）：1，旧消息：0
     * createTime	long	创建时间
     */
    public class MessageBeanData {
        public long id;
        public long uid;
        public String title;
        public String content;
        public int type;
        public int status;
        public String createTime;
    }
}
