package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitStatusCallbackEx;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.RegexUtils;

import retrofit2.Response;

/**
 * Created by cjx on 2018\9\29 0029.
 */

public abstract class AbsVerifyCodePresenter {

    MyCount mReformTimer;
    TextView mtmpGetVerification;
    String mFormatString;
    public void onDestroy() {

        if(mReformTimer!=null) mReformTimer.cancel();

    }

    protected abstract void showLoading();

    protected abstract void hideLoading();

    protected abstract Activity getActivity();

    public void setAuthCodeView(TextView verificationView){
        mtmpGetVerification=verificationView;
        mFormatString= ResourceUtils.getString(R.string.xs_$s_second_re_get2);
    }

    public void setAuthCodeView(TextView verificationView,String formatStr){
        mtmpGetVerification=verificationView;
        mFormatString=formatStr;
    }

    public void getSmsAuthCode(String phone,String authType){
        if(mReformTimer!=null&&mReformTimer.isRunning()){
            return ;
        }
        if(!RegexUtils.matcherPhone(phone)){
           // MaterialToast.makeText(getContext(), R.string.xs_please_input_phone).show();
            ToastUtils.showShort("手机号不合法");
            return;
        }
        showLoading();
        //AuthApiServer.sendMsg(phone, authType).enqueue(getVerificationSubscriber());
    }


    private RetrofitStatusCallbackEx<BaseResponse> getVerificationSubscriber() {
        return new RetrofitStatusCallbackEx<BaseResponse>(getActivity()) {

            @Override
            public void onSuccess(Response<BaseResponse> verificationResponse) {
                hideLoading();
                ToastUtils.showShort("验证码已发送,请注意查收");
                 processTimer();
            }

            @Override
            public void onFailure(String error,int type) {
                hideLoading();
                ToastUtils.showShort(type==3? error:"短信验证码获取失败");

            }
        };
    }

    public boolean isAuthRunning(){
        if(mReformTimer!=null&&mReformTimer.isRunning()){
            return true;
        }
        return  false;
    }


    class MyCount extends CountDownTimer {

        private boolean mIsRunning=false;
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void doStart(){
            mIsRunning=true;
            mtmpGetVerification.setEnabled(false);
            mtmpGetVerification.setPressed(true);
            super.start();
        }

        public boolean isRunning(){
            return mIsRunning;
        }
        @Override
        public void onFinish() {

            mIsRunning=false;
            mtmpGetVerification.setText("获取验证码");
            mtmpGetVerification.setEnabled(true);
            mtmpGetVerification.setPressed(false);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            mtmpGetVerification.setText(String.format(mFormatString,millisUntilFinished / 1000));
            // Toast.makeText(NewActivity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延�?
        }
    }


    private void processTimer() {

        if(mReformTimer==null) mReformTimer = new MyCount(60000, 1000);
        if(mReformTimer.isRunning()) return;
        mReformTimer.doStart();
    }
}
