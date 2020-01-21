package com.huatu.handheld_huatu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

/**
 * Created by cjx on 2018\8\28 0028.
 */

public class ScreenRotateUtil {


    private Activity mActivity          ;//: Activity? = null
    private boolean isClickFullScreen   ;//  : Boolean = false        // 记录全屏按钮的状态，默认false
    private boolean isOpenSensor = true  ;//    // 是否打开传输，默认打开
    private boolean isLandscape = false ;//     // 默认是竖屏
    private boolean isChangeOrientation = true  ;//// 记录点击全屏后屏幕朝向是否改变，默认会自动切换

    private boolean isEffetSysSetting = false ;//  // 手机系统的重力感应设置是否生效，默认无效，想要生效改成true就好了

    private SensorManager sm;//: SensorManager? = null
    private OrientationSensorListener listener;//: OrientationSensorListener? = null
    private Sensor sensor;//: Sensor? = null

    /**
     * 接收重力感应监听的结果，来改变屏幕朝向
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage( Message msg) {
            if (msg.what == 888) {
                int orientation = msg.arg1;
                /**
                 * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                 */
                if (orientation > 45 && orientation < 135) {

                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    isLandscape = true;
                } else if (orientation > 135 && orientation < 225) {
                   // mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);关闭竖屏方向的旋转，离线缓存不要竖屏
                    isLandscape = false;
                } else if (orientation > 225 && orientation < 315) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isLandscape = true;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                   // mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isLandscape = false;
                }
            }
        }
    };


    // 这里在构造里初始化重力重力感应
    public ScreenRotateUtil(Context context) {
        // 获取传感器管理器
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // 获取传感器类型
        sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        // 初始化监听器
        listener = new OrientationSensorListener(mHandler);
    }

    private boolean mIsRegisted=false;
    public void start(Activity activity) {
        if(mIsRegisted){
            return;
        }
        mIsRegisted=true;
        // 接收activity，用于操作屏幕的旋转
        mActivity = activity;
        // 注册传感器监听
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mIsRegisted=false;
        // 注销监听
        sm.unregisterListener(listener,sensor);
        // 防止内存泄漏
        mActivity = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void toggleRotate() {

        /**
         * 先判断是否已经开启了重力感应，没开启就直接普通的切换横竖屏
         */
        if(isEffetSysSetting){
            try {
                int isRotate = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                // 如果用户禁用掉了重力感应就直接切换
                if (isRotate == 0) {
                    setOrientation(isLandscape, true);
                    return;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 如果开启了重力感应就需要修改状态
         */
        isOpenSensor = false;
        isClickFullScreen = true;
       /* if (isChangeOrientation) {
            setOrientation(isLandscape, false);
        } else {
            isLandscape = !isLandscape;
            changeOrientation(isLandscape, false);
        }*/


        if (isChangeOrientation) {
            setOrientation(isLandscape, false);
        } else {
            setOrientation(isLandscape, false);
        }
        isLandscape = !isLandscape;
    }

    private final int DATA_X = 0;
    private final int DATA_Y = 1;
    private final int DATA_Z = 2;
    final int ORIENTATION_UNKNOWN = -1;
    public class OrientationSensorListener implements SensorEventListener {
        Handler mRotateHandler;
        public OrientationSensorListener(Handler rotateHandler){
            mRotateHandler=rotateHandler;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy){}

        @Override
        public void onSensorChanged(SensorEvent event)  {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float x = -values[DATA_X];
            float y = -values[DATA_Y];
            float z = -values[DATA_Z];
            float magnitude = x * x + y * y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= z * z) {
                // 屏幕旋转时
                float oneEightyOverPi = 57.29577957855f;
                float angle = (float)Math.atan2((-y), x) * oneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }


            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if (isEffetSysSetting) {
                try {
                    int isRotate = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) return;
                } catch (  Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

            }

            // 只有点了按钮时才需要根据当前的状态来更新状态
            if (isClickFullScreen) {
                if (isLandscape && screenIsPortrait(orientation)) {           // 之前是横屏，并且当前是竖屏的状态
                     LogUtils.e("onSensorChanged: 横屏 ----> 竖屏");
                    updateState(false, false, true, true);
                } else if (!isLandscape && screenIsLandscape(orientation)) {  // 之前是竖屏，并且当前是横屏的状态
                    LogUtils.e("onSensorChanged: 竖屏 ----> 横屏");
                    updateState(true, false, true, true);
                } else if (isLandscape && screenIsLandscape(orientation)) {    // 之前是横屏，现在还是横屏的状态
                    LogUtils.e("onSensorChanged: 横屏 ----> 横屏");
                    isChangeOrientation = false;
                } else if (!isLandscape && screenIsPortrait(orientation)) {  // 之前是竖屏，现在还是竖屏的状态
                    LogUtils.e("onSensorChanged: 竖屏 ----> 竖屏");
                    isChangeOrientation = false;
                }
            }

            // 判断是否要进行中断信息传递
            if (!isOpenSensor) {
                return;
            }

            if (mRotateHandler != null) {
                mRotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
     }


    /**
     * 更新状态
     *
     * @param isLandscape         横屏
     * @param isClickFullScreen   全屏点击
     * @param isOpenSensor        打开传输
     * @param isChangeOrientation 朝向改变
     */
    private void updateState(boolean isLandscape,boolean isClickFullScreen,boolean isOpenSensor,   boolean isChangeOrientation) {
        this.isLandscape = isLandscape;
        this.isClickFullScreen = isClickFullScreen;
        this.isOpenSensor = isOpenSensor;
        this.isChangeOrientation = isChangeOrientation;
    }

    /**
     * 当前屏幕朝向是否横屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsLandscape(int orientation ) {
        return (orientation>=46&&orientation<=135)||(orientation>=226&&orientation<=315);
        //return orientation in 46..135 || orientation in 226..315
    }

    /**
     * 当前屏幕朝向是否竖屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsPortrait(int orientation ) {
       // return orientation in 316..360 || orientation in 0..45 || orientation in 136..225
        return (orientation>=316&&orientation<=360)||(orientation>=0&&orientation<=45)||(orientation>=136&&orientation<=225);
    }

    /**
     * 根据朝向来改变屏幕朝向
     *
     * @param isLandscape
     * @param isNeedChangeOrientation 是否需要改变判断值
     */
    private void setOrientation(boolean isLandscape ,boolean isNeedChangeOrientation) {
        if (isLandscape) {
            // 切换成竖屏
           // mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mActivity.getWindow().clearFlags(1024);
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (isNeedChangeOrientation) this.isLandscape = false;
        } else {
            // 切换成横屏
           // mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            mActivity.getWindow().addFlags(1024);
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (isNeedChangeOrientation) this.isLandscape = true;
        }
    }

}
