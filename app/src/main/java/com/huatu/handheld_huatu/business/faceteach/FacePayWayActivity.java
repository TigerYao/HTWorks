package com.huatu.handheld_huatu.business.faceteach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class FacePayWayActivity extends BaseActivity {
    @BindView(R.id.ll_split)
    View splitView;

    private PayUtils mPayUtils;
    private String oid;
    private String title;
    private String orderDes;
    private String price;
    private String alipay_account;
    private String pid;
    private String payOid;
    private String payPrice;
    private boolean isFromSplit;


    public static void startFacePayWayForResultActivity(Activity ctx, String oid, String payOid, String title, String orderDes, String price, String payPrice, String alipay_account, String pid) {
        Intent intent = new Intent(ctx, FacePayWayActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("price", price);
        intent.putExtra("title", title);
        intent.putExtra("orderDes", orderDes);
        intent.putExtra("alipay_account", alipay_account);
        intent.putExtra("pid", pid);
        intent.putExtra("payOid", payOid);
        intent.putExtra("payPrice", payPrice);
        ctx.startActivityForResult(intent,3002);
    }

    public static void startFacePayWayActivity(Context ctx, String oid, String payOid, String title, String orderDes, String price, String payPrice, String alipay_account, String pid) {
        Intent intent = new Intent(ctx, FacePayWayActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("price", price);
        intent.putExtra("title", title);
        intent.putExtra("orderDes", orderDes);
        intent.putExtra("alipay_account", alipay_account);
        intent.putExtra("pid", pid);
        intent.putExtra("payOid", payOid);
        intent.putExtra("payPrice", payPrice);
        ctx.startActivity(intent);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_face_pay_way;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(FacePayWayActivity.this);
        mPayUtils = new PayUtils();
        oid = getIntent().getStringExtra("oid");
        title = getIntent().getStringExtra("title");
        orderDes = getIntent().getStringExtra("orderDes");
        price = getIntent().getStringExtra("price");
        alipay_account = getIntent().getStringExtra("alipay_account");
        pid = getIntent().getStringExtra("pid");
        payOid = getIntent().getStringExtra("payOid");
        payPrice = getIntent().getStringExtra("payPrice");
        isFromSplit = getIntent().getBooleanExtra("isFromSplit", false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (Float.parseFloat(payPrice) > 5000) {
            splitView.setVisibility(View.VISIBLE);
        } else
            splitView.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_back, R.id.ll_zfb, R.id.ll_split})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                if(isFromSplit)
                SplitOrderActivity.startSplitOrderActivity(this, oid, title, orderDes, price, alipay_account, pid, false);
                finish();
                break;
            case R.id.ll_zfb:
                mPayUtils.payZFB(this, title, orderDes, payPrice, payOid, "https://bm.huatu.com/api/new_alipay2/notify_url.php", pid, alipay_account, false);
                break;
            case R.id.ll_split:
                SplitOrderActivity.startSplitOrderActivity(this, oid, title, orderDes, price, alipay_account, pid, true);
                finish();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (isFromSplit)
            SplitOrderActivity.startSplitOrderActivity(this, oid, title, orderDes, price, alipay_account, pid, false);
        finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(PayMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
            ToastUtils.makeText(this, "支付成功！");
            PrefStore.putUserSettingInt("refresh_order_status",1);
            if (!isFromSplit){
                EventBus.getDefault().post(new BaseMessageEvent<>(BaseMessageEvent.BASE_EVENT_TYPE_ON_PAYED_ALL));
                PrefStore.putUserSettingInt("refresh_order_status",1);
            }else {
                SplitOrderActivity.startSplitOrderActivity(this, oid, title, orderDes, price, alipay_account, pid, false);
            }
            finish();
        } else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_FAIL) {
            Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
        } else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_8000) {
            Toast.makeText(this, "支付异常", Toast.LENGTH_SHORT).show();
        }

        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
    }

}
