package com.huatu.handheld_huatu.business.ztk_vod;


import android.view.View;

/**
 * Created by cjx on 2018\7\23 0023.
 */

public interface BJCustomCenterView  {

      enum ActionType{ SHOWSPEED,SHOWDEFINITION,SHOWAUDIOMODE;}

      void showActionPanel(ActionType actionType);

      void  showProgressPercentSlide(int oldProgress,int curProgress,int duration,boolean isDragEnd);

      void  showPauseStatus(boolean isPaused);

}
