package com.huatu.handheld_huatu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.adapter.SearchListAdapter;
import com.huatu.handheld_huatu.adapter.onRecyclerViewItemClickListener;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.essay.adapter.EssaySearchAdapter;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchData;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchResult;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.ExerciseBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.LoadMoreRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

//import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by ljzyuhenda on 16/7/22.
 * 行测，申论搜索页
 */
public class SearchActivity extends Activity implements TextWatcher, View.OnClickListener, LoadMoreRecyclerView.onLoadMoreListener, onRecyclerViewItemClickListener {

    private static final String TAG = "SearchActivity";

    @BindView(R.id.rl_right_topbar)
    RelativeLayout rl_right_topbar;
    @BindView(R.id.tv_right_topbar)
    TextView tv_right_topbar;
    @BindView(R.id.et_search_topbar)
    EditText et_search_topbar;
    @BindView(R.id.rcv_search)
    LoadMoreRecyclerView rcv_search;
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;

    private SearchListAdapter mSearchListAdapter;                           // 行测搜索adapter
    private int mCurrentPage = 1;
    private boolean isFromEssay;
    private CustomConfirmDialog dialog;
    private CompositeSubscription mCompositeSubscription;
    private EssaySearchAdapter mEssaySearchAdapter;                         // 申论搜索adapter
    private ArrayList<EssaySearchData> mData = new ArrayList<>();
    private ArrayList<EssaySearchResult> mEssayData = new ArrayList<>();
    private String mSingleTitle;
//    private EssayCheckImpl mEssayCheckImpl;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
//        if (event.type == EssayExamMessageEvent.EssayExam_net_getCheckCountList) {
//            if (isFromEssay && mEssayCheckImpl != null) {
//                mEssayCheckImpl.checkCountVerify(2);
//                mEssayCheckImpl.checkCountVerify(0);
//                mEssayCheckImpl.checkCountVerify(1);
//            }
//        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
//            if (isFromEssay && mEssayCheckImpl != null) {
//                mEssayCheckImpl.checkCountVerify(2);
//                mEssayCheckImpl.checkCountVerify(0);
//                mEssayCheckImpl.checkCountVerify(1);
//            }
//        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onCreate()");
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        tv_right_topbar.setText(R.string.netschool_dialog_cancel);
        initDatas();
        setListener();
    }

    private void setListener() {
        layout_net_unconnected.setOnClickListener(this);
        et_search_topbar.addTextChangedListener(this);
        et_search_topbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = et_search_topbar.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {
                        ToastUtils.showShort("请输入搜索内容");
                        return true;
                    }
                    if (isFromEssay) {
                        mEssaySearchAdapter.setKeyWords(et_search_topbar.getText().toString());
                    } else {
                        mSearchListAdapter.setKeyWords(et_search_topbar.getText().toString());
                    }
                    loadMoreData(true);
                    tv_right_topbar.setText(R.string.netschool_dialog_cancel);
                    //  这里记得一定要将键盘隐藏了
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_search_topbar.getWindowToken(), 0);
                }
                return false;
            }
        });

        if (!isFromEssay) {
            mSearchListAdapter.setOnRecyclerViewItemClickListener(this);
        } else {
            mEssaySearchAdapter.setOnRecyclerViewItemClickListener(this);
        }
        et_search_topbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (layout_nodata.getVisibility() == View.VISIBLE) {
                            layout_nodata.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initDatas() {
        Intent bIntent = getIntent();
        isFromEssay = bIntent.getBooleanExtra("isFromEssay", false);
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_search.setLayoutManager(linearLayoutManager);

        mSearchListAdapter = new SearchListAdapter(this);
        mEssaySearchAdapter = new EssaySearchAdapter(this, mCompositeSubscription);
        if (isFromEssay) {
//            mEssayCheckImpl = new EssayCheckImpl(mCompositeSubscription);
//            if (mEssayCheckImpl != null) {
//                mEssayCheckImpl.checkCountVerify(0);
//                mEssayCheckImpl.checkCountVerify(1);
//                mEssayCheckImpl.checkCountVerify(2);
//            }
            et_search_topbar.setHint("请输入关键词，例如“旅游”");
            rcv_search.setAdapter(mEssaySearchAdapter);
        } else {
            rcv_search.setAdapter(mSearchListAdapter);
        }
        rcv_search.setAutoLoadMoreEnable(false);
        rcv_search.setOnLoadMoreListener(this);

        RxUtils.clicks(rl_right_topbar).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Void aVoid) {
                InputMethodManager inputManager = (InputMethodManager) tv_right_topbar.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(tv_right_topbar.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (getResources().getString(R.string.search).equals(tv_right_topbar.getText().toString())) {
                    if (isFromEssay) {
                        mEssaySearchAdapter.setKeyWords(et_search_topbar.getText().toString());
                    } else {
                        mSearchListAdapter.setKeyWords(et_search_topbar.getText().toString());
                    }
                    //搜索
                    loadMoreData(true);
                    tv_right_topbar.setText(R.string.netschool_dialog_cancel);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            if (getResources().getString(R.string.netschool_dialog_cancel).equals(tv_right_topbar.getText())) {
                tv_right_topbar.setText(R.string.search);
                tv_right_topbar.setTextColor(getResources().getColor(R.color.indicator_color));
            } else {
                tv_right_topbar.setTextColor(getResources().getColor(R.color.gray010));
            }
        } else {
            tv_right_topbar.setText(R.string.netschool_dialog_cancel);
            if (isFromEssay) {
                mEssaySearchAdapter.getDataList().clear();
            } else {
                mSearchListAdapter.getDataList().clear();
            }
            rcv_search.notifyDataSetChanged();
            rcv_search.setAutoLoadMoreEnable(false);
            mCurrentPage = 1;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                loadMoreData(true);
                break;
        }
    }

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    private void loadMoreData(boolean isReset) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络未连接，请联网后点击屏幕重试");
            layout_net_unconnected.setVisibility(View.VISIBLE);
            rcv_search.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
            return;
        } else {
            layout_net_unconnected.setVisibility(View.GONE);
            rcv_search.setVisibility(View.VISIBLE);
        }
        if (isReset) {
            if (isFromEssay) {
                mEssaySearchAdapter.getDataList().clear();
            } else {
                mSearchListAdapter.getDataList().clear();
            }
            rcv_search.notifyDataSetChanged();

            rcv_search.setAutoLoadMoreEnable(true);
            mCurrentPage = 1;
        }
        if (isFromEssay) {
//            rcv_search.setAutoLoadMoreEnable(false);
            loadEssaySearch(mCurrentPage, isReset);
        } else {
            loadDatas(mCurrentPage, isReset);
        }
    }

    private void loadEssaySearch(int page, final boolean isReset) {
        ServiceProvider.getEssaySearch(mCompositeSubscription, et_search_topbar.getText().toString(), -1, page, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                if (model != null && model.data != null) {
                    mData.clear();
                    mData.addAll(model.data);
                }
                if (mData == null || mData.size() == 0) {
                    if (isReset) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        CommonUtils.showToast(R.string.noMoreData);
                    }
                } else {
//                    mCurrentPage++;
                }
                mEssaySearchAdapter.setData(mData);
                rcv_search.notifyMoreFinished(true);
            }
            // @Override
