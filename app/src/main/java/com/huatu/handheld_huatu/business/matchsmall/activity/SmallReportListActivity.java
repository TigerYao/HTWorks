package com.huatu.handheld_huatu.business.matchsmall.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.matchsmall.adapter.ReportAdapter;
import com.huatu.handheld_huatu.helper.SimpleAnimListener;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallReportListBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.library.PullToRefreshBase;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.jaaksi.pickerview.picker.BasePicker;
import org.jaaksi.pickerview.picker.TimePicker;
import org.jaaksi.pickerview.topbar.ITopBar;
import org.jaaksi.pickerview.widget.DefaultCenterDecoration;
import org.jaaksi.pickerview.widget.PickerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;

import static org.jaaksi.pickerview.picker.TimePicker.TYPE_DAY;
import static org.jaaksi.pickerview.picker.TimePicker.TYPE_MONTH;
import static org.jaaksi.pickerview.picker.TimePicker.TYPE_YEAR;

public class SmallReportListActivity extends BaseActivity {

    private final long ONE_DAY_STAMP = 1000 * 60 * 60 * 24;

    @BindView(R.id.iv_filter)
    ImageView ivFilter;                     // 筛选按钮

    @BindView(R.id.rlv_content)
    PullRefreshRecyclerView rlvContent;     // 刷新加载列表
    @BindView(R.id.view_err)
    CommonErrorView errorView;              // 错误、空白页面

    @BindView(R.id.view_bg)
    View viewBg;                            // 筛选器背景

    @BindView(R.id.ll_filter)
    LinearLayout llFilter;                  // 筛选上部布局
    @BindView(R.id.cb_seven)
    CheckBox cbSeven;                       // 七天
    @BindView(R.id.cb_thirty)
    CheckBox cbThirty;                      // 三十天
    @BindView(R.id.ll_custom)
    LinearLayout llCustom;                  // 自定义的布局
    @BindView(R.id.cb_custom_start)
    CheckBox cbCustomStart;                 // 自定义开始时间
    @BindView(R.id.cb_custom_end)
    CheckBox cbCustomEnd;                   // 自定义开始时间
    @BindView(R.id.tv_reset)
    TextView tvReset;                       // 重置

    private int pageSize = 20;              // 默认加载20条
    private int page = 1;                   // 页码

    // 记录从2019年01月01号，到今天，每天的日期和时间戳
    private long[] stampArray;
    private long startTime = -1;            // 开始时间
    private long endTime = -1;              // 结束时间

    private long pickerStartTime = -1;            // 开始时间
    private long pickerEndTime = -1;              // 结束时间

    private ArrayList<SmallReportListBean.SmallReportBean> datas;
    private ReportAdapter adapter;

    private boolean isFilterShow = false;       // 筛选器z是否显示

