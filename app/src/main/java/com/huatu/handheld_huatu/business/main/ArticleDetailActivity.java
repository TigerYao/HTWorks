package com.huatu.handheld_huatu.business.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.utils.CreamArticleCache;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleDetail;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.tencent.mm.opensdk.utils.Log;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by saiyuan on 2019/2/21.
 */

public class ArticleDetailActivity extends BaseActivity {
    private static final String TAG = "ArticleDetailActivitys";
    @BindView(R.id.tv_top_bar)
    TopActionBar tv_top_bar;
    @BindView(R.id.wv_article_content)
    X5WebView wv_article_content;
    @BindView(R.id.iv_advertise)
    ImageView iv_advertise;
    @BindView(R.id.tv_read_num)
    TextView tv_read_num;
    @BindView(R.id.tv_like_num)
    TextView tv_like_num;
    private long articleId;
    private CreamArticleDetail mData;
    private int type;
    private int mFirstType;

    @BindView(R.id.progress_tip_bar)
    WebProgressBar mProgressLoading;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_article_detail_layout;
    }

    @Override
    protected void onInitView() {
        articleId = getIntent().getLongExtra("articleId", 0);
        CreamArticleCache.getInstance().readCacheToFile();
        type = CreamArticleCache.getInstance().getCache(articleId + "");
        mFirstType = CreamArticleCache.getInstance().getCache(articleId + "");
        initTopBar();
        initListener();
        initWebView();
    }

    @Override
    protected void onLoadData() {
        loadArticleDetail();
    }

    private void loadArticleDetail() {
        showProgress();
        ServiceProvider.getCreamArticleDetail(compositeSubscription, articleId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                if (model != null) {
                    mData = (CreamArticleDetail) model.data;
                    if (mData!=null&&wv_article_content != null && mData.staticPageUrl != null) {
                        Log.i(TAG,"-----"+mData.staticPageUrl);
                        wv_article_content.loadUrl(mData.staticPageUrl);
                    }else {
                        ToastUtils.showEssayToast("出错啦，请退出重试");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();

            }
        });

    }

    private void freshUI() {
        if (mData != null) {
            if (iv_advertise != null &&! TextUtils.isEmpty(mData.adImg)) {
                GlideApp.with(this).load(mData.adImg).into(iv_advertise);
            }else {
                iv_advertise.setVisibility(View.GONE);
            }

            if (tv_read_num != null && mData.click != 0) {
                tv_read_num.setText("阅读 " + mData.click);
            }

           if (tv_like_num!=null){
                tv_like_num.setVisibility(View.VISIBLE);
           }
            if (tv_like_num != null&&mData.goodPost!=0) {
                if (type == 1) {
                    //没有赞
                    tv_like_num.setText(mData.goodPost + "");
                    tv_like_num.setTextColor(ContextCompat.getColor(this, R.color.black250));
                    Drawable drawable = getApplicationContext().getResources().getDrawable(R.mipmap.mip_like);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_like_num.setCompoundDrawables(drawable, null, null, null);
                } else {
                    //已赞
                    tv_like_num.setText(mData.goodPost + "");
                    tv_like_num.setTextColor(ContextCompat.getColor(this, R.color.red250));
                    Drawable drawable = getApplicationContext().getResources().getDrawable(R.mipmap.mip_like_yes);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_like_num.setCompoundDrawables(drawable, null, null, null);
                }
            }
        }

    }

    private void initTopBar() {
        tv_top_bar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                ArticleDetailActivity.this.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    private void initListener() {
        tv_like_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceProvider.getPraiseDetail(compositeSubscription, articleId, type, new NetResponse() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtil.showToast("哎呀失败了，请稍后重试");
                    }

                    @Override
                    public void onListSuccess(BaseListResponseModel model) {
                        super.onListSuccess(model);
                        changeLikeUI();

                    }

                });
            }
        });
        iv_advertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mData!=null&&mData.classInfo!=null){
                if (NetUtil.isConnected()){
                int videoType = 1;
                if (!TextUtils.isEmpty(mData.classInfo.videoType)) {
                    videoType = Integer.parseInt(mData.classInfo.videoType);
                }
                if (mData.classInfo.isCollect) {
                    CourseCollectSubsetFragment.show(ArticleDetailActivity.this,
                            mData.classInfo.collectId, mData.classInfo.title, mData.classInfo.title, videoType);
                } else if (mData.classInfo.secondKill) {
                    BaseFrgContainerActivity.newInstance(ArticleDetailActivity.this,
                            SecKillFragment.class.getName(),
                            SecKillFragment.getArgs(mData.classInfo.classId, mData.classInfo.title, false));
                } else {
                    int collageActiveId = 0;
                    if (!TextUtils.isEmpty(mData.classInfo.collageActiveId)) {
                        collageActiveId = Integer.parseInt(mData.classInfo.collageActiveId);
                    }
                    Intent intent = new Intent(ArticleDetailActivity.this, BaseIntroActivity.class);
                    intent.putExtra("NetClassId", mData.classInfo.classId);//lesson.NetClassId
                    intent.putExtra("course_type", videoType);
                    intent.putExtra("price", mData.classInfo.actualPrice);
                    intent.putExtra("originalprice", mData.classInfo.price);
                    intent.putExtra("collageActiveId", collageActiveId);
                    intent.putExtra("saleout", mData.classInfo.isSaleOut);
                    intent.putExtra("rushout", mData.classInfo.isRushOut);
                    intent.putExtra("daishou", mData.classInfo.isTermined);
                   startActivity(intent);
                }
            } else {
                ToastUtils.showShort("网络错误，请检查您的网络");
            }
            }
            }
        });
    }

    @JavascriptInterface
    public void resize(final float height) {

        LogUtils.e("resize",height+"");
        if(height==0){
            return;
        }
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv_article_content.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                        (int) (height * getResources().getDisplayMetrics().density)));
              /*  if (null != mOnHeaderLoadListener) {

                    mOnHeaderLoadListener.onHeaderSuccess();
                }*/
            }
        });
    }


    private void initWebView() {

        wv_article_content.getSettings().setJavaScriptEnabled(true);
    /*    wv_article_content.getSettings().setAllowFileAccess(true);
        wv_article_content.getSettings().setBuiltInZoomControls(true);
        wv_article_content.getSettings().setDisplayZoomControls(false);
        wv_article_content.getSettings().setLoadWithOverviewMode(true);
        wv_article_content.getSettings().setUseWideViewPort(true);
        wv_article_content.getSettings().setBlockNetworkImage(false);
        wv_article_content.getSettings().setAppCacheEnabled(false);
        wv_article_content.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_article_content.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
        wv_article_content.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
     //   wv_article_content.clearCache(true);
       // wv_article_content.clearHistory();
      //  wv_article_content.requestFocus();
        wv_article_content.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                super.onProgressChanged(arg0, arg1);
                if(null!=mProgressLoading)
                    mProgressLoading.onProgressFinished(arg1);
                if(arg1==100){
                    arg0.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
                }

            }

        });

        wv_article_content.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                //showProgress();
                if(null!=mProgressLoading) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                webView.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
                super.onPageFinished(webView, s);
               // hideProgress();

                freshUI();
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不另跳浏览器
//                // 在2.3上面不加这句话，可以加载出页面，在4.0上面必须要加入，不然出现白屏
//                if (url.startsWith("http://") || url.startsWith("https://")) {
//                    view.loadUrl(url);
//                    wv_article_content.stopLoading();
//                    return true;
//                }
//                return false;
//            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(error.getPrimaryError() == android.net.http.SslError.SSL_INVALID ){// 校验过程遇到了bug
                    handler.proceed();
                }else{
                    handler.cancel();
                }
            }

        });
        wv_article_content.addJavascriptInterface(this, "App");
    }

    private void changeLikeUI() {
        if (tv_like_num != null) {
            if (type == -1) {
                if (mFirstType==-1){
                    //进入时已赞
                    if (mData.goodPost>1){
                        tv_like_num.setText(mData.goodPost-1+ "");
                    }else {
                        tv_like_num.setText("");
                    }
                }else {
                    //进入时未赞
                    if (mData.goodPost!=0){
                        tv_like_num.setText(mData.goodPost+ "");
                    }else {
                        tv_like_num.setText("");
                    }
                }
                tv_like_num.setTextColor(ContextCompat.getColor(this, R.color.black250));
                Drawable drawable = getApplicationContext().getResources().getDrawable(R.mipmap.mip_like);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_like_num.setCompoundDrawables(drawable, null, null, null);
                type = 1;
                CreamArticleCache.getInstance().putCache(articleId + "", type);
                CreamArticleCache.getInstance().writeCacheToFile();
            } else {
                if (mFirstType==-1){
                    //进入时已赞
                    tv_like_num.setText(mData.goodPost+"");
                }else {
                    //进入时未赞
                    tv_like_num.setText(mData.goodPost+1+"");
                }
                tv_like_num.setTextColor(ContextCompat.getColor(this, R.color.red250));
                Drawable drawable = getApplicationContext().getResources().getDrawable(R.mipmap.mip_like_yes);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_like_num.setCompoundDrawables(drawable, null, null, null);
                type = -1;
                CreamArticleCache.getInstance().putCache(articleId + "", type);
                CreamArticleCache.getInstance().writeCacheToFile();
            }
        }
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

    public static void newInstance(Context context, long articleId) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra("articleId", articleId);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (mData!=null){
            mData=null;
        }
        if (wv_article_content != null && wv_article_content.getParent() != null) {
            ((ViewGroup) wv_article_content.getParent()).removeView(wv_article_content);

            wv_article_content.stopLoading();
            wv_article_content.loadUrl("about:blank");
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wv_article_content.getSettings().setJavaScriptEnabled(false);
            // webView.clearHistory();
            //  webView.clearView();
            wv_article_content.removeAllViews();
            wv_article_content.destroy();
            wv_article_content = null;
            WebStorage.getInstance().deleteAllData();
        }
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);

    }
}
