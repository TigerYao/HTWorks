package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.Date;

@SuppressLint("HandlerLeak")
public abstract class AdvancedCountdownTimer {

	private final long m_lCountdownInterval;
	private long m_lTotalTime;
	private long m_lRemainTime;
	private boolean m_bInfinit;
	private long m_lTimerStamp;
	private long m_lStartPauseStamp;

	public AdvancedCountdownTimer(long millisInFuture, long countDownInterval,
			long futureTimeStamp, boolean infinit) {
		m_lTotalTime = millisInFuture;
		m_lCountdownInterval = countDownInterval;
		m_lRemainTime = millisInFuture;
		m_bInfinit = infinit;
		m_lTimerStamp = futureTimeStamp;
	}

	public long getRemainTime() {
		return m_lRemainTime;
	}

	public final void seek(int value) {

		synchronized (AdvancedCountdownTimer.this) {
			m_lRemainTime = ((100 - value) * m_lTotalTime) / 100;
		}
	}

	public final void resetTimeStamp(long timestamp) {
		m_lTimerStamp = timestamp;
	}

	public final void cancel() {
		mHandler.removeMessages(MSG_RUN);
		mHandler.removeMessages(MSG_PAUSE);
	}

	public final void resume() {
		mHandler.removeMessages(MSG_PAUSE);
		mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_RUN));
		if (m_lStartPauseStamp != 0) {
			m_lTimerStamp += new Date().getTime() - m_lStartPauseStamp;
		}
	}

	public final void pause() {
		mHandler.removeMessages(MSG_RUN);
		mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_PAUSE));
		m_lStartPauseStamp = new Date().getTime();
	}

	public synchronized final AdvancedCountdownTimer start() {

		if (!m_bInfinit && m_lRemainTime <= 0) {
			onFinish();
			return this;
		}

		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_RUN),
				m_lCountdownInterval);
		return this;
	}

	public abstract void onTick(long millisUntilFinished, int percent);

	public abstract void onFinish();

	private static final int MSG_RUN = 1;
	private static final int MSG_PAUSE = 2;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			synchronized (AdvancedCountdownTimer.this) {

				if (msg.what == MSG_RUN) {
					// Utility.logI("timer is running");
					long lCurTime = new Date().getTime();
					m_lRemainTime = m_lTimerStamp - lCurTime;
					if (!m_bInfinit && m_lRemainTime < 0) {
						onFinish();
					} else {
						onTick(m_lRemainTime,
								m_lTotalTime > 0 ? Long.valueOf(
										100 * (m_lTotalTime - m_lRemainTime)
												/ m_lTotalTime).intValue() : 0);
						sendMessageDelayed(obtainMessage(MSG_RUN),
								m_lCountdownInterval);
					}

				} else if (msg.what == MSG_PAUSE) {
					// Utility.logI("timer is running");
				}
			}

		}

	};

}