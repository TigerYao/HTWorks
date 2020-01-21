package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.log.BJFileLog;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.me.viewhelper.UploadImgView;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackBean;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.UploadImgBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.shimmer.Shimmer;
import com.huatu.shimmer.ShimmerTextView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_left_topbar;
    private EditText et_feedback_content;
    private EditText et_tel_content;
    private RelativeLayout rl_commit;
    private CustomDialog customDialog;
    private TextView text_number;
    private TextView tv_submit;
    private GridView gview;
    private int defaultSelection = 0;

    Shimmer shimmer;
    ShimmerTextView mTipTextView;

    private String feedback_img_path;
    private int feedBackType = 4;

    private UploadImgView upload_ll;
    private List<String> mlist = new ArrayList<>();
    private FeedbackBean feedbackBean;

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarDarkMode(FeedbackActivity.this);
        initView();
        initList();
        initGradView();
        setListener();
        initUploadIv();
    }

    private void initList() {
        mlist.clear();
        mlist.add("系统问题");
        mlist.add("功能建议");
        mlist.add("申论反馈");
        mlist.add("课程内容");
        mlist.add("其他");
    }

    private void initGradView() {
        gview = findViewById(R.id.gview);
        gview.setAdapter(new CommonAdapter<String>(this, mlist, R.layout.item_feedback) {
            @Override
            public void convert(final ViewHolder holder, String item, final int position) {
                holder.setText(R.id.tv_feedback, item);
                if (position == defaultSelection) {
                    holder.getView(R.id.tv_feedback).setBackgroundResource(R.drawable.selected_feedback);
                    holder.setTextColor(R.id.tv_feedback, ContextCompat.getColor(mContext, R.color.white));
                } else {
                    holder.getView(R.id.tv_feedback).setBackgroundResource(R.drawable.unselect_feedback);
                    holder.setTextColor(R.id.tv_feedback, ContextCompat.getColor(mContext, R.color.black250));
                }
                holder.setViewOnClickListener(R.id.tv_feedback, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (position == 0) {
                            feedBackType = 4;
                        } else if (position == 1) {
                            feedBackType = 5;
                        } else if (position == 2) {
                            feedBackType = 7;
                        } else if (position == 3) {
                            feedBackType = 6;
                        } else if (position == 4) {
                            feedBackType = 1;
                        } else {
                            feedBackType = 0;
                        }
                        if (position != defaultSelection) {
                            defaultSelection = position;
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    public void toggleAnimation(ShimmerTextView target) {
        if (null == target) return;

        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(target);
        }
    }

    private void initView() {

        mTipTextView = findViewById(R.id.tip_text);
        toggleAnimation(mTipTextView);
        rl_left_topbar = findViewById(R.id.rl_left_topbar);
        et_feedback_content = findViewById(R.id.et_feedback_content);
        et_tel_content = findViewById(R.id.et_tel_content);
        text_number = findViewById(R.id.text_number);
        text_number.setText("0/200");
        rl_commit = findViewById(R.id.rl_commit);
        rl_commit.setEnabled(false);
        tv_submit = findViewById(R.id.tv_submit);

        upload_ll = findViewById(R.id.upload_ll);
        if (DisplayUtil.getScreenWidth() > 1500) {
            int size = DisplayUtil.getScreenWidth() / 1000;
            size = size * 15;
            if (size < 15) {
                size = 15;
            } else if (size > 50) {
                size = 50;
            }
        }
    }

    private void initUploadIv() {
        if (getIntent() != null) {
            feedback_img_path = getIntent().getStringExtra("feedback_img_path");
        }
        if (upload_ll != null) {
            upload_ll.setTagView(rl_left_topbar);
            upload_ll.setCompositeSubscription(compositeSubscription);
        }
        if (feedback_img_path != null && upload_ll != null) {
            upload_ll.addIvInfo(feedback_img_path);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (upload_ll != null) {
            upload_ll.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
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

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        rl_commit.setOnClickListener(this);

        et_feedback_content.setFilters(new InputFilter[]{new Max(200)});


        et_feedback_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trim = s.toString().trim();
                int length = trim.length();
                text_number.setText(length + "/200");
                if (length > 0) {
                    tv_submit.setTextColor(Color.parseColor("#A66200"));
                    rl_commit.setBackgroundColor(Color.parseColor("#FFCA0E"));
                    rl_commit.setEnabled(true);
                } else {
                    tv_submit.setTextColor(Color.parseColor("#4A4A4A"));
                    rl_commit.setBackgroundColor(Color.parseColor("#D8D8D8"));
                    rl_commit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class Max implements InputFilter {
        private int max;

        public Max(int max) {
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int len = max - (dest.length() - (dend - dstart));
            if (len < (end - start)) {
                CommonUtils.showToast("输入字符最大200");
            }
            if (len < 0) {
                return "";
            } else if (len >= end - start) {
                return null;
            } else {
                return source.subSequence(start, start + len);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_left_topbar:
                FeedbackActivity.this.finish();
                break;
            case R.id.rl_commit:
                sendFeedback();
                break;
        }
    }

    private void sendFeedback() {
        String content = et_feedback_content.getText().toString().trim();
        String contacts = et_tel_content.getText().toString().trim();

        customDialog = new CustomDialog(FeedbackActivity.this, R.layout.dialog_feedback_commit);
        customDialog.show();
        rl_commit.setEnabled(false);

        if (!NetUtil.isConnected()) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }

        if (!TextUtils.isEmpty(content) && content.length() > 200) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("反馈内容最多200字");
            return;
        }

        //联系方式不为空，且输入的内容不包含@，内容不是纯数字
        if (!TextUtils.isEmpty(contacts) &&
                !TextUtils.isDigitsOnly(contacts) &&
                !contacts.contains("@")) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("请输入正确的联系电话或邮箱");
            return;
        }

        //联系方式不为空，输入的为纯数字，判断为手机号，长度不为11，给出提示
        if (!TextUtils.isEmpty(contacts) &&
                TextUtils.isDigitsOnly(contacts) &&
                contacts.length() != 11) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("手机号长度错误");
            return;
        }

        //联系方式不为空，不是纯数字，包含@，全部认为是邮箱，判断是否满足邮箱的正则，给出提示
        if (!TextUtils.isEmpty(contacts) &&
                contacts.contains("@") &&
                !isEmail(contacts)) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("邮箱格式出错");
            return;
        }


        if (feedBackType == 0) {
            customDialog.dismiss();
            rl_commit.setEnabled(true);
            CommonUtils.showToast("请选择反馈类型");
            return;
        }

        String imgs = null;
        if (upload_ll != null) {
            ArrayList<UploadImgView.ivInfo> uploadIvList = upload_ll.getUploadIvList();
            if (uploadIvList != null) {
                if (uploadIvList.size() > 0) {
                    for (int i = 0; i < uploadIvList.size(); i++) {
                        UploadImgView.ivInfo ivInfo = uploadIvList.get(i);
                        if (ivInfo != null && ivInfo.url != null) {
                            if (i == 0) {
                                imgs = ivInfo.url;
                            } else {
                                imgs = imgs + "," + ivInfo.url;
                            }
                        }
                    }
                }
            }
        }
        feedbackBean = new FeedbackBean(content, contacts, feedBackType, imgs);
        LogUtils.d("FeedbackActivity", imgs);
        if (!uploadLog()) {
            reportFeedback();
        }
    }

    private void reportFeedback() {
        Subscription subscription = RetrofitManager.getInstance().getService().sendFeedback(feedbackBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FeedbackResponseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        rl_commit.setEnabled(true);
                        customDialog.dismiss();
                        CommonUtils.showToast("您的意见提交失败");
                    }

                    @Override
                    public void onNext(FeedbackResponseBean feedbackResponseBean) {
                        rl_commit.setEnabled(true);
                        customDialog.dismiss();

                        int code = feedbackResponseBean.code;
                        if (code == 1000000) {
                            et_feedback_content.setText("");
                            et_tel_content.setText("");
                            CommonUtils.showToast("您的意见已经提交成功");
                            FeedbackActivity.this.finish();
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("您的意见提交失败");
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\" +
                ".][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean uploadLog() {
        if (CommonUtils.isEmpty(BJFileLog.getLogDirPath()))
            return false;
        File file = new File(BJFileLog.getLogDirPath());
        if (file.exists() && file.isDirectory()) {
            BJFileLog.zipLogFiles(new BJFileLog.IZipCallback() {
                @Override
                public void zipSuccess(String logPath) {
                    File logFile = new File(logPath);
                    if (logFile != null && logFile.exists() && logFile.length() > 0)
                        uploadFile(logFile);
                    else
                        zipFailed("");
                }

                @Override
                public void zipFailed(String errorMsg) {
                    reportFeedback();
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private void uploadFile(File logPath) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/zip"), logPath);
        MultipartBody.Part body = MultipartBody.Part.createFormData("log", logPath.getName(), requestBody);
        Subscription logSubscribe = RetrofitManager.getInstance().getService().sendFeedBackLog(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadImgBean>() {
                    @Override
                    public void onCompleted() {
                        reportFeedback();
                        BJFileLog.deleteZipLogFiles();
                    }

                    @Override
                    public void onError(Throwable e) {
                        reportFeedback();
                        BJFileLog.deleteZipLogFiles();
                    }

                    @Override
                    public void onNext(UploadImgBean responseBean) {
                        long code = responseBean.code;
                        if (code == 1000000) {
                            String data = (String) responseBean.data;
                            if (!CommonUtils.isEmpty(data)) {
                                String url = data;
                                feedbackBean.setLog(url);
                                LogUtils.d("feedback", "zipUrl=" + url);
                            }
                        }
                    }
                });
        compositeSubscription.add(logSubscribe);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toggleAnimation(mTipTextView);
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static void newInstance(Context context, String path) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        if (path != null) {
            intent.putExtra("feedback_img_path", path);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void newInstance(Context context) {
        newInstance(context, null);
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
