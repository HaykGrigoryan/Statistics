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

    @SerializedName("status")
    @Expose
    protected String status;

    @SerializedName("status_description")
    @Expose
    protected String statusDesctiption;

    @SerializedName("date_begin")
    @Expose
    protected String dateBegin;

    @SerializedName("date_end")
    @Expose
    protected String dateEnd;

    @SerializedName("comment")
    @Expose
    protected String comment;
    @SerializedName("NAM_RUS")
    @Expose
    protected String name;


    @SerializedName("details")
    @Expose
    protected TaskItemDetails details;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesctiption() {
        return statusDesctiption;
    }

    public void setStatusDesctiption(String statusDesctiption) {
        this.statusDesctiption = statusDesctiption;
    }

    public String getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskItemDetails getDetails() {
        return details;
    }

    public void setDetails(TaskItemDetails details) {
        this.details = details;
    }

    public static class TaskItemDetails {
        @SerializedName("streets_data")
        @Expose
        protected List<StreetItem> streetItems;
        @SerializedName("cato")
        @Expose
        protected String kato;

        @SerializedName("geo")
        @Expose
        protected List<GeoItem> geoItems;

        public List<StreetItem> getStreetItems() {
            return streetItems;
        }

        public void setStreetItems(List<StreetItem> streetItems) {
            this.streetItems = streetItems;
        }

        public String getKato() {
            return kato;
        }

        public void setKato(String kato) {
            this.kato = kato;
        }

        public List<GeoItem> getGeoItems() {
            return geoItems;
        }

        public void setGeoItems(List<GeoItem> geoItems) {
            this.geoItems = geoItems;
        }
    }
}
