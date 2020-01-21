package com.huatu.handheld_huatu.business.essay.video;


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
import com.huatu.handheld_huatu.business.essay.event.EssayCheckMessageEvent;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.widget.MyRadioGroup;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class CheckPlayRateDialogFragment extends DialogFragment implements MyRadioGroup.OnCheckedChangeListener {

    private final float[] rateArr = new float[]{0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2f};

    public static CheckPlayRateDialogFragment getInstance(float playRate) {
        Bundle args = new Bundle();
        args.putFloat(ArgConstant.KEY_ID, playRate);

        CheckPlayRateDialogFragment tmpFragment = new CheckPlayRateDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    private float mCurPlayRate = 0;

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

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.play_rate_setting_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.NoDimThemeDialogPopup);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        view.findViewById(R.id.cancel_action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        MyRadioGroup actionLayout = view.findViewById(R.id.rate_radiogroup);

        for (int i = 0; i < actionLayout.getChildCount(); i++) {
            actionLayout.getChildAt(i).setId(i);
        }
        for (int i = 0; i < rateArr.length; i++) {
            if (rateArr[i] == mCurPlayRate) {
                ((RadioButton) actionLayout.getChildAt(i)).setChecked(true);
                break;
            }
        }
        actionLayout.setOnCheckedChangeListener(this);

        return dialog;
    }


    @Override
    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
        float curRate = rateArr[checkedId];
        EssayCheckMessageEvent event = new EssayCheckMessageEvent(EssayCheckMessageEvent.EssayCheck_change_video_rate);
        Bundle bundle = new Bundle();
        bundle.putFloat("curRate", curRate);
        event.extraBundle = bundle;
        EventBus.getDefault().post(event);
        dismiss();
    }


}
