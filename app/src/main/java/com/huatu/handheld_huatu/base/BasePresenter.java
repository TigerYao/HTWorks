package com.huatu.handheld_huatu.base;

import com.huatu.handheld_huatu.base.BaseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019\8\23 0023.
 */

public class BasePresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
   // protected List<Disposable> disposables;

    @Override
    public void attachView(T view) {
        this.mView = view;
       // disposables = new ArrayList<>();
    }

    @Override
    public void detachView() {
        if (mView != null) {
            mView = null;
        }
      /*  for (Disposable dis : disposables) {
            dis.dispose();
        }
        disposables.clear();*/
    }

}
