package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public abstract class AdvancedTimer {

	private final long mCountdownInterval;
	private long mDurationTime;

	public AdvancedTimer(long countDownInterval) {
		mCountdownInterval = countDownInterval;
		mDurationTime = 0;
	}
	public AdvancedTimer(long countDownInterval,long mDurationTime) {
		mCountdownInterval = countDownInterval;
		this.mDurationTime = mDurationTime;
	}

	public long getDuration() {
		return mDurationTime;
	}
	public long setDuration() {
		return mDurationTime;
	}

	public final void cancel() {
		mHandler.removeMessages(MSG_RUN);
		mHandler.removeMessages(MSG_PAUSE);
	}

	public final void resume() {
		mHandler.removeMessages(MSG_PAUSE);
		mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_RUN));
	}

	public final void pause() {
		mHandler.removeMessages(MSG_RUN);
		mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_PAUSE));
	}

	public synchronized final AdvancedTimer start() {

		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_RUN),
				mCountdownInterval);
		return this;
	}

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();

	private static final int MSG_RUN = 1;
	private static final int MSG_PAUSE = 2;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			synchronized (AdvancedTimer.this) {

				if (msg.what == MSG_RUN) {
					mDurationTime = mDurationTime + mCountdownInterval;

					onTick(mDurationTime);
					sendMessageDelayed(obtainMessage(MSG_RUN),
							mCountdownInterval);

				} else if (msg.what == MSG_PAUSE) {
					
				}
			}

		}

	};

}