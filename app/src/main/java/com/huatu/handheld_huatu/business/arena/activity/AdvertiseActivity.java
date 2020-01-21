package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.netease.hearttouch.router.HTRouter;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.socialize.UMShareAPI;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * desc:AdvertiseActivity
 *
 * @author zhaodongdong
 * QQ: 676362303
 * email: androidmdeveloper@163.com
 * 广告页，WebView
 */
@HTRouter(url = {"ztk://h5/simulate"},  needLogin = false)
public class AdvertiseActivity extends BaseActivity {
    @BindView(R.id.wv_advertise)
    X5WebView mWvAdvertise;
    private String mUrl;
    private String mShareId;
    private boolean isOnPause = false;
    private int type = 0;
    private String name;
    private String desc = "我在华图在线万人模考大赛等你！";
    private String show;
    private boolean mToHome;
    private TextView tv_title_titlebar;

    WebProgressBar mProgressLoading;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_advertise;
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
    protected void onInitView() {
        super.onInitView();
        Uri uri = getIntent().getData();
        if (uri != null) {
            try {
                mToHome ="1".equals(uri.getQueryParameter("toHome")) ;
                mUrl =uri.getQueryParameter("url");
                type=1;
                name=uri.getQueryParameter("title");

            } catch (Exception e) {  }
        }else {
            mUrl = getIntent().getStringExtra("url");
            mShareId = getIntent().getStringExtra("shareId");
            type = getIntent().getIntExtra("type", 0);
            name = getIntent().getStringExtra("name");
            mToHome = getIntent().getBooleanExtra("toHome", false);
        }
        initView();
        initMethod();
    }

    private void initView() {
        ButterKnife.bind(this);
        RelativeLayout rl_right_topbar = (RelativeLayout) findViewById(R.id.rl_right_topbar);
        rl_right_topbar.setVisibility(View.GONE);
        String title = "推荐";
        if (type == 1) {
            title = "万人模考";
            rl_right_topbar.setVisibility(View.VISIBLE);
        }
        tv_title_titlebar = (TextView) findViewById(R.id.tv_title_titlebar);
        mProgressLoading = findViewById(R.id.progress_tip_bar);
        tv_title_titlebar.setText(title);
        setBoldText(tv_title_titlebar);
        findViewById(R.id.rl_left_topbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToHome&& !ActivityStack.getInstance().hasRootActivity()) {
                    MainTabActivity.newIntent(AdvertiseActivity.this);
                }
                finish();
            }
        });

        rl_right_topbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name == null || mUrl == null) {
                    CommonUtils.showToast("分享参数异常");
                } else {
                    show = name + "正在报名中，来参赛吧！";
                    ShareUtil.test(AdvertiseActivity.this, mShareId, desc, show, mUrl);
                }
            }
        });

//        WebSettings settings = mWvAdvertise.getSettings();
        mWvAdvertise.getSettings().setJavaScriptEnabled(true);
        //支持内容重新布局,页面元素在一列中显示出来
//        mWvAdvertise.getSettings().setLayoutAlgorithm(SINGLE_COLUMN);
        //设置显示缩放按钮
        mWvAdvertise.getSettings().setBuiltInZoomControls(true);
        //设置支持缩放
        mWvAdvertise.getSettings().setSupportZoom(true);
        //缩放至屏幕大小
        mWvAdvertise.getSettings().setLoadWithOverviewMode(true);
        //图片调整到适合webView大小
        mWvAdvertise.getSettings().setUseWideViewPort(true);
