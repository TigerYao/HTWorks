package com.huatu.handheld_huatu.business.essay.mainfragment.fragment_single;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.SingleExerciseAdapter;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseTabData;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.XListViewNestedS;
import com.ogaclejapan.v4.FragmentPagerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 单题列表
 */
public class EssayListPaperFragment extends BaseFragment implements View.OnClickListener, XListViewNestedS.IXListViewListener {

    private static final String TAG = "EssayListPaperFragment";

    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.lv_my_single)
    XListViewNestedS lv_my_single;
    @BindView(R.id.rl_select_type)
    RelativeLayout rl_select_type;
    @BindView(R.id.tv_select)
    TextView tv_select;
    @BindView(R.id.lv_son_title)
    ListView lv_son_title;
    @BindView(R.id.fl_bc)
    FrameLayout fl_bc;

    private SingleExerciseTabData mData;
    private SingleExerciseAdapter adapter;
    private ArrayList<SingleExerciseResult> dataList = new ArrayList<>();
    private int mPage = 1;          // 当前页数
    private int next = 1;           // 是否还有下一页
    private int mTitleid;
    private int mSel;

    private CustomLoadingDialog mDailyDialog;
    private boolean isShow = false;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave_fail) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        }
        return true;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.single_exam_list_efragment;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onLoadData() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.clear();
        mPage = 1;
        loadData(false);
    }

    private void initView() {
        adapter = new SingleExerciseAdapter(mActivity);
        lv_my_single.setAdapter(adapter);
        lv_my_single.setHeaderDividersEnabled(false);
        lv_my_single.setFooterViewVisible(false);
        lv_my_single.setPullLoadEnable(false);
        lv_my_single.setPullRefreshEnable(true);
        lv_my_single.setXListViewListener(this);
        lv_my_single.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (position - 1 >= dataList.size()) {
                    return;
                }
                final SingleExerciseResult mResult = dataList.get(position - 1);
                if (mResult.essayQuestionBelongPaperVOList.isEmpty()) {
                    ToastUtils.showEssayToast("暂无试题，请稍后重试");
                    return;
                }
                StudyCourseStatistic.clickEssaySingle("标准答案", mResult.similarId + "");

                EssayRoute.navigateToAnswer(mActivity, compositeSubscription, true, mResult, null, null);
            }
        });
    }

    private void initData() {
        mData = null;
        mData = (SingleExerciseTabData) args.getSerializable("TITLES");
        if (mData != null) {
            mTitleid = mData.id;
        }
    }

    private void initListener() {
        fl_bc.setOnClickListener(this);
        layout_nodata.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
    }

    private void loadData(final boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            if (lv_my_single != null) {
                lv_my_single.stopLoadMore();
                lv_my_single.stopRefresh();
                lv_my_single.setVisibility(View.GONE);
            }
            if (layout_nodata != null) {
                layout_nodata.setVisibility(View.GONE);
            }
            if (layout_net_error != null) {
                layout_net_error.setVisibility(View.GONE);
            }
            if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
                ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            }
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.VISIBLE);
            }
            return;
        } else {
            if (lv_my_single != null) {
                lv_my_single.setVisibility(View.VISIBLE);
            }
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.GONE);
            }
        }
        mSel = SpUtils.getSelectPoint();
        int position = FragmentPagerItem.getPosition(getArguments());
        if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS && !isRefresh && mSel == position) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
        }

        ServiceProvider.getSingleExercise(compositeSubscription, mTitleid, mPage, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                if (layout_net_error != null) {
                    layout_net_error.setVisibility(View.GONE);
                }
                SingleExerciseData mData = (SingleExerciseData) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                if (lv_my_single != null) {
                    lv_my_single.stopLoadMore();
                    lv_my_single.stopRefresh();
                }
                if (mData.result != null) {
                    dataList.addAll(mData.result);
                }
                if (dataList.isEmpty()) {
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (lv_my_single != null) {
                        lv_my_single.setVisibility(View.VISIBLE);
                    }
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.GONE);
                    }
                }
                adapter.setData(dataList);
                next = mData.next;
                if (mData.next == 1) {
                    mPage++;
                    lv_my_single.setPullLoadEnable(true);
                } else {
                    lv_my_single.setPullLoadEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                dismissLoadingDialog();
                if (lv_my_single != null) {
                    lv_my_single.stopLoadMore();
                    lv_my_single.stopRefresh();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_select:
                if (!isShow) {
                    Drawable drawable = getResources().getDrawable(R.mipmap.single_essay_list_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tv_select.setCompoundDrawables(null, null, drawable, null);
                    tv_select.setCompoundDrawablePadding(DisplayUtil.dp2px(6));
                    fl_bc.setVisibility(View.VISIBLE);
                    lv_son_title.setVisibility(View.VISIBLE);
                    isShow = true;
                } else {
                    Drawable drawable1 = getResources().getDrawable(R.mipmap.single_essay_list_down);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    tv_select.setCompoundDrawables(null, null, drawable1, null);
                    tv_select.setCompoundDrawablePadding(DisplayUtil.dp2px(6));
                    fl_bc.setVisibility(View.GONE);
                    lv_son_title.setVisibility(View.GONE);
                    isShow = false;
                }
                break;
            case R.id.layout_nodata:
                layout_nodata.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                loadData(false);
                break;
        }
    }

    public static EssayListPaperFragment newInstance() {
        return new EssayListPaperFragment();
    }

    @Override
    public void onRefresh() {
        dataList.clear();
        if (layout_nodata != null) {
            layout_nodata.setVisibility(View.GONE);
        }
        mPage = 1;
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        loadData(true);
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomLoadingDialog(mActivity);

        }
        lv_my_single.post(new Runnable() {
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
        }

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        onLoadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
