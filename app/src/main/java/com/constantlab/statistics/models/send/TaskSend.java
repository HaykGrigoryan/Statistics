package com.constantlab.statistics.models.send;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 19/02/2018.
 */

public class TaskSend implements Serializable {
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("data")
    @Expose
    private TaskSendData data;
    @SerializedName("key")
    @Expose
    private String key;


    public TaskSend(Integer taskId, String key) {
        this.taskId = taskId;
        this.key = key;
        this.data = new TaskSendData();
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer task_id) {
        this.taskId = task_id;
    }

    public List<StreetSend> getStreets() {
        return data.getStreets();
    }

    public void setStreets(List<StreetSend> streets) {
        data.streets = streets;
    }

    public class TaskSendData implements Serializable {
        @SerializedName("streets")
        @Expose
        private List<StreetSend> streets;

        public TaskSendData() {
            streets = new ArrayList<>();
        }

        public List<StreetSend> getStreets() {
            return streets;
        }

        public void setStreets(List<StreetSend> streets) {
            this.streets = streets;
        }
    }
}
