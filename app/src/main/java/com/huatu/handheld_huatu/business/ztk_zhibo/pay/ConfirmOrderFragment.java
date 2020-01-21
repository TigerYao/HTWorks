package com.huatu.handheld_huatu.business.ztk_zhibo.pay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.ExamProtocolFragment;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolExamUserInfo;
import com.huatu.handheld_huatu.business.me.fragment.LevelPrivilegeExplainFragment;
import com.huatu.handheld_huatu.business.play.bean.ActDetailInfo;
import com.huatu.handheld_huatu.business.play.bean.CourseActDetailBean;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.highmianshou.HuaTuXieYiActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.address.EditAddressFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.address.SelectAddressFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.Exposition;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.OrdersPrevInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PoundInfoBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomBottomDialog;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.handheld_huatu.view.SimpleLabelsLayout;
import com.huatu.handheld_huatu.view.TagSpan;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2017/9/22.
 */

public class ConfirmOrderFragment extends BaseFragment {
    @BindView(R.id.confirm_order_action_bar)
    TopActionBar topActionBar;
    @BindView(R.id.confirm_order_no_address_layout)
    View layoutNoAddress;
    @BindView(R.id.confirm_order_contact_des)
    TextView tvContactDes;
    @BindView(R.id.confirm_order_contact_layout)
    View layoutContact;
    @BindView(R.id.confirm_order_contact_name)
    TextView tvUserName;
    @BindView(R.id.confirm_order_contact_phone)
    TextView tvUserPhone;
    @BindView(R.id.confirm_order_contact_address)
    TextView tvUserAddress;

    @BindView(R.id.confirm_order_protocol_layout)
    View layoutProtocol;
    @BindView(R.id.confirm_order_protocol_enter_layout)
    View layoutEnterProtocol;
    @BindView(R.id.confirm_order_protocol_detail_layout)
    View layoutProtocolDetail;
    @BindView(R.id.confirm_order_modify_protocol_btn)
    TextView btnModifyProtocol;
    @BindView(R.id.confirm_order_preview_protocol_btn)
    TextView btnPreviewProtocol;
    @BindView(R.id.confirm_order_user_info_des_tv)
    TextView tvProtocolUserInfoDes;
    @BindView(R.id.confirm_order_user_info_tv)
    TextView tvProtocolUserInfo;
//    @BindView(R.id.confirm_order_user_exam_des_tv)
//    TextView tvProtocolExamDes;
//    @BindView(R.id.confirm_order_user_exam_tv)
//    TextView tvProtocolExam;

    @BindView(R.id.confirm_order_detail_img)
    ImageView ivDetailImg;
    @BindView(R.id.confirm_order_detail_title)
    TextView tvDetailTitle;
    @BindView(R.id.confirm_order_detail_price)
    TextView tvDetailPrice;
    //    @BindView(R.id.confirm_order_old_price)
//    TextView tvOldPrice;
    @BindView(R.id.confirm_order_extra_list)
    NoScrollListView mExtrListView;
    @BindView(R.id.confirm_order_detail_time)
    TextView tvDetailTime;
    @BindView(R.id.tv_order_title)
    View mOrderTitle;

    @BindView(R.id.ll_mianshou)
    LinearLayout ll_mianshou;
    @BindView(R.id.tv_mianshou)
    TextView tv_mianshou;
    @BindView(R.id.iv_mianshou)
    ImageView iv_mianshou;
    @BindView(R.id.rl_iv_mianshou)
    RelativeLayout rl_iv_mianshou;

    @BindView(R.id.tv_discount)
    TextView tvPriceOff;
    @BindView(R.id.confirm_order_price_off)
    TextView tv_discount;
    @BindView(R.id.confirm_order_price_off_title)
    TextView tv_discount_title;
    @BindView(R.id.confirm_order_integration_layout)
    View layoutIntegration;
    @BindView(R.id.confirm_order_integration)
    TextView tvOrderIntegration;
    @BindView(R.id.confirm_order_pay_number)
    TextView tvPayNumber;
    @BindView(R.id.confirm_order_pay_number_two)
    TextView tvPayNumberTwo;
    @BindView(R.id.confirm_order_confirm_btn)
    TextView btnConfirm;

    @BindView(R.id.confirm_order_price)
    TextView mProductPrice;
    @BindView(R.id.confirm_order_pay_offer)
    TextView mOrderOffTv;
    @BindView(R.id.tv_default_flag)
    TextView mFlagView;

