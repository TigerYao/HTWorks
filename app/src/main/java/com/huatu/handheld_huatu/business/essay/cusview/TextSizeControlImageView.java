package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.utils.EventBusUtil;

/**
 * 改变字体大小的自定义ImageView
 * 记得要设置style，就是现实图片的样式颜色
 */

public class TextSizeControlImageView extends android.support.v7.widget.AppCompatImageView {

    private int type;           // 是显示黑底字还是白底字 0、显示黑底字 1、显示白底字
    private String page;        // 是哪个页面，用于埋点

    public TextSizeControlImageView(Context context) {
        this(context, null);
    }

    public TextSizeControlImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSizeControlImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setType(int type, String page) {
        this.type = type;
        this.page = page;
        initStyle();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 神策埋点 题库->申论 资料页/作答页/查看答案页 字号
                StudyCourseStatistic.clickStatistic("题库->申论", page, "字号");
                int materialsTxtSizeNow = EssayExamDataCache.getInstance().materialsTxtSize;
                switch (materialsTxtSizeNow) {
                    case 15:
                        EssayExamDataCache.getInstance().materialsTxtSize = 16;
                        break;
                    case 16:
                        EssayExamDataCache.getInstance().materialsTxtSize = 17;
                        break;
                    case 17:
                        EssayExamDataCache.getInstance().materialsTxtSize = 15;
                        break;
                }
                initStyle();
                EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize));
            }
        });
    }

    public void initStyle() {
        int materialsTxtSizeNow = EssayExamDataCache.getInstance().materialsTxtSize;
        switch (materialsTxtSizeNow) {
            case 15:
                if (type == 0) {
                    setImageResource(R.mipmap.action_bar_text_size_small);
                } else {
                    setImageResource(R.mipmap.action_bar_text_size_white_small);
                }
                break;
            case 16:
                if (type == 0) {
                    setImageResource(R.mipmap.action_bar_text_size_middle);
                } else {
                    setImageResource(R.mipmap.action_bar_text_size_white_middle);
                }
                break;
            case 17:
                if (type == 0) {
                    setImageResource(R.mipmap.action_bar_text_size_big);
                } else {
                    setImageResource(R.mipmap.action_bar_text_size_white_big);
                }
                break;
        }
    }

}
