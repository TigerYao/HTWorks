package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;

public class Exposition{
        public String title;
        public String count;
        public String expireDate;
        public String getTitleEx(){
            return Utils.isEmptyOrNull(count) ? title : title + "  x"+count;
        }
    }