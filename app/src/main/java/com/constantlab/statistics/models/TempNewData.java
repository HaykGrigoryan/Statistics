package com.constantlab.statistics.models;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Hayk on 05/01/2018.
 */

public class TempNewData extends RealmObject implements Serializable {
    private String data;
    private Double lat;
    private Double lon;

    public TempNewData(String data) {
        this.data = data;
    }

    public TempNewData(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public TempNewData(){}

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public History.NewData getNewData() {
        History.NewData newData = new History.NewData();
        newData.setData(data);
        return newData;
    }

    public History.ChangePointNewData getChangePointNewData() {
        History.ChangePointNewData changePointNewData = new History.ChangePointNewData();
        changePointNewData.setLat(lat);
        changePointNewData.setLon(lon);
        return changePointNewData;
    }
}