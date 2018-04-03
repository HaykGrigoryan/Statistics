package com.constantlab.statistics.models.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 19/02/2018.
 */

public class BuildingSend implements Serializable {
    @SerializedName("house_id")
    @Expose
    private Integer houseId;
    @SerializedName("is_new")
    @Expose
    private Integer isNew;
    @SerializedName("changes")
    @Expose
    private List<ChangeSend> changes;
    @SerializedName("flats")
    @Expose
    private List<ApartmentSend> flats;
    @SerializedName("change_time")
    @Expose
    private String changeTime;


    public BuildingSend(Integer houseId, Integer isNew) {
        this.houseId = houseId;
        this.isNew = isNew;
        this.changes = new ArrayList<>();
        this.flats = new ArrayList<>();
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
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

    public List<ApartmentSend> getFlats() {
        return flats;
    }

    public void setFlats(List<ApartmentSend> flats) {
        this.flats = flats;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }
}
