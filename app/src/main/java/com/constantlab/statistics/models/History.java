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

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
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
    @SerializedName("temp_object_id")
    private Integer temp_object_id;
    @SerializedName("new_data")
    private TempNewData new_data;
    @SerializedName("old_data")
    private TempNewData old_data;
    @SerializedName("user_id")
    private Integer user_id;

    private Integer reference_id;
    private boolean synced;
    private boolean inactive;

    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTitle(Context context) {
        return RealmManager.getInstance().getChangeTypeNameById(change_type);
//        return ChangeType.getDescriptionById(context, change_type);
    }


    public String getMessage(Context context) {
        String message = "";
        switch (change_type) {
            case 5:
                switch (object_type) {
                    case 1:
                        message = StreetType.getDescriptionById(Integer.valueOf(new_data.getData()));
                        break;
                    case 2:
                        message = RealmManager.getInstance().getBuildingTypeNameById(Integer.valueOf(new_data.getData()));// BuildingType.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                    case 3:
                        message = RealmManager.getInstance().getApartmentTypeNameById(Integer.valueOf(new_data.getData()));//ApartmentType.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                }

                break;
            case 6:
                switch (object_type) {
                    case 2:
                        message = RealmManager.getInstance().getBuildingStatusTypeNameById(Integer.valueOf(new_data.getData()));// BuildingStatus.getDescriptionById(context, Integer.valueOf(new_data.getData()));
                        break;
                }
                break;
            case 4:
            case 15:
            case 16:
                message = old_data.getData();
                break;
            default:
                message = new_data.getData();
//                message = old_data.getData();

        }
        return message;
    }

    public GeoPoint getPoint() {
        if (change_type == 14 && new_data != null && new_data.getLat() != null && new_data.getLon() != null) {
            return new GeoPoint(new_data.getLat(), new_data.getLon());
        }
        return null;
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

    public Integer getTempObjectId() {
        return temp_object_id;
    }

    public void setTempObjectId(Integer temp_object_id) {
        this.temp_object_id = temp_object_id;
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

    public TempNewData getOldData() {
        return old_data;
    }

    public void setOldData(TempNewData oldData) {
        this.old_data = oldData;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getReferenceId() {
        return reference_id;
    }

    public void setReferenceId(Integer referenceId) {
        this.reference_id = referenceId;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public boolean isNew() {
        return change_type != null && (change_type.equals(1) || change_type.equals(2) || change_type.equals(3));
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getAddress(Context context, Integer userId) {
        String address = "";
        Realm realm = null;
        Street street = null;
        Building building = null;
        Apartment apartment = null;
        try {
            realm = RealmManager.getInstance().getDefaultInstance(context);
            switch (object_type) {
                case 1:
                    street = realm.where(Street.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", temp_object_id).findFirst();
//                    address += context.getString(R.string.label_street_short) + " " + street.getName();
                    address += street.getName();
                    break;
                case 2:
                    building = realm.where(Building.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", temp_object_id).findFirst();
                    street = realm.where(Street.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", building.getStreetId()).findFirst();
//                    address += context.getString(R.string.label_street_short) + " " + street.getName() + ", " + context.getString(R.string.label_bld_short) + " " + building.getHouseNumber();
                    address += street.getName() + ", " + building.getHouseNumber();
                    break;
                case 3:
                    apartment = realm.where(Apartment.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", temp_object_id).findFirst();
                    building = realm.where(Building.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", apartment.getBuildingId()).findFirst();
                    street = realm.where(Street.class).equalTo("user_id",userId).equalTo("task_id", task_id).equalTo("id", building.getStreetId()).findFirst();
//                    address += context.getString(R.string.label_street_short) + " " + street.getName() + ", " + context.getString(R.string.label_bld_short) + " " + building.getHouseNumber() + ", " + context.getString(R.string.label_apt_no) + " " + apartment.getApartmentNumber();
                    address += street.getName() + ", " + building.getHouseNumber() + ", " + apartment.getApartmentNumber();
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

    public static List<History> getNotSyncedHistories(Integer userId) {
        Realm realm = null;
        List<History> historyList = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            historyList = realm.copyFromRealm(realm.where(History.class).equalTo("user_id", userId).equalTo("synced", false).equalTo("inactive", false).findAll());

        } finally {
            if (realm != null)
                realm.close();
        }

        return historyList;

    }

    public static List<History> getAllHistories(Integer userId) {
        Realm realm = null;
        List<History> historyList = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            historyList = realm.copyFromRealm(realm.where(History.class).equalTo("user_id", userId).findAll());

        } finally {
            if (realm != null)
                realm.close();
        }

        return historyList;

    }

    public static History getNotSyncedHistory(Integer userId) {
        Realm realm = null;
        History history = null;
        try {
            realm = Realm.getDefaultInstance();
            History historyRealm = realm.where(History.class).equalTo("user_id", userId).equalTo("synced", false).equalTo("inactive", false).sort("id", Sort.ASCENDING).findFirst();
            if (historyRealm != null) {
                history = realm.copyFromRealm(historyRealm);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return history;

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

    public void updateReferenceHisrories(Context context, Integer tempId, Integer userId) {
        Realm realm = null;

        try {
            realm = RealmManager.getInstance().getDefaultInstance(context);
            List<History> historyList = realm.where(History.class).equalTo("user_id",userId).equalTo("reference_id", id).findAll();
            realm.executeTransaction(realmObject -> {
                for (History h : historyList) {
                    h.setObjectId(tempId);
                    realmObject.insertOrUpdate(h);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


}
