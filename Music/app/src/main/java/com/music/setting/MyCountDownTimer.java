package com.music.setting;

import android.os.CountDownTimer;
import android.util.Log;

import com.music.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingfeng on 2016/5/18.
 */
public class MyCountDownTimer extends CountDownTimer {

    static int defaultinterval = 1000;
    List<ICallBack> callBackList = new ArrayList<>();

    public interface ICallBack {
        void onTick(int millisUntilFinished);
        void onFinish();
    }

    private static MyCountDownTimer INSTANCE = null;

    public static MyCountDownTimer getInstance(long millisInFuture) {
        if (INSTANCE == null) {
            INSTANCE = new MyCountDownTimer(millisInFuture, defaultinterval);
        }
        return INSTANCE;
    }

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void setCallBack(ICallBack callBack) {
        callBackList.add(callBack);
    }

    public void removeInstance() {
        INSTANCE = null;
    }

    public void removeCallBack(ICallBack callBack) {
        callBackList.remove(callBack);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        for (ICallBack callBack : callBackList) {
            callBack.onTick((int) millisUntilFinished / 1000);
        }
    }

    @Override
    public void onFinish() {
        for (ICallBack callBack : callBackList) {
            callBack.onFinish();
        }
        SharedPreferencesUtil.getInstance().putShare("sleep_mode", -1);
    }
}
