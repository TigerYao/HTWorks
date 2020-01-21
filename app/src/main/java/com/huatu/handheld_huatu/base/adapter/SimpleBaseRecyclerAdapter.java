package com.huatu.handheld_huatu.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2018\7\11 0011.
 */

public abstract class SimpleBaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   protected List<T> mItems;
   protected Context mContext;


    protected OnRecItemClickListener onRecyclerViewItemClickListener;

    public void setOnViewItemClickListener(OnRecItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }


    public SimpleBaseRecyclerAdapter(Context context) {
      mItems = new ArrayList<>();
      this.mContext = context;
      //this.mInflater = LayoutInflater.from(context);

   }

    public SimpleBaseRecyclerAdapter(Context context,List<T> items) {
        this.mItems = items;
        this.mContext = context;
        //this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemCount(){
        return ArrayUtils.size(mItems);
    }

    public void addAll(List<T> items) {
        if (items != null) {
            this.mItems.addAll(items);
            notifyItemRangeInserted(this.mItems.size(), items.size());
        }
    }

    public void addAllAt(int postion,List<T> items){
        if (items != null) {
            this.mItems.addAll(postion,items);
            notifyItemRangeInserted(postion, items.size());
        }
      //  notifyItemRangeInserted
    }

    public void removeAllAt(int postion,List<T> items){
        if (items != null) {
            this.mItems.removeAll(items);
            this.notifyItemRangeRemoved(postion,items.size());
            //notifyItemRangeInserted(postion, items.size());
        }
        //  notifyItemRangeInserted
    }

    public void removeAt(int postion,T item){
        if (item != null) {
            this.mItems.remove(item);
            this.notifyItemRemoved(postion);
        }
    }

    public void removeAt(int p){
        if (p < 0 || p >= mItems.size()) return;
        this.mItems.remove(p);
        this.notifyItemRemoved(p);
     }

    public void refresh(List<T> items){
        if (items != null) {
            this.mItems.clear();
            this.mItems.addAll(items);
            this.notifyDataSetChanged();
        }
     }

    public void clearAndRefresh() {
        this.mItems.clear();
        this.notifyDataSetChanged();
     }

    public final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size());
        }
    }


    public void addItem(int position, T item) {
        if (item != null) {
            this.mItems.add(position, item);
            notifyItemInserted(position);
        }
    }

    public final T getItem(int position) {
        int p = position;
        if (p < 0 || p >= mItems.size())
            return null;
        return mItems.get(position);
    }

    public List<T> getAllItems(){
        return mItems;
    }
}
