package com.constantlab.statistics.models.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hayk on 19/02/2018.
 */

public class ChangeSend implements Serializable {
    @SerializedName("change_type")
    @Expose
    private Integer changeType;
    @SerializedName("new_data")
    @Expose
    private String newData;
    @SerializedName("old_data")
    @Expose
    private String oldData;

    public ChangeSend(Integer changeType, String newData, String oldData) {
        this.changeType = changeType;
        this.newData = newData;
        this.oldData = oldData;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }
}
