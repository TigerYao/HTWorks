package com.huatu.handheld_huatu.business.arena.fragment;

import com.huatu.handheld_huatu.base.BaseFragment;

public abstract class DownloadChildBaseFragment extends BaseFragment {
    /**
     * 点击编辑按钮
     *
     * @param edit true、编辑 false、完成
     */
    public abstract void editItem(boolean edit);

    /**
     * 点击全选
     *
     * @param select true、全选 false、全取消
     */
    public abstract void selectAll(boolean select);

    public abstract void delete();
}
