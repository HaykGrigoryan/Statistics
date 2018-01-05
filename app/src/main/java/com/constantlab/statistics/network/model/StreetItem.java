package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hayk on 04/01/2018.
 */

public class StreetItem {
    @SerializedName("id")
    @Expose
    protected Integer id;

    @SerializedName("title_ru")
    @Expose
    protected String title;

    @SerializedName("type_code")
    @Expose
    protected Integer streetTypeCode;

    @SerializedName("address_data")
    @Expose
    protected List<AddressData> addressData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStreetTypeCode() {
        return streetTypeCode;
    }

    public void setStreetTypeCode(Integer streetTypeCode) {
        this.streetTypeCode = streetTypeCode;
    }

    public List<AddressData> getAddressData() {
        return addressData;
    }

    public void setAddressData(List<AddressData> addressData) {
        this.addressData = addressData;
    }

    public static class AddressData {
        @SerializedName("street_type_id")
        @Expose
        protected Integer streetType;

        @SerializedName("kato_id")
        @Expose
        protected Integer kato;

        @SerializedName("house_data")
        @Expose
        protected List<BuildingItem> buildingItems;

        public Integer getStreetType() {
            return streetType;
        }

        public void setStreetType(Integer streetType) {
            this.streetType = streetType;
        }

        public Integer getKato() {
            return kato;
        }

        public void setKato(Integer kato) {
            this.kato = kato;
        }

        public List<BuildingItem> getBuildingItems() {
            return buildingItems;
        }

        public void setBuildingItems(List<BuildingItem> buildingItems) {
            this.buildingItems = buildingItems;
        }
    }
}
