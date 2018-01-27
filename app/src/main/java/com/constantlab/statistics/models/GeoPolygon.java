package com.constantlab.statistics.models;

import com.constantlab.statistics.network.model.GeoItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Hayk on 09/01/2018.
 */

public class GeoPolygon extends RealmObject {
    @PrimaryKey
    private Integer local_id;
    private Integer task_id;
    private Integer poly_id;
    private String data;

    public Integer getLocalId() {
        return local_id;
    }

    public void setLocalId(Integer local_id) {
        this.local_id = local_id;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getPolyId() {
        return poly_id;
    }

    public void setPolyId(Integer poly_id) {
        this.poly_id = poly_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static GeoPolygon getGeoPolygon(Integer taskId, GeoItem item) {
        GeoPolygon polygon = new GeoPolygon();
        polygon.setTaskId(taskId);
        polygon.setPolyId(item.getPolyId());
        polygon.setData(item.getData());
        return polygon;
    }

    public List<LatLng> getPoints() {
        List<LatLng> pointsList = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<List<Double>>>() {
        }.getType();

        List<List<Double>> list = gson.fromJson(data, listType);
        for (List<Double> doubleList : list) {
            if (doubleList.size() == 2) {
                pointsList.add(new LatLng(doubleList.get(0), doubleList.get(1)));
            }
        }

        return pointsList;
    }
}
