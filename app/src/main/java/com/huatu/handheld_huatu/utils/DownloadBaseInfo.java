package com.huatu.handheld_huatu.utils;

/**
 * Created by saiyuan on 2016/11/17.
 */

public class DownloadBaseInfo {
    public DownloadBaseInfo(String url) {
        downloadUrl = url;
    }
    /** 下载的链接 */
    public String downloadUrl = "";
    /** 名字 */
    public String name = "";
    /**MD5 */
    public String md5 = "";
    /**文件大小*/
    public long fileSize;
    /**本地文件的存放地址*/
    public String filePath;
}
