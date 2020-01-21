package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;

/**
 * share preference存储操作类
 *
 * @author zhaodongdong
 */
public class PrefStore {
    private static final String KEY_FONT_SIZE = "font.size";
    private static final String KEY_MAC_ADDRESS = "mac.address";
    private static final String KEY_SCREEN_HEIGHT = "screen.height";
    private static final String KEY_SCREEN_WIDTH = "screen.width";
    private static final String KEY_VERSION_NAME = "version.name";
    private static final String KEY_VERSION_CODE = "version.code";
    private static final String KEY_WINDOW_THEME = "window.theme";
    private static final String KEY_SIM_OPERATOR_NAME = "sim.operator.name";
    private static final String KEY_DEVICE_ID = "device.id";
    private static final String KEY_DEVICE_MANUFACTURER = "device.manufacturer";
    private static final String KEY_DEVICE_VERSION = "device.version";
    private static final String KEY_DEVICE_MODEL = "device.model";
    private static final String KEY_USER_ACCOUNT = "user.account";
    private static final String KEY_USER_PASSWORD = "user.password";
    private static final String KEY_USER_NAME = "user.name";
    private static final String KEY_USER_HEAD_ICON = "user.head.icon";
    private static final String KEY_USER_ID = "user.id";
    private static final String KEY_USER_PHONE_NUM = "user.phone.num";
    private static final String KEY_USER_LOCATION = "user.location";
    private static final String KEY_REMEMBER_USER_LOGIN = "remember.user.login";
    private static final String KEY_SPLASH_CONFIG = "splash.config";
    private static final String KEY_STATE_FIRST_RUN = "state.first.guide";
    private static final String KEY_LAST_LOGIN_TIME = "login.time";
    private static final String KEY_LOGIN_STATE = "login.state";
    private static final String PREFERENCE_COMMON = "preference.common";
    private static final String PREFERENCE_SETTING = "preference.setting";
    private static final String PREFERENCE_USER = "preference.user";
    private static final String STORAGE_STATE = "storage.state";
    private static final String LOCATION_STATE = "location.state";
    private static final String PHONE_STATE = "phone.state";
    private static final String SENSORS_STATE = "sensors.state";
    private static final String CAMERA_STATE = "camera.state";
    private static final String CALENDAR_STATE = "calendar.state";
    private static final String CONTACTS_STATE = "contacts.state";
    private static final String SMS_STATE = "sms.state";
    private static final String MICROPHONE_STATE = "microphone.state";
    private static final String PERMISSION_STORAGE = "permission_storage_never";
    private static final String ADVERTISE_HOME_ID = "advertise.home.id";

   /* settingSharePreference 定义开始*/
    private static SharedPreferences mSettingSharedPref;

    private static void init(){
        if(mSettingSharedPref == null){
            synchronized (PrefStore.class){
                if(mSettingSharedPref == null){
                    mSettingSharedPref = getApp().getSharedPreferences(PREFERENCE_SETTING, Context.MODE_PRIVATE);;
                }
            }
        }
    }

    public static SharedPreferences userReadPreference() {
        return getApp().getSharedPreferences(UserInfoUtil.userName, Context.MODE_PRIVATE);
    }

    /**
     * @return setting preference
     */
    private static SharedPreferences settingPreference() {
        init();
        return mSettingSharedPref;
    }

    private PrefStore() { }

    public static String getSettingString(String key, String defValue){
          return settingPreference().getString(key, defValue);
    }

    public static boolean putSettingString(String key, String value){
         return settingPreference().edit().putString(key,value).commit();
    }

    public static int  getSettingInt(String key,int  defValue){
        return settingPreference().getInt(key, defValue);
    }

    public static boolean putSettingInt(String key, int value){
        return settingPreference().edit().putInt(key,value).commit();
    }

    public static void removeSettingkey(String key){
          settingPreference().edit().remove(key).commit();
    }

    public static boolean putUserSettingInt(String key, int value){
        return settingPreference().edit().putInt(UserInfoUtil.userId+"_"+key,value).commit();
    }

    public static int getUserSettingInt(String key,int  defValue){
        return settingPreference().getInt(UserInfoUtil.userId+"_"+key, defValue);
    }
    /* settingSharePreference 定义结束*/


