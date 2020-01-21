package com.huatu.handheld_huatu.business.ztk_zhibo.pay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.me.account.RechargeNowActivity;
import com.huatu.handheld_huatu.business.me.bean.RedBagInfo;
import com.huatu.handheld_huatu.business.me.bean.RedBagShareInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.XxbRemainderBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.PayResultDialog;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodOrderBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayPayInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomAddGroupDialog;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

//import com.huatu.handheld_huatu.business.lessons.MySingleTypeCourseFragment;

/**
 * Created by saiyuan on 2017/9/22.
 */

public class ConfirmPaymentFragment2 extends BaseFragment {

    private static final String TAG = "ConfirmPaymentFragment";

    @BindView(R.id.confirm_payment_zfb_layout)
    View layoutZfb;
    @BindView(R.id.confirm_payment_zfb_img)
    ImageView ivZfb;
    @BindView(R.id.confirm_payment_we_chat_layout)
    View layoutWeChat;
    @BindView(R.id.confirm_payment_we_chat_img)
    ImageView ivWeChat;
    @BindView(R.id.confirm_payment_xxb_layout)
    View layoutXxb;
    @BindView(R.id.confirm_payment_xxb_img)
    ImageView ivXxb;
    @BindView(R.id.confirm_payment_xxb_value_tv)
    TextView tvCurrentValue;
    @BindView(R.id.confirm_payment_xxb_charge_tv)
    TextView btnCharge;
    @BindView(R.id.confirm_payment_xxb_error_tv)
    TextView tvRemainderError;
    @BindView(R.id.confirm_payment_xxb_name)
    TextView tvXXbName;
    @BindView(R.id.confirm_payment_zfb_name)
    TextView tvzfbName;
    @BindView(R.id.confirm_payment_we_chat_name)
    TextView tvWxName;
    private String payNumber;
    private PayInfo payInfo;
    private int payType = 32;
    private int essayPayType = 2;
    private PayResultDialog payResultDialog;
    private CustomConfirmDialog dialDlg;
    private IWXAPI wxApi;
    private IWXAPI api;
    private boolean isSecKill;
    private boolean isEssayOrder;
    private int payResult = 0;
    private PayUtils mPayUtils;
    private DecimalFormat df = new DecimalFormat("0.00");
    private long xxbRemainder = -1;
    private final int xxbConverter = 100;
    private long orderId;

