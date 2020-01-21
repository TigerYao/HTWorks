package com.huatu.handheld_huatu.business.matches.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.newtips.NewTipsManager;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.bean.GiftDescribeBean;
import com.huatu.handheld_huatu.business.matches.fragment.NewScReportListFragment;
import com.huatu.handheld_huatu.business.matches.fragment.SCMainChildFragmentNew;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.matchs.MatchTabBean;
import com.huatu.handheld_huatu.mvppresenter.simulation.SimulationContestImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * 模考大赛列表，此Activity只有模考列表Tab（新）
 * 新逻辑，也把就模考大赛页中各种页面分开。
 * 模考列表页、模考历史、我的模考记录、模考报告。
 * <p>
 * 行测模考列表，如果是复合考试（行测&申论），就需要调用两个报名接口
 * 申论模考调用原接口
 */

public class SimulationContestActivityNew extends BaseActivity {

    @BindView(R.id.rl_tip)
    RelativeLayout rlTip;                           // 引导布局
    @BindView(R.id.iv_tip_2)
    ImageView ivTip02;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.error_view)
    CommonErrorView errorView;

    @BindView(R.id.rl_bottom_gift)
    RelativeLayout rlGift;                          // 下边的弹窗
    @BindView(R.id.iv_gift_close)
    ImageView ivClose;                              // 关闭弹窗
    @BindView(R.id.tv_bottom_gift)
    TextView tvGift;                                // 弹窗内容
    @BindView(R.id.iv_bottom_gift)
    ImageView ivGift;                               // 弹窗左边小图标

    protected SimulationContestImpl mPresenter;     // 网络访问

    private boolean mToHome;
    //    private boolean isEssay;                        // 是否是申论调过来的，用于View的隐现
    private int subject = 1;                        // 科目
    private int showSubject;                        // 需要显示的科目

    private FragmentPagerItemAdapter adapter;       // ViewPager适配器
    private int pagePageIndex;                      // 当前是哪一页

    private ArrayList<MatchTabBean> matchTabBeans;  // Tab数据
    private GiftDescribeBean giftDescribeBean;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_simulation_contest_new;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event.type == SimulationContestMessageEvent.SHOW_GIFT_DESCRIBE) {
            showGiftDescribe();
        }
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = new CompositeSubscription();
        mPresenter = new SimulationContestImpl(compositeSubscription);

        Bundle extraArgs;

        if (originIntent != null) {
            mToHome = originIntent.getBooleanExtra("mToHome", false);
            subject = originIntent.getIntExtra("subject", 1);
            showSubject = originIntent.getIntExtra("showSubject", -1);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            boolean toHome = extraArgs.getBoolean("toHome", false);
            mToHome = toHome ? toHome : mToHome;
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlGift.setVisibility(View.GONE);
            }
        });

        QMUIStatusBarHelper.setStatusBarLightMode(SimulationContestActivityNew.this);

