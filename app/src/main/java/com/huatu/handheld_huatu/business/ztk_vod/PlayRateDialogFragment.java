package com.huatu.handheld_huatu.business.ztk_vod;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.MyRadioGroup;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class PlayRateDialogFragment extends DialogFragment implements MyRadioGroup.OnCheckedChangeListener {

    private final float[] rateArr=new float[]{0.5f,0.75f,1.0f,1.25f,1.5f,1.75f,2f};

    public static PlayRateDialogFragment getInstance(float playRate) {
        Bundle args = new Bundle();
        args.putFloat(ArgConstant.KEY_ID, playRate);

        PlayRateDialogFragment tmpFragment = new PlayRateDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    private float mCurPlayRate=0;
    protected void parserParams(Bundle args) {
        mCurPlayRate = args.getFloat(ArgConstant.KEY_ID, 0);
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

        View view = LayoutInflater.from(getActivity()).inflate( R.layout.play_rate_setting_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.NoDimThemeDialogPopup );
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        view.findViewById(R.id.cancel_action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        MyRadioGroup actionLayout = (MyRadioGroup) view.findViewById(R.id.rate_radiogroup);

        for(int i=0;i<actionLayout.getChildCount();i++ ){
            actionLayout.getChildAt(i).setId(i);
        }
        for(int i=0;i<rateArr.length;i++){
            if(rateArr[i]==mCurPlayRate){
                ((RadioButton)actionLayout.getChildAt(i)).setChecked(true);
                break;
            }
        }
        actionLayout.setOnCheckedChangeListener(this);


        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/
        return dialog;
    }



    @Override
    public void onCheckedChanged(MyRadioGroup group, int checkedId){
         float curRate= rateArr[checkedId];
         if(getActivity()!=null&&(getActivity() instanceof BJRecordPlayActivity)){
             ((BJRecordPlayActivity)getActivity()).changeRate(curRate);
         }

         dismiss();
    }



}
