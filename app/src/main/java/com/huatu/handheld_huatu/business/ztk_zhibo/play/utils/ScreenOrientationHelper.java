package com.huatu.handheld_huatu.business.ztk_zhibo.play.utils;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.utils.CommonUtils;

public class ScreenOrientationHelper implements SensorEventListener {

    private final static String TAG = ScreenOrientationHelper.class.getSimpleName();

    private Activity mActivity;
    private int mOriginOrientation;
    private Boolean mPortraitOrLandscape;

    private SensorManager mSensorManager;
    private Sensor[] mSensors;

    private float[] mAccelerometerValues = new float[3];
    private float[] mMagneticFieldValues = new float[3];

    boolean isPad = false;

    public ScreenOrientationHelper(Activity activity) {
        this(activity, null);
    }

    public ScreenOrientationHelper(Activity activity, View button1) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
//        isPad = CommonUtils.isPad(mActivity);
    }

    public void enableSensorOrientation() {
        if (mSensors == null) {
            mOriginOrientation = mActivity.getRequestedOrientation();

            mSensors = new Sensor[2];
            mSensors[0] = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensors[1] = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            mSensorManager.registerListener(this, mSensors[0], SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensors[1], SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void disableSensorOrientation(boolean reset) {
        if (mSensors != null) {
            mSensorManager.unregisterListener(this, mSensors[0]);
            mSensorManager.unregisterListener(this, mSensors[1]);
            mSensors = null;

            if (reset == true) {
                mActivity.setRequestedOrientation(mOriginOrientation);
            }
        }
    }

    public void disableSensorOrientation() {
        disableSensorOrientation(true);
    }

    public void landscape() {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setButtonChecked(true);
        mPortraitOrLandscape = false;
    }

    public void portrait() {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setButtonChecked(false);
        mPortraitOrLandscape = true;
    }

    public void setButtonChecked(boolean landscape) {
//        mFullScreen.setEnabled(!landscape);
    }

    public void postOnStart() {
        if (mSensors != null) {
            mSensorManager.registerListener(this, mSensors[0], SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensors[1], SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void postOnStop() {
        if (mSensors != null) {
            mSensorManager.unregisterListener(this, mSensors[0]);
            mSensorManager.unregisterListener(this, mSensors[1]);
        }
    }

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];

        SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagneticFieldValues);
        SensorManager.getOrientation(R, values);

        if (mSensors != null) {
            if (mSensors[1] == null)
                calculateByAccelerometer(mAccelerometerValues);
            else
                calculateByOrientation(values);
        }
    }

    private void calculateByAccelerometer(float[] values) {
        int orientation = mActivity.getRequestedOrientation();

        if ((-2f < values[1] && values[1] <= 2f) && values[0] < 0) {// 向左
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || !mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(true);
            }


            if (mPortraitOrLandscape != null && !mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if (4f < values[1] && values[1] < 10f) { // 向下
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(false);
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if (-10f < values[1] && values[1] < -4f) { // 向上
            int shouldOrentation =  (!isPad ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(false);
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if ((-2f < values[1] && values[1] <= 2f) && values[0] > 0) { // 向右
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || !mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(true);
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape)
                mPortraitOrLandscape = null;
        }
    }

    private void calculateByOrientation(float[] values) {
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        int orientation = mActivity.getRequestedOrientation();

        if ((-10.0f < values[1] && values[1] <= 10f) && values[2] < -40f) {// 向左
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || !mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(true);
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if (40.0f < values[1] && values[1] < 90.0f) { // 向下
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(false);
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if (-90.0f < values[1] && values[1] < -40.0f) { // 向上
            int shouldOrentation =  (!isPad ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(false);
            }

            if (mPortraitOrLandscape != null && mPortraitOrLandscape)
                mPortraitOrLandscape = null;

        } else if ((-10.0f < values[1] && values[1] <= 10f) && values[2] > 40f) { // 向右
            int shouldOrentation =  (isPad ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            if (orientation != shouldOrentation
                    && (mPortraitOrLandscape == null || !mPortraitOrLandscape)) {
                mActivity.setRequestedOrientation(shouldOrentation);
                setButtonChecked(true);
            }

            if (mPortraitOrLandscape != null && !mPortraitOrLandscape)
                mPortraitOrLandscape = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!isOpen())//判断方向键是否开启
            return;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagneticFieldValues = event.values;
                break;

            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerValues = event.values;
                break;

            default:
                break;
        }
        calculateOrientation();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private boolean isOpen(){
        int screenchange = 1;
        try {
            screenchange = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        }catch (Exception e){
            e.printStackTrace();
        }

        return screenchange == 1;
    }
}
