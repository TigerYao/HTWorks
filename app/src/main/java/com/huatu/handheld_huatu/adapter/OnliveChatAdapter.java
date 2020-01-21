package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.graphics.Color;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessage;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.chat.ChatMsgTextView;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.view.CommonAdapter;

import java.util.List;

/**
 * Created by xing on 2018/4/8.
 */
@Deprecated
public class OnliveChatAdapter extends CommonAdapter<ChatMessage> {

    public OnliveChatAdapter(List<ChatMessage> data,int layoutId) {
        super(data, layoutId);
     }

     boolean isPortrait=true;
     public void setIsPortrait( boolean portrait){
         isPortrait=portrait;
     }

    @Override
    public void convert(ViewHolder holder, final ChatMessage item, final int position) {
      /*  ChatMsgTextView tvMsg = holder.getView(R.id.chat_msg_content_tv);
        tvMsg.setPlayerType(item.playerType);
        String color = "#999999";
        String textColor = "#333333";
        if (!isPortrait) {
            textColor = "#ffffff";
        }
        if (item.isHost()) {
            color = "#e9304e";
        }
        String userName = "<font color=\"" + color + "\">"+ item.getRoleDes() +item.getSendUserName() + "：" + "</font>";

        tvMsg.setTextColor(Color.parseColor(textColor));
        if (item.playerType == 1) {
            tvMsg.setText(userName + item.getText());
        } else {
            tvMsg.setRichText(userName + item.getRich());
        }*/
    }
}
