package com.huatu.handheld_huatu.business.me.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht on 2017/9/19.
 */
public class OrderLogisticBean  implements Serializable{
    public int code;
    public DataEntityX data;
    public String message;


    public static class DataEntityX implements Serializable {
        public String comName;//承运公司
        public String comTel;//承运公司
        public String condition;
        public String ischeck;
        public String message;
        public String nu;//运单号
        public String state;
        public String status;
        public List<DataEntity> data;

        public static class DataEntity  implements Serializable{

            /**
             * areaCode : 1
             * areaName : 1
             * context : 已签收,签收人是拍照签收
             * ftime : 2016-05-15 12:52:16
             * status : 签收
             * time : 2016-05-15 12:52:16
             */

            public String areaCode;
            public String areaName;
            public String context;
            public String ftime;
            public String status;
            public String time;

        }
    }

//    code		string	@mock=1000000
//    data		object
//    com		string	@mock=zhongtong
//    condition		string	@mock=F00
//    data|11		array<object>
//    areaCode	邮编	string	@mock=$order('CN330726000000','CN330726000000','CN330700000000','CN330700000000','CN330700000000','CN330400000000','CN330400000000','CN110000000000','CN110000000000','CN110000000000','CN110000000000')
//    areaName	地区名字	string	@mock=$order('浙江,金华市,浦江县','浙江,金华市,浦江县','浙江,金华市','浙江,金华市','浙江,金华市','浙江,嘉兴市','浙江,嘉兴市','北京','北京','北京','北京')
//    context	物流信息	string	@mock=$order('【金华市】 快件已在 【浦江】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!','【金华市】 【浦江】 的杨小猫（15355327574） 正在第1次派件, 请保持电话畅通,并耐心等待','【金华市】 快件到达 【浦江】','【金华市】 快件离开 【金华中转部】 发往 【浦江】','【金华市】 快件到达 【金华中转部】','【嘉兴市】 快件离开 【嘉兴中转部】 发往 【金华中转部】','【嘉兴市】 快件到达 【嘉兴中转部】','【北京市】 快件离开 【北京】 发往 【嘉兴中转部】','【北京市】 快件到达 【北京】','【北京市】 快件离开 【北京业务部】 发往 【北京】','【北京市】 【北京业务部】（010-56998362、010-56998340、010-56998368） 的 云仓 （15810711776） 已揽收')
//    ftime	时间	string	@mock=$order('2018-05-28 10:45:52','2018-05-28 08:28:58','2018-05-28 07:57:15','2018-05-28 04:03:02','2018-05-28 03:01:52','2018-05-27 19:55:58','2018-05-27 19:23:22','2018-05-26 20:03:24','2018-05-26 20:01:17','2018-05-26 17:21:13','2018-05-26 14:00:39')
//    status	状态	string	@mock=$order('签收','派件','在途','在途','在途','在途','在途','在途','在途','在途','揽收')
//    time		string	@mock=$order('2018-05-28 10:45:52','2018-05-28 08:28:58','2018-05-28 07:57:15','2018-05-28 04:03:02','2018-05-28 03:01:52','2018-05-27 19:55:58','2018-05-27 19:23:22','2018-05-26 20:03:24','2018-05-26 20:01:17','2018-05-26 17:21:13','2018-05-26 14:00:39')
//    ischeck		string	@mock=1
//    message		string	@mock=ok
//    nu		string	@mock=496785832320
//    state		string	@mock=3
//    status		string	@mock=200

}
