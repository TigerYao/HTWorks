package com.huatu.handheld_huatu.business.essay.video;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;

import com.baijiahulian.player.BJPlayerView;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.PlayRateDialogFragment;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.widget.MyRadioGroup;

/**
 * 视频方向 orientation 参考android类Configuration的常量
 * *                    Configuration.ORIENTATION_LANDSCAPE 横向
 * *                    Configuration.ORIENTATION_PORTRAIT  纵向
 */

public class CheckBJPlayerView extends BJPlayerView {

    private Context context;

    protected BJCheckVideoControlTopView mBJTopController;
    protected BJCheckVideoControlCenterView mBJCenterController;
    private BJCheckVideoControlBottomView mBJBottomController;

    public interface OnSpeedShowListener {
        void onPlayRateClick();
    }

    //private String[] speedStr = {"0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x"};
    protected float[] bjPlaySpeedLists = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};

    public void initSpeedShowListener() {
        if (null != mBJCenterController) {
            mBJCenterController.setOnSpeedShowListener(new CheckBJPlayerView.OnSpeedShowListener() {
                @Override
                public void onPlayRateClick() {
                    Activity contenxt = CommonUtils.getActivityFromView(CheckBJPlayerView.this);
                    if (null != contenxt && (contenxt instanceof FragmentActivity)) {
                        float playRate = CheckBJPlayerView.this.getVideoRateInFloat();
                        final PlayRateDialogFragment ratefragment = PlayRateDialogFragment.getInstance(playRate);
                        ratefragment.show(((FragmentActivity) contenxt).getSupportFragmentManager(), "rateplay");
                        ratefragment.setCheckedListener(new MyRadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                                mBJCenterController.setSpeed(bjPlaySpeedLists[checkedId]);
                                CheckBJPlayerView.this.setVideoRate(bjPlaySpeedLists[checkedId]);
                                ratefragment.dismiss();
                            }
                        });
                    }
                }
            });
        }
    }

    protected boolean isFullScreen;       // 是否充满屏幕了，各处的view样式根据这个字段进行更改，而不以横竖屏判断

    public CheckBJPlayerView(Context context) {
        this(context, null);
    }

    public CheckBJPlayerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CheckBJPlayerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        init();
    }

    protected void init() {
        // 视频相关
        // 初始化partnerId，第一个参数换成您的partnerId
        this.initPartner(Constant.BAIJIAYNN_PARTNER_KEY, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        initController();
    }

    protected void initController() {
        // 以下三个方法分别设置底部、顶部和中部界面
        mBJTopController = new BJCheckVideoControlTopView(this.getTopView(), this);
        this.setTopPresenter(mBJTopController);

        mBJCenterController = new BJCheckVideoControlCenterView(this.getCenterView(), this);
        this.setCenterPresenter(mBJCenterController);

        mBJBottomController = new BJCheckVideoControlBottomView(this.getBottomView(), this);
        this.setBottomPresenter(mBJBottomController);
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    /**
     * 如果是全屏就设置成当前方向的屏幕高。否则就是16/9
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (!isFullScreen) {        // 不是全屏就 16/9
            int specSize = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.makeMeasureSpec((int) (specSize * 0.563), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, height);
        } else {                    // 全屏就是当前屏幕方向的高度
            int heightPx;
          /*  Configuration mConfiguration = context.getResources().getConfiguration();       // 获取设置的配置信息
            if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                heightPx = ;
            } else {
                heightPx = DisplayUtil.getScreenHeight();
            }*/
            heightPx = Math.min(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight());//
            int height = MeasureSpec.makeMeasureSpec(heightPx, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, height);
        }
    }

    /**
     * Configuration.ORIENTATION_PORTRAIT    肖像画方向，竖屏
     * Configuration.ORIENTATION_LANDSCAPE   风景画方向，横屏
     */
    public void setPlayerControlConfig(int orientation) {
        mBJTopController.setOrientation(orientation);
        mBJCenterController.setOrientation(orientation);
        mBJBottomController.setOrientation(orientation);
    }
}
