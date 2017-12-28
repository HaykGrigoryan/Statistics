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
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;

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
        NotificationCenter.getInstance().notifyOpenPage(ApartmentDetailsFragment.newInstance(-1, null));

    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onEditApartment(Apartment apartment, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(ApartmentDetailsFragment.newInstance(apartment.getId(), apartment.getDisplayName(getContext())));
    }
}
