package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by saiyuan on 2017/8/28.
 */

public class BaseWebViewFragment extends BaseFragment {
    TopActionBar topActionBar;
    X5WebView webview;
    LinearLayout layoutBottom;
    TextView btnBottom;
    WebProgressBar mProgressLoading;

    public static final String ARGS_STRING_URL = "args_string_url";
    public static final String ARGS_STRING_TITLE = "args_string_title";
    public static final String ARGS_STRING_RIGHT = "args_string_right";

    public final int URL_TYPE_REMOTE = 0;//网络url
    public final int URL_TYPE_LOCAL = 1;//本地文件
    public final int URL_TYPE_CONTENT = 2;//内容

    private boolean isShowTitle = false;
    private boolean isShowButton = false;
    private boolean isSupportBack = false;
    protected String reqUrl;
    protected String title;
    protected String right;
    protected int urlType = URL_TYPE_REMOTE;
    private boolean toHome;
    private boolean isFromOrder = false;
    private boolean showRightShare = false;

    public boolean canReadCache(){
        return true;
    }


    public static Bundle getArgs(String webUrl, String title) {
        //String webUrl = RetrofitManager.getInstance().getBaseUrl() +"http://m.v.huatu.com/customer/explain.html";
        LogUtils.i("webUrl: " + webUrl);
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, webUrl);
        bundle.putString(ARGS_STRING_TITLE, title);
        bundle.putBoolean("isShowTitle", true);
        bundle.putBoolean("isShowButton", false);
        bundle.putBoolean("isSupportBack", false);
        bundle.putInt("url_type", 0);
        bundle.putInt("function_type", 0);
        return bundle;
    }

    public static void lanuch(Context context, String url, String title) {
        BaseFrgContainerActivity.newInstance(context,
                BaseWebViewFragment.class.getName(),
                getArgs(url, title));

    }


    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_base_webview_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        reqUrl = args.getString(ARGS_STRING_URL);
        title = args.getString(ARGS_STRING_TITLE);
        right = args.getString(ARGS_STRING_RIGHT);
        urlType = args.getInt("url_type");
        isShowButton = args.getBoolean("isShowButton", false);
        isFromOrder = args.getBoolean("isFromOrder", false);
        showRightShare = args.getBoolean("showRightShare", false);
        isShowTitle = args.getBoolean("isShowTitle", false);
        isSupportBack = args.getBoolean("isSupportBack", false);
        toHome = args.getBoolean("toHome", false);
        topActionBar = (TopActionBar) rootView.findViewById(R.id.base_web_view_title_bar);
        mProgressLoading = (WebProgressBar) rootView.findViewById(R.id.progress_w5_bar);
        webview = (X5WebView) rootView.findViewById(R.id.base_web_view_id);
        if((!canReadCache())&&(null!=webview)){
             webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        layoutBottom = (LinearLayout) rootView.findViewById(R.id.base_web_view_bottom_layout);
        btnBottom = (TextView) rootView.findViewById(R.id.base_web_view_bottom_btn);
        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBottomBtn();
            }
        });
        if (hasToolbar()) {
            initToolBar();
            topActionBar.setVisibility(View.VISIBLE);
        } else {
            topActionBar.setVisibility(View.GONE);
        }
        if (isBottomButtons()) {
            if (!isStandardBottomButton()) {
                View view = getBottomLayout();
                if (view != null) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(44));
                    layoutBottom.addView(view, lp);
                }
            }
            layoutBottom.setVisibility(View.VISIBLE);
