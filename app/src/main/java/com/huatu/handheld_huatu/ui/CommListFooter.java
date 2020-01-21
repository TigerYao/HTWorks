package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;


/**
 * Listview 上拉加载更多
 * Created by xing
 * Date : 2015/12/28 0028.
 */
public class CommListFooter extends LinearLayout {

    private View mInputContainer;
    private TextView mFootTextView;
    private ProgressBar mProgressBar;

    public CommListFooter(Context context) {
        super(context);

    }

    public CommListFooter(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInputContainer = this.findViewById(R.id.comm_loading_layout);
        mFootTextView=(TextView)this.findViewById(R.id.xi_footer_text);
        mProgressBar=(ProgressBar)this.findViewById(R.id.xi_footer_bar);
    }


    /**
     * 是否显示加载更多
     *
     * @param show
     */
    public void show(boolean show) {
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }
        ViewGroup.LayoutParams params = mInputContainer.getLayoutParams();// mContainer.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            mInputContainer.setLayoutParams(params);
            setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void reset() {
        ViewGroup.LayoutParams params = mInputContainer.getLayoutParams();// mContainer.getLayoutParams();
        if (null != params) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mInputContainer.setLayoutParams(params);
        }
    }

    public void showEnd(){
        if(null!=mFootTextView){
            mProgressBar.setVisibility(GONE);
            mFootTextView.setVisibility(VISIBLE);
        }
        show(true);
    }

    public void showLoading(){
        if(null!=mFootTextView){
            mProgressBar.setVisibility(VISIBLE);
            mFootTextView.setVisibility(GONE);
        }
        show(true);
    }
}
