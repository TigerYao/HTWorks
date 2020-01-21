package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.lessons.adapter.NewShoppingAdapter;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.bean.Courses;
import com.huatu.handheld_huatu.business.lessons.bean.Lessons;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.XListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by saiyuan on 2017/10/9.
 */

public abstract class BaseCourseListFragment extends BaseFragment
        implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    protected NewShoppingAdapter mNewShoppingAdapter;
    protected CommonErrorView layoutErrorView;
    protected RelativeLayout layoutEmptyError;
    protected XListView mListView;


    public ArrayList<CourseCategoryBean> selCategoryList = new ArrayList<>();
    protected String keyword = "";
    protected int mOrderId = 0;
    protected int mPriceId = 1000;
    protected int mPage = 1;

    protected int mCourseType = 0;
    protected ArrayList<Lessons> dataList = new ArrayList<>();
    protected Subscription timeSubscription;
    protected NetResponse courseListResponse;

    @Override
    protected void onInitView() {
        super.onInitView();
        mListView = (XListView) rootView.findViewById(R.id.fragment_shopping_lv);
        mNewShoppingAdapter = new NewShoppingAdapter(mActivity);
        layoutErrorView = new CommonErrorView(UniApplicationContext.getContext());
        layoutErrorView.setErrorText("暂无直播课程，敬请期待！");
        layoutEmptyError = new RelativeLayout(UniApplicationContext.getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutEmptyError.addView(layoutErrorView, lp);
        mListView.setHeaderDividersEnabled(true);
        mListView.setFooterViewVisible(false);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setAdapter(mNewShoppingAdapter);
        mListView.setOnItemClickListener(this);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    protected void onSaveState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("list_data", dataList);
        super.onSaveState(savedInstanceState);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Lessons> tmpList = (ArrayList<Lessons>) savedInstanceState.getSerializable("list_data");
            if (tmpList != null) {
                dataList.clear();
                dataList.addAll(tmpList);
            }
            mNewShoppingAdapter.setData(dataList);
        }
    }

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

    private long stopTime = 0;
    @Override
    public void onStart() {
        super.onStart();
        if(stopTime > 0 && !Method.isListEmpty(dataList)) {
            long divTime = (SystemClock.elapsedRealtime() - stopTime) / 1000;
            LogUtils.i("divTime: " + divTime);
            for (Lessons lesson : dataList) {
                if (lesson.lSaleStart > 0) {
                    lesson.lSaleStart -= divTime;
                }
                if (lesson.lSaleEnd > 0) {
                    lesson.lSaleEnd -= divTime;
                }
            }
            mNewShoppingAdapter.setData(dataList);
        }
        startCountDownTask();
    }

    private int lastPosition = 0;
    @Override
    public void onStop() {
        super.onStop();
        stopTime = SystemClock.elapsedRealtime();
        if(compositeSubscription != null) {
            if(timeSubscription != null) {
                timeSubscription.unsubscribe();
                compositeSubscription.remove(timeSubscription);
            }
        }
    }

    private void refreshData() {
        if(Method.isListEmpty(dataList)) {
            return;
        }
        for (Lessons lesson : dataList) {
            if (lesson.lSaleStart > 0) {
                lesson.lSaleStart -= 1;
            }
            if (lesson.lSaleEnd > 0) {
                lesson.lSaleEnd -= 1;
            }
        }
        mNewShoppingAdapter.setData(dataList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventUpdate(MessageEvent event) {
        if(event == null || compositeSubscription == null) {
            return;
        }
        if(//event.message == MessageEvent.HT_FRAGMENT_MSG_TYPE_TRI_SUBJECT_UPDATE_VIEW ||
                event.message == MessageEvent.COURSE_BUY_SUCCESS) {
            LogUtils.i("MessageEvent.HT_FRAGMENT_MSG_TYPE_TRI_SUBJECT_UPDATE_VIEW");
            onRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (CommonUtils.isFastDoubleClick()) return;
        Lessons lesson = dataList.get(position - 1);
        Intent intent;
        if (NetUtil.isConnected()) {
            if (lesson.isCollect == 1) {
                CourseCollectSubsetFragment.show(mActivity,
                        lesson.collectId, lesson.ShortTitle, lesson.title,mCourseType);
            } else if (lesson.isSeckill == 1) {
                BaseFrgContainerActivity.newInstance(mActivity,
                        SecKillFragment.class.getName(),
                        SecKillFragment.getArgs(lesson.rid, lesson.title,false));
            } else {
//                intent = new Intent(mActivity, BuyDetailsActivity.class);
                intent = new Intent(mActivity, BaseIntroActivity.class);
                intent.putExtra("rid", lesson.rid);
                intent.putExtra("NetClassId", lesson.NetClassId);
                intent.putExtra("course_type",mCourseType);
                intent.putExtra("price",lesson.ActualPrice); //价格为空直接学习
                intent.putExtra("originalprice",lesson.Price);
                intent.putExtra("saleout",lesson.isSaleOut);
                intent.putExtra("rushout",lesson.isRushOut);
                intent.putExtra("daishou",lesson.isTermined);
                intent.putExtra("collageActiveId", lesson.collageActiveId);
                startActivityForResult(intent, 10001);
            }
        } else {
            CommonUtils.showToast("网络错误，请检查您的网络");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            onRefresh();
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

    @Override
    protected void onLoadData() {
        startCountDownTask();
        String value = SpUtils.getLiveCategoryList();
        if(!TextUtils.isEmpty(value)) {
            Gson gson = new Gson();
            try {
                selCategoryList = gson.fromJson(value,
                        new TypeToken<ArrayList<CourseCategoryBean>>(){}.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ensureSelectedItem();
    }

    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            mPage = 1;
        }
        mActivity.showProgress();
        if(Method.isListEmpty(selCategoryList)) {
            if(Method.isListEmpty(SignUpTypeDataCache.getInstance().getCourseCategoryList())) {
                ServiceProvider.getCourseCategoryList(compositeSubscription, new NetResponse(){
                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();
                        if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                            return;
                        }
                        mListView.stopLoadMore();
                        mListView.stopRefresh();
                        if(isRefresh) {
                            dataList.clear();
                        }
                        if (dataList.isEmpty()) {
                            if(e instanceof ApiException
                                    && ((ApiException) e).getErrorCode() == ApiErrorCode.ERROR_INVALID_DATA) {
                                showEmptyView();
                                layoutErrorView.setErrorText("");
                            } else {
                                showErrorView();
                            }
                        } else {
                            hideEmptyView();
                        }
                        mActivity.hideProgress();
                    }

                    @Override
                    public void onListSuccess(BaseListResponseModel model) {
                        mActivity.hideProgress();
                        SignUpTypeDataCache.getInstance().setCourseCategoryList(model.data);
                        onSetCategoryDataList();
                        getCourseData(isRefresh);
                    }
                });
            } else {
                onSetCategoryDataList();
                getCourseData(isRefresh);
            }
        } else if(SpUtils.getSelectedLiveCategory() < 0) {
            ensureSelectedItem();
            getCourseData(isRefresh);
        } else {
            getCourseData(isRefresh);
        }
    }

    protected void onSetCategoryDataList(){
        selCategoryList.clear();
        List<CourseCategoryBean> tmpList = SignUpTypeDataCache
                .getInstance().getCourseCategoryList();
        if(!Method.isListEmpty(tmpList)) {
            for(int i = 0; i < tmpList.size(); i++) {
                if(tmpList.get(i).cateId == SpUtils.getSelectedLiveCategory()) {
                    tmpList.get(i).isSelected = true;
                } else {
                    tmpList.get(i).isSelected = false;
                }
                if(tmpList.get(i).checked) {
                    selCategoryList.add(tmpList.get(i));
                }
            }
        }
        ensureSelectedItem();
        if(!Method.isListEmpty(selCategoryList)) {
            Gson gson = new Gson();
            String value = "";
            try {
                value = gson.toJson(selCategoryList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(value)) {
                SpUtils.setLiveCategoryList(value);
            }
        }
    }

    protected void ensureSelectedItem() {
        boolean isSelected = false;
        for(int i = 0; i < selCategoryList.size(); i++) {
            if (selCategoryList.get(i).isSelected) {
                isSelected = true;
                break;
            }
        }
        if(!isSelected && selCategoryList.size() > 0) {
            onSelectCategory(0);
        }
    }

    protected void resetSelectItems() {
        for(int i = 0; i < selCategoryList.size(); i++) {
            selCategoryList.get(i).isSelected = false;
        }
    }

    protected void onSelectCategory(final int position) {
        if(Method.isListEmpty(selCategoryList) || position >= selCategoryList.size()) {
            return;
        }
        if(!selCategoryList.get(position).isSelected) {
            resetSelectItems();
            selCategoryList.get(position).isSelected = true;
        }
        for(int i = 0; i < selCategoryList.size(); i++) {
            if(selCategoryList.get(i).isSelected) {
                SpUtils.setSelectedLiveCategory(selCategoryList.get(i).cateId);
                break;
            }
        }
        if(!Method.isListEmpty(selCategoryList)) {
            Gson gson = new Gson();
            String value = "";
            try {
                value = gson.toJson(selCategoryList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(value)) {
                SpUtils.setLiveCategoryList(value);
            }
        }
    }

    protected void getCourseData(final boolean isRefresh) {
        if(SpUtils.getSelectedLiveCategory() <= 0) {
            LogUtils.i("You must select a category");
            return;
        }
        mActivity.showProgress();
        ensureResponse(isRefresh);
        ServiceProvider.getCourseList(compositeSubscription,
                String.valueOf(SpUtils.getSelectedLiveCategory()), keyword,
                mOrderId, mPriceId, mPage, courseListResponse);
    }

    protected void ensureResponse(final boolean isRefresh) {
        courseListResponse = new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                Courses courses = (Courses) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                mListView.stopLoadMore();
                mListView.stopRefresh();
                if (courses.result != null) {
                    for (int i = 0; i < courses.result.size(); i++) {
                        courses.result.get(i).lSaleStart = Method.parseInt(courses.result.get(i).saleStart);
                        courses.result.get(i).lSaleEnd = Method.parseInt(courses.result.get(i).saleEnd);
                    }
                }
                if (isRefresh) {
                    dataList.clear();
                }
                if(courses.result != null) {
                    dataList.addAll(courses.result);
                }
                if (dataList.isEmpty()) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
                mNewShoppingAdapter.setData(dataList);
                if (courses.next == 1) {
                    mPage++;
                    mListView.setPullLoadEnable(true);
                } else {
                    mListView.setPullLoadEnable(false);
                }
                mActivity.hideProgress();
                if(isRefresh) {
                    UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setSelection(lastPosition);
                        }
                    },150);
                }
            }

            @Override
            public void onError(final Throwable e) {
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                mListView.stopLoadMore();
                mListView.stopRefresh();
                if(isRefresh) {
                    dataList.clear();
                }
                if (dataList.isEmpty()) {
                    if(e instanceof ApiException
                            && ((ApiException) e).getErrorCode() == ApiErrorCode.ERROR_INVALID_DATA) {
                        showEmptyView();
                        layoutErrorView.setErrorText("");
                    } else {
                        showErrorView();
                    }
                } else {
                    hideEmptyView();
                }
                mNewShoppingAdapter.setData(dataList);
                mActivity.hideProgress();
            }
        };
    }

    public void showErrorView() {
        layoutErrorView.updateUI();
        layoutErrorView.setVisibility(View.VISIBLE);
        layoutErrorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadData();
            }
        });
        setEmptyViewLp();
        mListView.addHeaderView(layoutEmptyError);
        mListView.setPullRefreshEnable(false);
    }

    public void showEmptyView() {
        layoutErrorView.setVisibility(View.VISIBLE);
        layoutErrorView.setOnReloadButtonListener(null);
        layoutErrorView.setErrorImageVisible(false);
        setEmptyViewLp();
        mListView.addHeaderView(layoutEmptyError);
        mListView.setPullRefreshEnable(true);
        layoutErrorView.setErrorText("暂无直播课程，敬请期待！");
    }

    public void hideEmptyView() {
        mListView.removeHeaderView(layoutEmptyError);
        mListView.setPullRefreshEnable(true);
    }

    public void setEmptyViewLp() {
        int listHeight = mListView.getHeight();
        int headerHeight = 0;
        for (int i = 0; i < mListView.getHeaderViewsCount(); i++) {
            View view = mListView.getChildAt(i);
            if (view != null && (view.getId() != layoutEmptyError.getId())) {
                headerHeight += view.getHeight();
            }
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                layoutErrorView.getLayoutParams();
        lp.height = listHeight - headerHeight;
        layoutErrorView.setLayoutParams(lp);
    }

}
