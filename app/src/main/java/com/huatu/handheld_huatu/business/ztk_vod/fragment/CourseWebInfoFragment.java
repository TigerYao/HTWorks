package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.ExplandListAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.utils.ArrayUtils;
import com.huatu.widget.WebProgressBar;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Arrays;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class CourseWebInfoFragment extends AbsSettingFragment implements  NestedScrollView.OnScrollChangeListener{

    @BindView(R.id.progress_tip_bar)
    WebProgressBar mProgressLoading;

    @BindView(R.id.course_teacher_detail_judge_lv)
    NoScrollListView mListView;

    NestedScrollView mScrollview;

    @BindView(R.id.bt_dingbu)
    ImageView mGoTopBtn;

    @BindView(R.id.list_expland_img)
    ImageView mExplandImg;

    private String mCourseId = "";
    private int mCourseType=0;

    X5WebView mWebView;
    boolean isFristLoading=true;

    public static void lanuch(Context context, String courseId, int courseType) {

        Bundle arg = new Bundle();
        arg.putString(ArgConstant.COURSE_ID, courseId);
        arg.putInt(ArgConstant.TYPE,courseType);

        FragmentParameter tmppars= new FragmentParameter( CourseWebInfoFragment.class, arg);
  /*      tmppars.setAnimationRes(new int[]{R.anim.qscale_enter, R.anim.qslide_still,
                R.anim.qslide_still, R.anim.qscale_exit});*/

        UIJumpHelper.jumpFragment(context,tmppars);
    }

    @Override
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mCourseType=args.getInt(ArgConstant.TYPE,0);
    }

    @Override
    protected int getContentView() {
        return R.layout.play_scrollweb_layout;
    }


    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("课程详情");
        initWebView(R.id.lay_webview_layout);
    }

    void initWebView(@IdRes int layId) {
        // OWebView webView = new OWebView(getActivity());
        mWebView = new X5WebView(getContext().getApplicationContext());
        ((ViewGroup) getRootView().findViewById(layId)).addView(mWebView,  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

  /*      mWebView.setOnScrollChangedCallback(new OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy) {
                XLog.e("onScrollChange01", dx + "," + dy);
            }
        });*/
      /*  mWebView.setWebViewClient(new OWebClient(new Runnable() {
            @Override
            public void run() {
                hideLoadingView();
            }
        }));
        mWebView.addJavascriptInterface(this, "App");*/
    }

    @Override
    public void requestData() {
        super.requestData();
        mScrollview = this.findViewById(R.id.lay_nsv);
        mScrollview.setOnScrollChangeListener(this);
        mGoTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // v.setVisibility(View.GONE);
                //v.setTag("0");
                mScrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=mScrollview){

                            mScrollview.fling(0);
                            mScrollview.smoothScrollTo(0, 0);
                        }
                            //mScrollview.fullScroll(View.FOCUS_UP);
                    }
                });
             }
        });
        this.findViewById(R.id.list_expland_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistAdapter==null) return;
                if (mlistAdapter.getCount() == ExplandListAdapter.ExplandCount) {
                    mlistAdapter.setItemNum(mlistAdapter.getRealCount());
                  /*  ivDown.setImageDrawable(getResources().getDrawable(
                            R.drawable.icon_up));*/
                    AnimUtils.showOpenRotateAnimation(v);
                    mlistAdapter.notifyDataSetChanged();
                } else {
                    mlistAdapter.setItemNum(ExplandListAdapter.ExplandCount);
                    AnimUtils.showCloseRotateAnimation(v);
                    mlistAdapter.notifyDataSetChanged();
                }
             }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView arg0, int arg1) {
                 super.onProgressChanged(arg0, arg1);
                if(mProgressLoading!=null) mProgressLoading.onProgressFinished(arg1);
                // onProgressFinished(arg1);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {//处理网页内部链接
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mProgressLoading!=null) mProgressLoading.onLoadingStart();
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
               // onLoadingFinish(webView, url);
                isFristLoading=false;
            }

        });

        Observable<BaseResponseModel<CourseDetailBean>> tmplist=  RetrofitManager.getInstance().getService().getCourseDetailInfo(mCourseId, 0, 0);
