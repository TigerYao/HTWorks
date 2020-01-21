package com.huatu.handheld_huatu.business.arena.textselect.interfaces;

import android.view.View;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;
import com.huatu.handheld_huatu.business.arena.textselect.engine.SelectInfo;

import java.util.ArrayList;

/**
 * 选项弹窗
 *
 * @param <T>   标记类
 */
public interface IMenu<T extends MarkInfo> {

    /**
     * 返回选项View
     */
    View getRootView();

    /**
     * 根据选择的起始位置，处理View的显示情况
     *
     * @param start 开始
     * @param end   结束
     * @return      如果需要重新选择位置，返回重新选择的位置。否则返回null
     */
    SelectInfo dealBtnStyle(int start, int end);

    /**
     * 获取标记列列表，以进行绘制
     *
     * @return  标记列表
     */
    ArrayList<T> getMarkInfoList();

    void setMarkInfoList(ArrayList<T> markInfoList);
}
