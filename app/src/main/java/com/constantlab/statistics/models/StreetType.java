package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.utils.GsonUtils;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 08-12-2017.
 */

public class StreetType extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreetType that = (StreetType) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static int getIndex(List<StreetType> items, int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public static String getDescriptionById(Context context, int id) {
        List<StreetType> items = GsonUtils.getStreetTypeData(context);
        for (StreetType streetType : items) {
            if (streetType.getId() == id) {
                return streetType.getName();
            }
        }
        return "";
    }
}
