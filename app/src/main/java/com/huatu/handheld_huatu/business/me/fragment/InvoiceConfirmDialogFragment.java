package com.huatu.handheld_huatu.business.me.fragment;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.widget.MyRadioGroup;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class InvoiceConfirmDialogFragment extends DialogFragment  {


    public interface OnSubmitListener{
        void onSubmit();
    }


    private OnSubmitListener mOnSubmitListener;
    public void setOnSubmitListener(OnSubmitListener submitListener){
        this.mOnSubmitListener=submitListener;
    }

    public static InvoiceConfirmDialogFragment getInstance(String invoiceHead,String invoiceType,String taxNum) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.KEY_ID, invoiceHead);
        args.putString(ArgConstant.TYPE_ID, invoiceType);
        args.putString(ArgConstant.TITLE, taxNum);
        InvoiceConfirmDialogFragment tmpFragment = new InvoiceConfirmDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    private String mInvoiceHead,mInvoiceType,mTaxNum;
    protected void parserParams(Bundle args) {
        mInvoiceHead = args.getString(ArgConstant.KEY_ID, "");
        mInvoiceType= args.getString(ArgConstant.TYPE_ID, "");
        mTaxNum= args.getString(ArgConstant.TITLE, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    ViewGroup mLettersLayout;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.invoice_inputconfrim_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup );
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        if(mInvoiceType.equals("单位")){
            view.findViewById(R.id.taxnum_layout).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.txt_taxNum)).setText(mTaxNum);
        }
        ((TextView)view.findViewById(R.id.headType)).setText(mInvoiceType);
        ((TextView)view.findViewById(R.id.invoichead)).setText(mInvoiceHead);

        view.findViewById(R.id.cancel_action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null!=mOnSubmitListener){
                    mOnSubmitListener.onSubmit();
                }
                dismiss();
            }
        });


        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/
        return dialog;
    }

    @Override
    public void dismiss() {
        this.mOnSubmitListener=null;
        super.dismiss();
    }
 /*   @Override
    public void onCheckedChanged(MyRadioGroup group, int checkedId){
         float curRate= rateArr[checkedId];
         if(getActivity()!=null&&(getActivity() instanceof BJRecordPlayActivity)){
             ((BJRecordPlayActivity)getActivity()).changeRate(curRate);
         }

         dismiss();
    }
*/


}
