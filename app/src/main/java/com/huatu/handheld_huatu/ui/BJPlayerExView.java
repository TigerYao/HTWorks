package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.BJBottomViewImpl;
import com.huatu.handheld_huatu.business.ztk_vod.BJCenterViewExPresener;
import com.huatu.handheld_huatu.business.ztk_vod.BJCenterViewPresenterCopy;
import com.huatu.handheld_huatu.business.ztk_vod.BJCustomCenterView;
import com.huatu.handheld_huatu.business.ztk_vod.BJTopViewImpl;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.PlayRateDialogFragment;
import com.huatu.handheld_huatu.listener.SimpleBjPlayerStatusListener;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.music.player.PlayManager;
import com.huatu.widget.MyRadioGroup;

/**
 * Created by cjx on 2018\8\1 0001.
 */

public class BJPlayerExView extends BJPlayerView {


    BJCenterViewPresenterCopy mBjCenterViewPresenter;
    BJBottomViewImpl mBjBottomViewPresenter;
    BJTopViewImpl mBjTopViewPresenter;
    private SimpleBjPlayerStatusListener mBjPlayerStatusListener;
    protected float[] bjPlaySpeedLists = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};

    public BJPlayerExView(Context var1) {
        super(var1);
    }

    public BJPlayerExView(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public BJPlayerExView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    public void setBjPlayerStatusListener(SimpleBjPlayerStatusListener mBjPlayerStatusListener) {
        this.mBjPlayerStatusListener = mBjPlayerStatusListener;
    }

    private void setPlayerViewListener() {
        this.setPlayerTapListener(new BJPlayerView.OnPlayerTapListener() {
            @Override
            public void onSingleTapUp(MotionEvent motionEvent) {
               /* if (listview_ml.getVisibility() == View.VISIBLE) {
                    listview_ml.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onDoubleTap(MotionEvent motionEvent) {
                if (BJPlayerExView.this != null) {
                    if (BJPlayerExView.this.isPlaying()) {
                        BJPlayerExView.this.pauseVideo();
                        //  btnPlay.setImageResource(R.mipmap.vod_play_icon);
                    } else {
                        BJPlayerExView.this.playVideo();
                        //  btnPlay.setImageResource(R.mipmap.vod_pause_icon);
                    }
                }
            }
        });
        this.setOnPlayerViewListener(new SimpleBjPlayerStatusListener() {
            @Override
            public void onVideoPrepared(BJPlayerView playerView) {
                super.onVideoPrepared(playerView);
                //TODO: 视频信息初始化结束
                // LogUtils.e("onVideoPrepared,"+mPlayerView.getVideoDefinition());

                if (mBjCenterViewPresenter != null) mBjCenterViewPresenter.setCoverImageShow(false);
                if (mBjBottomViewPresenter != null) {
                    mBjBottomViewPresenter.setLayoutShow(true);
                  /*  if(!mIsLocalVideo)
                        mBjBottomViewPresenter.showLearnPoint(mPlayingCourseWare,getSubscription());*/
                }
                if(mBjPlayerStatusListener != null)
                    mBjPlayerStatusListener.onVideoPrepared(playerView);
               /* if(mBjTopViewPresenter!=null){
                    mBjTopViewPresenter.loadInitDanMu(mCourseId,mPlayingCourseWare,getSubscription());
                }*/
            }

            @Override
            public String getVideoTokenWhenInvalid() {
                return "";
            }

            @Override
            public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {
                //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
               /* if(currenttTitlePosition<0||currenttTitlePosition>=ArrayUtils.size(livelession)){
                    return;
                }
                if (livelession.get(currenttTitlePosition).getIsComment() == 0) {
                    if (!sharedPreferences.getBoolean(livelession.get(currenttTitlePosition).rid + "judge", false)) {
                        CourseJudgeActivity.newInstance(BJRecordPlayActivity.this,
                                classid, livelession.get(currenttTitlePosition).rid);
                    }
                }
                savePlayProgress(currenttTitlePosition,true,true);*/
                // savePlayProgress(true,true);
                //mCurrentPlayTime = 0;
                if (mBjPlayerStatusListener != null)
                    mBjPlayerStatusListener.onPlayCompleted(playerView, item, nextSection);
                LogUtils.e("onPlayCompleted", "onPlayCompleted");
            }

            @Override
            public void onPause(BJPlayerView playerView) {
                super.onPause(playerView);
                // if(mBjTopViewPresenter!=null) mBjTopViewPresenter.pause();
                if(mBjPlayerStatusListener != null)
                    mBjPlayerStatusListener.onPause(playerView);
            }

            @Override
            public void onPlay(BJPlayerView playerView) {
                super.onPlay(playerView);
                //if(mBjTopViewPresenter!=null) mBjTopViewPresenter.resume();
                if(mBjPlayerStatusListener != null)
                    mBjPlayerStatusListener.onPlay(playerView);
            }

            @Override
            public void onSeekComplete(BJPlayerView playerView, int position) {
                //TODO: 拖动进度条
                // mSeekPostion = -1;
                LogUtils.e("onSeekComplet", position + "");
                //if(mBjTopViewPresenter!=null) mBjTopViewPresenter.start(position,true);
                //if(mBjTopViewPresenter!=null) mBjTopViewPresenter.start(position*1000,true);
            }

            @Override
            public void onError(BJPlayerView playerView, int code) {
                super.onError(playerView, code);
                if (code == -2) {
                    playerView.setEnableNetWatcher(false);
                }
            }
        });
    }

    public void setVideoSize(long videoSize){
        if(mBjCenterViewPresenter!=null) {
            mBjCenterViewPresenter.resetPlaySpeed();
            mBjCenterViewPresenter.setVideoSize(videoSize);
        }
    }

    public void initBJYPlayer() {
        //以下三个方法分别设置底部、顶部和中部界面
        mBjBottomViewPresenter = new BJBottomViewImpl(this.getBottomView(), false) {
            @Override
            public void setDuration(int duration) {
                super.setDuration(duration);
            }

            @Override
            public void setCurrentPosition(int position) {
                super.setCurrentPosition(position);
            }
        };
    /*    mBjBottomViewPresenter.setOnLessionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCourseListDialog==null){
                    mCourseListDialog=new PopRightCourseListDialog(v.getContext(), StringUtils.parseLong(mCourseId),getSubscription());
                    mCourseListDialog.setOnSubItemClickListener(BJRecordPlayActivity.this);
                    mCourseListDialog.setCourseWarelist(mRecordLessionlist);
                }
                mCourseListDialog.setCurrentSelectIndex(0);
                mCourseListDialog.show();
            }
        });*/

        // mBjBottomViewPresenter.setOnShowInClassListener(this);
        this.setBottomPresenter(mBjBottomViewPresenter);
        mBjTopViewPresenter = new BJTopViewImpl(this.getTopView(), this,false) {
           /* @Override
            public void setTitle(String title) {
                if (isQuickPlay) {
                    if (!TextUtils.isEmpty(mCourseName))
                        title = mCourseName;
                } else {
                    if (mPlayingCourseWare!=null) {
                        title=mPlayingCourseWare.title;
                    }
                }
                LogUtils.e("setTitle", title + "");
                super.setTitle(title);
            }*/
        };
        mBjTopViewPresenter.setPortraitPopListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showSharePopWindow(v);
            }
        });

        this.setTopPresenter(mBjTopViewPresenter);
        this.getTopViewPresenter().setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBjCenterViewPresenter = new BJCenterViewExPresener(this.getCenterView()) {
            @Override
            public void setActionText(BJCustomCenterView.ActionType actionType, String value) {
//                if (mBjBottomViewPresenter != null)
//                    mBjBottomViewPresenter.setActionText(actionType, value);
            }

            @Override
            public void changeRateAction(){
                PlayRateDialogFragment ratefragment = null;
                if(PlayManager.mService!=null){
                    if(PlayManager.isPause()||PlayManager.isPlaying()){
                        float playRate=PlayManager.getPlayRate();
                         ratefragment=PlayRateDialogFragment.getInstance(playRate);
                        ratefragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "rateplay");
                    }
                }else {
                    float playRate=getVideoRateInFloat();
                     ratefragment=PlayRateDialogFragment.getInstance(playRate);
                    ratefragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "rateplay");
                }
                if(ratefragment != null)
                ratefragment.setCheckedListener(new MyRadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                        float rateSpeed = bjPlaySpeedLists[checkedId];
                        if(PlayManager.mService!=null){
                            LogUtils.e("getPlayRate0",rateSpeed+"");
                            PlayManager.setPlayRate(rateSpeed);
                            LogUtils.e("getPlayRate",PlayManager.getPlayRate()+"");
                        }
                        if(null!=mBjCenterViewPresenter)
                            mBjCenterViewPresenter.changeRate(rateSpeed);
                    }
                });
            }
        };
        mBjCenterViewPresenter.setRightMenuHidden(false);
        this.setCenterPresenter(mBjCenterViewPresenter);
        mBjBottomViewPresenter.setIPlayerCenterContact(mBjCenterViewPresenter);


       /* mStartPlayBtn=(Button) mPlayerView.findViewById(R.id.start_play_btn);
        mStartPlayBtn.setOnClickListener(this);

        mPlayerView.findViewById(R.id.danmu_input_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mPlayingCourseWare==null||mPlayingCourseWare.coursewareId==0) return;
                if(mDanMuDialogbuilder==null){

                    mDanMuDialogbuilder = new DanmakuInputDialogBuilder(v.getContext(),BJRecordPlayActivity.this);
                    mDanMuDialogbuilder.setOnDanmaSendListener(BJRecordPlayActivity.this);
                    mDanMuDialogbuilder.setCourseId(StringUtils.parseLong(mCourseId))
                            .setLessionId(mPlayingCourseWare.coursewareId).create(R.style.QMUI_TipDialog).show();
                }else {
                    mDanMuDialogbuilder.setLessionId(mPlayingCourseWare.coursewareId);
                    mDanMuDialogbuilder.show();
                }

            }
        });*/

        //回调接口为播放器状态改变之后向上层app的通知，可以在每个回调方法中实现自己的业务逻辑
        setPlayerViewListener();

        //初始化partnerId，第一个参数换成您的partnerId
        this.initPartner(Constant.BAIJIAYNN_PARTNER_KEY, BJPlayerView.PLAYER_DEPLOY_ONLINE);
        //设置片头
        this.setHeadTailPlayMethod(BJPlayerView.HEAD_TAIL_PLAY_NONE);
        mBjBottomViewPresenter.disableDanmuInput();
        //第一个参数为百家云后台配置的视频id，第二个参数为视频token
        // playerView.setVideoId(videoId, videoToken);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mBjTopViewPresenter != null) {
            mBjTopViewPresenter.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBjTopViewPresenter != null) {
            mBjTopViewPresenter.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (null != mBjBottomViewPresenter) {
            mBjBottomViewPresenter.setIPlayerCenterContact(null);
            mBjBottomViewPresenter.setOnShowInClassListener(null);
            mBjBottomViewPresenter = null;
        }
        if (mBjCenterViewPresenter != null) {
            mBjCenterViewPresenter.resetPlaySpeed();
            mBjCenterViewPresenter = null;
        }

        if (mBjTopViewPresenter != null) {
            mBjTopViewPresenter.destory();
            mBjTopViewPresenter = null;
        }
        if (mBjBottomViewPresenter != null) {
            mBjBottomViewPresenter.resetPoint();
            mBjBottomViewPresenter = null;
        }
        this.setPlayerTapListener(null);
        this.setOnPlayerViewListener(null);
        super.onDestroy();
    }

    public void setCoverImg(String classScaleimg){
        if (!TextUtils.isEmpty(classScaleimg))
            ImageLoad.displaynoCacheImage(getContext(), R.drawable.trans_bg,classScaleimg,mBjCenterViewPresenter.getCoverImageView());

    }


    public BJCenterViewPresenterCopy getBjCenterViewPresenter() {
        return mBjCenterViewPresenter;
    }

}
