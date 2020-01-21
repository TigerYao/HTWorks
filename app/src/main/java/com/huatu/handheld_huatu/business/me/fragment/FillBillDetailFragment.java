package com.huatu.handheld_huatu.business.me.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.business.me.bean.MyV5OrderContent;
import com.huatu.handheld_huatu.event.InvoiceStatusEvent;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.InvoicResultBean;
import com.huatu.handheld_huatu.mvpmodel.InvoiceDetailBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.ReportIntBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.InputMethodUtils;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 填写开票单
 */
public class FillBillDetailFragment extends MySupportFragment {
    //课程名称
    @BindView(R.id.bill_course_name)
    TextView mCourseTitle;
    @BindView(R.id.bill_price)
    TextView mBillPrice;
    @BindView(R.id.bill_type)
    TextView mBillType;
    @BindView(R.id.bill_head_type)
    RadioGroup mBillHeadTypeRG;

    @BindView(R.id.bill_head)
    EditText mBillHead;

    @BindView(R.id.bill_head_tv)
    TextView mBillHeadTv;

    @BindView(R.id.bill_code)
    EditText mBillCode;
    @BindView(R.id.bill_code_name)
    TextView mBillCodeName;

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;

    @BindView(R.id.send_mail)
    Button mSendMailBtn;

    private int mTitleType=0;//抬头类型1个人2单位

    private String mOrderIDStr,mOrderNumStr,mCourseName,mPrice,mOrderDate;


    public static void lanuch(Context context, String orderID, String orderNum, String orderDate,String invoiceMoney,ArrayList<MyV5OrderContent.ClassInfo> classInfos){
        Bundle tmpArg=new Bundle();
        tmpArg.putString(ArgConstant.KEY_ID,orderID);
        tmpArg.putString(ArgConstant.TYPE_ID,orderNum);
        tmpArg.putString(ArgConstant.KEY_TITLE,orderDate);
        if(!ArrayUtils.isEmpty(classInfos)){
            tmpArg.putString(ArgConstant.TITLE,classInfos.get(0).title);

        }
        tmpArg.putString(ArgConstant.TYPE,invoiceMoney);
        FragmentParameter tmpPar=new FragmentParameter(FillBillDetailFragment.class,tmpArg);
        UIJumpHelper.jumpSupportFragment(context,tmpPar,1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mOrderIDStr= getArguments().getString(ArgConstant.KEY_ID,"");
            mOrderNumStr=getArguments().getString(ArgConstant.TYPE_ID,"");
            mOrderDate=getArguments().getString(ArgConstant.KEY_TITLE,"");

            mCourseName=getArguments().getString(ArgConstant.TITLE,"");
            mPrice=getArguments().getString(ArgConstant.TYPE,"");
        }
    }

    @Override
    public int getContentView() {
        return R.layout.layout_fill_bill_detail;
    }


    @Override
    protected void setListener() {
        mTopTitleBar.setTitle("开票详情");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if(menuItem.getId() == R.id.xi_title_bar_home){
                      getActivity().finish();
                }
            }
        });


        mSendMailBtn.setBackground(CustomShapeDrawable.buildRoundBackground(22,0xFFFF6D73));
        initView();
    }