    private PayInfo payInfoFromNet;                             // 点击支付，获取订单信息
    private RedBagInfo redBagInfo;                              // 红包信息

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_confirm_payment_layout2;
    }

    // 这个好像没地方发消息了
    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(PayMessageEvent event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
        if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
            ToastUtils.showEssayToast("支付成功！");
            onLoadCharge();
        } else {
            ToastUtils.showEssayToast("支付失败！");
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(PayResultEvent event) {
        if (event.type == PayResultEvent.PAY_RESULT_EVENT_SHOW_CUSTOMER) {              // 选择其他支付方式，然后就打电话给客服了
            onCallPhone();
        } else if (event.type == PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK) {         // 支付成功了
            if (isEssayOrder) {
                if (event.params == PayUtils.PAY_RESULT_SUCC) {                         // 支付成功了
                    ToastUtils.showEssayToast("支付成功！");
                    payEssaySuccess();
                } else {
                    ToastUtils.showEssayToast("支付失败！");
                }
            } else {
                payResult = event.params;
                if (payResult == PayUtils.PAY_RESULT_SUCC) {
                    ToastUtils.showShort("支付成功");
                    if (payInfoFromNet != null && payInfoFromNet.redEnvelopeId > 0) {
                        showRedBag();                                                   // 检查、发红包
                    } else {
                        if (payInfo != null && payInfo.qqWxGroup == null) {
                            createPayResultDlg();
                            payResultDialog.show();
                            payResultDialog.setResultType(payResult);
                        }else if(payInfo == null){
                            onLoadCharge();
                        }else
                            paySuccess();
                    }
                } else
                    ToastUtils.showEssayToast("支付失败！");
            }
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        payNumber = args.getString("pay_number");
        orderId = args.getLong("order_id");
        payInfo = (PayInfo) args.getSerializable("pay_info");
        isSecKill = args.getBoolean("is_sec_kill");
        isEssayOrder = args.getBoolean("is_essay_order");
        xxbRemainder = args.getLong("xxbRemainder", -1);
        payType = args.getInt("paytype");
        mPayUtils = new PayUtils();
        api = WXAPIFactory.createWXAPI(UniApplicationContext.getContext(), PayUtils.APP_ID);
        api.registerApp(PayUtils.APP_ID);
//        if (payType == 1) {
        onClickXxb();
        layoutXxb.setVisibility(View.VISIBLE);
//        } else {
//            onClickZfb();
//            layoutXxb.setVisibility(View.GONE);
//        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfoFromNet = this.payInfo = payInfo;
        if(payInfo != null && payInfo.qqWxGroup != null)
            payInfo.qqWxGroup.title = payInfo.title;
    }

    //    /**
//     * 申论支付成功
//     */
    private void payEssaySuccess() {

        TimeUtils.delayTask(new Runnable() {
            @Override
            public void run() {
                EssayExamMessageEvent messageEvent = new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_getCheckCountList);
                EventBus.getDefault().post(messageEvent);
//                    BaseFrgContainerActivity.newInstance(mActivity,
//                            CheckCountFragment.class.getName(),
//                            null);

                if (!Method.isActivityFinished(mActivity) && mActivity != null && mActivity.hasWindowFocus()) {
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
                if (payInfo != null && payInfo.qqWxGroup != null) {
                    CustomAddGroupDialog.showCustomDialog(payInfo.qqWxGroup);
                }
//                UIJumpHelper.startActivity(getContext(), CustomAddGroupDialog.class);
            }
        }, 1000);
    }

    /**
     * 支付结果Dialog，成功/失败
     */
    private void createPayResultDlg() {
        if(isFragmentDetached()) return;
        if (payResultDialog == null) {
            payResultDialog = new PayResultDialog(mActivity, 0);
        }
        payResultDialog.setCancelable(false);
        payResultDialog.setCanceledOnTouchOutside(false);
        payResultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (payResult == PayUtils.PAY_RESULT_SUCC) {
                    paySuccess();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            onLoadCharge();
        }
    }

    @Override
    protected void onLoadData() {
//        if (payType != 1 || xxbRemainder > 0)
//            return;
//        if (!NetUtil.isConnected()) {
//            ToastUtils.showShort("网络未连接，请检查您的网络");
//            return;
//        }

    }

    private boolean isFragmentDetached(){
        return  Method.isActivityFinished(getActivity())||(mActivity==null)||this.isDetached();
    }

    private void onLoadCharge(){
        if(isFragmentDetached()) return;
        mActivity.showProgress();
        ServiceProvider.getXxbRemainder(getCompositeSubscription(), new NetResponse() {
            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                xxbRemainder = ((XxbRemainderBean) model.data).gold;
                setXxbRemainderState();
            }
        });
    }

    private void setXxbRemainderState() {
        if (payType != 1 || essayPayType != 2 || xxbRemainder == -1) {
            tvCurrentValue.setVisibility(View.GONE);
            btnCharge.setVisibility(View.GONE);
            tvRemainderError.setVisibility(View.GONE);
        } else {
            tvCurrentValue.setVisibility(View.VISIBLE);
            tvCurrentValue.setText(xxbRemainder + "图币");
            tvRemainderError.setVisibility(View.VISIBLE);
            if (xxbRemainder >= (Method.parseInt(payNumber) * xxbConverter)) {
                btnCharge.setVisibility(View.GONE);
                tvRemainderError.setText("图币充足");
                tvCurrentValue.setTextColor(Color.parseColor("#999999"));
                tvRemainderError.setTextColor(Color.parseColor("#999999"));
            } else {
                btnCharge.setVisibility(View.VISIBLE);
                tvRemainderError.setText("图币不足");
                tvCurrentValue.setTextColor(Color.parseColor("#FF3F47"));
                tvRemainderError.setTextColor(Color.parseColor("#FF3F47"));
            }
        }
    }

    public int getPayType() {
        return payType;
    }

    public boolean isCanNotPayTubi(){
        return payType == 1 && (xxbRemainder < (Method.parseInt(payNumber) * xxbConverter));
    }

    /**
     * 支付方式选项
     * 支付宝：32
     * 微信：33
     * 图币：1
     */


    @OnClick(R.id.confirm_payment_zfb_layout)
    public void onClickZfb() {
        payType = 32;
        essayPayType = 0;
        ivZfb.setImageResource(R.drawable.icon_checked);
        ivWeChat.setImageResource(R.mipmap.icon_pay_check_normal);
        ivXxb.setImageResource(R.mipmap.icon_pay_check_normal);
        tvXXbName.setSelected(false);
        tvWxName.setSelected(false);
        tvzfbName.setSelected(true);
        setXxbRemainderState();
    }

    @OnClick(R.id.confirm_payment_we_chat_layout)
    public void onClickWeChat() {
        payType = 33;
        essayPayType = 1;
        ivWeChat.setImageResource(R.drawable.icon_checked);
        ivZfb.setImageResource(R.mipmap.icon_pay_check_normal);
        ivXxb.setImageResource(R.mipmap.icon_pay_check_normal);
        tvXXbName.setSelected(false);
        tvWxName.setSelected(true);
        tvzfbName.setSelected(false);
        setXxbRemainderState();
    }

    @OnClick(R.id.confirm_payment_xxb_layout)
    public void onClickXxb() {
        payType = 1;
        essayPayType = 2;
        ivXxb.setImageResource(R.drawable.icon_checked);
        ivZfb.setImageResource(R.mipmap.icon_pay_check_normal);
        ivWeChat.setImageResource(R.mipmap.icon_pay_check_normal);
        tvXXbName.setSelected(true);
        tvWxName.setSelected(false);
        tvzfbName.setSelected(false);
        if (xxbRemainder == -1) {
            onLoadData();
        } else {
            setXxbRemainderState();
        }
    }

    /**
     * 账户充值
     */
    @OnClick(R.id.confirm_payment_xxb_charge_tv)
    public void onClickXxbCharge() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请检查您的网络");
            return;
        }
        RechargeNowActivity.newInstance(mActivity, 1001);
    }

    /**
     * 点击支付
     */
    public void onClickConfirm() {
        if (payType < 0) {
            ToastUtils.showShort("请选择支付方式");
            return;
        }
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请检查您的网络");
            return;
        }
        if (payType == 1 && xxbRemainder < (Method.parseInt(payNumber) * xxbConverter)) {
            ToastUtils.showShort("图币余额不足，请充值");
            return;
        }

        if (isSecKill) {
            payOrderSecKill();
        } else {
            if (isEssayOrder) {
                payEssayOrder();            // 申论支付
            } else {
                payOrder();                 // 订单支付
            }
        }
    }

    /**
     * 申论支付
     */
    private void payEssayOrder() {
        CheckGoodOrderBean mvar = new CheckGoodOrderBean();
        float f = Float.parseFloat(payNumber);
        mvar.total = f * 100;
        mvar.payType = essayPayType;
        mvar.orderId = orderId;
        LogUtils.d(TAG, mvar.toString());
        if (mActivity != null) {
            mActivity.showProgress();

        }
        ServiceProvider.createCheckOrder(getCompositeSubscription(), mvar, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                ToastUtils.showEssayToast("支付失败");
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
                if (model != null && model.data != null) {
                    Log.d(TAG, "model.data:" + model.data);
                    EssayPayInfo var = (EssayPayInfo) model.data;
                    if (Method.isActivityFinished(mActivity)) {
                        return;
                    }
                    if (essayPayType == 2) {
                        ToastUtils.showEssayToast("支付成功!");
                        payEssaySuccess();
                    } else if (essayPayType == 0) {
                        if (mPayUtils != null) {
                            mPayUtils.payZFBV2(mActivity, var.orderStr);
                        }
                    } else if (essayPayType == 1) {
                        PayInfo var2 = new PayInfo();
                        var2.appid = var.appId;
                        var2.noncestr = var.nonceStr;
                        var2.packageValue = var.packageValue;
                        var2.partnerid = var.partnerId;
                        var2.prepay_id = var.prepayId;
                        var2.sign = var.sign;
                        var2.timestamp = var.timestamp;
                        if (mPayUtils != null) {
                            mPayUtils.payWX(api, var2, mActivity);
                        }
                    }
                } else {
                    if (essayPayType == 2) {
                        ToastUtils.showShort("支付成功");
                        payEssaySuccess();
                    }
                }
            }
        });
    }

    /**
     * 课程支付
     */
    private void payOrder() {
        if (payType == 1) {                                 // 图币支付
            PayResultEvent event = new PayResultEvent();
            event.type = PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK;
            event.params = PayUtils.PAY_RESULT_SUCC;
            EventBus.getDefault().post(event);
        } else if (payType == 32) {
            payByZfb(payInfoFromNet);                       // 支付宝支付
        } else if (payType == 33) {
            payByWeChat(payInfoFromNet);                    // 微信支付
        }
    }

    /**
     * 秒杀支付
     */
    private void payOrderSecKill() {
        mActivity.showProgress();
        ServiceProvider.payOrderSecKill(getCompositeSubscription(), payInfo.OrderNum, payType, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                payInfoFromNet = (PayInfo) model.data;
                if (payInfoFromNet != null && payInfoFromNet.redEnvelopeId > 0) {
                    getRedBagInfo();
                }
                if (payType == 1) {
                    PayResultEvent event = new PayResultEvent();
                    event.type = PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK;
                    event.params = PayUtils.PAY_RESULT_SUCC;
                    EventBus.getDefault().post(event);
                    ToastUtils.showShort("支付成功");
                } else if (payType == 32) {
                    payByZfb(payInfoFromNet);
                } else if (payType == 33) {
                    payByWeChat(payInfoFromNet);
                }
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                if(e!= null && e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    ToastUtils.showEssayToast(apiException.getErrorMsg());
                }
            }
        });
    }

    /**
     * 支付宝支付
     */
    private void payByZfb(final PayInfo zfbPayInfo) {
        if (zfbPayInfo == null) {
            return;
        }
        if (!PayUtils.checkAliPayInstalled(mActivity)) {
            CommonUtils.showToast("没有安装支付宝");
        }
        // 订单
//        final String orderInfo = getZfbOrderInfo(zfbPayInfo.title, zfbPayInfo.notify_url, zfbPayInfo.description,
//                zfbPayInfo.MoneySum, zfbPayInfo.orderNum);
//        Log.v("order", orderInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
//                String singStr = "";
//                try {
//                    // 对订单做RSA 签名
//                    JsonObject payerReg = new JsonObject();
//                    payerReg.addProperty("orderInfo", orderInfo);
//
//                    Response<BaseResponseModel<String>> sign = CourseApiService.getApi().getAliPaySign(0, payerReg).execute();
//                    if (sign.isSuccessful()) {
//                        singStr = sign.body().data;
//                    }
//                } catch (IOException e) {
//                    //onFailure(call, e);
//                }
//                try {
//                    // 仅需对sign 做URL编码
//                    singStr = URLEncoder.encode(singStr, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//                // 完整的符合支付宝参数规范的订单信息
//                final String payInfo = orderInfo + "&sign=\"" + singStr + "\"&" + getSignType();
//                Log.v("payInfo", payInfo);
//
//                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(zfbPayInfo.aliSign, true);
                Log.d(TAG, "run: " + result);
                dealZfb(result);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝支付成功
     */
    private void dealZfb(final String result) {
        Method.runOnUiThread(mActivity, new Runnable() {
            @Override
            public void run() {
                PayResult zfbPayResult = new PayResult(result);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = zfbPayResult.getResult();
                String resultStatus = zfbPayResult.getResultStatus();
                LogUtils.i("resultStatus:" + resultStatus + ", resultInfo:" + resultInfo);
                if (Method.isActivityFinished(mActivity)) {
                    return;
                }
                PayResultEvent event = new PayResultEvent();
                event.type = PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK;
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtils.showShort("支付成功");
                    event.params = PayUtils.PAY_RESULT_SUCC;
                } else if (TextUtils.equals(resultStatus, "8000")) {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                    // 最终交易是否成功以服务端异步通知为准（小概率状态）
                    event.params = PayUtils.PAY_RESULT_UNUSUAL;
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    event.params = PayUtils.PAY_RESULT_FAIL;
                }
                EventBus.getDefault().post(event);
            }
        });
    }

