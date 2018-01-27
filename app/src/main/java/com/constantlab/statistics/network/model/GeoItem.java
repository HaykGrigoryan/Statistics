package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hayk on 09/01/2018.
 */

public class GeoItem implements Serializable {
    @SerializedName("poly_id")
    @Expose
    int polyId;

    @SerializedName("data")
    @Expose
    String data;

    public int getPolyId() {
        return polyId;
    }

    public void setPolyId(int polyId) {
        this.polyId = polyId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
