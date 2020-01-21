package com.huatu.handheld_huatu.base;


import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback2;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallbackWrapper;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallbackWrapper2;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;

/**
 * Created by xing

 */
public abstract class AbsBaseHeader<RESPONSE extends BaseResponse> extends BaseHeader implements KindRetrofitCallBack<RESPONSE> {


    protected RESPONSE mHeadInfoResponse;
    public RESPONSE getHeadResponse(){return mHeadInfoResponse; }

    public interface onHeaderLoadListener{
         void onHeaderSuccess();
    }

    protected onHeaderLoadListener mOnHeaderLoadListener;
    public void setOnHeaderLoadListener(onHeaderLoadListener onHeaderLoadListener){
        mOnHeaderLoadListener=onHeaderLoadListener;
    }

    abstract public void bindingData(RESPONSE data);

    /**
     * 获取一个网络访问回调
     */
    protected RetrofitCallback<RESPONSE> getCallback() {
        return new RetrofitCallbackWrapper<RESPONSE>(this);
    }

    protected RetrofitCallback2<RESPONSE> getCallback2() {
        return new RetrofitCallbackWrapper2<RESPONSE>(this);
    }

    @Override
    public void onSubscriberStart(){}

    @Override
    public boolean isFragmentFinished(){
        AbsFragment curFrag=getFragment();
        return curFrag==null||curFrag.isFragmentFinished();
    }

/*
    */
/**
     * 网络连接错误
     *//*

    @Override
    public void onNetworkError(RetrofitError error) {
        //MaterialToast.makeText(getContext(), R.string.xs_operation_failed).show();
    }
*/

    /**
     * 数据获取错误
     */
    @Override
    public void onError(String throwable, int type) {

    }

    /**
     * 数据加载成功
     */
    @Override
    public void onSuccess(RESPONSE response)   {
         mHeadInfoResponse=response;
         bindingData(response);
         if(null!=mOnHeaderLoadListener) mOnHeaderLoadListener.onHeaderSuccess();
    }


}
