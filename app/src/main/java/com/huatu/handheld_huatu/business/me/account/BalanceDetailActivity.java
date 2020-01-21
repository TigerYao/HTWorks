package com.huatu.handheld_huatu.business.me.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.adapter.EssayConsumeAdapter;
import com.huatu.handheld_huatu.business.me.bean.BalanceDetailData;
import com.huatu.handheld_huatu.business.me.bean.BalanceDetailResult;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.OnRefreshListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.RefreshListView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * 余额明细
 * Created by chq on 2017/8/30.
 */
public class BalanceDetailActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {
    private static final String TAG = "BalanceDetailActivity";
    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    @BindView(R.id.balance_selector_tv)
    TextView balance_selector_tv;
     @BindView(R.id.tv_recharge_record)
    TextView tv_recharge_record;
    @BindView(R.id.tv_consume_record)
    TextView tv_consume_record;
    @BindView(R.id.rlv_detail_record)
    RefreshListView rlv_detail_record;
    @BindView(R.id.error_view)
    CommonErrorView errorView;

    private PopupWindow mPopupWindowSelector;
    private CompositeSubscription compositeSubscription;
    private ArrayList<BalanceDetailResult> dataList=new ArrayList<>();

    private int courseType=0;
    private int mType = 1;
    private int mPage = 1;
    private AsyncTask<Void, Void, Void> mAsyncTask;

