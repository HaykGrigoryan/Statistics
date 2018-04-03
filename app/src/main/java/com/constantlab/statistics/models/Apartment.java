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
    private String apartmentNumber;
    private String apartmentNumberLowerCase;
    private Integer totalInhabitants;
    private Integer apartmentType;
    private String ownerName;
    private String comment;
    private Integer task_id;
    private int building_id;
    private boolean isNew;
    private boolean isEdited;
    private Integer user_id;
    private long change_time;

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

    public String getApartmentNumber() {
        return apartmentNumber == null ? "" : apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        if (apartmentNumber != null) {
            apartmentNumberLowerCase = apartmentNumber.toLowerCase();
        }
    }

    public Integer getTotalInhabitants() {
        return totalInhabitants == null ? 0 : totalInhabitants;
    }

    public void setTotalInhabitants(Integer totalInhabitants) {
        this.totalInhabitants = totalInhabitants;
    }

    public String getDisplayName(Context context) {
//        return context.getString(R.string.label_apt_no) + " " + getApartmentNumber();
        return getApartmentNumber();
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
