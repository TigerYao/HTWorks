package com.huatu.handheld_huatu.mvpmodel.matchs;

import java.util.List;

/**
 * Created by ht on 2018/2/9.
 */
public class UserMate {
        public long currentPracticeId;//未完成的答题卡id
        public int finishCount;//完成次数
        public String id;//用户统计数据id
        public long paperId;//试卷id
        public List<Long> practiceIds;//完成的答题卡id集合
        public long uid;//用户id


//        currentPracticeId	未完成的答题卡id	number	@mock=-1
//        finishCount	完成次数	number	@mock=1
//        id	用户统计数据id	string	@mock=233906356_3526939
//        paperId	试卷id	number	@mock=3526939
//        practiceIds	完成的答题卡id集合	array<number>	@mock=1854839137824145400
//        uid	用户id number	@mock=233906356
}
