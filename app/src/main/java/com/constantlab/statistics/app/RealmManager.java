package com.constantlab.statistics.app;

import android.content.Context;

import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by Hayk on 17/01/2017.
 */

public class RealmManager {
    private final long DAY_TO_MILLIS = 24 * 60 * 60 * 1000;

    private RealmManager() {

    }

    private static RealmManager _instance;

    public static RealmManager getInstance() {
        if (_instance == null) {
            _instance = new RealmManager();
        }
        return _instance;
    }

    public void init(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(ConstKeys.REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public Realm getDefaultInstance(Context context) {
        try {
            Realm.getDefaultInstance();
        } catch (IllegalStateException e) {
            init(context);
        }
        return Realm.getDefaultInstance();
    }

    public void clearLocalData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<Task> tasks = realm.where(Task.class).findAll();
            RealmResults<Street> streets = realm.where(Street.class).findAll();
            RealmResults<Building> buildings = realm.where(Building.class).findAll();
            RealmResults<Apartment> apartments = realm.where(Apartment.class).findAll();
            RealmResults<History> histories = realm.where(History.class).findAll();
            RealmResults<GeoPolygon> geoPolygons = realm.where(GeoPolygon.class).findAll();
            RealmResults<StreetType> streetTypes = realm.where(StreetType.class).findAll();
            RealmResults<BuildingType> buildingTypes = realm.where(BuildingType.class).findAll();
            RealmResults<BuildingStatus> buildingStatuses = realm.where(BuildingStatus.class).findAll();
            RealmResults<ApartmentType> apartmentTypes = realm.where(ApartmentType.class).findAll();
            RealmResults<ChangeType> changeTypes = realm.where(ChangeType.class).findAll();
            realm.executeTransaction(realmObject -> {
                tasks.deleteAllFromRealm();
                streets.deleteAllFromRealm();
                buildings.deleteAllFromRealm();
                apartments.deleteAllFromRealm();
                histories.deleteAllFromRealm();
                geoPolygons.deleteAllFromRealm();
                streetTypes.deleteAllFromRealm();
                buildingTypes.deleteAllFromRealm();
                buildingStatuses.deleteAllFromRealm();
                apartmentTypes.deleteAllFromRealm();
                changeTypes.deleteAllFromRealm();
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public List<GeoPolygon> getTaskGeoPolygons(Integer taskId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<GeoPolygon> geoPolygons = realm.where(GeoPolygon.class).equalTo("task_id", taskId).findAll();
            if (geoPolygons != null) {
                return realm.copyFromRealm(geoPolygons);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
        return new ArrayList<>();
    }

    public Task getTask(Integer taskId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Task task = realm.where(Task.class).equalTo("task_id", taskId).findFirst();

            if (task != null) {
                return realm.copyFromRealm(task);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
        return null;
    }

    public boolean checkApartmentDuplicateName(Integer taskId, Integer buildingId, Integer apartmentId, String number) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("task_id", taskId).equalTo("building_id", buildingId).findAll();
            for (Apartment apartment : apartments) {
                if (!apartment.getId().equals(apartmentId) && apartment.getApartmentNumber().equals(number)) {
                    return true;
                }
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return false;
    }

    public boolean checkBuildingDuplicateName(Integer taskId, Integer streetId, Integer buildingId, String number) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Building> buildings = realm.where(Building.class).equalTo("task_id", taskId).equalTo("street_id", streetId).findAll();
            for (Building building : buildings) {
                if (!building.getId().equals(buildingId) && building.getHouseNumber().equals(number)) {
                    return true;
                }
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return false;
    }

    public boolean checkStreetStreetName(Integer taskId, Integer streetId, String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Street> streets = realm.where(Street.class).equalTo("task_id", taskId).findAll();
            for (Street street : streets) {
                if (!street.getId().equals(streetId) && street.getName().equals(name)) {
                    return true;
                }
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return false;
    }

//    public void insertChangeTypes(List<ChangeType> changeTypes) {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            realm.executeTransaction(realmObject -> {
//                for (ChangeType type : changeTypes) {
//                    realmObject.insert(type);
//                }
//            });
//
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

    public String getChangeTypeNameById(int id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            ChangeType changeType = realm.where(ChangeType.class).equalTo("id", id).findFirst();
            if (changeType != null) {
                return changeType.getName();
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return "";
    }

//    public void insertStreetTypes(List<StreetType> streetTypes) {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            realm.executeTransaction(realmObject -> {
//                for (StreetType type : streetTypes) {
//                    realmObject.insert(type);
//                }
//            });
//
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

    public List<?> getTypes(Class<? extends RealmObject> className) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults types = realm.where(className).findAll();
            if (types != null) {
                return realm.copyFromRealm(types);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return new ArrayList<>();
    }


    public String getStreetTypeNameById(int id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            StreetType streetType = realm.where(StreetType.class).equalTo("id", id).findFirst();
            if (streetType != null) {
                return streetType.getName();
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return "";
    }

    public void insertTypes(List<? extends RealmObject> types) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                for (RealmObject type : types) {
                    realmObject.insert(type);
                }
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }


    public String getBuildingTypeNameById(int id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            BuildingType type = realm.where(BuildingType.class).equalTo("id", id).findFirst();
            if (type != null) {
                return type.getName();
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return "";
    }

    public String getBuildingStatusTypeNameById(int id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            BuildingStatus type = realm.where(BuildingStatus.class).equalTo("id", id).findFirst();
            if (type != null) {
                return type.getName();
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return "";
    }


    public String getApartmentTypeNameById(int id) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            ApartmentType apartmentType = realm.where(ApartmentType.class).equalTo("id", id).findFirst();
            if (apartmentType != null) {
                return apartmentType.getName();
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return "";
    }

    public void changeHistoryInactiveStatus(Integer taskId, Integer buildingId, boolean inactive, Realm realm) {
        RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("task_id", taskId).equalTo("building_id", buildingId).findAll();
        if (apartments == null) {
            return;
        }

        Integer[] ids = new Integer[apartments.size()];
        for (int i = 0; i < apartments.size(); i++) {
            ids[i] = apartments.get(i).getId();
        }
        if (ids.length != 0) {
            RealmResults<History> histories = realm.where(History.class).in("temp_object_id", ids).findAll();
            if (histories == null) {
                return;
            }

            for (History h : histories) {
                h.setInactive(inactive);
                realm.insertOrUpdate(h);
            }
        }
    }
}
