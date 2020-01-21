package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.handheld_huatu.ui.recyclerview.LetterSortAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ht-ldc on 2016/12/20.
 */
public class CourseMineBean implements Serializable {
        public int next;
        public List<ResultBean> result;

        public class ResultBean extends LetterSortAdapter.LetterSortBean  implements Serializable {
            public String ActualPrice;
            public String ClassNo;
            public String NetClassId;
            public int Status;
            public String TeacherDesc;
            public String TimeLength;
            public String endDate;
            public int iszhibo;
            public String limitUserCount;
            public String rid;
            public String scaleimg;
            public String startDate;
            public String title;
            public String validityDate;
            public int isStudyExpired;
            public String orderId;
            public String orderNum;
            public boolean isxianshi;
            private boolean isSelect;// 是否被选中
            public int isSuit;
            public int courseType;
            public int oneToOne;
            public String treatyUrl;
            public String isMianshou;

            @Override
            public String toString() {
                return "ResultBean{" +
                        "ActualPrice='" + ActualPrice + '\'' +
                        ", ClassNo='" + ClassNo + '\'' +
                        ", NetClassId='" + NetClassId + '\'' +
                        ", Status=" + Status +
                        ", TeacherDesc='" + TeacherDesc + '\'' +
                        ", TimeLength='" + TimeLength + '\'' +
                        ", endDate='" + endDate + '\'' +
                        ", iszhibo=" + iszhibo +
                        ", limitUserCount='" + limitUserCount + '\'' +
                        ", rid='" + rid + '\'' +
                        ", scaleimg='" + scaleimg + '\'' +
                        ", startDate='" + startDate + '\'' +
                        ", title='" + title + '\'' +
                        ", validityDate='" + validityDate + '\'' +
                        ", isStudyExpired=" + isStudyExpired +
                        ", orderId='" + orderId + '\'' +
                        ", orderNum='" + orderNum + '\'' +
                        ", isxianshi=" + isxianshi +
                        ", isSelect=" + isSelect +
                        ", isSuit=" + isSuit +
                        ", courseType=" + courseType +
                        ", oneToOne=" + oneToOne +
                        ", treatyUrl='" + treatyUrl + '\'' +
                        ", isMianshou='" + isMianshou + '\'' +
                        '}';
            }

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean isSelect) {
                this.isSelect = isSelect;
            }
        }
}
