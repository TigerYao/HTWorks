package com.huatu.handheld_huatu.base;

/**
 *
 */

public interface NetObjResponse<T> extends NetErrorResponse{
     // void onError(String message,int type);
      void onSuccess(BaseResponseModel<T> model);
}
