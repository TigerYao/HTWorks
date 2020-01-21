package com.huatu.handheld_huatu.network;


import com.huatu.handheld_huatu.mvpmodel.VideoCloudDirectoryBean;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 */
public interface HttpShopService {
    //https://shop.huatu.com/
    /**
     * 获取云网视频目录
     *
     * @param bno 视频编号
     * @return
     */
    @GET("app_yw/yw_book_catalog")
    Observable<VideoCloudDirectoryBean> getVideoCloudDirectoryBean(
            @Query("bno") String bno,
            @Query("page") int page
    );
}
