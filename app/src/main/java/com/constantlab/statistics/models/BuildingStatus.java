package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.utils.GsonUtils;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 19-12-2017.
 */

public class BuildingStatus extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.name = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildingStatus that = (BuildingStatus) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public static int getIndex(List<BuildingStatus> items, int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }
//
//    public static String getDescriptionById(Context context, int id) {
//        List<BuildingStatus> items = GsonUtils.getBuildingStatusData(context);
//        for (BuildingStatus buildingStatus : items) {
//            if (buildingStatus.getId() == id) {
//                return buildingStatus.getName();
//            }
//        }
//        return "";
//    }
}
