package com.huatu.handheld_huatu.business.me;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.MeAdvertiseHolder;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.customview.ProportionRelativeLayout;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;
import com.huatu.handheld_huatu.business.essay.checkfragment.CheckCountFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.me.account.MyAccountActivity;
import com.huatu.handheld_huatu.business.me.bean.LevelBean;
import com.huatu.handheld_huatu.business.me.bean.MyAccountBean;
import com.huatu.handheld_huatu.business.me.bean.MyRedPacketBean;
import com.huatu.handheld_huatu.business.me.bean.MySignBean;
import com.huatu.handheld_huatu.business.me.bean.SignBean;
import com.huatu.handheld_huatu.business.me.fragment.CourseCardFragment;
import com.huatu.handheld_huatu.business.me.fragment.CousreCollectFragment;
import com.huatu.handheld_huatu.business.me.fragment.LevelConditionFragment;
import com.huatu.handheld_huatu.business.me.fragment.ServiceCenterFragment;
import com.huatu.handheld_huatu.business.me.order.OrderActivity;
import com.huatu.handheld_huatu.business.message.MessageGroupListFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownCourseManageFragment;
import com.huatu.handheld_huatu.datacache.HomeFDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.event.me.MeMsgMessageEvent;
import com.huatu.handheld_huatu.helper.SimpleAnimationListener;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseItem;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountBean;
import com.huatu.handheld_huatu.mvppresenter.me.MeArenaContentImpl;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.tinker.PatchUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomSupDialog;
import com.huatu.handheld_huatu.view.custom.CustomRotateAnim;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht on 2016/7/20.
 */
