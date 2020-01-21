package com.huatu.handheld_huatu.mvpmodel.essay;

import com.huatu.utils.StringUtils;

import java.io.Serializable;

public class AnswerComment implements Serializable{

    /**
     *                 "questionId":1126,
     *                 "answerComment":"<p>       1.电商平台的出现和普及，改变了农产品销售方式，拓宽了销售渠道。<br/>       2.村镇居民收入增加，生活水平提高。<br/>       3.村民对文化生活有新的期待，希望有跳舞、下棋的娱乐场所。<br/>       4.村镇的交通、居住的生活环境得到了改善。<br/>       5.人口流向发生改变，回乡创业打工的人越来越多。<br/>       6.资金流向发生改变，城里往乡镇的投资越来越多。<br/>       7.城镇居民观念转变，对农村未来的发展信心更足，相信生活会越来越好。</p>",
     *                 "topic":"",
     *                 "subTopic":"",
     *                 "callName":"",
     *                 "inscribedDate":"",
     *                 "inscribedName":"",
     *                 "answerFlag":1,
     *                 "id":2399,
     *                 "bizStatus":1,
     *                 "status":1,
     *                 "gmtCreate":"2018-12-02 23:12:36",
     *                 "gmtModify":"2019-02-01 15:03:52"
     */

    public String answerComment;        // 答案内容	string
    public int answerFlag;              // 答案标识（0 参考答案 1标准答案）	number
    public String callName;             // 称呼	string
    public String inscribedDate;        // 落款日期	string
    public String inscribedName;        // 落款名称	string
    public String subTopic;             // 子标题	string
    public String topic;                // 标题

    public String formatStandAnswer(){
        String content = "";
//        setTopView(topic, topicStr);
//        setTopView(subTopic, subTopicStr);
//        setTopView(callName, callNameStr);

        AnswerComment answerComment=this;
        if (!StringUtils.isEmpty(answerComment.topic)) {
            content += ("<p style=\"text-align:center;\">" + answerComment.topic + "</p>");
        }
        if (!StringUtils.isEmpty(answerComment.subTopic)) {
            content += ("<p style=\"text-align:center;\">" + answerComment.subTopic + "</p>");
        }
        if (!StringUtils.isEmpty(answerComment.callName)) {
            content += (answerComment.callName + "<br/>");
        }
        if (!StringUtils.isEmpty(answerComment.answerComment)) {
            content += (answerComment.answerComment);
        }

        if (!StringUtils.isEmpty(answerComment.inscribedName)) {
            content += ("<p style=\"text-align:end;\">" + answerComment.inscribedName + "</p>");
        }
        if (!StringUtils.isEmpty(answerComment.inscribedDate)) {
            content += ("<p style=\"text-align:end;\">" + answerComment.inscribedDate + "</p>");
        }
        return content;

    }
}
