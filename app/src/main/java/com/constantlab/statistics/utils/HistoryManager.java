package com.constantlab.statistics.utils;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;

import io.realm.Realm;
import io.realm.RealmObject;

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

    public Integer addOrUpdateHistory(History history, Realm realm) {
        History h = realm.where(History.class).equalTo("task_id", history.getTaskId()).equalTo("change_type", history.getChangeType()).equalTo("object_type", history.getObjectType()).equalTo("temp_object_id", history.getTempObjectId()).equalTo("synced", false).findFirst();
        History changedHistory = null;
        int nextId = 0;
        if (h != null) {
            changedHistory = realm.copyFromRealm(h);
            changedHistory.setNewData(history.getNewData());
            nextId = changedHistory.getId();
        } else {
            changedHistory = history;
            Number currentIdNum = realm.where(History.class).max("id");
            if (currentIdNum == null) {
                nextId = 1;
            } else {
                nextId = currentIdNum.intValue() + 1;
            }
            changedHistory.setId(nextId);
        }

        realm.insertOrUpdate(changedHistory);
        return nextId;
    }

    public void removeHistory(History history, Realm realm) {
        History h = realm.where(History.class).equalTo("task_id", history.getTaskId()).equalTo("change_type", history.getChangeType()).equalTo("object_type", history.getObjectType()).equalTo("temp_object_id", history.getTempObjectId()).equalTo("synced", false).findFirst();
        if (h != null) {
            RealmObject.deleteFromRealm(h);
        }
    }
}