//            webview.setOnWebViewFlingListener(new FlingWebView.onWebViewFlingListener() {
//
//                @Override
//                public void onFlingToBottom(boolean flag) {
//                    if (flag) {
//                        buttonShow();
//                    } else {
//                        buttonHide();
//                    }
//                }
//            });
        } else {
            layoutBottom.setVisibility(View.GONE);
        }
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());

    }

    protected void buttonShow() {
        layoutBottom.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(UniApplicationContext.getContext(),
                R.anim.slide_in_bottom);
        layoutBottom.startAnimation(anim);
    }

    protected void buttonHide() {
        Animation anim = AnimationUtils.loadAnimation(UniApplicationContext.getContext(),
                R.anim.slide_out_bottom);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutBottom.setVisibility(View.GONE);
            }
        });
        layoutBottom.startAnimation(anim);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        // 设置标题
        if (!TextUtils.isEmpty(title)) {
            topActionBar.setTitle(title);
        }
        // 加载网页
        if (!TextUtils.isEmpty(reqUrl)) {
            if (urlType == URL_TYPE_CONTENT) {
                webview.loadData(reqUrl, "text/html", "utf-8");
            } else if (urlType == URL_TYPE_LOCAL) {
                webview.loadUrl(reqUrl);
            } else {
                webview.loadUrl(reqUrl);
            }
        }

    }

    public boolean isBottomButtons() {
        return isShowButton;
    }

    public boolean isStandardBottomButton() {
        return true;
    }

    public View getBottomLayout() {
        return null;
    }

    public boolean hasToolbar() {
        return isShowTitle;
    }

    public void initToolBar() {
        if (isFromOrder) {
            topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        } else {
            topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        }
        topActionBar.showButtonText(right, TopActionBar.RIGHT_AREA, R.color.text_color_light);
        if (showRightShare) {
            topActionBar.showButtonImage(R.drawable.share_btn, TopActionBar.RIGHT_AREA);
        }
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (toHome) {
                    MainTabActivity.newIntent(mActivity);
                } else {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                onRightClickBtn();

            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (toHome) {
            MainTabActivity.newIntent(mActivity);
        }
        if (isSupportBack && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webview != null) {
            webview.saveState(outState);
        }
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        if (webview != null) {
            webview.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webview != null) {
            webview.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.onDestory();
            webview = null;
        }
        super.onDestroy();
    }

    protected boolean dealOverrideUrl(String dealUrl) {
        boolean isDeal = UIJumpHelper.dealOverrideUrl(mActivity, dealUrl);
        return isDeal;
    }

    /**
     * webView加载过程监听
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return dealOverrideUrl(url);
        }

   /*     @Override
        public void onLoadResource(WebView webView, String s) {
            if (dealOverrideUrl(s)){
                return;
            }
            super.onLoadResource(webView, s);
        }*/

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (null != mProgressLoading) {
                mProgressLoading.onLoadingStart();
            }
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
         /*   String mTitle = webView.getTitle();
            if (isShowTitle && TextUtils.isEmpty(title)&&!TextUtils.isEmpty(mTitle)) {
                topActionBar.setTitle(mTitle);
            }*/
        }
    }

    /**
     * webView加载进度监听
     */
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (null != mProgressLoading) {
                mProgressLoading.onProgressFinished(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (isShowTitle) {
                if (TextUtils.isEmpty(BaseWebViewFragment.this.title) && (!TextUtils.isEmpty(title)))
                    topActionBar.setTitle(title);
            }

        }
    }

    public void onClickBottomBtn() {

    }

    public void onRightClickBtn() {

    }

    public static BaseWebViewFragment newInstance(String url, String title, String right,
                                                  boolean isTitle, boolean isBottomBtn) {
        return newInstance(url, title, right, isTitle, isBottomBtn, 0, false);
    }

    public static BaseWebViewFragment newInstance(String url, String title, String right, boolean isTitle,
                                                  boolean isBottomBtn, int type, boolean isSupportBack) {
        return newInstance(url, title, right, isTitle, false, false, isBottomBtn, type, isSupportBack, 0);
    }

    public static BaseWebViewFragment newInstance(String url, String title, String right, boolean isTitle, boolean isFromOrder,
                                                  boolean showRightShare, boolean isBottomBtn, int type, boolean isSupportBack, int funcType) {
        BaseWebViewFragment fragment = new BaseWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_STRING_URL, url);
        bundle.putString(ARGS_STRING_TITLE, title);
        bundle.putString(ARGS_STRING_RIGHT, right);
        bundle.putBoolean("isShowButton", isBottomBtn);
        bundle.putBoolean("isShowTitle", isTitle);
        bundle.putBoolean("isFromOrder", isFromOrder);
        bundle.putBoolean("showRightShare", showRightShare);
        bundle.putBoolean("isSupportBack", isSupportBack);
        bundle.putInt("url_type", type);
        bundle.putInt("function_type", funcType);
        fragment.setArguments(bundle);
        return fragment;
    }
}
