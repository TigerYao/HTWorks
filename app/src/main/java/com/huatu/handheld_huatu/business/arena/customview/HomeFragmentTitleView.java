package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.SearchActivity;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.lessons.view.HorizontalListView;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.mvppresenter.me.ExamTargetAreaImpl;
import com.huatu.handheld_huatu.utils.DBitmapUtil;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * 首页，行测，的ActionBar
 */
public class HomeFragmentTitleView extends TopActionBar implements TopActionBar.OnTopBarButtonClickListener {

    private Context mContext;

    private int requestType;
    private boolean toOpenPopWindow = false;//是否要展开弹层
    private final int POP_WINDOW_WIDTH_DP = 130;
    private int POP_WINDOW_HEIGHT_DP = 145;
    private final int POP_WINDOW_ITEM_HEIGHT_DP = 30;
    private PopupWindow mPopupWindow;
    private ExamTargetAreaImpl mPresenter;
    protected CompositeSubscription compositeSubscription;
    private HorizontalListView ke_mu_show_lv;

    // 考试类型；1，公考 2，教师考试 3，事业单位 4，医疗 catgory
    // 考试科目；1，行测 2，公共基础 3，行测  subject
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<ExamTypeAreaMessageEvent> event) {
        if (event == null || mPresenter == null || event.typeExObject == null) {
            return;
        }
        if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_SHOW) {                                   // 有题库，显示搜索按钮
            showRightButton(true);
        } else if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_NOT_SHOW) {                               // 没有题库，隐藏搜索按钮
            showRightButton(false);
        }
    }

    public HomeFragmentTitleView(Context context) {
        super(context);
        init(context);
    }

    public HomeFragmentTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeFragmentTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        initView();
    }

    protected void init() {
        layoutResId = CanTranslucent() ? R.layout.top_actionbar_layout_trannew : R.layout.top_actionbar_layout_new;
        super.init();
    }

    private void initView() {
        ke_mu_show_lv = findViewById(R.id.ke_mu_show_lv);
        setButtonClickListener(this);
        compositeSubscription = new CompositeSubscription();
        mPresenter = new ExamTargetAreaImpl(compositeSubscription);
        if (titleTextView != null) {
            updateViews(requestType);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    public void updateViews(int requestType) {
        if (titleTextView == null) {
            return;
        }

        this.requestType = requestType;
        updateTitleVg();
    }

    ViewGroup.LayoutParams paramsPar = null;

    // 判断有几个子科目，这里进行显示，小于等于3个，横排显示，多余三个就下拉窗显示
    private void updateTitleVg() {
        // 子元素不超过3个 横排显示，否则弹层显示
        if (!SignUpTypeDataCache.getInstance().isHasChildrenMore()) {
            if (ke_mu_show_lv != null) {
                ke_mu_show_lv.setVisibility(VISIBLE);
            }
            titleTextView.setVisibility(GONE);

            final List<Category.Subject> vars = getChildData();
            if (vars == null) return;

            if (ke_mu_show_lv != null) {
                paramsPar = ke_mu_show_lv.getLayoutParams();
                if (paramsPar == null) {
                    if (vars.size() == 1) {
                        paramsPar = new ViewGroup.LayoutParams(DisplayUtil.dp2px(180), DisplayUtil.dp2px(40));
                    } else {
                        paramsPar = new ViewGroup.LayoutParams(vars.size() * DisplayUtil.dp2px(60), DisplayUtil.dp2px(40));
                    }
                }
            }
            CommonAdapter homeTitleAdapter = new CommonAdapter<Category.Subject>(mContext.getApplicationContext(), vars, R.layout.list_item_homef_title_layout) {
                @Override
                public void convert(ViewHolder holder, Category.Subject item, int position) {
                    convertBind(1, R.id.homef_title_item_tv, holder, item, position);
                    TextView v = holder.getView(R.id.homef_title_item_tv);
                    if (vars.size() == 3) {
                        v.setGravity(Gravity.CENTER_VERTICAL);
                    } else if (vars.size() == 1) {
                        final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
                        lp.width = DisplayUtil.dp2px(180);
                        v.setLayoutParams(lp);
                        v.setGravity(Gravity.CENTER);
                    } else {
                        v.setGravity(Gravity.CENTER);
                    }
                }
            };
            if (ke_mu_show_lv != null) {
                ke_mu_show_lv.setAdapter(homeTitleAdapter);
                if (paramsPar != null) {
                    paramsPar.height = DisplayUtil.dp2px(40);
                    if (vars.size() == 1) {
                        paramsPar.width = DisplayUtil.dp2px(180);
                    } else {
                        paramsPar.width = vars.size() * DisplayUtil.dp2px(60);
                    }
                    ke_mu_show_lv.setLayoutParams(paramsPar);
                }
            }
        } else {
            if (ke_mu_show_lv != null) {
                ke_mu_show_lv.setVisibility(GONE);
            }
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toOpenPopWindow = true;
                    updateTitleCompoundView();
                    updatePBPopWindow();
                }
            });
            updateTitleContent();
            updateTitleCompoundView();
            updatePBPopWindow();
        }
    }

    @Nullable
    private List<Category.Subject> getChildData() {
        SignUpTypeDataCache cache = SignUpTypeDataCache.getInstance();
        if (cache.curCategory == null || cache.curCategory.childrens == null) {
            return null;
        }
        return cache.curCategory.childrens;
    }

    // title显示什么，公务员就是行测/申论。其他去children里取
    public void updateTitleContent() {
        titleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
        if (SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.CIVIL_SERVANT) {
            if (requestType == 0) {
                titleTextView.setText(SignUpTypeDataCache.getInstance().getSubjectTitle(0));
            } else {
                titleTextView.setText(SignUpTypeDataCache.getInstance().getSubjectTitle(1));
            }
        } else {
            titleTextView.setText(SignUpTypeDataCache.getInstance().getSubjectTitle());
        }
    }

    // 下拉弹窗的时候，title显示样式
    private void updateTitleCompoundView() {
        if (SignUpTypeDataCache.getInstance().isHasChildrenMore()) {
            if (toOpenPopWindow) {
                titleTextView.setCompoundDrawables(null, null, DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_up_v62), null);
            } else {
                titleTextView.setCompoundDrawables(null, null, DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_down_v62), null);
            }
        } else {
            titleTextView.setCompoundDrawables(null, null, null, null);
        }
    }

    // 选择科目的弹窗
    private void updatePBPopWindow() {
        if (SignUpTypeDataCache.getInstance().isHasChildrenMore()) {
            if (toOpenPopWindow) {
                final List<Category.Subject> vars = getChildData();
                if (vars == null) return;

                titleTextView.measure(0, 0);
                POP_WINDOW_HEIGHT_DP = vars.size() * POP_WINDOW_ITEM_HEIGHT_DP + 10;
                int xoffdp = (DisplayUtil.px2dp(titleTextView.getMeasuredWidth()) - POP_WINDOW_WIDTH_DP) / 2;
                PopWindowUtil.showPopWindow(mContext, titleTextView, xoffdp, -10, R.layout.layout_pop_homef_title, POP_WINDOW_WIDTH_DP, POP_WINDOW_HEIGHT_DP, new PopWindowUtil.PopViewCall() {

                    @Override
                    public void popViewCall(View contentView, PopupWindow popWindow) {
                        mPopupWindow = popWindow;
                        ListView pop_homef_title_list_view_id = contentView.findViewById(R.id.pop_home_title_list_view_id);
                        CommonAdapter homeTitlePopAdapter = new CommonAdapter<Category.Subject>(mContext.getApplicationContext(), vars, R.layout.list_item_homef_pop_title_layout) {
                            @Override
                            public void convert(ViewHolder holder, final Category.Subject item, final int position) {
                                convertBind(0, R.id.homef_title_pop_item_tv, holder, item, position);
                            }
                        };
                        pop_homef_title_list_view_id.setAdapter(homeTitlePopAdapter);
                    }

                    @Override
                    public void popViewDismiss() {
                        toOpenPopWindow = false;
                        mPopupWindow = null;
                        updateTitleCompoundView();
                    }
                });

            } else {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        }
    }

    // 显示item和item点击事件
    private void convertBind(final int type, int viewId, CommonAdapter.ViewHolder holder, final Category.Subject item, final int position) {
        holder.setText(viewId, item.name);
        SignUpTypeDataCache cache2 = SignUpTypeDataCache.getInstance();

        List<Category.Subject> childData = getChildData();
        // 当前Tab显示红色，如果就一个Tab就不显示红色 .curSubject
        if (item.id == cache2.getCurSubject() && childData != null && getChildData().size() > 1) {
            holder.setTextColor(viewId, ContextCompat.getColor(mContext, R.color.redF3));
        } else {
            holder.setTextColor(viewId, ContextCompat.getColor(mContext, R.color.black001));
        }

        holder.setViewOnClickListener(viewId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyCourseStatistic.clickStatistic("题库", "页面第一模块中间", item.name);

                if (type == 0) {
                    toOpenPopWindow = false;
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }

                SignUpTypeDataCache cache3 = SignUpTypeDataCache.getInstance();
                if (cache3.getCurCategory() == Type.SignUpType.CIVIL_SERVANT) {                      // 公务员
                    if (item.id == Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST) {                     // 行测
                        if (requestType == 1) {                                                     // 在申论下点击行测
                            mPresenter.setUserAreaTypeConfig(5, null, null, item.id, null, null);
                            SpUtils.setUserSubject(Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST);
                            cache3.setCurSubject(Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA));
                        }
                    } else if (item.id == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {        // 申论
                        if (requestType == 0) {                                                         // 在行测下点击申论
                            SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                            cache3.setCurSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY));
                        }
                    }
                } else {                                                                        // 不是公务员
                    if (item.id != Type.PB_ExamType.NTEGRATED_APPLICATION) {                        // 不是综合应用
                        mPresenter.setUserAreaTypeConfig(1, null, null, item.id, null, null);
                    } else {                                                                        // 综合应用跳转新页面
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_START_NTEGRATED_APPLICATION));
                    }
                }
            }
        });
    }

    public void clearView() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void showProgressBar() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).showProgress();
        }
    }

    public void dismissProgressBar() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).hideProgress();
        }
    }

    // 左边按钮点击事件
    @Override
    public void onLeftButtonClick(View view) {
        // 选择考试类型，选择地区
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请检查您的网络设置");
            return;
        }
        StudyCourseStatistic.clickStatistic("题库", "页面第一模块左上角", "考试类型切换");
        ExamTargetAreaActivity.newIntent(mContext, null);
    }

    // 右边2按钮点击事件
    @Override
    public void onRightButton2Click(View view) {

    }

    // 右边按钮点击事件
    @Override
    public void onRightButtonClick(View view) {
        // 搜索页面
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请检查您的网络设置");
            return;
        }
        StudyCourseStatistic.clickSearchBar("题库");
        SearchActivity.newIntent(getContext());
    }
}
