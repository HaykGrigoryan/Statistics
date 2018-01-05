package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.utils.GsonUtils;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 19-12-2017.
 */

public class ApartmentType extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApartmentType that = (ApartmentType) o;

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

    public static int getIndex(List<ApartmentType> items, int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public static String getDescriptionById(Context context, int id) {
        List<ApartmentType> items = GsonUtils.getApartmentTypeData(context);
        for (ApartmentType apartmentType : items) {
            if (apartmentType.getId() == id) {
                return apartmentType.getName();
            }
        }
        return "";
    }
}
