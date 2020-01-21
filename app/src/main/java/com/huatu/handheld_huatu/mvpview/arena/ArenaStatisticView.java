package com.huatu.handheld_huatu.mvpview.arena;

import com.huatu.handheld_huatu.mvpview.BaseView;

/**
 * Created by saiyuan on 2016/10/28.
 */
public interface ArenaStatisticView<T> extends BaseView {
    public void getShareInfoSucc(T bean);
    public void getShareInfoFail();
}