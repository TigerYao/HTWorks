package com.huatu.handheld_huatu.business.faceteach.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.faceteach.ConfirmOrderActivity;
import com.huatu.handheld_huatu.business.faceteach.adapter.F2fJobSelectAdapter;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobBean;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobData;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobStatusBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class F2fJobSelectFragment extends BaseFragment implements View.OnClickListener, IonLoadMoreListener {

    @BindView(R.id.fragment_title_bar)
    TopActionBar fragment_title_bar;
    @BindView(R.id.ll_top_search_bar)
    LinearLayout ll_top_search_bar;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.search_bar)
    EditText search_bar;
    @BindView(R.id.tv_sign_up)
    TextView tv_sign_up;
    @BindView(R.id.tv_sign_bottom)
    TextView tv_sign_bottom;
    @BindView(R.id.prv_job_list)
    PullRefreshRecyclerView prv_job_list;
    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    protected CompositeSubscription compositeSubscription;
    private F2fJobSelectAdapter mAdapter;
    private int page = 1;
    private List<F2fJobData> mData = new ArrayList<>();
    private String value;
    private Map<String, Object> params = new HashMap<>();
    private Map<String, String> statusParams = new HashMap<>();
    private String ssfb;
    private boolean isSearch = false;


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

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_f2f_job_select;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        initData();
        initTopBar();
        initRecyclerView();
        initListener();

    }

    private void initData() {
//        ssfb = "3353";
        args = getArguments();
        if (args != null) {
            ssfb = args.getString("ssfb");
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    value = search_bar.getText().toString().trim();
                    if (TextUtils.isEmpty(value)) {
                        ToastUtils.showShort("请输入搜索内容");
                        return true;
                    }
                    //搜索内容
                    onRefresh();
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_bar.getWindowToken(), 0);
                }
                return false;
            }
        });

        tv_sign_up.setOnClickListener(this);
        tv_sign_bottom.setOnClickListener(this);
    }

    private void initRecyclerView() {
        // 设置每页加载的条数，判断是否是最后一页
        prv_job_list.getRefreshableView().setPagesize(10);
        // 侧滑的实现，是配合com.nalan.swipeitem.recyclerview.SwipeItemLayout使用
        // prv_job_list.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(mActivity));
        prv_job_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(mActivity));
        // 自动加载更多的回调
        prv_job_list.getRefreshableView().setOnLoadMoreListener(this);
        // 加载过程中是否可以滑动
        prv_job_list.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        prv_job_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                F2fJobSelectFragment.this.onRefresh();
            }
        });

        mAdapter = new F2fJobSelectAdapter(mActivity);
        mAdapter.setSelectedPosition(0);
        prv_job_list.getRefreshableView().setRecyclerAdapter(mAdapter);
    }

    private void onRefresh() {
        loadMode = RefreshMode.refresh;
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            prv_job_list.onRefreshComplete();
            return;
        }
        try {
            if (prv_job_list.getRefreshableView() != null) {
                prv_job_list.getRefreshableView().reset();
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }

        mCommloadingView.setVisibility(View.GONE);
        page = 1;
        if (!isSearch) {
            mAdapter.setSelectedPosition(0);
        } else {
            mAdapter.setSelectedPosition(-1);
        }
        mData.clear();
        loadJobData();
    }

    @Override
    public void OnLoadMoreEvent(boolean isRetry) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            prv_job_list.onRefreshComplete();
            return;
        }
        page++;
        loadMode = RefreshMode.loadmore;
        loadJobData();
    }


    private void initTopBar() {
        fragment_title_bar.setTitle("职位选择");
        fragment_title_bar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                isSearch = true;
                ll_top_search_bar.setVisibility(View.VISIBLE);
                fragment_title_bar.setVisibility(View.GONE);
                tv_sign_bottom.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //重回职位列表
                isSearch = false;
                ll_top_search_bar.setVisibility(View.GONE);
                fragment_title_bar.setVisibility(View.VISIBLE);
                tv_sign_bottom.setVisibility(View.VISIBLE);
                onRefresh();
                break;
            case R.id.tv_sign_up:
            case R.id.tv_sign_bottom:
                //立即报名
                checkJobStatusAndSign();
                break;
        }
    }

    private void checkJobStatusAndSign() {
        int selectedPosition = mAdapter.getSelectedPosition();
        if (selectedPosition == -1 || selectedPosition > mData.size()) {
            ToastUtils.showEssayToast("请选择职位");
            return;
        }
        final F2fJobData f2fJobData = mData.get(selectedPosition);
        //校验是否可以报名
        String currentTime = System.currentTimeMillis() / 1000 + "";
        String md5=String.format("timestamp=%s&zwdm=%s",currentTime, f2fJobData.zwid);
        String sign = Md5Util.toSign(md5);
        statusParams.put("zwdm", f2fJobData.zwid);
        statusParams.put("timestamp", currentTime);
        statusParams.put("sign",sign);
        Gson gson = new Gson();
        String params = gson.toJson(statusParams);
        Subscription subscribe = RetrofitManager.getInstance().getService().checkF2fJobstatus(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<F2fJobStatusBean>() {

                    @Override
                    public void onCompleted() {

                        }
                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showEssayToast("出错啦，请稍后重试");
                        }

                        @Override
                        public void onNext(F2fJobStatusBean f2fJobStatusBean) {
                            if (f2fJobStatusBean.state.equals("1")){
                                //未招满
                                args.putString("htwyid", f2fJobData.htwyzwdm);
                                ConfirmOrderActivity.launch(mActivity, args);
                            }else {
                                ToastUtils.showEssayToast(f2fJobStatusBean.message);
                            }
                        }
                        });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }

    }

    @Override
    protected void onLoadData() {
        loadJobData();
    }

    private void loadJobData() {
        String currentTime = System.currentTimeMillis() / 1000 + "";
        String key="zwmc";
        final String md5;
        if (!TextUtils.isEmpty(value)) {
            md5 = String.format("key=%s&page=%s&rows=10&ssfb=%s&timestamp=%s&value=%s",key, page, ssfb, currentTime, value);
        } else {
            md5 = String.format("page=%s&rows=10&ssfb=%s&timestamp=%s", page, ssfb, currentTime);
        }
        LogUtils.e("test", md5);
        String sign = Md5Util.toSign(md5);
        LogUtils.e("test-sign", sign);
        if (!TextUtils.isEmpty(value)){
            params.put("key",key);

        }
        params.put("page", page);
        params.put("rows", 10);
        params.put("ssfb", ssfb);
        params.put("value", value);
        params.put("timestamp", currentTime);
        params.put("sign", sign);
        Gson gson = new Gson();
        String param = gson.toJson(params);
        LogUtils.e("test-param", param);
        Subscription subscribe = RetrofitManager.getInstance().getService().getF2fJobList(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<F2fJobBean>() {
                    @Override
                    public void onNext(F2fJobBean model) {
                        if (null != mCommloadingView) {
                            mCommloadingView.setVisibility(View.GONE);
                        }
                        if (null != prv_job_list) {
                            prv_job_list.onRefreshComplete();
                        }
                        if (model != null && model.code == 0) {
                            if (model.data != null && model.data.size() != 0) {
                                mData.addAll(model.data);
                                mAdapter.setData(mData);
                            }
                            if (model.data != null && loadMode == RefreshMode.loadmore && prv_job_list.getRefreshableView() != null) {
                                prv_job_list.getRefreshableView().checkloadMore(model.data.size());
                                prv_job_list.getRefreshableView().hideloading();
                            }
                            // 隐藏加载动画
                            prv_job_list.getRefreshableView().hideloading();
                            if (page == 1) {
                                // 下拉刷新后显示第一行
                                prv_job_list.getRefreshableView().scrollToPosition(0);
                            }
                        } else {
                            if (null != mCommloadingView) {
                                mCommloadingView.setVisibility(View.VISIBLE);
                                mCommloadingView.showServerError();
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mCommloadingView) {
                            if (NetUtil.isConnected())
                                mCommloadingView.showServerError();
                            else
                                mCommloadingView.showNetworkTip();
                        }
                        if (null != prv_job_list) prv_job_list.onRefreshComplete();
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);

    }
}
