package com.huatu.handheld_huatu.business.ztk_zhibo.address;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/29.
 */

public class AddressManageFragment extends BaseListFragment {
    private CustomConfirmDialog dialDlg;
    private boolean isChanged = false;
    private View bottomView;

    @Override
    protected void onInitView() {
        super.onInitView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray_divider)));
        listView.setDividerHeight(1);
        isPageDivided = false;
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        topActionBar.setTitle("管理收货地址");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<AddressInfoBean>(
                dataList, R.layout.search_address_item) {
            @Override
            public void convert(ViewHolder holder, final AddressInfoBean item, final int position) {
                if (!TextUtils.isEmpty(item.consignee)) {
                    holder.setText(R.id.tv_shouhuoren, item.consignee);
                }
                if (!TextUtils.isEmpty(item.phone)) {
                    holder.setText(R.id.tv_phone, item.phone);
                }
                if (!TextUtils.isEmpty(item.address) && !TextUtils.isEmpty(item.province)
                        && !TextUtils.isEmpty(item.city)&& !TextUtils.isEmpty(item.area)) {
                    holder.setText(R.id.tv_address, item.province + item.city + item.area + item.address);
                }
                if (item.isDefault == 1) {
                    holder.setViewImageResource(R.id.iv_moren, R.drawable.zf_icon_b3x);
                    holder.setText(R.id.tv_moren, "默认地址");
                } else {
                    holder.setViewImageResource(R.id.iv_moren, R.drawable.zf_icon_bb);
                    holder.setText(R.id.tv_moren, "设为默认地址");
                }

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.isDefault != 1) {
                            setDefaultAddress(item, position);
                        }
                    }
                };
                holder.setViewOnClickListener(R.id.iv_moren, onClickListener);
                holder.setViewOnClickListener(R.id.tv_moren, onClickListener);
                holder.setViewOnClickListener(R.id.bt_bianji, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditAddressFragment editAddressFragment = new EditAddressFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("address_info", item);
                        editAddressFragment.setArguments(bundle);
                        startFragmentForResult(editAddressFragment);
                    }
                });
                holder.setViewOnClickListener(R.id.bt_shanchu, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteConfirm(item.id, position);
                    }
                });
            }
        };
    }

    private void deleteConfirm(final long addressId, final int position) {
        if(dialDlg == null) {
            dialDlg = DialogUtils.createDialog(mActivity,
                    "提示", "确认要删除此收货地址吗？");
        }
        dialDlg.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(addressId, position);
            }
        });
        dialDlg.show();
    }

    private void deleteAddress(final long addressId, final int position) {
        showProgressBar();
        ServiceProvider.deleteAddress(compositeSubscription, addressId, new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                dismissProgressBar();
                isChanged = true;
                if(position < dataList.size()) {
                    dataList.remove(position);
                }
                List<AddressInfoBean> tmpList = new ArrayList<>();
                tmpList.addAll(dataList);
                AddressManageFragment.this.onSuccess(tmpList, true);
                showEmpty();
                onLoadData();
            }
        });
    }

    private void showEmpty() {
        layoutErrorView.setErrorImage(R.drawable.dizhitu);
        layoutErrorView.setErrorImageVisible(true);
        layoutErrorView.setErrorImageMargin(130);
        layoutErrorView.setErrorText("您还没有添加收货地址哦~");
    }

    private void setDefaultAddress(final AddressInfoBean addressInfoBean, final int position) {
        mActivity.showProgress();
        addressInfoBean.isDefault = 1;
        ServiceProvider.updateAddress(compositeSubscription, addressInfoBean, new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                isChanged = true;
                if(position < dataList.size()) {
                    for(int i = 0; i < dataList.size(); i++) {
                        ((AddressInfoBean)dataList.get(i)).isDefault = 0;
                    }
                    ((AddressInfoBean)dataList.get(position)).isDefault = 1;
                }
                mAdapter.setDataAndNotify(dataList);
            }
        });
    }

    @Override
    protected void onLoadData() {
        showProgressBar();
        ServiceProvider.getAddressList(compositeSubscription, new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                onLoadDataFailed();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                dismissProgressBar();
                AddressManageFragment.this.onSuccess(model.data,true);
                showEmpty();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK  && data != null) {
            if (requestCode == BaseFragment.REQUEST_FRAGMENT_RESULT) {
                isChanged = true;
                onLoadData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dialDlg != null) {
            dialDlg.dismiss();
            dialDlg = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        if(isChanged) {
            setResultForTargetFrg(Activity.RESULT_OK, null);
        } else {
            setResultForTargetFrg(Activity.RESULT_CANCELED, null);
        }
        return true;
    }

    @Override
    public boolean isBottomButtons() {
        return true;
    }

    @Override
    public View getBottomLayout() {
        bottomView = mLayoutInflater.inflate(R.layout.select_address_bottom_layout, null, false);
        bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAddressFragment editAddressFragment = new EditAddressFragment();
                startFragmentForResult(editAddressFragment);
            }
        });
        return bottomView;
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        topActionBar.setTitle("管理收货地址");
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                onBackPressed();
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
    public void showProgressBar() {
        mActivity.showProgress();
    }

    @Override
    public void dismissProgressBar() {
        mActivity.hideProgress();
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onRefresh() {
        listView.stopRefresh();
    }

    @Override
    public void onLoadMore() {

    }

    public static void newInstance(Context context) {
        BaseFrgContainerActivity.newInstance(context,
                AddressManageFragment.class.getName(), null);
    }
}
