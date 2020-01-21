package com.huatu.handheld_huatu.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by saiyuan on 2018/10/24.
 */

public class GiftWebViewActivity extends BaseActivity {

    private final static String GIFT_URL = "gift_url";

    @BindView(R.id.web_view)
    X5WebView webView;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
//                Window window = getWindow();
//                View decorView = window.getDecorView();
//                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
//                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//                decorView.setSystemUiVisibility(option);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.TRANSPARENT);
//                //导航栏颜色也可以正常设置
////                window.setNavigationBarColor(Color.TRANSPARENT);
//            } else {
//                Window window = getWindow();
//                WindowManager.LayoutParams attributes = window.getAttributes();
//                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
//                attributes.flags |= flagTranslucentStatus;
////                attributes.flags |= flagTranslucentNavigation;
//                window.setAttributes(attributes);
//            }
//        }
//    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_gift_webview_layout;
    }

    @Override
    protected void onInitView() {
//        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String url) {
                if (url.equals("http://huatu.com/")) {                   // 监听到跳转这个，就finish页面
                    onBackPressed();
                }
                super.onLoadResource(webView, url);
            }
        });
    }

    @Override
    protected void onLoadData() {
        String url = originIntent.getStringExtra(GIFT_URL);
        webView.loadUrl(url);
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    public void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.onDestory();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        // 刷新 模考/精准估分的报告
        EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.REFRESH_REPORT));
        super.onBackPressed();
    }

    /**
     * webView加载进度监听
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress >= 100) {
                hideProgress();
            } else {
                showProgress();
            }
        }

    }

    public static void newInstance(Context context, String giftUrl) {
        Intent intent = new Intent(context, GiftWebViewActivity.class);
        intent.putExtra(GIFT_URL, giftUrl);
        context.startActivity(intent);
    }
}
