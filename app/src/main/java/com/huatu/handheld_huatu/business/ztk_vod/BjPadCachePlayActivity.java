package com.huatu.handheld_huatu.business.ztk_vod;

import com.huatu.handheld_huatu.utils.ScreenRotateUtil;

/**
 * Created by Administrator on 2019\5\29 0029.
 */

public class BjPadCachePlayActivity extends BJRecordPlayActivity {
    ScreenRotateUtil mScreenRotateUtil;
    @Override
    protected void registerScreenRoate(){
        if(mScreenRotateUtil==null){
            mScreenRotateUtil=new ScreenRotateUtil(this);
            mScreenRotateUtil.start(this);
        }
    }

    @Override
    public void onResume(){
        if(mScreenRotateUtil!=null){
            mScreenRotateUtil.start(this);
        }
        super.onResume();
    }

     @Override
     public void onPause(){
        if(mScreenRotateUtil!=null){
            mScreenRotateUtil.stop();
        }
        super.onPause();
     }
}
