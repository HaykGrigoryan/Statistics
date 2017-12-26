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
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.Kato;
import com.constantlab.statistics.models.AddressStreet;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.MapActivity;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Hayk on 26/12/2017.
 */

public class BuildingDetailsFragment extends BaseFragment {
    private static final int REQUEST_LOCATION = 4;

    Integer buildingId;
    String buildingName;

    Building mBuilding;
    @BindView(R.id.btn_map)
    Button btnMap;
    @BindView(R.id.et_house)
    EditText etHouse;
    //    @BindView(R.id.et_floors)
//    EditText etFloors;
//    @BindView(R.id.et_total_flats)
//    EditText etTotalFlats;
//    @BindView(R.id.et_area)
//    EditText etArea;
    @BindView(R.id.sp_building_status)
    Spinner spBuildingStatus;
    @BindView(R.id.sp_building_type)
    Spinner spBuildingType;
    @BindView(R.id.et_owner)
    EditText etOwner;
    //    @BindView(R.id.et_territory)
//    EditText etTerritory;
    //    @BindView(R.id.sp_house_wall)
//    Spinner spHouseWall;
//    @BindView(R.id.et_full_address)
//    EditText etAddress;
//    @BindView(R.id.sp_region)
//    Spinner spRegions;
//    @BindView(R.id.sp_street)
//    Spinner spStreet;
    @BindView(R.id.sp_street_type)
    Spinner spStreetType;
    //    @BindView(R.id.et_latitude)
//    EditText etLatitude;
//    @BindView(R.id.et_longitude)
//    EditText etLongitude;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.locationIndicator)
    TextView mLocationIndicator;
    @BindView(R.id.title)
    TextView mTitle;
    Double mSelectedLat, mSelectedLon;

    public static BuildingDetailsFragment newInstance(Integer buildingId, String buildingName) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        args.putString(ConstKeys.TAG_BUILDING_NAME, buildingName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
            buildingName = getArguments().getString(ConstKeys.TAG_BUILDING_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_details, container, false);
        ButterKnife.bind(this, view);
        if (buildingId != -1) {
            setStoredData();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (buildingName != null) {
            mTitle.setText(buildingName);
        }
    }

    private void setStoredData() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Building building = realm.where(Building.class).equalTo("id", buildingId).findFirst();
            if (building != null) {
                mBuilding = realm.copyFromRealm(building);
                showData(mBuilding);
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void showData(Building object) {
//        btnSave.setVisibility(View.GONE);
//        etAddress.setText(object.getAddress().getAddressRu());
//        etArea.setText(object.getAreaSq() != null ? String.format(Locale.getDefault(), "%f", object.getAreaSq()) : "");
//        etLatitude.setText(object.getLatitude() != null ? String.format(Locale.getDefault(), "%f", object.getLatitude()) : "");
//        etLongitude.setText(object.getLongitude() != null ? String.format(Locale.getDefault(), "%f", object.getLongitude()) : "");
//        etFloors.setText(object.getFloorNumber() != null ? String.format(Locale.getDefault(), "%d", object.getFloorNumber()) : "");
        etHouse.setText(object.getHouseNumber() != null ? object.getHouseNumber() : "");
//        etTotalFlats.setText(object.getTotalFlats() != null ? String.format(Locale.getDefault(), "%d", object.getTotalFlats()) : "");
        etOwner.setText(object.getOwnerName());
//        etTerritory.setText(object.getTerritoryName());
//        setupHouseWall(object.getHouseWall());
        mSelectedLon = object.getLatitude();
        mSelectedLon = object.getLongitude();
        setupBuildingStatus(object.getBuildingStatus());
        setupBuildingType(object.getBuildingType());
        setupRegion(object.getAddress().getKato());
        setupStreet(object.getAddress().getStreet());
        setupStreetType(object.getAddress().getStreetType());
        updateLocationIndicator(object.getLatitude() != null && object.getLongitude() != null);
    }


    private void setupBuildingType(BuildingType buildingType) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<BuildingType> realmResults = realm.where(BuildingType.class).findAll();
            List<BuildingType> buildingTypes = realm.copyFromRealm(realmResults);
            int index = -1;
            if (buildingType != null) {
                index = buildingTypes.indexOf(buildingType);
            }
            ArrayAdapter<BuildingType> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, buildingTypes);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spBuildingType.setAdapter(arrayAdapter);
            if (index != -1) {
                spBuildingType.setSelection(index);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }


    private void setupRegion(Kato kato) {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            RealmResults<Kato> realmResults = realm.where(Kato.class).findAll();
//            List<Kato> katoList = realm.copyFromRealm(realmResults);
//            int index = -1;
//            if (kato != null) {
//                index = katoList.indexOf(kato);
//            }
//            ArrayAdapter<Kato> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, katoList);
//            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spRegions.setAdapter(arrayAdapter);
//            if (index != -1) {
//                spRegions.setSelection(index);
//            }
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
    }

    private void setupBuildingStatus(BuildingStatus buildingStatus) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<BuildingStatus> realmResults = realm.where(BuildingStatus.class).findAll();
            List<BuildingStatus> buildingStatuses = realm.copyFromRealm(realmResults);
            int index = -1;
            if (buildingStatus != null) {
                index = buildingStatuses.indexOf(buildingStatus);
            }
            ArrayAdapter<BuildingStatus> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, buildingStatuses);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spBuildingStatus.setAdapter(arrayAdapter);
            if (index != -1) {
                spBuildingStatus.setSelection(index);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupStreetType(StreetType streetType) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<StreetType> realmResults = realm.where(StreetType.class).findAll();
            List<StreetType> streetTypeList = realm.copyFromRealm(realmResults);
            int index = -1;
            if (streetType != null) {
                index = streetTypeList.indexOf(streetType);
            }
            ArrayAdapter<StreetType> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, streetTypeList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStreetType.setAdapter(arrayAdapter);
            if (index != -1) {
                spStreetType.setSelection(index);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupStreet(AddressStreet street) {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            RealmResults<AddressStreet> realmResults = realm.where(AddressStreet.class).findAll();
//            List<AddressStreet> streetList = realm.copyFromRealm(realmResults);
//            int index = -1;
//            if (street != null) {
//                index = streetList.indexOf(street);
//            }
//            ArrayAdapter<AddressStreet> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, streetList);
//            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spStreet.setAdapter(arrayAdapter);
//            if (index != -1)
//                spStreet.setSelection(index);
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
    }

//    private void setupHouseWall(HouseWall houseWall) {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            RealmResults<HouseWall> realmResults = realm.where(HouseWall.class).findAll();
//            List<HouseWall> houseWalls = realm.copyFromRealm(realmResults);
//            int index = -1;
//            if (houseWall != null) {
//                index = houseWalls.indexOf(houseWall);
//            }
//            ArrayAdapter<HouseWall> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, houseWalls);
//            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spHouseWall.setAdapter(arrayAdapter);
//            if (index != -1)
//                spHouseWall.setSelection(index);
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }


    @OnClick(R.id.btn_map)
    public void gotoMaps() {
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra(ConstKeys.KEY_MAP_ACTION, MapFragment.MapAction.PICK_LOCATION.ordinal());
//        if (mBuilding != null && mBuilding.getLatitude() != null && mBuilding.getLongitude() != null) {
        intent.putExtra(ConstKeys.KEY_LATITUDE, mSelectedLat);
        intent.putExtra(ConstKeys.KEY_LONGITUDE, mSelectedLon);
//        }
        startActivityForResult(intent, REQUEST_LOCATION);
    }


    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.btn_save)
    public void save() {
        etHouse.setError(null);
//        etLongitude.setError(null);
//        etTotalFlats.setError(null);
//        etArea.setError(null);
//        etAddress.setError(null);
        boolean proceed = true;

        if (etHouse.getText().toString().isEmpty()) {
            proceed = false;
            etHouse.setError(getString(R.string.error_empty_field));
        }

//        if (etAddress.getText().toString().isEmpty()) {
//            proceed = false;
//            etAddress.setError(getString(R.string.error_empty_field));
//        }
//
//        if (etArea.getText().toString().isEmpty()) {
//            proceed = false;
//            etArea.setError(getString(R.string.error_empty_field));
//        }
//
//        if (etTotalFlats.getText().toString().isEmpty()) {
//            proceed = false;
//            etTotalFlats.setError(getString(R.string.error_empty_field));
//        }
//
//        if (etLatitude.getText().toString().isEmpty()) {
//            proceed = false;
//            etLongitude.setError(getString(R.string.error_select_location));
//        }
//
//        if (etFloors.getText().toString().isEmpty()) {
//            proceed = false;
//            etFloors.setError(getString(R.string.error_empty_field));
//        }

        if (proceed) {
            saveDataToDatabase();
        }
    }

