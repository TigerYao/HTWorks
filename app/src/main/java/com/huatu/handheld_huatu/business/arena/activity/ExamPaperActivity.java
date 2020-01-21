package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Intent;
import android.support.annotation.UiThread;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseRecycleViewActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaZhentiYanlianAdapter;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.mvpmodel.arena.ExamPagerItem;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by saiyuan on 2016/12/16.
 * 综合应用不能下载试卷（2019/10/02开始能下载了）
 */
public class ExamPaperActivity extends BaseRecycleViewActivity<ExamPagerItem.ExamPaperResult> {

    private ExamAreaItem examAreaData;
    private int page = 1;
    private int paperType = 1;
    private int requestType;
    private int subject;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS
                || event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS) {
            // 行测交卷、保存成功
            onRefresh();
        }
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 0);
            examAreaData = (ExamAreaItem) originIntent.getSerializableExtra("examAreaData");
        }
        if (examAreaData == null) {
            finish();
            return;
        }

        super.onInitView();
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN) {
            subject = SignUpTypeDataCache.getInstance().getCurSubject();
        } else if (requestType == ArenaConstant.START_NTEGRATED_APPLICATION) {
            subject = Type.PB_ExamType.NTEGRATED_APPLICATION;
        }
        // 真题演练需要侧滑，综合应用页需要侧滑
        // 侧滑的实现，是配合com.nalan.swipeitem.recyclerview.SwipeItemLayout使用
        listView.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(ExamPaperActivity.this));
        topActionBar.setTitle(examAreaData.areaName);
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                ExamPaperActivity.this.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    protected void onLoadData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getData(true);
    }

    @Override
    public void onLoadMore() {
        getData(false);
    }

    @Override
    public void initAdapter() {
        mAdapter = new ArenaZhentiYanlianAdapter(ExamPaperActivity.this, dataList, compositeSubscription, subject);
    }

    @Override
    public void showTips() {
        //行测，事业单位-公基和职测，教师，招警有试卷下载
        if (SpUtils.getDownRealPaperTip()) {
            if (SignUpTypeDataCache.getInstance().getCurSubject() == Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST  // 公务员行测
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.PUBLIC_BASE            // 事业单位公基
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.JOB_TEST               // 事业单位职测
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.PUBLIC_SECURITY         // 公安招警
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.TEACHER_T) {            // 教师
                if (rl_tip != null) {
                    rl_tip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1033) {
            onLoadData();
        }
    }

    private void getData(final boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            listView.onRefreshComplete();
            return;
        }
        int reqPage = 1;
        if (!isRefresh) {
            reqPage = page;
        } else {
            page = 1;
        }
        showProgressBar();
        ServiceProvider.getRealExamAreaPaperList(compositeSubscription, reqPage,
                pageSize, examAreaData.area, subject, paperType, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        ExamPagerItem item = (ExamPagerItem) model.data;
                        onDataSuccess(item, isRefresh);
                    }

                    @Override
                    public void onError(final Throwable e) {
                        ExamPaperActivity.this.onLoadDataFailed();
                    }
                });
    }

    @UiThread
    private void onDataSuccess(final ExamPagerItem item, final boolean isRefresh) {
        if (Method.isActivityFinished(this)) {
            return;
        }
        dismissProgressBar();
        listView.onRefreshComplete();
        page++;
        if (item.resutls != null && item.resutls.size() > 0) {
            if (isRefresh) {
                listView.getRefreshableView().reset();
                dataList.clear();
            }
            dataList.addAll(item.resutls);
        }
        if (dataList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
//        if (item.resutls == null || item.resutls.isEmpty() || item.resutls.size() < pageSize) {
//            listView.setPullLoadEnable(false);
//        } else {
//            listView.setPullLoadEnable(true);
//        }

        listView.getRefreshableView().checkloadMore(item.resutls != null ? item.resutls.size() : 0);
        listView.getRefreshableView().hideloading();
        mAdapter.notifyDataSetChanged();
        if (isRefresh) {
            listView.getRefreshableView().scrollToPosition(0);
        }
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rl_tip != null) {
            rl_tip.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
