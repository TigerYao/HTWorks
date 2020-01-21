package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.BaiJiaVideoPlayerImpl;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.OnliveChatPresenter;
import com.huatu.handheld_huatu.business.ztk_zhibo.view.LiveChatEditDialog;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.utils.DensityUtils;

public class LiveVideoView extends FrameLayout {
    private FrameLayout mVideoLayout;
    private FrameLayout mChatLayout;
    private ViewGroup mVideoContainer;
    private View mVideoContainerView;
    private TextView mVideoTeacherNameTv;
    private TextView mPptTeacherName;

    private boolean isShowVideo = false;
    private boolean isChatShow = false;
    public boolean isAlreadyShow = false;
    private OnliveChatPresenter mFullScrrenChatPresent;
    private OnliveChatPresenter mTeacherNoteList;
    private VideoPlayer.BaseView mVideoPlayer;

    public LiveChatEditDialog mDialog;
    private Drawable mDrawable;

    private int mChatHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private int mPptChangeWidth = 0;
    private int mPptChangeHeight = 0;
    private float mRatio = 0.56f;

    public LiveVideoView(@NonNull Context context) {
        this(context, null);
    }

    public LiveVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void showTeacherVideo(boolean showVideo) {
        if (isShowVideo == showVideo)
            return;
        isShowVideo = showVideo;
        if (isShowVideo) {
            initVideoSize();
            mVideoLayout.setVisibility(VISIBLE);
            mChatLayout.getLayoutParams().width = mPptChangeWidth;
            mVideoContainer.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            mVideoContainer.getLayoutParams().height= mVideoHeight;
           // int pptChangeHeight = LayoutParams.MATCH_PARENT;
            int pptChangeHeight = isChatShow ?  mPptChangeHeight : getHeight();
            mVideoContainerView.setLayoutParams(new FrameLayout.LayoutParams(mPptChangeWidth, pptChangeHeight));
            if (mFullScrrenChatPresent != null)
                mFullScrrenChatPresent.fiterTeacherChatMessage();
        } else {
            mVideoLayout.setVisibility(GONE);
            mVideoContainerView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,  LayoutParams.MATCH_PARENT));
            mChatLayout.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }
        if (mVideoPlayer != null && mVideoPlayer.getPlayer() != null)
            ((BaiJiaVideoPlayerImpl) mVideoPlayer.getPlayer()).showVideo(isShowVideo);

        if (mPptTeacherName.getVisibility() == View.VISIBLE) {
            LayoutParams params = (LayoutParams) mPptTeacherName.getLayoutParams();
            int defaultBtm = getResources().getDimensionPixelOffset(R.dimen.common_10dp);
            params.bottomMargin = (isChatShow && !isShowVideo) ? defaultBtm + mChatHeight : defaultBtm;
        }
    }

    public void setChatList(VideoPlayer.BaseView videoPlayer) {
        mFullScrrenChatPresent = new OnliveChatPresenter(mVideoLayout, videoPlayer);
        mFullScrrenChatPresent.setColor("#669DBC", "#4A4A4A", "#FF6D73");
        mFullScrrenChatPresent.shouldShadow(false, true);
        mTeacherNoteList = new OnliveChatPresenter(mChatLayout, videoPlayer, true);
        mTeacherNoteList.setContentColor("#ffffff", "#ffffff");
        mTeacherNoteList.shouldShadow(true, false);
        mVideoPlayer = videoPlayer;
    }

    public void setChatListView(boolean show) {
        isChatShow = show;
        initVideoSize();
        //int pptHeight = getHeight();
        int pptHeight = show && isShowVideo ? mPptChangeHeight : getHeight();
        int pptWidth = isShowVideo ? mPptChangeWidth : LayoutParams.MATCH_PARENT;
        mVideoContainerView.setLayoutParams(new FrameLayout.LayoutParams(pptWidth, pptHeight));
        mChatLayout.setVisibility(show ? VISIBLE : GONE);
        if (show && mTeacherNoteList != null) {
            mTeacherNoteList.fiterTeacherChatMessage();
        }
        if (mPptTeacherName.getVisibility() == View.VISIBLE) {
            LayoutParams params = (LayoutParams) mPptTeacherName.getLayoutParams();
            int defaultBtm = getResources().getDimensionPixelOffset(R.dimen.common_10dp);
            params.bottomMargin = (isChatShow && !isShowVideo) ? defaultBtm + mChatHeight : defaultBtm;
        }
    }

    public void setTeacherNots() {
        if (mFullScrrenChatPresent != null && isShowVideo) {
            mFullScrrenChatPresent.fiterTeacherChatMessage();
        }
        if (mTeacherNoteList != null && mChatLayout.getVisibility() == View.VISIBLE) {
            mTeacherNoteList.fiterTeacherChatMessage();
        }
    }

    public void closeFull() {
        if (mDialog != null)
            mDialog.dismiss();
        isAlreadyShow = isShowVideo;
        isChatShow = false;
        mVideoLayout.setVisibility(GONE);
        mVideoContainerView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mChatLayout.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        mChatLayout.setVisibility(GONE);
        isShowVideo = false;
        LayoutParams params = (LayoutParams) mPptTeacherName.getLayoutParams();
        int defaultBtm =  getResources().getDimensionPixelOffset(R.dimen.common_10dp);
        params.bottomMargin = defaultBtm;
//        mVideoView = null;
        if (mVideoPlayer != null && mVideoPlayer.getPlayer() != null && isAlreadyShow)
            ((BaiJiaVideoPlayerImpl) mVideoPlayer.getPlayer()).showVideo(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * mRatio), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView(Context ctx) {
        View.inflate(ctx, R.layout.layout_live_video_view_land, this);
        mVideoLayout = (FrameLayout) findViewById(R.id.live_video_view_father_layout);
        mChatLayout = (FrameLayout) findViewById(R.id.layout_live_video_chat);
        mVideoContainer = (ViewGroup) findViewById(R.id.live_video_view_container_layout);
        mVideoContainerView = findViewById(R.id.live_video_play_layout);
        mPptTeacherName = findViewById(R.id.teacher_name_ppt);
        mVideoContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               changeVideoMode();
            }
        });
        mDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.video_switch_tips_img);
        mRatio = mDrawable.getIntrinsicHeight() * 1f / mDrawable.getIntrinsicWidth();
        mVideoTeacherNameTv = (TextView) findViewById(R.id.teacher_name);
    }

    private void changeVideoMode(){
        if (mVideoPlayer != null && mVideoPlayer.getPlayer() != null) {
            mVideoPlayer.getPlayer().changeVideoMode();
            boolean isVideoPlay = mVideoPlayer.getPlayer().isVideoPlay;
            mPptTeacherName.setVisibility(isVideoPlay ? VISIBLE : GONE);
            mVideoTeacherNameTv.setVisibility(isVideoPlay ? GONE : VISIBLE);
        }
    }

    private void initVideoSize() {
        int screenHeight = getHeight();
        int screenWith = getWidth();
        int height = Math.min(screenHeight, screenWith);
        int width = Math.max(screenHeight, screenWith);
        int videoWidth = width/4;
        if (mVideoWidth == videoWidth)
            return;
        mVideoWidth = videoWidth;
        mVideoHeight = (int)(mVideoWidth*mRatio);
        mPptChangeWidth = width - mVideoWidth;
        mPptChangeHeight = (int)(mRatio * mPptChangeWidth);
        mChatHeight = Math.max(height - mPptChangeHeight, height/4);
        mPptChangeHeight = height - mChatHeight;
        mChatLayout.getLayoutParams().height = mChatHeight;
        mChatLayout.getLayoutParams().width = width;
        mVideoLayout.getLayoutParams().height = height;
        mVideoLayout.getLayoutParams().width = mVideoWidth;
        mVideoContainer.getLayoutParams().height = mDrawable.getIntrinsicHeight();
        mChatLayout.setVisibility(GONE);
        mVideoLayout.setVisibility(GONE);
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public boolean isInterceptTouchEvent(MotionEvent ev) {
        if (mChatLayout.getVisibility() == View.VISIBLE && mTeacherNoteList != null) {
            Rect rect = new Rect();
            mChatLayout.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                dispatchTouchEvent(ev);
                return true;
            }
        }

        if (isShowVideo && ev.getAction() == MotionEvent.ACTION_UP){
            Rect rect = new Rect();
            mVideoContainer.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                mVideoContainer.performClick();
//                dispatchTouchEvent(ev);
                return true;
            }
        }

        if (isShowVideo && mFullScrrenChatPresent != null) {
            Rect rect = new Rect();
            mVideoLayout.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                dispatchTouchEvent(ev);
                return true;
            }
        }

        return false;
    }

    public void showAddChatDialog(final VideoPlayer player) {
        if (mDialog == null) {
            mDialog = new LiveChatEditDialog(getContext()) {
                @Override
                public void sendChatMsg(String chat, String richMes, boolean isEmoji) {
                    player.sendChatMsg(chat,richMes, isEmoji);
                }
            };
        }
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }else {
            mDialog.showDialog();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mDialog!= null && mDialog.isShowing() && !mDialog.isShow)
                        mDialog.showKeyboard();
                }
            }, 300);
        }
    }

    public boolean closeDialog(){
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            return true;
        }
        return false;
    }

    public void setTeacherName(String name){
        if (!TextUtils.isEmpty(name)) {
            mVideoTeacherNameTv.setText(name + "老师");
            mPptTeacherName.setText(name + "老师");
        }
    }

}
