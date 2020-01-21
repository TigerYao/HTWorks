/*******************************************************************************
 * Copyright 2013-2014 Toaker framework-master
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;


import com.huatu.handheld_huatu.R;

import java.util.Arrays;


/**
 * 页面跳转的参数封装对象
 *
 * author Toaker [Toaker](ToakerQin@gmail.com)
 *         [Toaker](http://www.toaker.com)
 * Time Create by 2015/4/8 22:44
 */
public class FragmentParameter implements Parcelable {

    public static final String FRAGMENT_CLASS = "fragment_class";

    public static final String FRAGMENT_PARAMETER = "fragment_parameter";

    public static final String FRAGMENT_TAG = "fragment_tag";

    public static final String REQUEST_CODE = "request_code";

    public static final String RESULT_CODE = "result_code";

    public static final String FRAGMENT_ANIMATION = "fragment_animation";



    /** 序列化参数 到 Intent*/
    public void serialization(Intent intent){
        if(intent == null){
            return;
        }
        intent.putExtra(FRAGMENT_CLASS,getFragmentClassName());
        intent.putExtra(FRAGMENT_PARAMETER,getParams());
        intent.putExtra(FRAGMENT_TAG,getTag());
        intent.putExtra(REQUEST_CODE,getRequestCode());
        intent.putExtra(RESULT_CODE,getResultCode());
        intent.putExtra(FRAGMENT_ANIMATION,getAnimationRes());

    }

    /** 从 Intent 中反序列化  参数到 该对象 */
    public static FragmentParameter deserialization(Intent intent){
        if(intent == null){
            return null;
        }
        FragmentParameter parameter = new FragmentParameter(intent.getStringExtra(FRAGMENT_CLASS));
        parameter.setParams(intent.getBundleExtra(FRAGMENT_PARAMETER));
        parameter.setTag(intent.getStringExtra(FRAGMENT_TAG));
        parameter.setRequestCode(intent.getIntExtra(REQUEST_CODE,NO_RESULT_CODE));
        parameter.setResultCode(intent.getIntExtra(RESULT_CODE,NO_RESULT_CODE));
        parameter.setAnimationRes(intent.getIntArrayExtra(FRAGMENT_ANIMATION));

        return parameter;
    }

    public static final int NO_RESULT_CODE = -9991;

    public String mFragmentClass;

    public Bundle mParams;

    public String mTag;

    public int                mRequestCode = NO_RESULT_CODE;

    public int                mResultCode = NO_RESULT_CODE;

    public int                mFlags;//intent的flag

   // public int                mFragmentFlags;//FragmentParameter的flag

    public int[]              mAnimationDefaultRes = new int[]{R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit,
            R.anim.fragment_slide_right_back_in, R.anim.fragment_slide_left_back_out};

 /*   public int[]              mAnimationDefaultRes = new int[]{R.anim.v_fragment_enter, R.anim.v_fragment_pop_exit,
            R.anim.v_fragment_pop_enter, R.anim.v_fragment_exit};*/


    /*    public static int[]              mAnimationTopRes=new int[]{R.anim.fragment_slide_top_enter,R.anim.fragment_slide_bottom_exit,
                R.anim.fragment_slide_bottom_back_in,R.anim.fragment_slide_top_back_out};*/
//    public int[]              mAnimationRes = new int[4];
    public static int[] mAnimationTopRes = new int[]{R.anim.fragment_slide_top_enter, R.anim.slide_still,
            R.anim.slide_still, R.anim.fragment_slide_top_back_out};
    public Intent mResultParams;

     FragmentParameter(String fragmentClassName){
        if(TextUtils.isEmpty(fragmentClassName)){
            throw new IllegalArgumentException("To jump fragments cannot be NULL");
        }
        this.mFragmentClass = fragmentClassName;
        this.mTag = fragmentClassName;
    }

    public static FragmentParameter create(String fragmentClassName,Bundle args){
         FragmentParameter parameter=new FragmentParameter(fragmentClassName);
         parameter.setParams(args);
         return parameter;
    }

