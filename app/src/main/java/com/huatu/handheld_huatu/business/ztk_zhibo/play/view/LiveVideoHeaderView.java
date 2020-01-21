package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;

/**
 * Created by yaohu on 2018/8/7.
 */

public class LiveVideoHeaderView extends RelativeLayout {
    private ImageView mMoreBtn;
    private ImageView mBackBtn;
    private TextView tvTitle;
    private View.OnClickListener mClickListener;

    public LiveVideoHeaderView(@NonNull Context context) {
        this(context,null);
    }

    public LiveVideoHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setFullScreen(boolean isFullScreen){
//        mMoreBtn.setImageResource(isFullScreen ? R.drawable.video_play_title_more_land : R.drawable.video_play_title_more);
    }

    private void initView(Context ctx){
        View.inflate(ctx, R.layout.layout_live_video_header, this);
        mMoreBtn = (ImageView) findViewById(R.id.live_title_more_btn);
        tvTitle = (TextView) findViewById(R.id.live_title_tv);
        mBackBtn = (ImageView) findViewById(R.id.image_live_back);
        mBackBtn.setOnClickListener(onClickListener);
        mMoreBtn.setOnClickListener(onClickListener);
        mBackBtn.setVisibility(View.INVISIBLE);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onClick(view);
        }
    };

}
