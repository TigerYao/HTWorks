package com.huatu.handheld_huatu.business.essay.checkfragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.cusview.TipTextView;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDataBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 */
public class CheckCountFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CheckCountFragment";

    @BindView(R.id.fragment_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.tip_one)
    TipTextView tipOne;
    @BindView(R.id.check_count_listv)
    ListView check_count_listv;
    @BindView(R.id.person_check_count_listv)
    ListView person_check_count_listv;
    @BindView(R.id.go_buy_check)
    TextView goBuyCheck;
    @BindView(R.id.tv_tips)
    TextView tv_tips;
    @BindView(R.id.tv_tip4)
    TextView tv_tip4;

    private String titleView;
    private boolean isSingle;
    private boolean isStartToCheckDetail;
    private Bundle extraArgs;
    private int selPos;
    private AppPermissions rxPermissions;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
        if (event.type == EssayExamMessageEvent.EssayExam_net_getCheckCountList) {
            getCheckCountList();
        }
        return true;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.check_count_flayout;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (args != null) {
            requestType = args.getInt("request_type");
            extraArgs = args.getBundle("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            if(extraArgs!=null) {
                titleView = extraArgs.getString("titleView");
                isSingle = extraArgs.getBoolean("isSingle");
                isStartToCheckDetail = extraArgs.getBoolean("isStartToCheckDetail");
            }
        }
        initTitleBar();
        tipOne.setVisibility(View.GONE);
    }

    private void initTips() {
        rxPermissions = new AppPermissions(mActivity);
        tv_tips.setText("说明：\n\n1.标准答案批改只能用于标准答案批改，套题批改只能用于套题批改，文章写作批改只能用于文章写作批改；\n\n2.请在网络条件较好的情况下进行购买，并耐心等待购买结果，不建议进行其他无关操作；\n");
        SpannableStringBuilder builder=new SpannableStringBuilder("");
        builder.append("3.如遇到支付失败情况，请拨打华图在线客服电话");
        builder.append(SpUtils.getAboutPhone());
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.blue250)),23,builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new UnderlineSpan(),23,builder.length(), Paint.UNDERLINE_TEXT_FLAG );
        builder.append("进行咨询，或者联系在线客服。\n");
        tv_tip4.setText(builder);
        tv_tip4.setOnClickListener(this);
    }

    private void initBuyButton() {
        if (SpUtils.getEssayCorrectFree() == 1){
//        if(EssayCheckDataCache.getInstance().isCheckFree==1){
            goBuyCheck.setVisibility(View.GONE);
        }else {
            goBuyCheck.setVisibility(View.VISIBLE);
        }
    }

