package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.adapter.NewShoppingAdapter;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.QQRefreshHeader;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.RefreshLayout;
import com.huatu.handheld_huatu.helper.KeyboardStatusDetector;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitStatusCallbackEx;
import com.huatu.handheld_huatu.mvpmodel.RecordCourseListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.XListView;
import com.huatu.utils.InputMethodUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

/**
 * Created by ht-ldc on 2017/9/21.
 *
 */

public class VodCourseSerachActivity extends BaseActivity implements TextWatcher,
        View.OnClickListener,XListView.IXListViewListener{
    private static final String TAG = "VodCourseSerachActivity";
    private RelativeLayout rl_right_topbar;
    private TextView tv_right_topbar;
    private EditText et_search_topbar;
    private LinearLayout ll_down_no;
    private XListView refreshListView;
    private String keyword;

    private TextView tv_xianshi;
    private RefreshLayout refreshLayout;
    private LinearLayout layout_no_network;
    private NewShoppingAdapter mAdapter;
    ArrayList<Lessons> mVodCourseList = new ArrayList<>();
    private int currentPage = 1;
    private int categoryId ;
    private String subjectId ;
    private String orderId ;
    private CompositeSubscription compositeSubscription;
    boolean isOpen=false;
    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_vodcourse_search;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        subjectId = getIntent().getStringExtra("subjectId");
        orderId = getIntent().getStringExtra("orderId");
        layout_no_network = (LinearLayout) findViewById(R.id.nonetwork);
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        rl_right_topbar = (RelativeLayout) findViewById(R.id.rl_right_topbar);
        tv_right_topbar = (TextView) findViewById(R.id.tv_right_topbar);
        et_search_topbar = (EditText) findViewById(R.id.et_search_topbar);
        et_search_topbar.setHint("搜索录播课程或老师名字");
        ll_down_no = (LinearLayout) findViewById(R.id.ll_down_no);
        tv_xianshi = (TextView) findViewById(R.id.tv_xianshi);
        et_search_topbar.addTextChangedListener(this);
        et_search_topbar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(!TextUtils.isEmpty(et_search_topbar.toString().trim())) {
                        tv_right_topbar.setText("搜索");
                    }
                    //搜索
                    showProgress();
                    loadMoreData();
                    InputMethodUtils.closeKeybord(et_search_topbar,VodCourseSerachActivity.this);
                    tv_xianshi.setText("在录播中搜索“" + keyword + "”的结果为:");
                    tv_right_topbar.setText("取消");
                    return true;
                }
                return false;
            }
        });

        InputMethodUtils.showMethodDelayed(this,et_search_topbar,1000);
        KeyboardStatusDetector detector = new KeyboardStatusDetector();
        detector.registerActivity(this);
        detector.setVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean keyboardVisible) {
                isOpen = keyboardVisible;
            }
        });
        tv_right_topbar.setOnClickListener(this);
        refreshListView = (XListView) findViewById(R.id.refreshlistview);
        refreshListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isOpen) {
                    InputMethodUtils.closeKeybord(et_search_topbar, VodCourseSerachActivity.this);
                    return true;
                }
                return false;
            }
        });
        if (!NetUtil.isConnected()) {
            refreshListView.setVisibility(View.INVISIBLE);
        }

        mAdapter = new NewShoppingAdapter(VodCourseSerachActivity.this) {
        };
        refreshListView.setPullLoadEnable(false);
        refreshListView.setPullRefreshEnable(true);
        refreshListView.setXListViewListener(this);
        refreshListView.setAdapter(mAdapter);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CommonUtils.isFastDoubleClick()) return;
                Lessons lesson = mVodCourseList.get(position - 1);
                Intent intent;
                if (NetUtil.isConnected()) {
                    if (lesson.isCollect == 1) {
                        CourseCollectSubsetFragment.show(VodCourseSerachActivity.this,
                                lesson.collectId, lesson.ShortTitle, lesson.title,0);
                    } else if (lesson.isSeckill == 1) {
                        BaseFrgContainerActivity.newInstance(VodCourseSerachActivity.this,
                                SecKillFragment.class.getName(),
                                SecKillFragment.getArgs(lesson.rid, lesson.title,false));
                    } else {
//                        intent = new Intent(VodCourseSerachActivity.this, BuyDetailsActivity.class);
                        intent = new Intent(VodCourseSerachActivity.this, BaseIntroActivity.class);
                        intent.putExtra("rid", lesson.rid);
                        intent.putExtra("collageActiveId", lesson.collageActiveId);
                        startActivityForResult(intent, 10001);
                    }
                } else {
                    CommonUtils.showToast("网络错误，请检查您的网络");
                }
            }
        });
        //监听
        setOnListener();
    }
    private void loadMoreData() {
        onRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startCountDownTask();
    }

    protected Subscription timeSubscription;
    protected void startCountDownTask() {
        if(timeSubscription != null) {
            timeSubscription.unsubscribe();
            compositeSubscription.remove(timeSubscription);
        }
        timeSubscription = Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        refreshData();
                    }
                });
        compositeSubscription.add(timeSubscription);
    }

    private void refreshData() {
        if(Method.isListEmpty(mVodCourseList)) {
            return;
        }
        LogUtils.i("refreshData");
        for (Lessons lesson : mVodCourseList) {
            if (lesson.lSaleStart > 0) {
                lesson.lSaleStart -= 1;
            }
            if (lesson.lSaleEnd > 0) {
                lesson.lSaleEnd -= 1;
            }
        }
        mAdapter.setData(mVodCourseList);
    }

    private void setOnListener() {
        layout_no_network.setOnClickListener(this);
        tv_right_topbar.setOnClickListener(this);
        et_search_topbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (ll_down_no.getVisibility() == VISIBLE) {
                            ll_down_no.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.GONE);
                        }
                        break;
                }

                return false;
            }
        });
        if (refreshLayout != null) {
            // 刷新状态的回调
            refreshLayout.setRefreshListener(new RefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 延迟3秒后刷新成功
                    refreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.refreshComplete();
                            onRefresh();
                        }
                    }, 3000);
                }
            });
        }

        QQRefreshHeader header = new QQRefreshHeader(this);
        refreshLayout.setRefreshHeader(header);
        refreshLayout.autoRefresh();
    }


    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e("onTextChanged", "onTextChanged");
        if (charSequence.length() > 0) {
            if (getResources().getString(R.string.netschool_dialog_cancel).equals(tv_right_topbar.getText())) {
                tv_right_topbar.setText(R.string.search);
            }
            keyword = et_search_topbar.getText().toString();
            Log.e("keyword", keyword);
        } else {
            tv_right_topbar.setText(R.string.netschool_dialog_cancel);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nonetwork:
                if (NetUtil.isConnected()) {
                    layout_no_network.setVisibility(View.GONE);
                    //搜索
                    loadMoreData();
                    InputMethodUtils.hideMethod(this,et_search_topbar);
                   // imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    tv_xianshi.setText("在录播中搜索“" + keyword + "”的结果为:");
                    tv_right_topbar.setText("取消");
                } else {
                    CommonUtils.showToast("网络错误，请检查您的网络");
                }

                break;
            case R.id.tv_right_topbar:
                if (NetUtil.isConnected()) {
                   // imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    InputMethodUtils.hideMethod(this,et_search_topbar);
                    if (getResources().getString(R.string.search).equals(tv_right_topbar.getText().toString())) {
                        //搜索
                        showProgress();
                        loadMoreData();
                      //  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        InputMethodUtils.hideMethod(this,et_search_topbar);
                        tv_xianshi.setText("在录播中搜索“" + keyword + "”的结果为:");
                        tv_right_topbar.setText("取消");
                    } else {
                        finish();
                    }
                } else {
                    if (getResources().getString(R.string.search).equals(tv_right_topbar.getText().toString())) {
                        tv_right_topbar.setText("取消");
                        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        InputMethodUtils.hideMethod(this,et_search_topbar);
                        tv_xianshi.setText("在录播中搜索“" + et_search_topbar.getText().toString() + "”的结果为:");
                        layout_no_network.setVisibility(VISIBLE);
                    } else {
                        finish();
                    }
                }

                break;

        }
    }

    @Override
    public void onRefresh() {
        getData(true);
    }

    @Override
    public void onLoadMore() {
        getData(false);
    }
    private void getData(final boolean isRefresh) {
        if(TextUtils.isEmpty(keyword)) {
            refreshListView.stopRefresh();
            return;
        }
        if(isRefresh) {
            currentPage = 1;
        }

        CourseApiService.getApi().getVodListCourse(currentPage, categoryId, subjectId, orderId, keyword).enqueue(
                new RetrofitStatusCallbackEx<RecordCourseListResponse>(this) {
                    @Override
                    protected void onSuccess(Response<RecordCourseListResponse> response) {

                        refreshListView.stopLoadMore();
                        refreshListView.stopRefresh();
                        List<Lessons> tmpList = response.body().getListResponse();

                        if (tmpList != null) {
                            for (int i = 0; i < tmpList.size(); i++) {
                                Lessons lessons = tmpList.get(i);
                                lessons.lSaleStart = Method.parseInt(lessons.saleStart);
                                lessons.lSaleEnd = Method.parseInt(lessons.saleEnd);
                                lessons.CourseLength = lessons.CourseLength + "课时";
                             }
                        }
                        if (Method.isListEmpty(tmpList)) {
                            refreshLayout.setVisibility(VISIBLE);
                            ll_down_no.setVisibility(VISIBLE);
                            refreshListView.setVisibility(View.GONE);
                        } else {
                            refreshLayout.setVisibility(View.GONE);
                            ll_down_no.setVisibility(View.GONE);
                            refreshListView.setVisibility(VISIBLE);
                        }
                        if (isRefresh) {
                            mVodCourseList.clear();
                            mVodCourseList.addAll(tmpList);
                            refreshListView.setSelection(0);

                        } else {
                            mVodCourseList.addAll(tmpList);
                        }
                        mAdapter.setData(mVodCourseList);
                        if (response.body().data.next == 1) {
                            currentPage++;
                            refreshListView.setPullLoadEnable(true);
                        } else {
                            refreshListView.setPullLoadEnable(false);
                        }
                        hideProgress();
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    protected void onFailure(String error, int type) {
                        hideProgress();
                        refreshListView.stopLoadMore();
                        refreshListView.stopRefresh();
                        refreshLayout.setVisibility(VISIBLE);
                        refreshListView.setVisibility(View.GONE);
                    }
                });

    }
    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    /**
     * @param context
     */
    public static void newIntent(Context context,int categoryId,String subjectId,String orderId) {
        Intent intent = new Intent(context, VodCourseSerachActivity.class);
        intent.putExtra("categoryId",categoryId);
        intent.putExtra("subjectId",subjectId);
        intent.putExtra("orderId",orderId);
        context.startActivity(intent);
    }
}
