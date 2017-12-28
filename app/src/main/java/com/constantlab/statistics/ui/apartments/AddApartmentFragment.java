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
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Sunny Kinger on 18-12-2017.
 */

public class AddApartmentFragment extends BaseFragment {

    Integer buildingId;

    @BindView(R.id.et_apartment)
    EditText etApartmentNumber;
    @BindView(R.id.et_total_rooms)
    EditText etTotalRooms;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.et_owner)
    EditText etOwner;
    @BindView(R.id.sp_apartment_type)
    Spinner spApartmentType;

    //    @BindView(R.id.et_area)
//    EditText etArea;
    @BindView(R.id.et_residents)
    EditText etResidents;
    @BindView(R.id.tv_save)
    TextView tvSave;

    public static AddApartmentFragment newInstance(Integer buildingId) {
        AddApartmentFragment fragment = new AddApartmentFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_apartment, container, false);
        ButterKnife.bind(this, view);
        setApartmentTypes();
        return view;
    }


    private void setApartmentTypes() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<ApartmentType> realmResults = realm.where(ApartmentType.class).findAll();
            List<ApartmentType> apartmentTypes = realm.copyFromRealm(realmResults);
            ArrayAdapter<ApartmentType> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, apartmentTypes);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spApartmentType.setAdapter(arrayAdapter);


        } finally {
            if (realm != null)
                realm.close();
        }
    }


    @OnClick(R.id.tv_save)
    public void save() {
        etApartmentNumber.setError(null);
//        etArea.setError(null);
        etResidents.setError(null);
        etTotalRooms.setError(null);
        boolean proceed = true;

        if (etApartmentNumber.getText().toString().isEmpty()) {
            proceed = false;
            etApartmentNumber.setError(getString(R.string.error_empty_field));
        }

        if (etTotalRooms.getText().toString().isEmpty()) {
            proceed = false;
            etTotalRooms.setError(getString(R.string.error_empty_field));
        }

        if (etOwner.getText().toString().isEmpty()) {
            proceed = false;
            etOwner.setError(getString(R.string.error_empty_field));
        }
//
//        if (etArea.getText().toString().isEmpty()) {
//            proceed = false;
//            etArea.setError(getString(R.string.error_empty_field));
//        }

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
                Apartment apartment = new Apartment();
                Number currentIdNum = realmObject.where(Apartment.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                apartment.setId(nextId);
                apartment.setTotalRooms(Integer.parseInt(etTotalRooms.getText().toString().trim()));
                apartment.setTotalInhabitants(Integer.parseInt(etResidents.getText().toString().trim()));
                apartment.setOwnerName(etOwner.getText().toString().trim());
                apartment.setComment(etComment.getText().toString().trim());
                ApartmentType apartmentType = (ApartmentType) spApartmentType.getSelectedItem();
                apartment.setApartmentType(apartmentType);
//                apartment.setAreaSquare(Integer.parseInt(etArea.getText().toString().trim()));
                apartment.setApartmentNumber(etApartmentNumber.getText().toString().trim());

                Building building = realmObject.where(Building.class).equalTo("id", buildingId).findFirst();
                RealmList<Apartment> apartmentRealmList = building.getApartmentList();
                if (apartmentRealmList == null) {
                    apartmentRealmList = new RealmList<>();
                }
                apartmentRealmList.add(apartment);
                realmObject.insertOrUpdate(building);
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
