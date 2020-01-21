package com.huatu.autoapi.auto_api;

import android.content.Context;
import com.netease.libs.auto_api.ApiBase;
import java.lang.String;

/**
 * com.huatu.handheld_huatu.StartChatUtil's api Interface
 */
public interface SobotChatHelper extends ApiBase {
  int startAsk(String consultInfo, String customGroupId, String titleName, Context context, String useId, String usename, String mobile, String avater, String areaName);

  int startTalk(String customGroupId, String titleName, Context context, String useId, String usename, String mobile, String avater, String areaName);

  int talkerAfter(boolean switchPerson, Context context, String useId, String usename, String mobile, String avater, String areaName);
}