    @BindView(R.id.teacher_layout)
    LinearLayout mTeacherLayout;
    @BindView(R.id.activte_labels)
    View loabelView;
    @BindView(R.id.labels_layout)
    SimpleLabelsLayout mLabelsLayout;
    @BindView(R.id.confirm_order_fee)
    TextView mOrderFee;
    @BindView(R.id.leve_des)
    View mLeveDes;

    @BindView(R.id.confirm_order_fee_title)
    View feeTitleView;

    private int goodsId;
    private String courseId;
    private OrdersPrevInfo ordersPrevInfo;
    private PoundInfoBean selPound = null;
    private AddressInfoBean selAddressInfo;
    private final int POST_NAME = 0;
    private final int POST_PHONE = 1;
    private final int POST_ADDRESS_DETAIL = 2;
    private String fromUser;
    private String tjCode = "0";
    private PayInfo payInfo;
    private CustomConfirmDialog confirmDialog;
    private DecimalFormat df = new DecimalFormat("0.00");
    private boolean isSelect = false;//是否选中华图面试协议
    public String pageSource = "";
    private ConfirmPaymentFragment2 paymentFragment2;
    private CourseActDetailBean mActBean;
    private CustomBottomDialog mActIntroDialog;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_confirm_order_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        courseId = args.getString("course_id");
        fromUser = args.getString("from_user", "0");
        pageSource = args.getString("from");
        goodsId = args.getInt("goodsId");
        mActBean = (CourseActDetailBean) args.getSerializable("actBean");
        initTitleBar();
        mLeveDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFrgContainerActivity.newInstance(mActivity,
                        LevelPrivilegeExplainFragment.class.getName(),
                        LevelPrivilegeExplainFragment.getArgs());
            }
        });
    }

    private void initTitleBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
        loabelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRedbagTip();
            }
        });
    }

    @Override
    protected void onLoadData() {
        mActivity.showProgress();
        ServiceProvider.getOrdersPrevInfo(getCompositeSubscription(), courseId, goodsId, pageSource, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                ordersPrevInfo = (OrdersPrevInfo) model.data;
                refreshUI();
                mActivity.hideProgress();
            }

            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if (e != null && e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
//                    ToastUtils.showEssayToast(apiException.getErrorMsg());
                    String tipStr= apiException.getMessage();
                    CustomConfirmDialog customConfirmDialog = DialogUtils.onShowOnlyConfirmRedDialog(getActivity(), R.layout.video_finished_tip_dialog,tipStr, 0);
                    customConfirmDialog.setCanceledOnTouchOutside(false);
                    customConfirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mActivity.finish();
                        }
                    });
                }
            }
        });
    }

    private void refreshUI() {
        if (ordersPrevInfo == null) {
            return;
        }
        if (ordersPrevInfo.isLogistics == 1 && ordersPrevInfo.address != null) {
            selAddressInfo = ordersPrevInfo.address;
        }
        int color = ContextCompat.getColor(getContext(), ordersPrevInfo.hasProtocol == 0 && ordersPrevInfo.isLogistics != 1 ? R.color.red002 : R.color.gray002);
        btnConfirm.setBackgroundColor(color);
        refreshAddressState();
        tvDetailTitle.setText(ordersPrevInfo.title);
        String priceValue = "￥" + ordersPrevInfo.actualPrice;
        tvPayNumber.setText(priceValue);
        tvPayNumberTwo.setText(priceValue);
        tvDetailPrice.setText("￥" + ordersPrevInfo.price);
        mProductPrice.setText("￥" + ordersPrevInfo.price);
        if (!Utils.isEmptyOrNull(ordersPrevInfo.brief))
            tvDetailTime.setText(ordersPrevInfo.brief);
        else
            tvDetailTime.setVisibility(View.GONE);
        if (!Utils.isEmptyOrNull(ordersPrevInfo.memberDiscount) && Float.parseFloat(ordersPrevInfo.memberDiscount) > 0f) {
            tvPriceOff.setVisibility(View.VISIBLE);
            mLeveDes.setVisibility(View.VISIBLE);
            tvPriceOff.setText("-￥" + ordersPrevInfo.memberDiscount);
        } else {
            tvPriceOff.setVisibility(View.GONE);
            mLeveDes.setVisibility(View.GONE);
        }

        if (ordersPrevInfo.calcDisCount > 0) {
            String mDiscount = df.format(ordersPrevInfo.calcDisCount);
            tv_discount.setText("-" + "￥" + mDiscount);
            tv_discount.setVisibility(View.VISIBLE);
            tv_discount_title.setVisibility(View.VISIBLE);
        } else {
            tv_discount.setText("0");
            tv_discount.setVisibility(View.GONE);
            tv_discount_title.setVisibility(View.GONE);
        }

        if (!Utils.isEmptyOrNull(ordersPrevInfo.moneyDisCount) && Float.parseFloat(ordersPrevInfo.moneyDisCount) > 0f) {
            mOrderOffTv.setText("已优惠" + "￥" + ordersPrevInfo.moneyDisCount);
            mOrderOffTv.setVisibility(View.VISIBLE);
        } else
            mOrderOffTv.setVisibility(View.GONE);

        if (ordersPrevInfo.point > 0) {
            tvOrderIntegration.setText(ordersPrevInfo.point + "");
            layoutIntegration.setVisibility(View.VISIBLE);
        } else {
            layoutIntegration.setVisibility(View.GONE);
        }

        if (ordersPrevInfo.isLogistics == 1) {
            mOrderFee.setVisibility(View.VISIBLE);
            feeTitleView.setVisibility(View.VISIBLE);
            if (ordersPrevInfo.logisticsCost > 0f) {
                mOrderFee.setText("-￥" + ordersPrevInfo.logisticsCost);
            } else
                mOrderFee.setText("￥" + ordersPrevInfo.logisticsCost);
        } else {
            mOrderFee.setVisibility(View.GONE);
            feeTitleView.setVisibility(View.GONE);
        }
        if (ordersPrevInfo.exposition != null && !ordersPrevInfo.exposition.isEmpty()) {
            mOrderTitle.setVisibility(View.VISIBLE);
            mExtrListView.setVisibility(View.VISIBLE);
            mExtrListView.setAdapter(new CommonAdapter<Exposition>(ordersPrevInfo.exposition, R.layout.essay_order_item_layout) {
                @Override
                public void convert(ViewHolder holder, Exposition item, int position) {
                    holder.setText(R.id.tv_order_content, item.getTitleEx());
                    if (!Utils.isEmptyOrNull(item.expireDate))
                        holder.setText(R.id.tv_order_expiry_date, item.expireDate);
                }
            });
        }
//        calculatePrice();
        if (Method.isEqualString("1", ordersPrevInfo.isMianshou)) {
            ll_mianshou.setVisibility(View.VISIBLE);
            //高端面试课事件
            mianshouClick();
        } else {
            ll_mianshou.setVisibility(View.GONE);
        }
        setProtocolState();
        replaceFragment();
        mTeacherLayout.removeAllViews();
        for (OrdersPrevInfo.TeacherInfo info : ordersPrevInfo.teacherInfo) {
            LinearLayout ll_teacher = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_teacher2, null, false);
            ImageView imageView = ll_teacher.findViewById(R.id.civ_teacher_photo);
            TextView textView = ll_teacher.findViewById(R.id.tv_teacher_name);
            // Glide.with(UniApplicationContext.getContext()).load(mResult.teacher.get(i).roundPhoto).into(imageView);

            ImageLoad.displaynoCacheUserAvater(getContext(), info.roundPhoto, imageView, R.mipmap.user_default_avater);
            textView.setText(info.teacherName);
            mTeacherLayout.addView(ll_teacher);
        }
        if (mActBean == null || mActBean.data == null || mActBean.data.size() == 0)
            loabelView.setVisibility(View.GONE);
        else
            showActiveLabelsView();

    }

    private void setProtocolState() {
        if (ordersPrevInfo.hasProtocol > 0) {
            if (ordersPrevInfo.protocolInfo == null) {
                ordersPrevInfo.protocolInfo = new ProtocolExamUserInfo();
            }
            if (!TextUtils.isEmpty(ordersPrevInfo.treatyId)) {
                ordersPrevInfo.protocolInfo.protocolId = ordersPrevInfo.treatyId;
            }
            if (!TextUtils.isEmpty(ordersPrevInfo.protocolName)) {
                ordersPrevInfo.protocolInfo.protocolName = ordersPrevInfo.protocolName;
            }
        }

        if (ordersPrevInfo.hasProtocol == 1) {
            layoutProtocol.setVisibility(View.VISIBLE);
            layoutEnterProtocol.setVisibility(View.VISIBLE);
            layoutProtocolDetail.setVisibility(View.GONE);
        } else if (ordersPrevInfo.hasProtocol == 2) {
            layoutProtocol.setVisibility(View.VISIBLE);
            layoutEnterProtocol.setVisibility(View.GONE);
            layoutProtocolDetail.setVisibility(View.VISIBLE);
            tvProtocolUserInfoDes.setText("姓名\n\n性别\n\n手机号\n\n身份证号");
            String sexInfo;
            if (ordersPrevInfo.protocolInfo.sex == 1) {
                sexInfo = "男";
            } else {
                sexInfo = "女";
            }
            tvProtocolUserInfo.setText(ordersPrevInfo.protocolInfo.studentName + "\n\n"
                    + sexInfo + "\n\n"
                    + ordersPrevInfo.protocolInfo.telNo + "\n\n"
                    + ordersPrevInfo.protocolInfo.idCard);
        } else {
            layoutProtocol.setVisibility(View.GONE);
            return;
        }
        int color = ContextCompat.getColor(getContext(), ordersPrevInfo.hasProtocol == 1 || !checkAddressInfo() ? R.color.gray002 : R.color.red002);
        btnConfirm.setBackgroundColor(color);
    }

    @OnClick(R.id.confirm_order_protocol_enter_layout)
    public void onClickEnterProtocol() {
        ExamProtocolFragment fragment = new ExamProtocolFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("protocol_detail", ordersPrevInfo.protocolInfo);
        fragment.setArguments(arg);
        startFragmentForResult(fragment, 10113);
    }

    @OnClick(R.id.confirm_order_modify_protocol_btn)
    public void onClickModifyProtocol() {
        ExamProtocolFragment fragment = new ExamProtocolFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("protocol_detail", ordersPrevInfo.protocolInfo);
        fragment.setArguments(arg);
        startFragmentForResult(fragment, 10113);
    }

    @OnClick(R.id.confirm_order_preview_protocol_btn)
    public void onClickProtocolDetail() {
        String baseUrl = "https://apitk.huatu.com";
        if (RetrofitManager.getInstance().getBaseUrl().contains("ns.huatu.com")) {
            baseUrl = "https://apitk.huatu.com/";
        } else {
            baseUrl = "http://tk.htexam.com/";
        }
        String url = Utils.isEmptyOrNull(ordersPrevInfo.treatyUrl) ? baseUrl + "v3/order/protocolView.php?TreatyId="
                + ordersPrevInfo.treatyId + "&username=" + SpUtils.getUname() : ordersPrevInfo.treatyUrl;//UserInfoUtil.userName;
        LogUtils.i("url: " + url);
        BaseWebViewFragment fragment = BaseWebViewFragment.newInstance(url,
                ordersPrevInfo.protocolName, null, true, false);
        startFragmentForResult(fragment);
    }

    private void mianshouClick() {
        rl_iv_mianshou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelect) {
                    isSelect = false;
                    iv_mianshou.setImageResource(R.drawable.orderkong);
                } else {
                    isSelect = true;
                    iv_mianshou.setImageResource(R.drawable.orderzhong);
                }
            }
        });
        tv_mianshou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(ordersPrevInfo.treatyUrl)) {
                    return;
                }
                HuaTuXieYiActivity.newIntent(mActivity, ordersPrevInfo.treatyUrl);
            }
        });
    }

    private void refreshAddressState() {
        if (ordersPrevInfo != null && ordersPrevInfo.isLogistics != 1) {
            tvContactDes.setVisibility(View.GONE);
            layoutContact.setVisibility(View.GONE);
            layoutNoAddress.setVisibility(View.GONE);
            return;
        }
        if (selAddressInfo == null) {
            layoutContact.setVisibility(View.GONE);
            layoutNoAddress.setVisibility(View.VISIBLE);
        } else {
            layoutContact.setVisibility(View.VISIBLE);
            layoutNoAddress.setVisibility(View.GONE);
            tvUserName.setText(selAddressInfo.consignee);
            tvUserPhone.setText(selAddressInfo.phone);
            String address = selAddressInfo.province
                    + selAddressInfo.city + selAddressInfo.area + selAddressInfo.address;

            if (selAddressInfo.isDefault == 1) {
                int color = Color.parseColor("#ffff3f47");
                String ex_address = "默认";
                address = ex_address + address;
                int startIndex = address.indexOf(ex_address);
                int endIndex = startIndex + ex_address.length();
                SpannableStringBuilder spannableString = new SpannableStringBuilder(address);
                TagSpan tagSpan = new TagSpan(Paint.Style.STROKE, color, color, tvUserAddress.getTextSize(), 5, 10, 10, 10, true);
                spannableString.setSpan(tagSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tvUserAddress.setText(spannableString);
            } else {
                String ex_address = "地址：";
                address = ex_address + address;
                tvUserAddress.setText(address);
            }

        }
        int color = ContextCompat.getColor(getContext(), selAddressInfo == null || checkNoWriteProtocol() ? R.color.gray002 : R.color.red002);
        btnConfirm.setBackgroundColor(color);
    }

    @OnClick(R.id.confirm_order_confirm_btn)
    public void onClickConfirm() {
        if (ordersPrevInfo == null) {
            return;
        }
        if (paymentFragment2.isCanNotPayTubi()) {
            ToastUtils.showShort("图币不足");
            return;
        }
        if (checkNoWriteProtocol()) {
            ToastUtils.showShort("请签订协议");
            return;
        }
        if (!checkAddressInfo()) {
            return;
        }
        String addressId = "0";
        if (selAddressInfo != null) {
            addressId = String.valueOf(selAddressInfo.id);
        }
//        String poundId = "0";
//        if (selPound != null) {
//            poundId = selPound.ID;
//        }
        mActivity.showProgress();
        NetResponse response = new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                if (e != null && e instanceof ApiException) {
                    ApiException exception = (ApiException) e;
                    String errorMsg = exception.getErrorMsg();
                    ToastUtils.showMessage(TextUtils.isEmpty(errorMsg) ? "发生错误，稍后重试" : errorMsg);
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                payInfo = (PayInfo) model.data;
                startPayment();
            }
        };
        if (payInfo == null || payInfo.orderId == 0)
            ServiceProvider.createOrder(getCompositeSubscription(), ordersPrevInfo.actualPrice, goodsId, courseId, ordersPrevInfo.p, addressId,
                    fromUser, paymentFragment2.getPayType() + "", ordersPrevInfo.protocolInfo == null ? ordersPrevInfo.treatyId : ordersPrevInfo.protocolInfo.rid, response);
        else
            ServiceProvider.continuePayOrder(getCompositeSubscription(), payInfo.orderId + "", paymentFragment2.getPayType(), response);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
    }


    private void startPayment() {
        payInfo.title = ordersPrevInfo.title;
        paymentFragment2.setPayInfo(payInfo);
        paymentFragment2.onClickConfirm();
    }

    private void replaceFragment() {
        paymentFragment2 = new ConfirmPaymentFragment2();
        Bundle arg = new Bundle();
//        actualPrice = Method.parseFloat(payInfo.MoneySum);
        arg.putString("course_id", courseId);
        arg.putString("pay_number", ordersPrevInfo.actualPrice);
        arg.putLong("xxbRemainder", ordersPrevInfo.goldNum);
        arg.putInt("paytype", ordersPrevInfo.payGold);
//        arg.putSerializable("pay_info", new PayInfo());
        paymentFragment2.setArguments(arg);
        getChildFragmentManager().beginTransaction().replace(R.id.pay_container, paymentFragment2).commitNow();
    }

    private boolean checkAddressInfo() {
        if (ordersPrevInfo.isLogistics == 1) {
            if (selAddressInfo == null) {
                ToastUtils.showShort("请添加收货地址");
                return false;
            }
            return checkPost(selAddressInfo.consignee, POST_NAME)
                    && checkPost(selAddressInfo.phone, POST_PHONE)
                    && checkPost(selAddressInfo.address, POST_ADDRESS_DETAIL);
        }
        return true;
    }

    private boolean checkNoWriteProtocol() {
        boolean isHas = ordersPrevInfo.hasProtocol > 0
                && (ordersPrevInfo.protocolInfo == null
                || TextUtils.isEmpty(ordersPrevInfo.protocolInfo.rid));
        isHas = isHas || (Method.isEqualString("1", ordersPrevInfo.isMianshou) && !isSelect);
        return isHas;
    }

    private boolean checkPost(String string, int type) {
        boolean isTrue = false;
        switch (type) {
            case POST_NAME:
                if (TextUtils.isEmpty(string)) {
                    ToastUtils.showShort("请填写收货人姓名");
                } else
                    isTrue = true;
                break;
            case POST_PHONE:
                if (TextUtils.isEmpty(string)) {
                    ToastUtils.showShort("请填写手机号码");
                } else
                    isTrue = true;
                break;
            case POST_ADDRESS_DETAIL:
                if (TextUtils.isEmpty(string)) {
                    ToastUtils.showShort("请填写详细地址");
                } else
                    isTrue = true;
                break;
            default:
                break;
        }
        return isTrue;
    }

    @OnClick(R.id.confirm_order_no_address_layout)
    public void onClickNoAddress() {
        EditAddressFragment fragment = new EditAddressFragment();
        startFragmentForResult(fragment, 10001);
    }

    @OnClick(R.id.confirm_order_contact_layout)
    public void onClickSelectAddress() {
        SelectAddressFragment fragment = new SelectAddressFragment();
        if (selAddressInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("selected_address_id", selAddressInfo.id);
            fragment.setArguments(bundle);
        }
        startFragmentForResult(fragment, 10002);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 10001 || requestCode == 10002) {
                AddressInfoBean bean = (AddressInfoBean) data.getSerializableExtra("address_info");
                if (bean != null) {
                    selAddressInfo = bean;
                    if (requestCode == 10001)
                        selAddressInfo.isDefault = 1;
                    refreshAddressState();
                }

            } else if (requestCode == 10004) {
                setResultForTargetFrg(Activity.RESULT_OK);
            } else if (requestCode == 10113) {
                if (ordersPrevInfo != null) {
                    ordersPrevInfo.hasProtocol = 2;
                    ordersPrevInfo.protocolInfo = (ProtocolExamUserInfo)
                            data.getSerializableExtra("protocol_detail");
                    setProtocolState();
                }
            } else
                paymentFragment2.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
            int size = data.getIntExtra("address_list_size", 0);
            if ((requestCode == 10001 || requestCode == 10002)) {
                if (size == 1 && data.hasExtra("address_info")) {
                    AddressInfoBean info = (AddressInfoBean) data.getSerializableExtra("address_info");
                    if (info != null) {
                        selAddressInfo = info;
                        refreshAddressState();
                    }
                } else if (size == 0) {
                    selAddressInfo = null;
                    refreshAddressState();
                }
            }
        }
    }

    public static void newInstance(Activity activity, CourseActDetailBean bean, int goodsId, String courseId, String pageSource) {
        newInstance(activity, bean, goodsId, courseId, null, pageSource);
    }

    public static void newInstance(Activity activity, CourseActDetailBean bean, int goodsId, String courseId, String fromUser, String pageSource) {
        Bundle arg = new Bundle();
        arg.putString("course_id", courseId);
        arg.putString("from_user", fromUser);
        arg.putString("from", pageSource);
        arg.putSerializable("actBean", bean);
        arg.putInt("goodsId", goodsId);
        BaseFrgContainerActivity.newInstance(activity,
                ConfirmOrderFragment.class.getName(), arg);
    }

    private void showActiveLabelsView() {
        loabelView.setVisibility(View.VISIBLE);
        mLabelsLayout.removeAllViews();
        for (ActDetailInfo info : mActBean.data) {
            View view = mLayoutInflater.inflate(R.layout.labels_intro_layout, null);
            TextView labelName = (TextView) view.findViewById(R.id.label_name);
            labelName.setText(info.title);
            mLabelsLayout.addView(view);
        }
    }

    private void showRedbagTip() {
        if (mActIntroDialog == null && mActBean != null) {
            mActIntroDialog = new CustomBottomDialog(getContext(), R.style.CustomBottomDialog) {
                @Override
                public int getLayoutId() {
                    return R.layout.dialog_actions_tip;
                }

                @Override
                public void onViewCreate(View contentView) {
                    super.onViewCreate(contentView);
                    contentView.findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mActIntroDialog.dismiss();
                        }
                    });
                }
            };
            mActIntroDialog.getWindow().setGravity(Gravity.BOTTOM);
            for (ActDetailInfo info : mActBean.data)
                mActIntroDialog.addItem(info.title, info.introduction);
        }
        if (mActIntroDialog != null)
            mActIntroDialog.show();
    }

}
