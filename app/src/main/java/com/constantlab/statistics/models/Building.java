package com.constantlab.statistics.models;

import android.content.Context;
import android.content.Intent;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.Constants;
import com.constantlab.statistics.network.model.BuildingItem;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class Building extends RealmObject {

    @PrimaryKey
    private Integer local_id;
    private Integer id;
    private Integer parentId;
    private String houseNumber;
    private String houseNumberLowerCase;
    private Integer buildingStatus;
    private Integer buildingType;
    private String ownerName;
    private Double latitude;
    private Double longitude;
    private Integer street_id;
    private String kato;
    private String comment;
    private Integer temporaryInhabitants;
    private boolean isNew;
    private boolean isEdited;
    private Integer task_id;
    private Integer historyId;
    private Integer user_id;
    private long change_time;

    private int apartmentCount;
    private int residentsCount;

    public Integer getBuildingType() {
        return buildingType == null ? 0 : buildingType;
    }

    public void setBuildingType(Integer buildingType) {
        this.buildingType = buildingType;
    }

    public Integer getBuildingStatus() {
        return buildingStatus == null ? 0 : buildingStatus;
    }

    public void setBuildingStatus(Integer buildingStatus) {
        this.buildingStatus = buildingStatus;
    }

    public String getOwnerName() {
        return ownerName == null ? "" : ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayAddress(Integer userId) {
//        return context.getString(R.string.label_street_short) + " " + getStreetName() + " " + context.getString(R.string.label_bld_short) + " " + getHouseNumber();
        return getStreetName(userId) + " " + getHouseNumber();
    }

    public String getHouseNumber() {
        return houseNumber == null ? "" : houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        if (houseNumber != null) {
            houseNumberLowerCase = houseNumber.toLowerCase();
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getStreetId() {
        return street_id;
    }

    public void setStreetId(Integer streetId) {
        this.street_id = streetId;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer task_id) {
        this.task_id = task_id;
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

    public Integer getTemporaryInhabitants() {
        return temporaryInhabitants == null ? 0 : temporaryInhabitants;
    }

    public void setTemporaryInhabitants(Integer temporaryInhabitants) {
        this.temporaryInhabitants = temporaryInhabitants;
    }

    public String getStreetName(Integer userId) {
        String street = "";
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            street = realm.where(Street.class).equalTo("user_id", userId).equalTo("id", street_id).findFirst().getName();
            return street;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public static Building getBuilding(BuildingItem item) {
        Building building = new Building();
        building.setId(item.getId());
        building.setHouseNumber(item.getBuildingNumber());
        building.setBuildingType(item.getBuildingType());
        building.setBuildingStatus(item.getBuildingStatus());
        building.setComment(item.getBuildingComment());
        building.setOwnerName(item.getBuildingOwner());
        building.setLatitude(item.getBuildingLat());
        building.setLongitude(item.getBuildingLng());
        building.setTemporaryInhabitants(item.getBuildingPeople());
        building.setParentId(item.getParentId());
        return building;
    }


    public static void refreshCounts(Integer userId, Integer task_id, Integer street_id, Integer buildingId, Realm realm) {
        Building building = realm.where(Building.class).equalTo("user_id", userId).equalTo("task_id", task_id).equalTo("street_id", street_id).equalTo("id", buildingId).findFirst();
        if (building != null) {
            int aptCount = 0;
            int residentsCount = 0;
            RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("task_id", task_id).equalTo("building_id", buildingId).findAll();
            aptCount = apartments.size();
            if (!Building.isFlatLevelEnabled(building.getBuildingType(), building.getBuildingStatus())) {
                residentsCount = building.getTemporaryInhabitants();
            } else {
                for (Apartment apartment : apartments) {
                    residentsCount += apartment.getTotalInhabitants();
                }
            }

            building.setApartmentCount(aptCount);
            building.setResidentsCount(residentsCount);
            realm.insertOrUpdate(building);
        }
    }

//    public Integer getApartmentCount(Integer userId) {
//        Realm realm = null;
//        int apartmentCount = 0;
//        try {
//            realm = Realm.getDefaultInstance();
//
//            List<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("building_id", id).equalTo("task_id", task_id).findAll();
//            apartmentCount = apartments.size();
//
//            return apartmentCount;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }
//
//    public Integer getApartmentInhabitantsCount(Integer userId) {
//        Realm realm = null;
//        int inhabitantsCount = 0;
//        try {
//            realm = Realm.getDefaultInstance();
//
//            List<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("building_id", id).equalTo("task_id", task_id).findAll();
//            for (Apartment apartment : apartments) {
//                inhabitantsCount += apartment.getTotalInhabitants();
//            }
//
//            return inhabitantsCount;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

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

    public String getComment() {
        return comment == null ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static boolean isTypeClosedInstitution(Integer buildingType) {
        for (Integer type : Constants.BUILDING_TYPE_CLOSED_INSTITUTIONS) {
            if (type.equals(buildingType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTypeSpetialInstitution(Integer buildingType) {
        for (Integer type : Constants.BUILDING_TYPE_SPECIAL_INSTITUTIONS) {
            if (type.equals(buildingType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStatusInactive(Integer buildingType) {
        for (Integer type : Constants.BUILDING_STATUS_INACTIVE) {
            if (type.equals(buildingType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFlatLevelEnabled(Integer type, Integer status) {
        return !Building.isStatusInactive(status) && !Building.isTypeSpetialInstitution(type) && !Building.isTypeClosedInstitution(type);
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

    public int getApartmentCount() {
        return apartmentCount;
    }

    public void setApartmentCount(int apartmentCount) {
        this.apartmentCount = apartmentCount;
    }

    public int getResidentsCount() {
        return residentsCount;
    }

    public void setResidentsCount(int residentsCount) {
        this.residentsCount = residentsCount;
    }
}
