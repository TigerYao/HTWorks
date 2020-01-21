package com.netease.hearttouch.router;

import android.os.Bundle;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.essay.mainfragment.MultExamEssay;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.netease.hearttouch.router.intercept.HTInterceptAnno;
import com.netease.hearttouch.router.intercept.IRouterInterceptor;

/**
 *
 *
 * ztk://small/estimate 小模考
 *
 * ztk://essay/argument 申论文章写作
 *
 * ztk://essay/paper    申论套题
 *
 *
 *
 */
//@HTInterceptAnno(url = {"ztk://small/estimate", "ztk://essay/argument", "ztk://essay/paper", "ztk://match/essay"})
public class UserCatgoryInterceptor implements IRouterInterceptor {

    @Override
    public void intercept(IRouterCall call) {
      /*  HTDroidRouterParams params = (HTDroidRouterParams) call.getParams();
        HTLogUtil.d("Anno Interceptor 统计数据：" + params.getContext().getClass().getSimpleName() + "-->跳转url-->" + params.url +
                "  参数intent" + params.sourceIntent);
        //如果需要拦截或者改变跳转的目标可以直接改变url或者sourceIntent
        call.proceed();*/

        if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
            call.proceed();
        } else {
            ToastUtils.showMessage("配置错误，请正确配置跳转地址");
            call.cancel();
        }
    }
}
