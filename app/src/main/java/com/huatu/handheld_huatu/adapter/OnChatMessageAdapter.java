package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijia.player.playback.util.DisplayUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.common.LargeImageActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.OnliveChatPresenter;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.chat.ChatMsgTextView;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.StringUtils;


/**
 * Created by wangkangfei on 17/8/17.
 */

public class OnChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MESSAGE_TYPE_TEXT = 0;
    private static final int MESSAGE_TYPE_EMOJI = 1;
    private static final int MESSAGE_TYPE_IMAGE = 2;

    private Context context;
    private int emojiSize;
    private int orientationState;
    private boolean mShouldShadow = false;
    private boolean mIsSetFontStyle = false;
    // private List<IMessageModel> messageModelList = new ArrayList<>();
    private OnliveChatPresenter pbChatPresenter;
    private ChatMessageListWrap messageModellist;
    private Typeface mTypeface;

    boolean isPortrait = true;

    public void setIsPortrait(boolean portrait) {
        isPortrait = portrait;
    }

    public OnChatMessageAdapter(Context context, OnliveChatPresenter pbChatPresenter) {
        this.context = context;
        messageModellist = new ChatMessageListWrap();
        this.pbChatPresenter = pbChatPresenter;
        emojiSize = (int) (DisplayUtils.getScreenDensity(context) * 32);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "font/Heavy.ttf");
    }

    public void setMessageModelList(ChatMessageListWrap messageModelList) {
        this.messageModellist.clear();
        this.messageModellist.addAll(messageModelList);
    }

    public void clearAndRefresh() {
        this.messageModellist.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModellist.playerType == PlayerTypeEnum.BaiJia.getValue()) {
            switch (messageModellist.baijiaMsg.get(position).getMessageType()) {
                case Emoji:
                case EmojiWithName:
                    return MESSAGE_TYPE_EMOJI;
                case Image:
                    return MESSAGE_TYPE_IMAGE;
                default:
                    return MESSAGE_TYPE_TEXT;
            }
        }
        return MESSAGE_TYPE_TEXT;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case MESSAGE_TYPE_EMOJI:
                View emojiView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_emoji, parent, false);
                return new PBEmojiViewHolder(emojiView);
            case MESSAGE_TYPE_IMAGE:
                View imgView = LayoutInflater.from(context).inflate(R.layout.item_pb_chat_image, parent, false);
                return new PBImageViewHolder(imgView);

            case MESSAGE_TYPE_TEXT:
            default:
                View textView = LayoutInflater.from(context).inflate(R.layout.chat_msg_list_item_layout, parent, false);
                return new PBTextViewHolder(textView);
        }
    }

    private String mContentColor, mNameColor, mStudenNameColor, mStudentContentColor, mTeacherColor;

    public void setNormalTextColor(String contentColor, String nameColor) {
        mContentColor = contentColor;
        mNameColor = nameColor;
    }

    public void setColor(String nameColor, String contentColor, String teacherColor) {
        mStudenNameColor = nameColor;
        mStudentContentColor = contentColor;
        mTeacherColor = teacherColor;
    }

    public void setShouldShadow(boolean shouldShadow, boolean isSetFontStyle) {
        this.mShouldShadow = shouldShadow;
        this.mIsSetFontStyle = isSetFontStyle;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String textColor = "#333333";
        if (!isPortrait) {
            textColor = "#ffffff";
        }
        if (messageModellist.baijiaMsg != null && messageModellist.playerType == PlayerTypeEnum.BaiJia.getValue() && position < messageModellist.baijiaMsg.size()) {
            final IMessageModel item = messageModellist.baijiaMsg.get(position);
            boolean changeNameSize = false;//item.getFrom().getType() != LPConstants.LPUserType.Student && mIsSetFontStyle;
            String color = "#999999";
            if (item.getFrom().getType() == LPConstants.LPUserType.Teacher || item.getFrom().getType() == LPConstants.LPUserType.Assistant) {
                color = "#e9304e";
            }
            String userName = "";
            String nameColor = Utils.isEmptyOrNull(mNameColor) ? color : mNameColor;
            if (item.getFrom().getType() == LPConstants.LPUserType.Teacher) {
                String teacherColor = Utils.isEmptyOrNull(mTeacherColor) ? nameColor : mTeacherColor;
                userName = "<font color=\"" + teacherColor + "\">" + (changeNameSize ? "<small>" : "") + messageModellist.getBaijiaRoleDes(item) + item.getFrom().getName() + "：" +  (changeNameSize ? "</small>" : "") + "</font>";
            } else if (item.getFrom().getType() == LPConstants.LPUserType.Assistant) {
                String teacherColor = Utils.isEmptyOrNull(mTeacherColor) ? nameColor : mTeacherColor;
                userName = "<font color=\"" + teacherColor + "\">"+  (changeNameSize ? "<small>" : "") + messageModellist.getBaijiaRoleDes(item) + item.getFrom().getName() + "：" +  (changeNameSize ? "</small>" : "") +"</font>";
            } else if (item.getFrom().getType() == LPConstants.LPUserType.Student) {
                String studenColor = Utils.isEmptyOrNull(mStudenNameColor) ? nameColor : mStudenNameColor;
                userName = "<font color=\"" + studenColor + "\">" + messageModellist.getBaijiaRoleDes(item) + item.getFrom().getName() + "：" + "</font>";
            }

            if (holder instanceof PBTextViewHolder) {
                PBTextViewHolder textViewHolder = (PBTextViewHolder) holder;
                ChatMsgTextView tvMsg = textViewHolder.textView;
                if (item.getFrom().getType() == LPConstants.LPUserType.Student && mIsSetFontStyle) {
                    tvMsg.setTypeface(mTypeface);
                } else
                    tvMsg.setTypeface(Typeface.DEFAULT_BOLD);
                tvMsg.setPlayerType(PlayerTypeEnum.BaiJia.getValue());
                String contentColor = (mContentColor == null || TextUtils.isEmpty(mContentColor)) ? Utils.isEmptyOrNull(mStudentContentColor) ? textColor : mStudentContentColor : mContentColor;
                if (item.getFrom().getType() != LPConstants.LPUserType.Student)
                    contentColor = !Utils.isEmptyOrNull(mTeacherColor) ? "#ff3f47" : contentColor;
                tvMsg.setTextColor(Color.parseColor(contentColor));
                tvMsg.setSuperText(Html.fromHtml(userName + "   " + item.getContent().replace("\n", "<br/>")));
                if (mShouldShadow) {
                    float radio = tvMsg.getResources().getDimension(R.dimen.common_8dp);
                    tvMsg.setShadowLayer(radio, 0, 0, Color.BLACK);
                }
            } else if (holder instanceof PBImageViewHolder) {
                final PBImageViewHolder imageViewHolder = (PBImageViewHolder) holder;
                if (item.getFrom().getType() == LPConstants.LPUserType.Student && mIsSetFontStyle) {
                    imageViewHolder.tvName.setTypeface(mTypeface);
                } else
                    imageViewHolder.tvName.setTypeface(Typeface.DEFAULT_BOLD);
                imageViewHolder.tvName.setText(StringUtils.forHtml(userName));
                imageViewHolder.bindData(item.getUrl());
                ImageLoad.displayCacheImage(context, R.drawable.interact_color, item.getUrl(), imageViewHolder.ivEmoji);
                if (mShouldShadow) {
                    float radio = imageViewHolder.tvName.getResources().getDimension(R.dimen.common_8dp);
                    imageViewHolder.tvName.setShadowLayer(radio, 0, 0, Color.BLACK);
                }
            } else if (holder instanceof PBEmojiViewHolder) {
                PBEmojiViewHolder emojiViewHolder = (PBEmojiViewHolder) holder;
                if (item.getFrom().getType() == LPConstants.LPUserType.Student && mIsSetFontStyle) {
                    emojiViewHolder.tvName.setTypeface(mTypeface);
                } else
                    emojiViewHolder.tvName.setTypeface(Typeface.DEFAULT_BOLD);
                ImageLoad.displayEmojiCacheImage(context, R.drawable.live_video_expression, item.getUrl(), emojiViewHolder.ivEmoji);
                emojiViewHolder.tvName.setTextColor(Color.parseColor(textColor));
                emojiViewHolder.tvName.setText(StringUtils.forHtml(userName));
                if (mShouldShadow) {
                    float radio = emojiViewHolder.tvName.getResources().getDimension(R.dimen.common_8dp);
                    emojiViewHolder.tvName.setShadowLayer(radio, 0, 0, Color.BLACK);
                }
            }

        }/* else if (messageModellist.geeChatMsg != null && position < messageModellist.geeChatMsg.size()){
            PBTextViewHolder textViewHolder = (PBTextViewHolder) holder;
            // textViewHolder.bottomLine.setVisibility(isPortrait?View.VISIBLE:View.GONE);
            final ChatMsg item = messageModellist.geeChatMsg.get(position);
            ChatMsgTextView tvMsg = textViewHolder.textView;
            tvMsg.setPlayerType(PlayerTypeEnum.Gensee.getValue());
            String color = "#999999";

            if (RoleType.isHost(item.getSenderRole()) || RoleType.isPresentor(item.getSenderRole()) || RoleType.isPanelist(item.getSenderRole())) {
                color = "#e9304e";
            }
            String userName = "<font color=\"" + color + "\">" + messageModellist.getGenseeRoleDes(item.getSenderRole()) + item.getSender() + "：" + "</font>";

            tvMsg.setTextColor(Color.parseColor(textColor));
            tvMsg.setRichText(userName + item.getRichText());
        }*/
    }


