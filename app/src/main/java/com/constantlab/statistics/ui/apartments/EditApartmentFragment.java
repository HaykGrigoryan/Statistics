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
import com.constantlab.statistics.ui.base.BaseFragment;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Sunny Kinger on 19-12-2017.
 */

public class EditApartmentFragment extends BaseFragment {

    Integer buildingId;
    Integer apartmentId;
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

    public static EditApartmentFragment newInstance(Integer apartmentId) {
        EditApartmentFragment fragment = new EditApartmentFragment();
        Bundle args = new Bundle();
        args.putInt(ApartmentActivity.APARTMENT_TAG, apartmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ApartmentActivity.BUILDING_TAG);
            apartmentId = getArguments().getInt(ApartmentActivity.APARTMENT_TAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_apartment, container, false);
        ButterKnife.bind(this, view);
        showStoredInformation();
        return view;
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

    private void setApartmentTypes(ApartmentType apartmentType) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<ApartmentType> realmResults = realm.where(ApartmentType.class).findAll();
            List<ApartmentType> apartmentTypes = realm.copyFromRealm(realmResults);
            int index = -1;
            if (apartmentType != null) {
                index = apartmentTypes.indexOf(apartmentType);
            }
            ArrayAdapter<ApartmentType> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, apartmentTypes);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spApartmentType.setAdapter(arrayAdapter);
            if (index != -1) {
                spApartmentType.setSelection(index);
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void showData(Apartment object) {
        etApartmentNumber.setText(object.getApartmentNumber() != null ? object.getApartmentNumber() : "");
        etOwner.setText(object.getOwnerName());
        etComment.setText(object.getComment() != null ? object.getComment() : "");
        setApartmentTypes(object.getApartmentType());
//        etArea.setText(object.getAreaSquare() != null ? String.format(Locale.getDefault(), "%d", object.getAreaSquare()) : "");
        etResidents.setText(object.getTotalInhabitants() != null ? String.format(Locale.getDefault(), "%d", object.getTotalInhabitants()) : "");
        etTotalRooms.setText(object.getTotalRooms() != null ? String.format(Locale.getDefault(), "%d", object.getTotalRooms()) : "");
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

                Apartment a = realmObject.where(Apartment.class).equalTo("id", apartmentId).findFirst();
                if (a != null) {
                    Apartment apartment = realmObject.copyFromRealm(a);
                    apartment.setTotalRooms(Integer.parseInt(etTotalRooms.getText().toString().trim()));
                    apartment.setTotalInhabitants(Integer.parseInt(etResidents.getText().toString().trim()));
                    apartment.setOwnerName(etOwner.getText().toString().trim());
                    apartment.setComment(etComment.getText().toString().trim());
                    ApartmentType apartmentType = (ApartmentType) spApartmentType.getSelectedItem();
                    apartment.setApartmentType(apartmentType);
//                    apartment.setAreaSquare(Integer.parseInt(etArea.getText().toString().trim()));
                    apartment.setApartmentNumber(etApartmentNumber.getText().toString().trim());
                    realmObject.insertOrUpdate(apartment);
                }
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
