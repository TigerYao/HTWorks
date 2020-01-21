package com.huatu.handheld_huatu.business.ztk_zhibo.play.chat;

import java.text.SimpleDateFormat;

public abstract class AbsChatMessage {

	public static final SimpleDateFormat formatter = new SimpleDateFormat(
			"HH:mm");
	public static final SimpleDateFormat formatter1 = new SimpleDateFormat(
			"HH:mm:ss");
	protected String text; // 纯文本
	protected String rich; // 富文本
	protected long time;
	protected String sendUserId;
	protected String mSendUserName;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbsChatMessage other = (AbsChatMessage) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (sendUserId != other.sendUserId)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((sendUserId == null) ? 0 : sendUserId.hashCode());
		return result;
	}

	public String getText() {
		return text;
	}

	public void setText(String msg) {
		this.text = msg;
	}

	public String getRich() {
		return rich;
	}

	public void setRich(String rich) {
		this.rich = rich;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendUserName() {
		return mSendUserName;
	}

	public void setSendUserName(String mSendUserName) {
		this.mSendUserName = mSendUserName;
	}
}
