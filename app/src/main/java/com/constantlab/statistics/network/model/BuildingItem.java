package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hayk on 04/01/2018.
 */

public class BuildingItem {
    @SerializedName("house_id")
    @Expose
    protected Integer id;

    @SerializedName("parent_id")
    @Expose
    protected Integer parentId;

    @SerializedName("house_type_id")
    @Expose
    protected Integer buildingType;

    @SerializedName("house_status_id")
    @Expose
    protected Integer buildingStatus;

    @SerializedName("house_num")
    @Expose
    protected String buildingNumber;

    @SerializedName("house_people")
    @Expose
    protected Integer buildingPeople;

    @SerializedName("house_comment")
    @Expose
    protected String buildingComment;

    @SerializedName("lat")
    @Expose
    protected Double buildingLat;

    @SerializedName("lng")
    @Expose
    protected Double buildingLng;

    @SerializedName("house_owner")
    @Expose
    protected String buildingOwner;

    @SerializedName("flat_data")
    @Expose
    protected List<ApartmentItem> apartmentItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingType() {
        return buildingType;
    }


    public String getBuildingNumber() {
        return buildingNumber;
    }


    public List<ApartmentItem> getApartmentItems() {
        return apartmentItems;
    }

    public void setApartmentItems(List<ApartmentItem> apartmentItems) {
        this.apartmentItems = apartmentItems;
    }

    public Integer getBuildingStatus() {
        return buildingStatus;
    }


    public Integer getBuildingPeople() {
        return buildingPeople;
    }


    public String getBuildingComment() {
        return buildingComment;
    }

    public Double getBuildingLat() {
        return buildingLat;
    }


    public Double getBuildingLng() {
        return buildingLng;
    }


    public String getBuildingOwner() {
        return buildingOwner;
    }

    public Integer getParentId() {
        return parentId;
    }

}
