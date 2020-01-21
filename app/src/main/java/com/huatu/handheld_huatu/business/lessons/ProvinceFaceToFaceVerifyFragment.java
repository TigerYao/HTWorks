package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmOrderFragment;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2018/3/28.
 */

public class ProvinceFaceToFaceVerifyFragment extends BaseFragment {
    @BindView(R.id.face_to_face_verify_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.face_to_face_apply_tv)
    TextView tvApply;
    @BindView(R.id.face_to_face_apply_line_1)
    View viewApplyLine1;
    @BindView(R.id.face_to_face_apply_view)
    View viewApplyCircle;
    @BindView(R.id.face_to_face_apply_line_2)
    View viewApplyLine2;
    @BindView(R.id.face_to_face_verify_tv)
    TextView tvVerify;
    @BindView(R.id.face_to_face_verify_line_1)
    View viewVerifyLine1;
    @BindView(R.id.face_to_face_verify_view)
    View viewVerifyCircle;
    @BindView(R.id.face_to_face_verify_line_2)
    View viewVerifyLine2;
    @BindView(R.id.face_to_face_pass_tv)
    TextView tvPass;
    @BindView(R.id.face_to_face_verify_success_layout)
    ViewGroup layoutSuccess;
    @BindView(R.id.face_to_face_verify_fail_layout)
    ViewGroup layoutFail;
    @BindView(R.id.face_to_face_phone_edit)
    EditText editPhone;
    @BindView(R.id.face_to_face_phone_error_tv)
    TextView tvPhoneError;
    @BindView(R.id.face_to_face_id_edit)
    EditText editId;
    @BindView(R.id.face_to_face_id_error_tv)
    TextView tvIdError;
    @BindView(R.id.face_to_face_verify_price_tv)
    TextView tvPrice;
    @BindView(R.id.face_to_face_verify_origin_price_tv)
    TextView tvOriginPrice;

    private boolean isPass = false;
    private int color46bb8c = 0;
    private String strPhone;
    private String strId;
    private String strPrice;
    private String strOriginPrice;
    private String courseId;
    private int sid;
    private CustomConfirmDialog confirmDialog;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_face_to_face_verify_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        sid = args.getInt("sid");
        courseId = args.getString("course_id");
        isPass = args.getBoolean("is_verify_passed");
        strPrice = args.getString("cur_price");
        strOriginPrice = args.getString("origin_price");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_CANCELED);
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_OK);
            }
        });
        color46bb8c = Color.parseColor("#46bb8c");
        if (isPass) {
            viewApplyCircle.setVisibility(View.GONE);
            viewApplyLine2.setVisibility(View.VISIBLE);
            viewApplyLine2.setBackgroundColor(color46bb8c);
            tvVerify.setBackgroundResource(R.drawable.shape_circle_46bb8c);
            tvVerify.setTextColor(Color.parseColor("#ffffff"));
            viewVerifyLine1.setBackgroundColor(color46bb8c);
            viewVerifyCircle.setVisibility(View.VISIBLE);
            viewVerifyCircle.setBackgroundResource(R.drawable.shape_circle_46bb8c);
            layoutSuccess.setVisibility(View.VISIBLE);
            layoutFail.setVisibility(View.GONE);
            tvPhoneError.setVisibility(View.GONE);
            tvIdError.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(strOriginPrice) && !Method.isEqualString(strOriginPrice, strPrice)) {
                tvOriginPrice.setVisibility(View.VISIBLE);
            } else {
                tvOriginPrice.setVisibility(View.GONE);
            }
            tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvOriginPrice.setText("¥" + strOriginPrice);
            tvPrice.setText("¥" + strPrice);
        } else {
            viewApplyCircle.setVisibility(View.VISIBLE);
            tvVerify.setBackgroundResource(R.drawable.shape_circle_dddddd);
            tvVerify.setTextColor(Color.parseColor("#f16140"));
            viewVerifyCircle.setVisibility(View.GONE);
            layoutSuccess.setVisibility(View.GONE);
            layoutFail.setVisibility(View.VISIBLE);
        }
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 11) {
                    strPhone = editPhone.getText().toString().trim();
                    if(!Method.isPhoneValid(strPhone)) {
                        tvPhoneError.setVisibility(View.VISIBLE);
                    } else {
                        tvPhoneError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    strPhone = editPhone.getText().toString().trim();
                    if(TextUtils.isEmpty(strPhone)) {
                        ToastUtils.showShort("请输入手机号码");
                        return;
                    }
                    if(!Method.isPhoneValid(strPhone)) {
                        tvPhoneError.setVisibility(View.VISIBLE);
                    } else {
                        tvPhoneError.setVisibility(View.GONE);
                    }
                }
            }
        });
        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 18) {
                    strId = editId.getText().toString().trim();
                    if(!Method.isIdValid(strId)) {
                        tvIdError.setVisibility(View.VISIBLE);
                    } else {
                        tvIdError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    strId = editId.getText().toString().trim();
                    if(TextUtils.isEmpty(strId)) {
                        ToastUtils.showShort("请输入身份证号码");
                        return;
                    }
                    if(!Method.isIdValid(strId)) {
                        tvIdError.setVisibility(View.VISIBLE);
                    } else {
                        tvIdError.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @OnClick(R.id.face_to_face_verify_buy_btn)
    public void onClickBuy() {
        strPhone = editPhone.getText().toString().trim();
        strId = editId.getText().toString().trim();
        if(TextUtils.isEmpty(strPhone)) {
            ToastUtils.showShort("请输入手机号码");
            return;
        }
        if(TextUtils.isEmpty(strId)) {
            ToastUtils.showShort("请输入身份证号码");
            return;
        }
        if(!Method.isPhoneValid(strPhone)) {
            tvPhoneError.setVisibility(View.VISIBLE);
            return;
        } else {
            tvPhoneError.setVisibility(View.GONE);
        }
        if(!Method.isIdValid(strId)) {
            tvIdError.setVisibility(View.VISIBLE);
            return;
        } else {
            tvIdError.setVisibility(View.GONE);
        }
        mActivity.showProgress();
        ServiceProvider.fillFaceToFaceInfo(compositeSubscription, String.valueOf(sid),
                strPhone, strId, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                Bundle arg = new Bundle();
                arg.putString("course_id", courseId);
                ConfirmOrderFragment fragment = new ConfirmOrderFragment();
                fragment.setArguments(arg);
                startFragmentForResult(fragment);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            setResultForTargetFrg(Activity.RESULT_OK);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
    }

    @OnClick(R.id.face_to_face_verify_get_course_btn)
    public void onClickGetCourse() {
        ProvinceFaceToFaceGetCourseFragment fragment = new ProvinceFaceToFaceGetCourseFragment();
        startFragmentForResult(fragment);
    }

}