/*    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("开票详情");
        mEditKeyListener = mBillHead.getKeyListener();
        initView();
    }*/


    @OnClick({R.id.bill_content,R.id.send_mail,R.id.bill_price,R.id.bill_content_description})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bill_price:
                BaseWebViewFragment.lanuch(getActivity(),"http://m.v.huatu.com/customer/explain.html", "开票说明");
                break;
            case R.id.bill_content_description:
            case R.id.bill_content:
                DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   }
                }, "","发票内容本公司只支持开“咨询费”", "", "知道了");
                break;
            case R.id.send_mail:
               checkFillInfo();
               break;
         }
    }


    String mInvoicehead="个人";
    String mtaxNum="";
    private void checkFillInfo(){

        if(mTitleType==0){
            ToastUtils.showShort("请选择发票抬头类型~");
            return;
        }
         if(mTitleType==2){

            if(TextUtils.isEmpty(mBillHead.getText())){
                ToastUtils.showShort("请填写你的发票抬头哦~");
                return;
            }
            if(TextUtils.isEmpty(mBillCode.getText())){
                ToastUtils.showShort("请填写纳税人识别号哦~");
                return;
            }
            mInvoicehead=mBillHead.getText().toString();
            mtaxNum=mBillCode.getText().toString();
       }else {
            mInvoicehead="个人";
            mtaxNum="";
        }

        InvoiceConfirmDialogFragment ratefragment=InvoiceConfirmDialogFragment.getInstance(mInvoicehead,mTitleType==1?"个人":"单位",mtaxNum);
        ratefragment.setOnSubmitListener(new InvoiceConfirmDialogFragment.OnSubmitListener() {
            @Override
            public void onSubmit() {
                buildRequest();
            }
        });
        ratefragment.show(this.getActivity().getSupportFragmentManager(), "invoiceConfirm");
    }


    CustomLoadingDialog mLoadingDialog;
    CustomConfirmDialog mNextActionDialog;
    private void buildRequest(){

        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().sendInvoiceRequest(
                "咨询费",mPrice,mInvoicehead,1,mOrderIDStr,mOrderNumStr,mtaxNum,mTitleType,mOrderDate),
                new NetObjResponse<InvoicResultBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<InvoicResultBean> model) {
                        DialogUtils.dismissLoading(mLoadingDialog);
                       // setUIInfo(model.data);

                        if("0000".equals(model.data.status)){

                            EventBus.getDefault().post(new InvoiceStatusEvent(mOrderIDStr,mOrderNumStr));
                            final Runnable nextActionRunnable=new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.dismissLoading(mNextActionDialog);
                                    startWithPop(BillDetailFragment.getInstance(mOrderIDStr));
                                }
                            };
                            mNextActionDialog= DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != mTopTitleBar){
                                        mTopTitleBar.removeCallbacks(nextActionRunnable);
                                    }
                                   startWithPop(BillDetailFragment.getInstance(mOrderIDStr));
                                }
                            }, "", "您的开票申请已成功提交", "", "查看开票详情");
                            mNextActionDialog.setCanceledOnTouchOutside(false);
                            if (null != mTopTitleBar) {
                                mTopTitleBar.postDelayed(nextActionRunnable, 2000);
                            }
                         }else {


                            DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, "", !TextUtils.isEmpty(model.data.message)?model.data.message:"您的开票申请提交失败，\n请核验后再提交", "", "去核验");

                        }
                     }

                    @Override
                    public void onError(String message, int type) {
                        DialogUtils.dismissLoading(mLoadingDialog);
                        ToastUtils.showShort(message+"");
                    }
                });

    }

    private void initView(){

        mCourseTitle.setText(mCourseName);
        mBillPrice.setText("开票金额 ￥"+mPrice);

        mBillHeadTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(id == R.id.bill_personal){
                    InputMethodUtils.hideMethod(getContext(), mBillHead);
                    mBillHeadTv.setVisibility(View.VISIBLE);
                    mBillHead.setVisibility(View.GONE);
                    mBillCode.setVisibility(View.GONE);
                    mBillCodeName.setVisibility(View.GONE);
                    mTitleType=1;
                }else{
                    mBillHead.setVisibility(View.VISIBLE);
                    mBillCode.setVisibility(View.VISIBLE);
                    mBillCodeName.setVisibility(View.VISIBLE);
                    mBillHeadTv.setVisibility(View.GONE);
                    mTitleType=2;
                }
            }
        });
        ((RadioButton)mBillHeadTypeRG.getChildAt(0)).setChecked(true);
    }


    @Override
    public void onPause() {
        super.onPause();
         InputMethodUtils.hideMethod(getContext(), mBillHead);
    }

}
