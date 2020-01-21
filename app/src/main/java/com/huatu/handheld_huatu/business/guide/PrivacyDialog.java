package com.huatu.handheld_huatu.business.guide;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.login.PrivacyClickableSpan;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.utils.DensityUtils;

/**
 * Created by Administrator on 2019\12\27 0027.
 */

public class PrivacyDialog {



    public static AlertDialog create(final Context context, final View.OnClickListener cancelListenr, final View.OnClickListener okListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.PrivacyDialogTheme);
        // builder.setTitle("fasdfasdf");
        // builder.setMessage(content);

        View view =View.inflate(context,R.layout.dialog_privacy_layout, null);// context..getLayoutInflater().inflate(R.layout.dialog_privacy_layout, null);
        builder.setCancelable(false);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        String desString="我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。" +
                "\n" +
                "请在使用我们的产品（或服务）前，仔细阅读并了解《华图在线用户服务协议》和《隐私政策》";
        SpannableStringBuilder sb = new SpannableStringBuilder(desString);
        String serverKeyword="《华图在线用户服务协议》";
        String privacyKeyword="《隐私政策》";

        int serKeyIndex= desString.lastIndexOf(serverKeyword);
        sb.setSpan(new PrivacyClickableSpan(0) , serKeyIndex, serKeyIndex+serverKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int priKeyIndex= desString.lastIndexOf(privacyKeyword);
        sb.setSpan(new PrivacyClickableSpan(1), priKeyIndex, priKeyIndex+privacyKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView contentView=(TextView)view.findViewById(R.id.custom_dialog_message_view);
        contentView.setText(sb);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        view.findViewById(R.id.custom_dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(null!=cancelListenr){
                    cancelListenr.onClick(v);
                }
            }
        });

        view.findViewById(R.id.custom_dialog_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(null!=okListener){
                    okListener.onClick(v);
                }
            }
        });

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setContentView(view); // 加载自定义布局,可完全覆盖dialog窗口

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DensityUtils.dp2px(context,300) ;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
                    dialog.dismiss();

                   Activity activity= CommonUtils.findActivity(context);
                   if(null!=activity){
                       activity.finish();
                   }
                    return true;
                }
                return false;
            }
        });
        dialog.show();
        return dialog;
    }


      /*   String desString="我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。" +
                "\n" +
                "       请在使用我们的产品（或服务）前，仔细阅读并了解《华图在线用户服务协议》和《隐私权政策》";
        SpannableStringBuilder sb = new SpannableStringBuilder(desString);
        String serverKeyword="《华图在线用户服务协议》";
        String privacyKeyword="《隐私权政策》";
        int serKeyIndex= desString.lastIndexOf(serverKeyword);
        sb.setSpan(new PrivacyClickableSpan(0) , serKeyIndex, serKeyIndex+serverKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int priKeyIndex= desString.lastIndexOf(privacyKeyword);

        sb.setSpan(new PrivacyClickableSpan(1), priKeyIndex, priKeyIndex+privacyKeyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        CustomConfirmDialog dialog= DialogUtils.createLayoutDialog(SplashActivity.this,R.layout.dialog_privacy_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SplashActivity.this.finish();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrefStore.putSettingInt(Constant.APP_PRIVACY_TIP,1);
                        redirectTo();
                    }
                }, "温馨提示",
                sb, "拒绝并退出", "同意");

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
                    dialog.dismiss();
                    SplashActivity.this.finish();
                    return true;
                }
                return false;
            }
        });*/

}
