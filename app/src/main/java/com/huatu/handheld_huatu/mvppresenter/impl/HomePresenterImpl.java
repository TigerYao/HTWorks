package com.huatu.handheld_huatu.mvppresenter.impl;

import android.graphics.Point;

import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.mvpmodel.HomeAdvBean;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;
import com.huatu.handheld_huatu.mvpview.HomeView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

/**
 * desc:HomePresenterImpl
 *
 * @author zhaodongdong
 * QQ: 676362303
 * email: androidmdeveloper@163.com
 */
public class HomePresenterImpl {
    private static final String TAG = "HomePresenterImpl";
    private HomeView mView;
    private CompositeSubscription compositeSubscription;

    public HomePresenterImpl(CompositeSubscription cs, HomeView homeFragment) {
        compositeSubscription = cs;
        mView = homeFragment;
    }

    // 获取轮播图广告
    public void getHomeAdvertise() {
        // ServiceProvider.getHomeAdvertise(compositeSubscription, SpUtils.getUserCatgory(), SpUtils.getIsWhite(), new NetResponse() {
        ServiceProvider.getHomeAdvertise(compositeSubscription, SignUpTypeDataCache.getInstance().getCurCategory(), SpUtils.getIsWhite(), new NetResponse() {

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mView.updateAdvertise(model);
            }
        });
    }

    // 获取行测首页icon
    public void getHomeIconsView() {
        int subject = SpUtils.getUserSubject();
        if (subject == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
            subject = Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST;
        }
        ServiceProvider.getHomeIcons(compositeSubscription, subject, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                if (model != null && model.data != null && model.data.size() > 0) {
                    ArrayList<HomeIconBean> icons = (ArrayList<HomeIconBean>) model.data;
                    for (HomeIconBean icon : icons) {
                        Point p = SignUpTypeDataCache.getInstance().getRequestType(icon.type);
                        if (p != null) {
                            icon.requestType = p.x;
                            icon.icon = p.y;
                        }
                    }
                    mView.updateHomeIcons(icons);
                }
            }
        });
    }

    // 获取全部知识树
    public void getHomeTreeData() {
        mView.showProgressBar();
        ServiceProvider.getHomeTreeData(compositeSubscription, SpUtils.getHomeQuestionMode() + 1, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.getTreePointFail();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mView.dismissProgressBar();
                mView.updateTreePoint(model);
            }
        });
    }

    // 获取当前模考大赛、小模考的id，用于显示红色角标数量
    public void getMatchIdForNewTip() {
        ServiceProvider.getMatchIdForNewTip(compositeSubscription, new NetResponse() {
            @Override
            public void onError(final Throwable e) {

            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mView.updateNewTips(model);
            }
        });
    }

    // 更具parentId获取知识树
    public void getHomeTreeDataById(final int parentId) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }
        mView.showProgressBar();
        ServiceProvider.getHomeTreeDataById(compositeSubscription, parentId, SpUtils.getHomeQuestionMode() + 1, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.getTreePointFail();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mView.dismissProgressBar();
                mView.updateTreePointById(parentId, model);
            }
        });
    }

    public void getHomeReport() {
        mView.showProgressBar();
        ServiceProvider.getHomeReport(compositeSubscription, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mView.dismissProgressBar();
            }
        });
    }

    public void getRealExamVersion() {
        ServiceProvider.getRealExamAreaVersion(compositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mView.refreshRealExamVersion(model);
            }
        });
    }

    public void getHomeConfig() {
        ServiceProvider.getHomeConfig(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
            }
        });
    }

    public void getDailyInfo() {
        mView.showProgressBar();
        ServiceProvider.getDailyList(compositeSubscription, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.onLoadDataFailed();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mView.dismissProgressBar();
                mView.dispatchDaily((DailySpecialBean) model.data);
            }
        });
    }

    // 首页公告这个功能被砍掉
    public void getHomeAdvList() {
        mView.showProgressBar();
        ServiceProvider.getHomeAdvList(compositeSubscription, SignUpTypeDataCache.getInstance().getCurCategory(), new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.onLoadDataFailed();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mView.dismissProgressBar();
                mView.updateHomeAdvList((HomeAdvBean) model.data);
            }
        });
    }
}
