package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by saiyuan on 2017/9/7.
 */

public class CourseIntroductionView extends LinearLayout {
    private XiaoNengHomeActivity mContext;
    private View rootView;
    private int resId = R.layout.fragment_course_introduction_layout;
    private TextView tvPhoneNumber;
    WebView webview;
    private int courseId;

    public CourseIntroductionView(XiaoNengHomeActivity context) {
        super(context, null);
        mContext = context;
        init();
    }

    public CourseIntroductionView(XiaoNengHomeActivity context, int cId) {
        super(context, null);
        mContext = context;
        courseId = cId;
        init();
    }

    public CourseIntroductionView(XiaoNengHomeActivity context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        tvPhoneNumber = (TextView) rootView.findViewById(R.id.course_introduction_phone_num_tv);
        tvPhoneNumber.setText("客服电话：" + SpUtils.getCoursePhone());
        webview = (WebView) rootView.findViewById(R.id.base_web_view_id);
        String webUrl = RetrofitManager.getInstance().getBaseUrl().replace("https","http") + "c/v3/courses/" + courseId;
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setSaveFormData(false);
        webview.getSettings().setSavePassword(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBlockNetworkImage(false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webView.clearCache(true);
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.loadUrl(webUrl);
        webview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LogUtils.i("getX5WebViewExtension(): " + webview.getX5WebViewExtension());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expect);
    }

    /**
     * webView加载过程监听
     */
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return UIJumpHelper.dealOverrideUrl(mContext, url);
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
