package com.huatu.handheld_huatu.business.me;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.adapter.RedPacketCourseInfoAdapter;
import com.huatu.handheld_huatu.business.me.adapter.RedPacketReceivedInfoAdapter;
import com.huatu.handheld_huatu.business.me.bean.RedPacketBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomSupDialog;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by acaige on 2018/9/27.
 */
public class RedPacketsActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_received_rabbit)
    ImageView iv_received_rabbit;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.tv_red_packet_num)
    TextView tv_red_packet_num;
    @BindView(R.id.tv_get_forward)
    TextView tv_get_forward;
    @BindView(R.id.rl_not_receive)
    RelativeLayout rl_not_receive;
    @BindView(R.id.rlv_red_pocket_num)
    RecyclerView rlv_red_pocket_num;
    @BindView(R.id.rlv_red_pocket_course)
    RecyclerView rlv_red_pocket_course;


    private RedPacketBean mRedPacketBean;
    private RedPacketReceivedInfoAdapter mReceivedInfoAdapter;
    private RedPacketCourseInfoAdapter mCourseInfoAdapter;

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_red_packets;
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

    @Override
    protected void onInitView() {
        super.onInitView();
        initRecyclerView();
        initListener();
    }

    private void initRecyclerView() {
        mReceivedInfoAdapter = new RedPacketReceivedInfoAdapter(this);
        mCourseInfoAdapter = new RedPacketCourseInfoAdapter(this);
        rlv_red_pocket_course.setLayoutManager(new LinearLayoutManager(this));
        rlv_red_pocket_course.setAdapter(mCourseInfoAdapter);
        rlv_red_pocket_num.setLayoutManager(new LinearLayoutManager(this));
        rlv_red_pocket_num.setAdapter(mReceivedInfoAdapter);
        rlv_red_pocket_course.setNestedScrollingEnabled(false);
        rlv_red_pocket_num.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        loadData();
    }

    private void loadData() {
        showProgress();
        ServiceProvider.getRedPacketData(compositeSubscription, SpUtils.getUname(), new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                if (model != null) {
                    mRedPacketBean = (RedPacketBean) model.data;
                }
                refreshUI();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                ToastUtils.showEssayToast(getResources().getString(R.string.xs_networkdata_failed1));
            }
        });
    }

    private void refreshUI() {
        if (mRedPacketBean.redEnvelopePrice != null) {
            tv_red_packet_num.setText(mRedPacketBean.redEnvelopePrice);
            if (mRedPacketBean.redEnvelopePrice.equals("0.00")) {
                if (rl_not_receive != null) {
                    rl_not_receive.setVisibility(View.VISIBLE);
                }
                if (tv_tip != null) {
                    tv_tip.setVisibility(View.VISIBLE);
                }
                if (rlv_red_pocket_num != null) {
                    rlv_red_pocket_num.setVisibility(View.GONE);
                }
                if (tv_get_forward != null) {
                    tv_get_forward.setTextColor(ContextCompat.getColor(this, R.color.black250));
                    tv_get_forward.setBackground(ContextCompat.getDrawable(this, R.drawable.no_way_get_forward));
                    tv_get_forward.setEnabled(false);
                }
            } else {
                if (iv_received_rabbit != null) {
                    iv_received_rabbit.setVisibility(View.VISIBLE);
                }
                if (rlv_red_pocket_num != null) {
                    rlv_red_pocket_num.setVisibility(View.VISIBLE);
                }
            }
        } else {
            tv_red_packet_num.setText("0");
            if (rl_not_receive != null) {
                rl_not_receive.setVisibility(View.VISIBLE);
            }
        }
        Typeface type = Typeface.createFromAsset(this.getAssets(), "font/851-CAI978.ttf");
        tv_red_packet_num.setTypeface(type);
        if (mRedPacketBean.receiveInfo.size() != 0) {
            if (rlv_red_pocket_num != null) {
                rlv_red_pocket_num.setVisibility(View.VISIBLE);
            }
            mReceivedInfoAdapter.setData(mRedPacketBean.receiveInfo);
        } else {
            if (rlv_red_pocket_num != null) {
                rlv_red_pocket_num.setVisibility(View.GONE);
            }
        }
        if (mRedPacketBean.redEnvelopeInfo.size() != 0) {
            mCourseInfoAdapter.setData(mRedPacketBean.redEnvelopeInfo);
        }

    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_get_forward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_get_forward:
                //  提现
                if (SpUtils.getMobile() != null) {
                    Intent intent = new Intent(this, RedPacketWithdrawalActivity.class);
                    startActivity(intent);
                } else {
                    //给提示
                    showBindPhoneTip();
                }
                break;
        }
    }

    private void showBindPhoneTip() {
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(this);
        builder.setRLayout(R.layout.layout_custom_no_red_packet_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                TextView tv_tips = (TextView) mView.findViewById(R.id.tv_tips);
                TextView tv_to_see = (TextView) mView.findViewById(R.id.tv_to_see);
                tv_tips.setText("请先绑定手机号。前往“我的”点击头像，选择“手机号”选择进入绑定页面~");
                tv_to_see.setText("知道了");
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                mView.findViewById(R.id.tv_to_see).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        CustomSupDialog dialog = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);

    }
}
