package com.constantlab.statistics.app;

import android.content.Context;

import com.constantlab.statistics.utils.ConstKeys;

import java.util.ArrayList;
import java.util.Calendar;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;


/**
 * Created by Hayk on 17/01/2017.
 */

public class RealmManager {
    private final long DAY_TO_MILLIS = 24 * 60 * 60 * 1000;

    private RealmManager() {

    }

    private static RealmManager _instance;

    public static RealmManager getInstance() {
        if (_instance == null) {
            _instance = new RealmManager();
        }
        return _instance;
    }

    public void init(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(ConstKeys.REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public Realm getDefaultInstance(Context context) {
        try {
            Realm.getDefaultInstance();
        } catch (IllegalStateException e) {
            init(context);
        }
        return Realm.getDefaultInstance();
    }
}
