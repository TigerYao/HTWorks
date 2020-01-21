package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.BaseBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.BuyRecycleCourseListResponse;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import java.util.ArrayList;

/**
 * Created by cjx on 2018\7\6 0006.
 */

public class MyRecylerCourseFragment extends MySingleTypeCourseFragment {

    private boolean mHasRecory=false;
    //private int mCourseType=0;
    @Override
    public boolean isRecylerView(){
        return true;
    }

     @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        //  EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("回收站");
         getTitleBar().setShadowVisibility(View.VISIBLE);
    }

   @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }


    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_date);
        getEmptyLayout().setTipText(R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.mipmap.course_no_cache_icon);
    }

    public static MyRecylerCourseFragment getRecylerInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ArgConstant.TYPE, type);
        MyRecylerCourseFragment tmpFragment = new MyRecylerCourseFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        // CourseApiService.getApi().getMyCourseList(0,offset).enqueue(getCallback());
        CourseApiService.getApi().getMyRecyleCourses("",1,"","",1,"","",offset,limit).enqueue(getCallback());
    }

  /*  protected RetrofitCallback<BuyRecycleCourseListResponse> getCallback3() {
        return new RetrofitCallbackWrapper3(this);
    }

    private class RetrofitCallbackWrapper3 extends RetrofitCallback<BuyRecycleCourseListResponse> {

        final KindRetrofitCallBack<BuyCourseListResponse> mCallback;

        public RetrofitCallbackWrapper3(KindRetrofitCallBack<BuyCourseListResponse> callBack){
            this.mCallback = callBack;
        }

        @Override
        protected void onSuccess(BuyRecycleCourseListResponse response)  {
            if(mCallback != null&&!mCallback.isFragmentFinished()){

                BuyCourseListResponse tmplist=new BuyCourseListResponse();
                tmplist.data=new BuyCourseListResponse.Data();
                tmplist.data.listObject=new BuyCourseBean();
                tmplist.data.listObject.last=new ArrayList<BuyCourseBean.Data>();
                tmplist.data.listObject.last.addAll(response.getListResponse());
                mCallback.onSuccess(tmplist);
            }
        }

        @Override
        protected void onFailure(String error,int type) {
            if(mCallback != null&&!mCallback.isFragmentFinished()){
                mCallback.onError(error, type);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            if(mCallback != null){
                mCallback.onSubscriberStart();
            }
        }
    }*/

    @Override
    public void onItemClick(int position, View view, int type) {
        BuyCourseBean.Data mineItem = mListAdapter.getCurrentItem(position);
        switch (type) {

            case EventConstant.EVENT_DELETE://回收站删除
                //setOnTop(mineItem.rid,mineItem.orderId,mineItem.isTop?false:true);
                deleteConfrim(mineItem,position);
               // deleteOrder(mineItem.orderId);
                break;
            case EventConstant.EVENT_CHANGE://回收站恢复
                recoveryCourse(mineItem.rid, mineItem.orderId,position);
                break;
            default:
               // super.onItemClick(position,view,type);
                break;
        }
    }

    private CustomConfirmDialog mRecyleConfirmDialog;
    private BuyCourseBean.Data  mRecyleCourseInfo;
    private int mDelPostion;
    private void deleteConfrim(BuyCourseBean.Data mineItem,int postion){
        mRecyleCourseInfo=mineItem;
        mDelPostion=postion;
        if(mRecyleConfirmDialog == null) {
            mRecyleConfirmDialog = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程\n该操作不可恢复");
            mRecyleConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mRecyleCourseInfo!=null){
                        deleteOrder(mRecyleCourseInfo.orderId);
                    }
                }
            });
        }
        if(!mRecyleConfirmDialog.isShowing()) {
            mRecyleConfirmDialog.show();
        }
    }

    private void deleteOrder(String orderId){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceExProvider.visitSimple(mCompositeSubscription,CourseApiService.getApi().deleteRecoveryCourse(orderId),new NetObjResponse<String>(){

            @Override
            public void onError(String message,int type){
                ToastUtils.showShort("删除出错");
            }

            @Override
            public void onSuccess(BaseResponseModel<String> model){
                ToastUtils.showShort("删除成功");
                if((mDelPostion>=0)&&(null!=mListAdapter)){
                    mListAdapter.removeAt(mDelPostion);
                    mDelPostion=-1;
                }
                else  if (null != myPeopleListView) {
                    myPeopleListView.getRefreshableView().scrollToPosition(0);
                    myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                }

            }
        });
    }

    private void recoveryCourse(long rid,String orderId,final int positon){

        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceExProvider.visit(mCompositeSubscription,CourseApiService.getApi().recoveryDelCourse(rid,orderId),new NetObjResponse<BaseBooleanBean>(){

            @Override
            public void onError(String message,int type){
                ToastUtils.showShort("恢复出错");
            }

            @Override
            public void onSuccess(BaseResponseModel<BaseBooleanBean> model){
                if(model.data.status){
                    mHasRecory=true;
                    ToastUtils.showShort("恢复成功");
                    if((positon>=0)&&(null!=mListAdapter)){
                        mListAdapter.removeAt(positon);
                    }
                    else if(null!=myPeopleListView){
                        myPeopleListView.getRefreshableView().scrollToPosition(0);
                        myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                    }
                }else
                    ToastUtils.showShort("恢复出错");
            }
        });
    }

    @Override
    protected void onGoBack() {
        if (mHasRecory) {
            Intent data = new Intent();
            setResult(Activity.RESULT_OK, data);
        }
        super.onGoBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mHasRecory) {
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
