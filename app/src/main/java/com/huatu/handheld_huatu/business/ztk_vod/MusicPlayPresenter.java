package com.huatu.handheld_huatu.business.ztk_vod;


import com.huatu.music.bean.Music;
import com.huatu.music.player.MusicPlayerService;
import com.huatu.music.player.playback.PlayProgressListener;
import  com.huatu.handheld_huatu.base.BasePresenter;
/**
 * Created by cjx on 2019\8\23 0023.
 */

public class MusicPlayPresenter extends BasePresenter<MusicPlayContract.View>
        implements MusicPlayContract.Presenter,PlayProgressListener {


    public void  onProgressUpdate(long position,long duration) {
        mView.updateProgress(position, duration);
    }

    public void attachView( MusicPlayContract.View view) {
        super.attachView(view);
        MusicPlayerService.addProgressListener(this);
    }

    public void detachView() {
        super.detachView();
        MusicPlayerService.removeProgressListener(this);
    }


    public void  updateNowPlaying(Music music,boolean isInit) {
      /*  mView?.showNowPlaying(music)
        CoverLoader.loadBigImageView(mView?.context, music) { bitmap ->
                doAsync {
            val blur = ImageUtils.createBlurredImageFromBitmap(bitmap, 12)
            uiThread {
                mView?.setPlayingBg(blur, isInit)
                mView?.setPlayingBitmap(bitmap)
            }
        }
        */
    }
}