    private TimePicker mTimePicker;             // 时间选择器
    private int pickerStep;                     // 正在选择开始时间 0 ，还是结束时间 1

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_small_report_list;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(SmallReportListActivity.this);
        initRecyclerView();
        initFilter();
    }

    @Override
    protected void onLoadData() {
        getData(true);
    }

    @OnClick({R.id.iv_back, R.id.view_err})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:              // 返回键
                finish();
                break;
            case R.id.view_err:             // 错误页面
                onLoadData();
                break;
        }
    }

    private void getData(final boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            showError(0);
            ToastUtils.showMessage("网络未连接，请检查您的网络设置");
            return;
        }
        if (isRefresh) {
            showProgress();
            page = 1;
        } else {
            page++;
        }
        ServiceProvider.getSmallMatchReportList(compositeSubscription, page, pageSize, startTime, endTime, new NetResponse() {

            @Override
            public void onError(Throwable e) {
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showMessage("获取数据失败");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                SmallReportListBean mod = (SmallReportListBean) model.data;
                if (mod != null && mod.result != null && mod.result.size() > 0) {
                    hideError();
                    if (isRefresh) {
                        datas.clear();
                    }
                    datas.addAll(mod.result);
                    adapter.notifyDataSetChanged();
                } else {
                    if (isRefresh) {
                        showError(2);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        // 设置每页加载的条数，判断是否是最后一页
        rlvContent.getRefreshableView().setPagesize(pageSize);
        rlvContent.getRefreshableView().setLayoutManager(new LinearLayoutManager(SmallReportListActivity.this));
        // 加载过程中是否可以滑动
        rlvContent.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        rlvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                getData(true);
            }
        });
        // 自动加载更多的回调
        rlvContent.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                getData(false);
            }
        });

        datas = new ArrayList<>();

        adapter = new ReportAdapter(SmallReportListActivity.this, datas, new ReportAdapter.OnItemClickListener() {
            @Override
            public void onClick(int type, int position) {
                Bundle extraArgs = new Bundle();
                extraArgs.putLong("practice_id", datas.get(position).practiceId);
                SmallMatchReportActivity.show(SmallReportListActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, extraArgs);
            }
        });

        rlvContent.getRefreshableView().setRecyclerAdapter(adapter);
    }

    // 日期选择器--------------------------------------

    private boolean isClickReset = false;       // 是否点击了reset按钮，点击了不收起筛选栏，不点击reset按钮的话reset了要收起

    @OnClick({R.id.iv_filter, R.id.view_bg, R.id.tv_reset, R.id.rl_seven, R.id.rl_thirty, R.id.rl_start, R.id.rl_end,
            R.id.ll_filter})                // 为了防止误触
    public void onClickFilter(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:            // 筛选按钮
                if (!isFilterShow) {
                    showFilter();
                } else {
                    hideFilter();
                }
                break;
            case R.id.view_bg:              // 筛选背景
                hideFilter();
                break;
            case R.id.rl_seven:             // 点击七天
                if (!cbSeven.isChecked()) {
                    setSelectType(1);
                } else {
                    setSelectType(0);
                }
                break;
            case R.id.rl_thirty:            // 点击三十天
                if (!cbThirty.isChecked()) {
                    setSelectType(2);
                } else {
                    setSelectType(0);
                }
                break;
            case R.id.rl_start:             // 点击，显示开始选择时间
                showPicker(0);
                break;
            case R.id.rl_end:               // 点击，显示结束选择时间
                showPicker(1);
                break;
            case R.id.tv_reset:             // 筛选重置
                isClickReset = true;
                setSelectType(0);
                break;
        }
    }

    /**
     * 初始化时间筛选器
     */
    private void initFilter() {

        tvReset.setClickable(false);
        viewBg.setVisibility(View.GONE);
        llFilter.setVisibility(View.GONE);

        // 计算三十五天的时间戳
        // 明天0点的时间戳
        long current = System.currentTimeMillis();
        long tomorrowZero = (current + ONE_DAY_STAMP) / ONE_DAY_STAMP * ONE_DAY_STAMP - TimeZone.getDefault().getRawOffset();

        long beginStamp = tomorrowZero - ONE_DAY_STAMP * 35;

        int daySize = (int) ((tomorrowZero - beginStamp) / ONE_DAY_STAMP + 1);

        stampArray = new long[daySize];

        for (int i = daySize - 1; i >= 0; i--) {
            stampArray[i] = tomorrowZero;
            tomorrowZero -= ONE_DAY_STAMP;
        }

        int type = TYPE_YEAR | TYPE_MONTH | TYPE_DAY;

        TimePicker.OnTimeSelectListener onTimeSelectListener = new TimePicker.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(TimePicker picker, Date date) {
                long time = date.getTime();

                if (pickerStep == 0) {   // 选择了开始时间
                    pickerStartTime = time;
                    setCBTime(cbCustomStart, pickerStartTime);
                    pickerGo();
                } else {                 // 选择了结束时间
                    pickerEndTime = time + ONE_DAY_STAMP;
                    setCBTime(cbCustomEnd, pickerEndTime - 1);
                    pickerGo();
                }
            }
        };

        mTimePicker = new TimePicker.Builder(this, type, onTimeSelectListener)
                // 设置时间区间
                .setRangDate(1546272000000L, System.currentTimeMillis())
                // 设置pickerview样式
                .setInterceptor(new BasePicker.Interceptor() {
                    @Override
                    public void intercept(PickerView pickerView) {
                        pickerView.setVisibleItemCount(5);
//                        pickerView.setIsCirculation(true);
                    }
                }).create();

        TopBar topBar = new TopBar();
        mTimePicker.setTopBar(topBar);
        pickerTitleView = topBar.rootView.findViewById(R.id.tv_title);

        List<PickerView> pickerViews = mTimePicker.getPickerViews();
        DefaultCenterDecoration decoration = new DefaultCenterDecoration(SmallReportListActivity.this);
        decoration.setLineColor(Color.parseColor("#979797"));
        for (PickerView pickerView : pickerViews) {
            pickerView.setColor(Color.parseColor("#4A4A4A"), Color.parseColor("#6E6E6E"));
            pickerView.setTextSize(16, 18);
            pickerView.setCenterDecoration(decoration);
        }
    }

    private void pickerGo() {
        if (pickerStartTime != -1 && pickerEndTime != -1 && pickerStartTime > pickerEndTime) {
            ToastUtils.showMessage("结束时间不能早于开始时间");
            return;
        }
        setSelectType(3);
    }

    /**
     * 显示时间筛选窗
     */
    private void showFilter() {

        isFilterShow = true;

        viewBg.setVisibility(View.VISIBLE);
        llFilter.setVisibility(View.VISIBLE);

        Animation animationTopIn = AnimationUtils.loadAnimation(this, R.anim.s_match_filter_top_in);
        llFilter.startAnimation(animationTopIn);

        ObjectAnimator animator = ObjectAnimator.ofFloat(viewBg, "alpha", 0f, 1f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(100);
        animator.start();
    }

    /**
     * 隐藏时间筛选器
     */
    private void hideFilter() {

        isFilterShow = false;

        Animation animationTopIn = AnimationUtils.loadAnimation(this, R.anim.s_match_filter_top_out);
        llFilter.startAnimation(animationTopIn);

        ObjectAnimator animator = ObjectAnimator.ofFloat(viewBg, "alpha", 1f, 0f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(100);
        animator.addListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewBg.setVisibility(View.GONE);
                llFilter.setVisibility(View.GONE);
            }
        });
        animator.start();

        if (mTimePicker != null) {
            mTimePicker.onCancel();
        }
    }

    /**
     * 时间选择窗
     */
    TextView pickerTitleView;

    private void showPicker(int type) {
        if (mTimePicker != null) {
            pickerStep = type;
            if (type == 0) {
                pickerTitleView.setText("请选择开始时间");
                mTimePicker.setSelectedDate(pickerStartTime == -1 ? stampArray[stampArray.length - 4] : pickerStartTime);
            } else {
                pickerTitleView.setText("请选择结束时间");
                mTimePicker.setSelectedDate(pickerEndTime == -1 ? stampArray[stampArray.length - 1] : pickerEndTime);
            }
            if (!mTimePicker.getPickerDialog().isShowing()) {
                mTimePicker.show();
            }
        }
    }

    private void setCBTime(CheckBox cb, long start) {
        cb.setChecked(true);
        cb.setText(DateUtils.getFormatData("yyyy.MM.dd", start));
    }

    /**
     * 哪个选择器被选中
     *
     * @param type 0、重置 1、近七天 2、近三十天 3、自定义时间
     */
    private void setSelectType(int type) {
        // 状态初始化
        tvReset.setTextColor(Color.parseColor("#FF6D73"));
        ivFilter.setImageResource(R.mipmap.small_report_filter_red);

        tvReset.setClickable(true);

        cbSeven.setChecked(false);
        cbThirty.setChecked(false);
        cbCustomStart.setChecked(false);
        cbCustomEnd.setChecked(false);

        switch (type) {
            case 0:         // 重置了
                startTime = -1;
                endTime = -1;
                tvReset.setClickable(false);
                tvReset.setTextColor(Color.parseColor("#C4C4C4"));
                ivFilter.setImageResource(R.mipmap.small_report_filter_nomal);
                if (isClickReset) {
                    isClickReset = false;
                } else {
                    hideFilter();
                }
                break;
            case 1:
                startTime = stampArray[stampArray.length > 8 ? stampArray.length - 8 : 0];
                endTime = stampArray[stampArray.length - 1];
                hideFilter();
                cbSeven.setChecked(true);
                break;
            case 2:
                startTime = stampArray[stampArray.length > 31 ? stampArray.length - 31 : 0];
                endTime = stampArray[stampArray.length - 1];
                hideFilter();
                cbThirty.setChecked(true);
                break;
            case 3:
                startTime = pickerStartTime;
                endTime = pickerEndTime;
                if (pickerStartTime != -1 && pickerEndTime != -1) {
                    hideFilter();
                }
                if (pickerStartTime != -1) {
                    cbCustomStart.setChecked(true);
                }
                if (pickerEndTime != -1) {
                    cbCustomEnd.setChecked(true);
                }
                break;
        }
        getData(true);
    }
    // 日期选择器--------------------------------------

    @Override
    public boolean canTransStatusbar() {
        return true;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("datas", datas);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<SmallReportListBean.SmallReportBean> saveDatas = (ArrayList<SmallReportListBean.SmallReportBean>) savedInstanceState.getSerializable("datas");
        if (saveDatas == null || saveDatas.size() == 0) {
            onLoadData();
        } else {
            if (datas != null) {
                datas.addAll(saveDatas);
                adapter.notifyDataSetChanged();
            } else {
                onLoadData();
            }
        }
    }

    private void hideError() {
        hideProgress();
        rlvContent.onRefreshComplete();
        errorView.setVisibility(View.GONE);
    }

    private void showError(int type) {
        hideProgress();
        rlvContent.onRefreshComplete();
        datas.clear();
        adapter.notifyDataSetChanged();
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无数据");
                break;
        }
    }

    private class TopBar implements ITopBar {

        private View rootView;

        @Override
        public View getTopBarView() {
            rootView = LayoutInflater.from(SmallReportListActivity.this).inflate(R.layout.s_match_picker_top_bar, null);
            return rootView;
        }

        @Override
        public View getBtnCancel() {
            return rootView.findViewById(R.id.btn_cancel);
        }

        @Override
        public View getBtnConfirm() {
            return rootView.findViewById(R.id.btn_confirm);
        }

        @Override
        public TextView getTitleView() {
            return rootView.findViewById(R.id.tv_title);
        }
    }
}
