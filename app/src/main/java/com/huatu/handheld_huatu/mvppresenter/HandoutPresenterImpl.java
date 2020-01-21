package com.huatu.handheld_huatu.mvppresenter;

import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.mvpview.HandoutView;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.CommonUtils;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht on 2016/12/23.
 */
public class HandoutPresenterImpl {

    private HandoutView baseView;
    private CompositeSubscription compositeSubscription;

    public HandoutPresenterImpl(HandoutView baseView, CompositeSubscription compositeSubscription) {
        this.baseView = baseView;
        this.compositeSubscription = compositeSubscription;
    }

    public void getHandoutInfo(int courseId) {

        baseView.showProgressBar();

        Subscription subscribe = DataController.getInstance().getHandoutData(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HandoutBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        baseView.dismissProgressBar();
                        CommonUtils.showToast("获取讲义信息失败");
                        baseView.onLoadDataFailed(1);
                    }

                    @Override
                    public void onNext(HandoutBean handoutBean) {
                        baseView.dismissProgressBar();
                        long code = handoutBean.code;
                        if (code == 1000000) {
                            HandoutBean.Data data = handoutBean.data;
                            if (data == null) {
                                CommonUtils.showToast("未查询到数据");
                                baseView.onLoadDataFailed(0);
                                return;
                            }
                            List<HandoutBean.Course> course_jiangyi = data.course_jiangyi;
                            if (course_jiangyi == null || course_jiangyi.size() < 0) {
                                CommonUtils.showToast("未查询到数据");
                                baseView.onLoadDataFailed(0);
                                return;
                            }
                            baseView.onSetData(course_jiangyi);
                        } else if (code == 0) {
                            CommonUtils.showToast("未查询到数据");
                            baseView.onLoadDataFailed(1);
                        } else if (code == -1) {
                            CommonUtils.showToast("参数错误");
                            baseView.onLoadDataFailed(1);
                        } else {
                            CommonUtils.showToast("获取讲义信息失败");
                            baseView.onLoadDataFailed(1);
                        }
                    }
                });

        compositeSubscription.add(subscribe);
    }
}
