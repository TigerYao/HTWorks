package com.huatu.handheld_huatu.business.essay.cusview.imgdrag;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.utils.ArrayUtils;

import java.util.Collections;
import java.util.List;

/**
 * 自定义拖拽
 * Created by kuyue on 2017/6/19 下午5:42.
 * 邮箱:595327086@qq.com
 */

public class MyCallBack extends ItemTouchHelper.Callback {

    private int dragFlags;
    private int swipeFlags;
    private EssayEditImgAdapter adapter;
    private List<AnswerImage> originImages;          // 图片没有经过处理，这里传这个进来是为了使原图片的顺序与拖拽顺序保持一致
    private boolean up;                         // 手指抬起标记位
    private Activity mActivity;

    public MyCallBack(Activity mActivity, EssayEditImgAdapter adapter, List<AnswerImage> originImages) {
        this.mActivity = mActivity;
        this.adapter = adapter;
        this.originImages = originImages;
    }

    /**
     * 设置item是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //判断 recyclerView的布局管理器数据
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {//设置能拖拽的方向
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;//0则不响应事件
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 当用户从item原来的位置拖动可以拖动的item到新位置的过程中调用
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//得到item原来的position
        int toPosition = target.getAdapterPosition();//得到目标position
        if (toPosition == originImages.size() - 1 || originImages.size() - 1 == fromPosition) {
            return true;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(originImages, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(originImages, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 设置是否支持长按拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 当用户与item的交互结束并且item也完成了动画时调用
     *
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.notifyDataSetChanged();
        initData();
        if (dragListener != null) {
            dragListener.clearView();
        }
    }

    /**
     * 重置
     */
    private void initData() {
        if (dragListener != null) {
            dragListener.deleteState(false);
            dragListener.dragState(false);
        }
        up = false;
    }

    /**
     * 自定义拖动与滑动交互
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (null == dragListener) {
            return;
        }

        int[] location = new int[2];
        recyclerView.getLocationOnScreen(location);    //获取在整个屏幕内的绝对坐标

        //获取View可见区域的bottom
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        if (dY >= (rect.bottom - location[1]
                - viewHolder.itemView.getBottom()//item底部距离recyclerView顶部高度
                - DisplayUtil.dp2px(50))) {//拖到删除处
            dragListener.deleteState(true);
            if (up) {//在删除处放手，则删除item
                viewHolder.itemView.setVisibility(View.INVISIBLE);//先设置不可见，如果不设置的话，会看到viewHolder返回到原位置时才消失，因为remove会在viewHolder动画执行完成后才将viewHolder删除

                int AdapterPostion = viewHolder.getAdapterPosition();
                if (AdapterPostion < 0 || (AdapterPostion >= ArrayUtils.size(originImages))) {
                    return;
                }
                AnswerImage remove = originImages.remove(AdapterPostion);
                dragListener.deleteView(remove);
                adapter.notifyItemRemoved(AdapterPostion);
                initData();
                return;
            }
        } else {//没有到删除处
            if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {//如果viewHolder不可见，则表示用户放手，重置删除区域状态
                dragListener.dragState(false);
            }
            dragListener.deleteState(false);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && dragListener != null) {
            dragListener.dragState(true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     *
     * @param recyclerView
     * @param animationType
     * @param animateDx
     * @param animateDy
     * @return
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        //手指放开
        up = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    public interface DragListener {
        /**
         * 用户是否将 item拖动到删除处，根据状态改变颜色
         *
         * @param delete
         */
        void deleteState(boolean delete);

        /**
         * 是否于拖拽状态
         *
         * @param start
         */
        void dragState(boolean start);

        /**
         * 当用户与item的交互结束并且item也完成了动画时调用
         */
        void clearView();

        void deleteView(AnswerImage remove);
    }

    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

}
