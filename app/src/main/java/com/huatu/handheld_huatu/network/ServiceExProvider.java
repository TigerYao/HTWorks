package com.huatu.handheld_huatu.network;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.huatu.handheld_huatu.TokenConflictActivity;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetErrorResponse;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleResponseModel;
import com.huatu.handheld_huatu.business.arena.activity.DailySpecialSettingActivity;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xing on 2018/4/9.
 */
public class ServiceExProvider {
    // List<? super E>   put原则
    //List<? extends E> get原则。
  /*  public static void test(CompositeSubscription cs, String ids, final NetResponse response){

          //nterControler<String>(cs,RetrofitManager.getInstance().getService().setVodCourseCategoryList(ids),response);
        new ServiceExProvider<String>().interControler(cs,RetrofitManager.getInstance().getService().setVodCourseCategoryList(ids),response);
    }
*/
    public static void test2(CompositeSubscription cs, String ids, final NetResponse response) {
      /* Observable<BaseResponseModel<CreateAnswerCardIdBean>> curObs=  CourseApiService.getApi().getEssayPagerInfo(courseWareId,videoType)
                 .flatMap(new Func1<BaseResponseModel<InClassEssayCardBean>, Observable<BaseResponseModel<CreateAnswerCardIdBean>>>() {
                     @Override
                     public Observable<BaseResponseModel<CreateAnswerCardIdBean>> call(BaseResponseModel<InClassEssayCardBean> inClassEssayCardBean) {
                         CreateAnswerCardPostBean tmpBean=new CreateAnswerCardPostBean();
                         return RetrofitManager.getInstance().getService().createHomeworkAnswerCardNew(tmpBean);
                     }
                 });*/
        //nterControler<String>(cs,RetrofitManager.getInstance().getService().setVodCourseCategoryList(ids),response);
       // ServiceExProvider.visit(cs, RetrofitManager.getInstance().getService().setVodCourseCategoryList(ids), response);
    }

 /*     if (e instanceof SocketTimeoutException) {
        ToastUtil.ToastCenter("网络中断，请检查您的网络状态");
    } else if (e instanceof ConnectException) {
        ToastUtil.ToastCenter("网络中断，请检查您的网络状态");
    }else if (e instanceof HttpException){             //HTTP错误
        ToastUtil.ToastCenter("网络异常，请检查您的网络状态");
        e.printStackTrace();
    } else if (e instanceof ApiException){
        ToastUtil.ToastCenter(e.getMessage());
        e.printStackTrace();
    }else {
        //这里可以收集未知错误上传到服务器
        ToastUtil.ToastCenter("服务器忙");
        e.printStackTrace();
    }
 */
    public static <T> void visit(Observable<BaseResponseModel<T>> baseObservable){
        Subscriber<BaseResponseModel<T>> subscriber = new Subscriber<BaseResponseModel<T>>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResponseModel<T> model) {

            }
        };
        baseObservable.subscribeOn(Schedulers.io()).subscribe(subscriber);
    }


    public static HttpService getCourseService(){
        return RetrofitManager.getInstance().getService();
    }


    private static void dealError(final Throwable e, final NetErrorResponse response) {
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            switch (exception.getErrorCode()) {
                case ApiErrorCode.ERROR_TOKEN_CONFLICT:
                    TokenConflictActivity.newIntent( UniApplicationContext.getContext(), exception.getErrorMsg());
                    return;
                case ApiErrorCode.ERROR_SESSION_TIMEOUT:
                    ActivityStack.getInstance().finishAllActivity();
                    LoginByPasswordActivity.newIntent(UniApplicationContext.getContext());
                    return;
                case ApiErrorCode.ERROR_NOT_SETTING_SPECIAL:
                    Activity topAct = ActivityStack.getInstance().getTopActivity();
                    if (topAct != null) {
                        Intent intent = new Intent(topAct, DailySpecialSettingActivity.class);
                        intent.putExtra("fromActivity", "HomeFragment");
                        topAct.startActivity(intent);
                    } else {
                        Intent intent = new Intent(UniApplicationContext.getContext(),
                                DailySpecialSettingActivity.class);
                        intent.putExtra("fromActivity", "HomeFragment");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        UniApplicationContext.getContext().startActivity(intent);
                    }
                    return;
                default:
                    break;
            }
            String message = TextUtils.isEmpty(exception.getErrorMsg()) ?  "网络请求错误，请重试"
                                                                        : exception.getErrorMsg();
            if (null != response) response.onError(message, 1);

        } else if (e instanceof HttpException) {
             if (null != response) response.onError(" 网络加载出错", ((HttpException)e).code()==504 ?3:2);

        } else {
            if (null != response) response.onError("数据加载出错", 3);
        }
    }

    public static <T> void visit(CompositeSubscription cs, Observable<BaseResponseModel<T>> baseObservable, final NetObjResponse<T> response) {
          Subscriber<BaseResponseModel<T>> subscriber = new Subscriber<BaseResponseModel<T>>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {
                dealError(e, response);  //LogUtils.e("onError",e.getMessage());
            }

            @Override
            public void onNext(BaseResponseModel<T> model) {
                // LogUtils.e("onNext", GsonUtil.GsonString(model));
                if (model.code == ApiErrorCode.ERROR_SUCCESS||model.code==200) {//教育的地址响应码为200
                      if(model.data==null) {
                          if(response!=null)   response.onError("数据解析出错", 0);
                          return;
                      }
                     if (response != null)  response.onSuccess(model);
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        cs.add(baseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));

    }

    public static <T> void visitList(CompositeSubscription cs, Observable<BaseListResponseModel<T>> baseObservable, final NetListResponse<T> response){
        visitList(cs,baseObservable,true,response);
    }

    public static <T> void visitList(CompositeSubscription cs, Observable<BaseListResponseModel<T>> baseObservable,final boolean adjustResult, final NetListResponse<T> response) {
        Subscriber<BaseListResponseModel<T>> subscriber = new Subscriber<BaseListResponseModel<T>>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {
                dealError(e, response);  //LogUtils.e("onError",e.getMessage());
            }

            @Override
            public void onNext(BaseListResponseModel<T> model) {
                // LogUtils.e("onNext", GsonUtil.GsonString(model));
                if (model.code == ApiErrorCode.ERROR_SUCCESS||model.code==200) {//教育的地址响应码为200
                    if(adjustResult&&model.data==null) {
                        if(response!=null)   response.onError("数据解析出错", 0);
                        return;
                    }
                    if (response != null)  response.onSuccess(model);
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        cs.add(baseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));

    }

    public static void visitSimple(CompositeSubscription cs, Observable<SimpleResponseModel> baseObservable, final NetObjResponse<String> response) {
        Subscriber<SimpleResponseModel> subscriber = new Subscriber<SimpleResponseModel>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {
                dealError(e, response);  //LogUtils.e("onError",e.getMessage());
            }

            @Override
            public void onNext(SimpleResponseModel model) {
                // LogUtils.e("onNext", GsonUtil.GsonString(model));
                if (model.code == ApiErrorCode.ERROR_SUCCESS) {
                     if (response != null)  response.onSuccess(null);
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        cs.add(baseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));

    }

}
