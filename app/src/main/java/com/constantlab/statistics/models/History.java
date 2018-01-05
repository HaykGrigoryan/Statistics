package com.constantlab.statistics.models;

import android.content.Context;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.utils.GsonUtils;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hayk on 27/12/2017.
 */

public class History extends RealmObject implements Serializable {

    @PrimaryKey
    private Integer id;
    @SerializedName("key")
    private String key;
    @SerializedName("task_id")
    private Integer task_id;
    @SerializedName("change_type")
    private Integer change_type;
    @SerializedName("object_type")
    private Integer object_type;
    @SerializedName("object_id")
    private Integer object_id;
    @SerializedName("new_data")
    private TempNewData new_data;
    private boolean synced;

    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTitle(Context context) {
        return ChangeType.getDescriptionById(context, change_type);
    }


    public String getMessage(Context context) {
        String message = "";
        switch (change_type) {
            case 5:
                switch (object_type) {
                    case 1:
                        message = StreetType.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                    case 2:
                        message = BuildingType.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                    case 3:
                        message = ApartmentType.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                }

                break;
            default:
                message = new_data.getData();

        }
        return message;
    }

    public Integer getTaskId() {
        return task_id;
    }

    public void setTaskId(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getChangeType() {
        return change_type;
    }

    public void setChangeType(Integer change_type) {
        this.change_type = change_type;
    }

    public Integer getObjectType() {
        return object_type;
    }

    public void setObjectType(Integer object_type) {
        this.object_type = object_type;
    }

    public Integer getObjectId() {
        return object_id;
    }

    public void setObjectId(Integer object_id) {
        this.object_id = object_id;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public TempNewData getNewData() {
        return new_data;
    }

    public void setNewData(TempNewData new_data) {
        this.new_data = new_data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress(Context context) {
        String address = "";
        Realm realm = null;
        Street street = null;
        Building building = null;
        Apartment apartment = null;
        try {
            realm = RealmManager.getInstance().getDefaultInstance(context);
            switch (object_type) {
                case 1:
                    street = realm.where(Street.class).equalTo("task_id", task_id).equalTo("id", object_id).findFirst();
                    address += context.getString(R.string.label_street_short) + " " + street.getName();
                    break;
                case 2:
                    building = realm.where(Building.class).equalTo("task_id", task_id).equalTo("id", object_id).findFirst();
                    street = realm.where(Street.class).equalTo("task_id", task_id).equalTo("id", building.getStreetId()).findFirst();
                    address += context.getString(R.string.label_street_short) + " " + street.getName() + ", " + context.getString(R.string.label_bld_short) + " " + building.getHouseNumber();
                    break;
                case 3:
                    apartment = realm.where(Apartment.class).equalTo("task_id", task_id).equalTo("id", object_id).findFirst();
                    building = realm.where(Building.class).equalTo("task_id", task_id).equalTo("id", apartment.getBuildingId()).findFirst();
                    street = realm.where(Street.class).equalTo("task_id", task_id).equalTo("id", building.getStreetId()).findFirst();
                    address += context.getString(R.string.label_street_short) + " " + street.getName() + ", " + context.getString(R.string.label_bld_short) + " " + building.getHouseNumber() + ", " + context.getString(R.string.label_apt_no) + " " + apartment.getApartmentNumber();
                    break;
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static List<History> getNotSyncedHistory() {
        Realm realm = null;
        List<History> historyList = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            historyList = realm.copyFromRealm(realm.where(History.class).equalTo("synced", false).findAll());

        } finally {
            if (realm != null)
                realm.close();
        }

        return historyList;

    }

    public void setHistorySynced() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                synced = true;
                realmObject.insertOrUpdate(this);
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }


    public static class ChangePointNewData {
        private Double lat;
        private Double lon;

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
    }

    public static class NewData {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


}
