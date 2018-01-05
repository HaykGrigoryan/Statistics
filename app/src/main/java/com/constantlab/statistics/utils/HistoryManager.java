package com.constantlab.statistics.utils;

import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;

import io.realm.Realm;

/**
 * Created by Hayk on 04/01/2018.
 */

public class HistoryManager {
    private static HistoryManager _instance;

    private HistoryManager() {
    }

    public static HistoryManager getInstance() {
        if (_instance == null) {
            _instance = new HistoryManager();
        }
        return _instance;
    }

    public void addOrUpdateHistory(History history, Realm realm) {
        History h = realm.where(History.class).equalTo("task_id", history.getTaskId()).equalTo("change_type", history.getChangeType()).equalTo("object_type", history.getObjectType()).equalTo("synced", false).findFirst();
        History changedHistory = null;
        if (h != null) {
            changedHistory = realm.copyFromRealm(h);
            changedHistory.setNewData(history.getNewData());
        } else {
            changedHistory = history;
            Number currentIdNum = realm.where(History.class).max("id");
            int nextId;
            if (currentIdNum == null) {
                nextId = 1;
            } else {
                nextId = currentIdNum.intValue() + 1;
            }
            changedHistory.setId(nextId);
        }

        realm.insertOrUpdate(changedHistory);

//        Realm realm = null;
//
//        try {
//            realm = Realm.getDefaultInstance();
//
//            realm.executeTransaction(realmObject -> {
//                History h = realmObject.where(History.class).equalTo("task_id", history.getTaskId()).equalTo("change_type", history.getChangeType()).equalTo("object_type", history.getObjectType()).equalTo("synced", false).findFirst();
//                History changedHistory = null;
//                if (h != null) {
//                    changedHistory = realmObject.copyFromRealm(h);
//                    changedHistory.setNewData(history.getNewData());
//                } else {
//                    changedHistory = history;
//                    Number currentIdNum = realmObject.where(Street.class).max("id");
//                    int nextId;
//                    if (currentIdNum == null) {
//                        nextId = 1;
//                    } else {
//                        nextId = currentIdNum.intValue() + 1;
//                    }
//                    changedHistory.setId(nextId);
//                }
//
//                realmObject.insertOrUpdate(changedHistory);
//
//            });
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
}
}
