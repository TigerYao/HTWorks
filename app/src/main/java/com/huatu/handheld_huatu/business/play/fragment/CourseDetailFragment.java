package com.huatu.handheld_huatu.business.play.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.bean.ActDetailInfo;
import com.huatu.handheld_huatu.business.play.bean.CourseActDetailBean;
import com.huatu.handheld_huatu.business.play.bean.VideoPlayVideoInfoBean;
import com.huatu.handheld_huatu.business.play.event.VideoInfoMessageEvent;
import com.huatu.handheld_huatu.business.play.event.VideoPlayGetVideoInfoErrorEvent;
import com.huatu.handheld_huatu.business.play.view.ListableScrollView;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomBottomDialog;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.SimpleLabelsLayout;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.X5WebView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 售前课程详情页
 */

public class CourseDetailFragment extends BaseFragment {
    private final String TAG = "httpCourseDetailFragment";
    @BindView(R.id.course_detail_title_tv)
    TextView tvTitle;
    @BindView(R.id.course_detail_description_tv)
    TextView tvDes;
    @BindView(R.id.course_detail_price_tv)
    TextView tvPrice;
    @BindView(R.id.course_detail_origin_price_tv)
    TextView tvOriginPrice;
    @BindView(R.id.course_detail_buy_num_tv)
    TextView tvBuyNum;

//    @BindView(R.id.course_detail_web_view_id)
    X5WebView mWebview;
    @BindView(R.id.bt_dingbu)
    Button bt_dingbu;
    @BindView(R.id._buydetails_scrollview)
    ListableScrollView buydetails_scrollview;
    @BindView(R.id.server_error_layout)
    RelativeLayout noServer;
    @BindView(R.id.no_network_layout)
    RelativeLayout no_network_layout;
    @BindView(R.id.course_content)
    LinearLayout course_content;
    @BindView(R.id.course_groupbuy_num)
    TextView mGroupBuyNum;
    @BindView(R.id.only_olderstud)
    TextView mIsOderStudCanJoin;
    @BindView(R.id.group_buy_rule_layout)
    View mGroupBuyRule;

    @BindView(R.id.labels_layout)
    SimpleLabelsLayout mLabelsLayout;
    @BindView(R.id.activte_labels)
    View mActiveLaybelsView;
    @BindView(R.id.detail_info_list)
    ListView mDetailInfoList;
    @BindView(R.id.divider)
    View mDivider;
    @BindView(R.id.load_more)
    View mLoadMoreBtn;

    private XiaoNengHomeActivity mActivity;
    private Bundle args;

    private String courseId;
    private int courseType;
    private boolean selectCollect;
    private CourseDetailBean courseDetailBean;
    private boolean mIsGroupBuyType = false;
    private int mActivityId = 0;
    private CustomBottomDialog mActIntroDialog;
    private CourseDetailAdapter mDetailAdapter;

    public CourseDetailFragment() {
        super();
    }

