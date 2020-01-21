package com.huatu.chat;

import android.app.Activity;

/**
 * Created by Administrator on 2019\10\16 0016. Provider
 */

public interface XiaoNengProvider {

    void init(Activity activity);

    void startChat(String groupId, String courseTitle, String goodsid);

    void logout();
}
