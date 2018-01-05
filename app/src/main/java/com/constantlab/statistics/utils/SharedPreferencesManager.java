package com.constantlab.statistics.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hayk on 29/12/2017.
 */

public class SharedPreferencesManager {
    private static SharedPreferencesManager _INSTANCE;

    private SharedPreferencesManager() {

    }

    public static SharedPreferencesManager getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new SharedPreferencesManager();
        }
        return _INSTANCE;
    }


    /*User Key*/
    public void setKey(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putString(ConstKeys.KEY_USER_KEY, key).apply();
    }

    public String getKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getString(ConstKeys.KEY_USER_KEY, null);
    }
}
