package com.music.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class BaseInfo {

    private static final String TAG = "BaseInfo";

    private static float density = 1.0f;

    private static int screenWidth;
    private static int screenHeight;

    private static boolean isCN = false;
    private static boolean sIsVersionAboveICS;

    private static final int SCREEN_TYPE_LOW = 0;
    private static final int SCREEN_TYPE_MIDDLE = 1;
    private static final int SCREEN_TYPE_HIGH = 2;
    //屏幕尺寸类型
    private static int screenType = SCREEN_TYPE_MIDDLE;
    private static String verName;
    private static String appName;
    private static int verCode;
    private static String phone_model;
    private static  String phone_sdk;
    private static  String APP_DIR;
    private static String LOG_DIR;
    //	private static String APP_DIR;
    private static final String LOG_PATH = "/crash/";

    public static void init(Context ctx) {
        final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        density = metrics.density;
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        if(screenHeight<screenWidth){
            //有时候列表高度很高的bug
            int tmp=screenWidth;
            screenWidth=screenHeight;
            screenHeight=tmp;
        }
        sIsVersionAboveICS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;//ICE_CREAM_SANDWICH;
        if(screenHeight < 480) {
            screenType = SCREEN_TYPE_LOW;
        } else if(screenHeight >= 480 && screenHeight < 1024) {
            screenType = SCREEN_TYPE_MIDDLE;
        } else {
            screenType = SCREEN_TYPE_HIGH;
        }
        //获取locale info
        initLocale(ctx);
        //zhy 20150512 基本信息放在这里统一管理
        PackageManager pm = ctx.getPackageManager();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //SD卡不存在
            setLOG_DIR(BaseInfo.getAPP_DIR() + LOG_PATH);
        }else{
            setLOG_DIR(IConstants.CRASHPATH);
        }
        try {
            // 获取基本信息
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            setVerName(pi.versionName);
            setAppName(pi.applicationInfo.loadLabel(ctx.getPackageManager()).toString());
            setVerCode(pi.versionCode);
            setPhone_model(android.os.Build.MODEL);
            setPhone_sdk(android.os.Build.VERSION.SDK);
            setAPP_DIR(ctx.getFilesDir().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initLocale(Context ctx) {
        Locale locale = ctx.getResources().getConfiguration().locale;
        isCN = false;
        if (locale != null) {
            String cstr = locale.toString();
            if ("zh_CN".equals(cstr)) {
                isCN = true;
            }
        }
    }

    public static void initLocale(Configuration config) {
        Locale locale = config.locale;
        isCN = false;
        if (locale != null) {
            String cstr = locale.toString();
            if ("zh_CN".equals(cstr)) {
                isCN = true;
            }
        }
    }
    /**
     * 当前是否在中国（即中文环境）
     * @return
     */
    private static boolean isInHell() {
        return isCN;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static boolean isVersionAboveICS() {
        return sIsVersionAboveICS;
    }

    public static String getVerName() {
        return verName;
    }

    public static void setVerName(String verName) {
        BaseInfo.verName = verName;
    }

    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        BaseInfo.appName = appName;
    }

    public static int getVerCode() {
        return verCode;
    }

    public static void setVerCode(int verCode) {
        BaseInfo.verCode = verCode;
    }

    public static String getPhone_model() {
        return phone_model;
    }

    public static void setPhone_model(String phone_model) {
        BaseInfo.phone_model = phone_model;
    }

    public static String getPhone_sdk() {
        return phone_sdk;
    }

    public static void setPhone_sdk(String phone_sdk) {
        BaseInfo.phone_sdk = phone_sdk;
    }

    public static String getAPP_DIR() {
        return APP_DIR;
    }

    public static void setAPP_DIR(String aPP_DIR) {
        APP_DIR = aPP_DIR;
    }

    public static String getLOG_DIR() {
        return LOG_DIR;
    }

    public static void setLOG_DIR(String lOG_DIR) {
        LOG_DIR = lOG_DIR;
    }


}
