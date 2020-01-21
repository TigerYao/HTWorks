package com.huatu.handheld_huatu.business.arena.newtips;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.business.arena.newtips.inter.INewTipsManager;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NewTipsManager implements INewTipsManager {

    private Gson gson;

    // 单例模式
    private static volatile NewTipsManager singleton;

    public static NewTipsManager newInstance() {
        if (singleton == null) {
            synchronized (NewTipsManager.class) {
                if (singleton == null) {
                    singleton = new NewTipsManager();
                }
            }
        }
        return singleton;
    }

    private NewTipsManager() {
        gson = GsonUtil.getGson();
    }

    @Override
    public ArrayList<TipBean> updateTips(int catgory, int subject, List<TipNewBean> tipNewBeans) {

        String matchNewTip = SpUtils.getMatchNewTip();
        // 对比新老数据
        if (!StringUtils.isEmpty(matchNewTip)) {        // 本地信息不为空
            try {
                ArrayList<TipNewBean> localTips = gson.fromJson(matchNewTip, new TypeToken<ArrayList<TipNewBean>>() {
                }.getType());
                if (localTips != null && localTips.size() > 0) {
                    for (TipNewBean tipNewBean : tipNewBeans) {         // 遍历现在的

                        long[] tipNewMatch = tipNewBean.match;
                        long[] tipNewSmall = tipNewBean.small;
                        int[] tipNewMatchFlag = tipNewBean.matchReadFlag;
                        int[] tipNewSmallFlag = tipNewBean.smallReadFlag;

                        if ((tipNewMatch != null && tipNewMatch.length > 0)
                                || (tipNewSmall != null && tipNewSmall.length > 0)) {

                            for (TipNewBean localTip : localTips) {             // 寻找本地对应科目的

                                long[] localMatch = localTip.match;
                                long[] localSmall = localTip.small;
                                int[] localNewMatchFlag = localTip.matchReadFlag;
                                int[] localNewSmallFlag = localTip.smallReadFlag;

                                if (localTip.category == tipNewBean.category
                                        && localTip.subject == tipNewBean.subject) {   // 找到对应的科目类型

                                    if ((tipNewMatch != null && tipNewMatch.length > 0)
                                            && (localMatch != null && localMatch.length > 0)) {
                                        for (int i = 0; i < tipNewMatch.length; i++) {      // 遍历模考大赛
                                            for (int j = 0; j < localMatch.length; j++) {
                                                if (tipNewMatch[i] == localMatch[j]) {
                                                    tipNewMatchFlag[i] = localNewMatchFlag[j];
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if ((tipNewSmall != null && tipNewSmall.length > 0)
                                            && (localSmall != null && localSmall.length > 0)) {
                                        for (int i = 0; i < tipNewSmall.length; i++) {      // 遍历小模考
                                            for (int j = 0; j < localSmall.length; j++) {
                                                if (tipNewSmall[i] == localSmall[j]) {
                                                    tipNewSmallFlag[i] = localNewSmallFlag[j];
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        String tipNewContent = gson.toJson(tipNewBeans);        // 存到本地
        SpUtils.setMatchNewTip(tipNewContent);

        return getTipBeans(catgory, subject, tipNewBeans);
    }

    @Override
    public ArrayList<TipBean> getHomeTips(int catgory, int subject) {

        // 获取本地数据
        String matchNewTip = SpUtils.getMatchNewTip();
        if (!StringUtils.isEmpty(matchNewTip)) {        // 本地信息不为空
            try {
                ArrayList<TipNewBean> localTips = gson.fromJson(matchNewTip, new TypeToken<ArrayList<TipNewBean>>() {
                }.getType());
                if (localTips != null && localTips.size() > 0) {
                    return getTipBeans(catgory, subject, localTips);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 过滤tipNewBeans中catgory下未读条数
     */
    private ArrayList<TipBean> getTipBeans(int catgory, int subject, List<TipNewBean> tipNewBeans) {
        ArrayList<TipBean> tipBeans = new ArrayList<>();
        tipBeans.add(new TipBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, 0));
        tipBeans.add(new TipBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, 0));

        TipBean tipMatch = tipBeans.get(0);     // 模考大赛
        TipBean tipSmall = tipBeans.get(1);     // 小模考

        // 这里过滤catgory下没看过的数量
        for (TipNewBean tipNewBean : tipNewBeans) {
            if (tipNewBean.category == catgory) {

                long[] match = tipNewBean.match;
                long[] small = tipNewBean.small;
                int[] matchFlag = tipNewBean.matchReadFlag;
                int[] smallFlag = tipNewBean.smallReadFlag;

                if (subject != Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS || tipNewBean.subject == subject) {  // 申论只要suject是申论模考，其他就要当前catgory下的所有模考
                    if (match != null && match.length > 0) {            // 大模考不分小科目
                        for (int i : matchFlag) {
                            if (i == 0) {
                                tipMatch.tipNum++;
                            }
                        }
                    }
                }

                if (tipNewBean.subject == subject) {                // 小模考分小科目
                    if (small != null && small.length > 0) {
                        for (int i : smallFlag) {
                            if (i == 0) {
                                tipSmall.tipNum++;
                            }
                        }
                    }
                }
            }
        }
        return tipBeans;
    }

    @Override
    public ArrayList<TipBean> getSubjectTip(int catgory, int paperType) {

        ArrayList<TipBean> tipBeans = new ArrayList<>();

        // 获取本地数据
        String matchNewTip = SpUtils.getMatchNewTip();
        if (!StringUtils.isEmpty(matchNewTip)) {        // 本地信息不为空
            try {
                ArrayList<TipNewBean> localTips = gson.fromJson(matchNewTip, new TypeToken<ArrayList<TipNewBean>>() {
                }.getType());
                if (localTips != null && localTips.size() > 0) {
                    for (TipNewBean localTip : localTips) {
                        if (localTip.category == catgory) {
                            TipBean tipBean = new TipBean(localTip.subject, 0);
                            tipBeans.add(tipBean);
                            long[] match = localTip.match;
                            int[] matchFlag = localTip.matchReadFlag;
                            if (match != null && match.length > 0) {
                                for (int i : matchFlag) {
                                    if (i == 0) {
                                        tipBean.tipNum++;
                                    }
                                }
                            }

                        }
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        return tipBeans;
    }

    @Override
    public void setTipsGone(int catgory, int subject, int paperType) {
        // 获取本地数据
        String matchNewTip = SpUtils.getMatchNewTip();
        if (!StringUtils.isEmpty(matchNewTip)) {        // 本地信息不为空
            try {
                ArrayList<TipNewBean> localTips = gson.fromJson(matchNewTip, new TypeToken<ArrayList<TipNewBean>>() {
                }.getType());
                if (localTips != null && localTips.size() > 0) {
                    for (TipNewBean localTip : localTips) {
                        if (localTip.category == catgory && localTip.subject == subject) {
                            if (paperType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {       // 模考大赛
                                int[] matchFlag = localTip.matchReadFlag;
                                if (matchFlag != null && matchFlag.length > 0) {
                                    for (int i = 0; i < matchFlag.length; i++) {
                                        matchFlag[i] = 1;
                                    }
                                }
                            } else if (paperType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {       // 小模考
                                int[] smallFlag = localTip.smallReadFlag;
                                if (smallFlag != null && smallFlag.length > 0) {
                                    for (int i = 0; i < smallFlag.length; i++) {
                                        smallFlag[i] = 1;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                String tipNewContent = gson.toJson(localTips);        // 存到本地
                SpUtils.setMatchNewTip(tipNewContent);

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
