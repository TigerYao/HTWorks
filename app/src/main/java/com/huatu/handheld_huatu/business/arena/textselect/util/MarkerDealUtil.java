package com.huatu.handheld_huatu.business.arena.textselect.util;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;
import com.huatu.handheld_huatu.business.arena.textselect.engine.SelectInfo;

import java.util.ArrayList;

/**
 * 当前的底纹和选择内容交集情况
 */
public class MarkerDealUtil {

    /**
     * 根据选择的开始和结束，检查是否有交集的MarkInfo，有交集的放入mixedMarkInfoList
     *
     * @param start             选择的开始位置
     * @param end               选择的结束位置
     * @param markInfoList      当前的MarkerList记录
     * @param mixedMarkInfoList 有交集的Marker存入其中
     */
    public static void checkMixed(int start, int end, ArrayList<MarkInfo> markInfoList, ArrayList<MarkInfo> mixedMarkInfoList) {
        mixedMarkInfoList.clear();

        if (markInfoList.size() == 0) {
            return;
        }

        MarkInfo selectMarkInfo = new MarkInfo();
        selectMarkInfo.start = start;
        selectMarkInfo.end = end;

        for (MarkInfo markInfo : markInfoList) {
            ArrayList<MarkInfo> markInfos = differentSet(selectMarkInfo, markInfo);
            if (!(markInfos != null && markInfos.size() > 0 && markInfos.get(0).start == selectMarkInfo.start && markInfos.get(0).end == selectMarkInfo.end)) {
                mixedMarkInfoList.add(markInfo);
            }
        }
    }

    /**
     * 检查选择区域对有交集的所有Marker依次进行差集计算
     *
     * @param start             选择的开始位置
     * @param end               选择的结束位置
     * @param mixedMarkInfoList 交集MarkerList
     * @return 如果差集为空返回true，返回false。 差集不为空，则表示选择了未mark的地方，所以这时候显示"添加Marker"，否则显示"取消Marker"
     */
    public static boolean differentSetNull(int start, int end, ArrayList<MarkInfo> mixedMarkInfoList) {

        if (mixedMarkInfoList.size() == 0) {
            return false;
        }

        MarkInfo selectMarkInfo = new MarkInfo();
        selectMarkInfo.start = start;
        selectMarkInfo.end = end;

        ArrayList<MarkInfo> oldMarkInfoList = new ArrayList<>();
        oldMarkInfoList.add(selectMarkInfo);

        ArrayList<MarkInfo> copymixedMarkInfoList = new ArrayList<>();

        for (MarkInfo markInfo : mixedMarkInfoList) {
            copymixedMarkInfoList.add(new MarkInfo(markInfo.start, markInfo.end));
        }

        ArrayList<MarkInfo> markInfos = differentSet(oldMarkInfoList, copymixedMarkInfoList);

        return markInfos.size() == 0;
    }

    private static ArrayList<MarkInfo> differentSet(ArrayList<MarkInfo> oneMarkInfoList, ArrayList<MarkInfo> twoMarkInfoList) {

        for (MarkInfo two : twoMarkInfoList) {
            ArrayList<MarkInfo> oneList = new ArrayList<>();
            for (MarkInfo one : oneMarkInfoList) {
                ArrayList<MarkInfo> difs = differentSet(one, two);
                if (difs != null) {
                    oneList.addAll(difs);
                }
            }
            oneMarkInfoList = oneList;
            if (oneMarkInfoList.size() == 0) {
                break;
            }
        }

        return oneMarkInfoList;
    }

    /**
     * 求两个集合的差集
     *
     * @return 可能是一个集合（没有交集就是oneMarker），可能是两个集合，可能是null
     */
    private static ArrayList<MarkInfo> differentSet(MarkInfo oneMarkInfo, MarkInfo twoMarkInfo) {
        ArrayList<MarkInfo> resultMarkInfoList = new ArrayList<>();
        if (twoMarkInfo.start <= oneMarkInfo.start && oneMarkInfo.start < twoMarkInfo.end
                && twoMarkInfo.end < oneMarkInfo.end) {                                                 // 第一个前交于第二个            第一个：2~4 第二个：1~3

            resultMarkInfoList.add(new MarkInfo(twoMarkInfo.end, oneMarkInfo.end));

        } else if (twoMarkInfo.start <= oneMarkInfo.start && oneMarkInfo.end <= twoMarkInfo.end) {      // 第一个完全被第二个包裹         第一个：2~3 第二个：1~4 两端可等
            return null;
        } else if (oneMarkInfo.start < twoMarkInfo.start
                && twoMarkInfo.start < oneMarkInfo.end && oneMarkInfo.end <= twoMarkInfo.end) {         // 第一个后交于第二个

            resultMarkInfoList.add(new MarkInfo(oneMarkInfo.start, twoMarkInfo.start));

        } else if (oneMarkInfo.start < twoMarkInfo.start && twoMarkInfo.end < oneMarkInfo.end) {        // 第一个完全包裹第二个

            resultMarkInfoList.add(new MarkInfo(oneMarkInfo.start, twoMarkInfo.start));
            resultMarkInfoList.add(new MarkInfo(twoMarkInfo.end, oneMarkInfo.end));
        } else {
            resultMarkInfoList.add(new MarkInfo(oneMarkInfo.start, oneMarkInfo.end));
        }

        return resultMarkInfoList;
    }

    /**
     * 得到交集的MarkInfoList的总范围
     *
     * @param mixedMarkInfoList 交集MarkerList
     * @return 包含所有有交集MarkInfo的总开始结束位置，返回
     */
    public static SelectInfo getRangeMixedMarkInfo(ArrayList<MarkInfo> mixedMarkInfoList) {
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.mStart = mixedMarkInfoList.get(0).start;
        selectInfo.mEnd = mixedMarkInfoList.get(0).end;
        for (int i = 1; i < mixedMarkInfoList.size(); i++) {
            MarkInfo markInfo = mixedMarkInfoList.get(i);
            if (markInfo.start < selectInfo.mStart) {
                selectInfo.mStart = markInfo.start;
            }
            if (selectInfo.mEnd < markInfo.end) {
                selectInfo.mEnd = markInfo.end;
            }
        }
        return selectInfo;
    }
}
