package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.model.StreetItem;

import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 26/12/2017.
 */

public class Street extends RealmObject {
    @PrimaryKey
    private Integer local_id;
    private Integer id;
    private String name;
    private String originalName;
    private Integer streetTypeCode;
    private Integer task_id;
    private String kato;
    private Integer historyId;
    private boolean isNew;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName == null ? "" : originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getBuidingsCount() {
        Realm realm = null;
        int buildingCount = 0;
        try {
            realm = Realm.getDefaultInstance();

            List<Building> buildings = realm.where(Building.class).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
            buildingCount = buildings.size();

            return buildingCount;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public int getApartmentCount() {
        Realm realm = null;
        int apartmentCount = 0;
        try {
            realm = Realm.getDefaultInstance();

            List<Building> buildings = realm.where(Building.class).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
            for (Building building : buildings) {
                List<Apartment> apartments = realm.where(Apartment.class).equalTo("building_id", building.getId()).equalTo("task_id", task_id).findAll();
                apartmentCount += apartments.size();
            }

            return apartmentCount;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public int getResidentsCount() {
        Realm realm = null;
        int residentsCount = 0;
        try {
            realm = Realm.getDefaultInstance();

            List<Building> buildings = realm.where(Building.class).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
            for (Building building : buildings) {
                List<Apartment> apartments = realm.where(Apartment.class).equalTo("building_id", building.getId()).equalTo("task_id", task_id).findAll();
                for (Apartment apartment : apartments) {
                    residentsCount += apartment.getTotalInhabitants();
                }
            }

            return residentsCount;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public String getDisplayName(Context context) {
//        return context.getString(R.string.label_street_short) + " " + getName();
        return getName();
    }

    public Integer getStreetTypeCode() {
        return streetTypeCode == null ? 0 : streetTypeCode;
    }

    public void setStreetTypeCode(Integer streetTypeCode) {
        this.streetTypeCode = streetTypeCode;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer taskId) {
        this.task_id = taskId;
    }

    public static Street getStreet(StreetItem item) {
        Street street = new Street();
        street.setId(item.getId());
        street.setName(item.getTitle());
        street.setOriginalName(item.getTitle());
        street.setStreetTypeCode(item.getStreetTypeCode());
        return street;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public Integer getLocalId() {
        return local_id;
    }

    public void setLocalId(Integer local_id) {
        this.local_id = local_id;
    }

    public String getKato() {
        return kato == null ? "" : kato;
    }

    public void setKato(String kato) {
        this.kato = kato;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public static Comparator<Street> getAscComparator() {
        return (street, t1) -> street.getName().compareTo(t1.getName());
    }

    public static Comparator<Street> getDescComparator() {
        return (street, t1) -> t1.getName().compareTo(street.getName());
    }
}
