package com.constantlab.statistics.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Address;
import com.constantlab.statistics.models.AddressStreet;
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
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.ui.sync.SyncFragment;
import com.constantlab.statistics.ui.tasks.TasksFragment;
import com.constantlab.statistics.utils.INavigation;
import com.constantlab.statistics.utils.NotificationCenter;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity implements INavigation {
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.task_container)
    FrameLayout mTaskContainer;

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected int getTaskFragmentContainerId() {
        return R.id.task_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        insertDummyContent();

        showTaskFragment(TasksFragment.newInstance(), false);
        showTaskContainer();
        BottomBar bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.tab_tasks:
                    showTaskContainer();
                    break;
                case R.id.tab_sync:
                    showFragment(SyncFragment.newInstance(), false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideTaskContainer();
                        }
                    },100);
                    break;
                case R.id.tab_map:
                    showFragment(MapFragment.newInstance(MapFragment.MapAction.VIEW), false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideTaskContainer();
                        }
                    },100);
                    break;
            }
        });

        NotificationCenter.getInstance().addNavigationListener(this);
    }

    private void refreshBottomNavigationSize(BottomNavigationView bottomNavigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView)
                bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView =
                    menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams =
                    iconView.getLayoutParams();
            final DisplayMetrics displayMetrics =
                    getResources().getDisplayMetrics();
            layoutParams.height = (int) getResources().getDimensionPixelSize(R.dimen.botomBar_img_size);
//                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.botomBar_img_size),
//                            displayMetrics);
            layoutParams.width = (int)getResources().getDimensionPixelSize(R.dimen.botomBar_img_size);
//                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.botomBar_img_size),
//                            displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }

    private void showTaskContainer() {
        mFragmentContainer.setVisibility(View.GONE);
        mTaskContainer.setVisibility(View.VISIBLE);
    }

    private void hideTaskContainer() {
        mFragmentContainer.setVisibility(View.VISIBLE);
        mTaskContainer.setVisibility(View.GONE);
    }

    private void insertDummyContent() {
        insertStreetType();
        insertAddressStreets();
//        insertHouseWalls();
        insertBuildingStatus();
        insertBuildingTypes();
        insertApartmentTypes();
        insertKato();
        insertAddress();
        insertBuilding();
        insertStreets();
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
                    buildingType.setType("Тип 1");
                    realmObject.insertOrUpdate(buildingType);
                    buildingType.setId(2);
                    buildingType.setType("Тип 2");
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
                    buildingStatus.setStatus("Статус 1");
                    realmObject.insertOrUpdate(buildingStatus);
                    buildingStatus.setId(2);
                    buildingStatus.setStatus("Статус 2");
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
                    apartmentType.setType("Квартира");
                    realmObject.insertOrUpdate(apartmentType);
                    apartmentType.setId(2);
                    apartmentType.setType("Комната");
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
                    RealmResults<Street> realmList = realmObject.where(Street.class).findAll();
                    RealmList<Street> streetsRealmList = new RealmList<>();
                    streetsRealmList.addAll(realmList);
                    task.setStreetList(streetsRealmList);
                    task.setTotalBuildings(task.getStreetList().size());
                    int totalApartments = 0;
                    int totalResidents = 0;
                    //Count Apartments
                    for (Street street : task.getStreetList()) {
                        for (Building building : street.getBuildingList()) {
                            if (building.getApartmentList() != null) {
                                totalApartments += building.getApartmentList().size();
                                for (Apartment apartment : building.getApartmentList()) {
                                    totalResidents += apartment.getTotalInhabitants();
                                }
                            }
                        }
                    }
                    task.setTotalApartments(totalApartments);
                    task.setTotalResidents(totalResidents);
                    task.setTaskName("Задания 1");
                    realmObject.insert(task);
                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void insertStreets() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Street.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    Street street = new Street();
                    street.setId(1);
                    RealmResults<Building> realmList = realmObject.where(Building.class).findAll();
                    RealmList<Building> buildingRealmList = new RealmList<>();
                    buildingRealmList.addAll(realmList);
                    street.setBuildingList(buildingRealmList);
                    //Count Apartments

                    street.setName("Иманова");
                    realmObject.insert(street);
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
                    building.setTerritoryName("Пустая Теретория");
                    building.setOwnerName("Никита Карачев");
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
                    AddressStreet street = realmObject.where(AddressStreet.class).findFirst();
                    address.setStreet(street);
                    StreetType streetType = realmObject.where(StreetType.class).findFirst();
                    address.setStreetType(streetType);
                    realmObject.insertOrUpdate(address);
                    address.setStreetType(streetType);
                    street = realmObject.where(AddressStreet.class).equalTo("id", 2).findFirst();
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

    private void insertAddressStreets() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(AddressStreet.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    AddressStreet street = new AddressStreet();
                    street.setId(1);
                    street.setTitleRu("Субурская");
                    street.setTitleKz("СИБИРСКАЯ");
                    realmObject.insertOrUpdate(street);
                    street.setId(2);
                    street.setTitleRu("С.Саметова");
                    street.setTitleKz("С.Саметова");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeNavigationListener(this);
    }

    @Override
    public void openPage(BaseFragment fragment) {
        showTaskFragment(fragment, true);
    }
}
