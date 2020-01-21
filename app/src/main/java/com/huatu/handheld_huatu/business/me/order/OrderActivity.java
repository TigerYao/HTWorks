package com.huatu.handheld_huatu.business.me.order;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.faceteach.fragment.FaceOrderListFragment;
import com.huatu.handheld_huatu.business.me.adapter.CourseOrderAdapter;
import com.huatu.handheld_huatu.business.me.adapter.OrderEssayAdapter;
import com.huatu.handheld_huatu.business.me.bean.EssayOrderBean;
import com.huatu.handheld_huatu.business.me.bean.MyEssayOrderData;
import com.huatu.handheld_huatu.business.me.bean.MyV5Order;
import com.huatu.handheld_huatu.business.me.bean.MyV5OrderContent;
import com.huatu.handheld_huatu.business.me.bean.MyV5OrderData;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseHandDownFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.InvoiceStatusEvent;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.SwitchFrameLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.swiperecyclerview.XRecyclerView;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuRecyclerView;
import com.huatu.library.PullToRefreshBase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 我的订单
 * Created by chq on 2017/8/29.
 */

public class OrderActivity extends XiaoNengHomeActivity implements View.OnClickListener, IonLoadMoreListener {
    private static final String TAG = "OrderActivitys";
    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;

    @BindView(R.id.tv_title_course)
    TextView tv_title_course;

    @BindView(R.id.tv_title_essay)
    TextView tv_title_essay;

    @BindView(R.id.tv_title_face)
    TextView mtvTypeface;


