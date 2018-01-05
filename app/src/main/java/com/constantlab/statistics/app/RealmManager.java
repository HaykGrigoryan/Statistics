package com.constantlab.statistics.app;

import android.content.Context;

import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
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

    public void clearLocalData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<Task> tasks = realm.where(Task.class).findAll();
            RealmResults<Street> streets = realm.where(Street.class).findAll();
            RealmResults<Building> buildings = realm.where(Building.class).findAll();
            RealmResults<Apartment> apartments = realm.where(Apartment.class).findAll();
            RealmResults<History> histories = realm.where(History.class).findAll();
            realm.executeTransaction(realmObject -> {
                tasks.deleteAllFromRealm();
                streets.deleteAllFromRealm();
                buildings.deleteAllFromRealm();
                apartments.deleteAllFromRealm();
                histories.deleteAllFromRealm();
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