//    /**
//     * get the sign type we use. 获取签名方式
//     */
//    public String getSignType() {
//        return "sign_type=\"RSA\"";
//    }
//
//    /**
//     * sign the order info. 对订单信息进行签名
//     *
//     * @param content 待签名订单信息
//     */
// /*   public String sign(String content) {
//        return SignUtils.sign(content, PayUtils.RSA_PRIVATE);
//    }*/
//
//    /**
//     * create the order info. 创建订单信息
//     */
//    public String getZfbOrderInfo(String subject, String notifyUrl, String body, String price,
//                                  String orderNum) {
//
//        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + PayUtils.PARTNER + "\"";
//
//        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + PayUtils.HTWX_SELLER + "\"";
//
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";
//
//        // 商品名称
//        orderInfo += "&subject=" + "\"" + subject + "\"";
//
//        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";
//
//        // 商品金额
//        orderInfo += "&total_fee=" + "\"" + price + "\"";
//
//        // 服务器异步通知页面路径
//        String noUrl = notifyUrl;
//        if (TextUtils.isEmpty(noUrl)) {
//            if (isSecKill) {
//                noUrl = PayUtils.ZFB_RESPONSE_URL_SECKILL;
//            } else {
//                noUrl = PayUtils.ZFB_RESPONSE_URL_COURSE;
//            }
//        }
//        orderInfo += "&notify_url=" + "\"" + noUrl + "\"";
//
//        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
//
//        // 参数编码， 固定值
//        orderInfo += "&_input_charset=\"utf-8\"";
//
//        // 设置未付款交易的超时时间
//        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//        // 取值范围：1m～15d。
//        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//        orderInfo += "&it_b_pay=\"30m\"";
//
//        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
//        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
//
//        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        // orderInfo += "&return_url=\"m.alipay.com\"";
//
//        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//        // orderInfo += "&paymethod=\"expressGateway\"";
//
//        return orderInfo;
//    }

    /**
     * 微信支付
     */
    private void payByWeChat(PayInfo weChatPayInfo) {
        if (weChatPayInfo == null) {
            return;
        }
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(UniApplicationContext.getContext(), PayUtils.APP_ID);
            wxApi.registerApp(PayUtils.APP_ID);
        }
        if (wxApi == null || !isWXAppInstalledAndSupported()) {
            ToastUtils.showShort("启动微信支付失败");
            return;
        }
        PayReq req = new PayReq();
        req.appId = weChatPayInfo.appid;
        req.partnerId = weChatPayInfo.partnerid;
        req.prepayId = weChatPayInfo.prepay_id;
        req.nonceStr = weChatPayInfo.noncestr;
        req.timeStamp = weChatPayInfo.timestamp;
        req.packageValue = weChatPayInfo.packageValue;
        req.sign = weChatPayInfo.sign;
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        wxApi.sendReq(req);
    }

    private boolean isWXAppInstalledAndSupported() {
        boolean sIsWXAppInstalledAndSupported = wxApi.isWXAppInstalled();//&& wxApi.isWXAppSupportAPI()
        return sIsWXAppInstalledAndSupported;
    }

    /**
     * 支付成功，关闭本页面，打开我的课程页面
     */
    private void paySuccess() {
        //MyPurchasedFragment.newInstance(0);
        // UIJumpHelper.jumpFragment(getContext(), MySingleTypeCourseFragment.class);

        if(isFragmentDetached()) return;
         UIJumpHelper.startStudyPage(getContext());
            EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.COURSE_BUY_SUCCESS));
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
            if (payInfo != null && payInfo.qqWxGroup != null)
                TimeUtils.delayTask(new Runnable() {
                    @Override
                    public void run() {
                        CustomAddGroupDialog.showCustomDialog(payInfo.qqWxGroup);
//                UIJumpHelper.startActivity(getContext(), CustomAddGroupDialog.class);
                    }
                }, 500);

    }

    /**
     * 咨询客服
     */
    private void onCallPhone() {
        if(isFragmentDetached()) return;
        if (dialDlg == null) {
            dialDlg = DialogUtils.createDialog(mActivity,
                    SpUtils.getAboutPhone(), "服务时间: 08:30-21:30");
            dialDlg.setPositiveButton("联系客服", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(SpUtils.getAboutPhone()));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        dialDlg.show();
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser) {
            LogUtils.v(TAG, "------可见----");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } else {
            LogUtils.v(TAG, "------不可见----");
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
        if (payResultDialog != null) {
            payResultDialog.dismiss();
            payResultDialog = null;
        }
        if (dialDlg != null) {
            dialDlg.dismiss();
            dialDlg = null;
        }
    }

    // 红包Window
    private PopupWindow redBagPop;

    private boolean isClickOpen = false;

    // 红包相关
    public void showRedBag() {
        if(isFragmentDetached()) return;
        PopWindowUtil.showPopInCenter(mActivity, ivZfb, 0, 0, R.layout.layout_redbag_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {

                    boolean isGoClassList = true;                       // 点

                    ValueAnimator animator;

                    Subscription timeSubscription;

                    long time24 = 24 * 60 * 60 * 1000;

                    @Override
                    public void popViewCall(final View contentView, final PopupWindow popWindow) {

                        redBagPop = popWindow;

                        final RelativeLayout rlRedBag = (RelativeLayout) contentView.findViewById(R.id.rl_redbag);  // 红包
                        final ImageView ivBtn = (ImageView) contentView.findViewById(R.id.iv_open);                 // 开红包
                        ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);                    // 关闭
                        final TextView tvTime = (TextView) contentView.findViewById(R.id.tv_time);                  // 剩余时间

                        // 按钮的动画
                        animator = ValueAnimator.ofFloat(1f, 1.15f, 1f);
                        animator.setInterpolator(new LinearInterpolator());                         // 设置线性差值器
                        animator.setRepeatCount(ValueAnimator.INFINITE);                            // 设置重复
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {     // 设置持续事件
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                ivBtn.setScaleX(values);
                                ivBtn.setScaleY(values);
                            }
                        });
                        animator.setDuration(1000);
                        animator.start();

                        // 入场动画
                        final ValueAnimator animatorIn = ValueAnimator.ofFloat(0.2f, 1f);
                        animatorIn.setInterpolator(new BounceInterpolator());                         // 设置差值器
                        animatorIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {     // 设置持续事件
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                rlRedBag.setScaleX(values);
                                rlRedBag.setScaleY(values);
                            }
                        });
                        animatorIn.setDuration(1200);
                        animatorIn.start();

                        // 倒计时
                        timeSubscription = Observable.interval(10, TimeUnit.MILLISECONDS)
                                .onBackpressureDrop()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Long>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        time24 -= 10;
                                        String formatData = DateUtils.millToTime(time24);
                                        tvTime.setText(formatData + " 后过期");
                                    }
                                });

                        ivBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isGoClassList = false;
                                if (redBagInfo != null) {
                                    openRedBag(redBagInfo);
                                } else {
                                    isClickOpen = true;
                                    mActivity.showProgress();
                                    getRedBagInfo();
                                }
                            }
                        });

                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popWindow.dismiss();
                            }
                        });
                    }

                    @Override
                    public void popViewDismiss() {
                        if (animator != null) {
                            animator.cancel();
                        }
                        if (timeSubscription != null && !timeSubscription.isUnsubscribed()) {
                            timeSubscription.unsubscribe();
                            timeSubscription = null;
                        }
                        if (isGoClassList) {
                            paySuccess();                   // 如果按返回/点关闭按钮，就跳课程
                        }
                    }
                });
    }

    /**
     * 点开之后获取红包数据
     */
    private void getRedBagInfo() {
        ServiceProvider.getRedBagInfo(getCompositeSubscription(), payInfoFromNet.redEnvelopeId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data instanceof RedBagInfo) {
                    redBagInfo = (RedBagInfo) model.data;
                    if (isClickOpen) {
                        mActivity.hideProgress();
                        openRedBag(redBagInfo);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });
    }

    // 红包Window
    private PopupWindow redBagOpenPop;

    private boolean isFinishThis = true;

    private void openRedBag(final RedBagInfo redBagInfo) {
        PopWindowUtil.showPopInCenter(mActivity, ivZfb, 0, 0, R.layout.layout_redbag_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {

                    ValueAnimator animator;

                    Subscription timeSubscription;

                    @Override
                    public void popViewCall(final View contentView, final PopupWindow popWindow) {

                        if (redBagOpenPop != null) {
                            isFinishThis = false;
                            redBagOpenPop.dismiss();
                            isFinishThis = true;
                        }

                        redBagOpenPop = popWindow;

                        final RelativeLayout rlRedBag = (RelativeLayout) contentView.findViewById(R.id.rl_redbag);  // 红包布局

                        final RelativeLayout rlBag = (RelativeLayout) contentView.findViewById(R.id.rl_bag);        // 包布局
                        final ImageView ivFlower = (ImageView) contentView.findViewById(R.id.iv_flower);            // 花花

                        TextView tvCongratulation = (TextView) contentView.findViewById(R.id.tv_congratulation);    // 恭喜
                        TextView tvMoney = (TextView) contentView.findViewById(R.id.tv_money);                      // 红包金额
                        TextView tvLastMoney = (TextView) contentView.findViewById(R.id.tv_last_money);             // 红包最小金额
                        final TextView tvTime = (TextView) contentView.findViewById(R.id.tv_time);                  // 倒计时

                        final ImageView ivSend = (ImageView) contentView.findViewById(R.id.iv_send);                // 去发红包

                        ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);                    // 关闭按钮

                        ImageView ivRule = (ImageView) contentView.findViewById(R.id.iv_rule);                        // 规则按钮

                        final RelativeLayout rlRule = (RelativeLayout) contentView.findViewById(R.id.rl_rule);      // 规则布局
                        TextView tvRuleContent = (TextView) contentView.findViewById(R.id.tv_rule_content);         // 规则内容
                        ImageView ivOk = (ImageView) contentView.findViewById(R.id.iv_ok);                          // 我知道了

                        Typeface fontStyle = Typeface.createFromAsset(getActivity().getAssets(), "font/851-CAI978.ttf");
                        tvMoney.setTypeface(fontStyle);

                        // 设置数据
                        tvCongratulation.setText(Html.fromHtml("恭喜！<br/>获得<u>" + redBagInfo.aloneNum + "</u>个好友助学现金红包"));
                        tvMoney.setText(String.valueOf(redBagInfo.aloneByPrice).replace(".0", ""));
                        tvLastMoney.setText("每人最少获得" + String.valueOf(redBagInfo.aloneMiniPrice).replace(".0", "") + "元");

                        redBagInfo.endTime *= 1000;

                        // 倒计时
                        timeSubscription = Observable.interval(10, TimeUnit.MILLISECONDS)
                                .onBackpressureDrop()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Long>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        redBagInfo.endTime -= 10;
                                        String formatData = DateUtils.millToTime(redBagInfo.endTime);
                                        tvTime.setText(formatData + " 后过期");
                                    }
                                });

                        tvRuleContent.setText(Html.fromHtml(redBagInfo.instruction));

                        // 设置视距
                        setCameraDistance(rlRedBag);
                        setCameraDistance(rlRule);

                        // 显示规则的翻转动画
                        final ObjectAnimator inA = ObjectAnimator.ofFloat(rlRule, "rotationY", 90, 0);
                        inA.setDuration(300);
                        inA.setInterpolator(new LinearInterpolator());

                        final ObjectAnimator outA = ObjectAnimator.ofFloat(rlRedBag, "rotationY", 0, -90);
                        outA.setDuration(300);
                        outA.setInterpolator(new LinearInterpolator());
                        outA.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                inA.start();
                                rlRedBag.setVisibility(View.GONE);
                                rlRule.setVisibility(View.VISIBLE);
                            }
                        });

                        // 隐藏规则的翻转动画
                        final ObjectAnimator inB = ObjectAnimator.ofFloat(rlRedBag, "rotationY", -90, 0);
                        inB.setDuration(300);
                        inB.setInterpolator(new LinearInterpolator());

                        final ObjectAnimator outB = ObjectAnimator.ofFloat(rlRule, "rotationY", 0, 90);
                        outB.setDuration(300);
                        outB.setInterpolator(new LinearInterpolator());
                        outB.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                inB.start();
                                rlRedBag.setVisibility(View.VISIBLE);
                                rlRule.setVisibility(View.GONE);
                            }
                        });

                        // 红包出现的动画
                        final ValueAnimator animatorBagIn = ValueAnimator.ofFloat(0.2f, 1f);
                        animatorBagIn.setInterpolator(new AnticipateOvershootInterpolator());
                        animatorBagIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                rlBag.setScaleX(values);
                                rlBag.setScaleY(values);
                            }
                        });
                        animatorBagIn.setDuration(900);
                        animatorBagIn.start();

                        // 花花出现的动画
                        final ValueAnimator animatorFlowerIn = ValueAnimator.ofFloat(0.2f, 1f);
                        animatorFlowerIn.setInterpolator(new DecelerateInterpolator());
                        animatorFlowerIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                ivFlower.setScaleX(values);
                                ivFlower.setScaleY(values);
                            }
                        });
                        animatorFlowerIn.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                ivFlower.setVisibility(View.VISIBLE);
                            }
                        });
                        animatorFlowerIn.setDuration(400);
                        animatorFlowerIn.setStartDelay(700);
                        animatorFlowerIn.start();

                        // 按钮的动画
                        animator = ValueAnimator.ofFloat(1f, 1.15f, 1f);
                        animator.setInterpolator(new LinearInterpolator());                         // 设置线性差值器
                        animator.setRepeatCount(ValueAnimator.INFINITE);                            // 设置重复
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {     // 设置持续事件
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                ivSend.setScaleX(values);
                                ivSend.setScaleY(values);
                            }
                        });
                        animator.setDuration(1000);
                        animator.start();

                        // 分享
                        ivSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareInfo(redBagInfo);
                            }
                        });

                        // 看规则
                        ivRule.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                outA.start();
                            }
                        });

                        // 关闭
                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popWindow.dismiss();
                            }
                        });

                        // 规则知道了
                        ivOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                outB.start();
                            }
                        });

                        // 让开之前的红包消失
                        if (redBagPop != null) {
                            redBagPop.dismiss();
                        }
                    }

                    @Override
                    public void popViewDismiss() {
                        if (animator != null) {
                            animator.cancel();
                        }
                        if (timeSubscription != null && !timeSubscription.isUnsubscribed()) {
                            timeSubscription.unsubscribe();
                            timeSubscription = null;
                        }
                        if (payResultDialog != null) {
                            payResultDialog.dismiss();
                        }
                        if (isFinishThis) {
                            paySuccess();
                        }
                    }
                });
    }

    /**
     * 获取分享信息，并分享
     */
    private void getShareInfo(RedBagInfo redBagInfo) {
        mActivity.showProgress();
        ServiceProvider.getRedBagShareInfo(getCompositeSubscription(), String.valueOf(redBagInfo.aloneByPrice), redBagInfo.redEnvelopeId, redBagInfo.param, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                if (model.data != null && model.data instanceof RedBagShareInfo) {
                    RedBagShareInfo redBagShareInfo = (RedBagShareInfo) model.data;
                    ShareUtil.test(getActivity(), redBagShareInfo.id, redBagShareInfo.desc, redBagShareInfo.title, redBagShareInfo.url, null, R.mipmap.redbag_share_img, null, null);
                } else {
                    ToastUtils.showShort(model.message);
                }
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                ToastUtils.showShort("获取分享信息失败，请重试");
            }
        });
    }

    // 改变视角距离, 贴近屏幕
    private void setCameraDistance(View v) {
        int distance = 6000;
        float scale = getResources().getDisplayMetrics().density * distance;
        v.setCameraDistance(scale);
    }

}
