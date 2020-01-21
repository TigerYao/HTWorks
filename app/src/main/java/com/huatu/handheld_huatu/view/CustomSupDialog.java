package com.huatu.handheld_huatu.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;

public class CustomSupDialog extends Dialog {

    private ImageView mTipView;
    private View mContentView;
    private int statusBarHeight = 0;
    private int mLeft;
    private int mTop;
    private Activity mContext;
    private int rLayout=R.layout.layout_evaluate_report_tips_dialog;
    private DialogInter mDialogInter;
    public interface DialogInter{
        void BindView(View mView,Dialog dialog);
    }
    DisplayMetrics dm = new DisplayMetrics();
    public CustomSupDialog(Activity context, int theme,int rLayout, DialogInter mDialogInter,int left, int top) {
        super(context, theme);
        LogUtils.d("EvaluateReportTipsDialog==>constr");
        if(context!=null) {
            this.mDialogInter=mDialogInter;
            this.mLeft = left;
            this.mTop = top;
            this.rLayout=rLayout;
            this.mContext = context;
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            initView(context,rLayout,mDialogInter,dm.density);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("EvaluateReportTipsDialog==>onCreate");
        if (mContentView!=null) {
            setContentView(mContentView);
            setCanceledOnTouchOutside(true);
            setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                }
            });
            WindowManager.LayoutParams p = getWindow()
                    .getAttributes();
            p.height = dm.heightPixels;
            p.width = dm.widthPixels;
            p.dimAmount = 0.0f;
            getWindow().setAttributes(p);
        }
    }

    @Override
    public void show() {
        LogUtils.d("EvaluateReportTipsDialog==>show");
        if(isActive(mContext)) {
            super.show();
        }
    }

    public static class Builder {
        private Activity mContext;
        private int mLeft;
        private int mTop;
        private int rLayout;
        private DialogInter mDialogInter;
        public Builder(Activity context) {
            this.mContext = context;
        }

        public Builder setmLeft(int mLeft) {
            this.mLeft = mLeft;
            return this;
        }

        public Builder setmTop(int mTop) {
            this.mTop = mTop;
            return this;
        }

        public Builder setRLayout(int rLayout) {
            this.rLayout = rLayout;
            return this;
        }

        public Builder setBindInter(DialogInter mDialogInter) {
            this.mDialogInter = mDialogInter;
            return this;
        }

        public CustomSupDialog create() {
            CustomSupDialog dialog = new CustomSupDialog(mContext, R.style.showrevareptipsdialog,rLayout,mDialogInter,mLeft,mTop);
            return dialog;
        }
    }

    private void initView(Context context, int rLayout,DialogInter mDialogInter,float density) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(rLayout, null);
        mContentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });
        if(mDialogInter!=null){
            mDialogInter.BindView(mContentView,this);
        }
    }

    private int getStatusBarHeight(Context context){
        if(statusBarHeight==0){
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if(resourceId>0){
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    private boolean isActive(Activity context){
        return !Method.isActivityFinished(context);
    }
}
