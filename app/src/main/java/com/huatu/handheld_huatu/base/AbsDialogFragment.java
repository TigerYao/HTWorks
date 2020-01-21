package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.huatu.handheld_huatu.listener.DetachableDialogDismissListener;
import com.huatu.handheld_huatu.utils.RxUtils;

/**
 * Created by cjx on 2019\9\18 0018.
 * DialogFragment内存泄露最强解决方案
 * https://www.jianshu.com/p/f2d6e6bc4b77
 *
 * https://www.cnblogs.com/endure/p/7664320.html
 */

public class AbsDialogFragment extends DialogFragment {
    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getShowsDialog()) {
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
        setShowsDialog(true);

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            getDialog().setContentView(view);
        }
        final Activity activity = getActivity();
        if (activity != null) {
            getDialog().setOwnerActivity(activity);
        }
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                getDialog().onRestoreInstanceState(dialogState);
            }
        }

        //复写子类的侦听
        Dialog dialog= getDialog();
        if(null!=dialog){
            mInnerDismissListener= DetachableDialogDismissListener.wrap(this);
            // dialog.setOnCancelListener(null);
            dialog.setOnDismissListener(mInnerDismissListener);
        }
    }

    DetachableDialogDismissListener mInnerDismissListener;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mInnerDismissListener){
            mInnerDismissListener.clear();
            mInnerDismissListener=null;
        }

    }


/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mShowsDialog) {
            return;
        }

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        final Activity activity = getActivity();
        if (activity != null) {
            mDialog.setOwnerActivity(activity);
        }
        mDialog.setCancelable(mCancelable);
        mDialog.setOnCancelListener(this);
        mDialog.setOnDismissListener(this);
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                mDialog.onRestoreInstanceState(dialogState);
            }
        }
    }*/
}
