package com.huatu.handheld_huatu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.PayResultEvent;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, PayUtils.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            PayResultEvent event = new PayResultEvent();
            event.type = PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK;
			if (resp.errCode == 0) {
                ToastUtils.showShort("支付成功");
				event.params = PayUtils.PAY_RESULT_SUCC;
			} else {
                LogUtils.i("微信支付失败：errCode: " + resp.errCode + ", errMsg:" + resp.errStr);
                event.params = PayUtils.PAY_RESULT_FAIL;
			}
            EventBus.getDefault().post(event);
            WXPayEntryActivity.this.finish();
		}
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}