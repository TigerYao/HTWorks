package com.huatu.handheld_huatu.mvpmodel.arena;

import java.io.Serializable;

/**
 * Created by dongd on 2016/10/17.
 */

public class AthleticsRequest implements Serializable{
    //发送的请求指令
    public int action;
    //请求和响应的标示,以此来让请求和响应对应起来
    public String ticket;
    //请求需要传递的参数
    public Param params=new Param();

    public class Param {
        public String token;
        //竞技模块id
        public int moduleId;
    }
}
