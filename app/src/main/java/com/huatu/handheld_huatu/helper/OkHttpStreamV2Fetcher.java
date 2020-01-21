package com.huatu.handheld_huatu.helper;

/**
 * Created by Administrator on 2019\5\6 0006.
 */

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baijiayun.glide.Priority;
import com.baijiayun.glide.load.DataSource;
import com.baijiayun.glide.load.HttpException;
import com.baijiayun.glide.load.data.DataFetcher;
import com.baijiayun.glide.util.Synthetic;


import com.baijiayun.glide.load.model.GlideUrl;
import com.baijiayun.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Fetches an {@link InputStream} using the okhttp library.
 */
public class OkHttpStreamV2Fetcher implements DataFetcher<InputStream>,
        okhttp3.Callback {
    private static final String TAG = "OkHttpFetcher";
    private final Call.Factory client;
    private final GlideUrl url;
    @Synthetic
    InputStream stream;
    @Synthetic
    ResponseBody responseBody;
    private volatile Call call;
    private com.baijiayun.glide.load.data.DataFetcher.DataCallback<? super InputStream> callback;


    private static final CacheControl FORCE_NETWORK = new CacheControl.Builder().noCache().noStore().build();


    public OkHttpStreamV2Fetcher(Call.Factory client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public void loadData(Priority priority, final com.baijiayun.glide.load.data.DataFetcher.DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        requestBuilder.cacheControl(FORCE_NETWORK);//强制网络
        Request request = requestBuilder.build();
        this.callback = callback;

        call = client.newCall(request);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            call.enqueue(this);
        } else {
            try {
                // Calling execute instead of enqueue is a workaround for #2355, where okhttp throws a
                // ClassCastException on O.
                onResponse(call, call.execute());
            } catch (IOException e) {
                onFailure(call, e);
            } catch (ClassCastException e) {
                // It's not clear that this catch is necessary, the error may only occur even on O if
                // enqueue is used.
                onFailure(call, new IOException("Workaround for framework bug on O", e));
            }
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "OkHttp failed to obtain result", e);
        }

        callback.onLoadFailed(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        responseBody = response.body();
        if (response.isSuccessful()) {
            long contentLength = responseBody.contentLength();
            stream = ContentLengthInputStream.obtain(responseBody.byteStream(), contentLength);
            callback.onDataReady(stream);
        } else {
            callback.onLoadFailed(new HttpException(response.message(), response.code()));
        }
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody.close();
        }
        callback = null;
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}