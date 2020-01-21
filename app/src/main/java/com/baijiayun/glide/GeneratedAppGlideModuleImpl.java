package com.baijiayun.glide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.model.GlideUrl;
import com.baijiayun.glide.request.RequestOptions;
import com.huatu.handheld_huatu.helper.OkHttpUrlV2Loader;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("deprecation")
final class GeneratedAppGlideModuleImpl extends GeneratedAppGlideModule {
 // private final MyAppModule appGlideModule;

  GeneratedAppGlideModuleImpl() {
  //  appGlideModule = new MyAppModule();
    if (Log.isLoggable("Glide", Log.DEBUG)) {
      Log.d("Glide", "Discovered AppGlideModule from annotation: com.example.hanks.photoviewer.MyAppModule");
      Log.d("Glide", "Discovered LibraryGlideModule from annotation: com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule");
    }
  }

  @Override
  public void applyOptions(@NonNull Context context, @NonNull com.baijiayun.glide.GlideBuilder builder) {
   // appGlideModule.applyOptions(context, builder);
    builder.setDefaultRequestOptions(
            new RequestOptions().skipMemoryCache(true).disallowHardwareConfig());
  }

  @Override
  public void registerComponents(@NonNull Context context, @NonNull com.baijiayun.glide.Glide glide,
      @NonNull Registry registry) {
  /*  new OkHttpLibraryGlideModule().registerComponents(context, glide, registry);
    appGlideModule.registerComponents(context, glide, registry);*/
     registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlV2Loader.Factory());
  }

  @Override
  public boolean isManifestParsingEnabled() {
    return false;
  }

  @Override
  @NonNull
  public Set<Class<?>> getExcludedModuleClasses() {
    return Collections.emptySet();
  }

  @Override
  @NonNull
  GeneratedRequestManagerFactory getRequestManagerFactory() {
    return new GeneratedRequestManagerFactory();
  }
}
