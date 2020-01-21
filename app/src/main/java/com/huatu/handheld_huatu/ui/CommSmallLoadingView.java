package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.utils.StringUtils;


/**
 * Created by cjx on 2016/12/22.
 */
public class CommSmallLoadingView extends LinearLayout {

    ProgressBar mProgressBar;
    TextView    mHitTxtView;
   protected TextView    mRefreshTxtView;
    public CommSmallLoadingView(Context context) {
        super(context);

    }

    public CommSmallLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public  void  setOnRefreshClickListener(OnClickListener onClickListener){
       if(null!=mRefreshTxtView) mRefreshTxtView.setOnClickListener(onClickListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mProgressBar= (ProgressBar)this.findViewById(R.id.progress_loading);
        mHitTxtView= (TextView) this.findViewById(R.id.xi_hite_txt);
        mRefreshTxtView = (TextView) this.findViewById(R.id.xi_tv_tips);
        showLoadingStatus();
    }

    public void showLoadingStatus() {
         show();
         mProgressBar.setVisibility(VISIBLE);
         mHitTxtView.setVisibility(VISIBLE);
         mRefreshTxtView.setVisibility(GONE);
    }

    public void showEmptyStatus(){
        show();
        mProgressBar.setVisibility(GONE);
        mHitTxtView.setVisibility(GONE);
        mRefreshTxtView.setVisibility(VISIBLE);
        mRefreshTxtView.setText(R.string.xs_none_date);

    }

    public void showNetworkError(){
        show();
        mProgressBar.setVisibility(GONE);
        mHitTxtView.setVisibility(GONE);
        mRefreshTxtView.setVisibility(VISIBLE);
        mRefreshTxtView.setText(StringUtils.forHtml(StringUtils.fontColor("#757575","网络出错<br>点击刷新")));
    }

    public  void hide(){
        if(this.getVisibility()!= View.GONE)
            this.setVisibility(View.GONE);


    }

    public  void show(){
        if(this.getVisibility()==View.GONE)  this.setVisibility(View.VISIBLE);
    }

}
