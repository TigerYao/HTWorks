package com.huatu.handheld_huatu.business.matches.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.BottomDialog;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.business.matches.adapter.AreaAdapter;
import com.huatu.handheld_huatu.business.matches.adapter.ScListAdapter;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBeanNew;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CalendarReminderUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * SimulationContest SC 模考大赛（新）
 * 主页，显示报名信息
 * <p>
 * 注意，刷新数据
 */
public class SCMainChildFragmentNew extends AbsSimulationContestFragment {

    private final static int oneHour = 60 * 60 * 1000;
    private final static int fiveMin = 5 * 60 * 1000;
    private final static int thrfenMin = 30 * 60 * 1000;

    private long reqTime;                                   // 记录当前点击的时间，如果与上一次点击的时间间隔超过五分钟，就重新访问网络，刷新数据

    // 多个Fragment加载控件通过ButterKnife加载有问题所以改成findViewById
//    @BindView(R.id.sr_layout)
    private SwipeRefreshLayout refreshLayout;                       // 刷新
    private RecyclerView recyclerView;                              // 列表

    private CommonErrorView errorView;                              // 无数据提示

    //    private int paperType;                                  // type 0、行测 1、申论
    private int subjectId = 1;                              // 科目Id

    private boolean isTimeStart;                            // 定时器已经开启

    private ArrayList<SimulationScDetailBean.Area> areas;    // 报名地区信息

    private ArrayList<SimulationScDetailBean> mSimulationContestDetailBeans;        // 模考列表数据
    private ScListAdapter scListAdapter;

