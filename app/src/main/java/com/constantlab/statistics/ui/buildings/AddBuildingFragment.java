package com.constantlab.statistics.ui.buildings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Address;
import com.constantlab.statistics.models.AddressStreet;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.Kato;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.MapActivity;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Sunny Kinger on 11-12-2017.
 */

public class AddBuildingFragment extends BaseFragment {
    private static final int REQUEST_LOCATION = 4;
    Integer taskId;
    @BindView(R.id.btn_map)
    Button btnMap;
    @BindView(R.id.et_house)
    EditText etHouse;
    @BindView(R.id.et_floors)
    EditText etFloors;
    @BindView(R.id.et_total_flats)
    EditText etTotalFlats;
    @BindView(R.id.et_area)
    EditText etArea;
    @BindView(R.id.sp_building_status)
    Spinner spBuildingStatus;
    @BindView(R.id.sp_building_type)
    Spinner spBuildingType;
    @BindView(R.id.et_owner)
    EditText etOwner;
    @BindView(R.id.et_territory)
    EditText etTerritory;

    //    @BindView(R.id.sp_house_wall)
//    Spinner spHouseWall;
    @BindView(R.id.et_full_address)
    EditText etAddress;
    @BindView(R.id.sp_region)
    Spinner spRegions;
    @BindView(R.id.sp_street)
    Spinner spStreet;
    @BindView(R.id.sp_street_type)
    Spinner spStreetType;
    @BindView(R.id.et_latitude)
    EditText etLatitude;
    @BindView(R.id.et_longitude)
    EditText etLongitude;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.btn_next)
    Button btnNext;


    public static AddBuildingFragment newInstance(Integer taskId) {
        AddBuildingFragment fragment = new AddBuildingFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_TASK, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_building, container, false);
        ButterKnife.bind(this, view);
        setInitialData();
        return view;
    }

    private void setInitialData() {
        tvSave.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        setupBuildingStatus();
//        setupHouseWall();
        setupBuildingType();
        setupStreet();
        setupStreetType();
        setupRegion();
    }

    private void setupBuildingStatus() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<BuildingStatus> realmResults = realm.where(BuildingStatus.class).findAll();
            List<BuildingStatus> buildingStatuses = realm.copyFromRealm(realmResults);
            ArrayAdapter<BuildingStatus> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, buildingStatuses);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spBuildingStatus.setAdapter(arrayAdapter);
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupBuildingType() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<BuildingType> realmResults = realm.where(BuildingType.class).findAll();
            List<BuildingType> buildingTypes = realm.copyFromRealm(realmResults);
            ArrayAdapter<BuildingType> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, buildingTypes);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spBuildingType.setAdapter(arrayAdapter);
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupRegion() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Kato> realmResults = realm.where(Kato.class).findAll();
            List<Kato> katoList = realm.copyFromRealm(realmResults);
            ArrayAdapter<Kato> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, katoList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRegions.setAdapter(arrayAdapter);
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupStreetType() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<StreetType> realmResults = realm.where(StreetType.class).findAll();
            List<StreetType> streetTypeList = realm.copyFromRealm(realmResults);
            ArrayAdapter<StreetType> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, streetTypeList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStreetType.setAdapter(arrayAdapter);
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupStreet() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<AddressStreet> realmResults = realm.where(AddressStreet.class).findAll();
            List<AddressStreet> streetList = realm.copyFromRealm(realmResults);
            ArrayAdapter<AddressStreet> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, streetList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStreet.setAdapter(arrayAdapter);
        } finally {
            if (realm != null)
                realm.close();
        }
    }

