package com.huatu.handheld_huatu.datacache.model;

import java.io.Serializable;

/**
 * Created by michael on 17/8/30.
 */

public class HomeIconBean implements Serializable {

    public String name;         // 名称 ('模考大赛','精准估分','真题演练','错题重练','专项模考'...)
    public int sort;            // 排序
    public String type;         // 类型 ('MKDS','JZGF','ZTYL','CTCL','ZXMK'...)
    public String url;          // 图标url

    public int requestType;     // 类型，本地类型 ArenaConstant
    public int icon;            // 图标，本地资源 R.mipmap...

    public int tipNum;          // 角标

}
