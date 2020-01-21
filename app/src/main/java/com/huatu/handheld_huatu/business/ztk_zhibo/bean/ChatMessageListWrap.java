package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import android.text.TextUtils;

import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.imodels.IMessageModel;

import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\5\14 0014.
 */

public class ChatMessageListWrap {
    public int playerType;
   // public List<ChatMsg>       geeChatMsg;
    public List<IMessageModel> baijiaMsg;
    public boolean mIsClearAll = false;
    public boolean mIsNewMessage = false;

    public void clear(){
       // if(geeChatMsg!=null) geeChatMsg.clear();
        if(baijiaMsg!=null)  baijiaMsg.clear();
    }

    public void addAll(ChatMessageListWrap listMsg){
        playerType=listMsg.playerType;
        if(playerType==PlayerTypeEnum.BaiJia.getValue()){
            if(baijiaMsg==null) baijiaMsg=new ArrayList<>();
            baijiaMsg.addAll(listMsg.baijiaMsg);
        }else {
        /*    if(geeChatMsg==null)  geeChatMsg=new ArrayList<>();
            geeChatMsg.addAll(listMsg.geeChatMsg);*/

        }
    }

    public int getCount(){
          return ArrayUtils.size(baijiaMsg);

    }


    public  String getBaijiaRoleDes(IMessageModel msgModel){
        if (msgModel.getFrom().getType() == LPConstants.LPUserType.Teacher) {
            return "[老师]";
        } else if (msgModel.getFrom().getType() == LPConstants.LPUserType.Assistant) {
            return "[助教]" ;//chatMessage.role = ChatMessage.CHAT_ROLE_ASSISTANT;
        } else if (msgModel.getFrom().getType() == LPConstants.LPUserType.Student) {
            return "    "; // chatMessage.role = ChatMessage.CHAT_ROLE_STUDENT;
        }
        return "    ";
    }

/*
    public String getGenseeRoleDes(int role){

        if(RoleType.isHost(role)) return "【老师】";
        else if(RoleType.isPresentor(role)) return "【主讲】";
        else if(RoleType.isPanelist(role)) return "【助教】";
        else if(RoleType.isAttendee(role)) return "【学生】";
        return "【学生】";
    }*/


    public ChatMessageListWrap getOnlyTeacher(){
        //if(!isOnlyTeacher) return this;
        if(playerType== PlayerTypeEnum.BaiJia.getValue()){
            ChatMessageListWrap tmpObj=new ChatMessageListWrap();
            tmpObj.playerType=PlayerTypeEnum.BaiJia.getValue();
            tmpObj.baijiaMsg=new ArrayList<>();
            for(IMessageModel item: this.baijiaMsg){
                if ((item.getFrom().getType() == LPConstants.LPUserType.Teacher)||(item.getFrom().getType() == LPConstants.LPUserType.Assistant))
                    tmpObj.baijiaMsg.add(item);

            }
            return tmpObj;
        }else {
            ChatMessageListWrap tmpObj=new ChatMessageListWrap();
            tmpObj.playerType=PlayerTypeEnum.Gensee.getValue();
          /*  tmpObj.geeChatMsg=new ArrayList<>();
            for(ChatMsg item: this.geeChatMsg){
                if (RoleType.isHost(item.getSenderRole())||RoleType.isPresentor(item.getSenderRole())||RoleType.isPanelist(item.getSenderRole()))
                    tmpObj.geeChatMsg.add(item);

            }*/
            return tmpObj;
        }
      }

      public boolean isContains(IMessageModel model){
          if (baijiaMsg == null || baijiaMsg.size() ==0)
              return false;
          if(baijiaMsg.contains(model))
              return true;
          for(IMessageModel item: this.baijiaMsg){
              String id = model.getFrom().getUserId();
              String fromId = model.getFrom().getUserId();
              String itemTime = item.getTimestamp() == null ? item.getTime() == null ? null : item.getTime().toString() : item.getTimestamp().toString();
              String modelTime = model.getTimestamp() == null ? model.getTime() == null ? null : model.getTime().toString() : model.getTimestamp().toString();
              if (itemTime == null || modelTime == null){
                  return false;
              }
              if(TextUtils.equals(itemTime, modelTime) && TextUtils.equals(model.getContent(), item.getContent()) && TextUtils.equals(id, fromId))
                  return true;

          }
          return false;
      }

//    public void addTeacherMsg(IMessageModel model){
//        if (model == null || model.getFrom() == null || (model.getFrom().getType() != LPConstants.LPUserType.Teacher && model.getFrom().getType() != LPConstants.LPUserType.Assistant))
//            return;
//        if (teacherMsgs == null)
//            teacherMsgs = new ArrayList<>();
//        if (teacherMsgs.size() == 0){
//            teacherMsgs.add(model);
//            return;
//        }
//        for(IMessageModel item: this.teacherMsgs){
//            String id = model.getFrom().getUserId();
//            String fromId = model.getFrom().getUserId();
//            String itemTime = item.getTimestamp() == null ? item.getTime() == null ? null : item.getTime().toString() : item.getTimestamp().toString();
//            String modelTime = model.getTimestamp() == null ? model.getTime() == null ? null : model.getTime().toString() : model.getTimestamp().toString();
//            if (itemTime == null || modelTime == null){
//                return;
//            }
//            if(TextUtils.equals(itemTime, modelTime) && TextUtils.equals(model.getContent(), item.getContent()) && TextUtils.equals(id, fromId))
//                return;
//        }
//        teacherMsgs.add(model);
//    }
//
//    public void addTeacherMsgs(List<IMessageModel> models){
//        if (teacherMsgs == null)
//            teacherMsgs = new ArrayList<>();
//        if (models.size() == 0) {
//            teacherMsgs.clear();
//            return;
//        }
//        List<IMessageModel> messageModels = null;
//        for(IMessageModel item: models){
//            if ((item.getFrom().getType() == LPConstants.LPUserType.Teacher)||(item.getFrom().getType() == LPConstants.LPUserType.Assistant)) {
//                if (messageModels == null)
//                    messageModels = new ArrayList<>();
//                messageModels.add(item);
//            }
//
//        }
//        if (messageModels != null) {
//            teacherMsgs.clear();
//            teacherMsgs = messageModels;
//            messageModels = null;
//        }
//    }

}
