package com.huatu.handheld_huatu.business.arena.textselect.abstracts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.IMenu;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.ISelectInfo;

import java.util.ArrayList;

/**
 * 选项按钮抽象类
 * 复制和全选功能：
 * SelectTextHelper 本身已经实现了复制和全选功能，只要 Layout 中有 id 为 tv_copy、tv_select_all 的按钮就可以了，不用做任何操作
 */
public abstract class AbsMenu<T extends MarkInfo> implements IMenu<T>, ISelectInfo {

    protected Context context;
    protected View rootView;
    protected TextSelectManager.SelectTools selectTools;

    protected ArrayList<T> markInfoList = new ArrayList<>();

    public AbsMenu(Context context) {
        this.context = context;
        getRootView();
    }

    public void setSelectTools(TextSelectManager.SelectTools selectTools) {
        this.selectTools = selectTools;
    }

    // 返回弹出菜单的View，及各种按钮的点击事件处理
    @Override
    public View getRootView() {
        if (rootView == null) {
            int layoutId = getLayoutId();
            if (layoutId == 0) {
                throw new RuntimeException("没有设置菜单弹窗布局layoutId");
            }
            rootView = LayoutInflater.from(context).inflate(layoutId, null);
            onInitView();
        }
        return rootView;
    }

    // 这里还要存储各种背景类型AbsMark
    // 以及画各种类型而完成的CusDrawInterface

    // 设置布局Id
    protected abstract int getLayoutId();

    // 初始化View
    protected abstract void onInitView();

    @Override
    public ArrayList<T> getMarkInfoList() {
        return markInfoList;
    }

    @Override
    public void setMarkInfoList(ArrayList<T> markInfoList) {
        this.markInfoList = markInfoList;
    }
}
