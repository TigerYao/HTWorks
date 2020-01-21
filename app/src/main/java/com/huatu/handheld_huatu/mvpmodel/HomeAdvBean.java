package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 首页可配置的广告列表
 */
public class HomeAdvBean implements Serializable {
    /**
     * {
     *         "customizeDoc": {
     *             "defaultDoc": 1,
     *             "document": "专项练习",
     *             "offLineTime": 1577089804000,
     *             "onLineTime": 1572769804000
     *         },
     *         "homeOperateDoc": {
     *             "defaultDoc": 1,
     *             "document": "为你推荐",
     *             "offLineTime": 1577089804000,
     *             "onLineTime": 1572769804000
     *         },
     *         "messageList": [
     *             {
     *                 "offLineTime": 1577462400000,
     *                 "onLineTime": 1575043200000,
     *                 "params": {
     *                     "cateId": 0,
     *                     "id": 351,
     *                     "image": "http://tiku.huatu.com/cdn/pandora/img/4ba2ecbd-5ec3-484b-a351-55437bfd84f0..png",
     *                     "padImageUrl": "",
     *                     "subject": 0,
     *                     "title": "测试首页运营位"
     *                 },
     *                 "target": "ztk://exam/articles",
     *                 "type": 6
     *             }
     *         ]
     *     }
     */

    public HomeAdvTitle customizeDoc;
    public HomeAdvTitle homeOperateDoc;
    public ArrayList<AdvertiseConfig> messageList;

    public static class HomeAdvTitle implements Serializable{
        public String defaultDoc;
        public String document;
        public long offLineTime;
        public long onLineTime;
    }

}
