package com.huatu.handheld_huatu.business.faceteach;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.utils.BottomDialog;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceUserInformation;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.bottomdialog.AddressPickerView;
import com.huatu.utils.RegexUtils;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class FillInformationActivity extends BaseActivity {

    @BindView(R.id.tl_name)
    TextInputLayout tlName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.sp_gender)
    Spinner spGender;
    @BindView(R.id.view_gender)
    View viewGender;
    @BindView(R.id.tv_gender_err)
    TextView tvGenderErr;
    @BindView(R.id.tl_phone)
    TextInputLayout tlPhone;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tl_id)
    TextInputLayout tlId;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.tl_email)
    TextInputLayout tlEmail;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.tl_area)
    TextInputLayout tlArea;
    @BindView(R.id.et_area)
    EditText etArea;
    @BindView(R.id.view_area_cover)
    View areaCover;
    @BindView(R.id.tl_address)
    TextInputLayout tlAddress;
    @BindView(R.id.et_address)
    EditText etAddress;

    private FaceUserInformation information;        // 用户信息


    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_fill_information;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(FillInformationActivity.this);

        initEditText();

        information = (FaceUserInformation) originIntent.getSerializableExtra("information");

        if (information == null) {
            information = new FaceUserInformation();
        } else {
            initInformation();
        }

    }

    private void initInformation() {
        etName.setText(information.rname);
        spGender.setSelection(information.sex + 1);
        etPhone.setText(information.tel);
        etId.setText(information.cardid);
        etEmail.setText(information.email);
        etArea.setText(information.addSelect);
        etAddress.setText(information.address);
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_confirm:
                onConfirm();
                break;
        }
    }

    private void onConfirm() {
        boolean isOk = true;

        information.rname = etName.getText().toString();
        if (StringUtils.isEmpty(information.rname)) {
            isOk = false;
            tlName.setError("请输入Name");
        }

        if (information.sex != 0 && information.sex != 1) {
            isOk = false;
            tvGenderErr.setText("请选择性别");
            viewGender.setBackgroundColor(Color.parseColor("#FF6269"));
        }

        information.tel = etPhone.getText().toString();
        if (!RegexUtils.matcherPhone(information.tel)) {
            isOk = false;
            tlPhone.setError("电话格式错误");
        }

        information.cardid = etId.getText().toString();
        if (!RegexUtils.checkIdentityNo(information.cardid)) {
            isOk = false;
            tlId.setError("身份证信息错误");
        }

        information.email = etEmail.getText().toString();
        if (!RegexUtils.matcherEmail(information.email)) {
            isOk = false;
            tlEmail.setError("E-mail格式错误");
        }

        information.addSelect = etArea.getText().toString();
        if (StringUtils.isEmpty(information.addSelect)) {
            isOk = false;
            tlArea.setError("请选择地区");
        }

        information.address = etAddress.getText().toString();
        if (StringUtils.isEmpty(information.address)) {
            isOk = false;
            tlAddress.setError("请填写详细地址");
        }

        if (!isOk) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("information", information);

        SpUtils.setFaceUserInformation(new Gson().toJson(information));

        setResult(ConfirmOrderActivity.INFORMATION_RESULT_CODE, intent);
        finish();
    }

    private void initEditText() {

        etName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.face_infor_name, 0);
        etName.setCompoundDrawablePadding(30);
        etName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tlName.setError("请输入Name");
                } else {
                    tlName.setError("");
                }
            }
        });

        spGender.setDropDownHorizontalOffset(500);
        ArrayList<String> genders = new ArrayList<>(Arrays.asList("性别", "女", "男"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FillInformationActivity.this, android.R.layout.simple_list_item_1, genders);
        spGender.setAdapter(adapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean isFirst = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                information.sex = position - 1;
                if (position > 0) {
                    tvGenderErr.setText("");
                    viewGender.setBackgroundColor(Color.parseColor("#EFEFEF"));
                } else if (isFirst) {
                    tvGenderErr.setText("请选择性别");
                    viewGender.setBackgroundColor(Color.parseColor("#FF6269"));
                } else {
                    isFirst = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.face_infor_phone, 0);
        etPhone.setCompoundDrawablePadding(30);
        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 11) {
                    tlPhone.setError("电话格式有错误");
                } else {
                    tlPhone.setError("");
                }
            }
        });

        etId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.face_infor_id, 0);
        etId.setCompoundDrawablePadding(30);
        etId.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 18) {
                    tlId.setError("身份证号格式有错误");
                } else {
                    tlId.setError("");
                }
            }
        });

        etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.face_infor_email, 0);
        etEmail.setCompoundDrawablePadding(30);
        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tlEmail.setError("E-mail格式错误");
                } else {
                    tlEmail.setError("");
                }
            }
        });

        etArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.homef_title_pop_down, 0);
        etArea.setCompoundDrawablePadding(30);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressBottomDialog();
            }
        };
        areaCover.setOnClickListener(onClickListener);

        etAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tlAddress.setError("请填写详细地址");
                } else {
                    tlAddress.setError("");
                }
            }
        });
    }

    private BottomDialog bottomDialog;

    private void showAddressBottomDialog() {

        // 省市区，本地数据，三联动，待完善
        if (bottomDialog == null) {
            View view = LayoutInflater.from(FillInformationActivity.this).inflate(R.layout.address_picker_layout, null);
            bottomDialog = new BottomDialog(FillInformationActivity.this, view, true, true);
            AddressPickerView addressPicker = view.findViewById(R.id.address_picker_view);

            addressPicker.setTitle("选择地区");

            addressPicker.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
                @Override
                public void onSureClick(String address, String province, String city, String district, String provinceCode, String cityCode, String districtCode) {
                    etArea.setText(address);
                    bottomDialog.dismiss();
                }
            });

            addressPicker.setOnAddressPickerClose(new AddressPickerView.OnAddressPickerCloseListener() {
                @Override
                public void onCloseClick() {
                    bottomDialog.dismiss();
                }
            });
        }
        bottomDialog.show();
    }

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
}
