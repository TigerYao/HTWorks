package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import org.json.JSONObject;

public class VideoTestJSBrigde {
    private LiveTestView mView;
    private String mCourseId;
    private String mRoomId;

    public void setMctx(LiveTestView liveTestView, String couseId, String roomId) {
        mView = liveTestView;
        mCourseId = couseId;
        mRoomId = roomId;
    }

    @JavascriptInterface
    public void closeWebView() {
        Method.runOnUiThread((Activity) mView.getContext(), new Runnable() {
            @Override
            public void run() {
               try {
                   mView.closePage();
                   ToastUtils.cancle();
                   hiddenWaiting();
               }catch (Exception e){}
            }
        });

    }


    @JavascriptInterface
    public void showToast(final String info) {
        Method.runOnUiThread((Activity) mView.getContext(), new Runnable() {
            @Override
            public void run() {
                try {
                    ToastUtils.showShortToast(mView.getContext(), info);
                }catch (Exception e){}
            }
        });
    }

    @JavascriptInterface
    public String connectCallBackUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", mCourseId);
            jsonObject.put("token", UserInfoUtil.token);
            jsonObject.put("roomId", mRoomId);
            LogUtils.d("connectCallBackUser");
        } catch (Exception e) {}
        return jsonObject.toString();
    }

    @JavascriptInterface
    public void showWaiting(){
        Method.runOnUiThread(((Activity) mView.getContext()), new Runnable() {
            @Override
            public void run() {
                ((LiveVideoForLiveActivity)mView.getContext()).showProgress();
            }
        });
    }

    @JavascriptInterface
    public void hiddenWaiting(){
        Method.runOnUiThread(((Activity) mView.getContext()), new Runnable() {
            @Override
            public void run() {
                ((LiveVideoForLiveActivity)mView.getContext()).hideProgess();
            }
        });
    }

}