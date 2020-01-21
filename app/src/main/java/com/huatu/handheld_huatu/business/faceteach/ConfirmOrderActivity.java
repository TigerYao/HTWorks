package com.huatu.handheld_huatu.business.faceteach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceUserInformation;
import com.huatu.handheld_huatu.mvpmodel.BaseStringBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FacePlaceOrderBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class ConfirmOrderActivity extends BaseActivity {

    // 填写信息的的请求code
    public static int INFORMATION_REQUEST_CODE = 10017;
    public static int INFORMATION_RESULT_CODE = 10018;

    @BindView(R.id.tv_information)
    TextView tvInformation;         // 去填写信息按钮
    @BindView(R.id.tv_name)
    TextView tvName;                // 姓名
    @BindView(R.id.ll_address)
    LinearLayout llAddress;         // 地址布局
    @BindView(R.id.tv_address)
    TextView tvAddress;             // 地址

    @BindView(R.id.tv_price)
    TextView tvPrice;               // 商品价格
    @BindView(R.id.tv_reduce)
    TextView tvReduce;              // 减免价格
    @BindView(R.id.tv_live_price)
    TextView tvLivePrice;           // 住宿费
    @BindView(R.id.tv_total)
    TextView tvTotal;               // 最后价格

    @BindView(R.id.cb_protocol)
    CheckBox cbProtocol;            // 同意协议
    @BindView(R.id.tv_protocol)
    TextView tvProtocol;            // 去查看协议

    @BindView(R.id.tv_last_price)
    TextView tvLastPrice;           // 最后的价格

    private FaceUserInformation information;        // 用户信息

    private Bundle args;            // 前页面传过来的各种参数

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(ConfirmOrderActivity.this);

        args = originIntent.getBundleExtra("args");
        if (args == null) {
            args = new Bundle();
        }
        String price = args.getString("price", "0");
        String yh_price = args.getString("yh_price", "0");

        String zs_price = args.getString("zs_price", "0");
        tvLivePrice.setText(zs_price + "元");

        tvPrice.setText("¥" + price);
        if (!yh_price.equals("0") && !yh_price.equals(price)) {    // 有优惠价格
            try {
                // 计算出优惠价格
                Float yh = Float.valueOf(yh_price);
                Float p = Float.valueOf(price);
                tvReduce.setText("- " + yh);
                tvTotal.setText("¥" + LUtils.formatPoint(p - yh));
                tvLastPrice.setText("¥ " + LUtils.formatPoint(p - yh));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                tvTotal.setText("¥" + price);
                tvLastPrice.setText("¥ " + price);
            }
        } else {
            tvTotal.setText("¥" + price);
            tvLastPrice.setText("¥ " + price);
        }

        String inf = SpUtils.getFaceUserInformation();
        if (!StringUtils.isEmpty(inf)) {
            try {
                information = new Gson().fromJson(inf, FaceUserInformation.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (information != null) {
                showUserInformation();
            }
        }

    }

    @OnClick({R.id.iv_back, R.id.tv_name, R.id.ll_address, R.id.tv_information, R.id.tv_submit_order, R.id.tv_protocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_name:              // 去填写信息
            case R.id.ll_address:
            case R.id.tv_information:
                fillInformation();
                break;
            case R.id.tv_submit_order:      // 提交订单
                placeOrder();
                break;
            case R.id.tv_protocol:
                BaseWebViewFragment.lanuch(this, "https://xue.huatu.com/service/payAgre.html", "华图教育");
                break;
        }
    }

    /**
     * —mid	        是	string	用户ID
     * —aid	        是	string	产品ID
     * —zstype	    是	int	是否住宿 0-不住宿 1-住宿
     * <p>
     * —htwyid	    否	string	华图职位代码
     * <p>
     * —rname	    是	string	姓名
     * —cardid	    是	string	身份证号码
     * —tel	        是	string	手机号码
     * —email	    是	string	邮件地址
     * —sex	        是	int	性别，0-女 1-男
     * <p>
     * —code	    是	string	客户端代码，安卓-APK，苹果-IPA
     * —source	    否	string	订单平台来源 101-华图在线
     * —timestamp	是	int	时间戳
     * —sign	    是	string	签名
     */
    private void placeOrder() {
        // 各种检查
        if (information == null) {
            ToastUtils.showEssayToast("请填写个人信息");
            return;
        }
        if (StringUtils.isEmpty(information.rname)
                || StringUtils.isEmpty(information.cardid)
                || StringUtils.isEmpty(information.tel)
                || StringUtils.isEmpty(information.email)) {
            ToastUtils.showEssayToast("请完善个人信息");
            return;
        }
        if (!cbProtocol.isChecked()) {
            ToastUtils.showEssayToast("请阅读并勾选协议");
            return;
        }
        // 开始下单
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("无网络连接");
            return;
        }

        showProgress();
        String mid = getMid();
        if (mid.equals("err")) {        // 本地没有mid，需要去请求网络获取，获取成功后悔再调用此下单方法
            return;
        }

        String aid = args.getString("aid");
        String zstype = String.valueOf(args.getInt("zstype"));
        String htwyid = args.getString("htwyid");
        String rname = information.rname;
        String cardid = information.cardid;
        String tel = information.tel;
        String email = information.email;
        String sex = String.valueOf(information.sex);
        String code = "APK";
        String source = "101";
        String timestamp = System.currentTimeMillis() + "";

        String params;

        if (StringUtils.isEmpty(htwyid)) {

            String md5 = String.format("aid=%s&cardid=%s&code=%s&email=%s&mid=%s&rname=%s&sex=%s&source=%s&tel=%s&timestamp=%s&zstype=%s",
                    aid, cardid, code, email, mid, rname, sex, source, tel, timestamp, zstype);

            String sign = Md5Util.toSign(md5);

            params = String.format("{'aid':'%s','cardid':'%s','code':'%s','email':'%s','mid':'%s','rname':'%s','sex':'%s','source':'%s','tel':'%s','timestamp':'%s','zstype':'%s','sign':'%s'}",
                    aid, cardid, code, email, mid, rname, sex, source, tel, timestamp, zstype, sign).replace("'", "\"");

        } else {

            String md5 = String.format("aid=%s&cardid=%s&code=%s&email=%s&htwyid=%s&mid=%s&rname=%s&sex=%s&source=%s&tel=%s&timestamp=%s&zstype=%s",
                    aid, cardid, code, email, htwyid, mid, rname, sex, source, tel, timestamp, zstype);

            String sign = Md5Util.toSign(md5);

            params = String.format("{'aid':'%s','cardid':'%s','code':'%s','email':'%s','htwyid':'%s','mid':'%s','rname':'%s','sex':'%s','source':'%s','tel':'%s','timestamp':'%s','zstype':'%s','sign':'%s'}",
                    aid, cardid, code, email, htwyid, mid, rname, sex, source, tel, timestamp, zstype, sign).replace("'", "\"");

        }
        LogUtils.e("params", params);
        ServiceProvider.createFaceOrder(compositeSubscription, params, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("下单失败");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();
                FacePlaceOrderBean placeOrderBean = (FacePlaceOrderBean) model.data;
                if (placeOrderBean != null) {
                    FacePayWayActivity.startFacePayWayActivity(ConfirmOrderActivity.this,
                            placeOrderBean.oid,
                            placeOrderBean.oid,
                            placeOrderBean.title,
                            placeOrderBean.kcdd + " " + placeOrderBean.sksj,
                            placeOrderBean.priceCount,
                            placeOrderBean.priceCount,
                            placeOrderBean.alipay_account,
                            placeOrderBean.pid);
                    finish();
                } else {
                    ToastUtils.showEssayToast("下单失败");
                }
            }
        });
    }

    // 获取mid
    private String getMid() {
        if (TextUtils.isEmpty(UserInfoUtil.mId)) {
            String mobile = TextUtils.isEmpty(UserInfoUtil.ucId) ? SpUtils.getMobile() : UserInfoUtil.ucId;
            String curTime = System.currentTimeMillis() + "";
            String md5 = String.format("mobile=%s&timestamp=%s", mobile, curTime);

            String sign = Md5Util.toSign(md5);

            ServiceExProvider.visit(compositeSubscription, CourseApiService.getApi().getEducationMid(mobile, curTime, sign), new NetObjResponse<BaseStringBean>() {
                @Override
                public void onSuccess(BaseResponseModel<BaseStringBean> model) {
                    UserInfoUtil.mId = model.data.url;
                    placeOrder();
                }

                @Override
                public void onError(String message, int type) {
                    ToastUtils.showEssayToast("下单失败");
                    hideProgress();
                }
            });
            return "err";
        } else {
            return UserInfoUtil.mId;
        }
    }

    /**
     * 去填写信息页面
     */
    private void fillInformation() {
        Intent intent = new Intent(ConfirmOrderActivity.this, FillInformationActivity.class);
        if (information != null) {
            intent.putExtra("information", information);
        }
        startActivityForResult(intent, INFORMATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INFORMATION_REQUEST_CODE && resultCode == INFORMATION_RESULT_CODE) {
            information = (FaceUserInformation) data.getSerializableExtra("information");
            if (information != null) {
                showUserInformation();
            }
        }
    }

    private void showUserInformation() {
        tvName.setText(information.rname + " " + information.tel);
        tvAddress.setText(information.addSelect + " " + information.address);
        tvName.setVisibility(View.VISIBLE);
        llAddress.setVisibility(View.VISIBLE);
        tvInformation.setVisibility(View.GONE);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
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

    /**
     * 下单接口
     * <p>
     * 下单需要的字段放在args里
     * aid      商品aid
     * zstype   是否需要住宿
     * htwyid   （如果是职位保护，会先去选择职位，有了这个职位代码字段）
     * ssfb     如果是职位保护，这个字段不为空（去选择保护职位需要的）
     * <p>
     * price    商品价格
     * yh_price 优惠后的价格（优惠价格和需付款经过计算得出）
     */
    public static void launch(Context context, Bundle args) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtra("args", args);
        context.startActivity(intent);
    }
}
