package com.constantlab.statistics.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hayk on 04/01/2018.
 */

public class ApartmentItem {

    @SerializedName("flat_id")
    @Expose
    protected Integer id;

    @SerializedName("flat_people")
    @Expose
    protected Integer inhabitants;

    @SerializedName("flat_num")
    @Expose
    protected String number;

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
        return inhabitants == null?0:inhabitants;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFlatOwner() {
        return flatOwner;
    }

    public String getFlatComment() {
        return flatComment;
    }

    public Integer getFlatType() {
        return flatType;
    }
}
