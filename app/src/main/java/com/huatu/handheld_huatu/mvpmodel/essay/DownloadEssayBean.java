package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;

/**
 * Created by michael on 17/11/23.
 */

public class DownloadEssayBean implements Serializable {

    public static final int DOWNLOAD_WAIT = -1;
    public static final int DOWNLOAD_ING = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_FAIL = 3;

    public String title;                    // 试卷名称 / 试卷名称 + 地区名称
    public int downtype;                    // 试卷类型 0、单题 1、套题 3、文章写作
    private int status;
    public String downloadUrl;              // 下载路径
    public int downProgress;                // 下载进度
    public String fileSize;                 // 文件大小
    public String time;                     // 下载时间
    public String filepath = "";            // 试卷路径
    public boolean isSingle;                // 是否单题
    public boolean isStartToCheckDetail;    // 是报告 还是 试卷（未批改）
    public int check;                       // 0、试卷（未批改）1、报告
    public long downLoadId;                 // SingleQuestionDetailBean.questionBaseId / PaperQuestionDetailBean.essayPaper.paperId
    public int type;                        // 试卷类型 0、默认 1、课后作业

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DownloadEssayBean other = (DownloadEssayBean) obj;
        if (title != null && title.equals(other.title)) {
            if (downloadUrl != null && downloadUrl.equals(other.downloadUrl)) {
//                if (fileSize != null && fileSize.equals(other.fileSize)) {
                if (check == other.check) {
                    return true;
                }
//                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (title != null) {
            result = prime * result + title.hashCode();
        }

        if (downloadUrl != null) {
            result = prime * result + downloadUrl.hashCode();
        }

//        if(fileSize!=null) {
//            result = prime * result + fileSize.hashCode();
//        }

        result = prime * result + check;
        return result;
    }
}