public class MeFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "MeFragment";

    private CompositeSubscription compositeSubscription;

    private LevelBean.LevelData mData;
    private boolean mSign;

    private String mProgress;

    @BindView(R.id.username_txt)
    TextView mUsernameTxt;
    @BindView(R.id.tv_my_check)
    TextView tv_my_check;
    @BindView(R.id.tv_tubi)
    TextView tv_tubi;

    @BindView(R.id.user_avater_img)
    ImageView mUserAvater;

    @BindView(R.id.tv_grade)
    TextView mUserLevelTxt;

    @BindView(R.id.rl_test_url)
    ViewGroup mSetTestUrlView;

    @BindView(R.id.tv_message_num)
    TextView mMsgNumTxt;

    @BindView(R.id.textview_target_test)
    TextView mExamAreaTxt;

    @BindView(R.id.tv_sign_btn)
    ImageView mSignImgBtn;
    @BindView(R.id.iv_red_pocket)
    ImageView iv_red_pocket;
    @BindView(R.id.ll_red_packet)
    LinearLayout ll_red_packet;

    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.rl_adv)
    ProportionRelativeLayout rlAdv;
    @BindView(R.id.me_advertise)
    ConvenientBanner meAdvertise;           // 轮播广告

    private BaseListResponseModel<AdvertiseConfig> homeAdvertiseList;
    private List<AdvertiseConfig> mAdvertiseList;                           // 轮播广告内容
    private List<String> imageUrls = new ArrayList<>();                     // 轮播图Url列表

    private long mUserGold;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_me;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.typeExObject instanceof MeMsgMessageEvent) {
            if (event.type == MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_HAS) {
                refreshMsgNum();
            } else if (event.type == MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_NO) {
                refreshMsgNum();
            }
        } else if (event.typeExObject instanceof ExamTypeAreaMessageEvent) {
            if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS) {
                loadMsgData();
                mExamAreaTxt.setText(SignUpTypeDataCache.getInstance().getCategoryTitle() + " " + getSelectCity());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMessage(MessageEvent event) {
        if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_UPDATE_VIEW) {      // 切换科目，重新获取广告
            getAdv();
        }
    }

    private void refreshMsgNum() {
        if (mMsgNumTxt != null && MeArenaContentImpl.data != null && MeArenaContentImpl.data.unreadMsgCount > 0) {
            if (MeArenaContentImpl.data.unreadMsgCount > 99) {
                mMsgNumTxt.setText("99+");
            } else {
                mMsgNumTxt.setText(MeArenaContentImpl.data.unreadMsgCount + "");
            }
            mMsgNumTxt.setVisibility(View.VISIBLE);
        } else {
            if (mMsgNumTxt != null) {
                mMsgNumTxt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_CLOSE, new MeMsgMessageEvent());
        if (!SpUtils.getLoginState()) {
            //游客模式下
            //头像
            mUsernameTxt.setText("注册/登录");
            mExamAreaTxt.setText("公务员 全国");
            //签到按钮
            if (mSignImgBtn != null) {
                mSignImgBtn.setEnabled(true);
            }
        }
        //不同环境下设置测试按钮是否可见
        if (BuildConfig.isDebug || (PatchUtils.hasLocalPatchConfig(getActivity()))) {
            mSetTestUrlView.setVisibility(View.VISIBLE);
        } else {
            mSetTestUrlView.setVisibility(View.GONE);
        }
    }

    private Runnable mRotateRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.e("mRotateRunnable", "mRotateRunnable");
            CustomRotateAnim rotateAnim = CustomRotateAnim.getCustomRotateAnim();
            // 一次动画执行1秒
            rotateAnim.setDuration(200);
            // 设置为循环播放
            rotateAnim.setRepeatCount(5);
            // 设置为匀速
            rotateAnim.setInterpolator(new LinearInterpolator());
            // 开始播放动画
            if (null != iv_red_pocket) iv_red_pocket.startAnimation(rotateAnim);
            if (null != ll_red_packet)
                ll_red_packet.postDelayed(this, 2800);
        }
    };

    private void initAnimation() {
        Animation translateAnimation = new TranslateAnimation(400, -10, 0, 0);//平移动画
        translateAnimation.setDuration(1500);//动画持续的时间
        translateAnimation.setFillAfter(true);//不回到起始位置
        translateAnimation.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                if (ll_red_packet != null) {
                    ll_red_packet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if (null != iv_red_pocket) iv_red_pocket.setClickable(true);
            }
        });

        if (null != ll_red_packet) {
            ll_red_packet.startAnimation(translateAnimation);//动画开始执行 放在最后即可
            ll_red_packet.removeCallbacks(mRotateRunnable);
            ll_red_packet.postDelayed(mRotateRunnable, 1500);
        }
    }

    @OnClick({R.id.rl_set, R.id.rl_test_url})
    public void OnClickSet(View view) {
        //设置
        switch (view.getId()) {
            case R.id.rl_set:
                AccountSetActivity.newInstance(mActivity);
                break;
            case R.id.rl_test_url://设置测试环境
                TestActivity.newInstance(mActivity);
                break;
        }
    }

    @OnClick({R.id.username_txt, R.id.tv_collection, R.id.rl_scan_btn, R.id.tv_sign_btn, R.id.iv_red_pocket, R.id.tv_grade, R.id.myorder_layout, R.id.mygold_layout, R.id.mycorrecting_layout, R.id.rl_downmanage, R.id.rl_course_card, R.id.rl_service, R.id.rl_feedback, R.id.iv_message, R.id.user_avater_img, R.id.rl_top_content, R.id.iv_right, R.id.rl_target_test})
    public void onClick(View view) {
        int id = view.getId();
        if (!CommonUtils.checkLogin(mActivity)) {
            return;
        }
        switch (id) {
            case R.id.tv_collection://我的收藏
                StudyCourseStatistic.clickStatistic("我的", "页面第三模块", "我的收藏");
                // CourseCollectListFragment.lanuch(getContext());
                UIJumpHelper.jumpFragment(getContext(), CousreCollectFragment.class);
                break;
            case R.id.rl_scan_btn://扫一扫
                StudyCourseStatistic.clickStatistic("我的", "页面第一模块右侧", "扫一扫");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                Intent intent = new Intent(mActivity, MyScanActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_red_pocket:
                //跳转红包页
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                iv_red_pocket.setClickable(false);
                checkRedPacket();
                break;
            case R.id.tv_sign_btn://签到
                StudyCourseStatistic.clickStatistic("我的", "页面第一模块左侧", "签到领图币");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (!mSign) {
                    Sign();
                }
                break;
            case R.id.myorder_layout://我的订单
                StudyCourseStatistic.clickStatistic("我的", "页面第三模块", "我的订单");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                OrderActivity.newInstance(mActivity, 0);
                break;
            case R.id.mygold_layout://我的图币
                StudyCourseStatistic.clickStatistic("我的", "页面第四模块", "我的图币");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                MyAccountActivity.newInstance(mActivity);
                break;
            case R.id.mycorrecting_layout://我的批改
                StudyCourseStatistic.clickStatistic("我的", "页面第四模块", "我的批改");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                BaseFrgContainerActivity.newInstance(mActivity, CheckCountFragment.class.getName(), null);
                break;
            case R.id.rl_downmanage://缓存课程管理
                StudyCourseStatistic.clickStatistic("我的", "页面第三模块", "离线缓存");
                DownCourseManageFragment.lanuch(mActivity, "MeFragment");
                break;
            case R.id.rl_course_card://课程卡
                BaseFrgContainerActivity.newInstance(mActivity, CourseCardFragment.class.getName(), CourseCardFragment.getArgs());
                break;
            case R.id.rl_service://在线客服
                StudyCourseStatistic.clickStatistic("我的", "页面第四模块", "在线客服");
                ServiceCenterFragment.lanuch(getContext());
                break;
            case R.id.rl_feedback://建议及反馈
                FeedbackActivity.newInstance(MeFragment.this.getActivity());
                break;
            case R.id.iv_message://消息
                StudyCourseStatistic.clickStatistic("我的", "页面第一模块右上角", "消息");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                BaseFrgContainerActivity.newInstance(mActivity, MessageGroupListFragment.class.getName(), null);
                break;
            case R.id.tv_grade://等级
                StudyCourseStatistic.clickStatistic("我的", "页面第二模块中间", "等级");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络错误，请检查网络");
                    return;
                }
                BaseFrgContainerActivity.newInstance(mActivity, LevelConditionFragment.class.getName(), LevelConditionFragment.getArgs());
                break;
            case R.id.user_avater_img://我的账户
            case R.id.iv_right:
            case R.id.username_txt:
            case R.id.rl_top_content:
                StudyCourseStatistic.clickStatistic("我的", "页面第二模块左侧", "头像");
                Intent accountIntent = new Intent(mActivity, AccountInfoActivity.class);
                startActivityForResult(accountIntent, 119);
                break;
            case R.id.rl_target_test://目标考试
                StudyCourseStatistic.clickStatistic("我的", "页面第四模块", "考试类型");
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                Intent targetIntent = new Intent(mActivity, ExamTargetAreaActivity.class);
                startActivityForResult(targetIntent, 120);
                break;
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        getAdv();
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser) {
            if (SpUtils.getLoginState()) {
                if (mSignImgBtn != null) {
                    mSignImgBtn.setEnabled(false);
                }
                if (mUserLevelTxt != null) {
                    mUserLevelTxt.setEnabled(false);
                }

                checkRedPacketShow();
                loadLevel();
                getUserCoin();
                getCheckCount();

                SignOrNot();
                loadMsgData();

                // 头像
                ImageLoad.displayUserAvater(mActivity, SpUtils.getAvatar(), mUserAvater, R.mipmap.me_default_avater);
                // 昵称
                mUsernameTxt.setText(getNickname());
                // 考试科目
                mExamAreaTxt.setText(SignUpTypeDataCache.getInstance().getCategoryTitle() + " " + getSelectCity());
            }
        } else {
            if (iv_red_pocket != null) {
                iv_red_pocket.clearAnimation();
                CustomRotateAnim.endAnim();
            }
            if (ll_red_packet != null) {
                ll_red_packet.clearAnimation();
                ll_red_packet.removeCallbacks(mRotateRunnable);
                ll_red_packet.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (SpUtils.getLoginState()) {
                checkRedPacketShow();
                loadLevel();
                getUserCoin();
                getCheckCount();
            }
        } else {
            if (iv_red_pocket != null) {
                iv_red_pocket.clearAnimation();
                CustomRotateAnim.endAnim();
            }
            if (ll_red_packet != null) {
                ll_red_packet.clearAnimation();
                ll_red_packet.removeCallbacks(mRotateRunnable);
                ll_red_packet.setVisibility(View.GONE);
            }
        }
    }

    // 广告
    private void getAdv() {
        if (!NetUtil.isConnected()) return;
        ServiceProvider.getMeAdv(compositeSubscription, SignUpTypeDataCache.getInstance().getCurCategory(), new NetResponse() {
            @Override
            public void onError(Throwable e) {
                viewDivider.setVisibility(View.VISIBLE);
                rlAdv.setVisibility(View.GONE);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                updateAdvertise(model);
            }
        });
    }

    /**
     * HomeView
     * 广告轮播内容数据回调
     */
    public void updateAdvertise(BaseListResponseModel<AdvertiseConfig> list) {
        homeAdvertiseList = list;
        if (homeAdvertiseList == null || homeAdvertiseList.code != 1000000
                || homeAdvertiseList.data == null || homeAdvertiseList.data.size() <= 0) {
            if (meAdvertise != null) {
                viewDivider.setVisibility(View.VISIBLE);
                rlAdv.setVisibility(View.GONE);
            }
            return;
        }
        if (meAdvertise != null) {
            viewDivider.setVisibility(View.GONE);
            rlAdv.setVisibility(View.VISIBLE);
        }
        imageUrls.clear();
        mAdvertiseList = homeAdvertiseList.data;
        for (int i = 0; i < homeAdvertiseList.data.size(); i++) {
            AdvertiseItem params = homeAdvertiseList.data.get(i).params;
            imageUrls.add(params.image);
            HomeFDataCache.getInstance().setScAdvertiseConfig(homeAdvertiseList.data.get(i));
        }

        meAdvertise.setPages(new CBViewHolderCreator<MeAdvertiseHolder>() {
            @Override
            public MeAdvertiseHolder createHolder() {
                return new MeAdvertiseHolder();
            }
        }, imageUrls)
                .setOnItemClickListener(this)
                .setPointViewVisible(true)
                .setCanLoop(true);
        meAdvertise.setScrollDuration(600);
        if (!meAdvertise.isTurning()) {
            meAdvertise.startTurning(3000);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mAdvertiseList == null || mAdvertiseList.size() == 0) return;
        AdvertiseConfig advertiseConfig = mAdvertiseList.get(position);
        if (advertiseConfig.params == null) return;
        AdvertiseItem params = advertiseConfig.params;

        MatchCacheData.getInstance().matchPageFrom = "app我的广告轮播图";
        ZtkSchemeTargetStartTo.startTo(mActivity, params, advertiseConfig.target, false, compositeSubscription);
    }

    // 红包入口是否显示
    private void checkRedPacketShow() {
        ServiceProvider.checkRedPacketShow(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model.data != null) {
                    MyRedPacketBean data = (MyRedPacketBean) model.data;
                    int isShow = data.showRedEnvelope;
                    if (isShow == 1) {
                        //显示红包入口
                        initAnimation();
                    } else {
                        if (ll_red_packet != null) {
                            ll_red_packet.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (ll_red_packet != null) {
                    ll_red_packet.setVisibility(View.GONE);
                }
            }
        });
    }

    // 等级查询
    private void loadLevel() {
        Subscription subscribe = RetrofitManager.getInstance().getService().getLevel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<LevelBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mUserLevelTxt != null) {
                            mUserLevelTxt.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNext(LevelBean levelBean) {
                        if (levelBean.code == 1000000) {
                            mData = levelBean.data;
                            if (mData != null) {
                                if (mData.level == 10) {
                                    mProgress = "100%";
                                } else {
                                    mProgress = mData.percent;
                                }
                            }
                            if (mUserLevelTxt != null) {
                                mUserLevelTxt.setVisibility(View.VISIBLE);
                                mUserLevelTxt.setEnabled(true);
                                mUserLevelTxt.setText("LV" + mData.level);
                            }
                        }
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    // 获取用户金币
    private void getUserCoin() {
        if (!NetUtil.isConnected()) return;
        Subscription subscribe = RetrofitManager.getInstance().getService().getMyAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyAccountBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MyAccountBean myAccountBean) {
                        if (myAccountBean.code == 1000000) {
                            MyAccountBean.MyAccountData mMyAccount = myAccountBean.data;
                            mUserGold = mMyAccount.userCountres.UserMoney;
                            if (tv_tubi != null) {
                                tv_tubi.setText(mUserGold + " 图币");
                            }
                        } else {
                            if (myAccountBean.message != null) {
                                CommonUtils.showToast(myAccountBean.message);
                            }
                        }
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    // 用户批改次数
    private void getCheckCount() {
        if (!NetUtil.isConnected()) return;
        ServiceProvider.getCheckCountList(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null && model.data != null) {
                    CheckCountBean var = (CheckCountBean) model.data;
                    int checkCount = var.totalNum;
                    if (tv_my_check != null) {
                        tv_my_check.setText("剩余" + checkCount + "次");
                    }
                } else {
                    if (tv_my_check != null) {
                        tv_my_check.setText("剩余0次");
                    }
                }

            }
        });
    }

    // 签到查询
    private void SignOrNot() {
        if (UserInfoUtil.userId <= 0) return;
        Subscription subscribe = RetrofitManager.getInstance().getService().getSign()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<SignBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mSignImgBtn != null) {
                            mSignImgBtn.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNext(SignBean signBean) {
                        if (signBean.code == 1000000) {
                            mSign = true;
                            if (mSignImgBtn != null) {
                                mSignImgBtn.setEnabled(false);
                            }

                        } else if (signBean.code == 1115107) {
                            mSign = false;
                            if (mSignImgBtn != null) {
                                mSignImgBtn.setEnabled(true);

                            }
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    // 获取未读消息数
    private void loadMsgData() {
        MeArenaContentImpl.loadMsgData(compositeSubscription);
    }

    // 检查用户是否有红包并跳红包页面
    private void checkRedPacket() {
        ServiceProvider.checkRedPacket(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model.data != null) {
                    MyRedPacketBean data = (MyRedPacketBean) model.data;
                    int hasRed = data.hasRedEnvelope;
                    if (hasRed == 1) {
                        Intent intent4 = new Intent(mActivity, RedPacketsActivity.class);
                        startActivity(intent4);
                    } else {
                        showNoRedPacket();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("网络出错了，请稍后重试");
                iv_red_pocket.setClickable(true);

            }
        });
    }

    // 无红包弹窗
    private void showNoRedPacket() {
        CustomSupDialog.Builder builder = new CustomSupDialog.Builder(mActivity);
        builder.setRLayout(R.layout.layout_custom_no_red_packet_dialog).setBindInter(new CustomSupDialog.DialogInter() {
            @Override
            public void BindView(final View mView, final Dialog dialog) {
                if (mView == null || dialog == null) {
                    return;
                }
                TextView tv_tips = mView.findViewById(R.id.tv_tips);
                TextView tv_to_see = mView.findViewById(R.id.tv_to_see);
                tv_tips.setText("凡购买参与红包活动课程的学员，分享助学红包后可领取现金红包，领取即可提现哦～");
                tv_to_see.setText("去看看");
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
                        //去直播列表
                        Intent intent = new Intent(mActivity, MainTabActivity.class);
                        intent.putExtra("require_index", 1);
                        startActivity(intent);
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

    // 签到
    private void Sign() {
        Subscription subscribe = RetrofitManager.getInstance().getService().mySign()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<MySignBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showEssayToast("签到失败了，请稍后重试");
                    }

                    @Override
                    public void onNext(MySignBean mySignBean) {
                        if (mySignBean.code == 1000000) {
                            MySignBean.MySignData mData = mySignBean.data;
                            if (mSignImgBtn != null) {
                                mSignImgBtn.setEnabled(false);
                            }
                            mSign = true;
                            ToastUtils.showMyRewardToast("签到成功！", "+" + mData.gold + "图币" + "，+" + mData.experience + "成长值");
                            getUserCoin();
                        }
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (iv_red_pocket != null) {
            iv_red_pocket.clearAnimation();
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120) {
            // 进入设置目标考试的页面，返回后重新设置目标考试地区
            mExamAreaTxt.setText(SignUpTypeDataCache.getInstance().getCategoryTitle() + " " + getSelectCity());
        } else if (requestCode == 119) {
            // 进入修改昵称页面返回后，重新设置账号信息的昵称
            mUsernameTxt.setText(getNickname());
            ImageLoad.displayUserAvater(mActivity, SpUtils.getAvatar(), mUserAvater, R.mipmap.me_default_avater);
        }
    }

    // 获取选择的目标考试区域名字
    private String getSelectCity() {
        return SpUtils.getAreaname();
    }

    // 获取昵称
    private String getNickname() {
        String nick = SpUtils.getNick();
        String mobile = SpUtils.getMobile();
        String uname = SpUtils.getUname();
        String email = SpUtils.getEmail();
        if (!TextUtils.isEmpty(nick)) {
            return nick;
        }
        if (!TextUtils.isEmpty(mobile)) {
            return mobile;
        }
        if (!TextUtils.isEmpty(uname)) {
            return uname;
        }
        if (!TextUtils.isEmpty(email)) {
            return email;
        }
        return "";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (meAdvertise != null && meAdvertise.isTurning()) {
            meAdvertise.stopTurning();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (meAdvertise != null && homeAdvertiseList != null && homeAdvertiseList.data != null && homeAdvertiseList.data.size() > 1) {
            meAdvertise.startTurning(3000);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (meAdvertise != null) {
            meAdvertise.setcurrentitem(0);
        }
    }
}