//    private void initTopTip() {
//        int max= EssayCheckDataCache.getInstance().maxCorrectTimes;
//        if (max==9999||max <= 0) {
//            tipOne.setVisibility(View.GONE);
//        }else {
//            tipOne.setVisibility(View.VISIBLE);
//            tipOne.setTag("CheckCountFragment_tip1");
//            tipOne.setText("同一单题或套题仅可批改"+max+"次");
//        }
//
//    }

    private void initTitleBar() {
        topActionBar.setTitle("申论批改次数");
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
//        topActionBar.showButtonImage(R.mipmap.download_paper_icon, TopActionBar.RIGHT_AREA);
//        topActionBar.showButtonImage(-1, TopActionBar.RIGHT_AREA);
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                Toast.makeText(mActivity, "download", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onLoadData() {
        super.onLoadData();
        getCheckCountList();
//        getMaxCorrectTimes();
    }

//    private void getMaxCorrectTimes() {
//        if (mEssayCheckImpl!=null){
//            mEssayCheckImpl.getCheckCountNum(0,0);
//            initTopTip();
//        }
//    }

    public void getCheckCountList(){
        if (!NetUtil.isConnected()){
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
        }
        mActivity.showProgress();
        ServiceProvider.getCheckCountList(compositeSubscription,new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                ToastUtils.showEssayToast("网络链接错误，请稍后重试");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
//                listChecks.clear();
                if(model!=null&&model.data!=null ) {

                    CheckCountBean var=(CheckCountBean)model.data;
//                    CheckCountBean var1=new CheckCountBean();
//                    var1.type=0;
//                    var1.usefulNum=var.singleNum;
//                    CheckCountBean var2=new CheckCountBean();
//                    var2.type=1;
//                    var2.usefulNum=var.multiNum;
//                    CheckCountBean var3=new CheckCountBean();
//                    var3.type=2;
//                    var3.usefulNum=var.argumentationNum;
//                    listChecks.add(var1);
//                    listChecks.add(var2);
//                    listChecks.add(var3);
//
//                    CheckCountBean var4=new CheckCountBean();
//                    var4.type=3;
//                    var4.usefulNum=var.singleNum;
//                    CheckCountBean var5=new CheckCountBean();
//                    var5.type=4;
//                    var5.usefulNum=var.multiNum;
//                    CheckCountBean var6=new CheckCountBean();
//                    var6.type=5;
//                    var6.usefulNum=var.argumentationNum;
//                    listPersonChecks.add(var4);
//                    listPersonChecks.add(var5);
//                    listPersonChecks.add(var6);
//                }else {
//                    CheckCountBean var=new CheckCountBean();
//                    var.type=0;
//                    CheckCountBean var2=new CheckCountBean();
//                    var2.type=1;
//                    CheckCountBean var3=new CheckCountBean();
//                    var3.type=2;
//                    listChecks.add(var);
//                    listChecks.add(var2);
//                    listChecks.add(var3);
//
//                    CheckCountBean var4=new CheckCountBean();
//                    var4.type=3;
//                    CheckCountBean var5=new CheckCountBean();
//                    var5.type=4;
//                    CheckCountBean var6=new CheckCountBean();
//                    var6.type=5;
//                    listPersonChecks.add(var4);
//                    listPersonChecks.add(var5);
//                    listPersonChecks.add(var6);
//                }
                    listChecks.addAll(var.machineCorrect);
                refreshGoodListv(var.machineCorrect);
                refreshPersonListv(var.manualCorrect);
                initBuyButton();
                initTips();
                }
            }


        });
    }

    //智能批改
    List<CheckCountDataBean> listChecks =new ArrayList<>();
    String title="";
    public void refreshGoodListv(List<CheckCountDataBean> list){
        check_count_listv.setAdapter(new CommonAdapter<CheckCountDataBean>(getContext(), list, R.layout.check_count_item_flayout) {
            @Override
            public void convert(final CommonAdapter.ViewHolder holder, final CheckCountDataBean item, int position) {
                if(item==null){
                    return;
                }
//                if(item.type==0){
//                    title="标准答案批改剩余：";
//                }else if (item.type==1){
//                    title="套题批改剩余：";
//                }else {
//                    title="文章写作批改剩余：";
//                }
//                SpannableStringBuilder builder = new SpannableStringBuilder(title+item.usefulNum+"次");
//                ForegroundColorSpan yellowSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
//                builder.setSpan(yellowSpan,title.length(), (title+item.usefulNum).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.single_check_count,item.goodsName);
                holder.setTextColor(R.id.single_check_count,R.color.blue057);
                if (item.isLimitNum==1){
                    //有限制
                    holder.setColorText(R.id.tv_count,R.color.blue057,item.num+"");
                    holder.setTextFaceType(R.id.tv_count,"font/851-CAI978.ttf");
                    holder.setColorText(R.id.tv_time,R.color.blue057,"次");
                }else {
                    //不限次
                    holder.setColorText(R.id.tv_count,R.color.blue057,"不限"+" ");
                    holder.setColorText(R.id.tv_time,R.color.blue057,"次");
                }
//                }else {
//                    holder.setColorText(R.id.tv_count,R.color.presale_tab_unselect,item.num+"");
//                    holder.setTextFaceType(R.id.tv_count,"font/851-CAI978.ttf");
//                    holder.setColorText(R.id.tv_time,R.color.presale_tab_unselect,"次");
//                }

                //
                if (item.willExpireNum!=0){
                    holder.setViewVisibility(R.id.tv_deadline, View.VISIBLE);
                    holder.setText(R.id.tv_deadline,item.willExpireNum+"次即将过期");
                }else {
                    holder.setViewVisibility(R.id.tv_deadline, View.GONE);
                }
                holder.setViewOnClickListener(R.id.rl_item_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  跳转次数详情
                        Bundle bundle=new Bundle();
                        bundle.putInt("goodsType",item.goodsType);
                        BaseFrgContainerActivity.newInstance(mActivity,CheckCountDetailListFragment.class.getName(),bundle);
                    }
                });
            }
        });
    }
    //人工批改
