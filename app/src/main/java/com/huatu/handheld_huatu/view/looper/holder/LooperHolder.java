package com.huatu.handheld_huatu.view.looper.holder;

import android.content.Context;
import android.view.View;

/**
 * desc:
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public interface LooperHolder<T> {
    View createView(Context context);

    void upDataUI(Context context, int position, T data);
}
