package com.music.util;

import android.content.SharedPreferences;

import com.music.MusicApplication;

import java.util.Set;

/**
 * Created by dingfeng on 2016/4/13.
 */
public class SharedPreferencesUtil {
    private static SharedPreferencesUtil spUtil;
    private static SharedPreferences sp;
    private final static String SHARE_NAME = "fbmusic_sp";

    public static SharedPreferencesUtil getInstance() {
        if(spUtil == null) {
            spUtil = new SharedPreferencesUtil();
        }
        if(sp == null) {
            sp = MusicApplication.getInstance().getSharedPreferences(SHARE_NAME, 0);
        }
        return spUtil;
    }

    /**
     * 保存sharepreference
     *
     * @param key
     * @param value
     */
    public void putShare(String key, Object value) {
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if(value instanceof Set<?>)
            editor.putStringSet(key, (Set<String>)value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public String getShare(String key, String value) {
        return sp.getString(key, value);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public int getShare(String key, int value) {
        return sp.getInt(key, value);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public boolean getShare(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public Set<String> getShare(String key){
        return sp.getStringSet(key, null);
    }

    public void sharePreferenceRemove(String key){
        sp.edit().remove(key).commit();
    }
}
