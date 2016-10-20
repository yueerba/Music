package com.music.util;

import android.util.Log;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class LogUtil {
    public static final String DATAYES_TAG = "dingfeng";
    private static final boolean IS_DEBUG = true;

    public static void ex(Throwable e) {
        if (IS_DEBUG) {
            e.printStackTrace();
        }
    }

    public static void v(Object source, String message) {
        v(DATAYES_TAG, source, message);
    }

    public static void v(String tag, Object source, String message) {
        if (IS_DEBUG)
            Log.v(tag + " " + getTagSurfix(source), (message != null) ? message
                    : "null");
    }

    public static void i(Object source, String message) {
        i(DATAYES_TAG, source, message);
    }

    public static void i(String tag, Object source, String message) {
        if (IS_DEBUG)
            Log.i(tag + " " + getTagSurfix(source), (message != null) ? message
                    : "null");
    }

    public static void d(Object source, String message) {
        d(DATAYES_TAG, source, message);
    }

    public static void d(String tag, Object source, String message) {
        if (IS_DEBUG)
            Log.d(tag + " " + getTagSurfix(source), (message != null) ? message
                    : "null");
    }

    public static void w(Object source, String message) {
        w(DATAYES_TAG, source, message);
    }

    public static void w(String tag, Object source, String message) {
        if (IS_DEBUG)
            Log.w(tag + " " + getTagSurfix(source), (message != null) ? message
                    : "null");
    }

    public static void e(Object source, String message) {
        e(DATAYES_TAG, source, message);
    }

    public static void e(String tag, Object source, String message) {
        if (IS_DEBUG)
            Log.e(tag + " " + getTagSurfix(source), (message != null) ? message
                    : "null");
    }

    private static String getTagSurfix(Object source) {
        if (source != null) {
            return source.getClass().getSimpleName();
        } else {
            return "";
        }
    }
}
