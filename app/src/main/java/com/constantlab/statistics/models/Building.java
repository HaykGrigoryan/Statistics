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
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class Building extends RealmObject {

    @PrimaryKey
    private Integer local_id;
    private Integer id;
    private String houseNumber;
    private Integer buildingStatus;
    private Integer buildingType;
    private String ownerName;
    private Double latitude;
    private Double longitude;
    private Integer street_id;
    private String kato;
    private String streetName;
    private String comment;
    private Integer temporaryInhabitants;
    private Integer streetType;
    private boolean isNew;
    private Integer task_id;
    private Integer historyId;

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

    public String getDisplayAddress(Context context) {
//        return context.getString(R.string.label_street_short) + " " + getStreetName() + " " + context.getString(R.string.label_bld_short) + " " + getHouseNumber();
        return getStreetName() + " " + getHouseNumber();
    }

    public String getHouseNumber() {
        return houseNumber == null ? "" : houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
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

    public String getStreetName() {
        String street = streetName;
        if (street == null) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                street = realm.where(Street.class).equalTo("id", street_id).findFirst().getName();
                return street;
            } finally {
                if (realm != null)
                    realm.close();
            }
        }
        return street;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Integer getStreetType() {
        return streetType;
    }

    public void setStreetType(Integer streetType) {
        this.streetType = streetType;
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

        return building;
    }

    public Integer getApartmentCount() {
        Realm realm = null;
        int apartmentCount = 0;
        try {
            realm = Realm.getDefaultInstance();

            List<Apartment> apartments = realm.where(Apartment.class).equalTo("building_id", id).equalTo("task_id", task_id).findAll();
            apartmentCount = apartments.size();

            return apartmentCount;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public Integer getApartmentInhabitantsCount() {
        Realm realm = null;
        int inhabitantsCount = 0;
        try {
            realm = Realm.getDefaultInstance();

            List<Apartment> apartments = realm.where(Apartment.class).equalTo("building_id", id).equalTo("task_id", task_id).findAll();
            for (Apartment apartment : apartments) {
                inhabitantsCount += apartment.getTotalInhabitants();
            }

            return inhabitantsCount;
        } finally {
            if (realm != null)
                realm.close();
        }
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
}
