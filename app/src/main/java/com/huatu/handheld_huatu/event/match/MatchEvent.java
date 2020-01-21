package com.huatu.handheld_huatu.event.match;

import android.os.Bundle;

public class MatchEvent {

    public String tag;
    public int type;
    public Bundle extraBundle;

    public MatchEvent(int type) {
        this.type = type;
    }

    public static final int GIFT_SCROLL_DOWN = 1;           // scrollView向下滑动
    public static final int GIFT_SCROLL_UP = 2;             // scrollView向上滑动

}
