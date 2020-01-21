package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.OnChatMessageAdapter;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.utils.AnimUtils;

/**
 * Created by Administrator on 2018\5\7 0007.
 */

public class OnliveChatPresenter implements View.OnClickListener {
    private ImageView ivTeacherOnly;
    private RecyclerView mChatMsgListView;
    private TextView textview_interactive_sug;
    private View sendMessageView;
    private OnChatMessageAdapter messageAdapter;
    private boolean isTeacherOnly = false;
    private VideoPlayer.BaseView mVideoView;
    public int mFixNum = -1;
    private boolean mIsCanScroller = true;
    private TextView mTipView;
    ChatMessageListWrap listWrap;
    private int mLastCount = 0;

    public OnliveChatPresenter(View interactiveView, VideoPlayer.BaseView videoPlayer, boolean onlyTeacher) {
        this(interactiveView, videoPlayer);
        isTeacherOnly = onlyTeacher;
    }

    public OnliveChatPresenter(View interactiveView, VideoPlayer.BaseView videoPlayer) {
        mVideoView = videoPlayer;

        messageAdapter = new OnChatMessageAdapter(interactiveView.getContext(), this);
        ivTeacherOnly = (ImageView) interactiveView
                .findViewById(R.id.iv_see_teacher);
        mChatMsgListView = (RecyclerView) interactiveView
                .findViewById(R.id.listview_interactive_content);
        if (mChatMsgListView == null)
            mChatMsgListView = (RecyclerView) interactiveView
                    .findViewById(R.id.listview_interactive_content1);
        View clearView = interactiveView.findViewById(R.id.iv_clear_message);
        if (clearView != null)
            clearView.setOnClickListener(this);

        mChatMsgListView.setLayoutManager(new LinearLayoutManager(interactiveView.getContext()));
        mChatMsgListView.setAdapter(messageAdapter);

        textview_interactive_sug = (TextView) interactiveView
                .findViewById(R.id.textview_interactive_sug);
        if (textview_interactive_sug != null)
            textview_interactive_sug.setVisibility(View.GONE);
        sendMessageView = interactiveView.getRootView()
                .findViewById(R.id.send_message_divider);
        if (ivTeacherOnly != null)
            ivTeacherOnly.setOnClickListener(this);
        mTipView = (TextView) interactiveView.findViewById(R.id.tip_new_tv);
        if (mTipView == null)
            mTipView = (TextView) interactiveView.findViewById(R.id.tip_new_tv1);
        if (mTipView != null)
            mTipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChatMsgListView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    mIsCanScroller = true;
                    mTipView.setVisibility(View.GONE);
                    mLastCount = messageAdapter.getItemCount();
                }
            });

        mChatMsgListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mTipView == null)
                    return;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mTipView != null) {
                    if (isCanScrollDown() && mIsCanScroller) {
                        mIsCanScroller = false;
                    } else if (mTipView.getVisibility() == View.VISIBLE && !isCanScrollDown()) {
                        mIsCanScroller = true;
                        mTipView.setVisibility(View.GONE);
                        mLastCount = messageAdapter.getItemCount();
                        if (mLastCount != listWrap.getCount()) {
                            mLastCount = listWrap.getCount();
                            messageAdapter.setMessageModelList(listWrap);
                            messageAdapter.notifyDataSetChanged();
                            mChatMsgListView.scrollToPosition(messageAdapter.getItemCount() - 1);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void showSendMessageView(boolean isNeedShow) {
        sendMessageView.setVisibility(isNeedShow ? View.VISIBLE : View.GONE);
    }

    private void onClickTeacherOnly() {
        if (isTeacherOnly) {
            isTeacherOnly = false;
            ivTeacherOnly.setImageResource(R.drawable.ic_check_teacher);
        } else {
            isTeacherOnly = true;
            ivTeacherOnly.setImageResource(R.drawable.ic_check_teacher_suc);
        }

        if (null == mVideoView || (mVideoView.getPlayer() == null)) {
            return;
        }
        mVideoView.getPlayer().getMsgList().mIsNewMessage = false;
        mIsCanScroller = true;
        mLastCount = 0;
        if (mTipView != null && mTipView.getVisibility() == View.VISIBLE)
            mTipView.setVisibility(View.GONE);
        fiterTeacherChatMessage();
    }

    public void fiterTeacherChatMessage() {
        if (null == mVideoView || (mVideoView.getPlayer() == null)) {
            return;
        }
        ChatMessageListWrap list = mVideoView.getPlayer().getMsgList();

        ChatMessageListWrap curlist = isTeacherOnly ? list.getOnlyTeacher() : list;
        listWrap = new ChatMessageListWrap();
        if (mFixNum > 0 && mFixNum < curlist.baijiaMsg.size()) {
            listWrap.baijiaMsg = curlist.baijiaMsg.subList(curlist.baijiaMsg.size() - mFixNum, curlist.baijiaMsg.size());
        } else
            listWrap.baijiaMsg = curlist.baijiaMsg;

       // listWrap.geeChatMsg = curlist.geeChatMsg;
        listWrap.playerType = curlist.playerType;
        int newCount = listWrap.getCount() - mLastCount;
        mIsCanScroller = mIsCanScroller || !mVideoView.getPlayer().getMsgList().mIsNewMessage;
        if (mIsCanScroller) {//更新列表
            mLastCount = listWrap.getCount();
            if (mTipView != null && mTipView.getVisibility() == View.VISIBLE)
                mTipView.setVisibility(View.GONE);
            messageAdapter.setMessageModelList(listWrap);
            messageAdapter.notifyDataSetChanged();
            mChatMsgListView.scrollToPosition(messageAdapter.getItemCount() - 1);
        } else if (mTipView != null && isCanScrollDown() && newCount > 0) {
            String numTip = newCount + "条新消息";
            mTipView.setText(numTip);
            mTipView.setVisibility(View.VISIBLE);
            AnimUtils.startTopDown(mTipView);
        }
    }

    public void setContentColor(String color, String nameColor) {
        messageAdapter.setNormalTextColor(color, nameColor);
    }

    public void setColor(String studentNameColor, String studentContentColor, String teacherColor){
        messageAdapter.setColor(studentNameColor, studentContentColor, teacherColor);
    }

    public void shouldShadow(boolean shadow, boolean hasFontStylke){
        messageAdapter.setShouldShadow(shadow, hasFontStylke);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_see_teacher:
                onClickTeacherOnly();
                break;
            case R.id.iv_clear_message:
                onClickClearMessage();
                break;

        }
    }

    private void onClickClearMessage() {
        if (messageAdapter != null && mVideoView != null && mVideoView.getPlayer() != null) {
            messageAdapter.clearAndRefresh();
            mVideoView.getPlayer().getMsgList().clear();
            mVideoView.getPlayer().getMsgList().mIsClearAll = true;
            mVideoView.getPlayer().getMsgList().mIsNewMessage = true;
            mIsCanScroller = true;
            mLastCount = 0;
            if (mTipView != null && mTipView.getVisibility() == View.VISIBLE)
                mTipView.setVisibility(View.GONE);
        }
    }


    public void showOrientationConfig(boolean isPortrait) {
        messageAdapter.notifyDataSetChanged();
    }

    public void setIsPortrait(boolean isPortrait) {
        messageAdapter.setIsPortrait(isPortrait);
    }

    public void clearMessage() {
        isTeacherOnly = false;
        ivTeacherOnly.setImageResource(R.drawable.ic_check_teacher);
        messageAdapter.clearAndRefresh();

    }

    public void onDestory() {
        mVideoView = null;
    }

    public boolean isCanScrollDown() {
        return mChatMsgListView != null && mChatMsgListView.canScrollVertically(1);
    }
}
