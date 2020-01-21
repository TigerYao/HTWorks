package com.huatu.handheld_huatu.base;

/**
 *
 */

public interface NetSimpleResponse {
      void onError(String message, int type);
      void onSuccess(SimpleResponseModel model);
}
