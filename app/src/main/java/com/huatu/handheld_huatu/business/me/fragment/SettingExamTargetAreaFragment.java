package com.huatu.handheld_huatu.business.me.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventListFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceBeanList;
import com.huatu.handheld_huatu.mvppresenter.me.ExamTargetAreaImpl;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 请选择要参加的考试页面
 * 这里选择地区。全国、北京、天津、河北、山西、内蒙古...
 */

public class SettingExamTargetAreaFragment extends AbsHtEventListFragment {

    private static final String TAG = "SettingExamTargetAreaFragment";
    public static final String ACTIVITYFROM = "activityfrom";

    private int areaId;//考试区域的id
    private String areaName;//考试区域的名字
    private ExamTargetAreaImpl mPresenter;
    protected int requestType;
    private Integer catgory = null;
    private Integer subject = null;
    private boolean isHasArea = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<ExamTypeAreaMessageEvent> event) {
        super.onEventUIUpdate(event);
        if (event == null || mPresenter == null || event.typeExObject == null) {
            return;
        }
        if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_AREA_LIST_SUCCESS) {
            if (mPresenter.getDataList() != null && mPresenter.getDataList().getAreas() != null) {
                areaId = mPresenter.getDataList().getUserArea();
                onSuccess(mPresenter.getDataList().getAreas(), true);
            }
        } else if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS) {
            onSetData();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ONLOAD_DATA_FAILED) {

            onLoadDataFailed();
            layoutErrorView.setErrorImageVisible(true);
            layoutErrorView.setErrorText("网络不太好，点击屏幕，刷新看看");
            layoutErrorView.setErrorImage(R.drawable.icon_common_net_unconnected);
        }
    }

    @Override
    protected void onInitView() {
        //获取考试区域名字和id

        super.onInitView();
        if (args != null) {
            requestType = args.getInt("request_type");
            catgory = args.getInt("obj0");
        }
        areaId = SpUtils.getArea(catgory);
        areaName = SpUtils.getAreaname();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray_divider)));
        listView.setDividerHeight(1);
        isPageDivided = false;
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        mPresenter = new ExamTargetAreaImpl(compositeSubscription);
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("请选择需要参加的考试");
        topActionBar.showButtonText("完成", TopActionBar.RIGHT_AREA);
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mPresenter.onBack();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (!NetUtil.isConnected()) {
                    CommonUtils.showToast("网络未连接，请检查您的网络设置");
                    return;
                }
                if (!isHasArea) {
                    CommonUtils.showToast("请选择需要参加的考试地区");
                    return;
                }
                if (catgory != null) {
                    subject = SignUpTypeDataCache.getInstance().fromCategory2Subject(catgory);
                    mPresenter.setUserAreaTypeConfig(0, catgory, areaId, subject, null, null);
                }
            }
        });
    }

    @Override
    public void initAdapter() {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        mAdapter = new CommonAdapter<ProvinceBeanList.AreasEntity>(mActivity.getApplicationContext(), dataList, R.layout.item_target_city) {
            @Override
            public void convert(ViewHolder holder, final ProvinceBeanList.AreasEntity item, int position) {
                if (!TextUtils.isEmpty(item.getName())) {
                    holder.setText(R.id.text_city_name, item.getName());
                } else {
                    holder.setText(R.id.text_city_name, "");
                }
                if (areaId == item.getId()) {
                    isHasArea = true;
                    holder.setViewVisibility(R.id.image_select, View.VISIBLE);
                    areaId = item.getId();
                    areaName = item.getName();
                } else {
                    holder.setViewVisibility(R.id.image_select, View.INVISIBLE);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areaId = item.getId();
                        areaName = item.getName();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    public void onSetData() {
        SpUtils.setAreaname(areaName);
        SpUtils.setArea(areaId);
        mPresenter.finish();
    }

    @Override
    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        mPresenter.getTargetAreaList(catgory);
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
    public void onRefresh() {
        listView.stopRefresh();
    }

    @Override
    public void onLoadMore() {

    }

    public static SettingExamTargetAreaFragment newInstance(Bundle extra) {
        SettingExamTargetAreaFragment fragment = new SettingExamTargetAreaFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
