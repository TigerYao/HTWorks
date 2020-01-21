package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class BaseFrgPreContainerActivity extends XiaoNengHomeActivity{
    private final  static String TAG = "BaseFrgPreContainerActivity";
    private final static String FRG_CLASS_NAME = "frg_class_name";
    private final static String FRG_EXTRA_BUNDLE = "frg_extra_bundle";

    private String frgClassName = "";
    private Bundle extraBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG,"onCreate()...");
        frgClassName = getIntent().getStringExtra(FRG_CLASS_NAME);
        extraBundle = getIntent().getBundleExtra(FRG_EXTRA_BUNDLE);
        LogUtils.d(TAG,"frgClassName is : "+frgClassName);
        if(TextUtils.isEmpty(frgClassName)) {
            LogUtils.i("frgClassName is null, finish");
            finish();
            return;
        }
        try {
            CourseTeacherDetailFragment mAFragment = (CourseTeacherDetailFragment) Fragment.instantiate(this, frgClassName);
            if(mAFragment != null) {
                if(extraBundle != null) {
                    mAFragment.setArguments(extraBundle);
                }
                String tag = frgClassName;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                removeAllFragments(fragmentManager, fragmentTransaction);
                fragmentTransaction.add( R.id.base_pre_container_id, mAFragment, tag);
                fragmentTransaction.commitAllowingStateLoss();

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

    public void removeAllFragments(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        if(fragmentManager == null || fragmentTransaction == null) {
            return;
        }
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null && fragments.size() > 0) {
            Iterator<Fragment> fragmentIterator = fragments.iterator();
            while (fragmentIterator.hasNext()) {
                Fragment fragment = fragmentIterator.next();
                if(fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
            }
        }
    }

    @Override
    public void customChatParam() {

    }


    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    public static void newInstance(Context context, String className, Bundle arg) {
        newInstance(context, className, arg, 10001);
    }

    public static void newInstance(Context context, String className, Bundle arg, int requestCode) {
        if(TextUtils.isEmpty(className)) {
            LogUtils.e("className is null, can not start FrgContainerAct");
            return;
        }
        Intent intent = new Intent(context, BaseFrgPreContainerActivity.class);
        intent.putExtra(FRG_CLASS_NAME, className);
        intent.putExtra(FRG_EXTRA_BUNDLE, arg);
        if(context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments != null && fragments.size() > 0) {
            for(int i = 0; i < fragments.size(); i++) {
                if(fragments.get(i) != null) {
                    fragments.get(i).onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_base_frgpre_container;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
}
