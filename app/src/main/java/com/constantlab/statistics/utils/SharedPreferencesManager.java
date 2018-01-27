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

    public Long getLastSyncFromServer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getLong(ConstKeys.KEY_LAST_SYNC_FROM_SERVER, -1);
    }

    public void setLastSyncFromServer(Context context, long time) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putLong(ConstKeys.KEY_LAST_SYNC_FROM_SERVER, time).commit();
    }

    public Long getLastSyncToServer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getLong(ConstKeys.KEY_LAST_SYNC_TO_SERVER, -1);
    }

    public void setLastSyncToServer(Context context, long time) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putLong(ConstKeys.KEY_LAST_SYNC_TO_SERVER, time).commit();
    }

    public void clearSyncTimeInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().remove(ConstKeys.KEY_LAST_SYNC_FROM_SERVER).remove(ConstKeys.KEY_LAST_SYNC_TO_SERVER).commit();
    }
}
