package com.huatu.handheld_huatu.business.me.bean;

import java.util.ArrayList;

/**
 * Created by chq on 2018/7/30.
 */

public class MyV5Order {
    public int current_page;      //	当前页码
    public ArrayList<MyV5OrderContent> data;
    public int from;
    public int last_page;    //最大页码
    public String next_page_url;
    public String path;
//    public String per_page;   //页长
//    public String prev_page_url;
    public int to;
    public int total;   //总条数

}
//    current_page	当前页码	number	@mock=1
//        data		array<object>
//    from		number	@mock=1
//        last_page	最大页码	number	@mock=5
//        next_page_url		string	@mock=http://localhost:1234/v4/common/order/list?page=2
//        path		string	@mock=http://localhost:1234/v4/common/order/list
//        per_page	页长	number	@mock=10
//        prev_page_url		number	@mock=1
//        to		number	@mock=10
//        total	总条数	number	@mock=44

