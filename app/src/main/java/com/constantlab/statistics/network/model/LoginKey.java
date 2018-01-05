package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hayk on 03/01/2018.
 */

public class LoginKey {
    @SerializedName("status")
    @Expose
    protected String status;

    @SerializedName("key")
    @Expose
    protected String key;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
