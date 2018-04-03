package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hayk on 31/01/2017.
 */

public class BasicSingleDataResponse<E> implements Serializable {
    @SerializedName("status")
    @Expose
    protected Integer status;

    @SerializedName("message")
    @Expose
    protected String message;

    //    @SerializedName("temp_id")
//    @Expose
//    protected Integer temp_id;
    //
    @SerializedName("data")
    @Expose
    protected E data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return status.equals(1);
    }

    public boolean isSuccessNestedStatus() {
        return status.equals(2);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
//    public Integer getTempId() {
//        return temp_id;
//    }
//
//    public void setTempId(Integer temp_id) {
//        this.temp_id = temp_id;
//    }
}
