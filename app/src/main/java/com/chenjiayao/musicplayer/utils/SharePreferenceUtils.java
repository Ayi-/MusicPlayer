package com.chenjiayao.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chen on 2015/12/17.
 */
public class SharePreferenceUtils {

    private static SharePreferenceUtils utils;

    private SharedPreferences preferences;

    private static final String fileName = "SETTING_PREFERENCE";
    private SharedPreferences.Editor editor;

    public static SharePreferenceUtils getInstance(Context context) {
        if (utils == null) {
            synchronized (SharePreferenceUtils.class) {
                utils = new SharePreferenceUtils(context);
            }
        }
        return utils;
    }

    private SharePreferenceUtils(Context context) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public boolean isFirstTimeUse() {
        boolean res = preferences.getBoolean("firstTime", true);
        return res;
    }

    public void setNotFirst() {
        editor.putBoolean("firstTime", false);
        editor.commit();
    }
}
