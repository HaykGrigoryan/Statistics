package com.constantlab.statistics.app;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class Statistics extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Realm
        RealmManager.getInstance().init(this);
//        Realm.init(this);
    }
}
