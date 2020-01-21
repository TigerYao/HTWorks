package com.huatu.music.player;

import android.os.RemoteException;

/**
 * Created by Administrator on 2019\12\4 0004.
 */

public interface IPlayServiceCallback {

     long getCurrentPosition() ;

     boolean isPlaying() ;

     void playPause();

}
