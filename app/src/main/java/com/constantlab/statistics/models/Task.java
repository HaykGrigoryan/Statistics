package com.constantlab.statistics.models;

import android.content.Context;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.model.GeoItem;
import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.utils.DateUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class Task extends RealmObject implements Serializable {
    @PrimaryKey
    private Integer local_id;
    private Integer task_id;
    private String date_begin;
    private String comment;
    private Integer katoId;
    private String katoCode;
    private String katoName;
    private Integer user_id;

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer taskId) {
        this.task_id = taskId;
    }

    public String getDisplayName() {
        return "№" + task_id + " " + getKatoName();
    }

    public String getComment() {
        return (comment == null ? "" : comment);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDisplayKato(Context context) {
        return context.getString(R.string.label_kato) + ": " + (katoCode == null ? "" : katoCode);
    }

    public Integer getId() {
        return task_id;
    }

    public void setId(Integer task_id) {
        this.task_id = task_id;
    }

    public String getDateBegin() {
        return date_begin;
    }

    public void setDateBegin(String date_begin) {
        this.date_begin = date_begin;
    }

    public Integer getKatoId() {
        return katoId;
    }

    public void setKatoId(Integer katoId) {
        this.katoId = katoId;
    }

    public String getKatoCode() {
        return katoCode;
    }

    public void setKatoCode(String katoCode) {
        this.katoCode = katoCode;
    }

    public String getKatoName() {
        return (katoName == null ? "" : katoName);
    }

    public void setKatoName(String katoName) {
        this.katoName = katoName;
    }

    public Integer getLocalId() {
        return local_id;
    }

    public void setLocalId(Integer local_id) {
        this.local_id = local_id;
    }

    public static Task getTask(TaskItem item) {
        Task task = new Task();
        task.setTaskId(item.getTaskId());
        task.setDateBegin(item.getDateBegin());
        task.setComment(item.getComment());
        task.setKatoId(item.getKatoId());
        task.setKatoCode(item.getKatoCode());
        task.setKatoName(item.getKatoName());

        return task;
    }

    public String getDisplayDateBegin(Context context) {
        return context.getString(R.string.label_date) + ": " + DateUtils.getDisplayDate(date_begin);
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }
}
