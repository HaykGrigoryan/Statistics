package com.constantlab.statistics.models;

import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.utils.DateUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class Task extends RealmObject implements Serializable {
    @PrimaryKey
    private Integer task_id;
    private String name;
    private String date_begin;
    private String date_end;
    private String comment;
    private String status_description;
    private String kato;
    private Integer totalBuildings;
    private Integer totalApartments;
    private Integer totalResidents;
    private RealmList<Street> streetList;

    public void setTotalBuildings(Integer totalBuildings) {
        this.totalBuildings = totalBuildings;
    }

    public void setTotalApartments(Integer totalApartments) {
        this.totalApartments = totalApartments;
    }

    public void setTotalResidents(Integer totalResidents) {
        this.totalResidents = totalResidents;
    }

    public RealmList<Street> getStreetList() {
        return streetList;
    }

    public void setStreetList(RealmList<Street> streetList) {
        this.streetList = streetList;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer taskId) {
        this.task_id = taskId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name + " #" + task_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateBegin() {
        return date_begin;
    }

    public void setDateBegin(String dateBegin) {
        this.date_begin = dateBegin;
    }

    public String getDateEnd() {
        return date_end;
    }

    public void setDateEnd(String dateEnd) {
        this.date_end = dateEnd;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatusDescription() {
        return status_description;
    }

    public void setStatusDescription(String statusDescription) {
        this.status_description = statusDescription;
    }

    public String getKato() {
        return kato;
    }

    public void setKato(String kato) {
        this.kato = kato;
    }

    public static Task getTask(TaskItem item) {
        Task task = new Task();
        task.setTaskId(item.getTaskId());
        task.setName(item.getName());
        task.setDateBegin(item.getDateBegin());
        task.setDateEnd(item.getDateEnd());
        task.setComment(item.getComment());
        task.setStatusDescription(item.getStatus());

        return task;
    }

    public String getDisplayDateBegin() {
        return DateUtils.getDisplayDate(date_begin);
    }

}
