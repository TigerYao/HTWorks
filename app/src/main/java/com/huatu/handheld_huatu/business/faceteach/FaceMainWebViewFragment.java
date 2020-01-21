package com.huatu.handheld_huatu.business.faceteach;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.main.AllCourseFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.BaseStringBean;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.WebProgressBar;
import com.levylin.detailscrollview.views.DetailSingleScrollView;
import com.levylin.detailscrollview.views.DetailSingleWebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\6\3 0003.
 */

public class FaceMainWebViewFragment extends AbsSettingFragment implements AreasFilterPopWin.OnFilterConfirmClickListener {

    @BindView(R.id.detail_webview)
    DetailSingleWebView mWebView;

    @BindView(R.id.whole_content)
    DetailSingleScrollView mRootView;

    @BindView(R.id.top_layout)
    FrameLayout mTopLayout;

    @BindView(R.id.rb_area_filter)
    RadioButton mRdAreabtn;

    @BindView(R.id.rb_cate_filter)
    RadioButton mRdCatebtn;

    @BindView(R.id.rb_type_filter)
    RadioButton mRdTypebtn;

    @BindView(R.id.rg_filter_group)
    RadioGroup mRdGroupBtn;

    @BindView(R.id.progress_tip_bar)
    WebProgressBar mProgressLoading;


    String mSeletAreaId,mSeletCateId,mTopAreaId,mSelectExamType;
    AreasFilterPopWin mAreasFilterPopWin;

    String mLastUrl;//  最后一次的Url

    @Override
    protected int getContentView() {
        return R.layout.course_faceteach_main_layout;
    }

    private List<FaceAreaBean> mAllCateList,mAllAreaList;


    @OnClick({R.id.rb_area_filter, R.id.rb_cate_filter,R.id.rb_type_filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_cate_filter:
         /*       isFilterAsc = !isFilterAsc;
                Drawable ascArrow = ResourceUtils.getDrawable(R.drawable.jiantous_icon);
                Drawable desArrow = ResourceUtils.getDrawable(R.drawable.jiantoux_icon);
                if (isFilterAsc) {
                    desArrow.setBounds(0, 0, desArrow.getMinimumWidth(), desArrow.getMinimumHeight());
                    rbPriceFilter.setCompoundDrawables(null, null, desArrow, null);
                    filter_type = FILTER_TYPE_PRICE_DES;
                } else {
                    ascArrow.setBounds(0, 0, ascArrow.getMinimumWidth(), ascArrow.getMinimumHeight());
                    rbPriceFilter.setCompoundDrawables(null, null, ascArrow, null);
                    filter_type = FILTER_TYPE_PRICE_ASC;
                }*/

                if(ArrayUtils.isEmpty(mAllAreaList)){
                    loadAllAreaData(2);
                    return;
                }
                showPopWin(2);
                break;

            case R.id.rb_area_filter:
                if(ArrayUtils.isEmpty(mAllCateList)){
                    loadAllAreaData(1);
                    return;
                }
                showPopWin(1);
                 break;
            case R.id.rb_type_filter:
                if(ArrayUtils.isEmpty(mAllCateList)){
                    loadAllAreaData(3);
                    return;
                }
                 showPopWin(3);
                break;
        }
    }

