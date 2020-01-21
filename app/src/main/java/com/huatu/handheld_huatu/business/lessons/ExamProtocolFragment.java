package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolExamUserInfo;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolResultBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.TopActionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2018/4/13.
 */

public class ExamProtocolFragment extends BaseFragment {
    @BindView(R.id.exam_protocol_action_bar)
    TopActionBar topActionBar;
    @BindView(R.id.exam_protocol_name_edit)
    EditText editName;
    @BindView(R.id.exam_protocol_gender_male_img)
    ImageView imgGenderMale;
    @BindView(R.id.exam_protocol_gender_male_tv)
    TextView tvGenderMale;
    @BindView(R.id.exam_protocol_gender_female_img)
    ImageView imgGenderFemale;
    @BindView(R.id.exam_protocol_gender_female_tv)
    TextView tvGenderFemale;
    @BindView(R.id.exam_protocol_phone_edit)
    EditText editPhone;
    @BindView(R.id.exam_protocol_id_card_edit)
    EditText editIdCard;
    @BindView(R.id.exam_protocol_exam_name_edit)
    EditText editExamName;
    @BindView(R.id.exam_protocol_exam_id_edit)
    EditText editExamId;
    @BindView(R.id.exam_protocol_bank_name_edit)
    EditText editBankName;
    @BindView(R.id.exam_protocol_bank_user_name_edit)
    EditText editBankUserName;
    @BindView(R.id.exam_protocol_bank_card_id_edit)
    EditText editBankCardId;
    @BindView(R.id.exam_protocol_agree_img)
    ImageView imgAgree;
    @BindView(R.id.exam_protocol_agree_tv)
    TextView tvAgree;
//    @BindView(R.id.ll_special_protocol)
//    LinearLayout ll_special_protocol;

