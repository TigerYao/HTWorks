package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.State;

/**
 * Created by AItsuki on 2016/6/15.
 *
 */
public class QQRefreshHeader extends FrameLayout implements RefreshHeader {


    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private ImageView arrowIcon;
    private ProgressBar mProgressBar;
    public QQRefreshHeader(Context context) {
        this(context, null);
    }

    public QQRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化动画
        rotate_up = AnimationUtils.loadAnimation(context , R.anim.rotate_up);
        rotate_down = AnimationUtils.loadAnimation(context , R.anim.rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(context , R.anim.rotate_infinite);
        inflate(context, R.layout.refresh_header, this);

        textView = (TextView) findViewById(R.id.tv_listview_header_state);
        arrowIcon = (ImageView) findViewById(R.id.iv_listview_header_arrow);
        mProgressBar = (ProgressBar)findViewById(R.id.pb_listview_header);
    }

    @Override
    public void reset() {
        textView.setText(getResources().getText(R.string.qq_header_reset));
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
    }

    @Override
    public void pull() {

    }

    @Override
    public void refreshing() {
        arrowIcon.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        textView.setText(getResources().getText(R.string.qq_header_refreshing));
        arrowIcon.clearAnimation();
    }

    @Override
    public void onPositionChange(float currentPos, float lastPos, float refreshPos, boolean isTouch, State state) {
        // 往上拉
        if (currentPos < refreshPos && lastPos >= refreshPos) {
            if (isTouch && state == State.PULL) {
                textView.setText(getResources().getText(R.string.qq_header_pull));
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_down);
            }
            // 往下拉
        } else if (currentPos > refreshPos && lastPos <= refreshPos) {
            if (isTouch && state == State.PULL) {
                textView.setText(getResources().getText(R.string.qq_header_pull_over));
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_up);
            }
        }
    }

    @Override
    public void complete() {
        mProgressBar.setVisibility(INVISIBLE);
    }
}
