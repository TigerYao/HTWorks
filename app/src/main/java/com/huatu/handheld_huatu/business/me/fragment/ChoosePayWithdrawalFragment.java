package com.huatu.handheld_huatu.business.me.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsVerifyCodePresenter;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.BindPhoneActivity;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.ClearEditText;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\9\29 0029.
 */

public class ChoosePayWithdrawalFragment extends MySupportFragment {
    private static final String TAG = "ChoosePayWithdrawalFrag";
    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;
    //微信提现
    @BindView(R.id.wx_check_layout)
    LinearLayout mWxCheckLayout;
    //支付宝提现
    @BindView(R.id.alipay_check_layout)
    LinearLayout mAliCheckLayout;
    //去微信授权布局
    @BindView(R.id.ll_to_wx)
    LinearLayout ll_to_wx;
    //去微信授权按钮
    @BindView(R.id.rl_go_to_wx)
    RelativeLayout rl_go_to_wx;

    //输入信息布局
    @BindView(R.id.msg_container_layout)
    LinearLayout msg_container_layout;

    //支付宝账号输入框
    @BindView(R.id.ali_username_txt)
    ClearEditText ali_username_txt;
    //手机号框
    @BindView(R.id.phone_account_txt)
    EditText phone_account_txt;
    //验证码框
    @BindView(R.id.sms_txt)
    ClearEditText sms_txt;

    @BindView(R.id.tip_type_txt)
    TextView mSelectTypeTxt;



    @BindView(R.id.authcode_btn)
    TextView mAuthCodeBtn;
    //提现按钮
    @BindView(R.id.submit_btn)
    TextView submit_btn;

    @BindView(R.id.tv_wx_tip)
    TextView tv_wx_tip;

    private int type = 0;
    private boolean hasRight = false;
    protected CompositeSubscription compositeSubscription;
    private Subscription mTimeClockSubscription;
    private int mCount = 60;
    private String openid;


    public static ChoosePayWithdrawalFragment newInstance() {
        return new ChoosePayWithdrawalFragment();
    }

