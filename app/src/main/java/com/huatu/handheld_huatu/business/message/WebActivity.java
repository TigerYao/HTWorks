package com.huatu.handheld_huatu.business.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;

import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
@Deprecated
public class WebActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "WebActivity";
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.webview)
    X5WebView webview;
    @BindView(R.id.loadview)
    View loadview;
    @BindView(R.id.progressBar_live)
    ProgressBar progressBar_live;


    private IX5WebChromeClient.CustomViewCallback mCallback;
    private View mView;
    public static final String ID = "id";
    public static final String TITLE = "title";
    private String mTitle;
    private long mId;
    HttpService mZtkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        setListener();
        initDatas();
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
    }

    private void initDatas() {
        initWebView();

        mZtkService = RetrofitManager.getInstance().getService();
        mId = getIntent().getLongExtra(ID, 0);
        mTitle = getIntent().getStringExtra(TITLE);

        tv_title_titlebar.setText("消息");

        loadview.setVisibility(View.VISIBLE);
        webview.setVisibility(View.GONE);
        Observable<ResponseBody> messageDetail = mZtkService.getMessageDetail(mId);
        messageDetail.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        loadview.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        loadview.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);

                        String res = "";
                        try {
                            res = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        webview.loadDataWithBaseURL(null, res, "text/html", "utf-8", null);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left_topbar:
                finish();
                break;
        }
    }

    private void hideCustomView() {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            parent.removeView(mView);
            parent.addView(webview);
            mView = null;

            if (mCallback != null) {
                mCallback.onCustomViewHidden();
                mCallback = null;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initWebView() {
        WebSettings settings = webview.getSettings();
        settings.setTextSize(WebSettings.TextSize.LARGEST);//通过设置WebSettings，改变HTML中文字的大小

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("http://v.huatu.com/h5/detail.php?rid")) {
                    String rid = url.substring(37);
//                    Intent intent = new Intent(WebActivity.this,
//                           BuyDetailsActivity.class);
                    Intent intent = new Intent(WebActivity.this,
                            BaseIntroActivity.class);
                    intent.putExtra("rid", rid);
                    startActivity(intent);
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar_live.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar_live.setVisibility(View.GONE);
            }
        });

        webview.setWebChromeClient(new  WebChromeClient() {

            // 配置权限 （在WebChromeClinet中实现）
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissionsCallback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            // 扩充数据库的容量（在WebChromeClinet中实现）
            @Override
            public void onExceededDatabaseQuota(String url,
                                                String databaseIdentifier, long currentQuota,
                                                long estimatedSize, long totalUsedQuota,
                                                WebStorage.QuotaUpdater quotaUpdater) {

                quotaUpdater.updateQuota(estimatedSize * 2);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar_live.setProgress(newProgress);
            }

            // 扩充缓存的容量
            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded,
                                                 long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

                quotaUpdater.updateQuota(spaceNeeded * 2);
            }

            // Android 使WebView支持HTML5 Video（全屏）播放的方法
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                if (mCallback != null) {
                    mCallback.onCustomViewHidden();
                    mCallback = null;

                    return;
                }

                ViewGroup parent = (ViewGroup) webview.getParent();
                parent.removeView(webview);
                parent.addView(view);
                mView = view;
                mCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        if (mView != null) {
            hideCustomView();
        }

        webview.onPause();
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webview != null) {
             webview.onDestory();
        }
    }

    /**
     * 跳转该页面
     *
     * @param context
     * @param title   web页面标题
     * @param id      消息对应id
     */
    public static void newIntent(Context context, String title, long id) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(ID, id);

        context.startActivity(intent);
    }


}