//        setTabWidth(tabLayout, 70);

        // 显示引导
        showGuid();
    }

    @Override
    protected void onLoadData() {
        getServiceTime();
        getTabData();
        getGiftDescribe();
    }

    @OnClick(R.id.iv_back)
    public void onClickBack() {          // 点击返回
        onBackPressed();
    }

    @OnClick(R.id.tv_my_report)
    public void goToReport() {                // 去我的报告页
        if (CommonUtils.isFastDoubleClick()) return;
        if (matchTabBeans == null || pagePageIndex >= matchTabBeans.size()) return;
        Bundle bundle = new Bundle();
        bundle.putInt("subjectId", matchTabBeans.get(pagePageIndex).id);
        BaseFrgContainerActivity.newInstance(this, NewScReportListFragment.class.getName(), bundle);
    }

    @OnClick(R.id.error_view)
    public void onClickErrView() {
        onLoadData();
    }

    // 获取服务器时间
    private void getServiceTime() {
        if (!NetUtil.isConnected()) {
            return;
        }
        ServiceProvider.getServiceTime(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model != null && model.data instanceof Long) {
                    long serviceTime = (long) model.data;
                    long dTime = serviceTime - System.currentTimeMillis();
                    if (Math.abs(dTime) > 1000 * 60 * 3) {
                        SignUpTypeDataCache.getInstance().dTime = dTime;
                    }
                }
            }
        });
    }

    /**
     * 获取Tab信息
     */
    private void getTabData() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请检查您的网络设置");
            onLoadDataFailed(0);
            return;
        }
        showProgress();
        ServiceProvider.getSimulationTabList(compositeSubscription, subject, subject, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                hideProgress();
                onLoadDataFailed(2);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                hideProgress();
                if (model == null || model.data == null || model.data.size() == 0) {
                    onLoadDataFailed(3);
                } else {
                    matchTabBeans = (ArrayList<MatchTabBean>) model.data;
                    initViewPager();
                }
            }
        });
    }

    /**
     * 获取大礼包描述详情
     */
    private void getGiftDescribe() {
        if (!NetUtil.isConnected()) {
            return;
        }
        ServiceProvider.getEstiMateInfo(compositeSubscription, 12, subject, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.code == 1000000) {
                    if (model.data instanceof GiftDescribeBean) {
                        giftDescribeBean = (GiftDescribeBean) model.data;
                        if (giftDescribeBean.isShow == 1) {
                            rlGift.setVisibility(View.VISIBLE);
                            tvGift.setText(giftDescribeBean.giftIntroduce);
                            ImageLoad.load(SimulationContestActivityNew.this, giftDescribeBean.iconUrl.trim(), ivGift);
                        } else {
                            rlGift.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void initViewPager() {
        FragmentPagerItems pages = new FragmentPagerItems(this);

        if (matchTabBeans == null || matchTabBeans.size() == 0) {
            return;
        }

        for (int i = 0; i < matchTabBeans.size(); i++) {
            MatchTabBean matchTabBean = matchTabBeans.get(i);
            Bundle arg = new Bundle();
            arg.putBoolean("mToHome", mToHome);
            arg.putInt("subjectId", matchTabBean.id);
            pages.add(FragmentPagerItem.of(matchTabBean.name, SCMainChildFragmentNew.class, arg));
            if (matchTabBean.flag == 1) {
                pagePageIndex = i;
            }
        }

        if (pages.size() == 1) {
            tabLayout.setVisibility(View.GONE);
        }

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewPager.setOffscreenPageLimit(100);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        if (pagePageIndex != 0) {
            viewPager.setCurrentItem(pagePageIndex);
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                pagePageIndex = i;
            }
        });

        // 获取本地更新数量
        ArrayList<TipBean> subjectTip = NewTipsManager.newInstance().getSubjectTip(SpUtils.getUserCatgory(), ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST);
        if (subjectTip != null && subjectTip.size() > 0) {
            for (TipBean tipBean : subjectTip) {
                for (MatchTabBean matchTabBean : matchTabBeans) {
                    if (matchTabBean.id == tipBean.type) {
                        matchTabBean.tipNum = tipBean.tipNum;
                    }
                }
            }
        }

        setupTabIcons();

        if (showSubject != -1) {
            for (int i = 0; i < matchTabBeans.size(); i++) {
                if (matchTabBeans.get(i).id == showSubject) {
                    if (i < matchTabBeans.size()) {
                        viewPager.setCurrentItem(i);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 显示引导图
     */
    private void showGuid() {
        if (SpUtils.getMatchReportTipShow()) {
            SpUtils.setMatchReportTipShow();
            rlTip.setVisibility(View.VISIBLE);
            rlTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlTip.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * 显示礼包描述弹窗
     */
    private void showGiftDescribe() {

        if (giftDescribeBean == null) {
            return;
        }

        View layout = LayoutInflater.from(SimulationContestActivityNew.this).inflate(R.layout.gift_dialog_layout, null);

        //对话框
        final Dialog dialog = new AlertDialog.Builder(SimulationContestActivityNew.this).create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(layout);

        WindowManager.LayoutParams lp = window.getAttributes();
        int width = DisplayUtil.getScreenWidth();                           // 获取屏幕宽、高用
        lp.width = (int) (width * 0.8);                                     // 高度设置为屏幕的0.6
        window.setAttributes(lp);

        // 内容
        TextView tvContent = (TextView) layout.findViewById(R.id.tv_content);
        tvContent.setText(giftDescribeBean.bigBagRemind);

        // OK按钮
        TextView tvOk = (TextView) layout.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
                errorView.setErrorText("暂无模考大赛");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
        }
    }

    // 为了改变TabLayout下的横线长度
    public void setTabWidth(final TabLayout tabLayout, final int padding) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);


                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = padding;
                        params.rightMargin = padding;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
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
    public void onBackPressed() {
        if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(SimulationContestActivityNew.this);
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("matchTabBeans", matchTabBeans);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        matchTabBeans = (ArrayList<MatchTabBean>) savedInstanceState.getSerializable("matchTabBeans");
        if (matchTabBeans == null) {
            onLoadData();
        } else {
            initViewPager();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 设置每个TabLayout的View
     */

    int colorSelect;
    int colorNormal;

    private void setupTabIcons() {

        colorSelect = Color.parseColor("#FF3F47");
        colorNormal = Color.parseColor("#000000");

        for (int i = 0; i < matchTabBeans.size(); i++) {
            TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            if (tabAt != null) {
                tabAt.setCustomView(getTabView(i));
            }
        }

        // 设置TabLayout的选中监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    TextView tvTitle = (TextView) customView.findViewById(R.id.tv_title);
                    TextView tvNum = (TextView) customView.findViewById(R.id.tv_num);
                    tvTitle.setTextColor(colorSelect);
                    tvNum.setVisibility(View.GONE);
                    MatchTabBean matchTabBean = matchTabBeans.get(tab.getPosition());
                    if (matchTabBean.tipNum != 0) {
                        matchTabBean.tipNum = 0;
                        NewTipsManager.newInstance().setTipsGone(SpUtils.getUserCatgory(), matchTabBean.id, ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    TextView tvTitle = (TextView) customView.findViewById(R.id.tv_title);
                    tvTitle.setTextColor(colorNormal);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 提供TabLayout的View
     * 根据index返回不同的View
     * 主意：默认选中的View要返回选中状态的样式
     */
    private View getTabView(int index) {

        //自定义View布局
        MatchTabBean matchTabBean = matchTabBeans.get(index);

        View view = LayoutInflater.from(this).inflate(R.layout.item_match_tab, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvNum = (TextView) view.findViewById(R.id.tv_num);
        tvTitle.setText(matchTabBean.name);
        if (matchTabBean.tipNum > 0) {
            tvNum.setText(matchTabBean.tipNum + "");
        } else {
            tvNum.setVisibility(View.GONE);
        }
        if (matchTabBean.flag == 1) {
            tvTitle.setTextColor(colorSelect);
            tvNum.setVisibility(View.GONE);
            matchTabBean.tipNum = 0;
            NewTipsManager.newInstance().setTipsGone(SpUtils.getUserCatgory(), matchTabBean.id, ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST);
        } else {
            tvTitle.setTextColor(colorNormal);
        }

        return view;
    }
}
