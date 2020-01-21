package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;

/**
 * Created by saiyuan on 2017/8/29.
 */

public class BaseFrgContainerActivity extends BaseActivity {

    private final static String FRG_CLASS_NAME = "frg_class_name";
    private final static String FRG_EXTRA_BUNDLE = "frg_extra_bundle";

    public String frgClassName = "";
    private Bundle extraBundle;

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        QMUIStatusBarHelper.setStatusBarLightMode(BaseFrgContainerActivity.this);
        frgClassName = originIntent.getStringExtra(FRG_CLASS_NAME);
        extraBundle = originIntent.getBundleExtra(FRG_EXTRA_BUNDLE);
        if (TextUtils.isEmpty(frgClassName)) {
            LogUtils.i("frgClassName is null, finish");
            finish();
            return;
        }
        try {
            BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, frgClassName);
            if (fragment != null) {
                if (extraBundle != null) {
                    fragment.setArguments(extraBundle);
                }
                addFragment(fragment.getClass().getSimpleName(), fragment, R.id.base_fragment_container_id, false);
            } else {
                LogUtils.i("fragment is null, finish");
                finish();
                return;
            }
        } catch (Fragment.InstantiationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.comm_base_container_layout;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return R.id.base_fragment_container_id;
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

    public static void newInstance(Context context, String className, Bundle arg) {
        newInstance(context, className, arg, 10001);
    }

    public static void newInstance(Context context, String className, Bundle arg, int requestCode) {
        if (TextUtils.isEmpty(className)) {
            LogUtils.e("className is null, can not start FrgContainerAct");
            return;
        }
        Intent intent = createIntent(context, className, arg);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static Intent createIntent(Context context, String className, Bundle arg) {
        if (TextUtils.isEmpty(className)) {
            LogUtils.e("className is null, can not start FrgContainerAct");
            return null;
        }
        Intent intent = new Intent(context, BaseFrgContainerActivity.class);
        intent.putExtra(FRG_CLASS_NAME, className);
        intent.putExtra(FRG_EXTRA_BUNDLE, arg);
        return intent;
    }
}