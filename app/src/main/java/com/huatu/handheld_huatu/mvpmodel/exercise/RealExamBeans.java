package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;
import java.util.List;

/**
 * 真题
 * Created by KaelLi on 2016/7/8.
 */
public class RealExamBeans {
    public RealExamBean data;
    public String code;
    public String message;

    public static class RealExamBean implements Serializable {
        public PaperBean paper;                 // 试卷信息
        public CardUserMetaBean cardUserMeta;   // 用户排名分数信息,只有当提交卷子后才会有,并且只存在于真题演练和模考估分
        public long id;                         // 答题卡id
        public long practiceId;                         // 答题卡id(阶段考试 同 id)
        public long paperId;                    // 试卷id
        public String userId;                   // 用户id
        public int subject;                     // 公务员...
        public int catgory;                     // 申论...
        public double score;                    // 得分
        public String scoreStr;                 // String得分
        public double difficulty;
        public String name;                     // 考试名称
        public int rcount;
        public int wcount;
        public int ucount;
        public int qcount;
        public int status;                      // 试卷状态 1:新建,2:未做完,3:已做完,4:删除
        // 答题卡类型
        // 0    类型不限
        // 1    智能刷题
        // 2    专项练习
        // 3    真题演练
        // 4    智能模考
        // 5    竞技练习
        // 6    错题重练
        // 7    每日特训
        // 8    收藏练习
        // 9    专项模考 (模考估分)
        // 10   砖超联赛
        // 11   微信练习
        // 12   模考大赛
        // 13   精准估分(模考估分)
        // 14   往期模考
        public int type;                        // 答题类型
        public int terminal;
        public int expendTime;                  // 做了多长时间
        public int speed;
        public long createTime;                 // 创建时间
        public int lastIndex;                   // 最新做到的题的下标,从0开始
        public List<Integer> corrects;          // 对应的题的答题结果(是否答对). 0:未做答,1:正确,2:错误
        public List<Integer> answers;           // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
        public List<Integer> times;             // 对应每道题的答题时间,单位:秒
        public List<PointBean> points;          // 知识点
        public int remainingTime;               // 考试剩余时间,真题演练和模考才会有,单位:秒
        public int remainTime;                  // 考试剩余时间,真题演练和模考才会有,单位:秒（阶段测试是这个字段）
        public int recommendedTime;             // 推荐用时
        public long startTime;                       // 试卷开始时间
        public long endTime;                         // 试卷结束时间
        public List<ModuleBean> modules;             // 模块列表
        public List<Integer> doubts;            // 答题卡疑问  1:用户对该试题有疑问,0:没有疑问
        public ScMatchMetaBean matchMeta;       // 模考大赛用户排名分数信息,只存在于模考大赛

        public String rightImgUrl;              // 右侧礼包图片地址
        public int hasGift;                     // 是否有礼包
        public String giftImgUrl;               // 大礼包图片
        public String giftHtmlUrl;              // 锦鲤包(加油包)地址
        public int hasGetBigGift;               // 0 尚未领取 ，1是已经领取
        public String addGroupUrl;              // 类型

        public String matchErrorPath;           // 新模考大赛，交卷时候，调用的答应日志地址

        //-------------------------存到这里，因为在页面被回收的时候，都一起存入onSaveInstanceState
        public long backGroundTime = 0;         // 进入后台时间
        //-------------------------
    }
}
