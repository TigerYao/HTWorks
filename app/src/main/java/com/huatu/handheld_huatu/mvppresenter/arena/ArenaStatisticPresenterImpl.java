package com.huatu.handheld_huatu.mvppresenter.arena;

import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.mvpview.arena.ArenaStatisticView;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class ArenaStatisticPresenterImpl {
    private ArenaStatisticView mView;
    private CompositeSubscription compositeSubscription;

    public ArenaStatisticPresenterImpl(CompositeSubscription cs, ArenaStatisticView view) {
        mView = view;
        compositeSubscription = cs;
        if(compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
    }

    public void getArenaDetailInfo(long arenaId) {
        if(arenaId <= 0) {
            LogUtils.e("getArenaDetailInfo direct return, arenaId is 0");
            return;
        }
        mView.showProgressBar();
        ServiceProvider.getArenaDetailInfo(compositeSubscription, arenaId, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.onLoadDataFailed();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mView.dismissProgressBar();
                ArenaDetailBean arenaDetailBean = (ArenaDetailBean)model.data;
                if(arenaDetailBean.status == ArenaConstant.ARENA_ROOM_STATE_FINISH) {
                    if(arenaDetailBean.results == null) {
                        arenaDetailBean.results = new ArrayList<>();
                    }
                    for (int i = 0; i < arenaDetailBean.results.size(); i++) {
                        arenaDetailBean.results.get(i).userInfo = new ArenaDetailBean.Player(arenaDetailBean.results.get(i).uid);
                    }
                    if(arenaDetailBean.players != null && arenaDetailBean.players.size() >= arenaDetailBean.results.size()) {
                        for (int i = 0; i < arenaDetailBean.results.size(); i++) {
                            for(int j = 0; j < arenaDetailBean.players.size(); j++) {
                                if(arenaDetailBean.results.get(i).uid == arenaDetailBean.players.get(j).uid) {
                                    arenaDetailBean.results.get(i).userInfo = arenaDetailBean.players.get(j);
                                }
                            }
                        }
                    }
                }
                mView.onSetData(arenaDetailBean);
            }
        });
    }

    public void getArenaStatisticShareInfo(long arenaId) {
        if(arenaId <= 0) {
            LogUtils.e("getArenaStatisticShareInfo direct return, arenaId is 0");
            return;
        }
      /*  mView.showProgressBar();
        ServiceProvider.getArenaStatisticShareInfo(compositeSubscription, arenaId, new NetResponse(){
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                mView.getShareInfoFail();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mView.dismissProgressBar();
                mView.getShareInfoSucc(model.data);
            }
        });*/
    }
}
