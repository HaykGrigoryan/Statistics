package com.constantlab.statistics.models;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 05/01/2018.
 */

public class HistoryForSend<T> {
    private Integer id;
    private String key;
    private Integer task_id;
    private Integer change_type;
    private Integer object_type;
    private Integer object_id;
    private T new_data;

    public static HistoryForSend getForSend(History history) {
        HistoryForSend historyForSend = null;
        switch (history.getChangeType()) {
            case 14:
                historyForSend = new HistoryForSend<History.ChangePointNewData>();
                historyForSend.setNewData(history.getNewData().getChangePointNewData());
                break;
            default:
                historyForSend = new HistoryForSend<History.NewData>();
                historyForSend.setNewData(history.getNewData().getNewData());
        }
        historyForSend.setKey(history.getKey());
        historyForSend.setChangeType(history.getChangeType());
        historyForSend.setObjectId(history.getObjectId());
        historyForSend.setObjectType(history.getObjectType());
        historyForSend.setTaskId(history.getTaskId());
        historyForSend.setId(history.getId());
        return historyForSend;
    }

    public History setHistorySynced() {
        Realm realm = null;
        History history = null;
        try {
            realm = Realm.getDefaultInstance();
            history = realm.where(History.class).equalTo("id", id).findFirst();
            History finalHistory = history;
            history = realm.copyFromRealm(history);
            realm.executeTransaction(realmObject -> {
                finalHistory.setSynced(true);
                realmObject.insertOrUpdate(finalHistory);
            });

        } finally {
            if (realm != null)
                realm.close();
        }

        return history;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getChangeType() {
        return change_type;
    }

    public void setChangeType(Integer change_type) {
        this.change_type = change_type;
    }

    public Integer getObjectType() {
        return object_type;
    }

    public void setObjectType(Integer object_type) {
        this.object_type = object_type;
    }

    public Integer getObjectId() {
        return object_id;
    }

    public void setObjectId(Integer object_id) {
        this.object_id = object_id;
    }

    public T getNewData() {
        return new_data;
    }

    public void setNewData(T new_data) {
        this.new_data = new_data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
