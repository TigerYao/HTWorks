package com.huatu.autoapi.auto_api.factory;

import com.huatu.autoapi.auto_api.AddUtilApi;

/**
 * com.huatu.handheld_huatu.AddUtil api Class's factory Interface
 */
public interface AddUtilApiFactory {
  AddUtilApi newInstance(int data1, int data2);

  AddUtilApi newInstance(int data);

  AddUtilApi getInstance(int data1, int data2);
}
