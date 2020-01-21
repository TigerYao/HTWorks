package com.huatu.handheld_huatu.business.me;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.essay.examfragment.FeedbackDialogFragment;
import com.huatu.handheld_huatu.business.login.ContractFragment;
import com.huatu.handheld_huatu.business.login.PrivacyClickableSpan;
import com.huatu.handheld_huatu.business.other.TestwebActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.netease.hearttouch.router.HTPageRouterCall;

import java.io.Serializable;

import rx.Subscriber;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {

    private RelativeLayout rl_left_topbar;
    private TextView text_app_version;
    private LinearLayout ll_weixin;
    private TextView text_weixin;
    private LinearLayout ll_weibo;
    private String weixinText;
    private LinearLayout ll_ph;
    private TextView tvPhoneNumber;
    private TextView tv_contract;
    private AppPermissions rxPermissions;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onInitView() {
        rxPermissions = new AppPermissions(this);
        rl_left_topbar = findViewById(R.id.rl_left_topbar);
        rl_left_topbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });

        text_app_version = findViewById(R.id.text_app_version);
        text_app_version.setText("华图在线 " + AppUtils.getVersionName());

        ll_ph = findViewById(R.id.ll_ph);
        tvPhoneNumber = findViewById(R.id.about_phone_tv);
        tvPhoneNumber.setText(SpUtils.getAboutPhone());
        ll_weixin = findViewById(R.id.ll_weixin);
        text_weixin = findViewById(R.id.text_weixin);
        tv_contract = findViewById(R.id.tv_contract);
        weixinText = text_weixin.getText().toString();
        ll_weibo = findViewById(R.id.ll_weibo);

        ll_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weibo.com/htwx"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                // UniApplicationContext.getContext().startActivity(intent);
                UIJumpHelper.openActionView(AboutActivity.this, intent);
            }
        });

        ll_weixin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(weixinText);
               // CommonUtils.showToast("华图在线 复制成功");
                ToastUtils.showBottom(AboutActivity.this,"华图在线 复制成功");
                return true;
            }
        });

        ll_ph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
             /*   Intent sourceIntent = new Intent().setData(Uri.parse("ztk://estimatePaper/home?rid=1119"));
                HTPageRouterCall.newBuilderV2("ztk://estimatePaper/home")
                        .context(AboutActivity.this)
                        .sourceIntent(sourceIntent)
                        .build()
                        .start();*/


                if(BuildConfig.DEBUG){
                    UIJumpHelper.startActivity(AboutActivity.this,TestwebActivity.class);

                }else {
                    showSercive();
                }

            }
        });

        initPolicy();
    }

    // 服务协议，隐私政策
    protected void initPolicy() {
        SpannableStringBuilder sb = new SpannableStringBuilder("《华图在线用户服务协议》和《隐私政策》");
        sb.setSpan(new PrivacyClickableSpan(0) , 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.setSpan(new PrivacyClickableSpan(1), 13, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_contract.setText(sb);
        tv_contract.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showSercive3() {
        //5079249    uyR4S0mMiWFMyA0LsBVeTFwfbZ5qrQs-NeYkNkwd-1NmTUhdo9o5xTTSEzZrILF4

        String md5 = "areaid=2&catid=101&timestamp=2019-5-31&typeid=1";

        String sign = Md5Util.toSign(md5);


        FeedbackDialogFragment ratefragment = FeedbackDialogFragment.getInstance("dd", "dd");
        ratefragment.show(this.getSupportFragmentManager(), "wechat_add_group");

        CommonUtils.showToast("邮箱复制成功");
    }

    private void showSercive2() {
        String md5 = "areaid=2&catid=101&timestamp=2019-5-31&typeid=1";

        String sign = Md5Util.toSign(md5);

        FeedbackDialogFragment ratefragment = FeedbackDialogFragment.getInstance("dd", "dd");
        ratefragment.show(this.getSupportFragmentManager(), "wechat_add_group");

        CommonUtils.showToast("邮箱复制成功");
    }

    private void showSercive() {
        final SerciveDialog serciveDialog = new SerciveDialog(this, R.layout.dialog_me_sevice);

        TextView text_ok = serciveDialog.mContentView.findViewById(R.id.text_ok);
        TextView text_cancel = serciveDialog.mContentView.findViewById(R.id.text_cancel);
        TextView zs_phone = serciveDialog.mContentView.findViewById(R.id.zs_phone);
        if (zs_phone != null) {
            zs_phone.setText(SpUtils.getAboutPhone());
        }
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serciveDialog.dismiss();
            }
        });

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serciveDialog.dismiss();
                tel();
            }
        });

        serciveDialog.show();
    }

    private void tel() {
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取打电话权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            String phone = "";
                            if (!TextUtils.isEmpty(tvPhoneNumber.getText())) {
                                phone = tvPhoneNumber.getText().toString().trim();
                                if (phone.contains("-")) {
                                    phone = phone.replace("-", "");
                                }
                            }
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                            AboutActivity.this.startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
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
}
