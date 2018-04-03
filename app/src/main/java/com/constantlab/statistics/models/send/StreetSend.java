package com.constantlab.statistics.models.send;

import com.constantlab.statistics.models.Street;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 19/02/2018.
 */

public class StreetSend implements Serializable {
    @SerializedName("street_id")
    @Expose
    private Integer streetId;
    @SerializedName("is_new")
    @Expose
    private Integer isNew;
    @SerializedName("changes")
    @Expose
    private List<ChangeSend> changes;
    @SerializedName("houses")
    @Expose
    private List<BuildingSend> houses;

    @SerializedName("change_time")
    @Expose
    private String changeTime;

    public StreetSend(Integer streetId) {
        this.streetId = streetId;
        this.changes = new ArrayList<>();
        this.houses = new ArrayList<>();
    }

    public StreetSend(Integer streetId, Integer isNew) {
        this.streetId = streetId;
        this.isNew = isNew;
        this.changes = new ArrayList<>();
        this.houses = new ArrayList<>();
    }

    public StreetSend() {

    }

    public Integer getStreetId() {
        return streetId;
    }

    public void setStreetId(Integer streetId) {
        this.streetId = streetId;
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

    public List<BuildingSend> getHouses() {
        return houses;
    }

    public void setHouses(List<BuildingSend> houses) {
        this.houses = houses;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }
}
