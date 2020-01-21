package com.huatu.handheld_huatu.business.me.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baijiahulian.common.permission.AppPermissions;

import com.huatu.autoapi.auto_api.factory.SobotChatHelperApiFactory;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.me.ChangePasswordActivity;
import com.huatu.handheld_huatu.business.me.ChangePhoneActivity;
import com.huatu.handheld_huatu.business.me.order.OrderActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.netease.libs.autoapi.AutoApi;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import rx.Subscriber;
/**
 * Created by cjx on 2018\9\12 0012.
 */

public class ServiceCenterFragment extends AbsSettingFragment {

    @BindView(R.id.progress_tip_bar)
    WebProgressBar mProgressLoading;

    private String mCourseId = "";
    private int mCourseType=0;

    X5WebView mWebView;
    boolean isFristLoading=true;
    private final String mHttpDomain="http://m.v.huatu.com";

    public static void lanuch(Context context) {

        Bundle arg = new Bundle();
        arg.putString(ArgConstant.COURSE_ID, "");
        arg.putInt(ArgConstant.TYPE,0);
        UIJumpHelper.jumpFragment(context, ServiceCenterFragment.class, arg);
    }

    @Override
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mCourseType=args.getInt(ArgConstant.TYPE,0);
    }


    @Override
    protected int getContentView() {
        return R.layout.comm_webview_layout;
    }

    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        if(null!=container)  container.setVisibility(View.GONE);
        return true;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
       // setHomeAsUpEnabled(true);

        initWebView(R.id.lay_webview_layout);
    }

    void initWebView(@IdRes int layId) {
        // OWebView webView = new OWebView(getActivity());
        mWebView = new X5WebView(getContext().getApplicationContext());
        ((ViewGroup) getRootView().findViewById(layId)).addView(mWebView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private String mOldquestUrl="";
    @Override
    public void requestData() {
        super.requestData();

        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                super.onProgressChanged(arg0, arg1);
                if(null!=mProgressLoading)
                    mProgressLoading.onProgressFinished(arg1);

            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//处理网页内部链接
              /*  if(mOldquestUrl.equals(url)){
                    return true;
                }*/
                mOldquestUrl=url;
                LogUtils.e("shouldOverrideUrlLoading",url+"");
                if(TextUtils.isEmpty(url)) return true;
                if(url.startsWith(mHttpDomain)){
                    if(url.contains("logistics")){
                       // LogisticDetailActivity
                       // OrderActivity.newInstance(getActivity(),2);
                        UIJumpHelper.startActivity(getContext(),OrderActivity.class);
                        StudyCourseStatistic.clickStatistic("我的->服务大厅","页面第一模块","物流信息");
                         return true;
                    }else if(url.contains("changepassword")){
                        UIJumpHelper.startActivity(getContext(),ChangePasswordActivity.class);
                        StudyCourseStatistic.clickStatistic("我的->服务大厅","页面第一模块","修改密码");
                        return true;
                    }
                    else if(url.contains("bindphone")){
                        UIJumpHelper.startActivity(getContext(),ChangePhoneActivity.class);
                        StudyCourseStatistic.clickStatistic("我的->服务大厅","页面第一模块","修改绑定手机");
                        return true;
                    }
                    else if(url.contains("invoicerequest")){
                        Intent actionIntent=new Intent(getContext(),OrderActivity.class);
                        actionIntent.putExtra("location",2);
                        getContext().startActivity(actionIntent);
                        return true;
                    }
                    else if(url.contains("nativeservice")){
                         startXN(false);
                         return true;
                    }else if(url.contains("artificialservice")){
                        startXN(true);
                        return true;
                    }else if(url.contains("close")){
                        onGoBack();
                        return true;
                    }
                }else if(url.startsWith("tel:400")){
                    tel(url);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(null!=mProgressLoading) mProgressLoading.onLoadingStart();

            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                // onLoadingFinish(webView, url);
                isFristLoading=false;
            }

        });
        String webUrl = mHttpDomain+"/customer/service_hall_new.html?username="+ UserInfoUtil.userName;
        mWebView.loadUrl(webUrl);
    }

    private void startXN(boolean switchPerson) {
      /*  Information info = new Information();
        info.setAppkey(Constant.SOBOT_APP_KEY);  //分配给App的的密钥
        info.setRobotCode(XiaoNengHomeActivity.TUTU_ROBOT_GROUPID);
        info.setSkillSetId(XiaoNengHomeActivity.HT_ZC_AFTER_SALE);
        info.setShowSatisfaction(true);
        UseInformation.buildUserInfo(info,"app服务大厅");

        //1仅机器人 2仅人工 3机器人优先 4人工优先
        info.setInitModeType(switchPerson?4:3);
       // info.setArtificialIntelligence(false);

        *//*if(switchPerson){
            //默认false：显示转人工按钮。true：智能转人工
            info.setArtificialIntelligence(true);
        }*//*
        SobotApi.startSobotChat(this.getContext(), info);*/

        SobotChatHelperApiFactory chatStub= AutoApi.getApiFactory("SobotChatHelper");
        if(chatStub!=null){
            chatStub.newInstance().talkerAfter(switchPerson,this.getContext(),""+UserInfoUtil.userId
                                         ,SpUtils.getUname(),""+SpUtils.getMobile(),SpUtils.getAvatar(),""+SpUtils.getAreaname());
        }

    }


    @Override
    public void onDestroy() {
        if(mWebView!=null) mWebView.onDestory();

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (mWebView!=null&&mWebView.canGoBack())) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void tel(final String telPhone) {

        new AppPermissions(getActivity()).request(Manifest.permission.CALL_PHONE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {  }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取打电话权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                           Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
                            startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }
}