    private void saveDataToDatabase() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Building b = realmObject.where(Building.class).equalTo("id", buildingId).findFirst();
                Building building = null;
                if (b != null) {
                    building = realmObject.copyFromRealm(b);
                } else {
                    building = new Building();
                    Number currentIdNum = realmObject.where(Building.class).max("id");
                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    building.setId(nextId);
                }
//                building.setAreaSq(Float.valueOf(etArea.getText().toString().trim()));
//                building.setTotalFlats(Integer.valueOf(etTotalFlats.getText().toString().trim()));
                building.setHouseNumber(etHouse.getText().toString().trim());
//                building.setFloorNumber(Integer.valueOf(etFloors.getText().toString().trim()));
                Address address = building.getAddress();
//                address.setAddressRu(etAddress.getText().toString().trim());
//                Kato kato = (Kato) spRegions.getSelectedItem();
//                address.setKato(kato);
//                AddressStreet street = (AddressStreet) spStreet.getSelectedItem();
//                address.setStreet(street);
                StreetType streetType = (StreetType) spStreetType.getSelectedItem();
                address.setStreetType(streetType);
                building.setAddress(address);
//                    HouseWall houseWall = (HouseWall) spHouseWall.getSelectedItem();
//                    building.setHouseWall(houseWall);
                BuildingType buildingType = (BuildingType) spBuildingType.getSelectedItem();
                building.setBuildingType(buildingType);
                BuildingStatus buildingStatus = (BuildingStatus) spBuildingStatus.getSelectedItem();
                building.setBuildingStatus(buildingStatus);
                building.setOwnerName(etOwner.getText().toString().trim());
//                building.setTerritoryName(etTerritory.getText().toString().trim());
//                building.setLatitude(Double.valueOf(etLatitude.getText().toString().trim()));
//                building.setLongitude(Double.valueOf(etLongitude.getText().toString().trim()));
                building.setLatitude(mSelectedLat);
                building.setLongitude(mSelectedLon);
                realmObject.insertOrUpdate(building);
            });
        } finally {
            if (realm != null)
                realm.close();
        }
//        getActivity().setResult(Activity.RESULT_OK);
        getActivity().onBackPressed();
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
//                etLatitude.setText(String.format(Locale.getDefault(), "%f", latitude));
//                etLongitude.setText(String.format(Locale.getDefault(), "%f", longitude));
                mSelectedLat = latitude;
                mSelectedLon = longitude;
                updateLocationIndicator(true);
            }
        }
    }

    private void updateLocationIndicator(boolean locationSelected) {
        mLocationIndicator.setText(locationSelected ? getString(R.string.label_have_mark) : getString(R.string.label_not_have_mark));
    }
}
