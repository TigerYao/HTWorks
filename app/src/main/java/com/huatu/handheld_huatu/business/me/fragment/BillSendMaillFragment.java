package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.zhibo.ReportIntBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.utils.InputMethodUtils;
import com.huatu.utils.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class BillSendMaillFragment extends MySupportFragment {
    @BindView(R.id.send_mail_success_layout)
    View mSendSuccessView;

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;

    @BindView(R.id.edit_invoice_txt)
    EditText mInvoiceText;

    private String mEmailStr,mOrderIDStr;

    public static BillSendMaillFragment getIntance(String orderId){

        Bundle arg=new Bundle();
        arg.putString(ArgConstant.KEY_ID,orderId);

        BillSendMaillFragment tmpFragment=new BillSendMaillFragment();
        tmpFragment.setArguments(arg);
        return tmpFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mOrderIDStr= getArguments().getString(ArgConstant.KEY_ID,"");
        }
     }

    @Override
    protected int getContentView() {
        return R.layout.activity_bill_mail;
    }

    @Override
    protected void setListener() {
        mTopTitleBar.setTitle("邮箱地址");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if(menuItem.getId() == R.id.xi_title_bar_home){
                     pop();
                    //getActivity().finish();
                }
            }
        });
    }

    @OnClick(R.id.send_mail_btn)
    public void sendMailAddress(){

          if(TextUtils.isEmpty(mInvoiceText.getText())){
             ToastUtils.showShort("邮箱不为空~");
              return;
          }
          if(!RegexUtils.matcherEmail(mInvoiceText.getText().toString())){
              ToastUtils.showShort("邮箱格式不正确哦~");
              return;
          }
         sendEamil(mInvoiceText.getText().toString());
    }

    CustomLoadingDialog mLoadingDialog;
    private void sendEamil(String emailStr){
        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().sendInvoiceEmail(emailStr,mOrderIDStr),
                new NetObjResponse<ReportIntBean>() {
            @Override
            public void onSuccess(BaseResponseModel<ReportIntBean> model) {
                DialogUtils.dismissLoading(mLoadingDialog);
                startWithPop(new InvoiceEmailSuccFragment());
            }

            @Override
            public void onError(String message, int type) {
                DialogUtils.dismissLoading(mLoadingDialog);
                ToastUtils.showShort(message+"");
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodUtils.hideMethod(getContext(), mInvoiceText);
    }
   /* @OnClick(R.id.check_bill_detail)
    public void checkBill(){
        UIJumpHelper.jumpFragment(this, BillDetailFragment.class);
    }*/
}
