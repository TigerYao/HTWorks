package com.huatu.handheld_huatu.business.essay.mainfragment;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.SingleExerciseAdapter;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.XListViewNestedS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by ht-acg on 2018/6/29.
 * 申论文章写作列表页
 */
public class ArgumentEssay extends BaseFragment implements XListViewNestedS.IXListViewListener, View.OnClickListener {

    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.fragment_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.xlv_essay_argument)
    XListViewNestedS xlv_essay_argument;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;

    private List<SingleExerciseData> mData = new ArrayList<>();
    private ArrayList<SingleExerciseResult> dataList = new ArrayList<>();
    private SingleExerciseAdapter adapter;
    private CustomConfirmDialog dialog;
    private CustomLoadingDialog mDailyDialog;
    private int mPage = 1;
    private boolean toHome;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_argument_essay;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        return true;

    }

    @Override
    protected void onInitView() {
        super.onInitView();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        toHome = args.getBoolean("toHome");
        initTitle();
        initView();
        initListener();
    }

    private void initListener() {
        layout_net_error.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
        layout_nodata.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                loadData(true);
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                loadData(true);
                break;
            case R.id.layout_nodata:
                layout_nodata.setVisibility(View.GONE);
                loadData(true);
                break;
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        dataList.clear();
        loadData(false);
    }

    private void loadData(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            xlv_essay_argument.setVisibility(View.GONE);
            layout_net_error.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
            layout_net_unconnected.setVisibility(View.VISIBLE);
            ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            return;
        } else {
            xlv_essay_argument.setVisibility(View.VISIBLE);
            layout_net_unconnected.setVisibility(View.GONE);
        }
        if (!isRefresh) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
        }
        ServiceProvider.getSingleExercise(compositeSubscription, 5, mPage, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                SingleExerciseData mData = (SingleExerciseData) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                xlv_essay_argument.setVisibility(View.VISIBLE);
                xlv_essay_argument.stopLoadMore();
                xlv_essay_argument.stopRefresh();
                if (mData.result != null) {
                    dataList.addAll(mData.result);
                }
                if (dataList.isEmpty()) {
                    tv_no_data.setText("暂无题目");
                    tv_no_data.setVisibility(View.VISIBLE);
//                    lv_my_check.setVisibility(View.GONE);
                } else {
                    xlv_essay_argument.setVisibility(View.VISIBLE);
                    tv_no_data.setVisibility(View.GONE);
                }
                adapter.setData(dataList);
                if (mData.next == 1) {
                    mPage++;
                    xlv_essay_argument.setPullLoadEnable(true);
                } else {
                    xlv_essay_argument.setPullLoadEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                dismissLoadingDialog();
                xlv_essay_argument.stopLoadMore();
                xlv_essay_argument.stopRefresh();
                xlv_essay_argument.setVisibility(View.GONE);
                layout_net_error.setVisibility(View.VISIBLE);
                layout_net_unconnected.setVisibility(View.GONE);
                layout_nodata.setVisibility(View.GONE);
            }
        });
    }

    private void initTitle() {
        topActionBar.setTitle("文章写作");
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (toHome) {
                    MainTabActivity.newIntent(mActivity);
                }
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    private void initView() {
        adapter = new SingleExerciseAdapter(mActivity);
        xlv_essay_argument.setAdapter(adapter);
        xlv_essay_argument.setHeaderDividersEnabled(false);
        xlv_essay_argument.setFooterViewVisible(false);
        xlv_essay_argument.setPullLoadEnable(false);
        xlv_essay_argument.setPullRefreshEnable(true);
        xlv_essay_argument.setXListViewListener(this);
        xlv_essay_argument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (position - 1 >= dataList.size()) {
                    return;
                }
                if(!CommonUtils.checkLogin(mActivity)){  return;   }
                final SingleExerciseResult mResult = dataList.get(position - 1);
                if (mResult.essayQuestionBelongPaperVOList.isEmpty()) {
                    ToastUtils.showEssayToast("暂无试题，请稍后重试");
                    return;
                }
                StudyCourseStatistic.clickEssaySingle("文章写作", mResult.similarId + "");

                EssayRoute.navigateToAnswer(mActivity, compositeSubscription, true, mResult, null, null);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            xlv_essay_argument.stopRefresh();
            return;
        }
        dataList.clear();
        tv_no_data.setVisibility(View.GONE);
        mPage = 1;
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            xlv_essay_argument.stopLoadMore();
            return;
        }
        loadData(true);
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomLoadingDialog(mActivity);

        }
        mDailyDialog.show();

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
    public boolean onBackPressed() {
        if (toHome) {
            MainTabActivity.newIntent(mActivity);
        }
        mActivity.finish();
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
//        Ntalker.getInstance().logout();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }


}
