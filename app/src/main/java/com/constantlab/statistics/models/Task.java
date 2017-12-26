package com.constantlab.statistics.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class Task extends RealmObject implements Serializable {
    @PrimaryKey
    private Integer id;
    private String taskName;
    private Integer totalBuildings;
    private Integer totalApartments;
    private Integer totalResidents;
    private RealmList<Street> streetList;

    public Integer getTotalBuildings() {
        return totalBuildings;
    }

    public void setTotalBuildings(Integer totalBuildings) {
        this.totalBuildings = totalBuildings;
    }

    public Integer getTotalApartments() {
        return totalApartments;
    }

    public void setTotalApartments(Integer totalApartments) {
        this.totalApartments = totalApartments;
    }

    public Integer getTotalResidents() {
        return totalResidents;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
