package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by ljzyuhenda on 16/9/20.
 */
public class VideoCloudDirectoryBean {
    public String code;
    public List<VideoCloudDirectory> data;

    /**
     * "no": "586686",
     * "title": "福建2015年学前教育试卷案例分析50",
     * "vduration": "201",
     * "vimgurl": "http://3.img.bokecc.com/comimage/A7A537A8A57CBB10/2016-09-08/395032EB265EE0019C33DC5901307461-0.jpg"
     */
    public class VideoCloudDirectory {
        public String no;
        public String title;
        public String vduration;
        public String vimgurl;
    }
}
