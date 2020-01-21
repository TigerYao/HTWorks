package com.huatu.handheld_huatu.business.me;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.adapter.RedPacketDetailAdapter;
import com.huatu.handheld_huatu.business.me.bean.RedBagInfo;
import com.huatu.handheld_huatu.business.me.bean.RedBagShareInfo;
import com.huatu.handheld_huatu.business.me.bean.RedPacketDetailBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class RedPacketDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_bref)
    ImageView iv_bref;

    @BindView(R.id.title_bar)
    TopActionBar title_bar;

    @BindView(R.id.tv_course_title)
    TextView tv_course_title;
    //领取情况
    @BindView(R.id.tv_received_num)
    TextView tv_received_num;
    //剩余时间
    @BindView(R.id.ll_remind_time)
    LinearLayout ll_remind_time;
    @BindView(R.id.tv_no_time_left)
    TextView tv_no_time_left;
    @BindView(R.id.tv_remind_time)
    TextView tv_remind_time;
    @BindView(R.id.tv_time_tip)
    TextView tv_time_tip;
    //没人领过
    @BindView(R.id.tv_no_body_receive)
    TextView tv_no_body_receive;
    //领完的角标
    @BindView(R.id.iv_no_red_packet_left)
    ImageView iv_no_red_packet_left;
    //领取人列表
    @BindView(R.id.rlv_red_received_people)
    RecyclerView rlv_red_received_people;

    //底部发送红包
    @BindView(R.id.rl_send_red_packet)
    RelativeLayout rl_send_red_packet;
    @BindView(R.id.iv_send_red_packet)
    ImageView iv_send_red_packet;

    private long redId;

    private RedPacketDetailAdapter mAdapter;
    private RedPacketDetailBean mData;
    private String mRule;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_red_packet_detail;
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
        initTitle();
        initData();
        initListener();
        initRecyclerView();
    }

    private void initTitle() {
        title_bar.setTitle("红包提现详情");
        title_bar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    private void initListener() {
        iv_send_red_packet.setOnClickListener(this);
        iv_bref.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_red_packet:
                //发红包
                getRedBagInfo();
                break;
            case R.id.iv_bref:
                //看活动说明
                if (mRule != null) {
                    showRuleWindow();
                } else {
                    getRuleData();
                }
                break;
        }

    }

    private void showRuleWindow() {
        PopWindowUtil.showPopInCenter(this, title_bar, 0, 0, R.layout.layout_redbag_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {
                    @Override
                    public void popViewCall(View contentView, final PopupWindow popWindow) {
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
                        ImageView ivOk = (ImageView) contentView.findViewById(R.id.iv_ok);
                        rlRedBag.setVisibility(View.GONE);
                        rlRule.setVisibility(View.VISIBLE);
                        tvRuleContent.setText(Html.fromHtml(mRule));
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
                                popWindow.dismiss();

                            }
                        });
                    }

                    @Override
                    public void popViewDismiss() {
                        startCountDownTask();
                    }
                });
    }

    private void getRuleData() {
        showProgress();
        ServiceProvider.getRedBagInfo(compositeSubscription, redId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();
                if (model.data != null && model.data instanceof RedBagInfo) {
                    RedBagInfo redBagInfo = (RedBagInfo) model.data;
                    mRule = redBagInfo.instruction;
                    if (mRule != null) {
                        showRuleWindow();
                    }
                } else {
                    ToastUtils.showShort(model.message);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
                ToastUtils.showShort("获取活动说明失败，请重试");
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        redId = intent.getLongExtra("redId", 0);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        loadDetailData();
    }

    private void loadDetailData() {
        ServiceProvider.getRedPacketDetail(compositeSubscription, redId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("服务出错了");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model.data != null) {
                    mData = (RedPacketDetailBean) model.data;
                    refreshUI();
                }
            }
        });
    }

    private void refreshUI() {
        if (mData.classTitle != null) {
            tv_course_title.setText(mData.classTitle);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder("");
        builder.append("助学红包已领取" + mData.receiveSum);
        builder.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                R.color.indicator_color)),
                7, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("/" + mData.sumNum + ",");
        SpannableStringBuilder builder1 = new SpannableStringBuilder("");
        if (!TextUtils.isEmpty(mData.receivePrice) && !TextUtils.isEmpty(mData.sumPrice)) {
            builder1.append("共" + mData.receivePrice);
            builder1.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                    R.color.indicator_color)),
                    1, builder1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder1.append("/" + mData.sumPrice + "元");
            builder.append(builder1);
        }
        if (tv_received_num != null) {
            tv_received_num.setText(builder);
        }
        if (mData.receiveSum == mData.sumNum) {
            //红包领完了
            if (ll_remind_time != null) {
                ll_remind_time.setVisibility(View.GONE);
            }
            //底部
            if (rl_send_red_packet != null) {
                rl_send_red_packet.setVisibility(View.GONE);
            }
            if (iv_no_red_packet_left != null) {
                iv_no_red_packet_left.setVisibility(View.VISIBLE);
            }
        } else {
            if (mData.receiveSum == 0) {
                //红包还木有人领
                if (tv_no_body_receive != null) {
                    tv_no_body_receive.setVisibility(View.VISIBLE);
                }
                if (rlv_red_received_people != null) {
                    rlv_red_received_people.setVisibility(View.GONE);
                }
            } else {
                if (rlv_red_received_people != null) {
                    rlv_red_received_people.setVisibility(View.VISIBLE);
                }
            }
        }
        if (mData.receiveInfo.size() != 0) {
            mAdapter.setData(mData.receiveInfo);
        }
        if (mData.status==1){
            if (mData.surplusTime <= 0) {
            //底部
                if (rl_send_red_packet != null) {
                    rl_send_red_packet.setVisibility(View.GONE);
                }
            //顶部时间
            if (tv_no_time_left != null) {
                tv_no_time_left.setVisibility(View.VISIBLE);
                tv_no_time_left.setText("哇哦~你已经错过发送红包时间啦~");
            }
        } else {
            if (tv_remind_time != null) {
                mData.surplusTime *= 1000;
                tv_remind_time.setVisibility(View.VISIBLE);
                tv_remind_time.setText(DateUtils.millToTime(mData.surplusTime));
                Typeface type = Typeface.createFromAsset(this.getAssets(), "font/851-CAI978.ttf");
                tv_remind_time.setTypeface(type);
                startCountDownTask();
                if (tv_time_tip != null) {
                    tv_time_tip.setVisibility(View.VISIBLE);
                }
            }

            if (mData.receiveSum!=mData.sumNum && rl_send_red_packet != null) {
                rl_send_red_packet.setVisibility(View.VISIBLE);
            }
            startAnimation();
        }
        }else if (mData.status==2){
            if (rl_send_red_packet != null) {
                rl_send_red_packet.setVisibility(View.GONE);
            }
            if (tv_no_time_left != null) {
            tv_no_time_left.setText("该课程已退款，不能再发送红包啦");
            tv_no_time_left.setVisibility(View.VISIBLE);
            }
        }else if (mData.status==3){
            if (rl_send_red_packet != null) {
                rl_send_red_packet.setVisibility(View.GONE);
            }
            if (tv_no_time_left != null) {
                tv_no_time_left.setText("该课程已转班，不能再发送红包啦");
                tv_no_time_left.setVisibility(View.VISIBLE);
            }
        }
    }

    protected Subscription timeSubscription;

    protected void startCountDownTask() {
        if (timeSubscription != null) {
            timeSubscription.unsubscribe();
            compositeSubscription.remove(timeSubscription);
        }
        if (mData != null && mData.surplusTime > 0) {
            timeSubscription = Observable.interval(10, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            refreshLeftTime();
                        }
                    });
            compositeSubscription.add(timeSubscription);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDownTask();
