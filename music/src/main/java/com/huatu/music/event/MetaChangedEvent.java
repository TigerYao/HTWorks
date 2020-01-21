package com.huatu.music.event;

import com.huatu.music.bean.Music;

/**
 * Created by Administrator on 2019\8\20 0020.
 */

public class MetaChangedEvent {
    public  Music mMusic;
    public MetaChangedEvent(Music music){
        mMusic=music;
    }
}
