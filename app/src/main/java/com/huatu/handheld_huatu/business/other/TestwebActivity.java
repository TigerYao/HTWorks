package com.huatu.handheld_huatu.business.other;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.helper.db.KnowPointInfoDao;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tobiasrohloff.view.NestedScrollWebView;

/**
 * Created by Administrator on 2018\12\10 0010.
 */

public class TestwebActivity extends SimpleBaseActivity {

    NestedScrollWebView mWebview;

    @Override
    protected int onSetRootViewId() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        return R.layout.test_webview;
    }

    @Override
    protected void onResume() {
        super.onResume();
        QMUIStatusBarHelper.setStatusBarLightMode(this);
    }


    String desString = "我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。\n" +
            "\n" +
            "       请在使用我们的产品（或服务）前，仔细阅读并了解《华图在线用户服务协议》和《隐私权政策》";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onInitView() {
        super.onInitView();

        mWebview = this.findViewById(R.id.wb_content);


        this.findViewById(R.id.nick_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           /*     SpannableStringBuilder sb = new SpannableStringBuilder(desString);
                String serverKeyword="《华图在线用户服务协议》";
                String privacyKeyword="《隐私权政策》";
                int serKeyIndex= desString.lastIndexOf(serverKeyword);
                sb.setSpan(new PrivacyClickableSpan(0) , serKeyIndex, serKeyIndex+serverKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int priKeyIndex= desString.lastIndexOf(privacyKeyword);

                sb.setSpan(new PrivacyClickableSpan(1), priKeyIndex, priKeyIndex+privacyKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                DialogUtils.createLayoutDialog(TestwebActivity.this,R.layout.dialog_privacy_layout, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, "温馨提示",
                        sb, "拒绝并退出", "同意");*/

                http:
//test.htexam.net/z/2020cjflAPP/index.shtml

                LogUtils.e("tst", KnowPointInfoDao.getInstance().create(0, 1, 1, 0, 2, "test") + "");

                LogUtils.e("tst2", GsonUtil.toJsonStr(KnowPointInfoDao.getInstance().getAll(1)));

                //  CollectAreanFragment.launch(TestwebActivity.this);

            }
        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//处理网页内部链接
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // if(mProgressLoading!=null) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                // onLoadingFinish(webView, url);
                //isFirstLoading=false;
            }

        });

    }

    @Override
    protected void onLoadData() {
        mWebview.loadUrl("http://www.baidu.com");

    }
}