    @BindView(R.id.tv_all)
    TextView tv_all;
    @BindView(R.id.tv_noPay)
    TextView tv_noPay;
    @BindView(R.id.tv_alreadyPay)
    TextView tv_alreadyPay;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.iv_tab)
    ImageView iv_tab;
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView xi_comm_page_list;
    @BindView(R.id.view_no_data)
    CommonErrorViewExsc viewNoData;                         // 没有数据
    private CustomLoadingDialog mDailyDialog;
    private AsyncTask<Void, Void, Void> mAsyncTask;

    private int mType = 0;
    private int mLocation = 0;
    private int mPage = 1;

    int currIndex=0;
    private int one;//两个相邻页面的偏移量
    private int offset;//图片移动的偏移量
    private int bmpW;//横线图片宽度
    private CourseOrderAdapter mOrderAdapter;
    private EssayExamImpl mEssayExamImpl;
    private OrderEssayAdapter mOrderEssayAdapter;
    private List<TextView> listTV = new ArrayList<>();
    private ArrayList<MyV5OrderContent> dataList = new ArrayList<>();
    private ArrayList<MyEssayOrderData.EssayOrderList> essayDataList = new ArrayList<>();
    private int orderStatus;
    private int subject = 0;
    private int location = 0;
    private boolean isHorizontal=false;
    private int offset0;
    private int offset1;
    private int currentstate=0;

    public enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 拉取更多
         */
        loadmore,
        /**
         * 刷新最新
         */
        refresh
    }

    private RefreshMode loadMode = RefreshMode.reset;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(InvoiceStatusEvent event) {
        if (event == null) {
            return;
        }
        if(null!=mOrderAdapter){
            mOrderAdapter.refreshInvoiceStatus(event.orderID,event.orderNum);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_delete_my_course_order) {
            if (xi_comm_page_list.getRefreshableView() != null) {
                xi_comm_page_list.getRefreshableView().reset();
            }
            if (mPage == 1) {
                dataList.clear();
                loadMyCourseOrder(false);
            }
//            loadMyCourseOrder(false);
//            mPage=1;
//            dataList.clear();
//            loadMyCourseOrder();
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_delete_my_essay_order) {
            if (xi_comm_page_list.getRefreshableView() != null) {
                xi_comm_page_list.getRefreshableView().reset();
            }
//            mPage=1;
//            essayDataList.clear();
//            loadMyEssayOrder();
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
           // "横屏"
            isHorizontal=true;
        }else{
         // "竖屏"
            isHorizontal=false;
        }
        startAnimation(mLocation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getScreenOrientation();
//        if(!CommonUtils.isPadv2(UniApplicationContext.getContext())){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mEssayExamImpl = new EssayExamImpl(compositeSubscription);
        location=getIntent().getIntExtra("location",0);
        initView();
        initRecyclerView();
        setListener();
        if (location==2){
            //来自客服页面，定位到已支付
            rl_left_top_bar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickAlreadyPay();
                }
            },800);
        }else {
            loadMyCourseOrder(false);
        }
    }

    private void getScreenOrientation() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            isHorizontal=true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            isHorizontal=false;
        }

    }

    private void initRecyclerView() {
        initCourseAdapter();
        xi_comm_page_list.getRefreshableView().setPagesize(20);
        xi_comm_page_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(this));
        xi_comm_page_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));
        xi_comm_page_list.getRefreshableView().setRecyclerAdapter(mOrderAdapter);
    }

    private void initCourseAdapter() {
        mOrderAdapter = new CourseOrderAdapter(this, mEssayExamImpl);
    }

    private void initEssayRecyclerView() {
        initEssayAdapter();
        xi_comm_page_list.getRefreshableView().setPagesize(20);
        xi_comm_page_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(this));
        xi_comm_page_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));
        xi_comm_page_list.getRefreshableView().setRecyclerAdapter(mOrderEssayAdapter);
    }

    private void initEssayAdapter() {
        mOrderEssayAdapter = new OrderEssayAdapter(this, mEssayExamImpl);
    }

    protected void onRefreshCompleted() {
        if (null != xi_comm_page_list) xi_comm_page_list.onRefreshComplete();
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        tv_title_course.setOnClickListener(this);
        tv_title_essay.setOnClickListener(this);
        mtvTypeface.setOnClickListener(this);

        tv_all.setOnClickListener(this);
        tv_noPay.setOnClickListener(this);
        tv_alreadyPay.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        xi_comm_page_list.getRefreshableView().setOnLoadMoreListener(this);
        xi_comm_page_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        viewNoData.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击刷新数据
                superOnRefresh();
            }
        });

    }

    private void superOnRefresh() {
        loadMode = RefreshMode.refresh;
        try {
            if (xi_comm_page_list.getRefreshableView() != null) {
                xi_comm_page_list.getRefreshableView().reset();
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }
        mPage = 1;
        if (subject == 0) {
            dataList.clear();
            loadMyCourseOrder(true);
        } else {
            essayDataList.clear();
            loadMyEssayOrder(true);
        }

    }

    private void initView() {
        tv_share.setVisibility(View.VISIBLE);
        listTV.add(tv_all);
        listTV.add(tv_noPay);
        listTV.add(tv_alreadyPay);
        listTV.add(tv_cancel);
        listTV.add(tv_share);
        this.getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        initTab();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            OrderActivity.this.getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            OrderActivity.this.getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });
        startAnimation(0);
    }


    private void loadMyCourseOrder(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请连接网络后点击屏幕重试");
            showEmptyView(1);
            return;
        }
        if (!isRefresh) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
        }
        Subscription subscribe = RetrofitManager.getInstance().getService().getMyOrder(mType, 1, mPage, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<MyV5OrderData>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        onRefreshCompleted();
                        showEmptyView(2);

                    }

                    @Override
                    public void onNext(MyV5OrderData myOrderBean) {
                        dismissLoadingDialog();
                        onRefreshCompleted();
                        if (myOrderBean.code == 1000000) {
                            MyV5Order data = myOrderBean.data;
                            if (data != null) {
                                dataList.addAll(data.data);
                                if (loadMode == RefreshMode.loadmore && xi_comm_page_list.getRefreshableView() != null) {
                                    xi_comm_page_list.getRefreshableView().checkloadMore(data.data.size());
                                    xi_comm_page_list.getRefreshableView().hideloading();
                                }
                            }

                            if (dataList != null && dataList.size() != 0) {
                                mOrderAdapter.setData(dataList);
                                viewNoData.setVisibility(View.GONE);
                            } else {
                                showEmptyView(3);
                            }


//                            if (data.current_page== data.last_page) {
//                                //没有下一页
//                                xi_comm_page_list.getRefreshableView().checkloadMore(0);
//                            }
//                            xi_comm_page_list.getRefreshableView().hideloading();
                            if (mPage == 1) {
                                // 下拉刷新后显示第一行
                                xi_comm_page_list.getRefreshableView().scrollToPosition(0);
                            }
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    private void loadMyEssayOrder(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请连接网络后点击屏幕重试");
            showEmptyView(1);
        }
        if (!isRefresh) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
        }
        Subscription subscribe = RetrofitManager.getInstance().getService().getMyEssayOrder(mType, mPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<EssayOrderBean>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        onRefreshCompleted();

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        onRefreshCompleted();
                        showEmptyView(2);
                    }

                    @Override
                    public void onNext(EssayOrderBean myOrderBean) {
                        dismissLoadingDialog();
                        if (myOrderBean.code == 1000000) {
                            MyEssayOrderData data = myOrderBean.data;
                            if (data != null) {
                                essayDataList.addAll(data.result);
                            }
                            if (essayDataList != null && essayDataList.size() != 0) {
                                mOrderEssayAdapter.setData(essayDataList);
                                viewNoData.setVisibility(View.GONE);
                            } else {
                                showEmptyView(3);
                            }
                            if (data.next == 0) {
                                xi_comm_page_list.getRefreshableView().checkloadMore(0);
                            }

                            xi_comm_page_list.getRefreshableView().hideloading();
                            if (mPage == 1) {
                                // 下拉刷新后显示第一行
                                xi_comm_page_list.getRefreshableView().scrollToPosition(0);
                            }
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    private void showEmptyView(int type) {
        viewNoData.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                viewNoData.setErrorText("网络未连接，联网后重试！");
                break;
            case 2:
                viewNoData.setErrorText("网络错误，请点击重试！");
                break;
            case 3:
                viewNoData.setErrorText("暂无相关订单！");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_top_bar:
                OrderActivity.this.finish();
                break;
            case R.id.tv_title_course:

                if (mLocation!=0){
                    mLocation=0;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                tv_share.setVisibility(View.VISIBLE);
                tv_cancel.setVisibility(View.VISIBLE);
                if (subject != 0) {
                    subject = 0;
                    changeTabShowPosition(false);
                    tv_title_course.setTextColor(ContextCompat.getColor(this, R.color.redF3));
                    tv_title_essay.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
                    mtvTypeface.setTextColor(0xFF4A4A4A);
                    mType = 0;
                    changeTabStatue(1, 0, 0, 0, 0);
                    startAnimation(0);
                    mPage = 1;
                    dataList.clear();
                    initRecyclerView();
                    loadMyCourseOrder(false);

                }
                break;
            case R.id.tv_title_essay:

                if (mLocation!=0){
                    mLocation=0;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                tv_cancel.setVisibility(View.VISIBLE);
                tv_share.setVisibility(View.GONE);
                if (subject != 1) {
                    subject = 1;
                    changeTabShowPosition(false);
                    tv_title_essay.setTextColor(ContextCompat.getColor(this, R.color.redF3));
                    tv_title_course.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
                    mtvTypeface.setTextColor(0xFF4A4A4A);
                    mType = -1;
                    changeTabStatue(1, 0, 0, 0, 0);
                    startAnimation(0);
                    mPage = 1;
                    initEssayRecyclerView();
                    essayDataList.clear();
                    loadMyEssayOrder(false);
                }
                break;

            case R.id.tv_title_face:
                if (mLocation!=0){
                    mLocation=0;
                }
                tv_cancel.setVisibility(View.GONE);
                tv_share.setVisibility(View.GONE);
                if (subject != 2) {
                    subject = 2;
                    tv_title_essay.setTextColor(0xFF4A4A4A);
                    tv_title_course.setTextColor(0xFF4A4A4A);
                    mtvTypeface.setTextColor(0xFFFF3F47);
                    FragmentManager fm = this.getSupportFragmentManager();
                    Fragment oldfragment = fm.findFragmentByTag("face_order_fag");

                    changeTabShowPosition(true);
                    changeTabStatue(1, 0, 0, 0, 0);
                    startAnimation(0);

                    if(oldfragment != null &&oldfragment.isAdded()&&(!oldfragment.isDetached())){
                        ((FaceOrderListFragment)oldfragment).refreshByType(0);
                    }
                 }
                break;
            case R.id.tv_all:
                if(subject==2){

                    switchTabFaceList(0);
                    return;
                }
                if (mLocation!=0){
                    mLocation=0;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                if (subject == 0) {
                    if (mType != 0) {
                        mType = 0;
                        mPage = 1;
                        dataList.clear();
                        changeTabStatue(1, 0, 0, 0, 0);
                        startAnimation(0);
                        initRecyclerView();
                        loadMyCourseOrder(false);
                    }
                } else {
                    if (mType != -1) {
                        mType = -1;
                        mPage = 1;
                        essayDataList.clear();
                        changeTabStatue(1, 0, 0, 0, 0);
                        startAnimation(0);
                        initEssayRecyclerView();
                        loadMyEssayOrder(false);
                    }
                }
                break;
            case R.id.tv_noPay:
                if(subject==2){
                    switchTabFaceList(1);
                    return;
                }
                if (mLocation!=1){
                    mLocation=1;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                if (subject == 0) {
                    if (mType != 1) {
                        mType = 1;
                        mPage = 1;
                        dataList.clear();
                        changeTabStatue(0, 1, 0, 0, 0);
                        startAnimation(1);
                        initRecyclerView();
                        loadMyCourseOrder(false);
                    }
                } else {
                    if (mType != 0) {
                        mType = 0;
                        mPage = 1;
                        essayDataList.clear();
                        changeTabStatue(0, 1, 0, 0, 0);
                        startAnimation(1);
                        initEssayRecyclerView();
                        loadMyEssayOrder(false);
                    }
                }
                break;
            case R.id.tv_alreadyPay:
                if(subject==2){
                    switchTabFaceList(2);
                    return;
                }
                clickAlreadyPay();
                break;
            case R.id.tv_cancel:
                if (mLocation!=3){
                    mLocation=3;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                if (subject == 0) {
                    if (mType != 3) {
                        mPage = 1;
                        dataList.clear();
                        changeTabStatue(0, 0, 0, 1, 0);
                        mType = 3;
                        initRecyclerView();
                        startAnimation(3);
                        loadMyCourseOrder(false);
                    }
                } else {
                    if (mType != 2) {
                        mType = 2;
                        mPage = 1;
                        essayDataList.clear();
                        changeTabStatue(0, 0, 0, 1, 0);
                        startAnimation(3);
                        initEssayRecyclerView();
                        loadMyEssayOrder(false);
                    }
                }
                break;
            case R.id.tv_share:
                if (mLocation!=4){
                    mLocation=4;
                }
                loadMode = RefreshMode.reset;
                if (xi_comm_page_list.getRefreshableView() != null) {
                    xi_comm_page_list.getRefreshableView().reset();
                }
                if (subject == 0) {
                    if (mType != 10) {
                        mPage = 1;
                        dataList.clear();
                        changeTabStatue(0, 0, 0, 0, 1);
                        mType = 10;
                        startAnimation(4);
                        initRecyclerView();
                        loadMyCourseOrder(false);
                    }
                }
                break;

        }

    }

    private void clickAlreadyPay() {
        if (mLocation!=2){
            mLocation=2;
        }
        loadMode = RefreshMode.reset;
        if (xi_comm_page_list.getRefreshableView() != null) {
            xi_comm_page_list.getRefreshableView().reset();
        }
        if (subject == 0) {
            if (mType != 2) {
                mType = 2;
                mPage = 1;
                dataList.clear();
                changeTabStatue(0, 0, 1, 0, 0);
                startAnimation(2);
                initRecyclerView();
                loadMyCourseOrder(false);
            }
        } else {
            if (mType != 1) {
                mType = 1;
                mPage = 1;
                essayDataList.clear();
                changeTabStatue(0, 0, 1, 0, 0);
                startAnimation(2);
                initEssayRecyclerView();
                loadMyEssayOrder(false);
            }
        }
    }

    private void changeTabStatue(int all, int noPay, int payed, int cancel, int share) {
        //全部
        if (all == 1) {
            tv_all.setTextColor(ContextCompat.getColor(this, R.color.vpi__background_holo_dark));
            tv_all.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_all.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
            tv_all.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //待付款
        if (noPay == 1) {
            tv_noPay.setTextColor(ContextCompat.getColor(this, R.color.vpi__background_holo_dark));
            tv_noPay.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_noPay.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
            tv_noPay.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //已支付
        if (payed == 1) {
            tv_alreadyPay.setTextColor(ContextCompat.getColor(this, R.color.vpi__background_holo_dark));
            tv_alreadyPay.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_alreadyPay.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
            tv_alreadyPay.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //已取消
        if (cancel == 1) {
            tv_cancel.setTextColor(ContextCompat.getColor(this, R.color.vpi__background_holo_dark));
            tv_cancel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_cancel.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
            tv_cancel.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        //待分享
        if (share == 1) {
            tv_share.setTextColor(ContextCompat.getColor(this, R.color.vpi__background_holo_dark));
            tv_share.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            tv_share.setTextColor(ContextCompat.getColor(this, R.color.blackF4));
            tv_share.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            superOnRefresh();
        }
    }

    @Override
    public int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }


    /**
     * 初始化动画位置
     */
    public void initTab() {
//        bmpW = iv_tab.getWidth();
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenW = dm.widthPixels;
//        offset = (screenW / 5 - DisplayUtil.dp2px(30)) / 2;
//        one = offset * 2 + bmpW;
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
//        if (isHorizontal){
//            if (CommonUtils.isPad(this)){
////                layoutParams.setMargins(offset + DisplayUtil.dp2px(-20), DisplayUtil.dp2px(22), 0, 0);
////                layoutParams.setMargins(offset, DisplayUtil.dp2px(22), 0, 0);
//            }else {
//                layoutParams.setMargins(offset - DisplayUtil.dp2px(25), DisplayUtil.dp2px(22), 0, 0);
//                layoutParams.setMargins(offset, DisplayUtil.dp2px(22), 0, 0);
//            }
//        }else {
//            layoutParams.setMargins(offset + DisplayUtil.dp2px(15), DisplayUtil.dp2px(22), 0, 0);
//        }
        //imageView设置平移，使下划线平移到初始位置（平移一个offset）
//        Matrix matrix = new Matrix();
//        matrix.postTranslate(offset, 0);
//        iv_tab.setImageMatrix(matrix);
        iv_tab.setVisibility(View.VISIBLE);

    }

    public void startAnimation(int position) {
        if (subject == 0) {
            int scrollX;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
            if (position == 0) {
                params.width = DisplayUtil.dp2px(30);
                iv_tab.setLayoutParams(params);
            } else {
                params.width = DisplayUtil.dp2px(50);
                iv_tab.setLayoutParams(params);
            }
            bmpW = iv_tab.getWidth();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenW = dm.widthPixels;
            Log.i(TAG,"position---  "+position);
            Log.i(TAG,"screenW---  "+screenW);
            Log.i(TAG,"bmpW---   "+bmpW);
            Log.i(TAG,"offset---   "+offset);
            one = screenW / 5;
            offset0=(screenW / 5 -DisplayUtil.dp2px(30)) / 2;
            offset1=(screenW / 5 - DisplayUtil.dp2px(50)) / 2;
            Log.i(TAG,"one---"+one);
            Log.i(TAG,"offset0---"+offset0);
            Log.i(TAG,"offset1---"+offset1);
                if (position == 0) {
                    scrollX = position * one +offset0;
                    Log.i(TAG,"scrollX1---"+scrollX);
                } else {
                    scrollX = position * one +offset1;
                    Log.i(TAG,"scrollX3---"+scrollX);
                }
//            Animation animation = new TranslateAnimation(currIndex * one, scrollX, 0, 0);
            Animation animation = new TranslateAnimation(currentstate, scrollX, 0, 0);
            currentstate=scrollX;
            currIndex = position;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            iv_tab.startAnimation(animation);//是用ImageView来显示动画的
        } else if(subject == 2){

            int scrollX;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
            if (position == 0) {
                params.width = DisplayUtil.dp2px(30);
                iv_tab.setLayoutParams(params);
            } else {
                params.width = DisplayUtil.dp2px(50);
                iv_tab.setLayoutParams(params);
            }
            bmpW = iv_tab.getWidth();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenW = dm.widthPixels;
            one = screenW / 3;
            offset0=(one-DisplayUtil.dp2px(30))/2;
            offset1=(one-DisplayUtil.dp2px(50))/2;
            if (position == 0) {
                scrollX = position * one +offset0;
            } else {
                scrollX = position * one +offset1;
            }
 //            Animation animation = new TranslateAnimation(currIndex * one, scrollX, 0, 0);
            Animation animation = new TranslateAnimation(currentstate, scrollX, 0, 0);
            currentstate=scrollX;
            currIndex = position;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            iv_tab.startAnimation(animation);//是用ImageView来显示动画的
         }
         else {
            int scrollX;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
            if (position == 0) {
                params.width = DisplayUtil.dp2px(30);
                iv_tab.setLayoutParams(params);
            } else {
                params.width = DisplayUtil.dp2px(50);
                iv_tab.setLayoutParams(params);
            }
            bmpW = iv_tab.getWidth();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenW = dm.widthPixels;
            one = screenW / 4;
            offset0=(one-DisplayUtil.dp2px(30))/2;
            offset1=(one-DisplayUtil.dp2px(50))/2;
                if (position == 0) {
                    scrollX = position * one +offset0;
                } else {
                    scrollX = position * one +offset1;
                }

//            Animation animation = new TranslateAnimation(currIndex * one, scrollX, 0, 0);
            Animation animation = new TranslateAnimation(currentstate, scrollX, 0, 0);
            currentstate=scrollX;
            currIndex = position;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            iv_tab.startAnimation(animation);//是用ImageView来显示动画的
        }
        setColor();
        listTV.get(position).setSelected(true);

    }

    public void setColor() {
        for (int i = 0; i < listTV.size(); i++) {
            listTV.get(i).setSelected(false);
        }
    }

    public static void newInstance(Activity context,int location) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra("location",location);
        context.startActivityForResult(intent, 10002);
    }


    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomLoadingDialog(this);

        }
        tv_all.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mDailyDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AlertDialog  Exception:", e.getMessage() + "");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Override
    public void customChatParam() {
        if (orderStatus == 2) {
            customGroupId = TUTU_ROBOT_GROUPID;
        } else {
            customGroupId = HUAHUA_ROBOT_GROUPID;
        }
        mTitleName = "我的订单";
    }

    @Override
    public void OnLoadMoreEvent(boolean isRetry) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            xi_comm_page_list.onRefreshComplete();
            return;
        }
        mPage++;
        loadMode = RefreshMode.loadmore;

        if (subject == 0) {
            loadMyCourseOrder(true);
        } else {
            loadMyEssayOrder(true);
        }
    }


    private void switchTabFaceList(int tabIndex){
        if(mLocation==tabIndex) return;
        mLocation=tabIndex;
        if(tabIndex==0)
           changeTabStatue(1, 0, 0, 0, 0);
        else if(tabIndex==1){
            changeTabStatue(0, 1, 0, 0, 0);
        }else {
            changeTabStatue(0, 0, 1, 0, 0);
        }
        startAnimation(tabIndex);
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("face_order_fag");
        if(fragment != null &&fragment.isAdded()&&(!fragment.isDetached())){
            ((FaceOrderListFragment)fragment).refreshByType(tabIndex);
        }

    }
    boolean mFaceTabShow=false;
    private void changeTabShowPosition(boolean needShow) {

        if(mFaceTabShow==needShow) return;
        mFaceTabShow=needShow;
        if (!needShow) {

            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("face_order_fag");
            if (fragment != null && fragment.isAdded()) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.hide(fragment);
                // ft.addToBackStack("figure_action_fag");
                ft.commitAllowingStateLoss();
            }
        } else {

            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("face_order_fag");
            if (fragment == null || !fragment.isAdded()) {
                FragmentTransaction ft = fm.beginTransaction();

                ft.add(R.id.face_container_layout, new FaceOrderListFragment(), "face_order_fag");
                // ft.addToBackStack("figure_action_fag");
                ft.commitAllowingStateLoss();
            } else {
                FragmentTransaction ft = fm.beginTransaction();
                ft.show(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_order;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
}
