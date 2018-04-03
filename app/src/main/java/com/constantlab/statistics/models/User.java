package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.app.RealmManager;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 23/02/2018.
 */

public class User extends RealmObject implements Serializable {
    @PrimaryKey
    private Integer user_id;
    private String username;
    private String password;
    private String key;
    private long lastSyncFromServer;
    private long lastSyncToServer;

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLastSyncFromServer() {
        return lastSyncFromServer;
    }

    public void setLastSyncFromServer(long lastSyncFromServer) {
        this.lastSyncFromServer = lastSyncFromServer;
    }

    public long getLastSyncToServer() {
        return lastSyncToServer;
    }

    public void setLastSyncToServer(long lastSyncToServer) {
        this.lastSyncToServer = lastSyncToServer;
    }

    public static User createUser(Context context, String username, String password, String key) {
        if (context == null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setLastSyncFromServer(-1);
        user.setLastSyncToServer(-1);
        user.setKey(key);
        Realm realm = null;
        try {
            realm = RealmManager.getInstance().getDefaultInstance(context);
            Number currentIdNum = realm.where(User.class).max("user_id");
            int nextId;
            if (currentIdNum == null) {
                nextId = 1;
            } else {
                nextId = currentIdNum.intValue() + 1;
            }
            user.setUserId(nextId);
        } finally {
            if (realm != null)
                realm.close();
        }
        return user;
    }
}
