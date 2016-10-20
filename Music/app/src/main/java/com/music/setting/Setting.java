package com.music.setting;

import com.music.util.SharedPreferencesUtil;

/**
 * Created by dingfeng on 2016/4/26.
 */
public class Setting {

    public boolean mNotificationDisplay;
    public boolean mShakeChangeSong;
    public boolean mFlickChangeSong;
    public boolean mDoublePress;
    public boolean mHookkeyControl;
    public boolean mHeadSetPlug;
    public boolean mHeadSetUnplug;

    public boolean mScanTimeLimit;
    public boolean mScanSizeLimit;

    private static class SingletonHolder {
        private static final Setting INSTANCE = new Setting();
    }

    public static final Setting getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setNotificationDisplay(boolean b) {
        mNotificationDisplay = b;
        SharedPreferencesUtil.getInstance().putShare("display_notification", b);
    }

    public void setShakeChangeSong(boolean b) {
        mShakeChangeSong = b;
        SharedPreferencesUtil.getInstance().putShare("shake_change_song", b);
    }

    public void setFlickChangeSong(boolean b) {
        mFlickChangeSong = b;
        SharedPreferencesUtil.getInstance().putShare("flick_change_song", b);
    }

    public void setHookkeyControlg(boolean b) {
        mHookkeyControl = b;
        SharedPreferencesUtil.getInstance().putShare("hook_key_control", b);
    }

    public void setHookkeyDoublePress(boolean b) {
        mDoublePress = b;
        SharedPreferencesUtil.getInstance().putShare("hook_double_press", b);
    }

    public void setHeadSetPlug(boolean b) {
        mHeadSetPlug = b;
        SharedPreferencesUtil.getInstance().putShare("headset_plugged", b);
    }

    public void setHeadSetUnPlug(boolean b) {
        mHeadSetUnplug = b;
        SharedPreferencesUtil.getInstance().putShare("headset_unplugged", b);
    }

    public void setScanTimeLimit(boolean b) {
        mScanTimeLimit = b;
        SharedPreferencesUtil.getInstance().putShare("scan_time_limit", b);
    }

    public void setScanSizeLimit(boolean b) {
        mScanSizeLimit = b;
        SharedPreferencesUtil.getInstance().putShare("scan_size_limit", b);
    }


    private Setting() {
        loadSettingsData();
    }

    private void loadSettingsData() {
        mNotificationDisplay = SharedPreferencesUtil.getInstance().getShare("display_notification", true);
        mShakeChangeSong = SharedPreferencesUtil.getInstance().getShare("shake_change_song", false);
        mFlickChangeSong = SharedPreferencesUtil.getInstance().getShare("flick_change_song", false);
        mHookkeyControl = SharedPreferencesUtil.getInstance().getShare("hook_key_control", true);
        mDoublePress = SharedPreferencesUtil.getInstance().getShare("hook_double_press", true);
        mHeadSetPlug = SharedPreferencesUtil.getInstance().getShare("headset_plugged", false);
        mHeadSetUnplug = SharedPreferencesUtil.getInstance().getShare("headset_unplugged", true);
        mScanTimeLimit = SharedPreferencesUtil.getInstance().getShare("scan_time_limit", true);
        mScanSizeLimit = SharedPreferencesUtil.getInstance().getShare("scan_size_limit", true);
    }
}
