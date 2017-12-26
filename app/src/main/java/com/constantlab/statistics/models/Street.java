package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 26/12/2017.
 */

public class Street extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private RealmList<Building> buildingList;

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

    public RealmList<Building> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(RealmList<Building> buildingList) {
        this.buildingList = buildingList;
    }

    public int getBuidingsCount() {
        return buildingList.size();
    }

    public int getApartmentCount() {
        int count = 0;
        for (Building building : buildingList) {
            count += building.getApartmentList().size();
        }
        return count;
    }

    public int getResidentsCount() {
        int count = 0;
        for (Building building : buildingList) {
            for (Apartment apartment : building.getApartmentList()) {
                count += apartment.getTotalInhabitants();
            }
        }
        return count;
    }

    public String getDisplayName(Context context) {
        return context.getString(R.string.label_street_short) + " " + name;
    }
}