    /*2G/3G/4G环境缓存设置开始*/
    public static boolean canDownloadIn3G() {
        return settingPreference().getBoolean("allow_net_down3G", false);
    }

    public static void setCanDownloadIn3G(boolean canDown) {
        settingPreference().edit().putBoolean("allow_net_down3G", canDown).apply();
    }
     /*设置结束*/



    public static int getAdvertiseHomeId() {
        return settingPreference().getInt(ADVERTISE_HOME_ID, 0);
    }

    public static void setAdvertiseHomeId(int id) {
        settingPreference().edit().putInt(ADVERTISE_HOME_ID, id).apply();
    }

    public static int getPermissionStorageState() {
        return settingPreference().getInt(PERMISSION_STORAGE, 0);
    }

    public static void setPermissionStorage(int state) {
        settingPreference().edit().putInt(PERMISSION_STORAGE, state).apply();
    }


    /**
     * 获取存储权限状态
     *
     * @return int
     */
    public static int getStorageState() {
        return settingPreference().getInt(STORAGE_STATE, 0);
    }

    /**
     * 设置存储权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setStorageState(int state) {
        settingPreference().edit().putInt(STORAGE_STATE, state).commit();
    }

    /**
     * 获取位置权限状态
     *
     * @return int
     */
    public static int getLocationState() {
        return settingPreference().getInt(LOCATION_STATE, 0);
    }

    /**
     * 设置位置权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setLocationState(int state) {
        settingPreference().edit().putInt(LOCATION_STATE, state).commit();
    }

    /**
     * 获取电话权限状态
     *
     * @return int
     */
    public static int getPhoneState() {
        return settingPreference().getInt(PHONE_STATE, 0);
    }

    /**
     * 设置电话权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setPhoneState(int state) {
        settingPreference().edit().putInt(PHONE_STATE, state).commit();
    }

    /**
     * 获取传感器权限状态
     *
     * @return int
     */
    public static int getSensorsState() {
        return settingPreference().getInt(SENSORS_STATE, 0);
    }

    /**
     * 设置传感器权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setSensorsState(int state) {
        settingPreference().edit().putInt(SENSORS_STATE, state).commit();
    }

    /**
     * 获取摄像头权限状态
     *
     * @return int
     */
    public static int getCameraState() {
        return settingPreference().getInt(CAMERA_STATE, 0);
    }

    /**
     * 设置摄像头权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setCameraState(int state) {
        settingPreference().edit().putInt(CAMERA_STATE, state).commit();
    }

    /**
     * 获取日程权限状态
     *
     * @return int
     */
    public static int getCalendarState() {
        return settingPreference().getInt(CALENDAR_STATE, 0);
    }

    /**
     * 设置日程权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setCalendarState(int state) {
        settingPreference().edit().putInt(CALENDAR_STATE, state).commit();
    }

    /**
     * 获取联系人权限状态
     *
     * @return int
     */
    public static int getContactsState() {
        return settingPreference().getInt(CONTACTS_STATE, 0);
    }

    /**
     * 设置联系人权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setContactsState(int state) {
        settingPreference().edit().putInt(CONTACTS_STATE, state).commit();
    }

    /**
     * 获取SMS权限状态
     *
     * @return int
     */
    public static int getSmsState() {
        return settingPreference().getInt(SMS_STATE, 0);
    }

    /**
     * 设置短信权限状态
     *
     * @param state 1为允许,0为不允许
     */
    public static void setSmsState(int state) {
        settingPreference().edit().putInt(SMS_STATE, state).commit();
    }

    /**
     * 获取Mic权限状态
     *
     * @return int
     */
    public static int getMicroPhoneState() {
        return settingPreference().getInt(MICROPHONE_STATE, 0);
    }

    /**
     * 设置mic权限状态
     *
     * @param state 0为不允许,1为允许
     */
    public static void setMicroPhoneState(int state) {
        settingPreference().edit().putInt(MICROPHONE_STATE, state).commit();
    }

    /**
     * @return font size
     */
    public static int getFontSize() {
        return settingPreference().getInt(KEY_FONT_SIZE, 0);
    }

