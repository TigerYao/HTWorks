package com.huatu.handheld_huatu.business.me.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.InvoiceDetailBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.ReportIntBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.library.internal.ViewCompat;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.ElasticOutInterpolator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillDetailFragment extends MySupportFragment {
    //开票状态
    @BindView(R.id.bill_state)
    TextView mBillState;
    //发票类型
    @BindView(R.id.bill_type)
    TextView mBillType;
    //订单号码
    @BindView(R.id.order_code)
    TextView mOrderCode;
    //下单时间
    @BindView(R.id.order_time)
    TextView mOrderTime;
    //抬头类型
    @BindView(R.id.bill_head_type)
    TextView mBillHeadType;
    //发票抬头
    @BindView(R.id.bill_head)
    TextView mBillHead;
    //发票内容
    @BindView(R.id.bill_content)
    TextView mBillContent;
    //开票金额
    @BindView(R.id.bill_price)
    TextView mBillPrice;
    //纳税人识别码
    @BindView(R.id.bill_code)
    TextView mBillCode;

    @BindView(R.id.bill_code_name)
    TextView mBillCodeName;


    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;

    @BindView(R.id.send_mail)
    Button mSendMailBtn;
    private int mCurrentStatus;


    private String mOrderIDStr;
    public static void lanuch(Context context,String orderId){
        Bundle tmpArg=new Bundle();
        tmpArg.putString(ArgConstant.KEY_ID,orderId);
        FragmentParameter tmpPar=new FragmentParameter(BillDetailFragment.class,tmpArg);
        UIJumpHelper.jumpSupportFragment(context,tmpPar,1);
    }

    public static BillDetailFragment getInstance(String orderId){
        Bundle tmpArg=new Bundle();
        tmpArg.putString(ArgConstant.KEY_ID,orderId);

        BillDetailFragment tmpFrag=new BillDetailFragment();
        tmpFrag.setArguments(tmpArg);
        return tmpFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mOrderIDStr= getArguments().getString(ArgConstant.KEY_ID,"");
        }
    }


    @Override
    public int getContentView() {
        return R.layout.activity_bill_detail;
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
    }

    View mAnimView;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAnimView=view.findViewById(R.id.content_container);
        mAnimView.setTranslationY(-DensityUtils.dp2px(getContext(),100));
        //android.support.v4.view.ViewCompat.offsetTopAndBottom(mAnimView, -DensityUtils.dp2px(getContext(),100));
       // tryBounceBack(view.findViewById(R.id.content_container));
    }

     @OnClick(R.id.send_mail)
    public void sendMail(){
        if(mCurrentStatus!=1) {
             return;
        }
         start(BillSendMaillFragment.getIntance(mOrderIDStr));
       // UIJumpHelper.jumpFragment(this, BillSendMaillFragment.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getInviceDetail();
    }

    CustomLoadingDialog mLoadingDialog;
    private void getInviceDetail(){
        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getInvoiceDetail(mOrderIDStr),
                new NetObjResponse<InvoiceDetailBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<InvoiceDetailBean> model) {
                        DialogUtils.dismissLoading(mLoadingDialog);
                        setUIInfo(model.data);
                    }

                    @Override
                    public void onError(String message, int type) {
                        DialogUtils.dismissLoading(mLoadingDialog);
                        ToastUtils.showShort(message+"");
                 /*       InvoiceDetailBean bean = new InvoiceDetailBean();
                        bean.status = 1;
                        bean.invoiceType = 1;
                        bean.orderNum = "HTKC";
                        bean.orderDate = "2019";
                        bean.titleType = 1;
                        bean.invoiceMoney = "1";
                        bean.invoiceTitle = "中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国";
                        bean.invoiceContent = "invoiceTitle";
                        setUIInfo(bean);*/
                    }
                });

    }

    private void setUIInfo(InvoiceDetailBean bean){
        try{



     /*  有缓存     if(mSendMailBtn.getBackground() instanceof GradientDrawable)  {
                GradientDrawable drawable =(GradientDrawable)mSendMailBtn.getBackground();
                drawable.setColor(bean.status==1?0xFFFF6D73:0xFFE0E0E0);
               // mSendMailBtn.invalidate();
            }*/

            if(bean.status==1){
                mSendMailBtn.setBackground(CustomShapeDrawable.buildRoundBackground(22,0xFFFF6D73));
            }

            mSendMailBtn.setTextColor(bean.status==1? Color.WHITE:0xFF9B9B9B);
            mCurrentStatus=bean.status;
            mBillState.setText("发票状态："+bean.getStatusDes());
            mBillType.setText("发票类型："+(bean.invoiceType==1?"电子发票":"纸质发票"));
            mOrderCode.setText(bean.orderNum);
            mOrderTime.setText(bean.orderDate);
            mBillHeadType.setText(bean.titleType==1? "个人":"单位");
            mBillHead.setText(bean.invoiceTitle);
            mBillContent.setText(bean.invoiceContent);
            mBillPrice.setText("￥"+bean.invoiceMoney);
            if(bean.titleType==2){
                mBillCode.setVisibility(View.VISIBLE);
                mBillCodeName.setVisibility(View.VISIBLE);
                mBillCode.setText(String.valueOf(bean.taxNum));
            }
            tryBounceBack(mAnimView);
//            if (mBillCode.getVisibility() == View.VISIBLE){
//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBillHead.getLayoutParams();
//                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.date);
//            }

        }catch (Exception e){}

    }


    ValueAnimator mBounceAnim;
    private void tryBounceBack(final View backView) {

      /*   TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.3f, Animation.RELATIVE_TO_SELF, 0f);
         mAnimation.setDuration(500);

         // mAnimation.setFillAfter(true);
         mAnimation.setInterpolator(new ElasticOutInterpolator());
         mAnimation.setStartOffset(2200);  -DensityUtils.dp2px(getContext(),100)

         backView.startAnimation(mAnimation);*/
        float startDistance= (float)-DensityUtils.dp2px(getContext(),100);
        mBounceAnim = ObjectAnimator.ofFloat(startDistance, 0);
        mBounceAnim.setInterpolator(new ElasticOutInterpolator());
        mBounceAnim.setDuration(500);
        mBounceAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float deltaY = (float) animation.getAnimatedValue() ;//- mHeaderController.getScroll()
                //moveBy(deltaY);
               // android.support.v4.view.ViewCompat.offsetTopAndBottom(backView,(int)deltaY);
                backView.setTranslationY(deltaY);
            }
        });
      /*  mBounceAnim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPullState = STATE_IDLE;
            }
        });*/

        mBounceAnim.start();

    }

    @Override
    public void onDestroy() {
         super.onDestroy();
         if(null!=mBounceAnim&&(mBounceAnim.isRunning())){
             mBounceAnim.cancel();
             mBounceAnim.removeAllUpdateListeners();
             mBounceAnim=null;
         }


    }
}