    @Override
    public int getContentView() {
        return R.layout.red_envelope_withdrawal_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mWxCheckLayout.setSelected(true);
        SpannableStringBuilder builder=new SpannableStringBuilder("");
        builder.append("提现金额不得低于1");
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(),
                R.color.indicator_color)), 8, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("元");
        tv_wx_tip.setText(builder);

        if (SpUtils.getMobile() != null) {
            phone_account_txt.setText(SpUtils.getMobile());
        }
        phone_account_txt.setEnabled(false);
        sms_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==6){
                    submit_btn.setTextColor(getResources().getColor(R.color.white));
                    submit_btn.setBackgroundResource(R.drawable.bt_download_red);
                    submit_btn.setClickable(true);
                }else {
                    submit_btn.setTextColor(getResources().getColor(R.color.black250));
                    submit_btn.setBackgroundResource(R.drawable.bt_download_gray);
                    submit_btn.setClickable(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void setListener() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        initTitle();
        initView();
    }

    private void initTitle() {
        mTopTitleBar.setTitle("红包提现");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if (menuItem.getId() == R.id.xi_title_bar_home) {
                    getActivity().finish();
                }
            }
        });
    }


    @OnClick({R.id.rl_go_to_wx, R.id.submit_btn, R.id.wx_check_layout, R.id.alipay_check_layout, R.id.authcode_btn})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_go_to_wx:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                //请求微信授权
                userOauthVerify(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.submit_btn:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (type == 0) {
                    //有授权，提现至微信
                    putForwardToWx();
                } else if (type == 1) {
                    //提现到支付宝
                    putForwardToAli();
                }
                break;
            case R.id.wx_check_layout:
                if (type != 0) {
                    type = 0;
                    view.setSelected(true);
                    mAliCheckLayout.setSelected(false);
                    mSelectTypeTxt.setText("确认是本人提取，请短信验证提现操作");
                    if (!hasRight) {
                        ll_to_wx.setVisibility(View.VISIBLE);
                        msg_container_layout.setVisibility(View.GONE);
                    } else {
                        ll_to_wx.setVisibility(View.GONE);
                        ali_username_txt.setVisibility(View.GONE);
                        msg_container_layout.setVisibility(View.VISIBLE);
                        tv_wx_tip.setVisibility(View.VISIBLE);
                        ali_username_txt.clearFocus();
                        sms_txt.requestFocus();
                    }

                }
                break;
            case R.id.alipay_check_layout:
                if (type != 1) {
                    type = 1;
                    view.setSelected(true);
                    ll_to_wx.setVisibility(View.GONE);
                    msg_container_layout.setVisibility(View.VISIBLE);
                    ali_username_txt.setVisibility(View.VISIBLE);
                    sms_txt.clearFocus();
                    ali_username_txt.requestFocus();
                    mWxCheckLayout.setSelected(false);
                    tv_wx_tip.setVisibility(View.VISIBLE);
                    mSelectTypeTxt.setText("提现至支付宝账号");
                }
                break;
            case R.id.authcode_btn:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (mAuthCodeBtn.isEnabled()) {
                    timeClockStart();
                }
                break;
        }
    }

    private void timeClockStart() {
        //发送验证码
        ServiceProvider.getRedConfirmCode(compositeSubscription, SpUtils.getMobile(), new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("获取失败，请稍后重试");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
            }
        });
        //开始倒计时
        changeTimeClockText(mCount);

        mTimeClockSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(mCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        mAuthCodeBtn.setText("发送验证码");
                        mAuthCodeBtn.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
                        mAuthCodeBtn.setTextColor(getResources().getColor(R.color.white));
                        mAuthCodeBtn.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        changeTimeClockText(mCount - aLong - 1);
                    }
                });

        compositeSubscription.add(mTimeClockSubscription);
    }

    private void changeTimeClockText(long count) {
        mAuthCodeBtn.setTextColor(getResources().getColor(R.color.black250));
        mAuthCodeBtn.setText("重新发送"+"("+count + ")");
        mAuthCodeBtn.setBackgroundResource(R.drawable.drawable_rectangle_f3f3f3);
    }

    private void putForwardToAli() {
        String payeeAccount = ali_username_txt.getText().toString();
        if (TextUtils.isEmpty(payeeAccount)) {
            ToastUtils.showEssayToast("请输入支付宝账号");
            return;
        }
        String verify=sms_txt.getText().toString();
        if (TextUtils.isEmpty(verify)){
            ToastUtils.showEssayToast("请输入验证码");
            return;
        }

        ServiceProvider.getForwardToALi(compositeSubscription,payeeAccount,SpUtils.getMobile(),verify,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                startWithPop(new WithdrawalSuccessFragment());

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof ApiException){
                    String msg=((ApiException) e).getErrorMsg();
                    if (!TextUtils.isEmpty(msg)){
                    ToastUtils.showEssayToast(msg);
                    }
                }

            }
        });

    }

    private void putForwardToWx() {
        if (openid==null){
            ToastUtils.showEssayToast("openid不能为空");
        }
        String verify=sms_txt.getText().toString();
        if (TextUtils.isEmpty(verify)){
            ToastUtils.showEssayToast("请输入验证码");
            return;
        }
        ServiceProvider.getForwardToWx(compositeSubscription,openid,SpUtils.getMobile(),verify,new NetResponse(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof ApiException){
                    String msg=((ApiException) e).getErrorMsg();
                    if (!TextUtils.isEmpty(msg)){
                        ToastUtils.showEssayToast(msg);
                    }
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                startWithPop(new WithdrawalSuccessFragment());

            }
        });
    }


    private ProgressDialog mDialog;

    private void userOauthVerify(SHARE_MEDIA platform) {

        UMAuthListener authListener = new UMAuthListener() {
            /**
             * @desc 授权开始的回调
             * @param platform 平台名称
             */
            @Override
            public void onStart(SHARE_MEDIA platform) {
            }

            /**
             * @desc 授权成功的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param data 用户资料返回
             */
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

                if (data != null)
                    LogUtils.e("onComplete", GsonUtil.GsonString(data));
                ToastUtils.showShort("授权成功");
                openid=data.get("openid");
                LogUtils.i(TAG,openid);
                hasRight = true;
                ll_to_wx.setVisibility(View.GONE);
                msg_container_layout.setVisibility(View.VISIBLE);
                ali_username_txt.setVisibility(View.GONE);
                tv_wx_tip.setVisibility(View.VISIBLE);
                mSelectTypeTxt.setText("确认是本人提取，请短信验证提现操作");
            }

            /**
             * @desc 授权失败的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param t 错误原因
             */
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtils.showShort("获取授权失败，请稍后重试");
            }

            /**
             * @desc 授权取消的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             */
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                ToastUtils.showShort("未获取授权");
            }
        };

        UMShareAPI.get(getContext()).getPlatformInfo(getActivity(), SHARE_MEDIA.WEIXIN, authListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
