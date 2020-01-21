package com.huatu.handheld_huatu.business.ztk_zhibo.address;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/29.
 */

public class SelectAddressFragment extends BaseListFragment {
    private View bottomView;
    private long selId = 0;
    AddressInfoBean mSelectInfo;

    @Override
    protected void onInitView() {
        super.onInitView();
        listView.setPullRefreshEnable(false);
        selId = args.getInt("selected_address_id", 0);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<AddressInfoBean>(
                dataList, R.layout.select_address_item) {
            @Override
            public void convert(ViewHolder holder, final AddressInfoBean item, int position) {
                if (item.isDefault == 1) {
                    holder.setViewVisibility(R.id.tv_morentv, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.tv_morentv, View.GONE);
                }
                if(item.isSelected) {
                    mSelectInfo = item;
                    holder.setViewVisibility(R.id.iv_address, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.iv_address, View.INVISIBLE);
                }
                if (!TextUtils.isEmpty(item.consignee)) {
                    holder.setText(R.id.tv_shouhuoren, item.consignee);
                }
                if (!TextUtils.isEmpty(item.phone)) {
                    holder.setText(R.id.tv_phone, item.phone);
                }
                if (!TextUtils.isEmpty(item.address) && !TextUtils.isEmpty(item.
                        province) && !TextUtils.isEmpty(item.city)&& !TextUtils.isEmpty(item.area)) {
                    holder.setText(R.id.tv_detail_address, item.province + item.city + item.area + item.address);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("address_info", item);
                        setResultForTargetFrg(Activity.RESULT_OK, intent);
                    }
                });
            }
        };
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        mActivity.showProgress();
        ServiceProvider.getAddressList(compositeSubscription, new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if(e instanceof ApiException) {
                    if(((ApiException) e).getErrorCode() == ApiErrorCode.ERROR_INVALID_DATA) {
                        SelectAddressFragment.this.onSuccess(
                                new ArrayList<AddressInfoBean>(), true);
                        return;
                    }
                }
                SelectAddressFragment.this.onLoadDataFailed();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                mActivity.hideProgress();
                List<AddressInfoBean> tmpList = new ArrayList<>();
                tmpList.addAll(model.data);
                boolean hasSelected = false;
                for(int i = 0; i < tmpList.size(); i++) {
                    if(tmpList.get(i).id == selId) {
                        tmpList.get(i).isSelected = true;
                        hasSelected = true;
                    }
                }
                if(!hasSelected && tmpList.size() > 0){
                    tmpList.get(0).isSelected = true;
                }
                SelectAddressFragment.this.onSuccess(tmpList, true);
            }
        });
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
        topActionBar.setTitle("选择收货地址");
        topActionBar.showButtonText("管理", TopActionBar.RIGHT_AREA);
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
                AddressManageFragment fragment = new AddressManageFragment();
                startFragmentForResult(fragment);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK ) {
            onLoadData();
        }
    }

    @Override
    public boolean onBackPressed() {
        if(dataList!=null && dataList.size()>0) {
            Intent intent =new Intent();
            intent.putExtra("address_list_size",1);
            intent.putExtra("address_info", mSelectInfo == null ? (AddressInfoBean)dataList.get(0) : mSelectInfo);
            setResultForTargetFrg(Activity.RESULT_CANCELED, intent);
        }else {
            Intent intent =new Intent();
            intent.putExtra("address_list_size",0);
            setResultForTargetFrg(Activity.RESULT_CANCELED, intent);
        }
        return true;
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

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
}
