package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class Apartment extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String apartmentNumber;
    private Integer totalRooms;
    private Integer totalInhabitants;
    private ApartmentType apartmentType;
    //    private Integer areaSquare;
    private String ownerName;
    private String comment;

    public ApartmentType getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(ApartmentType apartmentType) {
        this.apartmentType = apartmentType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getTotalInhabitants() {
        return totalInhabitants;
    }

    public void setTotalInhabitants(Integer totalInhabitants) {
        this.totalInhabitants = totalInhabitants;
    }

    public String getDisplayName(Context context) {
        return context.getString(R.string.label_apt_no) + " " + getApartmentNumber();
    }
//    public Integer getAreaSquare() {
//        return areaSquare;
//    }
//
//    public void setAreaSquare(Integer areaSquare) {
//        this.areaSquare = areaSquare;
//    }

}
