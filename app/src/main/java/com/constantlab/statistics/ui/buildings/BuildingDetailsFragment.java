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
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.TempNewData;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.MapActivity;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.GsonUtils;
import com.constantlab.statistics.utils.HistoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Hayk on 26/12/2017.
 */

public class BuildingDetailsFragment extends BaseFragment {
    private static final int REQUEST_LOCATION = 4;

    Integer buildingId;
    String buildingName;
    Integer streetId;
    @BindView(R.id.btn_map)
    Button btnMap;
    @BindView(R.id.et_house)
    EditText etHouse;
    @BindView(R.id.sp_building_status)
    Spinner spBuildingStatus;
    @BindView(R.id.sp_building_type)
    Spinner spBuildingType;
    @BindView(R.id.et_owner)
    EditText etOwner;
    @BindView(R.id.et_count_of_living)
    EditText etLivingCount;
    @BindView(R.id.et_kato)
    EditText etKato;
    @BindView(R.id.sp_street_type)
    Spinner spStreetType;
    @BindView(R.id.et_name_street)
    TextView etStreetName;
    @BindView(R.id.et_comment)
    TextView etComment;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.locationIndicator)
    TextView mLocationIndicator;
    @BindView(R.id.title)
    TextView mTitle;
    Double mSelectedLat, mSelectedLon;
    private Street mStreet;

    private Building mBuilding;

    public static BuildingDetailsFragment newInstance(Integer buildingId, String buildingName, Integer streetId) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        args.putInt(ConstKeys.TAG_STREET, streetId);
        args.putString(ConstKeys.TAG_BUILDING_NAME, buildingName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStreetType = new ArrayList<>();
        mBuildingTypes = new ArrayList<>();
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
            streetId = getArguments().getInt(ConstKeys.TAG_STREET);
            buildingName = getArguments().getString(ConstKeys.TAG_BUILDING_NAME);
            mStreet = getStreet();
        }
    }

    private Street getStreet() {
        Realm realm = null;
        Street street = new Street();
        try {
            realm = Realm.getDefaultInstance();
            street = realm.where(Street.class).equalTo("id", streetId).findFirst();
            if (street != null) {
                street = realm.copyFromRealm(street);
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return street;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_details, container, false);
        ButterKnife.bind(this, view);
        loadStaticContent();
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
            mBuilding = realm.where(Building.class).equalTo("id", buildingId).findFirst();
            if (mBuilding != null) {
                mBuilding = realm.copyFromRealm(mBuilding);
                showData(mBuilding);
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void showData(Building object) {
        etHouse.setText(object.getHouseNumber() != null ? object.getHouseNumber() + "" : "");
        etOwner.setText(object.getOwnerName());
        etComment.setText(object.getComment());
        mSelectedLon = object.getLatitude();
        mSelectedLon = object.getLongitude();
        etKato.setText(object.getKato());
        spBuildingType.setSelection(BuildingType.getIndex(mBuildingTypes, object.getBuildingType()));
        etLivingCount.setText(String.valueOf(object.getTemporaryInhabitants()));
        mSelectedLat = object.getLatitude();
        mSelectedLon = object.getLongitude();
        updateLocationIndicator(object.getLatitude() != null && object.getLongitude() != null);
    }

    private void loadStaticContent() {
        loadBuildingType();
        loadBuildingStatus();
        loadStreetType();
        etStreetName.setText(mStreet.getDisplayName(getContext()));
    }

    private List<BuildingType> mBuildingTypes;

    private void loadBuildingType() {
        mBuildingTypes = GsonUtils.getBuildingTypeData(getContext());

        ArrayAdapter<BuildingType> arrayAdapterEditBl = new ArrayAdapter<BuildingType>(getContext(), R.layout.spinner_item, mBuildingTypes);
        arrayAdapterEditBl.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spBuildingType.setAdapter(arrayAdapterEditBl);
    }

    private void loadBuildingStatus() {
        List<BuildingStatus> types = new ArrayList<>();

        ArrayList<String> arrayListBuildingStatus = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.demo_list)));
        int id = 1;
        for (String name : arrayListBuildingStatus) {
            BuildingStatus buildingStatus = new BuildingStatus();
            buildingStatus.setId(id++);
            buildingStatus.setStatus(name);
            types.add(buildingStatus);
        }

        ArrayAdapter<BuildingStatus> arrayAdapterEditSt = new ArrayAdapter<BuildingStatus>(getContext(), R.layout.spinner_item, types);
        arrayAdapterEditSt.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spBuildingStatus.setAdapter(arrayAdapterEditSt);
    }

    private List<StreetType> mStreetType;

    private void loadStreetType() {
        mStreetType = GsonUtils.getStreetTypeData(getContext());

        ArrayAdapter<StreetType> arrayAdapterEditSt = new ArrayAdapter<StreetType>(getContext(), R.layout.spinner_item, mStreetType);
        arrayAdapterEditSt.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStreetType.setAdapter(arrayAdapterEditSt);
        spStreetType.setEnabled(false);
        spStreetType.setSelection(StreetType.getIndex(mStreetType, mStreet.getStreetTypeCode()));
    }

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
        Integer taskId = mStreet.getTaskId();
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Building b = realmObject.where(Building.class).equalTo("id", buildingId).equalTo("task_id", taskId).findFirst();
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

                    Number currentLocalIdNum = realmObject.where(Building.class).max("local_id");
                    int nextLocalId;
                    if (currentLocalIdNum == null) {
                        nextLocalId = 1;
                    } else {
                        nextLocalId = currentLocalIdNum.intValue() + 1;
                    }
                    building.setLocalId(nextLocalId);

                    building.setStreetId(streetId);
                    building.setTaskId(taskId);
                    building.setNew(true);

                }
                building.setHouseNumber(etHouse.getText().toString());
                StreetType streetType = (StreetType) spStreetType.getSelectedItem();
                building.setStreetType(streetType.getId());
                BuildingType buildingType = (BuildingType) spBuildingType.getSelectedItem();
                building.setBuildingType(buildingType.getId());
                BuildingStatus buildingStatus = (BuildingStatus) spBuildingStatus.getSelectedItem();
                building.setBuildingStatus(buildingStatus);
                building.setOwnerName(etOwner.getText().toString().trim());
                building.setLatitude(mSelectedLat);
                Integer tempInhabitants = Integer.valueOf(etLivingCount.getText().toString());
                building.setTemporaryInhabitants(tempInhabitants);
                building.setLongitude(mSelectedLon);
                building.setComment(etComment.getText().toString());
                building.setKato(etKato.getText().toString());
                realmObject.insertOrUpdate(building);

                if (!building.isNew()) {
                    if (mBuilding.getBuildingType() != buildingType.getId()) {
                        addHistory(mStreet.getTaskId(), 5, new TempNewData(String.valueOf(buildingType.getId())), building.getId(), realmObject);
                    }

                    if (building.getOwnerName() != null && (!mBuilding.getOwnerName().equals(building.getOwnerName()))) {
                        addHistory(mStreet.getTaskId(), 7, new TempNewData(building.getOwnerName()), building.getId(), realmObject);
                    }

                    if (building.getComment() != null && !building.getComment().equals("") && !mBuilding.getComment().equals(building.getComment())) {
                        addHistory(mStreet.getTaskId(), 10, new TempNewData(building.getComment()), building.getId(), realmObject);
                    }

                    if (building.getHouseNumber() != null && !building.getHouseNumber().equals("") && !mBuilding.getHouseNumber().equals(building.getHouseNumber())) {
                        addHistory(mStreet.getTaskId(), 15, new TempNewData(building.getHouseNumber()), building.getId(), realmObject);
                    }

                    if (!etLivingCount.getText().toString().equals("") && tempInhabitants != mBuilding.getTemporaryInhabitants()) {
                        addHistory(mStreet.getTaskId(), 8, new TempNewData(String.valueOf(tempInhabitants)), building.getId(), realmObject);
                    }

                    if (mSelectedLat != null && !mSelectedLon.isNaN() && mSelectedLon != null && !mSelectedLon.isNaN() && mSelectedLat != mBuilding.getLatitude() && mSelectedLon != mBuilding.getLongitude()) {
                        addHistory(mStreet.getTaskId(), 14, new TempNewData(mSelectedLat, mSelectedLon), building.getId(), realmObject);
                    }
                }
            });
        } finally {
            if (realm != null)
                realm.close();
        }
        getActivity().onBackPressed();
    }

    private void addHistory(int taskId, int changeType, TempNewData newData, int buildingId, Realm realm) {
        History history = new History();
        history.setTaskId(taskId);
        history.setChangeType(changeType);
        history.setNewData(newData);
        history.setObjectId(buildingId);
        history.setObjectType(2);
        HistoryManager.getInstance().addOrUpdateHistory(history, realm);
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
