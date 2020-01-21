package com.huatu.handheld_huatu.business.me.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventListFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.mvppresenter.me.ExamTargetAreaImpl;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 设置考试类型
 */
public class SettingExamTypeFragment extends AbsHtEventListFragment {

    private static String TAG = "SettingExamTypeFragment";

    private ExamTargetAreaImpl mPresenter;
    public int requestType;
    private int catgory;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<ExamTypeAreaMessageEvent> event) {
        if (event == null || event.typeExObject == null) {
            return;
        }
        if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS) {
            if (SignUpTypeDataCache.getInstance().dataList != null) {
                onSuccess(SignUpTypeDataCache.getInstance().dataList, true);
            }
        }
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
    }

    @Override
    public void initAdapter() {
        isPageDivided = false;
        mAdapter = new CommonAdapter<Category>(mActivity.getApplicationContext(), dataList, R.layout.list_item_exam_type_layout) {
            @Override
            public void convert(ViewHolder holder, final Category item, final int position) {
                holder.setText(R.id.item_exam_type_tv, item.name);
                SignUpTypeDataCache var = SignUpTypeDataCache.getInstance();
                if (var != null && var.curCategory != null) {
                    if (item.id == var.curCategory.id) {
                        holder.setViewVisibility(R.id.image_select, View.VISIBLE);
                    } else {
                        holder.setViewVisibility(R.id.image_select, View.GONE);
                    }
                }
                holder.setViewVisibility(R.id.image_jiantou_right, View.VISIBLE);
                holder.setViewOnClickListener(R.id.item_exam_type_rl, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetUtil.isConnected()) {
                            ToastUtils.showShort("网络未连接，请检查您的网络设置");
                            return;
                        }
                        if (!CommonUtils.checkLogin(mActivity)) {
                            return;
                        }
                        catgory = item.id;
                        mPresenter.postEvent(ExamTypeAreaMessageEvent.ETA_MSG_SettingExamTargetAreaFragment_EFORM_SettingExamTypeFragment, catgory);
                    }
                });
            }
        };
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
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mPresenter.finish();
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
        if (SignUpTypeDataCache.getInstance().dataList != null) {
            onSuccess(SignUpTypeDataCache.getInstance().dataList, true);
        } else {
            SignUpTypeDataCache.getInstance().getCategoryListNet(1, compositeSubscription, new
                    BaseMessageEvent<ExamTypeAreaMessageEvent>(ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS,
                    new ExamTypeAreaMessageEvent()));
        }
        isPageDivided = false;
    }

    @Override
    public void onRefresh() {
        listView.stopRefresh();
    }

    @Override
    public void onLoadMore() {
    }

    public static SettingExamTypeFragment newInstance(Bundle extra) {
        SettingExamTypeFragment fragment = new SettingExamTypeFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
