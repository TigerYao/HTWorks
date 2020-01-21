package com.huatu.handheld_huatu.business.me.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventListFragment;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.mvppresenter.me.ExamTargetAreaImpl;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * First设置考试类型
 * 公务员、事业单位、教室招聘、招警考试、遴选...
 */

public class SettingExamTypeFromFirstFragment extends AbsHtEventListFragment {

    private static String TAG = "SettingExamTypeFromFirstFragment";

    @BindView(R.id.jump_to)
    protected TextView jump_to;

    private ExamTargetAreaImpl mPresenter;
    public int requestType;

    private Integer catgory = null;
    private Integer subject = null;
    private int examType;
    private int typePre = -1, typeSel = -1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<ExamTypeAreaMessageEvent> event) {
        if (event == null || event.typeExObject == null) {
            return;
        }
        if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS_from_SettingExamTypeFromFirstFragment) {
            if (mActivity != null) {
                MainTabActivity.newIntent(mActivity);
                ActivityStack.getInstance().finishAllActivityExMain();
                mActivity.finish();
            }
        } else if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS) {
            if (SignUpTypeDataCache.getInstance().dataList != null) {
                onSuccess(SignUpTypeDataCache.getInstance().dataList, true);
            }
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_setting_exam_type_firstlayout;
    }

    @Override
    protected void onInitView() {
        if (args != null) {
            requestType = args.getInt("request_type");
            Bundle extraArgs = args.getBundle("extra_args");
        }
        super.onInitView();
        mPresenter = new ExamTargetAreaImpl(compositeSubscription);
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray_divider)));
        listView.setDividerHeight(1);
        isPageDivided = false;
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        jump_to.setVisibility(View.GONE);
    }

    @Override
    public void initAdapter() {
        isPageDivided = false;
        mAdapter = new CommonAdapter<Category>(mActivity.getApplicationContext(), dataList, R.layout.list_item_exam_type_layout) {
            @Override
            public void convert(ViewHolder holder, final Category item, final int position) {
                holder.setText(R.id.item_exam_type_tv, item.name);
                holder.setViewVisibility(R.id.image_jiantou_right, View.GONE);
                if (position == typeSel) {
                    holder.setViewVisibility(R.id.image_select, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.image_select, View.GONE);
                }
                holder.setViewOnClickListener(R.id.item_exam_type_rl, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetUtil.isConnected()) {
                            ToastUtils.showShort("网络未连接，请检查您的网络设置");
                            return;
                        }
                        typeSel = position;
                        if (item != null) {
                            catgory = item.id;
                            if (item.childrens != null && item.childrens.get(0) != null) {
                                subject = item.childrens.get(0).id;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        refreshTitle();
                    }
                });
            }
        };
    }

    public void refreshTitle() {
        if (typePre != typeSel) {
            topActionBar.showButtonText("完成", TopActionBar.RIGHT_AREA, R.color.common_style_text_color);
        } else {
            topActionBar.showButtonText("完成", TopActionBar.RIGHT_AREA, R.color.new_tv_night_font);
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
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("设置考试类型");
        topActionBar.showButtonText("完成", TopActionBar.RIGHT_AREA);
        topActionBar.showButtonImage(-1, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {

            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (!NetUtil.isConnected()) {
                    CommonUtils.showToast("网络未连接，请检查您的网络");
                    return;
                }
                if (typePre != typeSel && catgory != null) {
                    mPresenter.setUserAreaTypeConfig(3, catgory, null, SignUpTypeDataCache.getInstance().fromCategory2Subject(catgory), null, null);
                } else {
                    Toast.makeText(mActivity, "您还未选择考试类型", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onLoadData() {
        if (SignUpTypeDataCache.getInstance().dataList != null) {
            onSuccess(SignUpTypeDataCache.getInstance().dataList, true);
        } else {
            SignUpTypeDataCache.getInstance().getCategoryListNet(1, compositeSubscription, new
                    BaseMessageEvent<ExamTypeAreaMessageEvent>(ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS,
                    new ExamTypeAreaMessageEvent()));
        }
        isPageDivided = false;
    }

    @OnClick({R.id.jump_to})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jump_to:
                if (mActivity != null) {
                    mActivity.finish();
                }
                MainTabActivity.newIntent(mActivity, 0);
                break;
        }
    }

    @Override
    public void onRefresh() {
        listView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
    }

    public static SettingExamTypeFromFirstFragment newInstance(Bundle extra) {
        SettingExamTypeFromFirstFragment fragment = new SettingExamTypeFromFirstFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
