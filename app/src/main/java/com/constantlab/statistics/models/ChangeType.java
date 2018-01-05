package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.utils.GsonUtils;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 29/12/2017.
 */

public class ChangeType extends RealmObject {
    @PrimaryKey
    private int type_id;
    private String description;

    public int getTypeId() {
        return type_id;
    }

    public void setTypeId(int type_id) {
        this.type_id = type_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getDescriptionById(Context context, int id) {
        List<ChangeType> items = GsonUtils.getChangeTypeData(context);
        for (ChangeType changeType : items) {
            if (changeType.getTypeId() == id) {
                return changeType.getDescription();
            }
        }
        return "";
    }
}
