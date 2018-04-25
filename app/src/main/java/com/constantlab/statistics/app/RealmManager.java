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
import com.constantlab.statistics.models.User;
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

    public void clearLocalData(Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<Task> tasks = realm.where(Task.class).equalTo("user_id", userId).findAll();
            RealmResults<Street> streets = realm.where(Street.class).equalTo("user_id", userId).findAll();
            RealmResults<Building> buildings = realm.where(Building.class).equalTo("user_id", userId).findAll();
            RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).findAll();
            RealmResults<History> histories = realm.where(History.class).equalTo("user_id", userId).findAll();
            RealmResults<GeoPolygon> geoPolygons = realm.where(GeoPolygon.class).equalTo("user_id", userId).findAll();
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

    public void addUser(User user) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realmObject) {
                    realmObject.insertOrUpdate(user);
                }
            });

        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public User getUser(String username, String password) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            User user = realm.where(User.class).equalTo("username", username).equalTo("password", password).findFirst();
            if (user != null) {
                return realm.copyFromRealm(user);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
        return null;
    }

    public void saveUser(User userForSave) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            User user = realm.where(User.class).equalTo("username", userForSave.getUsername()).equalTo("password", userForSave.getPassword()).findFirst();
            if (user != null) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realmObject) {
                        user.setLastSyncFromServer(userForSave.getLastSyncFromServer());
                        user.setLastSyncToServer(user.getLastSyncToServer());
                        realmObject.insertOrUpdate(user);
                    }
                });
            }

        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public List<GeoPolygon> getTaskGeoPolygons(Integer taskId, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            RealmResults<GeoPolygon> geoPolygons = realm.where(GeoPolygon.class).equalTo("task_id", taskId).equalTo("user_id", userId).findAll();
            if (geoPolygons != null) {
                return realm.copyFromRealm(geoPolygons);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
        return new ArrayList<>();
    }

    public Task getTask(Integer taskId, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Task task = realm.where(Task.class).equalTo("task_id", taskId).equalTo("user_id", userId).findFirst();

            if (task != null) {
                return realm.copyFromRealm(task);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
        return null;
    }

    public boolean checkApartmentDuplicateName(Integer taskId, Integer buildingId, Integer apartmentId, String number, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("task_id", taskId).equalTo("building_id", buildingId).equalTo("user_id", userId).findAll();
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

    public boolean checkBuildingDuplicateName(Integer taskId, Integer streetId, Integer buildingId, String number, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Building> buildings = realm.where(Building.class).equalTo("task_id", taskId).equalTo("street_id", streetId).equalTo("user_id", userId).findAll();
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

    public boolean checkStreetStreetName(Integer taskId, Integer streetId, String name, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Street> streets = realm.where(Street.class).equalTo("task_id", taskId).equalTo("user_id", userId).findAll();
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

    public Street getStreet(Integer taskId, Integer streetId, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Street street = realm.where(Street.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("id", streetId).findFirst();
            if (street != null) {
                return realm.copyFromRealm(street);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return null;
    }

    public Building getBuilding(Integer taskId, Integer buildingId, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Building building = realm.where(Building.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("id", buildingId).findFirst();
            if (building != null) {
                return realm.copyFromRealm(building);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return null;
    }

    public Apartment getApartment(Integer taskId, Integer apartmentId, Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Apartment apartment = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("id", apartmentId).findFirst();
            if (apartment != null) {
                return realm.copyFromRealm(apartment);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return null;
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

    public List<Building> getBuildingsWithPoints(Integer userId) {
        Realm realm = null;
        List<Building> buildings = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Building> b = realm.where(Building.class).equalTo("user_id", userId).isNotNull("latitude").notEqualTo("latitude", Double.NaN).findAll();
            if (b != null) {
                buildings = realm.copyFromRealm(b);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return buildings;
    }

    public List<History> getUserAddedPoints(Integer userId) {
        Realm realm = null;
        List<History> histories = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<History> b = realm.where(History.class).equalTo("user_id", userId).equalTo("change_type",14).findAll();
            if (b != null) {
                histories = realm.copyFromRealm(b);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return histories;
    }

    public void changeHistoryInactiveStatus(Integer taskId, Integer buildingId, boolean inactive, Realm realm, Integer userId) {
        RealmResults<Apartment> apartments = realm.where(Apartment.class).equalTo("user_id", userId).equalTo("task_id", taskId).equalTo("building_id", buildingId).findAll();
        if (apartments == null) {
            return;
        }

        Integer[] ids = new Integer[apartments.size()];
        for (int i = 0; i < apartments.size(); i++) {
            ids[i] = apartments.get(i).getId();
        }
        if (ids.length != 0) {
            RealmResults<History> histories = realm.where(History.class).equalTo("user_id", userId).in("temp_object_id", ids).findAll();
            if (histories == null) {
                return;
            }

            for (History h : histories) {
                h.setInactive(inactive);
                realm.insertOrUpdate(h);
            }
        }
    }

    public List<History> getNotSyncedHistories(Integer userId) {
        Realm realm = null;
        List<History> historyList = new ArrayList<>();
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<History> historyRealm = realm.where(History.class).equalTo("user_id", userId).equalTo("synced", false).equalTo("inactive", false).sort("id", Sort.ASCENDING).findAll();
            if (historyRealm != null) {
                historyList = realm.copyFromRealm(historyRealm);
            }

        } finally {
            if (realm != null)
                realm.close();
        }

        return historyList;
    }

    public void updateNotSyncHistories(Integer userId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<History> historyRealm = realm.where(History.class).equalTo("user_id", userId).equalTo("synced", false).equalTo("inactive", false).sort("id", Sort.ASCENDING).findAll();
            if (historyRealm != null) {
                realm.executeTransaction(realmObject -> {
                    for (History history : historyRealm) {
                        history.setSynced(true);
                        realmObject.insertOrUpdate(history);
                    }
                });
            }

        } finally {
            if (realm != null)
                realm.close();
        }
    }

}
