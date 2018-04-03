package com.constantlab.statistics.models.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 19/02/2018.
 */

public class ApartmentSend implements Serializable {
    @SerializedName("flat_id")
    @Expose
    private Integer flatId;
    @SerializedName("is_new")
    @Expose
    private Integer isNew;
    @SerializedName("changes")
    @Expose
    private List<ChangeSend> changes;
    @SerializedName("change_time")
    @Expose
    private String changeTime;


    public ApartmentSend(Integer flatId, Integer isNew) {
        this.flatId = flatId;
        this.isNew = isNew;
        changes = new ArrayList<>();
    }

    public Integer getFlatId() {
        return flatId;
    }

    public void setFlatId(Integer flatId) {
        this.flatId = flatId;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public List<ChangeSend> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeSend> changes) {
        this.changes = changes;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }
}
