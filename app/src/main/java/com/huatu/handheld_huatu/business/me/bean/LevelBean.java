package com.huatu.handheld_huatu.business.me.bean;

import java.util.List;

/**
 * Created by ht on 2017/10/14.
 */
public class LevelBean {
    public int code;
    public LevelData data;
    public String message;
//    code		number	@mock=1000000
//    data		object
//    message		string  @mock=查询成功

    public class LevelData {
        public int level;
        public int diff;
        public int nextlevel;
        public String percent;
        public List<String> privilege;
//        diff		number	@mock=200
//        level		number	@mock=7
//        nextlevel		number	@mock=8
//        percent		string	@mock=80%
//        privilege		array<string>	@mock=$order('购买课程时享8折优惠，且与其他优惠同享','免费获得模考大赛深度解析课程8次，可在直播-我的课程中查看')

    }


}
