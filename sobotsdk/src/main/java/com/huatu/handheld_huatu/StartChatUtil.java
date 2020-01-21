package com.huatu.handheld_huatu;

import android.content.Context;
import android.text.TextUtils;

import com.netease.libs.autoapi.anno.AutoApiClassAnno;
import com.netease.libs.autoapi.anno.AutoApiClassBuildMethodAnno;
import com.netease.libs.autoapi.anno.AutoApiConstructAnno;
import com.netease.libs.autoapi.anno.AutoApiMethodAnno;
import com.sobot.chat.R;
import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by zyl06 on 2018/10/26.
 */

@AutoApiClassAnno(name = "SobotChatHelper", allPublicStaticApi = true)
public class StartChatUtil {

    public static final String SOBOT_APP_KEY="f5a70ebb2d6a4702b853e39b06ec4f5c";


    public static String TUTU_ROBOT_GROUPID = "1";// 客服图图  我的在线客服 订单已支付
    public static String HUAHUA_ROBOT_GROUPID = "2";//客服花花 公务员（遴选生，体验课）客服  订单待支付  已取消  待分享
    public static String ZAIZAI_ROBOT_GROUPID = "3";//客服仔仔 事业单位
    public static String XIANXIAN_ROBOT_GROUPID = "4";//客服鲜鲜 其他
    public static final String HT_ZC_AFTER_SALE = "8440453721584df69dd3c12fa236b1b9";   // 售后
    public static final String HT_ZC_PRE_SALE ="f6b7f57257134b39ab5b8c3b00236c15";     // 售前

    public static final String PRODUCT_ACTION="com.huatu.handheld_huatu.productshow";

    //public static final String  PRODUCT_ACTION="com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity";

    public  static int startAsk(String consultInfo,String customGroupId,String titleName,Context context, String useId, String usename, String mobile, String avater, String areaName) {

        Information info = new Information();
        info.setAppkey(SOBOT_APP_KEY);  //分配给App的的密钥

        info.setShowSatisfaction(true);
        //可以设置为null
        info.setRobotCode(customGroupId);
        if(TextUtils.equals(customGroupId, TUTU_ROBOT_GROUPID))
            info.setSkillSetId(HT_ZC_AFTER_SALE);
        else
            info.setSkillSetId(HT_ZC_PRE_SALE);

        if(!TextUtils.isEmpty(consultInfo)&&consultInfo.contains("и")){
            String[] descArr=consultInfo.split("и");
            if(descArr!=null&&(descArr.length==5)){
                //咨询内容
                ConsultingContent consultingContent = new ConsultingContent();
                //咨询内容标题，必填
                consultingContent.setSobotGoodsTitle(descArr[0]);
                //咨询内容图片，选填 但必须是图片地址  http://www.li7.jpg
                consultingContent.setSobotGoodsImgUrl(descArr[1]);
                //咨询来源页，必填  www.sobot.co
                consultingContent.setSobotGoodsFromUrl(descArr[2]);
                //描述，选填
                consultingContent.setSobotGoodsDescribe(descArr[3]);
                //标签，选填
                consultingContent.setSobotGoodsLable("￥" + descArr[4]);
                info.setConsultingContent(consultingContent);
            }
        }
        buildUserInfo(info, titleName,useId,usename,mobile,avater,areaName);
        SobotApi.startSobotChat(context, info);
       return 1;
    }


    public static int startTalk(String customGroupId,String titleName,Context context, String useId, String usename, String mobile, String avater, String areaName){

        Information info = new Information();
        info.setAppkey(SOBOT_APP_KEY);  //分配给App的的密钥
        info.setRobotCode(customGroupId);
        info.setShowSatisfaction(true);
        if(TextUtils.equals(customGroupId, TUTU_ROBOT_GROUPID))
            info.setSkillSetId(HT_ZC_AFTER_SALE);
        else
            info.setSkillSetId(HT_ZC_PRE_SALE);
         buildUserInfo(info, titleName,useId,usename,mobile,avater,areaName);
        SobotApi.startSobotChat(context, info);
        return 1;
    }

    public static int talkerAfter(boolean switchPerson, Context context, String useId, String usename, String mobile, String avater, String areaName) {
        Information info = new Information();
        info.setAppkey(SOBOT_APP_KEY);  //分配给App的的密钥
        info.setRobotCode(TUTU_ROBOT_GROUPID);
        info.setSkillSetId(HT_ZC_AFTER_SALE);
        info.setShowSatisfaction(true);
        buildUserInfo(info,"app服务大厅",useId,usename,mobile,avater,areaName);

        //1仅机器人 2仅人工 3机器人优先 4人工优先
        info.setInitModeType(switchPerson?4:3);
        // info.setArtificialIntelligence(false);

       /* if(switchPerson){
            //默认false：显示转人工按钮。true：智能转人工
            info.setArtificialIntelligence(true);
        } */
        SobotApi.startSobotChat(context, info);
        return 1;
    }

    private static void buildUserInfo(Information information,String resource,
                                       String useId,String usename,String mobile,String avater,String areaName){

        Information info =information;
        //用户编号
        //注意：uid为用户唯一标识，不能传入一样的值
        info.setUid(""+useId);
        //用户昵称，选填
        info.setUname(""+usename);//getNick
        //用户姓名，选填
        info.setRealname(""+usename);
        //用户电话，选填
        info.setTel(""+mobile);
        //用户邮箱，选填
        //info.setEmail(""+SpUtils.getEmail());
        //自定义头像，选填
        info.setFace(""+avater);
        //用户QQ，选填
        info.setQq("");
        //用户备注，选填
        info.setRemark(""+areaName);
        //访问着陆页标题，选填
        info.setVisitTitle(""+resource);
        //访问着陆页链接地址，选填
        info.setVisitUrl("");
        //自定义信息，后台需要
        Map<String, String> customInfo = new HashMap<>();
        customInfo.put("customInfo", usename);
        customInfo.put("mobile", mobile);
        info.setCustomInfo(customInfo);

        //返回时是否弹出满意度评价
        // info.setShowSatisfaction(true);
        //Map<String,String> customInfo = new HashMap<String, String>();

        //设置 聊天界面左边气泡背景颜色
        SobotUIConfig.sobot_chat_left_bgColor =R.color.chat_left_bgColor;;// R.color.gray_divider;
        //设置 聊天界面右边气泡背景颜色
        SobotUIConfig.sobot_chat_right_bgColor =R.color.chat_right_bgColor;// R.color.red250;

        //        SobotUIConfig.sobot_serviceImgId = R.drawable.sobot_failed_normal;
//        SobotUIConfig.sobot_titleTextColor = R.color.sobot_color_evaluate_text_btn;
//        SobotUIConfig.sobot_moreBtnImgId  = R.drawable.sobot_btn_back_selector;
//        SobotUIConfig.sobot_titleBgColor = R.color.sobot_title_category_unselect_color;
//        SobotUIConfig.sobot_chat_bottom_bgColor = R.color.sobot_text_delete_hismsg_color;
//
//        SobotUIConfig.sobot_chat_left_textColor = R.color.sobot_text_delete_hismsg_color;
//        SobotUIConfig.sobot_chat_left_link_textColor = R.color.sobot_title_category_unselect_color;
//        SobotUIConfig.sobot_chat_left_bgColor = R.color.sobot_color_suggestion_history;
//
//        SobotUIConfig.sobot_chat_right_bgColor = R.color.sobot_title_category_unselect_color;
        SobotUIConfig.sobot_chat_right_link_textColor = R.color.sobot_bg_white;// R.color.white;  0xffffff

       // return information;

    }
}
