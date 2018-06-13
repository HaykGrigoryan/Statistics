package com.constantlab.statistics.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.constantlab.statistics.models.User;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

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
    public void setUser(Context context, User user) {
        if (context == null || user == null) {
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        Gson gson = new Gson();
        prefs.edit().putString(ConstKeys.KEY_USER, gson.toJson(user)).apply();
    }

    public void removeUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().remove(ConstKeys.KEY_USER).commit();
    }

    public User getUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        String json = prefs.getString(ConstKeys.KEY_USER, null);
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, User.class);
    }

//    public Long getLastSyncFromServer(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
//        return prefs.getLong(ConstKeys.KEY_LAST_SYNC_FROM_SERVER, -1);
//    }
//
//    public void setLastSyncFromServer(Context context, long time) {
//        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
//        prefs.edit().putLong(ConstKeys.KEY_LAST_SYNC_FROM_SERVER, time).commit();
//    }
//
//    public Long getLastSyncToServer(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
//        return prefs.getLong(ConstKeys.KEY_LAST_SYNC_TO_SERVER, -1);
//    }
//
//    public void setLastSyncToServer(Context context, long time) {
//        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
//        prefs.edit().putLong(ConstKeys.KEY_LAST_SYNC_TO_SERVER, time).commit();
//    }
//
//    public void clearSyncTimeInfo(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
//        prefs.edit().remove(ConstKeys.KEY_LAST_SYNC_FROM_SERVER).remove(ConstKeys.KEY_LAST_SYNC_TO_SERVER).commit();
//    }

    public void setSyncing(Context context, int state) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putInt(ConstKeys.KEY_SYNCING_INT, state).commit();
    }

    public int isSyncing(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getInt(ConstKeys.KEY_SYNCING_INT, -1);
    }

    public void setGeoPoint(Context context, GeoPoint geoPoint) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        Gson gson = new Gson();
        prefs.edit().putString(ConstKeys.KEY_GEOPOINT, gson.toJson(geoPoint, GeoPoint.class)).commit();
    }

    public GeoPoint getGeoPoint(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        String geoPointJson = prefs.getString(ConstKeys.KEY_GEOPOINT, null);
        if (geoPointJson == null) {
            return null;
        }
        return new Gson().fromJson(geoPointJson, GeoPoint.class);
    }

    public void clearGeoPoint(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().remove(ConstKeys.KEY_GEOPOINT).commit();
    }

    public void setZoom(Context context, int zoom) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putInt(ConstKeys.KEY_ZOOM, zoom).commit();
    }

    public int getZoom(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getInt(ConstKeys.KEY_ZOOM, -1);
    }

    public void clearZoom(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().remove(ConstKeys.KEY_ZOOM).commit();
    }


    public String getServerIP(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getString(ConstKeys.KEY_SERVER_IP, "");
    }

    public void setServerIP(Context context, String ip) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putString(ConstKeys.KEY_SERVER_IP, ip).commit();
    }

    public String getServerPort(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        return prefs.getString(ConstKeys.KEY_SERVER_PORT, "");
    }

    public void setServerPort(Context context, String port) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().putString(ConstKeys.KEY_SERVER_PORT, port).commit();
    }

    public void clearServerInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ConstKeys.KEY_APP_SHARED_PREFERENCES, 0);
        prefs.edit().remove(ConstKeys.KEY_SERVER_IP).remove(ConstKeys.KEY_SERVER_PORT).commit();
    }
}
