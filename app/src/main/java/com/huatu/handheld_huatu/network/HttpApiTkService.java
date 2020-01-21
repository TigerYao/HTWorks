package com.huatu.handheld_huatu.network;


import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.lessons.bean.PurchasedBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 */
public interface HttpApiTkService {
    //https://apitk.huatu.com
    /**
     */
    @GET("/v3/checkIsBuyWithId.php")
    Observable<BaseResponseModel<PurchasedBean>> checkIsBuyWithId(
            @Query("username") String username,
            @Query("rid") String rid
    );
}
