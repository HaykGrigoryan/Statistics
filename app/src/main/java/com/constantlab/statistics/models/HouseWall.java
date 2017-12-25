package com.constantlab.statistics.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sunny Kinger on 07-12-2017.
 */

public class HouseWall extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String titleKz;
    private String titleRu;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleKz() {
        return titleKz;
    }

    public void setTitleKz(String titleKz) {
        this.titleKz = titleKz;
    }

    public String getTitleRu() {
        return titleRu;
    }

    public void setTitleRu(String titleRu) {
        this.titleRu = titleRu;
    }

    @Override
    public String toString() {
        return titleRu;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HouseWall houseWall = (HouseWall) o;

        return id.equals(houseWall.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
