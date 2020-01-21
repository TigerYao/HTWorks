package com.huatu.handheld_huatu.business.ztk_vod.highmianshou;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
//import com.huatu.handheld_huatu.business.lessons.MySingleTypeCourseFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HighEndBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.MianShouInfoBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.setMianShouBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmOrderFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht-djd on 2017/12/9.
 * 高端面试课
 */

public class HighMianShouActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.high_mianshi_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.high_mianshi_name_edit)
    EditText editName;
    @BindView(R.id.high_mianshi_phone_edit)
    EditText editPhone;
    @BindView(R.id.high_mianshi_shenfen_edit)
    EditText editShenfen;
    @BindView(R.id.high_mianshi_zhunkao_edit)
    EditText editZhunkao;
    @BindView(R.id.high_mianshi_baokao_edit)
    EditText editBaokao;
    @BindView(R.id.high_mianshi_chengji_edit)
    EditText editChengji;
    @BindView(R.id.high_mianshi_gender_female)
    TextView btnFemale;
    @BindView(R.id.high_mianshi_gender_male)
    TextView btnMale;
    @BindView(R.id.v_blank_sort)
    View v_blank_sort;
    private HighMianShouActivity activity;
    private String userName;
    private int genderFlag = 0;//默认女 0女 1男
    private String phone;
    private String shenfen;
    private String zhunkao;
    private String baokao;
    private String chengji;
    private String rid;
    private int goodsId;
    private MianShouInfoBean.DataBean mianshoubean;
    private String sex;
    private PopupWindow popupWindow;
    private PopupWindow buypopupWindow;
    protected CustomConfirmDialog confirmDialog;
    private TextView tv_hide_title;
    private Button bt_hide;
    private HighEndBean highEndBean;
    protected CompositeSubscription compositeSubscription;
    private String pageSource;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_highmianshou;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        activity = HighMianShouActivity.this;
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        ButterKnife.bind(activity);
        rid = getIntent().getStringExtra("rid");
        pageSource = getIntent().getStringExtra("from");
        goodsId = getIntent().getIntExtra("goodsId", 0);
        initView();
        initTitleBar();
        highEndBean = new HighEndBean();
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        activity.showProgress();
        DataController.getInstance().getHighend()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MianShouInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MianShouInfoBean mianShouInfoBean) {
                        if (mianShouInfoBean.code == 1000000) {
                            mianshoubean = mianShouInfoBean.data;
                            activity.hideProgress();
                            refreshUI();
                        }
                    }

                });
    }

    private void refreshUI() {
        if (mianshoubean == null) {
            return;
        }
        if (!TextUtils.isEmpty(mianshoubean.nickName)) {
            editName.setText(mianshoubean.nickName);
        }
        if (!TextUtils.isEmpty(mianshoubean.sex)) {
            if (Method.isEqualString("2", mianshoubean.sex)) {
                genderFlag = 0;
            } else {
                genderFlag = 1;
            }
            setGenderState();
        }
        if (!TextUtils.isEmpty(mianshoubean.phone)) {
            editPhone.setText(mianshoubean.phone);
        }
        if (!TextUtils.isEmpty(mianshoubean.identifyID)) {
            editShenfen.setText(mianshoubean.identifyID);
        }
        if (!TextUtils.isEmpty(mianshoubean.admission_student)) {
            editZhunkao.setText(mianshoubean.admission_student);
        }
        if (!TextUtils.isEmpty(mianshoubean.SN)) {
            editBaokao.setText(mianshoubean.SN);
        }
        if (!TextUtils.isEmpty(mianshoubean.studentScore)) {
            editChengji.setText(mianshoubean.studentScore);
        }
    }

    private void initView() {
        btnMale.setOnClickListener(this);
        btnFemale.setOnClickListener(this);
        editChengji.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void initTitleBar() {
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
                onClickConfirm();
            }

        });
    }

    private void onClickConfirm() {
        Method.hideKeyboard(activity.getCurrentFocus());
        userName = editName.getText().toString().trim();
        baokao = editBaokao.getText().toString().trim();
        zhunkao = editZhunkao.getText().toString().trim();
        chengji = editChengji.getText().toString().trim();
        //校验姓名
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort("请填写姓名");
            return;
        }
        //校验手机号
        if (!checkPhone()) {
            return;
        }
        //校验身份证号
        if (!checkShenfen()) {
            return;
        }
        //校验准考证号
        if (TextUtils.isEmpty(zhunkao)) {
            ToastUtils.showShort("请填写准考证号");
            return;
        }
        //校验报名序列号
        if (TextUtils.isEmpty(baokao)) {
            ToastUtils.showShort("请填写报名序列号");
            return;
        }
        //校验综合成绩
        if (TextUtils.isEmpty(chengji)) {
            ToastUtils.showShort("请填写综合成绩");
            return;
        }
        if (genderFlag == 0) {
            sex = "2";
        } else {
            sex = "1";
        }
        highEndBean.SN = baokao;
        highEndBean.admission_student = zhunkao;
        highEndBean.identifyID = shenfen;
        highEndBean.nickName = userName;
        highEndBean.phone = phone;
        highEndBean.rid = rid;
        highEndBean.sex = sex;
        highEndBean.studentScore = chengji;
        Log.e("shuju",baokao+"``"+zhunkao);
        DataController.getInstance().setHighend(highEndBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<setMianShouBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e("onError", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", e.getMessage());
                    }

                    @Override
                    public void onNext(setMianShouBean highendInfoBean) {
                        Log.e("getcode",highendInfoBean.getCode()+"``"+highEndBean.nickName+"``"+highEndBean.SN
                        +"``"+highEndBean.admission_student);
                        if (highendInfoBean.getCode() == 1000000) {
                            showPopupWindow(highendInfoBean.getData());
                        } else if (highendInfoBean.getCode() == -11) {
                            showMianShouDialog("很抱歉，未查询到您入围面试，不符合本课程招生条件。");
                        } else if (highendInfoBean.getCode() == -12) {
                            showMianShouDialog("很抱歉，您的成绩不符合本课程招生条件。");
                        } else if (highendInfoBean.getCode() == -13) {
                            showBuyDialog();
                        } else if (highendInfoBean.getCode() == -15) {
                            showMianShouDialog("很抱歉，您来晚啦！您所在的职位名额已招满 ");
                        }
                    }

                });
    }

    private void showBuyDialog() {
        if (buypopupWindow == null) {
            View contentView = LayoutInflater.from(activity).inflate(
                    R.layout.highend_diss_window, null);
            buypopupWindow = new PopupWindow(contentView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            buypopupWindow.setFocusable(true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            buypopupWindow.setBackgroundDrawable(dw);
            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = 0.4f;
            activity.getWindow().setAttributes(lp);
            buypopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1f;
                    activity.getWindow().setAttributes(lp);
                }
            });
            bt_hide = (Button) contentView.findViewById(R.id.bt_dialog_sure);
            tv_hide_title = (TextView) contentView.findViewById(R.id.tv_dialog_content_top);

        }
        tv_hide_title.setText("很抱歉，您已购买过同种类型的高端面试课，不能重复购买 ");
        bt_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BuyDetailsActivity.newIntent(activity, rid);
                BaseIntroActivity.newIntent(activity,rid);
                activity.finish();
            }
        });
        buypopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void showMianShouDialog(String errorString) {
        confirmDialog = DialogUtils.createExitConfirmDialog(activity, null,
                errorString, "核实信息", "领取赠送课程");
        confirmDialog.setContentGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 获取赠送课程
                 */
                if (NetUtil.isConnected()) {
                    activity.showProgress();
                    DataController.getInstance().getReceiveHighend()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<BaseResponseModel<String>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    activity.hideProgress();
                                }

                                @Override
                                public void onNext(BaseResponseModel<String> stringBaseResponseModel) {
                                    activity.hideProgress();
                                    if (stringBaseResponseModel.code == 1000000) {
                                       // UIJumpHelper.jumpFragment(HighMianShouActivity.this, MySingleTypeCourseFragment.class);
                                        UIJumpHelper.startStudyPage(HighMianShouActivity.this);
                                       // MyPurchasedFragment.newInstance(0);
                                        activity.finish();
                                    }
                                }
                            });
                } else {
                    CommonUtils.showToast("网络错误，请检查您的网络");
                }


            }
        });
        confirmDialog.show();
    }

    private void showPopupWindow(setMianShouBean.DataBean data) {
        View customView = LayoutInflater.from(activity).inflate(R.layout.popupwindow_highend, null, false);
        TextView tv_tips = (TextView) customView.findViewById(R.id.tv_tips);
        TextView tv_department = (TextView) customView.findViewById(R.id.tv_department);
        TextView tv_departCode = (TextView) customView.findViewById(R.id.tv_departCode);
        TextView tv_position = (TextView) customView.findViewById(R.id.tv_position);
        TextView tv_positionCode = (TextView) customView.findViewById(R.id.tv_positionCode);
        TextView tv_mianshiScore = (TextView) customView.findViewById(R.id.tv_mianshiScore);
        TextView bt_queren = (TextView) customView.findViewById(R.id.bt_queren);
        ImageView iv_guanbi = (ImageView) customView.findViewById(R.id.iv_guanbi);
        bt_queren.setOnClickListener(this);
        iv_guanbi.setOnClickListener(this);
        if (data == null) {
            return;
        }
        if (!TextUtils.isEmpty(data.getTips())) {
            tv_tips.setText(data.getTips());
        }
        if (!TextUtils.isEmpty(data.getDepartment())) {
            tv_department.setText(data.getDepartment());
        }
        if (!TextUtils.isEmpty(data.getDepartCode())) {
            tv_departCode.setText(data.getDepartCode());
        }
        if (!TextUtils.isEmpty(data.getPosition())) {
            tv_position.setText(data.getPosition());
        }
        if (!TextUtils.isEmpty(data.getPositionCode())) {
            tv_positionCode.setText(data.getPositionCode());
        }
        if (!TextUtils.isEmpty(data.getMianshiScore())) {
            tv_mianshiScore.setText(data.getMianshiScore());
        }
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(customView, Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        v_blank_sort.setVisibility(View.VISIBLE);
    }

    private void hidePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.setFocusable(false);
            popupWindow.dismiss();
            v_blank_sort.setVisibility(View.GONE);
        }
    }

    private boolean checkPhone() {
        boolean isTrue = false;
        phone = editPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            Pattern pattern = Pattern.compile("1[34578]{1}\\d{9}$");
            Matcher matcher = pattern.matcher(phone);
            isTrue = matcher.matches();
            if (!isTrue) {
                ToastUtils.showShort("手机号码无效");
            }
        } else {
            ToastUtils.showShort("请填写手机号码");
        }
        return isTrue;

    }

    private boolean checkShenfen() {
        boolean isTrue = false;
        shenfen = editShenfen.getText().toString().trim();
        if (!TextUtils.isEmpty(shenfen)) {
            isTrue = IdcardUtil.isIdcard(shenfen);
            if (!isTrue) {
                ToastUtils.showShort("身份验证有误");
            }
        } else {
            ToastUtils.showShort("请填写身份证号码");
        }
        return isTrue;
    }

    private boolean checkChengji() {
        boolean isTrue = false;
        chengji = editChengji.getText().toString().trim();
        if (!TextUtils.isEmpty(chengji)) {
            Pattern pattern = Pattern.compile("^([1-9]\\d?(\\.\\d{1,2})?|0.\\d{1,2}|100)$");
            Matcher matcher = pattern.matcher(chengji);
            isTrue = matcher.matches();
            if (!isTrue) {
                ToastUtils.showShort("综合成绩有误");
            }
        } else {
            ToastUtils.showShort("请填写综合成绩");
        }
        return isTrue;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.high_mianshi_gender_female:
                Method.hideKeyboard(activity.getCurrentFocus());
                if (genderFlag != 0) {
                    genderFlag = 0;
                    setGenderState();
                }
                break;
            case R.id.high_mianshi_gender_male:
                Method.hideKeyboard(activity.getCurrentFocus());
                if (genderFlag != 1) {
                    genderFlag = 1;
                    setGenderState();
                }
                break;
            case R.id.iv_guanbi:
                hidePopupWindow();
                break;
            case R.id.bt_queren:
                ConfirmOrderFragment.newInstance(activity, null, goodsId, rid, pageSource);
                activity.finish();
                break;
        }
    }

    private void setGenderState() {
        Drawable drawableChecked = getResources().getDrawable(R.drawable.icon_checked);
        Drawable drawableUnChecked = getResources().getDrawable(R.drawable.icon_check_normal);
        drawableChecked.setBounds(0, 0, drawableChecked.getMinimumWidth(),
                drawableChecked.getMinimumHeight());
        drawableUnChecked.setBounds(0, 0, drawableUnChecked.getMinimumWidth(),
                drawableUnChecked.getMinimumHeight());
        if (genderFlag == 1) {
            btnMale.setCompoundDrawables(drawableChecked, null, null, null);
            btnFemale.setCompoundDrawables(drawableUnChecked, null, null, null);
        } else {
            btnFemale.setCompoundDrawables(drawableChecked, null, null, null);
            btnMale.setCompoundDrawables(drawableUnChecked, null, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
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

    public static void newIntent(Context context, int goodsId, String rid) {
        Intent intent = new Intent(context, HighMianShouActivity.class);
        intent.putExtra("rid", rid);
        intent.putExtra("goodsId", goodsId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
}
