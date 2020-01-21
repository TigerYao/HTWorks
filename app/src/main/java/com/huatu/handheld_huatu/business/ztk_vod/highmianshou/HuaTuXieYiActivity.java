package com.huatu.handheld_huatu.business.ztk_vod.highmianshou;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/12/13.
 * 华图网校用户协议页面
 */

public class HuaTuXieYiActivity extends BaseActivity {

    private TopActionBar topActionBar;
    private WebView webview;
    private String xieyiUrl;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_huatuxieyi;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        xieyiUrl = getIntent().getStringExtra("xieyiUrl");
        topActionBar = (TopActionBar) findViewById(R.id.confirm_order_action_bar);
        webview = (WebView) findViewById(R.id.webView);
        initTitleBar();
        initWebView();
    }

    private void initWebView() {
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //webview自适应手机屏幕
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //控制字体大小
        webSettings.setSupportZoom(true);
        webSettings.setTextSize(WebSettings.TextSize.LARGER);
        if (!TextUtils.isEmpty(xieyiUrl)) {
            webview.loadUrl(xieyiUrl);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    HuaTuXieYiActivity.this.hideProgress();
                } else {
                    // 加载中
                    HuaTuXieYiActivity.this.showProgress();
                }

            }
        });
    }

    private void initTitleBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                HuaTuXieYiActivity.this.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    /**
     * @param context
     * @param xieyiUrl 用户协议
     */
    public static void newIntent(Context context, String xieyiUrl) {
        Intent intent = new Intent(context, HuaTuXieYiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("xieyiUrl", xieyiUrl);
        context.startActivity(intent);
    }
}
