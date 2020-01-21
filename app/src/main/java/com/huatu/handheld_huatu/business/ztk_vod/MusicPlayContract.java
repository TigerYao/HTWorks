package com.huatu.handheld_huatu.business.ztk_vod;

import com.huatu.handheld_huatu.base.BaseContract;
import com.huatu.music.bean.Music;

/**
 * Created by Administrator on 2019\8\23 0023.
 */

public interface MusicPlayContract {

    interface View extends BaseContract.BaseView{
         void updateProgress(long progress,long max);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void updateNowPlaying(Music music,boolean isInit );
    }

}
