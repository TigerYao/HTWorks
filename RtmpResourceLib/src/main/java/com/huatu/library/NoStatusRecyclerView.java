package com.huatu.library;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/3/29.
 */
public class NoStatusRecyclerView extends RecyclerView {

    public NoStatusRecyclerView(Context context) {
        this(context, null);
    }

    public NoStatusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        super.onSaveInstanceState();
        // 返回一个空State,相当于没有保存RecyclerView的状态
        return BaseSavedState.EMPTY_STATE;
    }
}
