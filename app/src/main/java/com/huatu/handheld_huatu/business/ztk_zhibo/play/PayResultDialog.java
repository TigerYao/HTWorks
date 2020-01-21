package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.PayResultEvent;
import com.huatu.handheld_huatu.utils.PayUtils;

import org.greenrobot.eventbus.EventBus;

public class PayResultDialog extends Dialog {
    private LayoutInflater mInflater;
    public View rootView;
    ImageView btnClose;
    ImageView ivResult;
    TextView tvInfo;
    TextView tvTips;

    private int mType;
    private int timeStick = 3;

    public PayResultDialog(Context context, int type) {
        super(context, R.style.CustomProgressDialog);
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater) UniApplicationContext.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = mInflater.inflate(R.layout.dialog_payresult, null);
        setContentView(rootView);
        btnClose = (ImageView) rootView.findViewById(R.id.pay_result_close_btn);
        ivResult = (ImageView) rootView.findViewById(R.id.pay_result_face_img);
        tvInfo = (TextView) rootView.findViewById(R.id.pay_result_info_tv);
        tvTips = (TextView) rootView.findViewById(R.id.pay_result_tips_tv);
        setCanceledOnTouchOutside(false);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == PayUtils.PAY_RESULT_UNUSUAL) {
                    PayResultEvent event = new PayResultEvent();
                    event.type = PayResultEvent.PAY_RESULT_EVENT_SHOW_CUSTOMER;
                    EventBus.getDefault().post(event);
                    dismiss();
                } else if (mType == PayUtils.PAY_RESULT_SUCC) {
                    dismiss();
                }
            }
        });
    }

    /*
    should called after show()
     */
    public void setResultType(int type) {
        mType = type;
        setViewState();
    }

    private void setViewState() {
        if (mType == PayUtils.PAY_RESULT_SUCC) {
            btnClose.setVisibility(View.GONE);
            setCancelable(false);
            ivResult.setImageResource(R.drawable.icon_pay_sucess);
            tvInfo.setText("支付成功");
                timeStick = 3;
                tvTips.setText(timeStick + "秒后进入学习");
                startTimeStick();
        } else if (mType == PayUtils.PAY_RESULT_FAIL) {
            btnClose.setVisibility(View.VISIBLE);
            setCancelable(true);
            ivResult.setImageResource(R.drawable.pay_result_cry);
            tvInfo.setText("支付失败");
            tvTips.setText("请选择其他支付方式");
        } else {
            btnClose.setVisibility(View.VISIBLE);
            setCancelable(false);
            ivResult.setImageResource(R.drawable.icon_pay_warn);
            tvInfo.setText("支付异常");
            tvTips.setText("呼叫客服");
        }
    }

    private void startTimeStick() {
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                --timeStick;
                if (timeStick > 0) {
                    tvTips.setText(timeStick + "秒后进入学习");
                    startTimeStick();
                } else {
                    tvTips.setText("即将开始跳转");
                    dismiss();
                }
            }
        }, 1000);
    }
}