    /**
     * @param fontSize 字体大小
     */
    public static void setFontSize(int fontSize) {
        settingPreference().edit().putInt(KEY_FONT_SIZE, fontSize).commit();
    }

    /**
     * @return mac address
     */
    public static String getMacAddress() {
        return settingPreference().getString(KEY_MAC_ADDRESS, "");
    }

    /**
     * @param macAddress mac address
     */
    public static void setMacAddress(String macAddress) {
        settingPreference().edit().putString(KEY_MAC_ADDRESS, macAddress).commit();

    }

    /**
     * @return versionName
     */
    public static String getVersionName() {
        return settingPreference().getString(KEY_VERSION_NAME, "");
    }

    /**
     * @param versionName versionName
     */
    public static void setVersionName(String versionName) {
        settingPreference().edit().putString(KEY_VERSION_NAME, versionName).commit();
    }

    /**
     * @return versionCode
     */
    public static String getVersionCode() {
        return settingPreference().getString(KEY_VERSION_CODE, "");
    }

    /**
     * @param versionCode 版本号
     */
    public static void setVersionCode(String versionCode) {
        settingPreference().edit().putString(KEY_VERSION_CODE, versionCode).commit();
    }

    /**
     * @return 1为黑夜模式, 0为白天模式
     */
    public static int getAppTheme() {
        return settingPreference().getInt(KEY_WINDOW_THEME, 0);
    }

    /**
     * @param theme 1为黑夜模式,0为白天模式
     */
    public static void setAppTheme(int theme) {
        settingPreference().edit().putInt(KEY_WINDOW_THEME, theme).commit();
    }

    /**
     * @return 运行商名称
     */
    public static String getOperatorName() {
        return settingPreference().getString(KEY_SIM_OPERATOR_NAME, "");
    }

    /**
     * @param name 运行商名称
     */
    public static void setSimOperatorName(String name) {
        settingPreference().edit().putString(KEY_SIM_OPERATOR_NAME, name).commit();
    }

    /**
     * @return 设备IMEI
     */
    public static String getDeviceId() {
        return settingPreference().getString(KEY_DEVICE_ID, "");
    }

    /**
     * @param deviceId 设备IMEI
     */
    public static void setDeviceId(String deviceId) {
        settingPreference().edit().putString(KEY_DEVICE_ID, deviceId).commit();
    }

    /**
     * @param manufacturer 设备厂商
     */
    public static void setDeviceManufacturer(String manufacturer) {
        settingPreference().edit().putString(KEY_DEVICE_MANUFACTURER, manufacturer).commit();
    }

    /**
     * @param version 系统版本
     */
    public static void setDeviceVersion(String version) {
        settingPreference().edit().putString(KEY_DEVICE_VERSION, version).commit();
    }

    /**
     * 设置手机型号
     *
     * @param model 手机型号
     */
    public static void setDeviceModel(String model) {
        settingPreference().edit().putString(KEY_DEVICE_MODEL, model).commit();
    }

    /**
     * @return 账号
     */
    public static String getUserAccount() {
        return userPreference().getString(KEY_USER_ACCOUNT, "");
    }

    /**
     * @param account 账号
     */
    public static void setUserAccount(String account) {
        userPreference().edit().putString(KEY_USER_ACCOUNT, account).commit();
    }

    /**
     * @return password
     */
    public static String getUserPassword() {
        return userPreference().getString(KEY_USER_PASSWORD, "");
    }

    /**
     * @param password password
     */
    public static void setUserPassword(String password) {
        userPreference().edit().putString(KEY_USER_PASSWORD, password).commit();
    }

    /**
     * @param userName username
     */
    public static void setUserName(String userName) {
        userPreference().edit().putString(KEY_USER_NAME, userName).commit();
    }

    /**
     * @return 用户头像
     */
    public static String getUserHeadIcon() {
        return userPreference().getString(KEY_USER_HEAD_ICON, "");
    }

    /**
     * @param icon 用户头像,address or url or 二进制
     */
    public static void setUserHeadIcon(String icon) {
        userPreference().edit().putString(KEY_USER_HEAD_ICON, icon).commit();
    }

