package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.model.ApartmentItem;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class Apartment extends RealmObject {
    @PrimaryKey
    private Integer local_id;
    private Integer id;
    private Integer apartmentNumber;
    private Integer totalInhabitants;
    private Integer apartmentType;
    private String ownerName;
    private String comment;
    private Integer task_id;
    private int building_id;
    private boolean isNew;

    public Integer getApartmentType() {
        return (apartmentType == null) ? 0 : apartmentType;
    }

    public void setApartmentType(Integer apartmentType) {
        this.apartmentType = apartmentType;
    }

    public String getOwnerName() {
        return ownerName == null ? "" : ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getComment() {
        return comment == null ? "" : comment;
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

    public Integer getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(Integer apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Integer getTotalInhabitants() {
        return totalInhabitants == null ? 0 : totalInhabitants;
    }

    public void setTotalInhabitants(Integer totalInhabitants) {
        this.totalInhabitants = totalInhabitants;
    }

    public String getDisplayName(Context context) {
//        return context.getString(R.string.label_apt_no) + " " + getApartmentNumber();
        return String.valueOf(getApartmentNumber());
    }

    public int getBuildingId() {
        return building_id;
    }

    public void setBuildingId(int building_id) {
        this.building_id = building_id;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer task_id) {
        this.task_id = task_id;
    }

    public static Apartment getApartment(ApartmentItem item) {
        Apartment apartment = new Apartment();
        apartment.setId(item.getId());
        apartment.setApartmentNumber(item.getNumber());
        apartment.setTotalInhabitants(item.getInhabitants());
        apartment.setComment(item.getFlatComment());
        apartment.setOwnerName(item.getFlatOwner());
        apartment.setApartmentType(item.getFlatType());

        return apartment;
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
}
