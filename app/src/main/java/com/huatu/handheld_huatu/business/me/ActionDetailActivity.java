package com.huatu.handheld_huatu.business.me;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.activity.ExamPaperActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.mvpmodel.me.ActionListBean;
import com.huatu.handheld_huatu.mvpmodel.me.ActionNumberAddBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.music.utils.LogUtil;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.netease.hearttouch.router.HTPageRouterCall;
import com.netease.hearttouch.router.HTRouter;
import com.netease.hearttouch.router.HTRouterEntry;
import com.netease.hearttouch.router.HTRouterManager;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.Serializable;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 活动中心详情
 */
@HTRouter(url = {"ztk://h5/active"}, needLogin = false)
public class ActionDetailActivity extends BaseActivity {
    private static final String TAG = "ActionDetailActivitys";
    private LinearLayout ll_prompt;
    private ImageView image_empty;
    private TextView text_faile;

    private TextView tv_title_titlebar;
    private String mTitlename;
    private X5WebView mWebview;
    private RelativeLayout rl_left_topbar;
    private String link;
    private long mId;
    private boolean mToHome;

    WebProgressBar mProgressLoading;
    private FrameLayout mFlVideoContainer;

    private boolean mOnlyShow = false;
    private int mActionType=0;
    private boolean mJumpRefresh = false;