//        ToastUtils.showEssayToast("出现");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ToastUtils.showEssayToast("消失");

    }

    private void refreshLeftTime() {
        if (mData.surplusTime > 0) {
            mData.surplusTime -= 10;
            tv_remind_time.setText(DateUtils.millToTime(mData.surplusTime));
        } else {
            if (timeSubscription != null) {
                timeSubscription.unsubscribe();
                compositeSubscription.remove(timeSubscription);
            }
            tv_remind_time.setVisibility(View.GONE);
            tv_time_tip.setVisibility(View.GONE);
            tv_no_time_left.setVisibility(View.VISIBLE);
            rl_send_red_packet.setVisibility(View.GONE);
        }
    }

    ValueAnimator animator;

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(1f, 1.15f, 1f);
        animator.setInterpolator(new LinearInterpolator());                         // 设置线性差值器
        animator.setRepeatCount(ValueAnimator.INFINITE);                            // 设置重复
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {     // 设置持续事件
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                iv_send_red_packet.setScaleX(values);
                iv_send_red_packet.setScaleY(values);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }

    private void initRecyclerView() {
        mAdapter = new RedPacketDetailAdapter(this);
        rlv_red_received_people.setLayoutManager(new LinearLayoutManager(this));
        rlv_red_received_people.setNestedScrollingEnabled(false);
        rlv_red_received_people.setAdapter(mAdapter);
    }

    private void getRedBagInfo() {
        showProgress();
        ServiceProvider.getRedBagInfo(compositeSubscription, redId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();
                if (model.data != null && model.data instanceof RedBagInfo) {
                    RedBagInfo redBagInfo = (RedBagInfo) model.data;
//                    openRedBag(redBagInfo);
                    getShareInfo(redBagInfo);
                    mRule = redBagInfo.instruction;
                } else {
                    ToastUtils.showShort(model.message);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
                ToastUtils.showShort("获取红包失败，请重试");
            }
        });
    }
