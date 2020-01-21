package com.huatu.handheld_huatu.business.ztk_zhibo.pay;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_vod.bean.MiaoShaBean;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Crypt3Des;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.netease.hearttouch.router.HTRouter;

/**
 * Created by saiyuan on 2017/10/5.
 */

public class SecKillFragment extends BaseWebViewFragment {
    private String orderNumber = "";
    private String courseId;

    @Override
    public boolean canReadCache(){
        return false;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        courseId = args.getString("course_id");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    private void getSecKillPaymentParams() {
        mActivity.showProgress();
        ServiceProvider.getSecKillPaymentParams(compositeSubscription, orderNumber, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                PayInfo payInfo = (PayInfo) model.data;
                ConfirmPaymentFragment fragment = new ConfirmPaymentFragment();
                Bundle arg = new Bundle();
                arg.putBoolean("is_sec_kill", true);
                arg.putString("course_id", courseId);
                arg.putString("pay_number", payInfo.MoneySum);
                arg.putSerializable("pay_info", payInfo);
                fragment.setArguments(arg);
                startFragmentForResult(fragment);
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });
    }

    @Override
    protected boolean dealOverrideUrl(String dealUrl) {
        if (TextUtils.isEmpty(dealUrl)) {
            return false;
        }
        boolean isDeal = super.dealOverrideUrl(dealUrl);
        if(!isDeal && dealUrl.startsWith(getSecKillUrl() + "/order")) {
            if(dealUrl.contains("ordernum=")) {
                orderNumber = dealUrl.substring(dealUrl.indexOf("ordernum=") + "ordernum=".length());
                LogUtils.i("ordernum: " + orderNumber);
                getSecKillPaymentParams();
                return true;
            }
        }
        return false;
    }

    private static String getSecKillUrl(){
         return BuildConfig.DEBUG ? "http://sk.test.htexam.net" : SpUtils.getSecKillUrl();
    }

    public static Bundle getArgs(String courseId, String courseName,boolean toHome) {
        MiaoShaBean miaoShaBean = new MiaoShaBean();
        miaoShaBean.setUserName(SpUtils.getUname());
        miaoShaBean.setNickName(SpUtils.getNick());
        miaoShaBean.setUserFace(SpUtils.getAvatar());
        String param = "";
        try {
            Gson gson = new Gson();
            param = gson.toJson(miaoShaBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        param = Crypt3Des.encryptMode(param);


        String webUrl = getSecKillUrl() + "/api/user.php?info=" + param +"&classid=" + courseId;

        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        String mName;
        if (courseName!=null&&courseName.contains("<font color='red'>")){
            String name=courseName.replaceAll("<font color='red'>","");
            mName=name.replaceAll("</font>","");
        }else {
            mName=courseName;
        }
        bundle.putString(ARGS_STRING_TITLE, mName);
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putBoolean("toHome", toHome);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        bundle.putString("course_id", courseId);
        return bundle;
    }
}