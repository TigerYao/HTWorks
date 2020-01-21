package com.levylin.detailscrollview.views;

import com.levylin.detailscrollview.views.listener.OnScrollBarShowListener;

/**
 * Created by Administrator on 2019\4\3 0003.
 */

public interface IDetailSingleWebView  {

    void setScrollView(DetailSingleScrollView scrollView);

    boolean canScrollVertically(int direction);

    void customScrollBy(int dy);

    void customScrollTo(int toY);

    int customGetContentHeight();

    int customGetWebScrollY();

    int customComputeVerticalScrollRange();

    void startFling(int vy);

    void setOnScrollBarShowListener(OnScrollBarShowListener listener);


}
