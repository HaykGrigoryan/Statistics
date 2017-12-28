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

import org.w3c.dom.Text;

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

    Integer buildingId;
    Integer apartmentId;
    String apartmentName;
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

    @BindView(R.id.et_residents)
    EditText etResidents;

    @BindView(R.id.title)
    TextView mTitle;

    public static ApartmentDetailsFragment newInstance(Integer apartmentId, String apartmentName) {
        ApartmentDetailsFragment fragment = new ApartmentDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_APARTMENT, apartmentId);
        args.putString(ConstKeys.TAG_APARTMENT_NAME, apartmentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
            apartmentId = getArguments().getInt(ConstKeys.TAG_APARTMENT);
            apartmentName = getArguments().getString(ConstKeys.TAG_APARTMENT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment_details, container, false);
        ButterKnife.bind(this, view);
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
            ArrayAdapter<ApartmentType> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, apartmentTypes);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
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
        etResidents.setText(object.getTotalInhabitants() != null ? String.format(Locale.getDefault(), "%d", object.getTotalInhabitants()) : "");
        etTotalRooms.setText(object.getTotalRooms() != null ? String.format(Locale.getDefault(), "%d", object.getTotalRooms()) : "");
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
        etTotalRooms.setError(null);
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
                Apartment a = realmObject.where(Apartment.class).equalTo("id", apartmentId).findFirst();
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
                }

                apartment.setTotalInhabitants(Integer.parseInt(etResidents.getText().toString().trim()));
                apartment.setOwnerName(etOwner.getText().toString().trim());
                apartment.setComment(etComment.getText().toString().trim());
                apartment.setApartmentType((ApartmentType) spApartmentType.getSelectedItem());
                apartment.setApartmentNumber(etApartmentNumber.getText().toString().trim());
                realmObject.insertOrUpdate(apartment);

            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        getActivity().onBackPressed();
    }
}
