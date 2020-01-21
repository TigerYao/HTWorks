package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.utils.ArgConstant;


public class ReuseActivityHelper {

    static final boolean DEBUG = false;

    static final String  LOG_TAG = "ReuseActivityHelper:";

    public static final String SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM = "SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM";

    ReuseActivity mActivity;

    ReuseActivityHelper(ReuseActivity activity) {
        mActivity = activity;
    }

    /**
     * add ragment
     */
    AbsFragment ensureFragment(FragmentParameter param) {
      return  addFragmentByTag(param);
    }

    /**
     * addFragmentByTag
     *
     */
    private AbsFragment addFragmentByTag(FragmentParameter parameter) {
       /* if(DEBUG){
            XLog.i(LOG_TAG, "addFragmentByTag:%s", parameter);
        }*/

        //XLog.e(LOG_TAG, "addFragmentByTag:%s", parameter);
        FragmentManager fm = mActivity.getSupportFragmentManager();
        AbsFragment fragment = (AbsFragment) fm.findFragmentByTag(parameter.getTag());
        if (fragment == null) {
            //XLog.e(LOG_TAG, "fragment == null");
            FragmentTransaction ft = fm.beginTransaction();
            fragment = (AbsFragment) Fragment.instantiate(mActivity, parameter.getFragmentClassName(), parameter.getParams());
            ft.add(R.id.xi_reuse_replace_container,fragment, parameter.getTag());
            ft.commitAllowingStateLoss();
        } else if (fragment.isDetached()) {

            //XLog.e(LOG_TAG, "fragment.isDetached()");
            FragmentTransaction ft = fm.beginTransaction();
            ft.attach(fragment);
            ft.commitAllowingStateLoss();
        }
        return fragment;
    }

    static boolean isSingleFragmentIntent(Activity activity) {
        FragmentParameter param = activity.getIntent().getParcelableExtra(
                SINGLE_FRAGMENT_ACTIVITY_START_ME_PARAM);
        return param != null;
    }

 /*   public static IntentBuilder builderMessage(Context context) {
        IntentBuilder b = new IntentBuilder();
        b.create(context, ReuseMsgActivity.class);
        return b;
    }*/

    public static IntentBuilder builder(Context context) {
        IntentBuilder b = new IntentBuilder();
        b.create(context, ReuseActivity.class);
        return b;
    }

   /*  public static IntentBuilder builderCustom(Context context) {
        IntentBuilder b = new IntentBuilder();
        b.create(context, ReuseCustomActivity.class);
        return b;
    }

    public static IntentBuilder builderThemeCustom(Context context) {
        IntentBuilder b = new IntentBuilder();
        b.create(context, ReuseThemeActivity.class);
        return b;
    }



    public static IntentBuilder builderSlideCustom(Context context) {
        IntentBuilder b = new IntentBuilder();
        //b.create(context, ReuseActivity.class);
        b.create(context, ReuseSlideCustomActivity.class);
        return b;
    }

    public static IntentBuilderEx builderCustomEx(Context context) {
        IntentBuilderEx b = new IntentBuilderEx();
        b.create(context, TopicDetailActivity.class);
        return b;
    }

    public static IntentBuilder builderUpdownCustom(Context context) {
        IntentBuilder b = new IntentBuilder();
        b.create(context, ReuseUpdownCustomActivity.class);
        return b;
    }*/

    public static IntentBuilderEx builderCustomEx(Context context) {
        IntentBuilderEx b = new IntentBuilderEx();
        b.create(context, MyReuseSupportActivity.class);
        return b;
    }

    public static class IntentBuilder {

        private FragmentParameter mParams;

        private Intent intent;

        public IntentBuilder create(Context context,
                Class<? extends ReuseActivity> clazz) {
            intent = new Intent(context, clazz);
            return this;
        }

        public IntentBuilder setFragmentParameter(FragmentParameter parameter) {
            this.mParams = parameter;
            return this;
        }

        public IntentBuilder setFragment(Class<? extends AbsFragment> clazz){
            setFragmentParameter(new FragmentParameter(clazz));
            return this;
        }

        public Intent build() {
            mParams.serialization(intent);
            if(mParams != null){
                intent.addFlags(mParams.getFlags());
            }
            return intent;
        }
    }

    public static class IntentBuilderEx {

        private FragmentParameter mParams;

        private Intent intent;
        private int mTranslucent=0;

        public IntentBuilderEx create(Context context,
                                    Class<? extends MyReuseSupportActivity> clazz) {
            intent = new Intent(context, clazz);
            return this;
        }
/*

        public FragmentParameter addFlags(int flag){
            mFlags |= flag;
            return this;
        }

        public FragmentParameter clearFlags(){
            mFlags = 0;
            return this;
        }
*/


        public IntentBuilderEx setFragmentParameter(FragmentParameter parameter,int translucentFlag) {
            this.mParams = parameter;
            this.mTranslucent= translucentFlag;
            return this;
        }
/*
        public IntentBuilderEx setFragmentParameter(FragmentParameter parameter,boolean Whitetranslucent) {
            this.mParams = parameter;
            if(Whitetranslucent){
                this.mTranslucent|= 0x00000002;
            }
            return this;
        }*/

        public IntentBuilderEx setFragmentParameter(FragmentParameter parameter) {
            this.mParams = parameter;
            return this;
        }

        public IntentBuilderEx setFragment(Class<? extends MySupportFragment> clazz){
            setFragmentParameter(new FragmentParameter(clazz));
            return this;
        }

        public Intent build() {
            intent.putExtra(ArgConstant.TYPE,mTranslucent);
            mParams.serialization(intent);
            if(mParams != null){
                intent.addFlags(mParams.getFlags());
            }
            return intent;
        }
    }
}