    private void showPopWin(int actionType){
        if (mAreasFilterPopWin == null) {

            mAreasFilterPopWin = new AreasFilterPopWin(getActivity()).build(mAllCateList,mAllAreaList,mSeletAreaId,mSeletCateId,mTopAreaId);
            mAreasFilterPopWin.setOnFilterConfirmClickListener(this);
            mAreasFilterPopWin.setDismissListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mWebView){
                        mWebView.showMask(false);
                    }
                    if(null!=mRdGroupBtn){
                        mRdGroupBtn.clearCheck();
                    }
                }
            });
            mAreasFilterPopWin.setCurrentType(actionType);
        }else {
            mAreasFilterPopWin.setCurrentType(actionType);
        }
        if(actionType!=1&&(null!=mWebView)){
            mWebView.showMask(true);
        }
        mAreasFilterPopWin.showAtLocation(mTopLayout);
    }

    @Override
    protected void setListener(){
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                super.onProgressChanged(arg0, arg1);
                if(null!=mProgressLoading)
                    mProgressLoading.onProgressFinished(arg1);

            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//处理网页内部链接
              /*  if(mOldquestUrl.equals(url)){
                    return true;
                }*/
              /*  mOldquestUrl=url;
                LogUtils.e("shouldOverrideUrlLoading",url+"");
                if(TextUtils.isEmpty(url)) return true;
                if(url.startsWith(mHttpDomain)){

                } */
                if (TextUtils.isEmpty(url)) {
                    return true;
                }
                LogUtils.e("shouldOverrideUrlLoading",url);
                if (url.startsWith("tbopen:")) {//禁止跳转tbopen
                    return true;
                }else if(url.contains("htcourse/detail")){  //http://bm.huatu.com/htcourse/detail.php?course_id=114706&city_id=1755

                    Map<String, String> tmpUrl = UIJumpHelper.URLRequest(url);
                    String id = tmpUrl.get("course_id");
                    String city = tmpUrl.get("city_id") ;
                    FaceTeachDetailActivity.launch(getActivity(),StringUtils.valueOf(id),StringUtils.valueOf(city),url);
                    return true;
                }else if(url.contains("plus/view")) {//http://bm.huatu.com/plus/view.php?aid=5102983&city_id=1768

                    Map<String, String> tmpUrl = UIJumpHelper.URLRequest(url);
                    String id = tmpUrl.get("aid");
                    String city = tmpUrl.get("city_id") ;
                    String area=mSeletAreaId;

                    FaceTeachDetailActivity.launch(getActivity(),StringUtils.valueOf(id),StringUtils.valueOf(area),StringUtils.valueOf(mSeletCateId),url);
                    return true;
                }else if(url.startsWith("tel:")){
                    tel(url);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(null!=mProgressLoading) mProgressLoading.onLoadingStart();

            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                // onLoadingFinish(webView, url);
                //isFristLoading=false;
            }
         });
    }

    private void tel(final String telPhone) {
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
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
                            startActivity(intent);
                        } else {
                            CommonUtils.showToast("没有打电话权限");
                        }
                    }
                });
    }

    private boolean mIsRefresh=true;//刷新，或者第一次显示commonLoadingView
    public void refreshUI(){
        mIsRefresh=true;
        requestData();
    }

    @Override
    public void requestData() {

        mTopAreaId=PrefStore.getSettingString(UserInfoUtil.userId+"_"+"provinceAreaId","");
        mSelectExamType= PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceExamType","0");

        if(null!=mRdTypebtn){
            mRdTypebtn.setText(mSelectExamType.equals("1")?"面试":"笔试");
        }
        mSeletAreaId=PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceAreaId","");

        if(!TextUtils.isEmpty(mSeletAreaId)){
            String areaName=PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceAreaName","");
            mRdAreabtn.setText(StringUtils.cultString2(areaName,7));
        }

        mSeletCateId=PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceCateId","");
        if(!TextUtils.isEmpty(mSeletCateId)){
            String CateName=PrefStore.getSettingString(UserInfoUtil.userId+"_"+"faceCateName","");
            mRdCatebtn.setText(CateName);
        }
        if((!TextUtils.isEmpty(mSeletAreaId))&&(!TextUtils.isEmpty(mSeletCateId))){
            filterListConfirm(1,null);
            return;
        }
        loadAllAreaData(0);
    }

    private void setLoadingUIStatus(int type,boolean hasFinish){
        if(getParentFragment() instanceof AllCourseFragment){
             if(!hasFinish){
                if(type==3)
                    ((AllCourseFragment)getParentFragment()).getCommLoadingView().showNetworkTip();
                else
                    ((AllCourseFragment)getParentFragment()).getCommLoadingView().showServerError();
            }else {
                ((AllCourseFragment)getParentFragment()).getCommLoadingView().hide();
            }
        }
    }

    public void loadAllAreaData(final int actionType ){
        ((BaseActivity)getActivity()).showProgress();
          ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getEducationAllArea(), new NetListResponse<FaceAreaBean>() {
            @Override
            public void onSuccess(BaseListResponseModel<FaceAreaBean> model) {
                mAllAreaList=model.data;
                if(null!=mRdAreabtn&& !ArrayUtils.isEmpty(mAllAreaList)){
                    List<FaceAreaBean> child =mAllAreaList.get(0).child;
                    if(!ArrayUtils.isEmpty(child)&&(actionType==0)){
                        mRdAreabtn.setText(child.get(0).areaname);
                        mSeletAreaId=child.get(0).areaid;
                        mTopAreaId=mAllAreaList.get(0).areaid;
                    }
                }
                loadAllCateList(actionType);
            }

            @Override
            public void onError(String message, int type) {
                setLoadingUIStatus(type,false);
                ((BaseActivity)getActivity()).hideProgress();
            }
        });
    }

    private void loadAllCateList(final int actionType){
         ServiceExProvider.visitList(new CompositeSubscription(), CourseApiService.getApi().getEducationExamCategory(),
                new NetListResponse<FaceAreaBean>() {

            @Override
            public void onSuccess(BaseListResponseModel<FaceAreaBean> model){
                  mAllCateList=model.data;
                  if(null!=mRdCatebtn&&(!ArrayUtils.isEmpty(mAllCateList))&&(actionType==0)){
                      mRdCatebtn.setText(mAllCateList.get(0).areaname);
                      mSeletCateId=mAllCateList.get(0).areaid;
                  }
                ((BaseActivity)getActivity()).hideProgress();
                  if(actionType>0){
                      showPopWin(actionType);
                  }else {
                      filterListConfirm(1,null);
                  }

            }
            @Override
            public void onError(String message, int type){
                setLoadingUIStatus(type,false);
                ((BaseActivity)getActivity()).hideProgress();
            }
        });
    }



    @Override
    public  void filterListConfirm(int actionType, FaceAreaBean cateBean){

        if(actionType==1&&(cateBean!=null)){
            mSeletAreaId=cateBean.areaid;
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"faceAreaId",cateBean.areaid);
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"faceAreaName",cateBean.areaname);
            if(null!=mRdAreabtn)
               mRdAreabtn.setText(StringUtils.cultString2(cateBean.areaname,7));
        }
        else if(actionType==2){
            mSeletCateId=cateBean.areaid;
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"faceCateId",cateBean.areaid);
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"faceCateName",cateBean.areaname);
            if(null!=mRdCatebtn)
                mRdCatebtn.setText(cateBean.areaname);
        }else if(actionType==3){
            mSelectExamType=cateBean.areaid;
            PrefStore.putSettingString(UserInfoUtil.userId+"_"+"faceExamType",cateBean.areaid);
            if(null!=mRdTypebtn)
                mRdTypebtn.setText(cateBean.areaname.replace("课程",""));
        }

        if(TextUtils.isEmpty(mSeletAreaId)||TextUtils.isEmpty(mSeletCateId)) return;
        long curTime=System.currentTimeMillis();
        String md5= String.format("areaid=%s&catid=%s&timestamp=%s&typeid=%s",mSeletAreaId,mSeletCateId, curTime,mSelectExamType);

        LogUtils.e("test",md5);
        String sign=  Md5Util.toSign(md5);

        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getEducationCatalogurl(mSeletAreaId, mSeletCateId, String.valueOf(curTime), StringUtils.parseInt(mSelectExamType), sign),
                new NetObjResponse<BaseStringBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<BaseStringBean> model) {
                         if(mIsRefresh) {
                            mIsRefresh=false;
                            setLoadingUIStatus(0,true);
                         }
                        LogUtils.e("test",model.data.url);
                        mLastUrl=model.data.url;
                        /*if(mLastUrl.equals("http://test.bm.huatu.com/zhaosheng/ah/gkbs2908.html?app=1")){//笔试

                            mLastUrl="http://m.bj-alpha.huatu.com/jianzhang/ah/gkbs.html?app=1" ;
                        }else if(mLastUrl.equals("http://bm.huatu.com/zhaosheng/ah/gkms4742.html?app=1")){
                             mLastUrl="http://m.bj-alpha.huatu.com/jianzhang/ah/gkms.html?app=1";
                        }*/
                        mWebView.loadUrl(mLastUrl);
                    }

                    @Override
                    public void onError(String message, int type) {
                        if(mIsRefresh) {
                            mIsRefresh=false;
                        }
                        setLoadingUIStatus(type,false);
                        // showErrorUI(type);
                    }
                });
    }

   public void resumeUrl(){
        if(!TextUtils.isEmpty(mLastUrl)) {
            if(null!=mRootView){
                mRootView.scrollToTop();
            }
            mWebView.loadUrl(mLastUrl);
        }
   }

   public void clearContent(){

       if(null!=mWebView){
           mWebView.stopLoading();
           mWebView.loadUrl("about:blank");// 清空当前加载
       }

   }

}