/*    ChatMessage chatMessage = new ChatMessage();
    chatMessage.role=msg.getSenderRole();
                chatMessage.setSendUserName(msg.getSender());
                chatMessage.setSendUserId(String.valueOf(msg.getSenderId()));
                chatMessage.setRich(msg.getRichText());
                chatMessage.setText(msg.getContent());
                chatMessage.setTime(msg.getTimeStamp());
                PublicChatManager.getIns().addMsg(chatMessage);*/

    @Override
    public int getItemCount() {
        return messageModellist.getCount();
    }

    private static class PBTextViewHolder extends RecyclerView.ViewHolder {
        private ChatMsgTextView textView;
        PBTextViewHolder(View itemView) {
            super(itemView);
            textView = (ChatMsgTextView) itemView.findViewById(R.id.chat_msg_content_tv);
        }
    }

    private static class PBImageViewHolder extends PBEmojiViewHolder {

        String mPicUrl;

        PBImageViewHolder(View itemView) {
            super(itemView);
            ivEmoji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.e("mPicUrl", mPicUrl);
                    if (TextUtils.isEmpty(mPicUrl)) return;
                    LargeImageActivity.newIntent(v.getContext(), mPicUrl);
                }
            });
        }

        public void bindData(String picUrl) {
            mPicUrl = picUrl;
            LogUtils.e("mPicUrl1", mPicUrl);
        }
    }


    private static class PBEmojiViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivEmoji;
        // LinearLayout chatEmojiGroup;
        //  protected View bottomLine;

        PBEmojiViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.pb_item_chat_emoji_name);
            ivEmoji = (ImageView) itemView.findViewById(R.id.pb_item_chat_emoji);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            //    bottomLine=  itemView.findViewById(R.id.bottom_line);
            //  chatEmojiGroup = (LinearLayout) itemView.findViewById(R.id.pb_item_chat_emoji_group);
        }
    }

}
