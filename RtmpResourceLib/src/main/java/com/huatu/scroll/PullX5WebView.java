package com.huatu.scroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.gensee.rtmpresourcelib.R;
import com.huatu.widget.X5WebView;


/**
 * Created by Terry
 * Date : 2016/3/7 0007.
 * Email:
 */
public class PullX5WebView extends ScrollerFrameLayout<X5WebView> {

	/*private static final OnRefreshListener<WebView> defaultOnRefreshListener = new OnRefreshListener<WebView>() {

		@Override
		public void onRefresh(PullToRefreshBase<WebView> refreshView) {
			refreshView.getRefreshableView().reload();
		}

	};*/



    public PullX5WebView(Context context) {
        super(context);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        //setOnRefreshListener(defaultOnRefreshListener);
        //mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    public PullX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        //setOnRefreshListener(defaultOnRefreshListener);
        //mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }


    X5WebView webView;
    @Override
    protected X5WebView createRefreshableView(Context context, AttributeSet attrs) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            webView = new InternalWebViewSDK9(context.getApplicationContext());//, attrs
        } else {
            webView = new X5WebView(context.getApplicationContext());//, attrs
        }

        webView.setId(R.id.webview);
        return webView;
    }


    boolean mCanpull=true;
    public void setCanpull(boolean canpull) {
        mCanpull = canpull;
    }

    @Override
    protected boolean isReadyForPullStart() {

        try{
             if(mRefreshableView.getView() instanceof android.webkit.WebView) {
                // Log.e("mRefreshableView","android.webkit.WebView");
                return mCanpull&&(((android.webkit.WebView)mRefreshableView.getView()).getScrollY() == 0);
            }
            else
                return mCanpull&&(mRefreshableView.getWebScrollY() == 0);//.getScrollY()   // Log.e("mRefreshableView","not android.webkit.WebView ");
        }
        catch (Exception e){
             return false;
        }

    }

    @Override
    protected boolean isReadyForPullEnd() {
		/*float exactContentHeight = FloatMath.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
		return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());*/
        return false;
    }



    @TargetApi(9)
    final class InternalWebViewSDK9 extends X5WebView {

        // WebView doesn't always scroll back to it's edge so we add some
        // fuzziness
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

        // WebView seems quite reluctant to overscroll so we use the scale
        // factor to scale it's value
        static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

        public InternalWebViewSDK9(Context arg0){
            super(arg0);
            //setBackgroundColor(85621);
        }

        public InternalWebViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
/*			OverscrollHelper.overScrollBy(PullToRefreshWebView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);*/

            return returnValue;
        }

        //FloatMath
        private int getScrollRange() {
            return (int) Math.max(0, Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale())
                    - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
    }
}
