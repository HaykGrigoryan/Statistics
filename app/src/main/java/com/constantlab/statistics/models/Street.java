package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.model.StreetItem;

import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 26/12/2017.
 */

public class Street extends RealmObject {
    @PrimaryKey
    private Integer local_id;
    private Integer id;
    private String name;
    private String nameLowerCase;
    private String originalName;
    private Integer streetTypeCode;
    private Integer task_id;
    private String kato;
    private Integer historyId;
    private boolean isNew;
    private boolean isEdited;
    private Integer user_id;
    private long change_time;

    private int buildingCount;
    private int apartmentCount;
    private int residentsCount;


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
        if (name != null) {
            nameLowerCase = name.toLowerCase();
        }
    }

    public String getOriginalName() {
        return originalName == null ? "" : originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }


    public static void refreshCounts(Integer userId, Integer taskId, Integer streetId, Realm realm) {
        int buildingCount = 0;
        int apartmentCount = 0;
        int residentsCount = 0;

        Street street = realm.where(Street.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("id", streetId).findFirst();
        if (street != null) {
            RealmResults<Building> buildings = realm.where(Building.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("street_id", street.getId()).findAll();
            buildingCount = buildings.size();
            for (Building building : buildings) {
                apartmentCount += building.getApartmentCount();
                residentsCount += building.getResidentsCount();
            }

            street.setBuildingCount(buildingCount);
            street.setApartmentCount(apartmentCount);
            street.setResidentsCount(residentsCount);
            realm.insertOrUpdate(street);
        }


    }

    public int getBuildingCount() {
        return buildingCount;
    }

    public int getApartmentCount() {
        return apartmentCount;
    }

    public int getResidentsCount() {
        return residentsCount;
    }

    public void setBuildingCount(int buildingCount) {
        this.buildingCount = buildingCount;
    }

    public void setApartmentCount(int apartmentCount) {
        this.apartmentCount = apartmentCount;
    }

    public void setResidentsCount(int residentsCount) {
        this.residentsCount = residentsCount;
    }


    //    public int getBuildingsCount(Integer userId) {
//        Realm realm = null;
//        int buildingCount = 0;
//        try {
//            realm = Realm.getDefaultInstance();
//            List<Building> buildings = realm.where(Building.class).equalTo("user_id", userId).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
//            buildingCount = buildings.size();
//
//            return buildingCount;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }
//
//    public int getApartmentCount(Integer userId) {
//        Realm realm = null;
//        int apartmentCount = 0;
//        try {
//            realm = Realm.getDefaultInstance();
//
//            List<Building> buildings = realm.where(Building.class).equalTo("user_id", userId).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
//            for (Building building : buildings) {
//                List<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("building_id", building.getId()).equalTo("task_id", task_id).findAll();
//                apartmentCount += apartments.size();
//            }
//
//            return apartmentCount;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }
//
//    public int getResidentsCount(Integer userId) {
//        Realm realm = null;
//        int residentsCount = 0;
//        try {
//            realm = Realm.getDefaultInstance();
//
//            List<Building> buildings = realm.where(Building.class).equalTo("user_id", userId).equalTo("street_id", id).equalTo("task_id", task_id).findAll();
//            for (Building building : buildings) {
//                List<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("building_id", building.getId()).equalTo("task_id", task_id).findAll();
//                for (Apartment apartment : apartments) {
//                    residentsCount += apartment.getTotalInhabitants();
//                }
//            }
//
//            return residentsCount;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

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
        street.setName(item.getStreetName());
        street.setOriginalName(item.getStreetName());
        street.setStreetTypeCode(item.getStreetTypeCode());
        return street;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
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

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public long getChangeTime() {
        return change_time;
    }

    public void setChangeTime(long change_time) {
        this.change_time = change_time;
    }
}
