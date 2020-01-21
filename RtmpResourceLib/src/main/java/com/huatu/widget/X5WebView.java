package com.huatu.widget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.gensee.rtmpresourcelib.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Administrator on 2018\6\28 0028.
 */

public class X5WebView extends WebView {
    private final int minimumFontSize = 8;
    private final int minimumLogicalFontSize = 8;
    private final int defaultFontSize = 16;
    private final int defaultFixedFontSize = 13;



    public X5WebView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        init(var1);
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public X5WebView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 获取程序版本号
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    private void init(Context context){

        WebSettings webSetting=this.getSettings();
        this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webSetting.setLoadsImagesAutomatically(true);
       //  webSetting.setSaveFormData(false);
       //  webSetting.setSavePassword(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
       //  webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setMinimumFontSize(minimumFontSize);
        webSetting.setMinimumLogicalFontSize(minimumLogicalFontSize);
        webSetting.setDefaultFontSize(defaultFontSize);
        webSetting.setDefaultFixedFontSize(defaultFixedFontSize);
        webSetting.setTextSize(WebSettings.TextSize.NORMAL);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setAllowFileAccess(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setSupportZoom(true);
        webSetting.setBlockNetworkImage(false);

        //cnzz 异步统计代码 https://www.jianshu.com/p/e6d4d9c6cf9f
        //webview 跨域问题  允许cnzz的  同步调用
        webSetting.setAllowUniversalAccessFromFileURLs(true);

        webSetting.setUserAgentString("HuaTuOnline/"+getAppVersion(context)+" "+webSetting.getUserAgentString());
       // webSetting.setAllowUniversalAccessFromFileURLs(true);
        String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
        //https://blog.csdn.net/tong_hou/article/details/80283541
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode( android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
      //  webSetting.setAppCacheMaxSize(1024 * 1024 * 20);// 20m Long.MAX_VALUE
        webSetting.setDatabasePath(dir);
        webSetting.setAppCachePath(dir);
      /*  if(this.getX5WebViewExtension() != null){
            hasX5nited=true;
        }*/
    }

    public void onDestory(){
             if (this != null) {
                // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
                // destory()
                ViewParent parent = this.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(this);
                }

                this.stopLoading();
                // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
                 this.getSettings().setJavaScriptEnabled(false);
                // webView.clearHistory();
                //  webView.clearView();
                this.removeAllViews();

                try {
                    this.destroy();
                } catch (Throwable ex) {

                }
            }

    }
}
