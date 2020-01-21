package com.huatu.handheld_huatu.business.arena.textselect;

import android.content.Context;
import android.util.SparseArray;
import android.widget.TextView;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.AbsMenu;
import com.huatu.handheld_huatu.business.arena.textselect.engine.Cursor;
import com.huatu.handheld_huatu.business.arena.textselect.engine.MenuWindow;
import com.huatu.handheld_huatu.business.arena.textselect.engine.SelectHelper;
import com.huatu.handheld_huatu.business.arena.textselect.engine.SelectInfo;
import com.huatu.handheld_huatu.business.arena.textselect.engine.TextStyleDraw;
import com.huatu.handheld_huatu.business.arena.textselect.impl.BackgroundDrawStyle;
import com.huatu.handheld_huatu.business.arena.textselect.impl.MarkerPenMenu;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.IDrawStyle;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import java.util.ArrayList;

/**
 * 在开始初始化的时候：
 * 1、创建公用的菜单Window，和两个公用的光标Window
 * 2、初始化公用的背景绘制器
 * 3、添加基础的背景Style，可都在这里添加各种类型，也可在外边添加
 * 在添加TextView的时候：
 * 要构造TextView的一一对应的工具对象，
 * 比如SelectHelper，Menu，
 * Menu不公用是因为里边存储了背景位置List，根据当前的背景位置和选中的位置进行不同的显示，有很紧密的联系。
 */
public class TextSelectManager {

    // 单例模式
    private static volatile TextSelectManager singleton;

    public static TextSelectManager newInstance() {
        if (singleton == null) {
            synchronized (TextSelectManager.class) {
                if (singleton == null) {
                    singleton = new TextSelectManager();
                }
            }
        }
        return singleton;
    }

    private TextSelectManager() {
        helperXES = new ArrayList<>();
        drawStyleArray = new SparseArray<>();
    }

    private TextStyleDraw styleDraw;
    private MenuWindow windowX;
    private Cursor cursorLeft;
    private Cursor cursorRight;
    // 存储 key - Style（样式），value - IDrawStyle（绘制对象）
    public SparseArray<IDrawStyle> drawStyleArray;

    /**
     * 初始化，
     */
    public void init(Context context) {
        if (styleDraw != null) return;

        styleDraw = new TextStyleDraw();

        windowX = new MenuWindow(context);

        cursorLeft = new Cursor(context, true);
        cursorRight = new Cursor(context, false);

        BackgroundDrawStyle backgroundDrawStyle = new BackgroundDrawStyle();
        drawStyleArray.put(backgroundDrawStyle.style, backgroundDrawStyle);

    }

    private ArrayList<SelectHelper> helperXES;

    public void clearOthers(SelectHelper helper) {
        for (SelectHelper helperX : helperXES) {
            if (helper == null || !helper.equals(helperX)) {
                helperX.clearView();
            }
        }
    }

    public void destroy() {
        if (helperXES != null) {
            for (SelectHelper helperX : helperXES) {
                helperX.destroy();
            }
            helperXES.clear();
        }
        styleDraw = null;
        windowX = null;
        cursorLeft = null;
        cursorRight = null;
        drawStyleArray.clear();
    }

    /**
     * 开启选择复制
     *
     * @param type     这里标记是材料 or 题干
     * @param textView 要复制的TextView
     */
    public void openSelectDealStyle(int type, ExerciseTextView textView) {

        Context context = textView.getContext();

        MarkerPenMenu markMenu = new MarkerPenMenu(context, type);

        SelectHelper selectTextHelper = new SelectHelper(context,
                textView,
                markMenu,
                windowX,
                cursorLeft,
                cursorRight);

        helperXES.add(selectTextHelper);

        SelectTools selectTools = new SelectTools();
        selectTools.context = context;
        selectTools.textView = textView;
        selectTools.markMenu = markMenu;
        selectTools.textStyleDraw = styleDraw;
        selectTools.selectInfo = selectTextHelper.mSelectInfo;
        selectTools.selectTextHelper = selectTextHelper;

        textView.setSelectTools(selectTools);
        markMenu.setSelectTools(selectTools);
    }

    public static class SelectTools {
        public Context context;
        public TextView textView;
        public AbsMenu markMenu;
        public TextStyleDraw textStyleDraw;
        public SelectInfo selectInfo;
        public SelectHelper selectTextHelper;
    }
}
