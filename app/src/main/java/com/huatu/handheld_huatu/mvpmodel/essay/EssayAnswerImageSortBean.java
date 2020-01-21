package com.huatu.handheld_huatu.mvpmodel.essay;

import java.util.ArrayList;

public class EssayAnswerImageSortBean {

    public long answerId;
    public ArrayList<ImageSort> imageList;

    public static class ImageSort{
        public long imageId;
        public int sort;
    }
}
