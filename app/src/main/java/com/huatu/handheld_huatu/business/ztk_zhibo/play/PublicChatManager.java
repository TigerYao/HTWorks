package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import com.huatu.handheld_huatu.business.ztk_zhibo.play.chat.ChatManager;

@Deprecated
public class PublicChatManager extends ChatManager {
	private static PublicChatManager publicChatManager = null;

	public static PublicChatManager getIns() {
		synchronized (PublicChatManager.class) {
			if (null == publicChatManager) {
				publicChatManager = new PublicChatManager();
			}
		}
		return publicChatManager;
	}
}
