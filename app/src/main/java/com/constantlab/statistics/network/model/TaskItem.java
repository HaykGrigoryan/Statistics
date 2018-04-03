package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hayk on 03/01/2018.
 */

public class TaskItem {
    @SerializedName("task_id")
    @Expose
    protected Integer taskId;

    @SerializedName("begin_date")
    @Expose
    protected String dateBegin;

    @SerializedName("task_comment")
    @Expose
    protected String comment;

    @SerializedName("kato_id")
    @Expose
    protected Integer katoId;
    @SerializedName("kato_code")
    @Expose
    protected String katoCode;
    @SerializedName("kato_name")
    @Expose
    protected String katoName;

    @SerializedName("streets_details")
    @Expose
    protected List<StreetItem> streetItems;


    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getDateBegin() {
        return dateBegin;
    }

    public String getComment() {
        return comment;
    }

    public Integer getKatoId() {
        return katoId;
    }

    public String getKatoCode() {
        return katoCode == null ? "" : katoCode.trim();
    }

    public String getKatoName() {
        return katoName;
    }

    public List<StreetItem> getStreetItems() {
        return streetItems;
    }

}
