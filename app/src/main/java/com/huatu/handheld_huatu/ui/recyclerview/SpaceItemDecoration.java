package com.huatu.handheld_huatu.ui.recyclerview;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/10/19.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;
    private boolean mHasTop;
    private boolean mAllTop=false;

/*    public SpaceItemDecoration(int space) {
        this.space = space;
    }*/

    public SpaceItemDecoration(int space, boolean hasTop) {
        this.space = space;
        mHasTop=hasTop;
    }

    public SpaceItemDecoration(int space) {
        this.space = space;
        mAllTop=true;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
       if (parent.getLayoutManager() instanceof GridLayoutManager) {
           outRect.left = space;
       }
        if(mAllTop) {
            outRect.top = space;
            return;
        }
        if(mHasTop){
            if(parent.getChildAdapterPosition(view) >1 )
                outRect.top = space;
        }
        else {
            if(parent.getChildAdapterPosition(view) != 0)
                outRect.top = space;
        }
     }
}