package com.constantlab.statistics.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class PreferencesManager {
    public static final String KEY_AUTH_TOKEN = "key_session_id";
    public static final String KEY_FIRST_TIME_OPEN = "first_time_open";
    public static final String KEY_LANGUAGE = "language";


    private final SharedPreferences sharedPreferences;

    @Inject
    public PreferencesManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setBool(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBool(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public boolean clear() {
        return sharedPreferences.edit().clear().commit();
    }
}
