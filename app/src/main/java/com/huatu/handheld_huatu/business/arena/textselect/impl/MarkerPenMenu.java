package com.huatu.handheld_huatu.business.arena.textselect.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.arch.ActivityDataBus;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.bean.ArenaDataBus;
import com.huatu.handheld_huatu.business.arena.textselect.abstracts.AbsMenu;
import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;
import com.huatu.handheld_huatu.business.arena.textselect.engine.SelectInfo;
import com.huatu.handheld_huatu.business.arena.textselect.util.MarkerDealUtil;
import com.huatu.handheld_huatu.helper.db.KnowPointInfoDao;

import java.util.ArrayList;

/**
 * 弹窗实现
 * 标记内容
 * 选中后弹窗显示样式
 * 选中处理
 */
public class MarkerPenMenu extends AbsMenu<MarkInfo> {

    // 0、原始显示，"荧光笔"
    // 1、取消标记，"取消荧光笔"
    private int showStyle = 0;

    private TextView tvMarkerPen;

    private Drawable one, two;

    private long id;            // 答题卡id，为了数据库的增删改查
    private int type;           // 类型 材料 or 题干
    private int questionId;     // 题id

    public MarkerPenMenu(Context context, int type) {
        super(context);
        one = ContextCompat.getDrawable(context, R.mipmap.menu_line);
        one.setBounds(0, 0, one.getMinimumWidth(), one.getMinimumHeight());
        two = ContextCompat.getDrawable(context, R.mipmap.menu_line_d);
        two.setBounds(0, 0, two.getMinimumWidth(), two.getMinimumHeight());

        this.type = type;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.select_option_nite_pen;
    }

    @Override
    protected void onInitView() {
        tvMarkerPen = rootView.findViewById(R.id.tv_nite_pen);
        tvMarkerPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == 0) {
                    id = ActivityDataBus.getData(context, ArenaDataBus.class).practiceId;
                }
                switch (showStyle) {
                    case 0:     // 荧光笔
                        if (mixedMarkInfoList.size() == 0) {        // 交集为空，直接添加
                            MarkInfo markInfo = getMarkInfo(selectTools.selectInfo);
                            markInfoList.add(markInfo);
                            markInfo.seq = KnowPointInfoDao.getInstance().create(questionId, id, type, markInfo.start, markInfo.end, markInfo.content);
                        } else {                                    // 不为空，先把交集lis从markInfoList移除掉，然后和selection合并添加进markInfoList
                            for (MarkInfo markInfo : mixedMarkInfoList) {
                                markInfoList.remove(markInfo);
                                KnowPointInfoDao.getInstance().delete(markInfo.seq);
                            }
                            mixedMarkInfoList.add(getMarkInfo(selectTools.selectInfo));
                            MarkInfo markInfo = getMarkInfo(MarkerDealUtil.getRangeMixedMarkInfo(mixedMarkInfoList));
                            markInfoList.add(markInfo);
                            markInfo.seq = KnowPointInfoDao.getInstance().create(questionId, id, type, markInfo.start, markInfo.end, markInfo.content);
                            mixedMarkInfoList.clear();
                        }
                        break;
                    case 1:     // 取消标记
                        for (MarkInfo markInfo : mixedMarkInfoList) {
                            markInfoList.remove(markInfo);
                            KnowPointInfoDao.getInstance().delete(markInfo.seq);
                        }
                        mixedMarkInfoList.clear();
                        break;
                    default:
                }
                selectTools.selectTextHelper.clearView();
            }
        });
    }

    /**
     * 单独的荧光笔（划线）功能处理逻辑：
     */
    private ArrayList<MarkInfo> mixedMarkInfoList = new ArrayList<>();

    @Override
    public SelectInfo dealBtnStyle(int start, int end) {
        // 根据选择的开始和结束，检查是否有交集的MarkInfo，有交集的放入needDealMarkInfoList
        MarkerDealUtil.checkMixed(start, end, markInfoList, mixedMarkInfoList);
        if (mixedMarkInfoList.size() <= 0) {     // 无交集，显示荧光笔
            showStyle = 0;
        } else {                                 // 有交集
            if (MarkerDealUtil.differentSetNull(start, end, mixedMarkInfoList)) {       // 表示未选择无标记的字，所以显示"取消荧光笔"
                showStyle = 1;
            } else {                                                                    // 表示选择了无标记的字，所以显示"荧光笔"
                showStyle = 0;
            }
            showText();
            return MarkerDealUtil.getRangeMixedMarkInfo(mixedMarkInfoList);
        }
        showText();
        return null;
    }

    private void showText() {
        if (showStyle == 1) {
            tvMarkerPen.setCompoundDrawables(null, two, null, null);
            tvMarkerPen.setText("取消高亮");
        } else {
            tvMarkerPen.setCompoundDrawables(null, one, null, null);
            tvMarkerPen.setText("高亮");
        }
    }

    private MarkInfo getMarkInfo(SelectInfo selectInfo) {
        return new MarkInfo(selectInfo.mStart, selectInfo.mEnd, selectInfo.mSelectionContent, 0, Color.parseColor("#30F44336"));
    }
}
