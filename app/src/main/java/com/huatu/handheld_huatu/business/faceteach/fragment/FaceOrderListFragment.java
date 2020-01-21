package com.huatu.handheld_huatu.business.faceteach.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.faceteach.FacePayWayActivity;
import com.huatu.handheld_huatu.business.faceteach.SplitOrderActivity;
import com.huatu.handheld_huatu.business.faceteach.adapter.FaceOrderListAdapter;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceOrderBean;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceOrderListResponse;
import com.huatu.handheld_huatu.business.me.SerciveDialog;
import com.huatu.handheld_huatu.helper.XiaoNengAssist;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BaseStringBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FaceGoodsDetailBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullNestRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class FaceOrderListFragment extends ABaseListFragment<FaceOrderListResponse> implements OnRecItemClickListener {

    @BindView(R.id.xi_comm_page_list)
    PullNestRefreshRecyclerView mWorksListView;

    public int getContentView() {
        return R.layout.comm_ptrlist_layout4;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    FaceOrderListAdapter mListAdapter;

    public int mOrderStatus=2;// status 订单状态，0-未付款，1-已付款，2-全部 默认全部

    CompositeSubscription mCompositeSubscription;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListResponse = new FaceOrderListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new FaceOrderListAdapter(getContext(), mListResponse.mLessionlist);
        mListAdapter.setOnViewItemClickListener(this);

        PrefStore.putUserSettingInt("refresh_order_status",0);//如果是新建，没必要判断刷新状态
    }


    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    public void refreshByType(int tabIndex){
          if(tabIndex==0) mOrderStatus=2;
          else if(tabIndex==1) mOrderStatus=0;
          else mOrderStatus=1;
        showFirstLoading();
        superOnRefresh();

    }

    private void superOnRefresh(){
        super.onRefresh();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        mWorksListView.getRefreshableView().setPagesize(getLimit());
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        mWorksListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 6)));
    }


    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }
    @Override
    protected void onRefreshCompleted(){
        if(null!=mWorksListView) mWorksListView.onRefreshComplete();
    }

    @Override
    protected void setListener() {
        mWorksListView.getRefreshableView().setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData( int offset, int limit) {

        if(TextUtils.isEmpty(UserInfoUtil.mId)){
            String mobile=  TextUtils.isEmpty(UserInfoUtil.ucId)? SpUtils.getMobile():UserInfoUtil.ucId;
            String curTime=System.currentTimeMillis()+"";
            final String md5= String.format("mobile=%s&timestamp=%s",mobile,curTime);

            LogUtils.e("test",md5);
            String sign=  Md5Util.toSign(md5);

            final  int curPage=offset;
            final int  curPageSize=limit;
            ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getEducationMid(mobile, curTime, sign), new NetObjResponse<BaseStringBean>() {
                @Override
                public void onSuccess(BaseResponseModel<BaseStringBean> model) {
                    UserInfoUtil.mId=model.data.url;
                    requestOrderList(curPage,curPageSize);
                }

                @Override
                public void onError(String message, int type) {
                    showNetworkPrompt(type);
                }
            });
            return;
        }
        requestOrderList(offset,limit);
     }