    boolean isModify = false;
    private int genderFlag = 1;
    private boolean isAgree = true;
    ProtocolExamUserInfo protocolInfo;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_exam_protocol_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        protocolInfo = (ProtocolExamUserInfo) args.getSerializable("protocol_detail");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        String strAgree = "我同意并签订《" + protocolInfo.protocolName + "》";
        SpannableStringBuilder ssb = new SpannableStringBuilder(strAgree);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue5D9)),
                "我同意并签订".length(), strAgree.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgree.setText(ssb);
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

            }
        });
        setGenderState();
    }

    @Override
    protected void onLoadData() {
        if(protocolInfo != null && !TextUtils.isEmpty(protocolInfo.rid)) {
            genderFlag = protocolInfo.sex;
            isModify = true;
            refreshUI();
        }
    }

    private void setGenderState() {
        if(genderFlag == 1) {
            imgGenderMale.setImageResource(R.drawable.icon_checked);
            tvGenderMale.setTextColor(Color.parseColor("#333333"));
            imgGenderFemale.setImageResource(R.drawable.icon_check_normal);
            tvGenderFemale.setTextColor(Color.parseColor("#999999"));
        } else if(genderFlag == 2) {
            imgGenderFemale.setImageResource(R.drawable.icon_checked);
            tvGenderFemale.setTextColor(Color.parseColor("#333333"));
            imgGenderMale.setImageResource(R.drawable.icon_check_normal);
            tvGenderMale.setTextColor(Color.parseColor("#999999"));
        } else {
            imgGenderFemale.setImageResource(R.drawable.icon_check_normal);
            tvGenderFemale.setTextColor(Color.parseColor("#999999"));
            imgGenderMale.setImageResource(R.drawable.icon_check_normal);
            tvGenderMale.setTextColor(Color.parseColor("#999999"));
        }
    }

    private void refreshUI() {
        if(protocolInfo == null) {
            return;
        }
        editName.setText(protocolInfo.studentName);
        setGenderState();
        editPhone.setText(protocolInfo.telNo);
        editIdCard.setText(protocolInfo.idCard);
        editExamName.setText(protocolInfo.forExam);
        editExamId.setText(protocolInfo.examCertifacteNo);
        editBankName.setText(protocolInfo.feeBank);
        editBankUserName.setText(protocolInfo.feeAccountName);
        editBankCardId.setText(protocolInfo.feeAccountNo);
    }

    @OnClick({R.id.exam_protocol_gender_female_img, R.id.exam_protocol_gender_female_tv})
    public void onClickGenderFemale() {
        genderFlag = 2;
        setGenderState();
    }

    @OnClick({R.id.exam_protocol_gender_male_tv, R.id.exam_protocol_gender_male_img})
    public void onClickGenderMale() {
        genderFlag = 1;
        setGenderState();
    }

    @OnClick(R.id.exam_protocol_agree_img)
    public void onClickAgreeImg() {
        if(isAgree) {
            imgAgree.setImageResource(R.drawable.protocol_unckeck);
            isAgree = false;
        } else {
            imgAgree.setImageResource(R.drawable.protocol_checked);
            isAgree = true;
        }
    }

    @OnClick(R.id.exam_protocol_agree_tv)
    public void onClickAgreeTv() {
        String baseUrl = "https://apitk.huatu.com";
        if(RetrofitManager.getInstance().getBaseUrl().contains("ns.huatu.com")) {
            baseUrl = "https://apitk.huatu.com/";
        } else {
            baseUrl = "http://tk.htexam.com/";
        }
        String url = baseUrl + "v3/order/protocolView.php?TreatyId="
                + protocolInfo.protocolId + "&username=" + UserInfoUtil.userName;
        LogUtils.i("url: " + url);
        BaseWebViewFragment fragment = BaseWebViewFragment.newInstance(url,
                protocolInfo.protocolName, null, true, false);
        startFragmentForResult(fragment);
    }

    @OnClick(R.id.exam_protocol_confirm_btn)
    public void onClickConfirm() {
        if(!isAgree) {
            ToastUtils.showShort("请同意并签订协议");
            return;
        }
        String name = editName.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请填写姓名");
            return;
        }
        if(genderFlag != 1 && genderFlag != 2) {
            ToastUtils.showShort("请选择性别");
            return;
        }
        String phone = editPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("请填写手机号码");
            return;
        }
        if(!Method.isPhoneValid(phone)) {
            ToastUtils.showShort("手机号不正确");
            return;
        }
        String idNumber = editIdCard.getText().toString().trim();
        if(TextUtils.isEmpty(idNumber)) {
            ToastUtils.showShort("请填写身份证号");
            return;
        }
        if(!Method.safeIdValid(idNumber)) {
            ToastUtils.showShort("身份证号不正确");
            return;
        }
//        String examName = editExamName.getText().toString().trim();
//        if(TextUtils.isEmpty(examName)) {
//            ToastUtils.showShort("请填写考试名称");
//            return;
//        }
//        String examId = editExamId.getText().toString().trim();
//        if(TextUtils.isEmpty(examId)) {
//            ToastUtils.showShort("请填写准考证号");
//            return;
//        }
        if(protocolInfo == null) {
            protocolInfo = new ProtocolExamUserInfo();
        }
        protocolInfo.studentName = name;
        protocolInfo.sex = genderFlag;
        protocolInfo.telNo = phone;
        protocolInfo.idCard = idNumber;
//        protocolInfo.forExam = examName;
//        protocolInfo.ExamCertifacteNo = examId;
//        protocolInfo.FeeBank = editBankName.getText().toString().trim();
//        protocolInfo.FeeAccountName = editBankUserName.getText().toString().trim();
//        protocolInfo.FeeAccountNo = editBankCardId.getText().toString().trim();
        mActivity.showProgress();
        ServiceProvider.sendProtocolInfo(compositeSubscription, protocolInfo, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                protocolInfo.rid = ((ProtocolResultBean) model.data).rid;
                Intent arg = new Intent();
                arg.putExtra("protocol_detail", protocolInfo);
                setResultForTargetFrg(Activity.RESULT_OK, arg);
            }

            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                ToastUtils.showShort("提交协议失败");
            }
        });
    }
}