//        mCourseType==0?RetrofitManager.getInstance().getService().getPlayoffCourseDetail(mCourseId, 0)
//                                                                              :RetrofitManager.getInstance().getService().getLiveCourseDetail(mCourseId, 0);
        ServiceExProvider.visit(getSubscription(), tmplist, new NetObjResponse<CourseDetailBean>() {
            @Override
            public void onSuccess(BaseResponseModel<CourseDetailBean> model) {
                bindUI(model.data);
            }

            @Override
            public void onError(String message, int type) {
                ToastUtils.showShort("课程信息加载失败");
            }
        });
        String webUrl = RetrofitManager.getInstance().getBaseUrl() + "c/v5/courses/"+mCourseId+"/getClassExt";
        mWebView.loadUrl(webUrl);
    }

    ExplandListAdapter mlistAdapter;
    private void bindUI(CourseDetailBean bean){

        try{
            if(!ArrayUtils.isEmpty(bean.columnHeaders)){
               if(mlistAdapter==null){
                    mlistAdapter=new ExplandListAdapter(getContext(), bean.columnHeaders,bean.columnDetails);
                }
                mListView.setAdapter(mlistAdapter);
               if(ArrayUtils.size(bean.columnHeaders)>ExplandListAdapter.ExplandCount)
                   mExplandImg.setVisibility(View.VISIBLE);
            }
            setText(R.id.course_detail_title_tv,bean.classTitle);
            setText(R.id.course_detail_description_tv,bean.courseIntroduction);

            TextView tvOriginPrice=this.findViewById(R.id.course_detail_origin_price_tv);
            tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvOriginPrice.setVisibility(View.GONE);

            TextView tvPrice=this.findViewById(R.id.course_detail_price_tv);
            if (TextUtils.isEmpty(bean.actualPrice) || TextUtils.equals("0", bean.actualPrice)) {
                setText(R.id.course_detail_price_tv,"免费");
                tvOriginPrice.setVisibility(View.GONE);
                if (mCourseType == 1) {
                    setText(R.id.course_detail_buy_num_tv,bean.buyNum + "人报名");
                } else {
                    setText(R.id.course_detail_buy_num_tv,bean.buyNum + "人学过");
                }
            } else {

                Spannable span = new SpannableString("¥ "+bean.actualPrice);
                span.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPrice.setText(span);
                tvPrice.setTextColor(Color.parseColor("#FF3F47"));
                if (TextUtils.equals(bean.actualPrice, bean.price)) {
                    tvOriginPrice.setVisibility(View.GONE);
                } else {
                    tvOriginPrice.setVisibility(View.VISIBLE);

                    tvOriginPrice.setText("¥ " + bean.price);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.e("bindUI",e.getMessage());
        }
     }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int dy = scrollY - oldScrollY;
        //XLog.e("onScrollChange", scrollY + "," + oldScrollY);
        boolean hasOverHeight = scrollY > DisplayUtil.getScreenHeight();
        if (null != mGoTopBtn) {
           boolean isShow = "1".equals(mGoTopBtn.getTag().toString());
            if (hasOverHeight) {
                if (!isShow) {
                    mGoTopBtn.setTag("1");
                    mGoTopBtn.setVisibility(View.VISIBLE);
                    AnimUtils.AlphaIn(mGoTopBtn);
                }
            } else {
                if (isShow) {
                    mGoTopBtn.setTag("0");
                    mGoTopBtn.setVisibility(View.GONE);
                }
            }
        }
     }

    @Override
    public void onDestroy() {
        if(mWebView!=null) mWebView.onDestory();
        if(null!=mScrollview) mScrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)null);
        super.onDestroy();
    }
}
