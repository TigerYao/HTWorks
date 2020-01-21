package com.huatu.handheld_huatu.business.ztk_zhibo.cache;


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

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.widget.MyRadioGroup;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class DefinitionDialogFragment extends DialogFragment implements MyRadioGroup.OnCheckedChangeListener {

    private final float[] rateArr=new float[]{ 2f,1f,0f};

    public static DefinitionDialogFragment getInstance(float playRate) {
        Bundle args = new Bundle();
        args.putFloat(ArgConstant.KEY_ID, playRate);

        DefinitionDialogFragment tmpFragment = new DefinitionDialogFragment();
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

        View view = LayoutInflater.from(getActivity()).inflate( R.layout.down_definition_setting_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup );
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

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
         if(getActivity()!=null&&(getActivity() instanceof DownLoadListActivity)){
             ((DownLoadListActivity)getActivity()).setCustomDefinition((int)curRate);
         }

         dismiss();
    }



}
