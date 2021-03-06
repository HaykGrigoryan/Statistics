package com.constantlab.statistics.models;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 19-12-2017.
 */

public class BuildingType extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildingType that = (BuildingType) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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

    public static int getIndex(List<BuildingType> items, int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

//    public static String getDescriptionById(Context context, int id) {
//        List<BuildingType> items = GsonUtils.getBuildingTypeData(context);
//        for (BuildingType buildingType : items) {
//            if (buildingType.getId() == id) {
//                return buildingType.getName();
//            }
//        }
//        return "";
//    }
}
