package com.huatu.handheld_huatu.business.ztk_zhibo.address;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.view.OptionsPickerView;
import com.huatu.handheld_huatu.mvpmodel.area.CityModel;
import com.huatu.handheld_huatu.mvpmodel.area.DistrictModel;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceModel;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.AddressUtil;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.bottomdialog.AddressPickerView;
import com.huatu.utils.InputMethodUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saiyuan on 2017/9/29.
 */

public class EditAddressFragment extends BaseFragment {
    TopActionBar topActionBar;
    EditText editReceiver;
    EditText editPhone;
    TextView tvAddress;
    EditText editDetail;
    TextView btnConfirm;
    private OptionsPickerView pvOptions;
    private PopupWindow popupWindow;

    private ArrayList<ProvinceModel> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<CityModel>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<DistrictModel>>> options3Items = new ArrayList<>();
    private ProvinceModel provinceBean;
    private CityModel cityModel;
    private DistrictModel districtModel;
    private boolean isEdit = false;

    private static final int POST_NAME = 0;
    private static final int POST_PHONE = 1;
    private static final int POST_ADDRESS_DETAIL = 2;
    private static final int POST_ADDRESS = 3;
    private AddressInfoBean addressInfoBean;
    private String districtName;
    private String districtId;
    private String cityName;
    private String cityId;
    private String provinceName;
    private String provinceId;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_add_address_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        addressInfoBean = (AddressInfoBean) args.getSerializable("address_info");
        if (addressInfoBean != null) {
            isEdit = true;
        }
        topActionBar = (TopActionBar) rootView.findViewById(R.id.add_address_toolbar_id);
        editReceiver = (EditText) rootView.findViewById(R.id.add_address_receiver_edit);
        editPhone = (EditText) rootView.findViewById(R.id.add_address_phone_edit);
        tvAddress = (TextView) rootView.findViewById(R.id.add_address_address_tv);
        editDetail = (EditText) rootView.findViewById(R.id.add_address_detail_edit);
        btnConfirm = (TextView) rootView.findViewById(R.id.add_address_confirm_btn);
        initTitleBar();
//        initPickView();
        if (isEdit) {
            editReceiver.setText(addressInfoBean.consignee);
            editPhone.setText(addressInfoBean.phone);
            tvAddress.setText(addressInfoBean.province
                    + addressInfoBean.city + addressInfoBean.area);
            editDetail.setText(addressInfoBean.address);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConfirm();
            }
        });
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickSelectAddress();
                Method.hideKeyboard(mActivity.getCurrentFocus());
                tvAddress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAddressPickerPop();
                    }
                }, 200);
            }
        });
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    /**
     * 显示地址选择的pop
     */
    private void showAddressPickerPop() {
        popupWindow = new PopupWindow(mActivity);
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.pop_address_picker, null, false);
        AddressPickerView addressView = (AddressPickerView) rootView.findViewById(R.id.apvAddress);
        addressView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String address, String province, String city, String district, String provinceCode, String cityCode, String districtCode) {
                tvAddress.setText(address);
                districtName = district;
                districtId = districtCode.substring(2);
                cityName = city;
                cityId = cityCode.substring(2);
                provinceName = province;
                provinceId = provinceCode.substring(2);
                hide();
            }
        });

        addressView.setOnAddressPickerClose(new AddressPickerView.OnAddressPickerCloseListener() {
            @Override
            public void onCloseClick() {
                hide();
            }
        });
        popupWindow.setContentView(rootView);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAsDropDown(topActionBar, 0, 0);
        popupWindow.update();

    }

    private void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initTitleBar() {
        if (isEdit) {
            topActionBar.setTitle("编辑收货地址");
        } else {
            topActionBar.setTitle("新增收货地址");
        }
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                InputMethodUtils.hideMethod(getContext(), getView());
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

    private void initPickView() {
        final List<ProvinceModel> provinceList = AddressUtil.getAddressListFromXml();
        try {
            for (ProvinceModel provinceModel : provinceList) {
                options1Items.add(provinceModel);
                ArrayList<CityModel> options2Item = new ArrayList<>();
                options2Item.addAll(provinceModel.getCityList());
                options2Items.add(options2Item);
                ArrayList<ArrayList<DistrictModel>> options3Item2 = new ArrayList<>();
                for (CityModel cityModel : provinceModel.getCityList()) {
                    ArrayList<DistrictModel> options3Item_3 = new ArrayList<>();
                    options3Item_3.addAll(cityModel.getDistrictList());
                    options3Item2.add(options3Item_3);
                }
                options3Items.add(options3Item2);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
        pvOptions = new OptionsPickerView(mActivity);
        pvOptions.setPicker(options1Items, options2Items, options3Items, true);
        pvOptions.setTitle("选择地址");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                try {
                    provinceBean = options1Items.get(options1);
                    cityModel = options2Items.get(options1).get(option2);
                    districtModel = options3Items.get(options1).get(option2).get(options3);
                    tvAddress.setText(provinceBean.getName()
                            + cityModel.getName() + districtModel.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        setResultForTargetFrg(Activity.RESULT_CANCELED, null);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pvOptions != null) {
            pvOptions.dismiss();
            pvOptions = null;
        }
        hide();
    }

    public void onClickSelectAddress() {
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }
        Method.hideKeyboard(mActivity.getCurrentFocus());
        pvOptions.show();
    }

    public void onClickConfirm() {
        if (!checkEditInfo()) {
            return;
        }
        mActivity.showProgress();
        NetResponse response = new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                if (isEdit) {
                    ToastUtils.showShort("修改地址成功");
                } else {
                    addressInfoBean.id = (int) model.data;
                    ToastUtils.showShort("新建地址成功");
                }
                Intent result = new Intent();
                result.putExtra("address_info", addressInfoBean);
                setResultForTargetFrg(Activity.RESULT_OK, result);
            }
        };
        if (addressInfoBean == null) {
            addressInfoBean = new AddressInfoBean();
        }
        try {
            addressInfoBean.address = editDetail.getText().toString();
            addressInfoBean.consignee = editReceiver.getText().toString().trim();
            addressInfoBean.phone = editPhone.getText().toString().trim();
            addressInfoBean.area = districtName;
            addressInfoBean.areaId = districtId;
            addressInfoBean.city = cityName;
            addressInfoBean.cityId = cityId;
            addressInfoBean.isDefault = 0;
            addressInfoBean.province = provinceName;
            addressInfoBean.provinceId = provinceId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isEdit) {
            ServiceProvider.updateAddress(compositeSubscription, addressInfoBean, response);
        } else {
            ServiceProvider.addAddress(compositeSubscription, addressInfoBean, response);
        }

    }

    private boolean checkEditInfo() {
        return checkPost(editReceiver.getText().toString().trim(), POST_NAME)
                && checkPost(editPhone.getText().toString().trim(), POST_PHONE)
                && checkPost(editDetail.getText().toString().trim(), POST_ADDRESS_DETAIL)
                && checkPost(tvAddress.getText().toString(), POST_ADDRESS);
    }

    private boolean checkPost(String string, int type) {
        boolean isTrue = false;
        switch (type) {
            case POST_NAME:
                if (!TextUtils.isEmpty(string)) {
                    if (string.length() >= 2 && string.length() < 16) {
                        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5a-zA-Z]{0,15}");
                        Matcher matcher = pattern.matcher(string);
                        isTrue = matcher.matches();
                        if (!isTrue) {
                            ToastUtils.showShort("收货人:2-15个字符限制，只支持中英文");
                        }
                    } else ToastUtils.showShort("收货人:2-15个字符限制，只支持中英文");
                } else {
                    ToastUtils.showShort("请填写收货人姓名");
                }
                break;
            case POST_PHONE:
                if (!TextUtils.isEmpty(string)) {
                    // Pattern pattern = Pattern.compile("1[34578]{1}\\d{9}$");
                    Pattern pattern = Pattern.compile("^[1]\\d{10}$");
                    Matcher matcher = pattern.matcher(string);
                    isTrue = matcher.matches();
                    if (!isTrue) {
                        ToastUtils.showShort("手机号码无效");
                    }
                } else {
                    ToastUtils.showShort("请填写手机号码");
                }
                break;
            case POST_ADDRESS_DETAIL:
                if (!TextUtils.isEmpty(string)) {
                    isTrue = string.length() >= 5;
                    if (!isTrue) {
                        ToastUtils.showShort("详细地址:5-60个字符限制");
                    }
                } else {
                    ToastUtils.showShort("请填写详细地址");
                }
                break;
            case POST_ADDRESS:
                if (!TextUtils.isEmpty(string)) {
                    isTrue = true;
                } else {
                    ToastUtils.showShort("请选择收货地址");
                    isTrue = false;
                }
                break;
            default:
                break;
        }
        return isTrue;
    }
}
