package com.huatu.handheld_huatu.business.essay.bean;

import java.util.ArrayList;

/**
 * Created by chq on 2018/12/15.
 */

public class EssaySearchContent {

    public long groupId;
    public int type;                                        // 单题问题类型
    public String showMsg;

    public String paperName;
    public String areaName;//套题地区
    public long paperId;
    public boolean flag;
    public ArrayList<EssaySearchQuestion> questionList;     // 单题问题
    public ArrayList<EssaySearchArena> areaList;            // 单题问题
    public ArrayList<EssaySearchStem> stemList;             // 套题问题
    public ArrayList<EssaySearchMaterial> materialList;

    public int paperType;                                   // 试卷类型 0、模考 1、真题（非模考）

//单题
//                  "groupId": 592,
//                  "type": 1,
//                  "showMsg": "菜市场1",
//                  "questionList":
    //                materialList
//套题
//     "paperName": "<b>测</b><b>试</b>",
//        "areaId": 2502,
//            "areaName": "贵州",
//            "subAreaId": 0,
//            "subAreaName": "",
//             "paperId": 508,
//             "flag": true,
//             "stemList": [],
//             "materialList": []
}
