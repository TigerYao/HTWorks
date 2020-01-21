package com.huatu.handheld_huatu.business.arena.newtips.inter;

import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 模考大赛和小模考角标提示管理类
 */
public interface INewTipsManager {

    /**
     * 网络获取当前的大小模考列表，这里进行比对，把本地已看过的赋到新的列表，更新本地信息
     * 首页获取网络数据后调用
     *
     * @param catgory 当前科目
     * @param subject 当前小科目
     * @param ts      网络数据
     * @return 对应试卷类型的角标数量
     */
    ArrayList<TipBean> updateTips(int catgory, int subject, List<TipNewBean> ts);

    /**
     * 获取本地大小模考角标数量
     * 首页本地更新使用
     */
    ArrayList<TipBean> getHomeTips(int catgory, int subject);

    /**
     * 根据suject获取角标显示的数量
     *
     * @param catgory   当前科目
     * @param paperType 试卷类型（大模考、小模考）
     * @return 对应科目的角标数量
     * <p>
     * paperType
     * ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST    模考大赛
     * ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH           小模考
     */
    ArrayList<TipBean> getSubjectTip(int catgory, int paperType);

    /**
     * 某一个科目看过了，设置已看过
     *
     * @param subject   已看过的科目
     * @param paperType 试卷类型（大模考、小模考）
     */
    void setTipsGone(int catgory, int subject, int paperType);
}