    /**
     * @return userId
     */
    public static String getUserId() {
        return userPreference().getString(KEY_USER_ID, "");
    }

    /**
     * @param userId userId
     */
    public static void setUserId(String userId) {
        userPreference().edit().putString(KEY_USER_ID, userId).commit();
    }

    /**
     * @return 用户手机号
     */
    public static String getUserPhoneNum() {
        return userPreference().getString(KEY_USER_PHONE_NUM, "");
    }

    /**
     * @param phoneNum 用户手机号
     */
    public static void setUserPhoneNum(String phoneNum) {
        userPreference().edit().putString(KEY_USER_PHONE_NUM, phoneNum).commit();
    }

    /**
     * @return 用户位置
     */
    public static String getUserLocation() {
        return userPreference().getString(KEY_USER_LOCATION, "");
    }

    /**
     * @param location 用户地理位置
     */
    public static void setUserLocation(String location) {
        userPreference().edit().putString(KEY_USER_LOCATION, location).commit();
    }

    /**
     * @return 1为记住用户名, 0为不记住用户名
     */
    public static int getLoginRemember() {
        return settingPreference().getInt(KEY_REMEMBER_USER_LOGIN, 0);
    }

    /**
     * @param remember 1为记住用户名, 0为不记住用户名
     */
    public static void setLoginRemember(int remember) {
        settingPreference().edit().putInt(KEY_REMEMBER_USER_LOGIN, remember).commit();
    }

    /**
     * @return 最后一次登陆的时间
     */
    public static long getLastLoginTime() {
        return settingPreference().getLong(KEY_LAST_LOGIN_TIME, System.currentTimeMillis());
    }

    /**
     * @param time 最后一次登陆的时间
     */
    public static void setLoginTime(long time) {
        settingPreference().edit().putLong(KEY_LAST_LOGIN_TIME, time).commit();
    }

    /**
     * @return 登陆状态 1 为已登录 0为未登陆
     */
    public static int getLoginState() {
        return settingPreference().getInt(KEY_LOGIN_STATE, 0);
    }

    /**
     * @param state 1 为已登录 0为未登陆
     */
    public static void setLoginState(int state) {
        settingPreference().edit().putInt(KEY_LOGIN_STATE, state).commit();
    }

    /**
     * 欢迎页配置信息
     */
    public static void setSplashConfig(String jsonSplash) {
        settingPreference().edit().putString(KEY_SPLASH_CONFIG+"_"+ SignUpTypeDataCache.getInstance().getCurCategory(), jsonSplash).commit();
    }

    public static String getSplashConfig() {
        return settingPreference().getString(KEY_SPLASH_CONFIG+"_"+ SignUpTypeDataCache.getInstance().getCurCategory(), "");
    }

    public static void clearSplashConfig() {
        settingPreference().edit().remove(KEY_SPLASH_CONFIG).commit();
    }

    /**
     * @return 引导页首次运行状态
     */
    public static boolean getGuideFirstRun() {
        return settingPreference().getBoolean(KEY_STATE_FIRST_RUN, true);
    }

    /**
     * 设置引导页首次运行状态
     */
    public static void setGuideFirstRun() {
        settingPreference().edit().putBoolean(KEY_STATE_FIRST_RUN, false).commit();
    }

    /**
     * 清理配置信息
     */
    public static void clearSettingInfo() {
        settingPreference().edit().clear().commit();
    }

    /**
     * 清理common信息
     */
    public static void clearCommonInfo() {
        commonPreference().edit().clear().commit();
    }

    /**
     * 清理用户信息
     */
    public static void clearUserInfo() {
        userPreference().edit().clear().commit();
    }

    /**
     * @return user preference
     */
    private static SharedPreferences userPreference() {
        return getApp().getSharedPreferences(PREFERENCE_USER, Context.MODE_PRIVATE);
    }

    /**
     * @return common preference
     */
    private static SharedPreferences commonPreference() {
        return getApp().getSharedPreferences(PREFERENCE_COMMON, Context.MODE_PRIVATE);
    }





    protected static Context getApp() {
        return UniApplicationContext.getContext();
    }
}
