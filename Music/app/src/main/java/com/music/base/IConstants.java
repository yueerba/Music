package com.music.base;

import android.os.Environment;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class IConstants {
    public static final boolean DEBUG = true;

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FbmusicRoot = SD_PATH + "/fbmusic/";
    public static final String CACH_IMG_PATH = FbmusicRoot + "cache/img/";
    public static final String CRASHPATH = FbmusicRoot + "crash/";
    public static final String LYRICPATH = FbmusicRoot + "lyric/";
    public static final String CACHE = FbmusicRoot + "cache/";

    //SharedPreference
    public static final String SELECTED_THEME = "selected_theme";

    // 播放模式
    public static final int ORDER_PLAY         = 0; // 顺序播放
    public static final int LIST_LOOP_PLAY     = 1; // 列表循环
    public static final int RANDOM_PLAY        = 2; // 随机播放
    public static final int SINGLE_LOOP_PLAY   = 3; // 单曲循环

    public static final String COMMAND_PLAY = "com.fbm.music.play";
    public static final String COMMAND_PAUSE = "com.fbm.music.pause";
    public static final String COMMAND_STOP = "com.fbm.music.stop";
    public static final String COMMAND_NEXT = "com.fbm.music.next";
    public static final String PLAY_COMPLETED = "com.fbm.music.playcompleted";

    public static final String BROADCAST_THEMECHANGE = "com.fbm.music.themechange";
}
