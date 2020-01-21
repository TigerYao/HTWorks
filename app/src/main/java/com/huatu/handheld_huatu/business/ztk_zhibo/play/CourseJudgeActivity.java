package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseDialogActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.InputMethodUtils;
import com.huatu.widget.CustomRatingBar;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2017/9/20.
 * https://blog.csdn.net/wood12943907/article/details/52190135
 * Android卡牌翻转动画效果实现
 */

public class CourseJudgeActivity extends BaseDialogActivity {

    @BindView(R.id.course_judge_edit_text)
    EditText mEditText;

    @BindView(R.id.num_count_txt)
    TextView mTvNumber;

    @BindView(R.id.support_now)
    LinearLayout mBtnConfirm;

    @BindView(R.id.xi_img_loading)
    ProgressBar mProgressBar;

    @BindView(R.id.star)
    CustomRatingBar mRatingBar;

    private String courseId;
    private int courseScore=-1;
    private String mParentId="";
    private String courseRemark;
    private String lessonId;
    private boolean mForResult=false;

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    protected   int getContentViewId() {
       if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
           getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            return R.layout.course_judge_landscape_layout;// 横屏
        } else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){

            return R.layout.course_judge_layout;           // 竖屏
        }
        return R.layout.course_judge_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        initView();

    }

    protected void initView() {

        mRatingBar.setOnRatingChangeListener(new CustomRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float rating) {
               // XLog.e("onLevelChanged", rating + "");
           /*     evaluateServer((int) rating);
                superDismiss();*/
                courseScore=((int) rating);
            }
        });
        courseId = getIntent().getStringExtra("course_id");
        lessonId = getIntent().getStringExtra("lesson_id");
        mParentId = getIntent().getStringExtra("parent_id");
        mForResult = getIntent().getBooleanExtra(ArgConstant.FOR_RESUTL,false);
        // setStarState();
        mEditText.addTextChangedListener(new SimpleTextWatcher() {
             @Override
            public void afterTextChanged(Editable s) {
                courseRemark = s.toString().trim();
                if(mTvNumber!=null)
                    mTvNumber.setText(courseRemark.length() + "/200");
             }
        });
    }

    @OnClick({R.id.close_btn,R.id.close_tip_txt})
    public void closeJudage() {
        CourseJudgeActivity.this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    @OnClick(R.id.support_now)
    public void onClickConfirm() {
        courseRemark = mEditText.getText().toString().trim();
        //showProgress();
       /* if(TextUtils.isEmpty(courseRemark)){
            ToastUtils.showShort("请输入评价内容");
            return;
        }*/
        if(courseScore==-1){
            ToastUtils.showShort("请评分");
            return;
        }
        InputMethodUtils.hideMethod(this, mEditText);
        mProgressBar.setVisibility(View.VISIBLE);
        mBtnConfirm.setEnabled(false);
        ServiceExProvider.visitSimple(getSubscription(),CourseApiService.getApi().judgeCourse(courseId, lessonId, courseRemark, courseScore,mParentId),
             new NetObjResponse<String>(){
                 @Override
                 public void onError(String message, int type) {
                     mProgressBar.setVisibility(View.GONE);
                     mBtnConfirm.setEnabled(true);
                     ToastUtils.showShort("评价失败");
                 }

                 @Override
                 public void onSuccess(BaseResponseModel<String> model) {
                      // hideProgress();
                     mBtnConfirm.setEnabled(true);
                     mProgressBar.setVisibility(View.GONE);

                    // LogUtils.e("getCourseJudgeFlag",SpUtils.getCourseJudgeFlag()+"");
                     if(SpUtils.getCourseJudgeFlag()) {
                         ToastUtils.showRewardToast("EVALUATE_AFTER");
                         SpUtils.setCourseJudgeFlag(false);
                     }else
                         ToastUtils.showShort("评价成功");

                   //  CommonUtils.showToast("提交成功");
                     SharedPreferences sharedPreferences = getSharedPreferences(
                             UserInfoUtil.userName, MODE_PRIVATE);
                     sharedPreferences.edit().putBoolean(lessonId + "judge", true).commit();

                     //数据是使用Intent返回
                     Intent intent = new Intent();
                     intent.putExtra(ArgConstant.KEY_ID, 1);
                     intent.putExtra("level", courseScore);
                     CourseJudgeActivity.this.setResult(RESULT_OK, intent);
                     CourseJudgeActivity.this.finish();

                 }
        });
       /* ServiceProvider.judgeCourse(mCompositeSubscription, courseId, lessonId, courseRemark, courseScore, 1,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
               // hideProgress();
                mBtnConfirm.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                ToastUtils.showShort("评价成功");
                if(SpUtils.getCourseJudgeFlag()) {
                    ToastUtils.showRewardToast("EVALUATE_AFTER");
                    SpUtils.setCourseJudgeFlag(false);
                }
                CommonUtils.showToast("提交成功");
                SharedPreferences sharedPreferences = getSharedPreferences(
                        UserInfoUtil.userName, MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(lessonId + "judge", true).commit();

                //数据是使用Intent返回
                Intent intent = new Intent();
                intent.putExtra(ArgConstant.KEY_ID, 1);
                CourseJudgeActivity.this.setResult(RESULT_OK, intent);
                CourseJudgeActivity.this.finish();
            }

            @Override
            public void onError(final Throwable e) {
               // hideProgress();
                mProgressBar.setVisibility(View.GONE);
                mBtnConfirm.setEnabled(true);
                if(e instanceof ApiException) {
                    if(((ApiException)e).getErrorCode() == -5) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                UserInfoUtil.userName, MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean(lessonId + "judge", true).commit();
                        CourseJudgeActivity.this.finish();
                    }
                }
            }
        });*/
    }

    public static void newInstance(Activity activity, String courseId, String lessonId,String parentId) {
        Intent intent = new Intent(activity, CourseJudgeActivity.class);
        intent.putExtra("course_id", courseId);
        intent.putExtra("lesson_id", lessonId);
        intent.putExtra("parent_id",parentId);
        intent.putExtra(ArgConstant.FOR_RESUTL,true);
        activity.startActivityForResult(intent,2000);
        //activity.startActivity(intent);
    }

    /**
     * 点击空白区域隐藏键盘.
     */
  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (CourseJudgeActivity.this.getCurrentFocus() != null) {
                if (CourseJudgeActivity.this.getCurrentFocus().getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(CourseJudgeActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodUtils.hideMethod(this, mEditText);
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