    public <T extends Fragment> FragmentParameter(Class<T> fragmentClass){
        if(fragmentClass == null){
            throw new IllegalArgumentException("To jump fragments cannot be NULL");
        }
        this.mFragmentClass = fragmentClass.getName();
        this.mTag = fragmentClass.getSimpleName();
    }

    public <T extends Fragment> FragmentParameter(Class<T> fragmentClass, Bundle args){
        this(fragmentClass);
        this.mParams = args;
    }

    @SuppressWarnings("unchecked")
    public String getFragmentClassName() {
        return mFragmentClass;
    }

    /**
     * @param flag {@link Intent} of Flags;
     * @return  self;
     */
    public FragmentParameter addFlags(int flag){
        mFlags |= flag;
        return this;
    }

    public FragmentParameter clearFlags(){
        mFlags = 0;
        return this;
    }

    /**
     * @param flag {@link Intent} is Flags;
     * @return  self;
     */
    public FragmentParameter removeFlag(int flag){
        mFlags &= ~flag;
        return this;
    }

    public int getFlags(){
        return mFlags;
    }

    public Intent getResultParams() {
        return mResultParams;
    }

    /** 设置页面返回参数 */
    public FragmentParameter setResultParams(Intent mResultParams) {
        this.mResultParams = mResultParams;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    /** 设置Fragment Tag */
    public FragmentParameter setTag(String mTag) {
        this.mTag = mTag;
        return this;
    }

    public Bundle getParams() {
        return mParams;
    }

    public FragmentParameter setParams(Bundle mParams) {
        this.mParams = mParams;
        return this;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    /** 设置请求码 */
    public FragmentParameter setRequestCode(int mRequestCode) {
        this.mRequestCode = mRequestCode;
        return this;
    }

    public int getResultCode() {
        return mResultCode;
    }

    /** 设置页面的返回码 */
    public FragmentParameter setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
        return this;
    }



    public int[] getAnimationRes() {
        return mAnimationDefaultRes;
    }

    /** 设置转场动画 */
    public FragmentParameter setAnimationRes(int[] mAnimationRes) {
        this.mAnimationDefaultRes = mAnimationRes;
        return this;
    }

    public FragmentParameter(Parcel in) {

        mFragmentClass = in.readString();

        mParams = in.readBundle();

        mTag = in.readString();

        mRequestCode = in.readInt();

        mResultCode = in.readInt();

        int size = in.readInt();
        mAnimationDefaultRes = new int[size];
        in.readIntArray(mAnimationDefaultRes);

        mResultParams = in.readParcelable(getClass().getClassLoader());

    }

    public static final Creator<FragmentParameter> CREATOR = new Creator<FragmentParameter>() {

        @Override
        public FragmentParameter createFromParcel(Parcel source) {

            return new FragmentParameter(source);

        }

        @Override
        public FragmentParameter[] newArray(int size) {

            return new FragmentParameter[size];

        }

    };

    @Override
    public int describeContents() {

        return 0;

    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mFragmentClass);

        dest.writeBundle(mParams);

        dest.writeString(mTag);

        dest.writeInt(mRequestCode);

        dest.writeInt(mResultCode);

        int size = 0;
        if(mAnimationDefaultRes != null){
            size = mAnimationDefaultRes.length;
        }
        dest.writeInt(size);

        dest.writeIntArray(mAnimationDefaultRes);

        dest.writeParcelable(mResultParams,flags);
    }

    @Override
    public String toString() {
        return "FragmentParameter{" +
                "mFragmentClass=" + mFragmentClass +
                ", mParams=" + mParams +
                ", mTag='" + mTag + '\'' +
                ", mRequestCode=" + mRequestCode +
                ", mResultCode=" + mResultCode +
                ", mFlags=" + mFlags +
                ", mAnimationRes=" + Arrays.toString(mAnimationDefaultRes) +
                ", mResultParams=" + mResultParams +
                '}';
    }
}
