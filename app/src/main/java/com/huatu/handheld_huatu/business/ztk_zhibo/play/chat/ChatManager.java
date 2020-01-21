package com.huatu.handheld_huatu.business.ztk_zhibo.play.chat;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Deprecated
public class ChatManager {

	private List<ChatMessage> chatMsgList;

	public ChatManager() {
		chatMsgList = new ArrayList<>();
		mLock = new ReentrantReadWriteLock();
	}
	protected boolean chatStatus;
	protected ReentrantReadWriteLock mLock;
	protected boolean bEnable = true;   //FALSE 禁言

	public void addMsg(ChatMessage msg) {
		mLock.writeLock().lock();
		try {
			chatMsgList.add(msg);
		} finally {
			mLock.writeLock().unlock();
		}
	}

	public void addMsgList(List<ChatMessage> msg) {
		mLock.writeLock().lock();
		try {
			chatMsgList.addAll(msg);
		} finally {
			mLock.writeLock().unlock();
		}
	}

	public List<ChatMessage> getMsgList() {

		List<ChatMessage> returnList = null;
		mLock.readLock().lock();
		try {
			returnList = new ArrayList<>(chatMsgList);
		} finally {
			mLock.readLock().unlock();
		}

		return returnList;
	}

	public void clearAll() {
		mLock.writeLock().lock();
		try {
			chatMsgList.clear();
		} finally {
			mLock.writeLock().unlock();
		}
	}

}
