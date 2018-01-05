package com.constantlab.statistics.ui.apartments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.models.TempNewData;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.GsonUtils;
import com.constantlab.statistics.utils.HistoryManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Hayk on 26/12/2017.
 */

public class ApartmentDetailsFragment extends BaseFragment {
    Integer taskId;
    Integer buildingId;
    Integer apartmentId;
    String apartmentName;
    @BindView(R.id.et_apartment)
    EditText etApartmentNumber;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.et_owner)
    EditText etOwner;
    @BindView(R.id.sp_apartment_type)
    Spinner spApartmentType;

    @BindView(R.id.et_residents)
    EditText etResidents;

    @BindView(R.id.title)
    TextView mTitle;

    public static ApartmentDetailsFragment newInstance(Integer apartmentId, String apartmentName, Integer buildingId, Integer taskId) {
        ApartmentDetailsFragment fragment = new ApartmentDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_TASK, taskId);
        args.putInt(ConstKeys.TAG_APARTMENT, apartmentId);
        args.putString(ConstKeys.TAG_APARTMENT_NAME, apartmentName);
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApartmentTypes = new ArrayList<>();
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
            apartmentId = getArguments().getInt(ConstKeys.TAG_APARTMENT);
            apartmentName = getArguments().getString(ConstKeys.TAG_APARTMENT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment_details, container, false);
        ButterKnife.bind(this, view);

        loadStaticData();
        showStoredInformation();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (apartmentName != null) {
            mTitle.setText(apartmentName);
        }
    }

    private void showStoredInformation() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Apartment apartment = realm.where(Apartment.class).equalTo("id", apartmentId).findFirst();
            if (apartment != null) {
                Apartment object = realm.copyFromRealm(apartment);
                showData(object);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void loadStaticData() {
        loadApartmentType();
    }

    private List<ApartmentType> mApartmentTypes;

    private void loadApartmentType() {
        mApartmentTypes = GsonUtils.getApartmentTypeData(getContext());
        ArrayAdapter<ApartmentType> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, mApartmentTypes);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spApartmentType.setAdapter(arrayAdapter);
    }

    private Integer getTaskId(int buildingId) {
        Realm realm = null;
        try {
            realm = RealmManager.getInstance().getDefaultInstance(getContext());
            Building building = realm.where(Building.class).equalTo("id", buildingId).findFirst();
            Street street = realm.where(Street.class).equalTo("id", building.getStreetId()).findFirst();
            return street.getTaskId();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void showData(Apartment object) {
        etApartmentNumber.setText(object.getApartmentNumber() != null ? object.getApartmentNumber() + "" : "");
        etOwner.setText(object.getOwnerName());
        etComment.setText(object.getComment() != null ? object.getComment() : "");
        spApartmentType.setSelection(ApartmentType.getIndex(mApartmentTypes, object.getApartmentType()));
        etResidents.setText(object.getTotalInhabitants() != null ? String.format(Locale.getDefault(), "%d", object.getTotalInhabitants()) : "");
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.btn_save)
    public void save() {
        etApartmentNumber.setError(null);
        etResidents.setError(null);
        boolean proceed = true;

        if (etApartmentNumber.getText().toString().isEmpty()) {
            proceed = false;
            etApartmentNumber.setError(getString(R.string.error_empty_field));
        }

        if (etResidents.getText().toString().isEmpty()) {
            proceed = false;
            etResidents.setError(getString(R.string.error_empty_field));
        }

        if (proceed) {
            saveToDatabase();
        }
    }

    private void saveToDatabase() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Apartment a = realmObject.where(Apartment.class).equalTo("id", apartmentId).equalTo("task_id", taskId).findFirst();
                Apartment apartment = null;
                if (a != null) {
                    apartment = realmObject.copyFromRealm(a);
                } else {
                    apartment = new Apartment();
                    Number currentIdNum = realmObject.where(Apartment.class).max("id");
                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    apartment.setId(nextId);

                    Number currentLocalIdNum = realmObject.where(Apartment.class).max("local_id");
                    int nextLocalId;
                    if (currentLocalIdNum == null) {
                        nextLocalId = 1;
                    } else {
                        nextLocalId = currentLocalIdNum.intValue() + 1;
                    }
                    apartment.setLocalId(nextLocalId);

                    apartment.setBuildingId(buildingId);
                    apartment.setTaskId(taskId);
                    apartment.setNew(true);
                }

                apartment.setTotalInhabitants(Integer.parseInt(etResidents.getText().toString().trim()));
                apartment.setOwnerName(etOwner.getText().toString().trim());
                apartment.setComment(etComment.getText().toString().trim());
                apartment.setApartmentType(((ApartmentType) spApartmentType.getSelectedItem()).getId());
                apartment.setApartmentNumber(Integer.parseInt(etApartmentNumber.getText().toString()));

                realmObject.insertOrUpdate(apartment);
                if (!apartment.isNew()) {
                    int taskId = getTaskId(buildingId);
                    if (etOwner.getText().toString() != null && !etOwner.getText().toString().equals("")) {
                        addHistory(taskId, 7, new TempNewData(apartment.getOwnerName()), apartment.getId(), realmObject);
                    }

                    if (etComment.getText().toString() != null && !etComment.getText().toString().equals("")) {
                        addHistory(taskId, 10, new TempNewData(apartment.getComment()), apartment.getId(), realmObject);
                    }
                    addHistory(taskId, 5, new TempNewData(String.valueOf(apartment.getApartmentType())), apartment.getId(), realmObject);
                    addHistory(taskId, 9, new TempNewData(String.valueOf(apartment.getTotalInhabitants())), apartment.getId(), realmObject);
                    addHistory(taskId, 16, new TempNewData(String.valueOf(apartment.getApartmentNumber())), apartment.getId(), realmObject);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        getActivity().onBackPressed();
    }

    private void addHistory(int taskId, int changeType, TempNewData newData, int apartmentId, Realm realm) {
        History history = new History();
        history.setTaskId(taskId);
        history.setChangeType(changeType);
        history.setNewData(newData);
        history.setObjectId(apartmentId);
        history.setObjectType(3);
        HistoryManager.getInstance().addOrUpdateHistory(history, realm);
    }
}