    public static CourseDetailFragment getInstance(String courseID, int courseTYPE, int minActivityId, boolean selectCollect) {
        Bundle ids = new Bundle();
        ids.putString("course_id", courseID);
        ids.putInt("course_type", courseTYPE);
        ids.putInt("activity_id", minActivityId);
        ids.putBoolean("selectCollect", selectCollect);
        CourseDetailFragment tempCourseDetailFragment = new CourseDetailFragment();
        tempCourseDetailFragment.setArguments(ids);
        return tempCourseDetailFragment;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_detail_layout_v2;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate...");
        this.mActivity = (XiaoNengHomeActivity) getActivity();
        args = getArguments();
        if (args != null) {
            courseId = args.getString("course_id");
            courseType = args.getInt("course_type");
            mActivityId = args.getInt("activity_id", 0);
            selectCollect = args.getBoolean("selectCollect");
            mIsGroupBuyType = mActivityId != 0;
            LogUtils.d(TAG, "onCreate..."+courseId +".....");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.v(this.getClass().getName() + " onCreateView()");
        super.onCreateView(inflater,container, savedInstanceState);

        mWebview = new X5WebView(UniApplicationContext.getContext());//
        LinearLayout.LayoutParams tmpParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        course_content.addView(mWebview,tmpParams);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        mGroupBuyRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBuyRules();
            }
        });
        initWebviewConfig();
    }

    private boolean mIsFirstResize=true;
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return UIJumpHelper.dealOverrideUrl(mActivity, url);
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            // hideProgress();
            if(mIsFirstResize){
                mIsFirstResize=false;
                return;
            }
            webView.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
        }
    }

    private void initWebviewConfig(){
        mWebview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebview.getSettings().setSaveFormData(false);
        mWebview.getSettings().setSavePassword(false);
        mWebview.setWebViewClient(new MyWebViewClient());
        mWebview.addJavascriptInterface(new JSInterface(), "App");
        mWebview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private final class JSInterface{
        @JavascriptInterface
        public void resize(final float height) {
            LogUtils.e("resize",height+"");
            if(height==0){
                return;
            }
            Method.runOnUiThread(getActivity(),new Runnable() {
                @Override
                public void run() {
                    mWebview.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                            (int) ((height+15) * getResources().getDisplayMetrics().density)));
              }
            });
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetUtil.isConnected()) {
            onLoadData();
        } else {
            course_content.setVisibility(View.GONE);
            no_network_layout.setVisibility(View.VISIBLE);
            no_network_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "no_network_layout onclicked...");
                    if (NetUtil.isConnected()) {
                        onLoadData();
                    }
                }
            });
        }
        initScrollInfo();
        setListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mWebview){
            mWebview.setOnTouchListener(null);
            mWebview.onDestory();
            mWebview=null;
        }
        LogUtils.v(this.getClass().getName() + " onDestroy()");
        LogUtils.d(TAG, "onDestroy...");
    }

    protected void onInitView() {
        courseId = args.getString("course_id");
        courseType = args.getInt("course_type");
        mActivityId = args.getInt("activity_id", 0);
        mIsGroupBuyType = mActivityId != 0;
        tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvOriginPrice.setVisibility(View.GONE);
    }

    //Todo
    private void initScrollInfo() {
        buydetails_scrollview.setScrollViewListener(new ListableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                if (y <= DisplayUtil.dp2px(5)) {
                    bt_dingbu.setVisibility(View.GONE);
                } else {
                    bt_dingbu.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setListener() {
        bt_dingbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buydetails_scrollview.scrollTo(0, 0);
                bt_dingbu.setVisibility(View.GONE);
            }
        });
    }

    protected void reLoad(String course_id, int course_type, int activity_id){
        mDetailAdapter = null;
        courseId = course_id;
        courseType = course_type;
        mActivityId = activity_id;
        courseDetailBean.activityList = null;
        mActiveLaybelsView.setVisibility(View.GONE);
        buydetails_scrollview.scrollTo(0, 0);
        bt_dingbu.setVisibility(View.GONE);
        onLoadData();
    }
    protected void onLoadData() {
        showLoadingView(true);
        NetResponse netResponse = new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showLoadingView(false);
                LogUtils.d(TAG, "onerror...");
                no_network_layout.setVisibility(View.GONE);
                course_content.setVisibility(View.GONE);
                noServer.setVisibility(View.VISIBLE);
                if(e!= null && e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    ToastUtils.showEssayToast(apiException.getErrorMsg());
                }
            }

            @Override
            public void onSuccess(BaseResponseModel responseModel) {
                try {
                    showLoadingView(false);
                    super.onSuccess(responseModel);
                    courseDetailBean = (CourseDetailBean)responseModel.data;
                    if(courseDetailBean == null){
                        onError(new NullPointerException("没有请求到数据"));
                        return;
                    }
                    noServer.setVisibility(View.GONE);
                    no_network_layout.setVisibility(View.GONE);
                    course_content.setVisibility(View.VISIBLE);
                    refreshView();
                    VideoInfoMessageEvent event = new VideoInfoMessageEvent(8899);
                    event.mCourseDetailBean = courseDetailBean;
                    EventBus.getDefault().post(event);
                    LogUtils.d(TAG, "courseDetailBean was post");
                    showActiveLabelsView();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        ServiceProvider.getCourseDetailInfo(getCompositeSubscription(), courseId, mActivityId, selectCollect ? 1 : 0, netResponse);
        String webUrl = RetrofitManager.getInstance().getBaseUrl() + "c/v5/courses/" + courseId + "/getClassExt";
        if(null!=mWebview){
            mWebview.loadUrl(webUrl);
        }
    }

    @OnClick(R.id.activte_labels)
    public void showRedbagTip() {
        if (mActIntroDialog == null && courseDetailBean.activityList != null) {
            mActIntroDialog = new CustomBottomDialog(getContext(), R.style.CustomBottomDialog);
            mActIntroDialog.getWindow().setGravity(Gravity.BOTTOM);
            for (ActDetailInfo info : courseDetailBean.activityList)
                mActIntroDialog.addItem(info.title, info.introduction);
        }
        if (mActIntroDialog != null)
            mActIntroDialog.show();
    }

    private CustomConfirmDialog mDialog; //拼团活动提示

    private void ShowBuyRules() {
        if (mDialog == null) {
            mDialog = DialogUtils.createDialog(getActivity(), R.layout.layout_custom_close_dialog, "拼团玩法", "1. 发起拼团或参加好友的拼团\n" +
                    "2. 参与人数达到要求即拼团成功，前往“华图在线APP”进入“我的”查看课程\n" +
                    "3. 若在活动时间内拼团未成功，预付款将原路返回");
            mDialog.setCancelBtnVisibility(false);
            mDialog.setBtnDividerVisibility(false);
            mDialog.setOkBtnVisibility(false);
            mDialog.setContentGravity(Gravity.LEFT);
            int rightLeft = getResources().getDimensionPixelSize(R.dimen.common_20dp);
            mDialog.getContentView().setPadding(rightLeft / 2, 0, rightLeft / 2, rightLeft);
            mDialog.getContentView().findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
        }
        mDialog.show();
    }

    private void refreshView() {
        tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvOriginPrice.setVisibility(View.GONE);
        if (courseDetailBean == null) {
            return;
        }
        if (!TextUtils.isEmpty(courseDetailBean.classTitle)) {
            tvTitle.setText(courseDetailBean.classTitle);
        }
        if (!TextUtils.isEmpty(courseDetailBean.courseIntroduction)) {
            tvDes.setText(courseDetailBean.courseIntroduction);
        }
        if (TextUtils.isEmpty(courseDetailBean.actualPrice) || TextUtils.equals(courseDetailBean.actualPrice, "0") || (TextUtils.isDigitsOnly(courseDetailBean.actualPrice) && Float.parseFloat(courseDetailBean.actualPrice) == 0f)) {
            tvPrice.setText("免费");
            if (TextUtils.isEmpty(courseDetailBean.price) || TextUtils.equals(courseDetailBean.price, "0")) {
                tvOriginPrice.setVisibility(View.GONE);
            } else {
                tvOriginPrice.setVisibility(View.VISIBLE);
                tvOriginPrice.setText("¥ " + courseDetailBean.price);
            }
            if (courseType == 1) {
                tvBuyNum.setText(courseDetailBean.buyNum + "人报名");
            } else {
                tvBuyNum.setText(courseDetailBean.buyNum + "人学过");
            }
        } else {
            tvPrice.setText("¥" + courseDetailBean.actualPrice);
            tvPrice.setTextColor(getResources().getColor(R.color.red));
            if (courseDetailBean.actualPrice.equalsIgnoreCase(courseDetailBean.price)) {
                tvOriginPrice.setVisibility(View.GONE);
            } else {
                tvOriginPrice.setVisibility(View.VISIBLE);
                tvOriginPrice.setText("¥ " + courseDetailBean.price);
            }
            if (mIsGroupBuyType)
                tvBuyNum.setText(courseDetailBean.buyNum + "人已拼");
            else
                tvBuyNum.setText(courseDetailBean.buyNum + "人购买");
        }
        if (courseDetailBean.buyNum == 0) {
            tvBuyNum.setVisibility(View.GONE);
        }else
            tvBuyNum.setVisibility(View.VISIBLE);

        if (mDetailAdapter == null)
            mDetailAdapter = new CourseDetailAdapter(getActivity(),courseDetailBean);
        else
            mDetailAdapter.setCourseDetailBean(courseDetailBean);
        mLoadMoreBtn.setRotation(mDetailAdapter.isExpand() ? 180 : 0);
        mDetailInfoList.setAdapter(mDetailAdapter);
        if (courseDetailBean.columnDetails.size() > 3) {
            mDivider.setVisibility(View.VISIBLE);
            mLoadMoreBtn.setVisibility(View.VISIBLE);
            mLoadMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDetailAdapter.expandList();
                    mLoadMoreBtn.setRotation(mDetailAdapter.isExpand() ? 180 : 0);
                }
            });
        }else {
            mDivider.setVisibility(View.GONE);
            mLoadMoreBtn.setVisibility(View.GONE);
        }
        mIsGroupBuyType = courseDetailBean.activityType == 21;
        if (mIsGroupBuyType) {
            mGroupBuyNum.setVisibility(View.VISIBLE);
            mGroupBuyNum.setText(courseDetailBean.collagePeople + "人团");
            mGroupBuyRule.setVisibility(View.VISIBLE);
            if (courseDetailBean.limitType > 0) {
                String limitTip = courseDetailBean.limitType == 1 ? "仅限新学员参加" : "仅限老学员参加";
                mIsOderStudCanJoin.setText(limitTip);
            }
        } else {
            mGroupBuyNum.setVisibility(View.GONE);
            mGroupBuyRule.setVisibility(View.GONE);
        }
    }

    private void showActiveLabelsView() {
        if(courseDetailBean == null || courseDetailBean.activityList == null || courseDetailBean.activityList.isEmpty())
            return;
        mActiveLaybelsView.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bt_dingbu.getLayoutParams();
        layoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.common_dimens_100dp);
        mLabelsLayout.removeAllViews();
        for (ActDetailInfo info : courseDetailBean.activityList) {
            addLabelView(info.title, info.introduction, info.label);
        }
        if (!TextUtils.isEmpty(courseDetailBean.aloneByPrice) && !TextUtils.equals("0", courseDetailBean.aloneByPrice))
            ((ViewGroup) bt_dingbu.getParent()).setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.common_dimens_30dp));
    }

    private void addLabelView(String text, String intro, String label) {
        View view = mLayoutInflater.inflate(R.layout.labels_intro_layout, null);
        TextView labelName = (TextView) view.findViewById(R.id.label_name);
        labelName.setText(text);
        if (courseDetailBean.activityList != null && courseDetailBean.activityList.size() > 3 && mLabelsLayout.getChildCount() >= 2)
            intro = null;

        if (label != null && !TextUtils.isEmpty(label)) {
            TextView labelIntro = (TextView) view.findViewById(R.id.lable_intro);
            labelIntro.setSingleLine(true);
            labelIntro.setText(label);
            labelIntro.setBackgroundResource(R.drawable.bg_send_redbag);
            labelIntro.setVisibility(View.VISIBLE);
            labelIntro.setTextColor(Color.parseColor("#ff3f47"));
            view.findViewById(R.id.view).setVisibility(View.VISIBLE);
        } else if (intro != null && !TextUtils.isEmpty(intro)) {
            TextView labelIntro = (TextView) view.findViewById(R.id.lable_intro);
            labelIntro.setSingleLine(true);
            labelIntro.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            labelIntro.setText(Html.fromHtml(intro));
            labelIntro.setVisibility(View.VISIBLE);
            view.findViewById(R.id.view).setVisibility(View.VISIBLE);
        }
        mLabelsLayout.addView(view);
    }

    private void showLoadingView(boolean show){
        if(mActivity == null)
            return;
        if(show)
            mActivity.showProgress();
        else
            mActivity.hideProgess();
    }
}
