package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.utils.CreamArticleCache;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.CollectionResultBean;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleDetail;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.widget.WebProgressBar;
import com.levylin.detailscrollview.views.DetailSingleWebView;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cjx on 2019\4\3 0003.
 */

public class DetailScrollViewFragment extends AbsSettingFragment {

    @BindView(R.id.iv_advertise)
    ImageView iv_advertise;
    @BindView(R.id.tv_read_num)
    TextView tv_read_num;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.tv_like_num)
    TextView tv_like_num;

    @BindView(R.id.progress_tip_bar)
    WebProgressBar mProgressLoading;

    private DetailSingleWebView mX5WebView;
    private boolean isCollect;                  // 是否收藏了本文章

    boolean isFirstLoading = true;

    private long mArticleId;
    private CreamArticleDetail mData;
    private int type;
    private int mFirstType;

    public static void lanuch(Context context, Long articleId) {
        Bundle arg = new Bundle();
        arg.putLong(ArgConstant.KEY_ID, articleId);
        UIJumpHelper.jumpFragment(context, DetailScrollViewFragment.class, arg);
    }

    @Override
    public void onDestroy() {
        if (mX5WebView != null) mX5WebView.onDestory();
        super.onDestroy();
    }

    @Override
    protected void parserParams(Bundle args) {
        mArticleId = args.getLong(ArgConstant.KEY_ID, 0);
    }

    @Override
    protected int getContentView() {
        return R.layout.scroll_webview_layout;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
    }

    @Override
    public void requestData() {
        super.requestData();
        type = CreamArticleCache.getInstance().getCache(mArticleId + "");
        mFirstType = CreamArticleCache.getInstance().getCache(mArticleId + "");
        mX5WebView = this.findViewById(R.id.test_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mX5WebView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        mX5WebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                super.onProgressChanged(arg0, arg1);
                if (mProgressLoading != null) mProgressLoading.onProgressFinished(arg1);
            }
        });
        mX5WebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//处理网页内部链接

                if (TextUtils.isEmpty(url)) return true;
                if (url.startsWith("tbopen:") || (!url.startsWith("http"))) {//禁止跳转tbopen
                    return true;
                }

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//                super.onReceivedSslError(webView, sslErrorHandler, sslError);
                sslErrorHandler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressLoading != null) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                // onLoadingFinish(webView, url);
                isFirstLoading = false;
                ViewGroup.LayoutParams params = mX5WebView.getLayoutParams();
                params.width = getResources().getDisplayMetrics().widthPixels;
                params.height = mX5WebView.getHeight();
                mX5WebView.setLayoutParams(params);
                freshUI();
            }
        });
        loadArticleDetail();
        initListener();
    }

    private void loadArticleDetail() {
        ServiceProvider.getCreamArticleDetail(getSubscription(), mArticleId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);

                if (model != null) {
                    mData = (CreamArticleDetail) model.data;
                    if (mData != null && mX5WebView != null && mData.staticPageUrl != null) {
                        mX5WebView.loadUrl(mData.staticPageUrl);
                    } else {
                        ToastUtils.showEssayToast("出错啦，请退出重试");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("出错啦，请退出重试");
            }
        });

    }

    private void initListener() {
        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.checkLogin(getContext())) {
                    return;
                }
                showLoading();
                Observable<BaseResponseModel<CollectionResultBean>> observable;
                if (!isCollect) {
                    observable = RetrofitManager.getInstance().getService().collectArticle(mArticleId);
                } else {
                    observable = RetrofitManager.getInstance().getService().cancelCollectArticle(mArticleId);
                }

                getSubscription()
                        .add(observable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<BaseResponseModel<CollectionResultBean>>() {
                                    @Override
                                    public void onCompleted() {
                                        hideLoading();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        hideLoading();
                                        ToastUtil.showToast("收藏失败");
                                    }

                                    @Override
                                    public void onNext(BaseResponseModel<CollectionResultBean> bean) {
                                        hideLoading();
                                        if (bean.code == ApiErrorCode.ERROR_SUCCESS && bean.data != null) {
                                            String msg = bean.data.msg;
                                            ToastUtil.showToast(msg);
                                            if (msg.equals("收藏成功")) {
                                                isCollect = true;
                                                ivCollect.setImageResource(R.mipmap.new_c_yes);
                                            } else {
                                                isCollect = false;
                                                ivCollect.setImageResource(R.mipmap.new_c_no);
                                            }
                                        } else {
                                            ToastUtil.showToast(bean.message);
                                        }
                                    }
                                }));
            }
        });
        tv_like_num.setOnClickListener(new View.OnClickListener() {
            @LoginTrace(type = 0)
            @Override
            public void onClick(View v) {
                ServiceProvider.getPraiseDetail(getSubscription(), mArticleId, type, new NetResponse() {
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
                if (mData != null && mData.classInfo != null) {
                    if (NetUtil.isConnected()) {
                        int videoType = 1;
                        if (!TextUtils.isEmpty(mData.classInfo.videoType)) {
                            videoType = Integer.parseInt(mData.classInfo.videoType);
                        }
                        if (mData.classInfo.isCollect) {
                            CourseCollectSubsetFragment.show(getContext(),
                                    mData.classInfo.collectId, mData.classInfo.title, mData.classInfo.title, videoType);
                        } else if (mData.classInfo.secondKill) {
                            BaseFrgContainerActivity.newInstance(getContext(),
                                    SecKillFragment.class.getName(),
                                    SecKillFragment.getArgs(mData.classInfo.classId, mData.classInfo.title, false));
                        } else {
                            int collageActiveId = 0;
                            if (!TextUtils.isEmpty(mData.classInfo.collageActiveId)) {
                                collageActiveId = Integer.parseInt(mData.classInfo.collageActiveId);
                            }
                            Intent intent = new Intent(getContext(), BaseIntroActivity.class);
                            intent.putExtra("rid", mData.classInfo.classId);
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

    private void changeLikeUI() {
        if (tv_like_num != null) {
            if (type == -1) {
                if (mFirstType == -1) {
                    //进入时已赞
                    if (mData.goodPost > 1) {
                        tv_like_num.setText(mData.goodPost - 1 + "");
                    } else {
                        tv_like_num.setText("");
                    }
                } else {
                    //进入时未赞
                    if (mData.goodPost != 0) {
                        tv_like_num.setText(mData.goodPost + "");
                    } else {
                        tv_like_num.setText("");
                    }
                }
                tv_like_num.setTextColor(ContextCompat.getColor(getContext(), R.color.black250));
                Drawable drawable = ResourceUtils.getDrawable(R.mipmap.mip_like);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_like_num.setCompoundDrawables(drawable, null, null, null);
                type = 1;
                CreamArticleCache.getInstance().putCache(mArticleId + "", type);
                CreamArticleCache.getInstance().writeCacheToFile();
            } else {
                if (mFirstType == -1) {
                    //进入时已赞
                    tv_like_num.setText(mData.goodPost + "");
                } else {
                    //进入时未赞
                    tv_like_num.setText(mData.goodPost + 1 + "");
                }
                tv_like_num.setTextColor(ContextCompat.getColor(getContext(), R.color.red250));
                Drawable drawable = ResourceUtils.getDrawable(R.mipmap.mip_like_yes);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_like_num.setCompoundDrawables(drawable, null, null, null);
                type = -1;
                CreamArticleCache.getInstance().putCache(mArticleId + "", type);
                CreamArticleCache.getInstance().writeCacheToFile();
            }
        }
    }

    private void freshUI() {
        if (mData != null) {
            if (iv_advertise != null && !TextUtils.isEmpty(mData.adImg)) {
                iv_advertise.setVisibility(View.VISIBLE);
                GlideApp.with(this).load(mData.adImg).into(iv_advertise);
            } else {
                iv_advertise.setVisibility(View.GONE);
            }

            if (tv_read_num != null && mData.click != 0) {
                tv_read_num.setText("阅读 " + mData.click);
            }

            if (tv_like_num != null) {
                tv_like_num.setVisibility(View.VISIBLE);
            }
            if (tv_like_num != null && mData.goodPost != 0) {
                if (type == 1) {
                    //没有赞
                    tv_like_num.setText(mData.goodPost + "");
                    tv_like_num.setTextColor(ContextCompat.getColor(getContext(), R.color.black250));
                    Drawable drawable = ResourceUtils.getDrawable(R.mipmap.mip_like);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_like_num.setCompoundDrawables(drawable, null, null, null);
                } else {
                    //已赞
                    tv_like_num.setText(mData.goodPost + "");
                    tv_like_num.setTextColor(ContextCompat.getColor(getContext(), R.color.red250));
                    Drawable drawable = ResourceUtils.getDrawable(R.mipmap.mip_like_yes);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_like_num.setCompoundDrawables(drawable, null, null, null);
                }
            }
            ivCollect.setVisibility(View.VISIBLE);
            isCollect = mData.isCollection;
            if (isCollect) {
                ivCollect.setImageResource(R.mipmap.new_c_yes);
            } else {
                ivCollect.setImageResource(R.mipmap.new_c_no);
            }
        }
    }
}
