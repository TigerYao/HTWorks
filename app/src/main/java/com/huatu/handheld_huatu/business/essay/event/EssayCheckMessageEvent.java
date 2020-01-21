package com.huatu.handheld_huatu.business.essay.event;


import com.huatu.handheld_huatu.event.BaseMessageEvent;

/**
 */
public class EssayCheckMessageEvent extends BaseMessageEvent {

    public static final int EssayCheck_start_base = 10000 * 1;


    public static final int EssayCheck_clearview_check = EssayCheck_start_base + 1;

    // get data from net
    public static final int EssayCheck_get_data_from_net = 11000 * 1;
    public static final int EssayCheck_net_getCheckDetail = EssayCheck_get_data_from_net + 1;
    public static final int EssayCheck_get_checkCount = EssayCheck_net_getCheckDetail + 1;
    public static final int EssayCheck_change_video_rate = EssayCheck_get_checkCount + 1;                   // 改变播放速度

    public EssayCheckMessageEvent(int type) {
        this.type = type;
    }

    public EssayCheckMessageEvent() {
    }
}
