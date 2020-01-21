package com.huatu.handheld_huatu.mvppresenter.me;



import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.event.me.MeMsgMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.HomeConfig;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.StringUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 */

public class MeArenaContentImpl  {
    private String TAG="MeArenaContentImpl";
    public MeArenaContentImpl() {
    }

    public static HomeConfig data;
    public static void setData(HomeConfig datavar) {
        data = datavar;
    }

    public static void loadMsgData(CompositeSubscription compositeSubscription) {


        ServiceExProvider.visit(compositeSubscription, CourseApiService.getApi().getMessageUnRead(), new NetObjResponse<String>() {
            @Override
            public void onSuccess(BaseResponseModel<String> homeConfigBaseResponseModel) {

                if (homeConfigBaseResponseModel != null) {
                    int code = homeConfigBaseResponseModel.code;
                    if (code == 1000000 && homeConfigBaseResponseModel.data!=null) {
                        int unReadMsg= StringUtils.parseInt(homeConfigBaseResponseModel.data);

                        if (unReadMsg > 0) {
                            setData(new HomeConfig(unReadMsg));
                            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_HAS,new MeMsgMessageEvent());
                        } else {
                            MeArenaContentImpl.data=null;
                            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_NO,new MeMsgMessageEvent());
                        }
                    } else {
                        MeArenaContentImpl.data=null;
                        EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_NO,new MeMsgMessageEvent());
                    }
                }
            }

            @Override
            public void onError(String message, int type) {
                EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_NO,new MeMsgMessageEvent());
            }
        });

    }
}
