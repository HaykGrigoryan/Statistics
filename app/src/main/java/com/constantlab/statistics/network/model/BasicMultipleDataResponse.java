package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hayk on 27/01/2017.
 */

public class BasicMultipleDataResponse<E> implements Serializable {
    @SerializedName("status")
    @Expose
    protected String status;

    @SerializedName("data")
    @Expose
    protected List<E> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return status.equals("1");
    }

    public boolean isSuccessNestedStatus() {
        return status.equals("2");
    }
}
