package com.music.setting;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by dingfeng on 2016/4/26.
 */
public class SensorListenerUtil implements SensorEventListener {

    private static SensorListenerUtil INSTANCE;
    private static SensorManager mSensorManager;
    private static Sensor mAccelerometerSensor;
    private static Sensor mProxitySensor;
    private long mLastShakeTime = 0;
    private long mCurShakeTime = 0;
    private static final int threshold = 18;

    private ICallBack mCallBack;

    public interface ICallBack {
        void onShake();
        void onProxityChange();
    }

    public static SensorListenerUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SensorListenerUtil();
            mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mProxitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        return INSTANCE;
    }

    public void register(ICallBack callBack) {
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mProxitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mCallBack= callBack;
    }

    public void unregister() {
        mSensorManager.unregisterListener(this, mAccelerometerSensor);
        mSensorManager.unregisterListener(this, mProxitySensor);
    }

    private boolean allowShake() {
        mCurShakeTime = System.currentTimeMillis();
        if (mCurShakeTime - mLastShakeTime > 2000) {
            mLastShakeTime = mCurShakeTime;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > threshold || Math.abs(values[1]) > threshold || Math.abs(values[2]) > threshold)) {
                if (allowShake()) {
                    if (mCallBack != null) {
                        mCallBack.onShake();
                    }
                }
            }

        } else if (sensorType == Sensor.TYPE_PROXIMITY) {
            if (values[0] > 0) {//遮住距离感应值为0，拿开后大于0
                mCallBack.onProxityChange();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
