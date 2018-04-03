package com.constantlab.statistics.ui.buildings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.models.TempNewData;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.MapActivity;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.ui.map.OSMMapFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.GsonUtils;
import com.constantlab.statistics.utils.HistoryManager;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.constantlab.statistics.utils.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    Integer taskId;
    Integer userId;
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
    private Task mTask;
    private Building mBuilding;

    private boolean isFlatLevelEnabled;

    public static BuildingDetailsFragment newInstance(Integer buildingId, String buildingName, Integer streetId, Integer taskId) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        args.putInt(ConstKeys.TAG_STREET, streetId);
        args.putInt(ConstKeys.TAG_TASK, taskId);
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
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
            buildingName = getArguments().getString(ConstKeys.TAG_BUILDING_NAME);
            userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
            mStreet = getStreet();
            mTask = getTask();

        }
    }

    private Street getStreet() {
        Realm realm = null;
        Street street = new Street();
        try {
            realm = Realm.getDefaultInstance();
            street = realm.where(Street.class).equalTo("task_id", taskId).equalTo("id", streetId).equalTo("user_id", userId).findFirst();
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

    private Task getTask() {
        Realm realm = null;
        Task task = new Task();
        try {
            realm = Realm.getDefaultInstance();
            task = realm.where(Task.class).equalTo("task_id", taskId).equalTo("user_id", userId).findFirst();
            if (task != null) {
                task = realm.copyFromRealm(task);
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return task;
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
            mBuilding = realm.where(Building.class).equalTo("task_id", taskId).equalTo("id", buildingId).equalTo("user_id", userId).findFirst();
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
        etKato.setText(mTask.getKatoCode());
        spBuildingType.setSelection(BuildingType.getIndex(mBuildingTypes, object.getBuildingType()));
        spBuildingStatus.setSelection(BuildingStatus.getIndex(mBuildingStatus, object.getBuildingStatus()));
        etLivingCount.setText(String.valueOf(object.getTemporaryInhabitants()));
        mSelectedLat = object.getLatitude();
        mSelectedLon = object.getLongitude();
        updateLocationIndicator(object.getLatitude() != null && object.getLongitude() != null);
    }

    private void loadStaticContent() {
        loadStreetType();
        loadBuildingType();
        loadBuildingStatus();
        refreshPeopleNumberState();
        etStreetName.setText(mStreet.getDisplayName(getContext()));
        etKato.setText(mTask.getKatoCode());
    }

    private void refreshPeopleNumberState() {
        int typeId = ((BuildingType) spBuildingType.getSelectedItem()).getId();
        int statusId = ((BuildingStatus) spBuildingStatus.getSelectedItem()).getId();
        isFlatLevelEnabled = Building.isFlatLevelEnabled(typeId, statusId) || Building.isTypeClosedInstitution(typeId);
        if (isFlatLevelEnabled) {
            etLivingCount.setVisibility(View.GONE);
            etLivingCount.setText("");
        } else {
            etLivingCount.setVisibility(View.VISIBLE);
        }

    }

    private List<BuildingType> mBuildingTypes;

    private void loadBuildingType() {
        mBuildingTypes = (List<BuildingType>) RealmManager.getInstance().getTypes(BuildingType.class);// GsonUtils.getBuildingTypeData(getContext());

        ArrayAdapter<BuildingType> arrayAdapterEditBl = new ArrayAdapter<BuildingType>(getContext(), R.layout.spinner_item, mBuildingTypes);
        arrayAdapterEditBl.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spBuildingType.setAdapter(arrayAdapterEditBl);
        spBuildingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                refreshPeopleNumberState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<BuildingStatus> mBuildingStatus;

    private void loadBuildingStatus() {
        mBuildingStatus = (List<BuildingStatus>) RealmManager.getInstance().getTypes(BuildingStatus.class);// GsonUtils.getBuildingStatusData(getContext());
        ArrayAdapter<BuildingStatus> arrayAdapterEditBl = new ArrayAdapter<BuildingStatus>(getContext(), R.layout.spinner_item, mBuildingStatus);
        arrayAdapterEditBl.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spBuildingStatus.setAdapter(arrayAdapterEditBl);
        spBuildingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshPeopleNumberState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<StreetType> mStreetType;

    private void loadStreetType() {
        mStreetType = (List<StreetType>) RealmManager.getInstance().getTypes(StreetType.class);// GsonUtils.getStreetTypeData(getContext());

        ArrayAdapter<StreetType> arrayAdapterEditSt = new ArrayAdapter<StreetType>(getContext(), R.layout.spinner_item, mStreetType);
        arrayAdapterEditSt.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStreetType.setAdapter(arrayAdapterEditSt);
        spStreetType.setEnabled(false);
        spStreetType.setSelection(StreetType.getIndex(mStreetType, mStreet.getStreetTypeCode()));
    }

    @OnClick(R.id.btn_map)
    public void gotoMaps() {
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra(ConstKeys.KEY_MAP_ACTION, OSMMapFragment.MapAction.PICK_LOCATION.ordinal());
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

        if (Building.isTypeSpetialInstitution(((BuildingType) spBuildingType.getSelectedItem()).getId()) && !isFlatLevelEnabled) {
            if (etLivingCount.getText().toString().isEmpty()) {
                if (proceed) {
                    etLivingCount.setError(getString(R.string.error_empty_field));
                }
                proceed = false;
            }
        }


        if (mSelectedLat == null || mSelectedLat.isNaN() || mSelectedLon == null || mSelectedLon.isNaN()) {
            if (proceed) {
                Toast.makeText(getContext(), getString(R.string.message_location_required), Toast.LENGTH_SHORT).show();
            }
            proceed = false;
        }

        if (proceed) {
            if (RealmManager.getInstance().checkBuildingDuplicateName(mStreet.getTaskId(), mStreet.getId(), buildingId, etHouse.getText().toString(), userId)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.dialog_title_attention));
                builder.setMessage(getString(R.string.dialog_duplicate_building_number));
                builder.setPositiveButton(getString(R.string.label_ok), null);
                builder.show();
            } else {
                saveDataToDatabase();
            }
        }
    }

    private void saveDataToDatabase() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Building b = realmObject.where(Building.class).equalTo("id", buildingId).equalTo("task_id", taskId).equalTo("user_id", userId).findFirst();
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
                    building.setUserId(userId);
                    building.setNew(true);

                }
                building.setHouseNumber(etHouse.getText().toString());
//                StreetType streetType = (StreetType) spStreetType.getSelectedItem();
//                building.setStreetType(streetType.getId());
                BuildingType buildingType = (BuildingType) spBuildingType.getSelectedItem();
                building.setBuildingType(buildingType.getId());
                BuildingStatus buildingStatus = (BuildingStatus) spBuildingStatus.getSelectedItem();
                building.setBuildingStatus(buildingStatus.getId());
                building.setOwnerName(etOwner.getText().toString().trim());
                building.setLatitude(mSelectedLat);
                String livingCount = etLivingCount.getText().toString();
//                boolean isTypeClosedInstitution = Building.isTypeClosedInstitution(((BuildingType) spBuildingType.getSelectedItem()).getId());
                Integer tempInhabitants = 0;
                if (isFlatLevelEnabled) {
                    building.setTemporaryInhabitants(null);
                } else {
                    tempInhabitants = livingCount.equals("") ? 0 : Integer.parseInt(livingCount);
                    building.setTemporaryInhabitants(tempInhabitants);
                }
                building.setLongitude(mSelectedLon);
                building.setComment(etComment.getText().toString());
                building.setKato(etKato.getText().toString());
                building.setEdited(true);
                building.setChangeTime(Calendar.getInstance().getTimeInMillis());
                if (!building.isNew()) {
                    if (!mBuilding.getBuildingType().equals(buildingType.getId())) {
                        addHistory(mStreet.getTaskId(), 5, new TempNewData(String.valueOf(buildingType.getId())), new TempNewData(String.valueOf(mBuilding.getBuildingType())), building.getId(), building.getId(), realmObject);
                    }

                    if (!mBuilding.getBuildingStatus().equals(buildingStatus.getId())) {
                        addHistory(mStreet.getTaskId(), 6, new TempNewData(String.valueOf(buildingStatus.getId())), new TempNewData(String.valueOf(mBuilding.getBuildingStatus())), building.getId(), building.getId(), realmObject);
                    }

                    if (!mBuilding.getOwnerName().equals(building.getOwnerName())) {
                        addHistory(mStreet.getTaskId(), 7, new TempNewData(building.getOwnerName()), new TempNewData(mBuilding.getOwnerName()), building.getId(), building.getId(), realmObject);
                    }

                    if (!mBuilding.getComment().equals(building.getComment())) {
                        addHistory(mStreet.getTaskId(), 10, new TempNewData(building.getComment()), new TempNewData(mBuilding.getComment()), building.getId(), building.getId(), realmObject);
                    }

                    if (building.getHouseNumber() != null && !building.getHouseNumber().equals("") && !mBuilding.getHouseNumber().equals(building.getHouseNumber())) {
                        addHistory(mStreet.getTaskId(), 15, new TempNewData(building.getHouseNumber()), new TempNewData(mBuilding.getHouseNumber()), building.getId(), building.getId(), realmObject);
                    }
                    if (isFlatLevelEnabled) {
                        removeHistory(mStreet.getTaskId(), 8, building.getId(), realmObject);
                    } else {
                        if (!etLivingCount.getText().toString().equals("") && !tempInhabitants.equals(mBuilding.getTemporaryInhabitants())) {
                            addHistory(mStreet.getTaskId(), 8, new TempNewData(String.valueOf(tempInhabitants)), new TempNewData(String.valueOf(mBuilding.getTemporaryInhabitants())), building.getId(), building.getId(), realmObject);
                        }
                    }

                    if (mSelectedLat != null && !mSelectedLon.isNaN() && mSelectedLon != null && !mSelectedLon.isNaN() && mSelectedLat != mBuilding.getLatitude() && mSelectedLon != mBuilding.getLongitude()) {
                        addHistory(mStreet.getTaskId(), 14, new TempNewData(mSelectedLat, mSelectedLon), new TempNewData(mBuilding.getLatitude(), mBuilding.getLongitude()), building.getId(), building.getId(), realmObject);
                    }
                } else {
                    Integer referenceId = null;
                    referenceId = addHistory(taskId, 2, new TempNewData(building.getHouseNumber()), new TempNewData(""), mStreet.getId(), building.getId(), realmObject);
                    addHistory(taskId, 15, new TempNewData(building.getHouseNumber()), new TempNewData(""), building.getId(), building.getId(), realmObject);
//                    if (!mStreet.isNew()) {
//                        referenceId = addHistory(taskId, 2, new TempNewData(building.getHouseNumber()), new TempNewData(""), mStreet.getId(), building.getId(), realmObject);
//                    } else {
//                        referenceId = addHistory(taskId, 12, new TempNewData(building.getHouseNumber()), new TempNewData(""), null, building.getId(), realmObject, mStreet.getHistoryId());
//                    }
                    building.setHistoryId(referenceId);
                    addHistory(mStreet.getTaskId(), 5, new TempNewData(String.valueOf(buildingType.getId())), new TempNewData(""), null, building.getId(), realmObject, referenceId);
                    addHistory(mStreet.getTaskId(), 6, new TempNewData(String.valueOf(buildingStatus.getId())), new TempNewData(""), null, building.getId(), realmObject, referenceId);
                    if (building.getOwnerName() != null && !building.getOwnerName().equals("")) {
                        addHistory(mStreet.getTaskId(), 7, new TempNewData(building.getOwnerName()), new TempNewData(""), null, building.getId(), realmObject, referenceId);
                    }
                    if (building.getComment() != null && !building.getComment().equals("")) {
                        addHistory(mStreet.getTaskId(), 10, new TempNewData(building.getComment()), new TempNewData(""), null, building.getId(), realmObject, referenceId);
                    }
                    if (!isFlatLevelEnabled) {
                        if (!etLivingCount.getText().toString().equals("")) {
                            addHistory(mStreet.getTaskId(), 8, new TempNewData(String.valueOf(tempInhabitants)), new TempNewData(""), null, building.getId(), realmObject, referenceId);
                        }
                    }

                    if (mSelectedLat != null && !mSelectedLon.isNaN() && mSelectedLon != null && !mSelectedLon.isNaN()) {
                        addHistory(mStreet.getTaskId(), 14, new TempNewData(mSelectedLat, mSelectedLon), new TempNewData(Double.NaN, Double.NaN), null, building.getId(), realmObject, referenceId);
                    }
                }
                realmObject.insertOrUpdate(building);
                Building.refreshCounts(userId, taskId, streetId, building.getId(), realmObject);
                Street.refreshCounts(userId, taskId, streetId, realmObject);

                if (!Building.isStatusInactive(building.getBuildingStatus()) && !Building.isTypeSpetialInstitution(building.getBuildingType()) && !Building.isTypeClosedInstitution(building.getBuildingType())) {
                    RealmManager.getInstance().changeHistoryInactiveStatus(taskId, buildingId, false, realmObject, userId);
                } else {
                    RealmManager.getInstance().changeHistoryInactiveStatus(taskId, buildingId, true, realmObject, userId);
                }

            });
        } finally {
            if (realm != null)
                realm.close();
        }
        UIUtils.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    private Integer addHistory(int taskId, int changeType, TempNewData newData, TempNewData oldData, Integer buildingId, Integer tempBuildingId, Realm realm) {
        return addHistory(taskId, changeType, newData, oldData, buildingId, tempBuildingId, realm, null);
    }


    private Integer addHistory(int taskId, int changeType, TempNewData newData, TempNewData oldData, Integer buildingId, Integer tempBuildingId, Realm realm, Integer referenceId) {
        History history = new History();
        history.setTaskId(taskId);
        history.setChangeType(changeType);
        history.setNewData(newData);
        history.setOldData(oldData);
        history.setObjectId(buildingId);
        history.setTempObjectId(tempBuildingId);
        history.setReferenceId(referenceId);
        history.setObjectType(2);
        return HistoryManager.getInstance().addOrUpdateHistory(userId, history, realm);
    }

    private void removeHistory(int taskId, int changeType, int tempBuildingId, Realm realm) {
        History history = new History();
        history.setTaskId(taskId);
        history.setChangeType(changeType);
        history.setTempObjectId(tempBuildingId);
        history.setObjectType(2);
        HistoryManager.getInstance().removeHistory(userId, history, realm);
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
        if (locationSelected) {
            StringBuilder builder = new StringBuilder();
            builder.append(mSelectedLat);
            builder.append("\n");
            builder.append(mSelectedLon);
            mLocationIndicator.setText(builder.toString());
            mLocationIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            mLocationIndicator.setText(locationSelected ? getString(R.string.label_have_mark) : getString(R.string.label_not_have_mark));
        } else {
            mLocationIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.color_red));
            mLocationIndicator.setText(getString(R.string.label_not_have_mark));
        }

    }
}
