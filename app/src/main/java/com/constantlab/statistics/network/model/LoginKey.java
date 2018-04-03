package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hayk on 03/01/2018.
 */

public class LoginKey implements Serializable {
    @SerializedName("data")
    @Expose
    protected String data;

    @SerializedName("status")
    @Expose
    protected Integer status;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKey() {
        return data;
    }

    public void setKey(String key) {
        this.data = key;
    }
}
