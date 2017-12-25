package com.constantlab.statistics.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Address;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.Kato;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.ui.sync.SyncFragment;
import com.constantlab.statistics.ui.tasks.TasksFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity {
    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertDummyContent();


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_navigation);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_tasks:
                        showFragment(TasksFragment.newInstance(), false);
                        break;
                    case R.id.tab_sync:
                        showFragment(SyncFragment.newInstance(), false);
                        break;
                    case R.id.tab_map:
                        showFragment(MapFragment.newInstance(), false);
                        break;
                }
            }
        });
    }

    private void insertDummyContent() {
        insertStreetType();
        insertStreets();
//        insertHouseWalls();
        insertBuildingStatus();
        insertBuildingTypes();
        insertApartmentTypes();
        insertKato();
        insertAddress();
        insertBuilding();
        insertTasks();
    }

    private void insertBuildingTypes() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(BuildingType.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    BuildingType buildingType = new BuildingType();
                    buildingType.setId(1);
                    buildingType.setType("Type 1");
                    realmObject.insertOrUpdate(buildingType);
                    buildingType.setId(2);
                    buildingType.setType("Type 2");
                    realmObject.insert(buildingType);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertBuildingStatus() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(BuildingStatus.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    BuildingStatus buildingStatus = new BuildingStatus();
                    buildingStatus.setId(1);
                    buildingStatus.setStatus("Status 1");
                    realmObject.insertOrUpdate(buildingStatus);
                    buildingStatus.setId(2);
                    buildingStatus.setStatus("Status 2");
                    realmObject.insert(buildingStatus);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertApartmentTypes() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(ApartmentType.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    ApartmentType apartmentType = new ApartmentType();
                    apartmentType.setId(1);
                    apartmentType.setType("Apartment");
                    realmObject.insertOrUpdate(apartmentType);
                    apartmentType.setId(2);
                    apartmentType.setType("Room");
                    realmObject.insertOrUpdate(apartmentType);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertTasks() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Task.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    Task task = new Task();
                    task.setId(1);
                    RealmResults<Building> realmList = realmObject.where(Building.class).findAll();
                    RealmList<Building> buildingRealmList = new RealmList<>();
                    buildingRealmList.addAll(realmList);
                    task.setBuildingList(buildingRealmList);
                    task.setTotalBuildings(task.getBuildingList().size());
                    int totalApartments = 0;
                    int totalResidents = 0;
                    //Count Apartments
                    for (Building building : task.getBuildingList()) {
                        if (building.getApartmentList() != null) {
                            totalApartments += building.getApartmentList().size();
                            for (Apartment apartment : building.getApartmentList()) {
                                totalResidents += apartment.getTotalInhabitants();
                            }
                        }
                    }
                    task.setTotalApartments(totalApartments);
                    task.setTotalResidents(totalResidents);
                    task.setTaskName("Dummy Task 1");
                    realmObject.insert(task);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertBuilding() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Building.class).findAll().size() == 0) {
                realm.executeTransaction(realObject -> {
                    Building building = new Building();
                    building.setId(1);
//                    HouseWall houseWall = realObject.where(HouseWall.class).findFirst();
//                    building.setHouseWall(houseWall);
                    Address address = realObject.where(Address.class).findFirst();
                    building.setAddress(address);
                    building.setHouseNumber("11");
                    building.setFloorNumber(1);
                    building.setTotalFlats(100);
                    building.setAreaSq(85.0f);
                    building.setLongitude(null);
                    building.setLongitude(null);
                    building.setMarkedOnMap(false);
                    building.setTerritoryName("Dummy Territory");
                    building.setOwnerName("Nikita Karachev");
                    BuildingType buildingType = realObject.where(BuildingType.class).findFirst();
                    building.setBuildingType(buildingType);
                    BuildingStatus buildingStatus = realObject.where(BuildingStatus.class).findFirst();
                    building.setBuildingStatus(buildingStatus);
                    Apartment apartment = new Apartment();
                    apartment.setId(1);
                    apartment.setApartmentNumber("1");
                    ApartmentType apartmentType = realObject.where(ApartmentType.class).findFirst();
                    apartment.setApartmentType(apartmentType);
                    apartment.setOwnerName("Yerzhan Ryskaliyev");
//                    apartment.setAreaSquare(85);
                    apartment.setTotalInhabitants(3);
                    apartment.setTotalRooms(4);
                    RealmList<Apartment> apartments = new RealmList<>();
                    apartments.add(apartment);
                    building.setApartmentList(apartments);
                    realObject.insert(building);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertAddress() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Address.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    Address address = new Address();
                    Kato kato = realmObject.where(Kato.class).findFirst();
                    address.setKato(kato);
                    Street street = realmObject.where(Street.class).findFirst();
                    address.setStreet(street);
                    StreetType streetType = realmObject.where(StreetType.class).findFirst();
                    address.setStreetType(streetType);
                    realmObject.insertOrUpdate(address);
                    address.setStreetType(streetType);
                    street = realmObject.where(Street.class).equalTo("id", 2).findFirst();
                    address.setStreet(street);
                    address.setKato(kato);
                    realmObject.insertOrUpdate(address);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertKato() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Kato.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    Kato kato = new Kato();
                    kato.setId(1);
                    kato.setNameRu("Г.АЯГОЗ");
                    kato.setNameKz("АЯГӨЗ Қ.");
                    realmObject.insert(kato);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

//    private void insertHouseWalls() {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            if (realm.where(HouseWall.class).findAll().size() == 0) {
//                realm.executeTransaction(realmObject -> {
//                    HouseWall houseWall = new HouseWall();
//                    houseWall.setId(1);
//                    houseWall.setTitleRu("Саман");
//                    houseWall.setTitleKz("Саман");
//                    realmObject.insertOrUpdate(houseWall);
//                    houseWall.setId(2);
//                    houseWall.setTitleRu("Крупнопанельный");
//                    houseWall.setTitleKz("Ірі панельді");
//                    realmObject.insertOrUpdate(houseWall);
//                });
//            }
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

    private void insertStreets() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Street.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    Street street = new Street();
                    street.setId(1);
                    street.setTitleRu("СИБИРСКАЯ");
                    street.setTitleKz("СИБИРСКАЯ");
                    realmObject.insertOrUpdate(street);
                    street.setId(2);
                    street.setTitleRu("С.САМЕТОВА");
                    street.setTitleKz("С.САМЕТОВА");
                    realmObject.insertOrUpdate(street);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertStreetType() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(StreetType.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    StreetType streetType = new StreetType();
                    streetType.setId(1);
                    streetType.setTitleRu("УЛИЦА");
                    streetType.setTitleKk("УЛИЦА");
                    realmObject.insert(streetType);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