//    private void setupHouseWall() {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            RealmResults<HouseWall> realmResults = realm.where(HouseWall.class).findAll();
//            List<HouseWall> houseWalls = realm.copyFromRealm(realmResults);
//            ArrayAdapter<HouseWall> arrayAdapter = new ArrayAdapter<HouseWall>(getContext(), android.R.layout.simple_spinner_item, houseWalls);
//            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spHouseWall.setAdapter(arrayAdapter);
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

    @OnClick(R.id.btn_map)
    public void gotoMaps() {
        startActivityForResult(new Intent(getContext(), MapActivity.class), REQUEST_LOCATION);
    }

    @OnClick(R.id.btn_next)
    public void save() {
        etHouse.setError(null);
        etLongitude.setError(null);
        etTotalFlats.setError(null);
        etArea.setError(null);
        etAddress.setError(null);
        boolean proceed = true;

        if (etHouse.getText().toString().isEmpty()) {
            proceed = false;
            etHouse.setError(getString(R.string.error_empty_field));
        }

        if (etOwner.getText().toString().isEmpty()) {
            proceed = false;
            etOwner.setError(getString(R.string.error_empty_field));
        }

        if (etTerritory.getText().toString().isEmpty()) {
            proceed = false;
            etTerritory.setError(getString(R.string.error_empty_field));
        }
        if (etAddress.getText().toString().isEmpty()) {
            proceed = false;
            etAddress.setError(getString(R.string.error_empty_field));
        }

        if (etArea.getText().toString().isEmpty()) {
            proceed = false;
            etArea.setError(getString(R.string.error_empty_field));
        }

        if (etTotalFlats.getText().toString().isEmpty()) {
            proceed = false;
            etTotalFlats.setError(getString(R.string.error_empty_field));
        }

        if (etLatitude.getText().toString().isEmpty()) {
            proceed = false;
            etLongitude.setError(getString(R.string.error_select_location));
        }

        if (etFloors.getText().toString().isEmpty()) {
            proceed = false;
            etFloors.setError(getString(R.string.error_empty_field));
        }

        if (proceed) {
            saveDataToDatabase();
        }
    }

    private void saveDataToDatabase() {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Building building = new Building();
                Number currentIdNum = realmObject.where(Building.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                building.setId(nextId);
                building.setAreaSq(Float.valueOf(etArea.getText().toString().trim()));
                building.setTotalFlats(Integer.valueOf(etTotalFlats.getText().toString().trim()));
                building.setHouseNumber(etHouse.getText().toString().trim());
                building.setFloorNumber(Integer.valueOf(etFloors.getText().toString().trim()));
                Address address = new Address();
                address.setAddressRu(etAddress.getText().toString().trim());
                Kato kato = (Kato) spRegions.getSelectedItem();
                address.setKato(kato);
                AddressStreet street = (AddressStreet) spStreet.getSelectedItem();
                address.setStreet(street);
                StreetType streetType = (StreetType) spStreetType.getSelectedItem();
                address.setStreetType(streetType);
                building.setAddress(address);
                BuildingType buildingType = (BuildingType) spBuildingType.getSelectedItem();
                building.setBuildingType(buildingType);
                BuildingStatus buildingStatus = (BuildingStatus) spBuildingStatus.getSelectedItem();
                building.setBuildingStatus(buildingStatus);
                building.setOwnerName(etOwner.getText().toString().trim());
                building.setTerritoryName(etTerritory.getText().toString().trim());
//                HouseWall houseWall = (HouseWall) spHouseWall.getSelectedItem();
//                building.setHouseWall(houseWall);
                building.setLatitude(Double.valueOf(etLatitude.getText().toString().trim()));
                building.setLongitude(Double.valueOf(etLongitude.getText().toString().trim()));
                building.setMarkedOnMap(true);
                Task task = realmObject.where(Task.class).equalTo("id", taskId).findFirst();
//                RealmList<Building> buildingList = task.getBuildingList();
//                if (buildingList == null) {
//                    buildingList = new RealmList<>();
//                }
//                buildingList.add(building);
//                task.setTotalBuildings(task.getBuildingList().size());
                realmObject.insertOrUpdate(task);
                //To send id to previous activity
                Intent intent = new Intent();
                intent.putExtra(ConstKeys.TAG_BUILDING, nextId);
                getActivity().setResult(Activity.RESULT_OK, intent);
            });
        } finally {
            if (realm != null)
                realm.close();
        }
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() == null)
                return;
            double latitude = data.getExtras().getDouble(MapActivity.LATITUDE_TAG);
            double longitude = data.getExtras().getDouble(MapActivity.LONGITUDE_TAG);
            if (isAdded()) {
                etLatitude.setText(String.format(Locale.getDefault(), "%f", latitude));
                etLongitude.setText(String.format(Locale.getDefault(), "%f", longitude));
            }
        }
    }
}
