package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class Building extends RealmObject {

    @PrimaryKey
    private Integer id;
    private Address address;
    private String houseNumber;
    private String territoryName;
    private BuildingStatus buildingStatus;
    private BuildingType buildingType;
    private Boolean markedOnMap;
    private String ownerName;
    private Integer floorNumber;
    private Integer totalFlats;
    private Double latitude;
    private Double longitude;
    private Float areaSq;
    private RealmList<Apartment> apartmentList;

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public String getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(String territoryName) {
        this.territoryName = territoryName;
    }

    public BuildingStatus getBuildingStatus() {
        return buildingStatus;
    }

    public void setBuildingStatus(BuildingStatus buildingStatus) {
        this.buildingStatus = buildingStatus;
    }

    public Boolean getMarkedOnMap() {
        return markedOnMap;
    }

    public void setMarkedOnMap(Boolean markedOnMap) {
        this.markedOnMap = markedOnMap;
    }

    public String getOwnerName() {
        return ownerName;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDisplayAddress(Context context) {
        return context.getString(R.string.label_street_short) + " " + getAddress().getStreet().getTitleRu() + " " + context.getString(R.string.label_bld_short) + " " + getHouseNumber();
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Integer getTotalFlats() {
        return totalFlats;
    }

    public void setTotalFlats(Integer totalFlats) {
        this.totalFlats = totalFlats;
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

    public Float getAreaSq() {
        return areaSq;
    }

    public void setAreaSq(Float areaSq) {
        this.areaSq = areaSq;
    }

    public RealmList<Apartment> getApartmentList() {
        return apartmentList;
    }

    public void setApartmentList(RealmList<Apartment> apartmentList) {
        this.apartmentList = apartmentList;
    }

}
