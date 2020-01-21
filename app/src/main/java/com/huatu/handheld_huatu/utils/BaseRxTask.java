package com.huatu.handheld_huatu.utils;

import android.content.Context;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**https://blog.csdn.net/relicemxd/article/details/52623003
 * Created by cjx on 2018\11\6 0006.
 */

public abstract class BaseRxTask<D, P> {

    /**
     * 参数数组
     */
    protected P[] p;
    protected Context mContext;
    private boolean needDialog = false;


    /**
     * 接口不需要传参数 复写此方法
     */
    protected BaseRxTask(Context context, boolean needDialog) {
        this.mContext = context;
        this.needDialog = needDialog;
    }

    /**
     * 接口需要传参数调 复写此方法
     *
     * @param context 上下文
     * @param needDialog 是否显示进度
     * @param p 参数数组
     */
    @SafeVarargs
    protected BaseRxTask(Context context, boolean needDialog, P... p) {
        this.mContext = context;
        this.needDialog = needDialog;
        this.p = p;
    }

    protected Observable<D> doInBackgroundObserVable() {
        return doInBackground();
    }

    public Observable<D> execute() {
        return getBaseDilogView();
    }

    /**
     * 进度View
     *
     * @return
     */
    private Observable<D> getBaseDilogView() {
        Observable<D> tObservable = doInBackgroundObserVable();
        //subscribeOn(Schedulers.io())事件所产生的线程,也就是subscribe所发生的线程.
        return tObservable.subscribeOn(Schedulers.io())
                //doOnSubscribe的作用是如它后面有subscribeOn(),那么它将执行
//离它最近的subscribeOn()所指定的线程,也就是下面的              //observeOn(AndroidSchedulers.mainThread())线程.
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (needDialog && mContext != null) {

                        }
                    }
                   // 注意这里添加了doOnCompleted,doOnError两个预处理的操作,
                  //所以在后面对Observable的订阅就要用subscribe(new Observer),如果使用subscribe(new Action)会报错.
                }).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                      //  CHelper.disimissDialog(dialog);
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                       // CHelper.disimissDialog(dialog);
                    }
                })
               //为了让进度框可以在UI线程里执行,影响了上面doOnSubscribe的线程
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected abstract Observable<D> doInBackground();

    /**
     * 接口没参数空实现即可
     *
     * @param p 参数数组
     */
    protected abstract String getParam(P... p);


}
