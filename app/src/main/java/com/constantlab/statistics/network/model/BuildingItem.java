package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hayk on 04/01/2018.
 */

public class BuildingItem {
    @SerializedName("id")
    @Expose
    protected Integer id;

    @SerializedName("house_type_id")
    @Expose
    protected Integer buildingType;

    @SerializedName("house_number")
    @Expose
    protected String buildingNumber;


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

    public void setBuildingType(Integer buildingType) {
        this.buildingType = buildingType;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public List<ApartmentItem> getApartmentItems() {
        return apartmentItems;
    }

    public void setApartmentItems(List<ApartmentItem> apartmentItems) {
        this.apartmentItems = apartmentItems;
    }
}
