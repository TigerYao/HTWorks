package com.huatu.handheld_huatu.business.essay.cusview.imgdrag;

import java.io.Serializable;

/**
 * 图片上上传后的返回结果
 */
public class ImageUpResult implements Serializable {

    // 网络使用变量
    public long id;             // 图片id
    public int sort;            // 排序
    public String url;          // 图片上传之后的网络路径
    public String content;      // 图片识别后的内容

}