//        //设置缓存模式,无论何时都会从缓存中加载,建议有网时使用LOAD_DEFAULT,无网使用此设置
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        //设置访问文件
//        settings.setAllowFileAccess(true);
//        //设置local缓存
//        settings.setDomStorageEnabled(true);
//        settings.setDatabaseEnabled(true);
//        //设置app缓存
//        settings.setAppCacheEnabled(true);
//        settings.setAppCachePath(getCacheDir().getAbsolutePath()+"webViewCache");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void initMethod() {
        mWvAdvertise.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (null != mProgressLoading)
                    mProgressLoading.onProgressFinished(i);

            }
        });

        mWvAdvertise.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if ((!TextUtils.isEmpty(url)) && url.startsWith("tbopen:")) {//禁止跳转tbopen
                    return true;
                }
                if (url.contains("http://v.huatu.com/h5/detail.php?rid")) {
                    String rid = url.substring(37);
                    Intent intent = new Intent(AdvertiseActivity.this,
                            BaseIntroActivity.class);
                    intent.putExtra("AdvNetClassId", rid);
                    startActivity(intent);
                    return true;
                } else if (url != null && url.equals("ztk://estimatePaper/home")) {
                    // 2估分列表
                    Intent intent = new Intent(AdvertiseActivity.this, SimulationExamActivity.class);
                    intent.putExtra("request_type", ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN);
                    intent.putExtra("toHome", mToHome);
                    startActivity(intent);
                    return true;
                } else if (url != null && url.equals("ztk://simulatePaper/home")) {
                    // 3专项模考列表
                    Intent intent = new Intent(AdvertiseActivity.this, SimulationExamActivity.class);
                    intent.putExtra("request_type", ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN);
                    intent.putExtra("toHome", mToHome);
                    startActivity(intent);
                    return true;
                } else if (url != null && url.equals("ztk://match/detail")) {
                    //4模考大赛首页
                    Intent intent = new Intent(AdvertiseActivity.this, SimulationContestActivityNew.class);
                    intent.putExtra("mToHome", mToHome);
                    intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                    startActivity(intent);
                    finish();
                    return true;
                } else if (url != null && url.equals("ztk://live/home")) {
                    //5课程列表
                    Intent intent = new Intent(AdvertiseActivity.this, MainTabActivity.class);
                    intent.putExtra("require_index", 1);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (url != null && url.equals("ztk://essay/home")) {
                    //6申论首页
                    if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                        // SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
                        SignUpTypeDataCache.getInstance().setCurSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        MainTabActivity.newIntent(AdvertiseActivity.this);
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
                    BaseFrgContainerActivity.newInstance(AdvertiseActivity.this,
                            SecKillFragment.class.getName(),
                            SecKillFragment.getArgs(rid + "", TITLE, mToHome));
                    return true;
                } else if (url != null && url.startsWith("ztk://course/collection?rid=")) {
                    //8 课程合集
                    Uri uri = Uri.parse(url);
                    String rid = uri.getQueryParameter("rid");
                    String TITLE = uri.getQueryParameter("title");
                    CourseCollectSubsetFragment.show(AdvertiseActivity.this, rid, TITLE, TITLE, mToHome);
                    return true;
                } else if (url != null && url.startsWith("ztk://course/detail?classId=")) {
                    //9 课程详情
                    Uri uri = Uri.parse(url);
                    String rid = uri.getQueryParameter("classId");
                    String mVideoType = uri.getQueryParameter("isLive");
                    int videoType = 1;
                    if (!TextUtils.isEmpty(mVideoType)) {
                        videoType = Integer.parseInt(mVideoType);
                    }
                    Intent intent = new Intent(AdvertiseActivity.this, BaseIntroActivity.class);
                    intent.putExtra("NetClassId", rid);
                    intent.putExtra("course_type", videoType);
                    intent.putExtra("toHome", mToHome);
                    startActivity(intent);
                    return true;
                } else if (url != null && url.startsWith("ztk://pastPaper?exerciseId=")) {
                    //10 真题做题页面 ,未做完的页面跳不了了，只能跳到未做的做题页了
                    Uri uri = Uri.parse(url);
                    String mExerciseId = uri.getQueryParameter("exerciseId");
                    long exerciseId = 0;
                    if (!TextUtils.isEmpty(mExerciseId)) {
                        exerciseId = Long.parseLong(mExerciseId);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putLong("point_ids", exerciseId);
                    bundle.putBoolean("toHome", mToHome);

                    ArenaExamActivityNew.show(AdvertiseActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
                    return true;
                }
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (null != mProgressLoading) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
            }
        });
        if(!TextUtils.isEmpty(UserInfoUtil.token)){
            mUrl=mUrl.contains("?") ? (mUrl+"&token="+UserInfoUtil.token)
                                    : (mUrl+"?token="+UserInfoUtil.token);
        }
        mWvAdvertise.loadUrl(mUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWvAdvertise.canGoBack()) {
                mWvAdvertise.goBack();
            } else {
                if (mToHome&& !ActivityStack.getInstance().hasRootActivity()) {
                    MainTabActivity.newIntent(this);
                }
                AdvertiseActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void newInstance(Context context, String shareId, String url, int type, String name) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(context, AdvertiseActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("shareId", shareId);
            intent.putExtra("type", type);
            intent.putExtra("name", name);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isOnPause) {
                if (mWvAdvertise != null) {
                    mWvAdvertise.getClass().getMethod("onResume").invoke(mWvAdvertise, (Object[]) null);
                }
                isOnPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mWvAdvertise != null) {
                mWvAdvertise.getClass().getMethod("onPause").invoke(mWvAdvertise, (Object[]) null);
                isOnPause = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWvAdvertise != null && mWvAdvertise.getParent() != null) {
            ((ViewGroup) mWvAdvertise.getParent()).removeView(mWvAdvertise);
            mWvAdvertise.destroy();
            mWvAdvertise = null;
        }
        UMShareAPI.get(this).release();
        super.onDestroy();
    }

    public void setBoldText(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
    }
}