    @Override
    protected int onSetRootViewId() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        return R.layout.activity_action_detail;
    }

    private final class JSInterface{
        @JavascriptInterface
        public void showLogin() {
             Method.runOnUiThread(ActionDetailActivity.this,new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Constant.APP_LOGIN_ACTION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ArgConstant.QUICK_LOGIN,true);
                    mJumpRefresh=true;
                    ActionDetailActivity.this.startActivityForResult(intent,10033);
                }
            });
        }

        @JavascriptInterface
        public void showShare(final String title,final String des, final String url,final String thumb) {
            Method.runOnUiThread(ActionDetailActivity.this,new Runnable() {
                @Override
                public void run() {
                    //ShareUtil.test(ActionDetailActivity.this,"",des,title,url);
                    ShareUtil.test(ActionDetailActivity.this, "", des, title, url, thumb, null, null);
                }
            });

        }
    }

    void initWebView() {
        mWebview = new X5WebView(this);//
        mWebview.addJavascriptInterface(new JSInterface(), "App");

        mWebview.setOnLongClickListener(new View.OnLongClickListener() {//屏闭长按事件，
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        RelativeLayout rootLayout = (RelativeLayout) this.findViewById(R.id.view_line1).getParent();
        RelativeLayout.LayoutParams tmpParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpParams.topMargin = DensityUtils.dp2px(this, 48) + 1;
        rootLayout.addView(mWebview, 2, tmpParams);
   }

    @Override
    protected void onInitView() {
        Intent intent = getIntent();
        mActionType = intent.getIntExtra("type", 0);
        mToHome = intent.getBooleanExtra(ArgConstant.TO_HOME, false);

        if (mActionType == 1) {//从智齿过来
            link = intent.getStringExtra("url");
            mOnlyShow = true;
        } else if (mActionType == 2) {
            mTitlename = intent.getStringExtra("activityTitle");
            link = intent.getStringExtra("url");
            mId = intent.getLongExtra("id", 0);
        } else {
            Uri uri = getIntent().getData();
            if (uri != null) {
                try {
                    mTitlename = uri.getQueryParameter("title");
                    link = (uri.getQueryParameter("url"));
                    mToHome = StringUtils.parseInt(uri.getQueryParameter("toHome")) == 1;
                    //#解析会截断
                    if(!TextUtils.isEmpty(link)){
                        link=link.replace("{n}","#");
                    }

                } catch (Exception e) {
                }
            } else {
                ActionListBean.ActionListData actionBean = (ActionListBean.ActionListData) intent.getSerializableExtra("action_center_bean_activity");
                mTitlename = actionBean.getName();
                link = actionBean.getLink();
                mId = actionBean.getId();
            }
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        image_empty = (ImageView) findViewById(R.id.image_empty);

        mProgressLoading = (WebProgressBar) findViewById(R.id.progress_tip_bar);

        text_faile = (TextView) findViewById(R.id.text_faile);

        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        tv_title_titlebar = (TextView) findViewById(R.id.tv_title_titlebar);
        tv_title_titlebar.setText(mTitlename);
        setBoldText(tv_title_titlebar);
        initWebView();
        mFlVideoContainer = findViewById(R.id.flVideoContainer);
        mWebview.setWebChromeClient(new WebChromeClient() {
            IX5WebChromeClient.CustomViewCallback mCallback;
            View xCustomView;

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                LogUtils.i("网页--title---：" + title);
                if (TextUtils.isEmpty(mTitlename)&&(!TextUtils.isEmpty(title))) {
                    if (!title.equals(mTitlename)) {
                        mTitlename = title;
                        if (null != tv_title_titlebar) {
                            tv_title_titlebar.setText(mTitlename);
                        }
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                super.onProgressChanged(arg0, arg1);
                if (null != mProgressLoading)
                    mProgressLoading.onProgressFinished(arg1);
            }

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                LogUtils.i("ToVmp", "onShowCustomView");
                super.onShowCustomView(view, callback);
                fullScreen();
                if (xCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                xCustomView = view;
                mCallback = callback;
                // 如果一个视图已经存在，那么立刻终止并新建一个
            /*    if (mCallback != null) {
                    callback.onCustomViewHidden();
                    return;
                }*/

                mFlVideoContainer.setVisibility(View.VISIBLE);
                mFlVideoContainer.addView(view);
                // mCallback = callback;
                // super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (xCustomView == null) {
                    // 不是全屏播放状态
                    return;
                }
                LogUtils.i("ToVmp", "onHideCustomView");
                fullScreen();
                mWebview.setVisibility(View.VISIBLE);
                mFlVideoContainer.setVisibility(View.GONE);
                mFlVideoContainer.removeAllViews();
              /*  if (mCallback != null) {
                    //  mCallback.onCustomViewHidden();
                    mCallback = null;
                }*/
                xCustomView = null;
                //super.onHideCustomView();

            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                if (dealUrl(s)) {
                    return true;
                }

                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (null != mProgressLoading) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                hideProgress();
            }
        });

        enablePageVideoFunc();
      //  LogUtils.i(TAG, "link" + link);
        // link="http://testm.v.huatu.com/z/1908kxj/index.html";


        LogUtils.i(TAG, "link" + link);
        mWebview.loadUrl(formatLink(link));

        setListener();
        loadData();
    }

    private final String formatLink(String url){
        if(!TextUtils.isEmpty(UserInfoUtil.token)){
            url=url.contains("?") ? (url+"&token="+UserInfoUtil.token)
                                  : (url+"?token="+UserInfoUtil.token);
        }
        return url;
    }

    private void enablePageVideoFunc() {
        if (mWebview.getX5WebViewExtension() != null) {
            // Toast.makeText(this, "页面内全屏播放模式", Toast.LENGTH_LONG).show();
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LogUtils.i("ToVmp", "横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LogUtils.i("ToVmp", "竖屏");
        }
    }


    private boolean dealUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith("tbopen:")) {//禁止跳转tbopen
            return true;
        }
        LogUtils.d("ActionDetailActivity", url);


        if (url.contains("weixin://wap/pay?")) {//
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                startActivity(intent);
                return true;
            } catch (Exception e) {
                Toast.makeText(ActionDetailActivity.this, "请下载安装最新版微信", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (url != null && url.startsWith("ztk://h5/active")) {
            //11 活动页 todo
//            this.finish();
            Uri uri = Uri.parse(url);
            String mUrl = uri.getQueryParameter("url");
            String title = uri.getQueryParameter("title");
            tv_title_titlebar.setText(title);
            mWebview.loadUrl(mUrl);
            return true;
        }else if (url != null && url.startsWith("ztk://share")) {
            Uri uri = Uri.parse(url.replace("#","{n}"));
            String des = uri.getQueryParameter("des");
            String title = uri.getQueryParameter("title");
            String shareUrl = uri.getQueryParameter("url");
            shareUrl=TextUtils.isEmpty(shareUrl) ? shareUrl:shareUrl.replace("{n}","#");
            ShareUtil.test(this,"",des,title,shareUrl);
            return true;
        }

        String target = url.contains("?") ? url.split("[?]")[0] : url;
        HTRouterEntry entity = HTRouterManager.findRouterEntryByUrl(target);
        //  ztk://h5/active  ztk://arena/home  ztk://h5/simulate  ztk://course/detail
        //为了防止匹配不上后循环跳，这里需要有个判断
        if (entity != null) {
            HTPageRouterCall.newBuilderV2(target)
                    .context(ActionDetailActivity.this).sourceIntent(new Intent().setData(Uri.parse(url)))
                    .build()
                    .start();
            return true;
        }
        if (url != null && url.equals("ztk://match/detail")) {
            //4模考大赛首页
            Intent intent = new Intent(ActionDetailActivity.this, SimulationContestActivityNew.class);
            intent.putExtra("mToHome", mToHome);
            intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
            startActivity(intent);
            finish();
            return true;
        } else if (url != null && url.equals("ztk://live/home")) {
            //5课程列表
            Intent intent = new Intent(this, MainTabActivity.class);
            intent.putExtra("require_index", 1);
            startActivity(intent);
            finish();
            return true;
        } else if (url != null && url.equals("ztk://essay/home")) {
            //6申论首页
            if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
//                if (!mToHome) {
//                SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
//                SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
//                EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY));
//                } else {
                // SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
                SignUpTypeDataCache.getInstance().setCurSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                MainTabActivity.newIntent(this);
                finish();
            } else {
                ToastUtils.showMessage("配置错误，请正确配置跳转地址");
            }
            return true;
        } else if (url != null && url.startsWith("ztk://course/seckill?courseId=")) {
            // 7 秒杀课程
            Uri uri = Uri.parse(url);
            String rid = uri.getQueryParameter("courseId");
            String TITLE = uri.getQueryParameter("title");
//          String rid=url.substring("ztk://course/seckill?courseId=".length(),url.indexOf("&"));
//          String mTitle=url.substring(url.indexOf("&")+1);
//            String title=mTitle.substring("title=".length());
//            String TITLE=URLDecoder.decode(title);
            BaseFrgContainerActivity.newInstance(this,
                    SecKillFragment.class.getName(),
                    SecKillFragment.getArgs(rid + "", TITLE, mToHome));
            return true;
        } else if (url != null && url.startsWith("ztk://pastPaper?exerciseId=")) {
            //10 真题做题页面 ,未做完的页面跳不了了，只能跳到未做的做题页了
            Uri uri = Uri.parse(url);
            String mExerciseId = uri.getQueryParameter("exerciseId");
//            String mExerciseId=url.substring("ztk://pastPaper?exerciseId=".length());
            long exerciseId = 0;
            if (!TextUtils.isEmpty(mExerciseId)) {
                exerciseId = Long.parseLong(mExerciseId);
            }
            Bundle bundle = new Bundle();
            bundle.putLong("point_ids", exerciseId);
            bundle.putBoolean("toHome", mToHome);
            ArenaExamActivityNew.show(this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
            return true;
        }  else if (url != null && url.startsWith("ztk://estimatePaper")) {
            //13 精准估分做题
            int requestType = ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
            Uri uri = Uri.parse(url);
            String mExerciseId = uri.getQueryParameter("exerciseId");
            long exerciseId = 0;
            if (!TextUtils.isEmpty(mExerciseId)) {
                exerciseId = Long.parseLong(mExerciseId);
            }
            Bundle bundle = new Bundle();
            bundle.putLong("point_ids", exerciseId);
            ArenaExamActivityNew.show(this, requestType, bundle);
            return true;
        } else if (url != null && url.startsWith("ztk://pastPaper/province")) {
            //15 真题演练跳具体省份试卷列表
            Uri uri = Uri.parse(url);
            String areaId = uri.getQueryParameter("areaId");
            String areaName = uri.getQueryParameter("areaName");
            int requestType = ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN;
            ExamAreaItem item = new ExamAreaItem();
            item.area = Integer.parseInt(areaId);
            item.areaName = areaName;
            Intent intent = new Intent(this, ExamPaperActivity.class);
            intent.putExtra("examAreaData", item);
            intent.putExtra("request_type", requestType);
            startActivity(intent);
            return true;
        } else if (url != null && url.startsWith("ztk://match/essay")) {
            //17 申论模考报名页
            if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                Intent intent = new Intent(ActionDetailActivity.this, SimulationContestActivityNew.class);
                intent.putExtra("subject", Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                ActionDetailActivity.this.startActivity(intent);
            } else {
                ToastUtils.showMessage("配置错误，请正确配置跳转地址");
            }
            return true;
        }
//        else if (url != null && url.startsWith("ztk://h5/simulate")) {
//            //12 H5模考  不跳了
//            Uri uri = Uri.parse(url);
//            String mUrl = uri.getQueryParameter("url");
//            String title = uri.getQueryParameter("title");
//            AdvertiseActivity.newInstance(this, "",mUrl, 1, title);
//            return true;
//        }

        return false;
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

    private void loadData() {
        if (mId != 0) {
            Subscription subscription = RetrofitManager.getInstance().getService().addActionNum(mId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ActionNumberAddBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(ActionNumberAddBean actionNumberAddBean) {
                            Log.i(TAG, actionNumberAddBean.toString());
                        }
                    });

            compositeSubscription.add(subscription);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("onActivityResult","SysonResume");
        if (mWebview != null) {
            mWebview.onResume();
            if (mJumpRefresh&&AppContextProvider.hasFlag(AppContextProvider.WEBVIEW_REFRESHTYPE)) {
                mJumpRefresh=false;
                LogUtil.e("onActivityResult","onResume");
                AppContextProvider.removeFlag(AppContextProvider.WEBVIEW_REFRESHTYPE);
                if(SpUtils.getLoginState()){
                    final String refreshUrl=formatLink(link);
                    LogUtil.e("onActivityResult",refreshUrl);
                    //mWebview.loadUrl("about:blank");
                    mWebview.loadUrl(refreshUrl);

                    /*mWebview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           mWebview.loadUrl(refreshUrl);
                        }
                    },450);*/
                   // mWebview.loadUrl(refreshUrl);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.e("onActivityResult","SysonPause");
        if (mWebview != null) {
            mWebview.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.onDestory();
            mWebview = null;
        }
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mOnlyShow&&(mActionType==1)) {
            MainTabActivity.newIntent(ActionDetailActivity.this);
        }else {
          if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(this);
          }
        }
        ActionDetailActivity.this.finish();
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mOnlyShow&&(mActionType==1)) {
                    MainTabActivity.newIntent(ActionDetailActivity.this);
                }else {
                    if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
                        MainTabActivity.newIntent(ActionDetailActivity.this);
                    }
                }
                ActionDetailActivity.this.finish();
            }
        });
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
