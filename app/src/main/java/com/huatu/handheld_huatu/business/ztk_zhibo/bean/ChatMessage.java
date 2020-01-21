package com.huatu.handheld_huatu.business.ztk_zhibo.bean;


import com.huatu.handheld_huatu.business.ztk_zhibo.play.chat.AbsChatMessage;

/**
 * Created by saiyuan on 2017/9/14.
 */

public class ChatMessage extends AbsChatMessage {
    public int playerType;
    public String msgId;
    public int role;

    public static final int CHAT_ROLE_STUDENT = 3;//学生
    public static final int CHAT_ROLE_HONOR = 4; //嘉宾
    public static final int CHAT_ROLE_ASSISTANT = 5;
    public static final int CHAT_ROLE_TEACHER = 6;
    public static final int CHAT_ROLE_ASSISTANT_1 = 7;



/*2.5.3 RoleType 角色类型
    isHost(int role)     //是否组织者/老师   true是/false不是
    isPresentor(int role) //是否主讲          true是/false不是
    isPanelist(int role)  //是否嘉宾/助教    true是/false不是
    isAttendee(int role) //是否为客户端学生/普通与会者 true是/false不是
    isAttendeeWeb(int role) //是否为web学生/普通与会者  true是/false不是
    e.g  boolean isHost = RoleType.isHost(role);
    点播不是全部都有role的   这个是通用定义，不一定有值的*/



    //http://dev.baijiayun.com/default/wiki/detail/29
    //'用户类型 0-学生 1-老师 2-助教'


   // com.gensee.common.RoleType
  /*  public String getRoleDes(){
         if(playerType==0){
             if(RoleType.isHost(role)) return "【老师】";
             else if(RoleType.isPresentor(role)) return "【主讲】";
             else if(RoleType.isPanelist(role)) return "【助教】";
             else if(RoleType.isAttendee(role)) return "【学生】";
                return "【学生】";

        }else{
             switch (role) {
                case CHAT_ROLE_STUDENT:
                    return "【学生】";
                case CHAT_ROLE_HONOR:
                    return "【嘉宾】";
                case CHAT_ROLE_TEACHER:
                    return "【老师】";
                case CHAT_ROLE_ASSISTANT_1:
                case CHAT_ROLE_ASSISTANT:
                    return "【助教】";
                default:
                    return "";
            }
        }
     }*/

   /* public boolean isHost() {
        if(playerType==0) return RoleType.isHost(role)||RoleType.isPresentor(role)||RoleType.isPanelist(role)  ;
        return role == CHAT_ROLE_HONOR //嘉宾
                || role == CHAT_ROLE_ASSISTANT//组织者
                || role == CHAT_ROLE_ASSISTANT_1//组织者
                || role == CHAT_ROLE_TEACHER;//主讲人
    }*/
}
