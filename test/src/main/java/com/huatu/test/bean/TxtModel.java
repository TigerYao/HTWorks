package com.huatu.test.bean;

import java.util.ArrayList;

/**
 * Created by michael on 18/4/16.
 */

public class TxtModel {

    public int lineCount;                                               // 总行数
    public float blanks_w;                                              //
    public int lineHeight;                                              // 行高
    public ArrayList<LineModel> lineModels = new ArrayList<LineModel>();

    public static class LineModel {
        public int lineStart;           // 行首
        public int lineEnd;             // 行末
        public float lineWidth;         // 行宽
        public boolean isScale;         // 是否缩放
        public float d;

        public ArrayList<CharModel> charModels = new ArrayList<CharModel>();
    }

    public static class CharModel {
        public float x;
    }

}