    private SimulationScDetailBean clickedBean;             // 点击了的模考

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_simulation_contest_child_main_new_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event == null || mPresenter == null || event.typeExObject == null) {
            return;
        }
        super.onEventUIUpdate(event);
        if (isEssay() && event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_DETAIL_DATA_ESSAY) {                   // 获取申论模考详情成功
            refreshViews();
        } else if (isEssay() && event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ESSAY) {       // 无申论模考大赛
            onLoadDataFailed(3);
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_SHOW_PROGRESS_BAR) {
            mActivity.showProgress();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR) {
            mActivity.hideProgress();
        } else if (isEssay() && (event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_ESSAY_SAVE_SUCCESS
                || event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_EXAM_COMP_ESSAY)) {                           // 申论交卷/保存之后
            getData();
        }
        // mPresenter.getSimulationEssayScDetail()          获取申论模考大赛内容
        //                    getArenaData();;   申论模考大赛内容
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        if (!isEssay() && (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS
                || event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS)) {        // 行测交卷/保存成功后
            getData();
        }
    }

    @Override
    protected void onInitView() {
        if (args != null) {
            subjectId = args.getInt("subjectId", 1);
        }

        refreshLayout = rootView.findViewById(R.id.sr_layout);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        errorView = rootView.findViewById(R.id.error_view);

        areas = new ArrayList<>();

        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                    if (refreshLayout != null) {
                        refreshLayout.setRefreshing(false);
                    }
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mSimulationContestDetailBeans = new ArrayList<>();

        scListAdapter = new ScListAdapter(getContext(), mSimulationContestDetailBeans, new ScListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int type, int position) {
                if (position >= mSimulationContestDetailBeans.size() || position < 0) return;
                clickedBean = mSimulationContestDetailBeans.get(position);
                if (clickedBean == null) return;
                switch (type) {
                    case ScListAdapter.OnItemClickListener.AREA:        // 点击地区
                        int status = clickedBean.status;
                        if (((status > 1 && status < 5) || status == 8) && clickedBean.enrollFlag != 1
                                && clickedBean.areaList != null && clickedBean.areaList.size() > 1) {
                            showAreaSign();
                        }
                        break;
                    case ScListAdapter.OnItemClickListener.SIGN:        // 点击报名、考试按钮
                        reaBtnClick();
                        break;
                    case ScListAdapter.OnItemClickListener.CLASS:       // 点击课程
                        if (CommonUtils.isFastDoubleClick()) return;
                        goClass();
                        break;
                    case ScListAdapter.OnItemClickListener.GIFT:        // 点击礼物盒
                        EventBus.getDefault().post(new BaseMessageEvent<SimulationContestMessageEvent>(SimulationContestMessageEvent.SHOW_GIFT_DESCRIBE));
                        break;
                    case ScListAdapter.OnItemClickListener.ADDCALEND:
                        addCalendar();
                        break;
                }
            }
        });
        recyclerView.setAdapter(scListAdapter);
        super.onInitView();
    }

    @Override
    protected void getData() {
        if (mPresenter != null) {
            // 获取模考大赛详情
            if (!NetUtil.isConnected()) {
                onLoadDataFailed(0);
                ToastUtils.showMessage("无网络连接！");
                return;
            }
            if (mPresenter != null) {
                if (isEssay()) {     // 如果是申论就单独走申论的接口
                    mPresenter.getSimulationEssayScDetail();
                } else {                                                                // 否则都走行测（题库的接口）
                    getArenaData();
                }
            }
        }
    }

    /**
     * 网络获取行测模考信息
     * 申论的再mPresenter中获取
     */
    private void getArenaData() {
        mActivity.showProgress();
        ServiceProvider.getSimulationContestDetailNew(compositeSubscription, 100, 1, subjectId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                ToastUtils.showShort("获取数据失败");
                onLoadDataFailed(2);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                SimulationScDetailBeanNew data = (SimulationScDetailBeanNew) model.data;
                if (data == null || data.list == null || data.list.size() == 0) {
                    onLoadDataFailed(3);
                } else {
                    // 由于行测、行测模考使用的是同一个页面
                    // 而行测接口是新接口，新数据，所以，对应成旧数据显示
                    ArrayList<SimulationScDetailBeanNew.ScInfo> beans = data.list;
                    mSimulationContestDetailBeans.clear();
                    for (SimulationScDetailBeanNew.ScInfo a : beans) {
                        SimulationScDetailBean b = new SimulationScDetailBean();
                        b.areaList = a.areaList;
                        b.stage = a.stage;
                        b.status = a.status;
                        if (a.courseInfo != null) {                                 // 字段不一样
                            b.courseId = a.courseInfo.classId;
                            b.courseInfo = a.courseInfo.courseTitle;
                        }
                        b.enterLimitTime = a.enterLimitTime;
                        b.endTime = a.endTime;
                        b.enrollCount = a.enrollCount;
                        b.essayEndTime = a.essayEndTime;
                        b.essayPaperId = a.essayPaperId;
                        b.essayStartTime = a.essayStartTime;
                        b.iconUrl = a.iconUrl;
                        b.instruction = a.instruction;
                        b.name = a.name;
                        b.paperId = a.matchId;                                      // 字段名称不一样
                        b.startTime = a.startTime;
                        b.subject = a.subject;
                        b.tag = a.tag;
                        b.timeInfo = a.timeInfo;
                        b.enrollFlag = a.enrollFlag;
                        b.flag = a.flag;
                        SimulationScDetailBean.UserMetaEntity bu = new SimulationScDetailBean.UserMetaEntity();
                        SimulationScDetailBeanNew.ScInfo.UserMetaEntity au = a.userMeta;
                        if (au != null) {
                            bu.id = au.id;
                            bu.paperId = au.matchId;                                // 字段名称不一样
                            bu.positionCount = au.positionCount;
                            bu.positionId = au.positionId;
                            bu.positionName = au.positionName;
                            bu.practiceId = au.practiceId;
                            bu.userId = au.userId;
                        }
                        b.userMeta = bu;
                        mSimulationContestDetailBeans.add(b);
                    }
                    setData();
                }
            }
        });
    }

    private void refreshViews() {
        if (mPresenter != null) {
            if (isEssay()) {                     // 申论
                mSimulationContestDetailBeans.clear();
                mSimulationContestDetailBeans.addAll(mPresenter.mSimulationContestEssayDetailBeans);
            }
            if (mSimulationContestDetailBeans != null && mSimulationContestDetailBeans.size() > 0) {
                setData();
            } else {
                onLoadDataFailed(3);
            }
        }
    }

    private void setData() {

        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }

        scListAdapter.notifyDataSetChanged();

        startTimer();
    }

    /**
     * 开启定时，更新考试状态，每分钟刷新    (1, TimeUnit.MINUTES)
     */
    private void startTimer() {
        if (isTimeStart || mSimulationContestDetailBeans == null) return;
        isTimeStart = true;
        timerSubscription = Observable.interval(1, TimeUnit.MINUTES)
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
                        scExamStateUpdate();
                    }
                });
    }

    private void stopTimer() {
        isTimeStart = false;
        if (timerSubscription != null && !timerSubscription.isUnsubscribed()) {
            timerSubscription.unsubscribe();
            timerSubscription = null;
        }
    }

    /*
        status
        1：未报名
        2：已报名
        3：开始考试-置灰-不可用
        4：开始考试
        5：无法考试
        6：可查看报告
        7：未出报告
        8：未交卷，可以继续做题
        9：未报名且错过报名

        status = 1 未报名
        status = 2 stage = 1 已报名
        status = 3 stage = 1 开始行测考试 (置灰)        还不能进入
        status = 4 stage = 1 开始行测考试（置红）        可以进入考试
        status = 5 stage = 1 开始行测考试（置灰）        时间错过
        status = 6 stage = 1 查看行测报告
        status = 7 stage = 1 查看行测报告（置灰）
        status = 8 stage = 1 继续考试（行测）

        status = 3 stage = 2 开始申论考试(置灰)         还未开始
        status = 4 stage = 2 开始申论考试（置红）       进入考试
        status = 5 stage = 2 开始申论考试（置灰）       时间错过
        status = 6 stage = 2 查看申论报告
        status = 7 stage = 2 查看申论报告（置灰）
        status = 8 stage = 2 继续考试（申论）

        status = 9 停止报名
     */
    private void scExamStateUpdate() {
        boolean isChange = false;
        for (SimulationScDetailBean mSimulationContestDetailBean : mSimulationContestDetailBeans) {
            long currentTimeMillis = System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime;
            if (mSimulationContestDetailBean != null) {
                int state = mSimulationContestDetailBean.stage;
                long startTime = 0;
                long endTime = 0;
                if (state == 1) {       // 行测
                    startTime = mSimulationContestDetailBean.startTime;
                    endTime = mSimulationContestDetailBean.endTime;
                } else if (state == 2) {    // 申论
                    startTime = mSimulationContestDetailBean.essayStartTime;
                    endTime = mSimulationContestDetailBean.essayEndTime;
                }
                int updateStatus = mSimulationContestDetailBean.status;

                if (mSimulationContestDetailBean.userMeta != null && mSimulationContestDetailBean.userMeta.positionId != 0) {         // 报名了
                    if (mSimulationContestDetailBean.enterLimitTime <= 0) {                                                            // 如果没有指定可提前进入的时间
                        if (mSimulationContestDetailBean.userMeta.practiceId > 0) {                                                             // 未交卷
                            if (updateStatus != 7 && updateStatus != 6) {                                                                           // 不是查看报告状态
                                if (endTime > currentTimeMillis) {                                                                                      // 没到考试结束时间，就显示继续考试
                                    updateStatus = 8;
                                } else {                                                                                                                // 超过结束时间，就显示时间错过
                                    updateStatus = 5;
                                }
                            }
                        } else if (startTime - currentTimeMillis < oneHour && startTime - currentTimeMillis > fiveMin) {                        // 报名了 & 开考前一小时 到 五分钟  "开始考试"置灰无法进入
                            updateStatus = 3;
                        } else if (startTime - currentTimeMillis < fiveMin && currentTimeMillis - startTime < thrfenMin) {                      // 报名了 & 开考前5分钟之内开始考试30分钟之内 "开始考试"置红可进入
                            updateStatus = 4;
                        } else if (currentTimeMillis - startTime > thrfenMin || endTime < currentTimeMillis) {                                  // 报名了 & 没有进入考试创建答题卡 开始超过三十分钟 "时间错过"置灰无法进入
                            updateStatus = 5;
                        }
                    } else {                                                                                                                // 指定了可提前进入的时间
                        int enterLimitTime = mSimulationContestDetailBean.enterLimitTime * 60 * 1000;  // 毫秒值
                        if (mSimulationContestDetailBean.userMeta.practiceId > 0) {                                                             // 继续考试
                            if (updateStatus != 7 && updateStatus != 6) {                                                                             // 不是查看报告
                                if (endTime > currentTimeMillis) {                                                                                      // 考试没结束 --> 继续考试
                                    updateStatus = 8;
                                } else {                                                                                                                // 考试结束 --> 错过考试
                                    updateStatus = 5;
                                }
                            }
                        } else if (startTime - currentTimeMillis < enterLimitTime && currentTimeMillis - startTime < thrfenMin) {            // 指定时间到开始考试半小时之内 --> 开始考试（置红）
                            updateStatus = 4;
                        } else if (currentTimeMillis - startTime > thrfenMin || endTime < currentTimeMillis) {                               // 考试开始后30分钟一次没有进入（没继续考试） --> 时间错过
                            updateStatus = 5;
                        }
                    }
                } else {                                                                                                                // 未报名
                    if (currentTimeMillis - startTime > thrfenMin) {                                                                        // 考试开始30分钟后，停止报名
                        updateStatus = 9;
                    }
                }
                if (mSimulationContestDetailBean.status != updateStatus) {
                    mSimulationContestDetailBean.status = updateStatus;
                    isChange = true;
                }
            }
        }
        if (isChange && scListAdapter != null) {
            scListAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.tv_past)
    public void goToPast() {
//        ToastUtils.showMessage("去模考历史");
        Bundle bundle = new Bundle();
        bundle.putInt("subjectId", subjectId);
        BaseFrgContainerActivity.newInstance(mActivity, NewScHistoryPaperListFragment.class.getName(), bundle);
    }

    @OnClick(R.id.error_view)
    public void onClickErrView() {
        getData();
    }

    /**
     * 去查看课程
     */
    private void goClass() {
        if (clickedBean != null) {
            Intent intent = new Intent(mActivity, BaseIntroActivity.class);
            intent.putExtra("from", "app模考大赛查看解析");
            intent.putExtra("NetClassId", clickedBean.courseId + "");
            if (mActivity != null) {
                mActivity.startActivity(intent);
            }
        }
    }

    /**
     * 报名弹窗
     */
    private void showAreaSign() {
        ArrayList<SimulationScDetailBean.Area> a = clickedBean.areaList;
        if (a != null) {
            if (a.size() == 0) {
                signUp(-9);
            } else if (a.size() == 1) {
                signUp(a.get(0).key);
            } else {
                areas.clear();
                areas.addAll(a);
                showAreaDialog();
            }
        } else {
            signUp(-9);
        }
    }

    private BottomDialog bottomDialog;              // 地区选择Dialog
    private AreaAdapter areaAdapter;
    private int areaPosition;
    private int areaId;

    /**
     * 点击地区，选择地区，确定报名
     * 点击报名按钮，选择地区，确定报名的BottomDialog
     */
    private void showAreaDialog() {

        if (areas == null || areas.size() == 0 || clickedBean == null) return;
        SimulationScDetailBean.UserMetaEntity userMeta = clickedBean.userMeta;
        if (userMeta == null || userMeta.positionId == 0) {
            areaId = areas.get(0).key;
        } else {
            areaId = userMeta.positionId;
        }

        for (int i = 0; i < areas.size(); i++) {
            SimulationScDetailBean.Area area = areas.get(i);
            if (area.key == areaId) {
                area.isSelect = true;
                areaPosition = i;
            } else {
                area.isSelect = false;
            }
        }

        if (bottomDialog == null) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.sc_area_choice_dialog, null);

            bottomDialog = new BottomDialog(mActivity, view, true, true);

            ImageView ivClose = view.findViewById(R.id.iv_close);
            RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
            AppCompatButton btnSign = view.findViewById(R.id.btn_sign);

            recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));

            areaAdapter = new AreaAdapter(mActivity, areas, new AreaAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    areas.get(areaPosition).isSelect = false;
                    areaAdapter.notifyItemChanged(areaPosition);
                    SimulationScDetailBean.Area bean = areas.get(position);
                    bean.isSelect = true;
                    areaPosition = position;
                    areaId = bean.key;
                    areaAdapter.notifyItemChanged(areaPosition);
                }
            });

            recyclerView.setAdapter(areaAdapter);

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomDialog.dismiss();
                }
            });

            btnSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickedBean.userMeta != null && clickedBean.userMeta.positionId == areaId) {
                        if (bottomDialog != null && bottomDialog.isShowing())
                            bottomDialog.dismiss();
                        return;
                    }
                    signUp(areaId);
                }
            });
        } else {
            areaAdapter.notifyDataSetChanged();
        }

        bottomDialog.show();
    }

    /**
     * 报名
     */
    private void signUp(int areaId) {
        if (clickedBean == null) return;
        if (isEssay()) {     // 申论
            int paperId = clickedBean.essayPaperId;
            signEssaySc(areaId, paperId);
        } else {
            int paperId = clickedBean.paperId;
            signArenaSc(areaId, paperId);
        }
    }

    // 行测报名
    private void signArenaSc(int areaId, int paperId) {
        if (paperId <= 0) {
            return;
        }
        mActivity.showProgress();
        ServiceProvider.postScSignUpNew(compositeSubscription, paperId, areaId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                CommonUtils.showToast("报名成功");
                getData();
                if (bottomDialog != null && bottomDialog.isShowing())
                    bottomDialog.dismiss();
                if (clickedBean != null)
                    addCalendar();
            }

            @Override
            public void onError(final Throwable e) {
                CommonUtils.showToast("报名失败");
                mActivity.hideProgress();
            }
        });
    }

    // 申论报名
    private void signEssaySc(int areaId, int paperId) {
        if (paperId <= 0) {
            return;
        }
        mActivity.showProgress();
        ServiceProvider.postSimulationEssayCSignUp(compositeSubscription, paperId, areaId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                LogUtils.d(model.data);
                CommonUtils.showToast("报名成功");
                getData();
                if (bottomDialog != null && bottomDialog.isShowing())
                    bottomDialog.dismiss();
                if (clickedBean != null)
                    addCalendar();
            }

            @Override
            public void onError(final Throwable e) {
                CommonUtils.showToast("报名失败");
                mActivity.hideProgress();
            }
        });
    }

    private Dialog mDialog;             // 加入日历的弹窗

    private void addCalendar() {
        if (!CalendarReminderUtils.checkPermission(getActivity(), CalendarReminderUtils.REQUEST_CODE))
            return;
        long startTime = isEssay() ? clickedBean.essayStartTime : clickedBean.startTime;
        boolean isCanAddTime = startTime - System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime > fiveMin * 2;
        if (clickedBean == null || !isCanAddTime || clickedBean.isAddCalender)
            return;
        if (mDialog == null)
            mDialog = DialogUtils.createExitConfirmDialog(getActivity(), "添加到日历提醒", "考前10分钟提醒进入考试，是否加入到日历", "残忍拒绝", "确认", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertCalender();
                }
            },null);
        mDialog.show();
    }

    private void insertCalender() {
        long startTime = isEssay() ? clickedBean.essayStartTime : clickedBean.startTime;
        long endTime = isEssay() ? clickedBean.essayEndTime : clickedBean.endTime;
        String courseDetail = clickedBean.name + "\n" + clickedBean.timeInfo;
        boolean isAdded = CalendarReminderUtils.addCalendarEvent(getActivity(), clickedBean.name, courseDetail, startTime, endTime);
        if (isAdded) scListAdapter.notifyDataSetChanged();
    }

    /**
     * 报名/考试按钮
     */
    private void reaBtnClick() {

        SimulationScDetailBean bean = this.clickedBean;

        if (bean != null) {
            int status = bean.status;              // 按钮状态
            if (status == 1) {              // 报名，判断时间是否允许
                if (!isEssay() && System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - bean.startTime < thrfenMin) {               // 行测
                    // 神策埋点
                    StudyCourseStatistic.simulatedSignUp(MatchCacheData.getInstance().matchPageFrom, String.valueOf(bean.paperId), bean.name, "行测", Type.getCategory(SpUtils.getUserCatgory()));
                    // 如果 enrollFlag == 1，那么报名不选择地区，默认全国（-9）
                    if (bean.enrollFlag != 1) {
                        showAreaSign();
                    } else {
                        signArenaSc(-9, bean.paperId);
                    }
                } else if (isEssay() && System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - bean.essayStartTime < thrfenMin) {    // 申论
                    // 神策埋点
                    StudyCourseStatistic.simulatedSignUp(MatchCacheData.getInstance().matchPageFrom, String.valueOf(bean.paperId), bean.name, "申论", Type.getCategory(SpUtils.getUserCatgory()));
                    // 如果 enrollFlag == 1，那么报名不选择地区，默认全国（-9）
                    if (bean.enrollFlag != 1) {
                        showAreaSign();
                    } else {
                        signEssaySc(-9, bean.essayPaperId);
                    }
                } else {                    // 如果已经过时，就重新请求网络获取详情
                    // 很遗憾！您已错过行测模考，下午请准时参加申论模考
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - reqTime > fiveMin) {
                                reqTime = System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime;
                                if (mPresenter != null) {
                                    if (isEssay()) {
                                        mPresenter.getSimulationEssayScDetail();
                                    } else {
                                        getArenaData();
                                    }
                                }
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, null, "抱歉，您已错过本次模考，可在模考历史查看，或稍后报名参加下次模考", null, "确认");
                }
            } else if (status == 3) {       // 开始考试，但是还不能进入
                if (mActivity != null) {
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - reqTime > fiveMin) {
                                reqTime = System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime;
                                if (mPresenter != null) {
                                    if (isEssay()) {
                                        mPresenter.getSimulationEssayScDetail();
                                    } else {
                                        getArenaData();
                                    }
                                }
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, null, "考试还未开始哦", null, "确认");
                }
            } else if (status == 4) {   // 开始测试
                if (!isEssay()) {           // 跳行测考试页
                    Bundle arg = new Bundle();
                    int paperId = bean.paperId;
                    if (paperId > 0) {
                        arg.putLong("practice_id", paperId);
                        ArenaExamActivityNew.show(mActivity, ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, arg);
                        SpUtils.setIsSimulationContest(true);
                    }
                } else {                    // 跳申论考试页
                    SpUtils.setIsSimulationContest(true);
                    Bundle m = new Bundle();
                    m.putString("titleView", bean.name);
                    m.putBoolean("isSingle", false);
                    m.putLong("paperId", bean.essayPaperId);
                    m.putLong("sc_start_exam_time", bean.essayStartTime);
                    m.putLong("sc_end_exam_time", bean.essayEndTime);
                    m.putBoolean("isStartToCheckDetail", false);
                    m.putInt("commitLimitTime", bean.commitLimitTime);
                    if (mActivity != null) {
                        EssayExamActivity.show(mActivity, EssayExamActivity.ESSAY_EXAM_SC, m);
                    }
                }
            } else if (status == 5) {   // 无法考试，时间错过
                if (mActivity != null) {
                    // 很遗憾！您已错过行测模考，下午请准时参加申论模考
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - reqTime > fiveMin) {
                                reqTime = System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime;
                                if (mPresenter != null) {
                                    if (isEssay()) {
                                        mPresenter.getSimulationEssayScDetail();
                                    } else {
                                        getArenaData();
                                    }
                                }
                            }
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, null, "抱歉，您已错过本次模考，可在模考历史查看，或稍后报名参加下次模考", null, "确认");
                }
            } else if (status == 6) {   // 去查看报告
                Intent intent = new Intent(mActivity, ScReportActivity.class);
                intent.putExtra("tag", isEssay() ? 2 : 1);

                Bundle arg = new Bundle();
                arg.putLong("practice_id", bean.paperId);
                arg.putInt("req_from", bean.tag);
                arg.putBoolean("is_report_finished", true);
                arg.putLong("essay_paperId", bean.essayPaperId);

                intent.putExtra("arg", arg);
                mActivity.startActivity(intent);
            } else if (status == 7) {   // 未出报告
                DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime - reqTime > fiveMin) {
                            reqTime = System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime;
                            if (mPresenter != null) {
                                if (isEssay()) {
                                    mPresenter.getSimulationEssayScDetail();
                                } else {
                                    getArenaData();
                                }
                            }
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, null, "模考结束后出成绩报告,\n请及时关注。", null, "确认");
            } else if (status == 8) {   // 未交卷，可以继续做题
                if (!isEssay()) {           // 1、行测
                    StudyCourseStatistic.simulatedOperation("继续答题", Type.getSubject(SpUtils.getUserSubject()), Type.getCategory(SpUtils.getUserCatgory()),
                            String.valueOf(bean.paperId), bean.name);
                    Bundle arg = new Bundle();
                    arg.putLong("practice_id", bean.paperId);
                    if (mActivity != null) {
                        ArenaExamActivityNew.show(mActivity, ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, arg);
                    }
                    SpUtils.setIsSimulationContest(true);
                } else {                    // 2、申论
                    StudyCourseStatistic.simulatedOperation("继续答题", Type.getSubject(SpUtils.getUserSubject()), Type.getCategory(SpUtils.getUserCatgory()),
                            String.valueOf(bean.essayPaperId), bean.name);
                    Bundle m = new Bundle();
                    m.putString("titleView", bean.name);
                    m.putBoolean("isSingle", false);
                    m.putLong("paperId", bean.essayPaperId);
                    m.putLong("sc_start_exam_time", bean.essayStartTime);
                    m.putLong("sc_end_exam_time", bean.essayEndTime);
                    m.putBoolean("isStartToCheckDetail", false);
                    m.putInt("commitLimitTime", bean.commitLimitTime);
                    EssayExamActivity.show(mActivity, EssayExamActivity.ESSAY_EXAM_SC, m);
                    SpUtils.setIsSimulationContest(true);
                }
            } else if (status == 9) {           // 停止报名
                ToastUtils.showMessage("很遗憾！您已错过本次模考，\n本次模考结束后可报名下次模考");
            } else {
                Toast.makeText(mActivity, status + "", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 是否是申论
    private boolean isEssay() {
        return subjectId == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
    }

    /**
     * @param flag 1、无网络
     *             2、获取数据失败
     *             3、无数据
     */
    public void onLoadDataFailed(int flag) {
        errorView.setVisibility(View.VISIBLE);
        switch (flag) {
            case 1:
                errorView.setErrorText("网络不太好，点击屏幕，刷新看看");
                errorView.setErrorImage(R.drawable.icon_common_net_unconnected);
                break;
            case 2:
                errorView.setErrorText("获取数据失败，点击刷新");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
            case 3:
                errorView.setErrorText("暂无模考大赛");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
        }
        errorView.setErrorImageVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public static SCMainChildFragmentNew newInstance(Bundle extra) {
        SCMainChildFragmentNew fragment = new SCMainChildFragmentNew();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CalendarReminderUtils.REQUEST_CODE) {
                if (clickedBean != null)
                    insertCalender();
            } else if (requestCode == CalendarReminderUtils.REQUEST_READ_CODE)
                getData();

        }
    }
}
