package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hayk on 04/01/2018.
 */

public class ApartmentItem {
    @SerializedName("id")
    @Expose
    protected Integer id;

    @SerializedName("quantityofinhabitants")
    @Expose
    protected Integer inhabitants;

    @SerializedName("num")
    @Expose
    protected Integer number;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(Integer inhabitants) {
        this.inhabitants = inhabitants;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
