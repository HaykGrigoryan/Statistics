package com.constantlab.statistics.ui.apartments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.Actions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Sunny Kinger on 13-12-2017.
 */

public class ApartmentFragment extends BaseFragment implements ApartmentAdapter.InteractionListener {

    private static final int REQUEST_ADD_APARTMENT = 89;
    private static final int REQUEST_EDIT_APARTMENT = 90;
    Integer buildingId;
    String buildingName;
    @BindView(R.id.rv_apartments)
    RecyclerView rvApartments;
    @BindView(R.id.pb_apartments)
    ProgressBar pbApartments;
    @BindView(R.id.tv_no_apartments)
    TextView tvNoApartments;
    @BindView(R.id.iv_add)
    ImageView add;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    private ApartmentAdapter mApartmentAdapter;


    public static ApartmentFragment newInstance(Integer buildingId, String buildingName) {
        ApartmentFragment fragment = new ApartmentFragment();
        Bundle args = new Bundle();
        args.putInt(ApartmentActivity.BUILDING_TAG, buildingId);
        args.putString(ApartmentActivity.BUILDING_NAME_TAG, buildingName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ApartmentActivity.BUILDING_TAG);
            buildingName = getArguments().getString(ApartmentActivity.BUILDING_NAME_TAG);
        }
        mApartmentAdapter = new ApartmentAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        showDummyData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (buildingName != null) {
            mToolbarTitle.setText(buildingName);
        }
    }

    private void showDummyData() {
        List<Apartment> apartmentList = getDummyList();
        if (apartmentList != null && apartmentList.size() > 0) {
            tvNoApartments.setVisibility(View.INVISIBLE);
            mApartmentAdapter.setApartmentList(apartmentList);
            mApartmentAdapter.setInteractionListener(this);
        } else {
            tvNoApartments.setVisibility(View.VISIBLE);
        }
    }

    private List<Apartment> getDummyList() {
        List<Apartment> apartmentList = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Building building = realm.where(Building.class).equalTo("id", buildingId).findFirst();
            if (building != null && building.getApartmentList() != null) {
                apartmentList = realm.copyFromRealm(building.getApartmentList());
            }
            return apartmentList;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupRecyclerView() {
        rvApartments.setHasFixedSize(true);
        rvApartments.setMotionEventSplittingEnabled(true);
        rvApartments.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        rvApartments.setLayoutManager(llm);
        rvApartments.setAdapter(mApartmentAdapter);
    }


    @OnClick(R.id.iv_add)
    public void addApartment() {
        Intent intent = new Intent(getContext(), ApartmentActivity.class);
        intent.putExtra(ApartmentActivity.ACTION_TAG, Actions.ADD_APARTMENT);
        intent.putExtra(ApartmentActivity.BUILDING_TAG, buildingId);
        startActivityForResult(intent, REQUEST_ADD_APARTMENT);
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onEditApartment(Apartment apartment, int adapterPosition) {
        Intent intent = new Intent(getContext(), ApartmentActivity.class);
        intent.putExtra(ApartmentActivity.ACTION_TAG, Actions.EDIT_APARTMENT);
        intent.putExtra(ApartmentActivity.APARTMENT_TAG, apartment.getId());
        startActivityForResult(intent, REQUEST_ADD_APARTMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_APARTMENT && resultCode == Activity.RESULT_OK) {
            mApartmentAdapter.clear();
            showDummyData();
            getActivity().setResult(Activity.RESULT_OK);
        } else if (requestCode == REQUEST_EDIT_APARTMENT && resultCode == Activity.RESULT_OK) {
            mApartmentAdapter.clear();
            showDummyData();
            getActivity().setResult(Activity.RESULT_OK);
        }
    }
}
