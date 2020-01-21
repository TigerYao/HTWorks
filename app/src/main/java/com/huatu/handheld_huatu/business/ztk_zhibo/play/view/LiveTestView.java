package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
//import android.net.http.SslError;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
//import android.webkit.SslErrorHandler;
//import android.webkit.WebChromeClient;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class LiveTestView extends FrameLayout {
    public WebView mTestView;
    private VideoTestJSBrigde jsMethod;

    public LiveTestView(@NonNull Context context) {
        this(context, null);
    }

    public LiveTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveTestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initWebview(String courseId, String roomId) {
        jsMethod = new VideoTestJSBrigde();
        jsMethod.setMctx(this, courseId, roomId);
    }

    public void loadUrl(String url) {
        if(jsMethod != null)
            jsMethod.hiddenWaiting();
        createWebview();
        requestFocus();
        LogUtils.d(url);
        mTestView.loadUrl(url);
    }

    public void closePage() {
        setVisibility(View.GONE);
        removeAllViews();
        if (mTestView != null) {
            mTestView.setWebChromeClient(null);
            mTestView.setWebViewClient(null);
            mTestView.removeJavascriptInterface("JsBridge");
            mTestView.clearHistory();
            mTestView.clearCache(true);
            mTestView.freeMemory();
            mTestView = null;
        }
    }

    private void createWebview() {
        if (mTestView == null) {
            mTestView = new WebView(UniApplicationContext.getContext());
            removeAllViews();
            addView(mTestView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
           // mTestView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            mTestView.getSettings().setLoadsImagesAutomatically(true);
            mTestView.getSettings().setSaveFormData(false);
            mTestView.getSettings().setSavePassword(false);
            mTestView.getSettings().setJavaScriptEnabled(true);
            mTestView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mTestView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mTestView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
            mTestView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mTestView.getSettings().setAllowFileAccess(true);
            mTestView.getSettings().setBuiltInZoomControls(false);
            mTestView.getSettings().setDisplayZoomControls(false);
            mTestView.getSettings().setUseWideViewPort(false);
            mTestView.getSettings().setLoadWithOverviewMode(true);
            mTestView.getSettings().setDomStorageEnabled(true);
            mTestView.getSettings().setGeolocationEnabled(false);
            mTestView.getSettings().setSupportZoom(false);
            mTestView.getSettings().setBlockNetworkImage(false);
          //  mTestView.getSettings().setPluginsEnabled(true);
            mTestView.addJavascriptInterface(jsMethod, "JsBridge");
            mTestView.setWebChromeClient(new MyWebChromeClient());
            //启用mixed content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTestView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            mTestView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view,
                                                        WebResourceRequest request) {
                    //                view.loadUrl(url);
                    //                return true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.loadUrl(request.getUrl().toString());
                    } else {
                        view.loadUrl(request.toString());
                    }
                    return true;
                }

                @Override
                public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                    super.onPageStarted(webView, s, bitmap);
                    if(webView != null)
                        webView.addJavascriptInterface(jsMethod, "JsBridge");
                    LogUtils.d("onPageStarted");
                }

                @Override
                public void onPageFinished(WebView webView, String s) {
                    super.onPageFinished(webView, s);
                    if(webView != null)
                        webView.addJavascriptInterface(jsMethod, "JsBridge");
                    LogUtils.d("onPageFinished");
                }

                @Override
                public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                    sslErrorHandler.proceed();
                    LogUtils.d("onReceivedSslError");
                }

                @Override
                public void onReceivedError(WebView webView, int i, String s, String s1) {
                    super.onReceivedError(webView, i, s, s1);
                    if (i == -8 && s1.startsWith("http"))
                        loadUrl(s1);
                    LogUtils.d("onReceivedError");
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                mTestView.setBackgroundColor(0);
            }else{
                mTestView.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mTestView == null)
            return;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mTestView.setBackgroundColor(0);
        }else{
            mTestView.setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * webView加载进度监听
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }
}
