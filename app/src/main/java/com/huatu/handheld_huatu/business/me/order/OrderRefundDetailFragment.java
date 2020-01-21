package com.huatu.handheld_huatu.business.me.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
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

/**
 * Created by saiyuan on 2017/10/1.
 */

public class OrderRefundDetailFragment extends BaseFragment {
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
    @BindView(R.id.refund_detail_status_tv)
    TextView tvStatus;
    @BindView(R.id.refund_detail_price_tv)
    TextView tvPrice;
    @BindView(R.id.refund_detail_time_tv)
    TextView tvTime;
    @BindView(R.id.refund_detail_course_tv)
    TextView tvCourse;
    @BindView(R.id.refund_detail_remark_tv)
    TextView tvRemark;
    @BindView(R.id.refund_detail_reason_tv)
    TextView tvReason;

    private String orderNumber;
    private String orderMoney;
    private OrderRefundResp orderRefundResp;
    private boolean isDetail = true;
    private PopupWindow popupWindow;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_order_refund_detail_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        orderRefundResp = (OrderRefundResp) args.getSerializable("refund_detail");
        orderMoney = args.getString("order_money");
        orderNumber = args.getString("order_number");
        if(orderRefundResp != null) {
            isDetail = false;
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        tvOrderNumber.setText("订单号码：" + orderNumber);
//        String mOrderMoney=df.format(orderMoney);
        if (orderMoney!= null) {
        tvOrderMoney.setText("退款金额：￥" + orderMoney);
        }
        initTitleBar();
        refreshUI();
    }

    private void initTitleBar() {
        if(isDetail) {
            topActionBar.setTitle("退款详情");
        } else {
            topActionBar.setTitle("申请成功");
        }
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    protected void onLoadData() {
        if(!isDetail) {
            return;
        }
        mActivity.showProgress();
        ServiceProvider.getRefundDetail(compositeSubscription, orderNumber, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                orderRefundResp = (OrderRefundResp) model.data;
                refreshUI();
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
            }
        });
    }

    private void refreshUI() {
        if(orderRefundResp == null) {
            return;
        }
//        imgApply.setImageResource(R.drawable.order_refund_complete);
//        imgProcessing.setImageResource(R.drawable.order_refund_complete);
//        imgProcessingDivider.setBackgroundResource(R.color.main_color);
//        imgComplete.setImageResource(R.drawable.order_refund_complete);
//        imgCompleteDivider.setBackgroundResource(R.color.main_color);
//        tvApply.setTextColor(getResources().getColor(R.color.main_color));
//        tvProcessing.setTextColor(getResources().getColor(R.color.gray_333333));
//        tvComplete.setTextColor(getResources().getColor(R.color.gray_333333));
        tvStatus.setText("已退款");
        String mTotal=df.format(orderRefundResp.total);
        tvPrice.setText("￥" + mTotal);
        tvTime.setText("申请时间：" + orderRefundResp.AddTime);
        tvCourse.setText("退款课程：" + orderRefundResp.Title);
        tvRemark.setText("退款去向：我们将按照您的支付方式原路返回，退款将于7个工作日内退回，请及时查收" );
        tvReason.setText("退款原因：" + orderRefundResp.Remark);
        if(!isDetail) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRefundSuccessDlg();
                    hideRefundSuccessDlg();
                }
            });
        }
    }

    private void hideRefundSuccessDlg() {
            tvStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        popupWindow.dismiss();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },3000);
    }

    private void showRefundSuccessDlg() {
        ToastUtils.showShort("退款成功");
        if(popupWindow == null) {
            popupWindow = new PopupWindow(UniApplicationContext.getContext());
            View view = mLayoutInflater.inflate(R.layout.order_refund_success_pop_layout, null);
            popupWindow.setContentView(view);
            popupWindow.setOutsideTouchable(false);
        }
        if(!popupWindow.isShowing()) {
            tvStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },200);
        }
    }

    @Override
    public boolean onBackPressed() {
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        }
        if(!isDetail) {
            setResultForTargetFrg(Activity.RESULT_OK);
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public static void newInstance(Activity act, String orderNumber, String money, OrderRefundResp orderRefundResp) {
        Bundle args = new Bundle();
        args.putSerializable("order_number", orderNumber);
        args.putString("order_money", money);
        args.putSerializable("refund_detail", orderRefundResp);
        BaseFrgContainerActivity.newInstance(act, OrderRefundDetailFragment.class.getName(), args);
    }
}
