package com.huatu.handheld_huatu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by ht on 2017/9/9.
 */

public class CustomShowRuleDialog extends Dialog {
    private Context mContext;
    private LayoutInflater mInflater;
    private View mDialogView;
    private WebView mWebView;
    private TextView tv_know;
    private int mKnowColor;
    private String mUrl;


    private android.view.View.OnClickListener mKnowClickListener;


    public CustomShowRuleDialog(Context context,int theme) {
        super(context,theme);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initView(mInflater);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialogView);

    }

    private void initView(LayoutInflater inflater) {
        mDialogView = inflater.inflate(R.layout.layout_show_rule_dialog, null);
        mWebView= (WebView) mDialogView.findViewById(R.id.wb_show_rule);
        tv_know= (TextView) mDialogView.findViewById(R.id.tv_know);
        tv_know.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mKnowClickListener != null) {
                    mKnowClickListener.onClick(v);
                }
            }
        });
    }

    public void loadWeb(String url){
        mUrl=url;
        mWebView.setWebViewClient(new WebViewClient());
        // 设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置可以访问文件
//                webView.getSettings().setAllowFileAccess(true);
        // 设置支持缩放
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.loadUrl(mUrl);
    }

    public void setPositiveButton(String text, android.view.View.OnClickListener onClickListener) {
        tv_know.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            tv_know.setText(text);
        }
        mKnowClickListener = onClickListener;
    }

    public void setPositiveButtonClickListener(android.view.View.OnClickListener onClickListener) {
        mKnowClickListener = onClickListener;
    }

    public void setPositiveColor(int color) {
        mKnowColor = color;
        if(mKnowColor != 0) {
            tv_know.setTextColor(mKnowColor);
        }
    }
    public static class Builder {
        private Context mContext;
        private String mWebURl;
        private String mKnowTextString;
        private android.view.View.OnClickListener mKnowClickListener;
        private OnCancelListener mOnCancelListener;
        private boolean isCanceledOnTouchOutside = true;
        private int mKnowColor;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setmWebURl(String webURl) {
            mWebURl = webURl;
            return this;

        }
        public Builder setmKnowTextString(String mKnowTextString) {
            this.mKnowTextString = mKnowTextString;
            return this;
        }
        public CustomShowRuleDialog.Builder setPositiveButton(int stringId, android.view.View.OnClickListener onClickListener) {
            mKnowTextString = mContext.getResources().getString(stringId);
            mKnowClickListener = onClickListener;
            return this;
        }
        public CustomShowRuleDialog.Builder setNegativeButton(String text, android.view.View.OnClickListener onClickListener) {
            mKnowTextString = text;
            mKnowClickListener = onClickListener;
            return this;
        }


        public Builder setmOnCancelListener(OnCancelListener mOnCancelListener) {
            this.mOnCancelListener = mOnCancelListener;
            return this;

        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            isCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;

        }

        public Builder setmKnowColor(int mKnowColor) {
            this.mKnowColor = mKnowColor;
            return this;

        }

    public CustomShowRuleDialog create() {
        CustomShowRuleDialog dialog = new CustomShowRuleDialog(mContext, R.style.CustomProgressDialog);


        if (!TextUtils.isEmpty(mWebURl)) {
            dialog.loadWeb(mWebURl);
        }
        if (!TextUtils.isEmpty(mKnowTextString)||mKnowClickListener!=null){
            dialog.setPositiveButton(mKnowTextString,mKnowClickListener);
        }
        if (mOnCancelListener != null) {
            dialog.setOnCancelListener(mOnCancelListener);
        }
        if(mKnowColor > 0) {
            dialog.setPositiveColor(mKnowColor);
        }
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        return dialog;
    }

    }
    private CustomDialog mDailyDialog;
    private void showLoadingDialog(Context cxt) {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomDialog(cxt, R.layout.dialog_type2);
            TextView tv = (TextView) mDailyDialog.mContentView.findViewById(R.id.tv_notify_message);
            tv.setText("正在加载");
        }
        mDailyDialog.show();

    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("AlertDialog  Exception:", e.getMessage() + "");
        }

    }
}
