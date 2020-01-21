package com.huatu.handheld_huatu.ui.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Administrator on 2016/8/8. RecyclerView.OnScrollListener,
 */
public class RecyclerViewTopEx extends RecyclerViewEx   {



    public RecyclerViewTopEx(Context context) {
        this(context, null);
    }

    public RecyclerViewTopEx(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewTopEx(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private boolean hasTopMore=true;

    public void setHasTopMore(boolean hastop){
        hasTopMore=hastop;
    }
    public boolean isTop(){
        if(hasTopMore) return false;
        return isRecyclerViewTop(this);
    }

    private static boolean isRecyclerViewTop(RecyclerViewEx recyView) {

        if(recyView instanceof RecyclerView){
            RecyclerView recyclerView=(RecyclerView)recyView;
            if (recyclerView != null) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    View childAt = recyclerView.getChildAt(0);
                    if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
