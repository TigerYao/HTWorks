package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.BaiJiaVideoPlayerImpl;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.BrightnessHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.PlayRateDialogFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VolumeChangeObserver;

import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.widget.MyRadioGroup;

/**
 * Created by yaohu on 2018/8/7.
 */

public class LiveVideoCenterView extends RelativeLayout {
    private View mSpeedLayout;
    private TextView mSpeedTextView;
    public TextView mRateChangeView;
    private View mLeftLayout;
    private View mRightLayout;
    private ImageView mLockBtn;
    private View mBVLayout;
    private ImageView mBVImg;
    private TextView mBVText;
    private ImageView mJudgeBtn;
    private ImageView mJudgeLandBtn;
    private ListView mSelectListView;
    private TextView mPptPageView;
    private TextView mLiveStartTimeView;
    private View mLiveBottomLayout;

    private View mExpandChatBtn;
    private View mExpandVideoBtn;
    private View mFloatVideoView;

    private int mLastBright = -1;
    public float mLastVolume = -1, mCurrentVolume = -1;
    private int mLastOffset = -1;

    private OnClickListener mClickListener;
    private OnSelectedChangeListener mSelectedChange;
    private ArrayAdapter mQualityListAdapter;
    private String[] speedStr = {"0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x"};
    protected float[] bjPlaySpeedLists = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};
    private int[] moreMenuDrawables = {R.drawable.live_video_more_share_btn, R.drawable.live_video_more_report_btn};
    private View mBtnAddChat;

    // 横屏状态是否展开聊天
    private boolean isChatExpand = false;
    // 视频是否展开
    private boolean isVideoExpand = false;
    // 是否是直播
    public boolean mIsLive = false;

    private int mCurQualityPosition = 3;
    private int mCurSpeedPosition = 2;

    public boolean mIsLock = false;
    private VolumeChangeObserver mVolumeChangeObserver;
    private ContentObserver mOVolumeChangeObserver;
    private ProgressBar mProgressBar;
    private View mTipLayout;
    private View mOperateTipView;
    private VideoPlayer mPlayer;

    private boolean mIsGesture = false;
    public boolean showJudege = true;
    public VideoStatisticsUtil mStatisticsUtil;
    private View mOperationView;

    public interface OnSelectedChangeListener {
        void onQualityChanged(int position, String quality);

        void onSpeedChanged(int postion, String speed);

        void onClickMoreMenu(int postion);
    }

    public LiveVideoCenterView(Context context) {
        this(context, null);
    }

    public LiveVideoCenterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void showSpeedTextView(boolean show) {
        if (mSpeedLayout != null)
            mSpeedLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void updateTime(boolean isKuijin, int curTime, int totalTime) {
        mSpeedLayout.setBackgroundResource(isKuijin ? R.drawable.bg_live_video_kuaijin : R.drawable.bg_live_video_kuaitui);
        mSpeedTextView.setText(TimeUtils.getTime(curTime) + "  /  " + TimeUtils.getTime(totalTime));
        mProgressBar.setMax(totalTime);
        mProgressBar.setProgress(curTime);
    }

    public void showJudege(boolean showJudge) {
        this.showJudege = showJudge;
        if (!showJudge) {
            mJudgeBtn.setVisibility(GONE);
            mJudgeLandBtn.setVisibility(GONE);
        }
    }

    public void switchLock() {
        if (mIsLock) {
            AnimUtils.translationSideAnim(mLeftLayout, -mLeftLayout.getRight(), 0, mLeftLayout.getVisibility() != View.VISIBLE);
        }
    }

    public boolean isShowLock() {
        return mLockBtn.getVisibility() == VISIBLE;
    }

    public void onGestSeeking(GestureState state) {
        if (mLastOffset == state.offset || state == null)
            return;
        mLastOffset = state.offset;
        switch (state.type) {
            case GestureState.GESTURE_TYPE_PROGRESS:
                showSpeedTextView(true);
                updateTime(state.offset > 0, state.seekedPosition, state.duration);
                break;
            case GestureState.GESTURE_TYPE_BRIGHTNESS:
                updateBright(true, state);
                break;
            case GestureState.GESTURE_TYPE_VOLUME:
                mIsGesture = true;
                updateVolume(true, state);
                break;
        }
    }

    public void onGestEnd(GestureState state) {
        if (state == null) return;
        switch (state.type) {
            case GestureState.GESTURE_TYPE_BRIGHTNESS:
                mLastBright = state.initBrightness;
                break;
            case GestureState.GESTURE_TYPE_VOLUME:
                mLastVolume = state.initVolume;
                mIsGesture = false;
                break;
        }
        showSpeedTextView(false);
        updateBrightVolumeUI(false, false, 2);
    }

    public void setIsExpandVideo(boolean isExpand) {
        this.isVideoExpand = isExpand;
        if(mOperationView != null) {
            mExpandVideoBtn.setSelected(isExpand);
            if(isExpand)
            mFloatVideoView.setSelected(false);
        }
        showAddChatBtn(isExpand);
        if (isExpand && SpUtils.isShowSwitchPPtTip()) {
            mTipLayout.setVisibility(VISIBLE);
            SpUtils.setShowSwitchPPtTip(false);
            mTipLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpUtils.setShowSwitchPPtTip(false);
                    mTipLayout.setVisibility(GONE);
                }
            });
        }
    }

    public void setIsExpandChat(boolean isExpand) {
        if(mOperationView == null)
            return;
        mExpandChatBtn.setSelected(isExpand);
        this.isChatExpand = isExpand;
    }

    public void setFloatState(boolean isOpen){
        if(mOperationView == null)
            return;
        mFloatVideoView.setSelected(isOpen);
        if(isOpen) mExpandVideoBtn.setSelected(false);
    }

    public boolean isChatExpand() {
        return isChatExpand;
    }

    public boolean isVideoExpand() {
        return isVideoExpand;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mClickListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * 0.56f), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void showCenter(boolean show, boolean isLand) {
        if (!isLand) {
            mLeftLayout.setVisibility(View.GONE);
            mRightLayout.setVisibility(View.GONE);
            AnimUtils.translationSideAnim(mJudgeBtn, mJudgeBtn.getRight(), 0, showJudege && show && !isLand);
            AnimUtils.translationSideAnim(mRateChangeView, -mRateChangeView.getRight(), 0, show && !isLand && !mIsLive);
        } else {
            mJudgeBtn.setVisibility(View.GONE);
            mRateChangeView.setVisibility(View.GONE);
            AnimUtils.translationSideAnim(mLeftLayout, -mLeftLayout.getRight(), 0, (show || mIsLock) && isLand);
            AnimUtils.translationSideAnim(mRightLayout, mRightLayout.getRight(), 0, show && isLand && !mIsLock);
        }
        if(!isLand || mIsLock)
            mLiveBottomLayout.setVisibility(View.GONE);

        if (mIsLive && mOperationView == null && isLand) {
            ViewStub vs = findViewById(R.id.live_bottom_viewstub);
            setFloatView(vs.inflate());
        }

        boolean canShow = isLand && !mIsLock && mIsLive;
        if (show && canShow && mLiveBottomLayout.getVisibility() != VISIBLE) {
            mLiveBottomLayout.setVisibility(VISIBLE);
            AnimUtils.animateBottomShow(mLiveBottomLayout);
        }
        else if (canShow && !show && mLiveBottomLayout.getVisibility() == VISIBLE)
            AnimUtils.animateBottomHide(mLiveBottomLayout).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mLiveBottomLayout.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mLiveBottomLayout.setVisibility(GONE);
                }
            });
        if (isLand && SpUtils.isShowLiveVideoTip() && mOperateTipView == null) {
            ViewStub viewStub = ((Activity)getContext()).findViewById(R.id.operate_tips);
            mOperateTipView = viewStub.inflate();
            mOperateTipView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpUtils.setShowLiveVideoTip(false);
                    mOperateTipView.setVisibility(GONE);
                }
            });
            mOperateTipView.setVisibility(VISIBLE);
            SpUtils.setShowLiveVideoTip(false);
            UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOperateTipView.setVisibility(GONE);
                }
            }, 5000);
        }
        else if (mOperateTipView != null && !isLand && mOperateTipView.getVisibility() == VISIBLE) {
            mOperateTipView.setVisibility(GONE);
        }
        else if (!isLand && mTipLayout.getVisibility() == VISIBLE)
            mTipLayout.setVisibility(GONE);
    }

    public void setPlayer(VideoPlayer player) {
        this.mPlayer = player;
    }

    public void setFloatView(View v){
        mOperationView = v;
        mFloatVideoView = v.findViewById(R.id.open_live_video);
        mExpandChatBtn = v.findViewById(R.id.video_play_interaction_btn);
        mExpandVideoBtn = v.findViewById(R.id.live_video_view_close_iv);
    }

    private void initView(Context ctx) {
        removeAllViews();
        View.inflate(ctx, R.layout.layout_live_video_center, this);
        mSpeedLayout = findViewById(R.id.live_video_speed_up_layout);
        mSpeedTextView = (TextView) findViewById(R.id.live_video_speed_up_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.play_progress);
        mRateChangeView = (TextView) findViewById(R.id.play_changeRate_txt);
        mLeftLayout = findViewById(R.id.layout_left_operate);
        mRightLayout = findViewById(R.id.layout_right_operate);
        mLockBtn = (ImageView) findViewById(R.id.video_lock_btn);
        mBVLayout = findViewById(R.id.volume_bright_layout);
        mBVImg = (ImageView) findViewById(R.id.volume_bright_icon);
        mBVText = (TextView) findViewById(R.id.volume_bright_value);
        mSelectListView = (ListView) findViewById(R.id.live_list_layout);
        mBtnAddChat = findViewById(R.id.open_add_chat_img);
        mTipLayout = findViewById(R.id.tip_layout);
        mJudgeBtn = (ImageView) findViewById(R.id.play_judege_txt);
        mJudgeLandBtn = findViewById(R.id.play_judege_land);
        mPptPageView = findViewById(R.id.ppt_page);
        mLiveStartTimeView = findViewById(R.id.live_start_time);
        mLiveBottomLayout = findViewById(R.id.live_bottom_layout);
        if (mBtnAddChat != null)
            mBtnAddChat.setOnClickListener(mOnClickListener);
        if (mSelectListView != null) {
            mSelectListView.setVerticalScrollBarEnabled(false);
            mSelectListView.setDividerHeight(0);
            mSelectListView.setSelector(R.color.transparent);
        }
        if (mExpandVideoBtn != null)
            mExpandVideoBtn.setOnClickListener(mOnClickListener);
        if (mLockBtn != null)
            mLockBtn.setOnClickListener(mOnClickListener);
        if (mExpandChatBtn != null)
            mExpandChatBtn.setOnClickListener(mOnClickListener);
        mVolumeChangeObserver = new VolumeChangeObserver(ctx);
        Typeface mtypeface = Typeface.createFromAsset(getContext().getAssets(), "font/851-CAI978.ttf");
        mSpeedTextView.setTypeface(mtypeface);
        mRateChangeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick()) return;
                final PlayRateDialogFragment ratefragment = PlayRateDialogFragment.getInstance(bjPlaySpeedLists[mCurSpeedPosition]);
                ratefragment.show(((LiveVideoForLiveActivity) getContext()).getSupportFragmentManager(), "rateplay");
                ratefragment.setCheckedListener(new MyRadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                        mCurSpeedPosition = checkedId;
                        mRateChangeView.setText(speedStr[checkedId]);
                        mSelectedChange.onSpeedChanged(checkedId, speedStr[checkedId]);
                        ratefragment.dismiss();
                    }
                });
            }
        });
        mJudgeBtn.setOnClickListener(mOnClickListener);
        mJudgeLandBtn.setOnClickListener(mOnClickListener);
    }

    private void updateBright(boolean show, GestureState gestureState) {
        int initBright = mLastBright <= 0 ? BrightnessHelper.getScreenBrightness(getContext()) : mLastBright;
        int brightness = initBright + (int) (gestureState.maxBrightness * (gestureState.offset * 1f / getMeasuredHeight()));
        if (brightness > gestureState.maxBrightness)
            brightness = gestureState.maxBrightness;
        else if (brightness < 0)
            brightness = 0;
        gestureState.initBrightness = brightness;
        BrightnessHelper.setScreenBrightness(((Activity) getContext()).getWindow(), brightness);
        updateBrightVolumeUI(show, false, (int) (brightness * 100f / gestureState.maxBrightness));
    }

    private void updateVolume(boolean show, GestureState gestureState) {
        if (mLastVolume <= 0)
            mLastVolume = mVolumeChangeObserver.getCurrentMusicVolume();///*VolumeUtils.getMediaVolume(mIsLive, getContext()) */ : (int) mLastVolume;
        float maxVolume = mVolumeChangeObserver.getMaxMusicVolume();//VolumeUtils.getMediaMaxVolume(mIsLive, getContext());
        float volume = mLastVolume + (maxVolume * gestureState.offset * 1f / getMeasuredHeight());
        if (volume > maxVolume)
            volume = maxVolume;
        else if (volume < 0)
            volume = 0;
        mVolumeChangeObserver.setMusicVolume((int) volume);
        int percent = (int) (volume * 100f / maxVolume);
        if (gestureState.mView != null && gestureState.mView.getPlayer() != null && mIsLive) {
            if (percent <= 0)
                gestureState.mView.getPlayer().mute();
            else
                gestureState.mView.getPlayer().unMute();
        }
        mCurrentVolume = volume;
        updateBrightVolumeUI(show, true, percent);
    }

    private void updateBrightVolumeUI(boolean show, boolean isVolume, int value) {
        mBVLayout.setVisibility(show ? VISIBLE : GONE);
        if (show) {
            mBVImg.setImageResource(isVolume ? (value <= 0 ? R.drawable.ic_live_video_volume_umute : R.drawable.ic_live_video_volume) : R.drawable.ic_live_video_bright);
            mBVText.setText(value + "%");
        }
    }

    public void setSelectedChange(OnSelectedChangeListener selectedChange) {
        this.mSelectedChange = selectedChange;
    }

    public void showPP(final int type) {
        if (type == 0 && mSelectListView != null && mSelectListView.getVisibility() == View.VISIBLE) {
            mSelectListView.setVisibility(View.GONE);
            return;
        } else if (type != 0) {
            final String[] mList = type == 2 ? speedStr : getResources().getStringArray(type == 1 ? R.array.video_qualities : R.array.video_more_menu);
            if (mList != null && mList.length > 0) {
                mSelectListView.setAdapter(getQualityAdapter(type, mList));
                mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String quality = mList[position];
                        if (mSelectedChange == null)
                            return;
                        if (type == 1) {
                            mSelectedChange.onQualityChanged(mList.length - position - 1, quality);
                            mCurQualityPosition = position;
                        } else if (type == 2) {
                            mSelectedChange.onSpeedChanged(position, quality);
                            mCurSpeedPosition = position;
                            mRateChangeView.setText(quality);
                        } else {
                            mSelectedChange.onClickMoreMenu(position);
                        }

                    }
                });
                mSelectListView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void openLock(boolean isLock) {
        mIsLock = isLock;
        if (mIsLock) {
            mLockBtn.setImageResource(R.mipmap.icon_lock_full_screen);
            mLockBtn.setTag(Boolean.TRUE);
            mIsLock = true;
        } else {
            mLockBtn.setImageResource(R.mipmap.icon_unlock_full_screen);
            mLockBtn.setTag(Boolean.FALSE);
            mIsLock = false;
        }
    }

    public void showAddChatBtn(boolean isShow) {
        if (isShow && mIsLive) {
            mBtnAddChat.setVisibility(View.VISIBLE);
        } else {
            mBtnAddChat.setVisibility(View.GONE);
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mTipLayout.getVisibility() == VISIBLE) {
                SpUtils.setShowSwitchPPtTip(false);
                mTipLayout.setVisibility(GONE);
            }
            else if (mOperateTipView != null && mOperateTipView.getVisibility() == VISIBLE) {
                SpUtils.setShowLiveVideoTip(false);
                mOperateTipView.setVisibility(GONE);
            }
            if (mClickListener != null) mClickListener.onClick(view);
        }
    };

    private BaseAdapter getQualityAdapter(final int type, final String[] mList) {
        mQualityListAdapter = new ArrayAdapter<String>(getContext(), R.layout.player_quality_list_item, R.id.quality_item, mList) {
            @Override
            public int getCount() {
                return mList.length;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
                try {
                    view = super.getView(position, view, parent);
                    TextView textView = (TextView) view.findViewById(R.id.quality_item);
                    textView.setText(mList[position]);
                    view.setMinimumHeight(LiveVideoCenterView.this.getHeight() / getCount() - 30);
                    if (type == 3) {
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, moreMenuDrawables[position], 0, 0);
                    }
                    if ((type == 1 && mCurQualityPosition == position) || (type == 2 && mCurSpeedPosition == position)) {
                        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.bg_live_video_item_select);
                        drawable.setCornerRadius(0f);
                        textView.setBackground(drawable);
                    } else
                        textView.setBackgroundResource(R.color.transparent);
                } catch (Exception e) {
                }
                return view;
            }
        };
        mQualityListAdapter.notifyDataSetChanged();
        return mQualityListAdapter;
    }

    public void registerVolumeChangeReceiver(Context ctx, boolean isLive) {
        this.mIsLive = isLive;
        mOVolumeChangeObserver = new ContentObserver(UniApplicationLike.getApplicationHandler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri != null && uri.toString().contains("volume")) {
                    if (!mIsGesture && mPlayer != null) {
                        mLastVolume = mVolumeChangeObserver.getCurrentMusicVolume();//VolumeUtils.getMediaVolume(mIsLive, getContext());
                        if (mIsLive)
                            if (mLastVolume <= 0) {
                                mPlayer.mute();
                            } else {
                                mPlayer.unMute();
                            }
                    }
                    if (mStatisticsUtil != null && mPlayer != null)
                        mStatisticsUtil.onVideoOperate(((BaiJiaVideoPlayerImpl) mPlayer).currentPlayPosition, String.valueOf((int) mLastVolume));

                    LogUtils.d("Volume", "mLastVolumeChange = " + mLastVolume);
                }

                LogUtils.d("Volume", "Uri = " + uri);
            }
        };
