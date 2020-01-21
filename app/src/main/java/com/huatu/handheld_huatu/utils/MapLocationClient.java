package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.helper.LocationForegoundService;
import com.huatu.handheld_huatu.network.RetrofitManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * @author liuzhe
 * @date 2019/4/9
 */
public class MapLocationClient {

   // private Context mContext;

    //声明AMapLocationClient类对象
    public AMapLocationClient mlocationClient = null;
    private AMapLocationClientOption mlocationOption = null;
    Intent mServiceIntent;

    public MapLocationClient(Context mContext) {

        mServiceIntent = new Intent();
        mServiceIntent.setClass(mContext,LocationForegoundService.class);
       // this.mContext = mContext;

    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (null != aMapLocation&&(aMapLocation.getErrorCode() == 0)) {
                getLocationData(aMapLocation);
                LogUtils.e("onLocationChanged",GsonUtil.GsonString(aMapLocation));
            }
        }
    };


    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 初始化
     */
    public void init() {

        if (mlocationClient == null)
        {
            mlocationClient = new AMapLocationClient(UniApplicationContext.getContext());
            mlocationOption = getDefaultOption();
            //设置定位参数
            mlocationClient.setLocationOption(mlocationOption);
            // 设置定位监听
            mlocationClient.setLocationListener(mLocationListener);
            if (null != mServiceIntent) {
                UniApplicationContext.getContext().startService(mServiceIntent);
            }
            // 启动定位
            mlocationClient.startLocation();
        }

    }

    public void getLocationData(AMapLocation aMapLocation) {
        int locationType = aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        double latitude = aMapLocation.getLatitude();//获取纬度
        double longitude = aMapLocation.getLongitude();//获取经度
        float accuracy = aMapLocation.getAccuracy();//获取精度信息
        String address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        String country = aMapLocation.getCountry();//国家信息
        String province = aMapLocation.getProvince();//省信息
        String city = aMapLocation.getCity();//城市信息
        String district = aMapLocation.getDistrict();//城区信息
        String street = aMapLocation.getStreet();//街道信息
        String streetNum = aMapLocation.getStreetNum();//街道门牌号信息
        String cityCode = aMapLocation.getCityCode();//城市编码
        String adCode = aMapLocation.getAdCode();//地区编码
        String aoiName = aMapLocation.getAoiName();//获取当前定位点的AOI信息
        String buildingId = aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
        String floor = aMapLocation.getFloor();//获取当前室内定位的楼层
        int gpsAccuracyStatus = aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态


        //获取定位时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(aMapLocation.getTime());
        df.format(date);

        setReportPosition(city, district, streetNum, address, province, street);

//        Log.i("getLocationData", "city = " + city + " district = " + district +  " streetNum = " + streetNum
//                + " address = " + address + " province = " + province + " street = " + street);

        stopLocation();
        onDestroy();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if(null!=mlocationClient)
           mlocationClient.stopLocation();
    }

    /**
     * 销毁本地定位服务
     */
    public void onDestroy() {
        //销毁定位客户端，同时销毁本地定位服务。
        if(null!=mlocationClient){
            mlocationClient.onDestroy();
            mlocationClient=null;
        }
        if(null != mServiceIntent){
            UniApplicationContext.getContext().stopService(mServiceIntent);
        }
    }



    /**
     * 上报地理位置信息
     *
     * @param city         param市名称
     * @param district     param区名称
     * @param number       门牌号
     * @param positionName 详细名称
     * @param province     param省名称
     * @param street       param 街道
     * @return
     */
    public void setReportPosition(String city,
                                  String district,
                                  String number,
                                  String positionName,
                                  String province,
                                  String street) {


        Observable<BaseResponseModel<Object>> baseResponseModelObservable = RetrofitManager.getInstance().getService().
                setReportPosition(city, district, number, positionName, province, street);

        baseResponseModelObservable.subscribeOn(Schedulers.io())
                 .subscribe(new Subscriber<BaseResponseModel<Object>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();


                    }

                    @Override
                    public void onNext(BaseResponseModel<Object> objectBaseResponseModel) {

                    }
                });



    }
}