//分享弹出红包 现在不弹了
    private void openRedBag(final RedBagInfo redBagInfo) {
        PopWindowUtil.showPopInCenter(this, title_bar, 0, 0, R.layout.layout_redbag_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {

                    ValueAnimator animator;

                    Subscription timeSubscription;

                    @Override
                    public void popViewCall(final View contentView, final PopupWindow popWindow) {
//                        contentView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                popWindow.dismiss();
//                            }
//                        });
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

                        Typeface fontStyle = Typeface.createFromAsset(RedPacketDetailActivity.this.getAssets(), "font/851-CAI978.ttf");
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
//                                        final String formatData = TimeUtils.getFormatData("HH:mm:ss.SSS", redBagInfo.endTime);
                                        tvTime.setText(DateUtils.millToTime(redBagInfo.endTime) + " 后过期");
                                    }
                                });

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

                        // 看规则 不在这了
                        ivRule.setVisibility(View.GONE);
//                        ivRule.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                outA.start();
//                            }
//                        });

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
                        startCountDownTask();

                    }
                });
    }

    /**
     * 获取分享信息，并分享
     */
    private void getShareInfo(RedBagInfo redBagInfo) {
        showProgress();
        ServiceProvider.getRedBagShareInfo(compositeSubscription, String.valueOf(redBagInfo.aloneByPrice), redBagInfo.redEnvelopeId, redBagInfo.param, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();
                if (model.data != null && model.data instanceof RedBagShareInfo) {
                    RedBagShareInfo redBagShareInfo = (RedBagShareInfo) model.data;
//                    ShareUtil.test(RedPacketDetailActivity.this, redBagShareInfo.id, redBagShareInfo.desc, redBagShareInfo.title, redBagShareInfo.url);
                    ShareUtil.test(RedPacketDetailActivity.this, redBagShareInfo.id, redBagShareInfo.desc, redBagShareInfo.title, redBagShareInfo.url, null, R.mipmap.redbag_share_img, null, null);
                } else {
                    ToastUtils.showShort(model.message);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
                ToastUtils.showShort("获取分享信息失败，请稍后重试");
            }
        });
    }

    // 改变视角距离, 贴近屏幕
    private void setCameraDistance(View v) {
        int distance = 6000;
        float scale = getResources().getDisplayMetrics().density * distance;
        v.setCameraDistance(scale);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animator != null) {
            animator.cancel();
        }
        if (timeSubscription != null && !timeSubscription.isUnsubscribed()) {
            timeSubscription.unsubscribe();
            timeSubscription = null;
        }
    }
}
