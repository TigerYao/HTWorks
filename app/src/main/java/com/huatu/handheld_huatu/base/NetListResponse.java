package com.huatu.handheld_huatu.base;

/**
 *
 */

public interface NetListResponse<T> extends NetErrorResponse {
     // void onError(String message, int type);
      void onSuccess(BaseListResponseModel<T> model);
}
