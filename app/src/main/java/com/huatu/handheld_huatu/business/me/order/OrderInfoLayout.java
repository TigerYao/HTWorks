package com.huatu.handheld_huatu.business.me.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.OrderDetailData;

/**
 * Created by Administrator on 2019\11\11 0011.
 */

public class OrderInfoLayout extends LinearLayout {

    public OrderInfoLayout(Context context) {
        this(context, null);
    }

    public OrderInfoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    TextView mOrderNumText,mSubmitDateTxt;
    TextView mConsultBtn;
    LinearLayout     mAlreadyPayLayout;
    TextView mPayDateTxt,mPayWayTxt,mSendOutTxt;
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mOrderNumText = this.findViewById(R.id.tv_order_num);
        mSubmitDateTxt = this.findViewById(R.id.tv_submit_time);
        mConsultBtn=this.findViewById(R.id.tv_consult);

        mAlreadyPayLayout=(LinearLayout)this.findViewById(R.id.ll_already_pay);
        mPayDateTxt=(TextView) mAlreadyPayLayout.getChildAt(0);
        mPayWayTxt=(TextView) mAlreadyPayLayout.getChildAt(1);
        mSendOutTxt=(TextView) mAlreadyPayLayout.getChildAt(2);
    }

    public void bindUI(OrderDetailData OrderData){

        if (!TextUtils.isEmpty(OrderData.orderNum)) {
            mOrderNumText.setVisibility(View.VISIBLE);
            mOrderNumText.setText(OrderData.orderNum);
        } else {
            mOrderNumText.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(OrderData.addTime)) {
            mSubmitDateTxt.setText("下单时间：" + OrderData.addTime);
        } else {
            mSubmitDateTxt.setVisibility(View.GONE);

        }

        if (OrderData.payStatus == 1) {
            mAlreadyPayLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(OrderData.payTime)) {
                mPayDateTxt.setText("付款时间：" + OrderData.payTime);
            } else {
                mPayDateTxt.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(OrderData.paymentDesc)) {
                mPayWayTxt.setText("支付方式：" + OrderData.paymentDesc);
            } else {
                mPayWayTxt.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(OrderData.address)) {
                //有实物，需要发货
                if (OrderData.hasLogistics) {
                    if (!TextUtils.isEmpty(OrderData.logisticsTime)) {
                        mSendOutTxt.setText("发货时间：" + OrderData.logisticsTime);
                    }
                } else {
                    mSendOutTxt.setText("发货时间：待发货");
                }
              /*  if (!TextUtils.isEmpty(mData.logisticsCost)) {
                    //运费
                    rl_freight.setVisibility(View.VISIBLE);
                    tv_freight.setText("¥ " + mData.logisticsCost);
                } else {
                    rl_freight.setVisibility(View.GONE);
                }*/
            } else {
                //没有实物，不需要发货，不显示发货时间
                mSendOutTxt.setVisibility(View.GONE);
               // rl_freight.setVisibility(View.GONE);
//                if (!TextUtils.isEmpty(mData.payTime)){
//                    tv_send_time.setText("发货时间：" + mData.payTime);
//                }
            }
        } else {
            mAlreadyPayLayout.setVisibility(View.GONE);
        }

    }

}