    private EssayConsumeAdapter mAdapter;
    private boolean hasMore=false;


    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_balance_detail;
    }

    @Override
    protected void onInitView() {
        initView();
        setListener();
    }

    @Override
    protected void onLoadData() {
        loadDetailRechargeData(1);
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        tv_recharge_record.setOnClickListener(this);
        tv_consume_record.setOnClickListener(this);
        balance_selector_tv.setOnClickListener(this);
        errorView.setOnClickListener(this);
    }

    private void initView() {
        ButterKnife.bind(this);
        initListView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

    }

    private void initListView() {
        mAdapter = new EssayConsumeAdapter(this);
        rlv_detail_record.setAdapter(mAdapter);
        rlv_detail_record.setOnRefreshListener(this);
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

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, BalanceDetailActivity.class);
        context.startActivity(intent);
    }
    /**
     * @param flag 1、无网络
     *             2、获取数据失败
     *             3、无数据
     */
    public void onLoadDataFailed(int flag) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (flag) {
            case 1:
                errorView.setErrorText("网络不太好，点击屏幕，刷新看看");
                errorView.setErrorImage(R.drawable.icon_common_net_unconnected);
                break;
            case 2:
                ToastUtils.showShort("获取数据失败");
                errorView.setErrorText("获取数据失败，点击刷新");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
            case 3:
                errorView.setErrorText("什么都没有");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
        }
    }
    //type为1是收入明细，type为2是课程支出
    private void loadDetailRechargeData(final int type) {
        if (!NetUtil.isConnected()){
            rlv_detail_record.setVisibility(View.GONE);
            onLoadDataFailed(1);
            ToastUtils.showEssayToast("网络未连接，请检查您的网络");
            return;
        }
        showProgress();
        ServiceProvider.getBalanceDetail(compositeSubscription,type,mPage,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                if (model.data!=null) {
                    rlv_detail_record.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    BalanceDetailData mBalanceDetailData = (BalanceDetailData) model.data;
                    convertData(type,mBalanceDetailData);
                    if (mBalanceDetailData.next == 1) {
                        mPage++;
                        hasMore = true;
                    } else {
                        hasMore = false;
                    }
                }else {
                    rlv_detail_record.setVisibility(View.GONE);
                    onLoadDataFailed(3);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                rlv_detail_record.setVisibility(View.GONE);
                onLoadDataFailed(2);
            }
        });
    }
    //将课程的收入和支出数据转换
    private void convertData(int type, BalanceDetailData mBalanceDetailData) {
        if (mBalanceDetailData!=null){
            if (type==1){
                //收入数据
                if (mBalanceDetailData.rechargeRes!=null && mBalanceDetailData.rechargeRes.size()!=0 && dataList!=null){
                    rlv_detail_record.setVisibility(View.VISIBLE);

                    for (BalanceDetailData.RechargeRes result : mBalanceDetailData.rechargeRes){
                        dataList.add(convertRecharge(result));
                    }
                } else {
                    rlv_detail_record.setVisibility(View.GONE);
                    onLoadDataFailed(3);
                }
            }else {
                //支出数据
                if (mBalanceDetailData.consumeRes!=null&&mBalanceDetailData.consumeRes.size()!=0&&dataList!=null){
                    rlv_detail_record.setVisibility(View.VISIBLE);
                    for (BalanceDetailData.ConsumeRes result : mBalanceDetailData.consumeRes){
                        dataList.add(convertConsume(result));
                    }
                } else {
                    rlv_detail_record.setVisibility(View.GONE);
                    onLoadDataFailed(3);
                }
            }

            mAdapter.setData(dataList);
        }else{
            rlv_detail_record.setVisibility(View.GONE);
            onLoadDataFailed(3);
        }

        }

    private BalanceDetailResult convertConsume(BalanceDetailData.ConsumeRes result) {
        BalanceDetailResult mBalanceDetailResult=new BalanceDetailResult();
        mBalanceDetailResult.name=result.consumeLog;
        mBalanceDetailResult.payMsg=result.MoneyReceipt;
        mBalanceDetailResult.payTime=result.PayDate;
        return mBalanceDetailResult;
    }



    private BalanceDetailResult convertRecharge(BalanceDetailData.RechargeRes result) {
        BalanceDetailResult mBalanceDetailResult=new BalanceDetailResult();
        mBalanceDetailResult.name=result.actionDetail;
        mBalanceDetailResult.payMsg=result.Amount;
        mBalanceDetailResult.payTime=result.OrderDate;
        return mBalanceDetailResult;
    }

    private void loadConsumeData(int type){
        //支出数据
            if (type==0){
                //课程支出
                loadDetailRechargeData(2);
            }else if (type==1){
                //申论支出
                loadEssayConsume();
            }else if(type==2){
                //导错题支出
                loadDeriveExerciseConsume();
            }
    }
    private void loadEssayConsume() {
        if (!NetUtil.isConnected()){
            rlv_detail_record.setVisibility(View.GONE);
            onLoadDataFailed(1);
            ToastUtils.showEssayToast("网络未连接，请检查您的网络");
            return;
        }
        showProgress();
        ServiceProvider.getEssayConsume(compositeSubscription, mPage,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                rlv_detail_record.setVisibility(View.VISIBLE);
                BalanceDetailData mData= (BalanceDetailData) model.data;
                if (mData.result!=null){
                    dataList.addAll(mData.result);
                }
                if (mData.next==1){
                    hasMore=true;
                    mPage++;
                }else {
                    hasMore=false;
                }
                mAdapter.setData(dataList);
                if(Method.isListEmpty(dataList)){
                    onLoadDataFailed(3);
                    rlv_detail_record.setVisibility(View.GONE);
                }else {
                    errorView.setVisibility(View.GONE);
                    rlv_detail_record.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                rlv_detail_record.setVisibility(View.GONE);
                onLoadDataFailed(2);

            }
        });
    }

    // 错题导出下载支出
    private void loadDeriveExerciseConsume() {
        if (!NetUtil.isConnected()){
            rlv_detail_record.setVisibility(View.GONE);
            onLoadDataFailed(1);
            ToastUtils.showEssayToast("网络未连接，请检查您的网络");
            return;
        }
        showProgress();
        ServiceProvider.getDeriveExerciseConsume(compositeSubscription, mPage,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgress();
                rlv_detail_record.setVisibility(View.VISIBLE);
                BalanceDetailData mData= (BalanceDetailData) model.data;
                if (mData.result!=null){
                    dataList.addAll(mData.result);
                }
                if (mData.next==1){
                    hasMore=true;
                    mPage++;
                }else {
                    hasMore=false;
                }
                mAdapter.setData(dataList);
                if(Method.isListEmpty(dataList)){
                    onLoadDataFailed(3);
                    rlv_detail_record.setVisibility(View.GONE);
                }else {
                    errorView.setVisibility(View.GONE);
                    rlv_detail_record.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgress();
                rlv_detail_record.setVisibility(View.GONE);
                onLoadDataFailed(2);

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_top_bar:
                BalanceDetailActivity.this.finish();
                break;
            case R.id.tv_consume_record://支出
                if (errorView!=null){
                    errorView.setVisibility(View.GONE);
                }
                if (mType == 1) {
                    mType = 2;
                    mPage = 1;
                    dataList.clear();
                    balance_selector_tv.setVisibility(View.VISIBLE);
                    if (courseType==0){
                        loadConsumeData(0);
                    }else if (courseType==1){
                        loadConsumeData(1);
                    }else {
                        loadConsumeData(2);
                    }
                    tv_consume_record.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv_consume_record.setBackgroundResource(R.drawable.textview_round_corners_right_red);
                    tv_recharge_record.setTextColor(ContextCompat.getColor(this, R.color.red120));
                    tv_recharge_record.setBackgroundResource(R.drawable.textview_rounded_corners_left_white);
                }
                break;
            case R.id.tv_recharge_record://收入
                if (errorView!=null){
                    errorView.setVisibility(View.GONE);
                }
                if (mType == 2) {
                    mType = 1;
                    mPage = 1;
                    dataList.clear();
                    rlv_detail_record.setVisibility(View.VISIBLE);
                    balance_selector_tv.setVisibility(View.INVISIBLE);
                    loadDetailRechargeData(1);
                    tv_recharge_record.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv_recharge_record.setBackgroundResource(R.drawable.textview_rounded_corners_left_red);
                    tv_consume_record.setTextColor(ContextCompat.getColor(this, R.color.red120));
                    tv_consume_record.setBackgroundResource(R.drawable.textview_round_corners_right_white);
                }
                break;
            case R.id.balance_selector_tv:
                initPopWindow();
                mPopupWindowSelector.showAsDropDown(balance_selector_tv, 70, 0);
                break;
            case R.id.error_view:
                errorView.setVisibility(View.GONE);
                mPage=1;
                dataList.clear();
                if (mType == 1) {
                    loadDetailRechargeData(1);
                } else {
                    if (courseType==0){
                        loadConsumeData(0);
                    }else if (courseType==1){
                        loadConsumeData(1);
                    }else {
                        loadConsumeData(2);

                    }
                }
                break;

        }

    }

    public void initPopWindow() {
            final LinearLayout mPopLayout = (LinearLayout) (mLayoutInflater.inflate(R.layout.balance_popwindow_layout, null));
            mPopupWindowSelector = new PopupWindow(mPopLayout, DisplayUtil.dp2px(107),
                    DisplayUtil.dp2px(141));
            mPopupWindowSelector.setFocusable(true);
            mPopupWindowSelector.setOutsideTouchable(true);
           final TextView tv_course = mPopLayout.findViewById(R.id.tv_course);
            final TextView tv_essay = mPopLayout.findViewById(R.id.tv_essay);
            final TextView tv_derive_exercise = mPopLayout.findViewById(R.id.tv_derive_exercise);
            tv_course.setText("课程");
            tv_essay.setText("申论");
            tv_derive_exercise.setText("下载");
            if (courseType==0){
                tv_course.setBackgroundColor(Color.parseColor("#3c464f"));
                tv_essay.setBackgroundColor(Color.parseColor("#00000000"));
                tv_derive_exercise.setBackgroundColor(Color.parseColor("#00000000"));
            }else if (courseType==1){
                tv_essay.setBackgroundColor(Color.parseColor("#3c464f"));
                tv_course.setBackgroundColor(Color.parseColor("#00000000"));
                tv_derive_exercise.setBackgroundColor(Color.parseColor("#00000000"));
            }else {
                tv_derive_exercise.setBackgroundColor(Color.parseColor("#3c464f"));
                tv_course.setBackgroundColor(Color.parseColor("#00000000"));
                tv_essay.setBackgroundColor(Color.parseColor("#00000000"));
            }
            mPopLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindowSelector.dismiss();
                }
            });
            tv_course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 0) {
                        courseType = 0;
                        mPage = 1;
                        balance_selector_tv.setText("课程");
                        tv_course.setBackgroundColor(Color.parseColor("#3c464f"));
                        tv_essay.setBackgroundColor(Color.parseColor("#00000000"));
                        tv_derive_exercise.setBackgroundColor(Color.parseColor("#00000000"));
                        dataList.clear();
                        loadConsumeData(0);
                        mPopupWindowSelector.dismiss();
                    } else {
                        mPopupWindowSelector.dismiss();
                    }
                }
            });
            tv_essay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 1) {
                        courseType = 1;
                        mPage = 1;
                        mPopupWindowSelector.dismiss();
                        balance_selector_tv.setText("申论");
                        tv_essay.setBackgroundColor(Color.parseColor("#3c464f"));
                        tv_course.setBackgroundColor(Color.parseColor("#00000000"));
                        tv_derive_exercise.setBackgroundColor(Color.parseColor("#00000000"));
                        rlv_detail_record.setVisibility(View.VISIBLE);
                        dataList.clear();
                        loadConsumeData(1);
                    } else {
                        mPopupWindowSelector.dismiss();
                    }
                }
            });
            tv_derive_exercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (courseType != 2) {
                        courseType = 2;
                        mPage = 1;
                        mPopupWindowSelector.dismiss();
                        balance_selector_tv.setText("下载");
                        tv_derive_exercise.setBackgroundColor(Color.parseColor("#3c464f"));
                        tv_course.setBackgroundColor(Color.parseColor("#00000000"));
                        tv_essay.setBackgroundColor(Color.parseColor("#00000000"));
                        rlv_detail_record.setVisibility(View.VISIBLE);
                        dataList.clear();
                        loadConsumeData(2);
                    } else {
                        mPopupWindowSelector.dismiss();
                    }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onDownPullRefresh() {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (NetUtil.isConnected()) {
                    SystemClock.sleep(1000);
                    mPage=1;
                    dataList.clear();
                    if (mType == 1) {
                        loadDetailRechargeData(1);
                    } else {
                        if (courseType==0){
                            loadConsumeData(0);
                        }else if (courseType==1){
                            loadConsumeData(1);
                        }else {
                            loadConsumeData(2);

                        }
                    }
                } else {
                    SystemClock.sleep(1000);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                    if (rlv_detail_record != null) {
                        rlv_detail_record.hideHeaderView();
                    }


            }
        };
        mAsyncTask.execute(new Void[]{});
    }


    @Override
    public void onLoadingMore() {
        if (hasMore){
            rlv_detail_record.hideFooterView();
            if (mType == 1) {
                loadDetailRechargeData(1);
            } else {
                if (courseType==0){
                    loadConsumeData(0);
                }else if (courseType==1){
                    loadConsumeData(1);
                }else {
                    loadConsumeData(2);

                }

            }
        }else {
            CommonUtils.showToast("没有更多内容了");
            rlv_detail_record.hideFooterView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
