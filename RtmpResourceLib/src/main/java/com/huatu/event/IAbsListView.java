package com.huatu.event;

import android.support.v7.widget.RecyclerView;
import android.widget.ListAdapter;

/**
 * Created by Terry
 * Date : 2016/3/4 0004.
 * Email: terry@xiaodao360.com
 */
public interface IAbsListView {

      void setOnLoadMoreListener(IonLoadMoreListener onLoadMoreListener);

      void notifyDataSetChanged(int positionStart, int itemSize) ;

      void setPagesize(int curPagesize);

      void setMyAdapter(ListAdapter madapter) ;

      void setRecyclerAdapter(RecyclerView.Adapter madapter) ;

      void hideloading() ;

      void  checkloadMore(int size);

      // public void set  onRefreshComplete
      void reset() ;

      void selectionFromTop();

      void showNetWorkError();
}
