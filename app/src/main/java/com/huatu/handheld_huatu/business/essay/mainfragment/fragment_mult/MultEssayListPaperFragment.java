package com.huatu.handheld_huatu.business.essay.mainfragment.fragment_mult;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.MultiExerciseAdapter;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayMultiExerciseDataCache;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.XListView;
import com.ogaclejapan.v4.FragmentPagerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 套题列表
 */
public class MultEssayListPaperFragment extends BaseFragment implements View.OnClickListener, XListView.IXListViewListener {

    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.lv_multi_exercise)
    XListView lv_multi_exercise;

    private MultiExerciseAdapter adapter;

    private long areasId;
    private int mPage = 1;
    private MultiExerciseData mData;
    private List<MultiExerciseResult> dataList = new ArrayList<>();

    @Override
    public int onSetRootViewId() {
        return R.layout.mult_exam_list_efragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getMultSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getMultSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getMultSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave_fail) {
            int position = FragmentPagerItem.getPosition(getArguments());
            if (position == SpUtils.getMultSelectPoint()) {
                dataList.clear();
                mPage = 1;
                loadData(false);
            }
        }
        return true;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initListener();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (args != null) {
            areasId = args.getLong("ID");
        }
        initData(areasId);

    }

    private void initData(long arenaId) {
        EssayMultiExerciseDataCache cache = EssayMultiExerciseDataCache.getInstance();
        mData = cache.getCache(arenaId);
        if (mData == null) {
            dataList.clear();
            loadData(false);
        } else {
            dataList = mData.result;
            if (dataList != null) {
                adapter.setData(dataList);
                if (layout_nodata != null) {
                    layout_nodata.setVisibility(View.GONE);
                }
                if (lv_multi_exercise != null) {
                    lv_multi_exercise.setVisibility(View.VISIBLE);
                }
            } else {
                if (layout_nodata != null) {
                    layout_nodata.setVisibility(View.VISIBLE);
                }
            }

        }
    }


    private void loadData(final boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            if (lv_multi_exercise != null) {
                lv_multi_exercise.stopRefresh();
                lv_multi_exercise.stopLoadMore();
                lv_multi_exercise.setVisibility(View.GONE);
            }
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.VISIBLE);
            }
            if (layout_nodata != null) {
                layout_nodata.setVisibility(View.GONE);
            }
            if (layout_net_error != null) {
                layout_net_error.setVisibility(View.GONE);
            }
            ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            return;
        } else {
            if (layout_net_unconnected != null) {
                layout_net_unconnected.setVisibility(View.GONE);
            }
            if (lv_multi_exercise != null) {
                lv_multi_exercise.setVisibility(View.VISIBLE);
            }
        }
        int position = FragmentPagerItem.getPosition(getArguments());
        if (!isRefresh && position == 0) {
            mActivity.showProgress();
        }
        ServiceProvider.getMultiExercise(compositeSubscription, areasId, mPage, 20, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                if (lv_multi_exercise != null) {
                    lv_multi_exercise.stopLoadMore();
                    lv_multi_exercise.stopRefresh();
                }
                if (model != null && model.data != null) {
                    mData = (MultiExerciseData) model.data;
                    setSelPos(areasId);

                }
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                if (mData.result != null) {
                    dataList.addAll(mData.result);
                }
                if (!dataList.isEmpty()) {
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.GONE);
                    }
                    if (lv_multi_exercise != null) {
                        lv_multi_exercise.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (layout_nodata != null) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    }
                }
                adapter.setData(dataList);
                if (mData.next == 1) {
                    mPage++;
                    if (lv_multi_exercise != null) {
                        lv_multi_exercise.setPullLoadEnable(true);
                    }
                } else {
                    if (lv_multi_exercise != null) {
                        lv_multi_exercise.setPullLoadEnable(false);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                if (lv_multi_exercise != null) {
                    lv_multi_exercise.stopRefresh();
                    lv_multi_exercise.stopLoadMore();
                }
            }
        });


    }

    private void setSelPos(final long areasId) {
        EssayMultiExerciseDataCache mCache = EssayMultiExerciseDataCache.getInstance();
        mCache.addCache(areasId, mData);
    }

    private void initListener() {
        layout_net_error.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
    }

    private void initView() {
        adapter = new MultiExerciseAdapter(mActivity);
        lv_multi_exercise.setAdapter(adapter);
        lv_multi_exercise.setHeaderDividersEnabled(false);
        lv_multi_exercise.setFooterViewVisible(false);
        lv_multi_exercise.setPullLoadEnable(false);
        lv_multi_exercise.setPullRefreshEnable(true);
        lv_multi_exercise.setXListViewListener(this);
        lv_multi_exercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (position - 1 >= dataList.size()) {
                    return;
                }
                if(!CommonUtils.checkLogin(mActivity)){  return;   }
                final MultiExerciseResult mResult = dataList.get(position - 1);
                StudyCourseStatistic.clickEssayMulti("套题", mResult.paperName, mResult.paperId + "");

                EssayRoute.navigateToAnswer(mActivity, compositeSubscription, false, null, mResult, null);
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_nodata:
                layout_nodata.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                loadData(false);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (dataList != null) {
            dataList.clear();
        }
        if (layout_nodata != null) {
            layout_nodata.setVisibility(View.GONE);
        }
        if (lv_multi_exercise != null) {
            lv_multi_exercise.setVisibility(View.VISIBLE);
        }
        mPage = 1;
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        if (layout_nodata != null) {
            layout_nodata.setVisibility(View.GONE);
        }
        if (lv_multi_exercise != null) {
            lv_multi_exercise.setVisibility(View.VISIBLE);
        }
        loadData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EssayMultiExerciseDataCache.getInstance().clearCache();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static MultEssayListPaperFragment newInstance() {
        return new MultEssayListPaperFragment();
    }
}
