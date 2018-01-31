package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hayk on 04/01/2018.
 */

public class ApartmentItem {
    @SerializedName("id")
    @Expose
    protected Integer id;

    @SerializedName("flat_people")
    @Expose
    protected Integer inhabitants;

    @SerializedName("num")
    @Expose
    protected Integer number;

    @SerializedName("flat_owner")
    @Expose
    protected String flatOwner;

    @SerializedName("flat_comment")
    @Expose
    protected String flatComment;

    @SerializedName("flat_type_id")
    @Expose
    protected Integer flatType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(Integer inhabitants) {
        this.inhabitants = inhabitants;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFlatOwner() {
        return flatOwner;
    }

    public void setFlatOwner(String flatOwner) {
        this.flatOwner = flatOwner;
    }

    public String getFlatComment() {
        return flatComment;
    }

    public void setFlatComment(String flatComment) {
        this.flatComment = flatComment;
    }

    public Integer getFlatType() {
        return flatType;
    }

    public void setFlatType(Integer flatType) {
        this.flatType = flatType;
    }
}
