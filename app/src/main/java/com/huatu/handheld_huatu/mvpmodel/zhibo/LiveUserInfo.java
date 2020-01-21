package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;

public class LiveUserInfo {
    public String userNumber;
    public String userNick;
    public String userAvatar;

    public boolean  hasUserModel(){
        if (Utils.isEmptyOrNull(userNumber))
            return false;
        return true;
    }

}
