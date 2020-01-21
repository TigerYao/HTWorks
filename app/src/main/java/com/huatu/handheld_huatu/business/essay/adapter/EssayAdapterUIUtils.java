package com.huatu.handheld_huatu.business.essay.adapter;

import android.view.View;
import android.widget.TextView;

class EssayAdapterUIUtils {

    /**
     * 单题、套题列表，item显示状态
     *
     * @param correctSum 智能批改次数
     * @param manualSum  人工批改次数
     * @param tvInfo     "全站共智能批改200次，人工批改100次"
     * @param correctNum 自己的智能批改次数
     * @param tvAi       "智能批改10次"
     * @param manualNum  自己的人工批改次数
     * @param tvPersonal "人工批改100次"
     */
    static void setItemData(int correctSum, int manualSum, TextView tvInfo,
                            int correctNum, TextView tvAi,
                            int manualNum, TextView tvPersonal) {

        tvInfo.setVisibility(View.VISIBLE);
        if (correctSum > 0 && manualSum > 0) {      // 都不为0
            tvInfo.setText("全站共智能批改" + correctSum + "次，人工批改" + manualSum + "次");
        } else if (correctSum > 0) {                // 智能不为0
            tvInfo.setText("全站共智能批改" + correctSum + "次");
        } else if (manualSum > 0) {                 // 人工不为0
            tvInfo.setText("全站共人工批改" + manualSum + "次");
        } else {
            tvInfo.setVisibility(View.GONE);
        }

        if (correctNum > 0) {           // 自己的批改次数不为0
            tvAi.setVisibility(View.VISIBLE);
            tvAi.setText("智能批改" + correctNum + "次");
        } else {
            tvAi.setVisibility(View.GONE);
        }

        if (manualNum > 0) {
            tvPersonal.setVisibility(View.VISIBLE);
            tvPersonal.setText("人工批改" + manualNum + "次");
        } else {
            tvPersonal.setVisibility(View.GONE);
        }
    }
}
