package com.huatu.library;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/3/29.
 */
public class PullRefreshNoStatusRecyclerView extends PullRefreshRecyclerView {

    public PullRefreshNoStatusRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullRefreshNoStatusRecyclerView(Context context) {
        super(context);
    }

    public PullRefreshNoStatusRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRefreshNoStatusRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        NoStatusRecyclerView recyclerView = new NoStatusRecyclerView(context,attrs);
        LinearLayoutManager mannagerTwo = new LinearLayoutManager(context);
        mannagerTwo.setOrientation(LinearLayoutManager.VERTICAL);
        //recyclerView.addItemDecoration(
        //    new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        recyclerView.setLayoutManager(mannagerTwo);

        return recyclerView;
    }

}