/*     @Override
    public void onSuccess(FaceOrderListResponse response){

        if(!ArrayUtils.isEmpty(response.data)){

            response.data.get(1).IsElectronicProtocol=1;
            response.data.get(1).state=1;
            response.data.get(1).hasElectronicProtocol=1;
        }
        super.onSuccess(response);
    }*/

    private void requestOrderList(int offset, int limit){
        String mid= String.valueOf(UserInfoUtil.userId);
        String bmid=UserInfoUtil.mId;
        int status=mOrderStatus  ;
        String curTime=System.currentTimeMillis()+"";

        String md5= String.format("bmmid=%s&mid=%s&ostate=%s&page=%s&rows=%s&timestamp=%s",bmid,mid,status, offset,limit,curTime);

        LogUtils.e("test",md5);
        String sign=  Md5Util.toSign(md5);


        String params=String.format("{'bmmid':'%s','mid':'%s','ostate':'%s','page':%s,'rows':'%s','timestamp':'%s','sign':'%s'}",bmid,mid,status, offset,limit,curTime,sign).replace("'","\"");
        CourseApiService.getApi().getEducationOrderByType(params).enqueue(getCallback());
    }


    private int mCurrentIndex=-1;
    private String mCurrentOid="";
    @Override
    public void onItemClick(final int position, View view, int type) {
        FaceOrderBean mineItem = mListAdapter.getItem(position);
        if(null==mineItem) return;
        switch (type) {
            case EventConstant.EVENT_LIKE:
               if (mineItem.state == 1||mineItem.state==4) {
                  if (mineItem.hasElectronicProtocol == 1) {
                      BaseWebViewFragment.lanuch(getContext(), mineItem.signurl, "");
                  }
               }
               else {
                   if(mineItem.split==0){
                       mCurrentIndex=position;
                       mCurrentOid=mineItem.oid;
                       FacePayWayActivity.startFacePayWayForResultActivity(getActivity(), mineItem.oid, mineItem.oid, mineItem.title, mineItem.title,
                               mineItem.priceCount, mineItem.priceCount, mineItem.alipay_account, mineItem.pid);
                   }else {
                       SplitOrderActivity.startSplitOrderForResultActivity(getActivity(),  mineItem.oid, mineItem.title, mineItem.title,  mineItem.priceCount,mineItem.alipay_account, mineItem.pid, false);
                   }
               }
               break;
            case EventConstant.EVENT_MORE:
                if(!XiaoNengAssist.getInstance().isInited()) {
                    new AppPermissions(getActivity()).request(Manifest.permission.READ_PHONE_STATE,
                                                              Manifest.permission.RECORD_AUDIO,
                                                              Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(new Subscriber<Boolean>() {
                                @Override
                                public void onCompleted() {  }
                                @Override
                                public void onError(Throwable e) {
                                    CommonUtils.showToast("获取权限失败");
                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    if (aBoolean) {
                                        XiaoNengAssist.getInstance().init(getActivity());
                                        showCallServer(position);
                                    } else {
                                        CommonUtils.showToast("没有权限");
                                    }
                                }
                            });

                }else {
                    showCallServer(position);
                }
                break;
        }
    }

    private void showCallServer(int postion){

        FaceOrderBean mineItem = mListAdapter.getItem(postion);
        if(null==mineItem) return;
        String curTime = System.currentTimeMillis() + "";

        String md5 = String.format("aid=%s&timestamp=%s", mineItem.aid,  curTime);

        String sign = Md5Util.toSign(md5);

        String params = String.format("{'aid':'%s','timestamp':'%s','sign':'%s'}", mineItem.aid, curTime, sign).replace("'", "\"");
        showProgress();
        final String curAid=mineItem.aid;
        ServiceProvider.getFaceGoodsDetail(getSubscription(), params, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
                ToastUtils.showShort("数据加载失败");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                FaceGoodsDetailBean  faceGoods = (FaceGoodsDetailBean) model.data;
                if (faceGoods != null) {
                    if(!TextUtils.isEmpty(faceGoods.customer_branch)){
                        XiaoNengAssist.getInstance().startChat(faceGoods.customer_branch, faceGoods.title, curAid);
                    }else if(!TextUtils.isEmpty(faceGoods.phone)){
                        showSercive(faceGoods.phone);
                    }
                }
                hideProgess();
            }
        });

    }

    private void showSercive(final String phone) {
        final SerciveDialog serciveDialog = new SerciveDialog(getContext(), R.layout.dialog_me_sevice);

        TextView text_ok = (TextView) serciveDialog.mContentView.findViewById(R.id.text_ok);
        TextView text_cancel = (TextView) serciveDialog.mContentView.findViewById(R.id.text_cancel);
        TextView zs_phone = (TextView) serciveDialog.mContentView.findViewById(R.id.zs_phone);
        if(zs_phone!=null) {
            zs_phone.setText(phone);
        }
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serciveDialog.dismiss();
            }
        });

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serciveDialog.dismiss();
                tel(phone.replace("-",""));
            }
        });
        serciveDialog.show();
    }

    private void tel(final String phone) {
        new AppPermissions(getActivity()).request(Manifest.permission.CALL_PHONE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {  }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取打电话权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                      ;
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                            startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult( requestCode,  resultCode,  data);
        if((requestCode==3001||requestCode==3002)){
            int curStatus= PrefStore.getUserSettingInt("refresh_order_status",1);
            if(curStatus==1){
                PrefStore.putUserSettingInt("refresh_order_status",0);
                if(null!=mWorksListView){
                    mWorksListView.getRefreshableView().scrollToPosition(0);
                    mWorksListView.setRefreshing(true);//会触发onRefresh事件
                }
            }else if(requestCode==3002){

                if(!TextUtils.isEmpty(mCurrentOid)){
                    final String oid=mCurrentOid;
                    mCurrentOid="";
                    int splitStatus=  PrefStore.getUserSettingInt("refresh_split_status_"+oid,0);
                    if(splitStatus==1){
                        PrefStore.putUserSettingInt("refresh_split_status_"+oid,0);
                        FaceOrderBean mineItem = mListAdapter.getItem(mCurrentIndex);
                        if(null==mineItem||(!mineItem.oid.equals(oid))) return;
                        mineItem.split=1;
                        mListAdapter.notifyItemChanged(mCurrentIndex);
                    }

                }

            }
         }
    }

    CustomLoadingDialog mLoadingDialog;
    private void showProgress(){
        mLoadingDialog= DialogUtils.showLoading(getActivity(),mLoadingDialog);
    }

    private void hideProgess(){
        DialogUtils.dismissLoading(mLoadingDialog);
    }
}
