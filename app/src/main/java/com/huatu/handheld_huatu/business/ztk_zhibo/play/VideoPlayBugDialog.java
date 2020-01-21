package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.utils.InputMethodUtils;

import rx.subscriptions.CompositeSubscription;

public class VideoPlayBugDialog extends Dialog implements View.OnClickListener {

    private Activity mContext;
    protected CompositeSubscription compositeSubscription = null;

    public VideoPlayBugDialog(Activity context) {
        super(context, R.style.ThemePopup);
        this.mContext = context;
       // this.curAddressId = curAddressId;
    }


    private EditText mEditBugText;;
    private TextView mBtnBugReportSend;
    private View mlayoutBugReportEdit,mlayoutBugReportFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//.SOFT_INPUT_ADJUST_RESIZE adjustPan

        setContentView(R.layout.live_video_bug_report_layout);

        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popup_anim_bottom2);
        findViewById(R.id.live_video_bug_report_can_not_play).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_no_voice).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_ppt).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_voice).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_play_other).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_other).setOnClickListener(this);
        findViewById(R.id.live_video_bug_report_close).setOnClickListener(this);
       // compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mEditBugText=(EditText)findViewById(R.id.live_video_bug_report_edit_text) ;
        mBtnBugReportSend=(TextView)findViewById(R.id.live_video_bug_report_send) ;
        mBtnBugReportSend.setOnClickListener(this);
        mEditBugText.addTextChangedListener(new SimpleTextWatcher() {
             @Override
             public void afterTextChanged(Editable s) {
                String text = mEditBugText.getText().toString().trim();
                if(TextUtils.isEmpty(text)) {
                    mBtnBugReportSend.setTextColor(Color.parseColor("#999999"));
                    mBtnBugReportSend.setEnabled(false);
                } else {
                    mBtnBugReportSend.setTextColor(Color.parseColor("#e9304e"));
                    mBtnBugReportSend.setEnabled(true);
                }
            }
        });
        this.findViewById(R.id.live_video_bug_report_other).setOnClickListener(this);
        mlayoutBugReportEdit=this.findViewById(R.id.live_video_bug_report_edit_layout);
        mlayoutBugReportFirst=this.findViewById(R.id.live_video_bug_report_first_layout);
        this.findViewById(R.id.live_video_bug_report_blank_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlayoutBugReportEdit.getVisibility() == View.VISIBLE) {
                    mlayoutBugReportFirst.setVisibility(View.VISIBLE);
                    mlayoutBugReportEdit.setVisibility(View.GONE);
                    InputMethodUtils.hideMethod(getContext(),mEditBugText);
                }
            }
        });
    }

    String mlessonId = "";
    int mPlayType;
    boolean mIsLiveVideo;
    String mCourseId;
    public void build(String lessionId,int playType,boolean isLive,String courseId,CompositeSubscription comSubscription){
        mlessonId=lessionId;
        mPlayType=playType;
        mIsLiveVideo=isLive;
        mCourseId=courseId;
        this.compositeSubscription=comSubscription;
    }

    private void reportBug(final String desc) {
        if(!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查您的网络");
            return;
        }
        showProgress();
        FeedbackBean feedbackBean = new FeedbackBean(desc + ",pType:" + mPlayType
                    + ",live:" + (mIsLiveVideo ? 1 : 0) + ",cId:" + mCourseId
                    + ",rId:" + mlessonId + ",model:" +  Build.MODEL
                    + ",os:" + Build.VERSION.RELEASE + ",con:" + (NetUtil.isConnected() ? 1: 0)
                    + ",wifi:" + (NetUtil.isWifi() ? 1 : 0)
                    + ",APP:" + AppUtils.getVersionName() + ",time:" + System.currentTimeMillis(),
                    TextUtils.isEmpty(SpUtils.getMobile()) ? SpUtils.getUname() : SpUtils.getMobile(), 4);
            ServiceProvider.sendFeedBack(compositeSubscription, feedbackBean, new NetResponse(){
                @Override
                public void onSuccess(BaseResponseModel model) {
                    hideProgess();
                    mlayoutBugReportFirst.setVisibility(View.VISIBLE);
                    mlayoutBugReportEdit.setVisibility(View.GONE);
                    if(mEditBugText!=null) mEditBugText.setText("");
                    ToastUtils.showShort("反馈成功");
                    VideoPlayBugDialog.this.dismiss();
                    //onClickBugReportClose();
                }
                 @Override
                public void onError(Throwable e) {
                     LogUtils.e("onError",e.getMessage());
                    hideProgess();
                    ToastUtils.showShort("反馈失败");
                }
            });

     }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           case R.id.live_video_bug_report_send:
               String tmpDes=mEditBugText.getText().toString().trim();
                reportBug(tmpDes);
                break;
            case R.id.live_video_bug_report_other:
                showBugReportOtherBug();
                break;
            case R.id.live_video_bug_report_close:
                this.dismiss();
                break;
            case R.id.live_video_bug_report_can_not_play:
                reportBug("视频无法播放");
                break;
            case  R.id.live_video_bug_report_no_voice:
                reportBug("视频没有声音");
                break;
            case R.id.live_video_bug_report_ppt:
                reportBug("PPT/声音不同步");
                break;
            case R.id.live_video_bug_report_voice:
                reportBug("人像/声音不同步");
                break;
            case R.id.live_video_bug_report_play_other:
                reportBug("播放不流畅");
                break;
        }
    }

    public void showBugReportOtherBug() {
        mlayoutBugReportFirst.setVisibility(View.GONE);
        mlayoutBugReportEdit.setVisibility(View.VISIBLE);
        mBtnBugReportSend.setTextColor(Color.parseColor("#999999"));
       // mBtnBugReportSend.setEnabled(false);
        mEditBugText.setFocusable(true);
        mEditBugText.setFocusableInTouchMode(true);
       // InputMethodUtils.showMethodDelayed(getContext(),mEditBugText,300);

        if(mEditBugText!=null){
            //设置可获得焦点
            mEditBugText.setFocusable(true);
            mEditBugText.setFocusableInTouchMode(true);
            //请求获得焦点
            mEditBugText.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) mEditBugText
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEditBugText, 0);
        }
    }

    CustomLoadingDialog progressDlg;
    private void showProgress() {
          if(!Method.isActivityFinished(mContext)) {
            if(progressDlg == null) {
                progressDlg = new CustomLoadingDialog(mContext);
            }
            progressDlg.show();
        }
    }

    private void hideProgess() {
        if (progressDlg == null) {
            return;
        }
        if (!Method.isActivityFinished(mContext)) {
            progressDlg.dismiss();
        }
    }


    /*   new Thread(new Runnable() {
            @Override
            public void run() {
                String url = GenseeLog.reportDiagonse(UniApplicationContext.getContext(),
                        desc, ServiceType.WEBCAST, true);
                LogUtils.i(url);
                UniApplicationLike.getApplicationHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.showShort("反馈成功");
                        onClickBugReportClose();
                    }
                });
            }
        }).start();*/
    /*@OnClick(R.id.live_video_bug_report_close)
    public void onClickBugReportClose() {
        layoutBugReport.setVisibility(View.GONE);
        if(isPortrait && isLiveVideo && currentItem == 1) {
            setSendLayoutState(true);
        }
    }
    @OnClick(R.id.live_video_bug_report_can_not_play)
    public void onClickBugReportCanNotPlay() {
        reportBug("视频无法播放");
    }
    @OnClick(R.id.live_video_bug_report_no_voice)
    public void onClickBugReportNoVoice() {
        reportBug("视频没有声音");
    }
    @OnClick(R.id.live_video_bug_report_ppt)
    public void onClickBugReportPPT() {
        reportBug("PPT/声音不同步");
    }
    @OnClick(R.id.live_video_bug_report_voice)
    public void onClickBugReportVoice() {
        reportBug("人像/声音不同步");
    }
    @OnClick(R.id.live_video_bug_report_play_other)
    public void onClickBugReportPlayOther() {
        reportBug("播放不流畅");
    }

    @OnClick(R.id.live_video_bug_report_send)
    public void onClickBugReportSend() {
        String text = editBugReport.getText().toString().trim();
        if(TextUtils.isEmpty(text)) {
            ToastUtils.showShort("请输入内容");
            return;
        }
        editBugReport.setText("");
        reportBug(text);
    }*/
}