//  List<CheckCountBean> listPersonChecks =new ArrayList<>();
    String mTitle="";
    public void refreshPersonListv(List<CheckCountDataBean> list){
        person_check_count_listv.setAdapter(new CommonAdapter<CheckCountDataBean>(getContext(), list, R.layout.check_count_item_flayout) {
            @Override
            public void convert(final CommonAdapter.ViewHolder holder, final CheckCountDataBean item, int position) {
                if(item==null){
                    return;
                }
//                if(item.type==3){
//                    mTitle="标准答案批改剩余：";
//                }else if (item.type==4){
//                    mTitle="套题批改剩余：";
//                }else {
//                    mTitle="文章写作批改剩余：";
//                }
//                SpannableStringBuilder builder = new SpannableStringBuilder(title+item.usefulNum+"次");
//                ForegroundColorSpan yellowSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
//                builder.setSpan(yellowSpan,title.length(), (title+item.usefulNum).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.single_check_count,item.goodsName);
                holder.setTextColor(R.id.single_check_count,R.color.goldA96);

                if (item.isLimitNum==1){
                    //有限制
                    holder.setColorText(R.id.tv_count,R.color.goldA96,item.num+"");
                    holder.setTextFaceType(R.id.tv_count,"font/851-CAI978.ttf");
                    holder.setColorText(R.id.tv_time,R.color.goldA96,"次");
                }else {
                    holder.setColorText(R.id.tv_count,R.color.goldA96,"不限"+" ");
                    holder.setColorText(R.id.tv_time,R.color.goldA96,"次");
                }
//                }else {
//                    holder.setColorText(R.id.tv_count,R.color.presale_tab_unselect,item.num+"");
//                    holder.setTextFaceType(R.id.tv_count,"font/851-CAI978.ttf");
//                    holder.setColorText(R.id.tv_time,R.color.presale_tab_unselect,"次");
//                }
                //即将过期的次数
                if (item.willExpireNum!=0){
                    holder.setViewVisibility(R.id.tv_deadline, View.VISIBLE);
                    holder.setText(R.id.tv_deadline,item.willExpireNum+"次即将过期");
                }else {
                    holder.setViewVisibility(R.id.tv_deadline, View.GONE);
                }
                holder.setViewOnClickListener(R.id.rl_item_view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转次数详情
                        Bundle bundle=new Bundle();
                        bundle.putInt("goodsType",item.goodsType);
                        BaseFrgContainerActivity.newInstance(mActivity,CheckCountDetailListFragment.class.getName(),bundle);

                    }
                });


            }
        });
    }

    @OnClick(R.id.go_buy_check)
    public void ongo_buy_check() {
        if (!NetUtil.isConnected()){
            ToastUtils.showShort("网络未连接，请检查您的网络设置");
            return;
        }
//        StudyCourseStatistic.clickEssayCheckCount(listChecks.get(0).num,listChecks.get(1).num,listChecks.get(2).num);
        CheckOrderFragment fragment = new CheckOrderFragment();
        Bundle bundle = new Bundle();
//            bundle.putInt("selected_address_id", selAddressInfo.id);
        bundle.putString("page_source","购买申论批改页");
        fragment.setArguments(bundle);
        startFragmentForResult(fragment, 10002);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_tip4:
                DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tel();
                    }
                },null, SpUtils.getAboutPhone(),"取消","拨打");
                break;
        }
    }
    private void tel() {
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取打电话权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            String phone=SpUtils.getAboutPhone();
                                if(phone.contains("-")){
                                    phone=phone.replace("-","");
                                }
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                            startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
//        Ntalker.getInstance().logout();
    }

    public static CheckCountFragment newInstance(Bundle extra) {
        CheckCountFragment fragment = new CheckCountFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            LogUtils.v(TAG,"------显示----");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }else {
            LogUtils.v(TAG,"------隐藏----");
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser){
            LogUtils.v(TAG,"------可见----");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }else {
            LogUtils.v(TAG,"------不可见----");

        }

    }
}
