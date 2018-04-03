package com.constantlab.statistics.network.model;

import com.constantlab.statistics.models.Building;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hayk on 04/01/2018.
 */

public class StreetItem {
    @SerializedName("street_id")
    @Expose
    protected Integer id;

    @SerializedName("street_name")
    @Expose
    protected String streetName;

    @SerializedName("street_type_id")
    @Expose
    protected Integer streetTypeCode;

    @SerializedName("house_data")
    @Expose
    protected List<BuildingItem> buildings;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStreetTypeCode() {
        return streetTypeCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public List<BuildingItem> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingItem> buildings) {
        this.buildings = buildings;
    }

    //    public static class AddressData {
//        @SerializedName("street_type_id")
//        @Expose
//        protected Integer streetType;
//
//        @SerializedName("kato_id")
//        @Expose
//        protected Integer kato;
//
//        @SerializedName("house_data")
//        @Expose
//        protected List<BuildingItem> buildingItems;
//
//        public Integer getStreetType() {
//            return streetType;
//        }
//
//        public void setStreetType(Integer streetType) {
//            this.streetType = streetType;
//        }
//
//        public Integer getKato() {
//            return kato;
//        }
//
//        public void setKato(Integer kato) {
//            this.kato = kato;
//        }
//
//        public List<BuildingItem> getBuildingItems() {
//            return buildingItems;
//        }
//
//        public void setBuildingItems(List<BuildingItem> buildingItems) {
//            this.buildingItems = buildingItems;
//        }
//    }
}
