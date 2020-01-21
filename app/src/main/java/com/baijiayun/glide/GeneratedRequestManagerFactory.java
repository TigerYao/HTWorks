package com.baijiayun.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.RequestManager;
import com.baijiayun.glide.manager.Lifecycle;
import com.baijiayun.glide.manager.RequestManagerRetriever;
import com.baijiayun.glide.manager.RequestManagerTreeNode;
import com.huatu.handheld_huatu.helper.GlideRequests;


/**
 * Generated code, do not modify
 */
final class GeneratedRequestManagerFactory implements RequestManagerRetriever.RequestManagerFactory {
  @Override
  @NonNull
  public RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle,
                              @NonNull RequestManagerTreeNode treeNode, @NonNull Context context) {
    return new GlideRequests(glide, lifecycle, treeNode, context);
  }
}