//            public void onSuccess(BaseResponseModel model) {
//                super.onSuccess(model);
//                if (model != null) {
//                    mData = (EssaySearchData) model.data;
//                }
//                if (mData != null) {
//                    mEssayData = mData.result;
////                }
//                if (mData == null || mEssayData == null || mEssayData.size() == 0) {
//                    if (isReset) {
//                        layout_nodata.setVisibility(View.VISIBLE);
//                    } else {
//                        CommonUtils.showToast(R.string.noMoreData);
//                    }
//                } else {
//                    mCurrentPage++;
//                    mEssaySearchAdapter.getDataList().addAll(mEssayData);
//                }
//
//                rcv_search.notifyMoreFinished(true);
//            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                CommonUtils.showToast(R.string.networkerror);
                rcv_search.notifyMoreFinished(true);
            }
        });

    }

    private void loadDatas(int page, final boolean isReset) {
        String searchContent = et_search_topbar.getText().toString().trim();
        if (searchContent.isEmpty()) {
            rcv_search.notifyMoreFinished(true);
            CommonUtils.showToast("搜索内容不能为空！");
            return;
        }
        Observable<ExerciseBean> searchedExersicesObservable = RetrofitManager.getInstance().getService()
                .getSearchedExersices(searchContent, page, 20, 1);
        Subscription subscription = searchedExersicesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExerciseBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        CommonUtils.showToast(R.string.networkerror);
                        rcv_search.notifyMoreFinished(true);
                    }

                    @Override
                    public void onNext(ExerciseBean exerciseData) {
                        if ("1110002".equals(exerciseData.code)) {
                            CommonUtils.showToast(R.string.sessionOutOfDateInfo);
                        } else if ("1000000".equals(exerciseData.code)) {
                            if (exerciseData == null || exerciseData.data.content == null || exerciseData.data.content.size() == 0) {
                                if (isReset) {
                                    layout_nodata.setVisibility(View.VISIBLE);
                                } else {
                                    CommonUtils.showToast(R.string.noMoreData);
                                }
                            } else {
                                mCurrentPage++;
                                mSearchListAdapter.getDataList().addAll(exerciseData.data.content);
                            }
                        } else {
                            CommonUtils.showToast(exerciseData.message);
                        }

                        rcv_search.notifyMoreFinished(true);
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    public void onLoadMore() {
        loadMoreData(false);
    }

    @Override
    public void onItemClick(View view, int position, int resId) {
        if (isFromEssay) {
//            if (!NetUtil.isConnected()) {
//                ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
//                return;
//            }
//            final EssaySearchResult mResult = mEssaySearchAdapter.getItem(position);
//            if (mResult.showMsg != null) {
//                String mTitle = mResult.showMsg.replaceAll("<font color='#e9304e'>", "");
//                mSingleTitle = mTitle.replaceAll("</font>", "");
//            }
//            if (mResult.essayQuestionBelongPaperVOList.isEmpty()) {
//                ToastUtils.showEssayToast("暂无试题，请稍后重试");
//                return;
//            }
//            if (SpUtils.getEssayCorrectFree() == 1) {
//                Bundle m = new Bundle();
//                m.putString("titleView", mSingleTitle);
//                m.putBoolean("isSingle", true);
//                if (!mResult.essayQuestionBelongPaperVOList.isEmpty()) {
//                    m.putLong("questionBaseId", mResult.essayQuestionBelongPaperVOList.get(0).id);
//                }
//                m.putLong("similarId", mResult.similarId);
//                m.putBoolean("isStartToCheckDetail", false);
//                EssayExamActivity.show(this, EssayExamActivity.show_EssayExamMaterials, m);
//            } else if (SpUtils.getEssayCorrectFree() == 0) {
//                if (EssayCheckDataCache.getInstance().existSingle == 0) {
//                    Bundle m = new Bundle();
//                    m.putString("titleView", mSingleTitle);
//                    m.putBoolean("isSingle", true);
//                    if (!mResult.essayQuestionBelongPaperVOList.isEmpty()) {
//                        m.putLong("questionBaseId", mResult.essayQuestionBelongPaperVOList.get(0).id);
//                    }
//                    m.putLong("similarId", mResult.similarId);
//                    m.putBoolean("isStartToCheckDetail", false);
//                    EssayExamActivity.show(this, EssayExamActivity.show_EssayExamMaterials, m);
//
//                } else if (EssayCheckDataCache.getInstance().existSingle == 1) {
//                    int max = EssayCheckDataCache.getInstance().maxCorrectTimes;
//                    if (max <= 0) {
//                        max = 0;
//                    }
//                    String title = "";
//                    String tip = "";
//                    int size = 14;
//                    if (max != 9999) {
//                        title = "批改次数不足，先练习再购买";
//                        tip = "（同一单题或套题仅可批改" + max + "次）";
//                        size = 14;
//                    } else {
//                        title = "";
//                        tip = "批改次数不足，先练习再购买";
//                        size = 16;
//                    }
//                    dialog = DialogUtils.createEssayDialog(this, title, tip, size,
//                            getResources().getColor(R.color.gray_666666));
//                    dialog.setPositiveButton("先练习", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Bundle m = new Bundle();
//                            m.putString("titleView", mSingleTitle);
//                            m.putBoolean("isSingle", true);
//                            if (!mResult.essayQuestionBelongPaperVOList.isEmpty()) {
//                                m.putLong("questionBaseId", mResult.essayQuestionBelongPaperVOList.get(0)
//                                        .id);
//                            }
////                                    m.putLong("questionDetailId",2);
//                            m.putLong("similarId", mResult.similarId);
//                            m.putBoolean("isStartToCheckDetail", false);
//                            EssayExamActivity.show(SearchActivity.this, EssayExamActivity.show_EssayExamMaterials, m);
//                            dialog.dismiss();
//
//                        }
//                    });
//                    dialog.setNegativeButton("去购买", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            BaseFrgContainerActivity.newInstance(SearchActivity.this,
//                                    CheckOrderFragment.class.getName(),
//                                    null);
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//                }
//            }
        } else {
            if(!CommonUtils.checkLogin(this)){  return;   }
            ExerciseBean.ExerciseInfoBean infoBean = mSearchListAdapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("exerciseIdList", "" + infoBean.id);
            bundle.putBoolean("fromSearch", true);
            ArenaExamActivityNew.show(SearchActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE, bundle);
            StudyCourseStatistic.clickTiKuSearchResult(infoBean.id + "", et_search_topbar.getText().toString(), mCurrentPage, position + "");
        }
    }
}
