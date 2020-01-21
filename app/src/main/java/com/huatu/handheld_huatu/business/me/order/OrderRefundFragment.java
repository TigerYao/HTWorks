package com.huatu.handheld_huatu.business.me.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/9/30.
 */

public class OrderRefundFragment extends BaseFragment {
    @BindView(R.id.order_refund_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.order_refund_order_number_tv)
    TextView tvOrderNumber;
    @BindView(R.id.order_refund_order_price_tv)
    TextView tvOrderMoney;
//    @BindView(R.id.order_refund_apply_img)
//    ImageView imgApply;
//    @BindView(R.id.order_refund_apply_tv)
//    TextView tvApply;
//    @BindView(R.id.order_refund_processing_img)
//    ImageView imgProcessing;
//    @BindView(R.id.order_refund_processing_divider)
//    View imgProcessingDivider;
//    @BindView(R.id.order_refund_processing_tv)
//    TextView tvProcessing;
//    @BindView(R.id.order_refund_complete_img)
//    ImageView imgComplete;
//    @BindView(R.id.order_refund_complete_divider)
//    View imgCompleteDivider;
//    @BindView(R.id.order_refund_complete_tv)
//    TextView tvComplete;
    @BindView(R.id.order_refund_reason_edit)
    EditText etReason;
    @BindView(R.id.order_refund_reason_number_tv)
    TextView tvReasonNumber;
    @BindView(R.id.order_refund_confirm_btn)
    TextView btnConfirm;

    private String orderNumber;
    private String orderMoney;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_order_refund_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        orderNumber = args.getString("order_number");
        orderMoney = args.getString("order_money");
        tvOrderNumber.setText("订单号码：" + orderNumber);
//        String mOrderMoney=df.format(orderMoney);
        if (orderMoney!=null) {
        tvOrderMoney.setText("退款金额：￥" +orderMoney);
        }
//        imgApply.setImageResource(R.drawable.order_refund_complete);
//        imgProcessing.setImageResource(R.drawable.order_refund_normal);
//        imgProcessingDivider.setBackgroundResource(R.color.main_color);
//        imgComplete.setImageResource(R.drawable.order_refund_normal);
//        imgCompleteDivider.setBackgroundResource(R.color.main_color);
//        tvApply.setTextColor(getResources().getColor(R.color.main_color));
//        tvProcessing.setTextColor(getResources().getColor(R.color.gray_333333));
//        tvComplete.setTextColor(getResources().getColor(R.color.gray_333333));
        etReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                tvReasonNumber.setText(text.length() + "/200");
            }
        });
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @OnClick(R.id.order_refund_confirm_btn)
    public void onClickConfirm() {
        final String remark = etReason.getText().toString();
        if(TextUtils.isEmpty(remark)) {
            ToastUtils.showShort("请输入退款原因");
            return;
        }
        mActivity.showProgress();
        ServiceProvider.applyRefund(compositeSubscription, orderNumber, remark, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                OrderRefundResp resp = (OrderRefundResp) model.data;
                if(resp.status == 1) {
                    resp.reason = remark;
                    OrderRefundDetailFragment.newInstance(mActivity,
                            orderNumber, orderMoney, resp);
                } else {
                    ToastUtils.showShort("退款处理失败，请稍后再试");
                }
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            setResultForTargetFrg(Activity.RESULT_OK, null);
            mActivity.finish();
        }
    }

    public static void newInstance(Activity act, String order, String money) {
        Bundle args = new Bundle();
        args.putString("order_number", order);
        args.putString("order_money", money);
        BaseFrgContainerActivity.newInstance(act, OrderRefundFragment.class.getName(), args);
    }
}
