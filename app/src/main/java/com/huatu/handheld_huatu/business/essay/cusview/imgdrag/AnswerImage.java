package com.huatu.handheld_huatu.business.essay.cusview.imgdrag;

import java.io.Serializable;

public class AnswerImage implements Serializable {

    // 网络使用变量
    public long id;             // 图片id
    public int sort;            // 排序
    public String imageUrl;     // 图片上传之后的网络路径
    public String content;      // 图片识别后的内容

    // 本地使用变量
    public String originPath;   // 系统图片路径
    public String path;         // 图片压缩后的绝对路径，用做上传

    public int upState;         // 图片上传状态 0、默认状态 1、正在压缩 2、压缩成功 3、正在上传 4、上传成功 -4、上传失败
    public int progress;        // 上传进度
}