//        mVolumeChangeObserver.registerReceiver();
        mVolumeChangeObserver.setType(mIsLive ? VolumeChangeObserver.LIVE_PHONTHE_TYPE : VolumeChangeObserver.MEDIA_PLAY_TYPE);
        ctx.getApplicationContext().getContentResolver().registerContentObserver(Uri.parse(isLive ? "content://settings/system/volume_voice_speaker" : "content://settings/system/volume_music_speaker"), false, mOVolumeChangeObserver);
    }

    public void unregisterVolumeChangeReceiver(Context ctx) {
        ctx.getApplicationContext().getContentResolver().unregisterContentObserver(mOVolumeChangeObserver);
    }

    public void pptPageChange(int currentPage, int totalPages) {
        if(mPptPageView != null && mPptPageView.getVisibility() != View.VISIBLE)
            mPptPageView.setVisibility(View.VISIBLE);
        if (mIsLive) {
            String value = currentPage == 0 ? "白板" : currentPage + "/" + (totalPages - 1);
            mPptPageView.setText(value);
        }
    }

    public void timeFromBegin(final String startTime) {
        if (!mIsLive || TextUtils.isEmpty(startTime) || !TextUtils.isDigitsOnly(startTime))
            return;
        long time = Long.parseLong(startTime);
        time = System.currentTimeMillis() / 1000 - time - 30 * 60;
        String showTimeStr = (time > 0 ? "直播中: " : "距离开课  ") + DateUtils.formatStrTime(Math.abs(time));
        mLiveStartTimeView.setText(showTimeStr);
        UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeFromBegin(startTime);
            }
        }, 1000);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mVolumeChangeObserver != null && mPlayer != null)
                    mVolumeChangeObserver.ajustVoluem(true);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mVolumeChangeObserver != null && mPlayer != null)
                    mVolumeChangeObserver.ajustVoluem(false);
                return true;
            default:
                break;
        }
        return false;
    }

}
