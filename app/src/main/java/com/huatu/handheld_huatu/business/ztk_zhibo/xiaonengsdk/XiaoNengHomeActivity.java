package com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.huatu.autoapi.auto_api.factory.SobotChatHelperApiFactory;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.netease.libs.autoapi.AutoApi;

import java.io.Serializable;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public abstract class XiaoNengHomeActivity extends BaseActivity {

    public static String TUTU_ROBOT_GROUPID = "1";// 客服图图  我的在线客服 订单已支付
    public static String HUAHUA_ROBOT_GROUPID = "2";//客服花花 公务员（遴选生，体验课）客服  订单待支付  已取消  待分享
    public static String ZAIZAI_ROBOT_GROUPID = "3";//客服仔仔 事业单位
    public static String XIANXIAN_ROBOT_GROUPID = "4";//客服鲜鲜 其他
    public static final String HT_ZC_AFTER_SALE = "8440453721584df69dd3c12fa236b1b9";   // 售后
    public static final String HT_ZC_PRE_SALE ="f6b7f57257134b39ab5b8c3b00236c15";     // 售前

    public String customGroupId = XIANXIAN_ROBOT_GROUPID;
    Ringtone ringtonenotification;
    public String mTitleName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CommonUtils.isPad(UniApplicationContext.getContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
        LogUtils.v(this.getClass().getName() + " onCreate()");
        Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.msgnotifyvoice);
        ringtonenotification = RingtoneManager.getRingtone(this, notification);
    }

    /**
     * 打开咨询聊天页
     */
    public void startChat() {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("网络错误，请检查您的网络");
            return;
        }
        customChatParam();
       /* Information info = new Information();
        info.setAppkey(Constant.SOBOT_APP_KEY);  //分配给App的的密钥
        info.setRobotCode(customGroupId);
        info.setShowSatisfaction(true);
        if(TextUtils.equals(customGroupId, TUTU_ROBOT_GROUPID))
            info.setSkillSetId(HT_ZC_AFTER_SALE);
        else
            info.setSkillSetId(HT_ZC_PRE_SALE);
        UseInformation.buildUserInfo(info, mTitleName);
        SobotApi.startSobotChat(this, info);
*/
        SobotChatHelperApiFactory chatStub= AutoApi.getApiFactory("SobotChatHelper");
        if(chatStub!=null){
            chatStub.newInstance().startTalk(customGroupId,mTitleName,this,""+ UserInfoUtil.userId
                    , SpUtils.getUname(),""+SpUtils.getMobile(),SpUtils.getAvatar(),""+SpUtils.getAreaname());
        }
    }

    public void startChat(String title, String price, String desc, String url, String img) {
        customChatParam();
       /* Information info = new Information();
        info.setAppkey(Constant.SOBOT_APP_KEY);  //分配给App的的密钥
        //咨询内容
        ConsultingContent consultingContent = new ConsultingContent();
        //咨询内容标题，必填
        consultingContent.setSobotGoodsTitle(title);
        //咨询内容图片，选填 但必须是图片地址  http://www.li7.jpg
        consultingContent.setSobotGoodsImgUrl(img);
        //咨询来源页，必填  www.sobot.co
        consultingContent.setSobotGoodsFromUrl(url);
        //描述，选填
        consultingContent.setSobotGoodsDescribe(desc);
        //标签，选填
        consultingContent.setSobotGoodsLable("￥" + price);
        info.setShowSatisfaction(true);
        //可以设置为null
        info.setRobotCode(customGroupId);
        if(TextUtils.equals(customGroupId, TUTU_ROBOT_GROUPID))
            info.setSkillSetId(HT_ZC_AFTER_SALE);
        else
            info.setSkillSetId(HT_ZC_PRE_SALE);
        info.setConsultingContent(consultingContent);
        UseInformation.buildUserInfo(info, mTitleName);
        SobotApi.startSobotChat(this, info);*/


        SobotChatHelperApiFactory chatStub= AutoApi.getApiFactory("SobotChatHelper");
        if(chatStub!=null){

            String consultInfo=title+"и"+img+"и"+url+"и"+desc+"и"+price;
            chatStub.newInstance().startAsk(consultInfo,customGroupId,mTitleName,this,""+ UserInfoUtil.userId
                    , SpUtils.getUname(),""+SpUtils.getMobile(),SpUtils.getAvatar(),""+SpUtils.getAreaname());
        }
    }


    public void onChatMsg(boolean b, String s, String s1, String s2, long l, boolean b1, int i, String s3) {

    }

    //小能SDK 开启窗口
    public void initChat() {
    }

    public void onError(int i) {

    }

    /*@Override
    protected void onDestroy() {
        try {

        } catch (Exception e) {
            LogUtils.e(e);
            e.printStackTrace();
        } finally {
        }
        super.onDestroy();
    }*/

    public CompositeSubscription compositeSubscription = null;
    public CustomLoadingDialog progressDlg;

    public void showProgress() {
        if (!Method.isActivityFinished(this)) {
            if (progressDlg == null) {
                progressDlg = new CustomLoadingDialog(this);
            }
            progressDlg.show();
        }
    }

    public void hideProgess() {
        if (!Method.isActivityFinished(this)) {
            progressDlg.dismiss();
        }
    }

    //开发详情页添加以下功能
    private boolean isSupportFragment = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSupportFragment) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (fragments.get(i) != null) {
                        fragments.get(i).onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isConsumed = false;
        if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment != null && fragment instanceof BaseFragment && fragment.isAdded() && !fragment.isHidden()) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        isConsumed = true;
                    }
                }
            }
        }
        if (!isConsumed) {
            super.onBackPressed();
        }
    }

    public void addFragment(String curFragmentTag, Fragment fragment, int clickId, boolean addToBackState) {
        addFragment(curFragmentTag, fragment, clickId, addToBackState, true);
    }

    public void addFragment(String curFragmentTag, Fragment fragment, int clickId, boolean addToBackState, boolean isHidePrev) {
        addFragment(curFragmentTag, fragment, clickId, addToBackState, isHidePrev, true);
    }

    public void addFragment(String curFragmentTag, Fragment fragment, int clickId,
                            boolean addToBackState, boolean isHidePrev, boolean isUserPrev) {
        int fragmentContainerId = getFragmentContainerId(clickId);
        if (!isSupportFragment || fragment == null || fragmentContainerId <= 0) {
            return;
        }
        String tag = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFragment = null;
        if (!TextUtils.isEmpty(curFragmentTag)) {
            prevFragment = fragmentManager.findFragmentByTag(curFragmentTag);
        }
        Fragment old = fragmentManager.findFragmentByTag(tag);
        if (isUserPrev && old != null) {
            if (!old.isAdded()) {
                fragmentTransaction.add(fragmentContainerId, old, tag);
            } else if (old.isHidden()) {
                fragmentTransaction.show(old);
            }
        } else {
            fragmentTransaction.add(fragmentContainerId, fragment, tag);
        }
        if (isHidePrev && prevFragment != null) {
            fragmentTransaction.hide(prevFragment);
        }
        if (addToBackState) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public int getFragmentContainerId(int clickId) {
        return -1;
    }

    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    public void customChatParam() {
    }
}

