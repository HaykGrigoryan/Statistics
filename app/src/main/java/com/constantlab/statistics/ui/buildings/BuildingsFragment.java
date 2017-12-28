package com.constantlab.statistics.ui.buildings;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.apartments.ApartmentActivity;
import com.constantlab.statistics.ui.apartments.ApartmentFragment;
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
 * Created by Sunny Kinger on 05-12-2017.
 */

public class BuildingsFragment extends BaseFragment implements BuildingsAdapter.InteractionListener {

    private static final int REQUEST_ADD_BUILDING = 23;
    private static final int REQUEST_EDIT_BUILDING = 24;
    private static final int REQUEST_APARTMENTS = 34;
    Integer streetId;
    String streetName;
    @BindView(R.id.rv_buildings)
    RecyclerView rvBuildings;
    @BindView(R.id.pb_buildings)
    ProgressBar pbBuildings;
    @BindView(R.id.tv_no_buildings)
    TextView tvNoBuildings;
    @BindView(R.id.iv_add)
    ImageView add;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    private BuildingsAdapter mBuildingsAdapter;

    public static BuildingsFragment newInstance(Integer streetId, String streetName) {
        BuildingsFragment fragment = new BuildingsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_STREET, streetId);
        args.putString(ConstKeys.TAG_STREET_NAME, streetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            streetId = getArguments().getInt(ConstKeys.TAG_STREET);
            streetName = getArguments().getString(ConstKeys.TAG_STREET_NAME);
        }
        mBuildingsAdapter = new BuildingsAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buildings, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        showDummyData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (streetName != null) {
            mToolbarTitle.setText(streetName);
        }
    }

    private void showDummyData() {
        List<Building> buildingList = getDummyBuildingList();
        if (buildingList != null && buildingList.size() > 0) {
            mBuildingsAdapter.setInteractionListener(this);
            mBuildingsAdapter.setBuildingList(buildingList);
        } else {
            tvNoBuildings.setVisibility(View.VISIBLE);
        }
    }

    private List<Building> getDummyBuildingList() {
        List<Building> buildingList = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Street streetFirst = realm.where(Street.class).equalTo("id", streetId).findFirst();
            if (streetFirst != null && streetFirst.getBuildingList() != null) {
                buildingList = realm.copyFromRealm(streetFirst.getBuildingList());
            }
            return buildingList;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupRecyclerView() {
        rvBuildings.setHasFixedSize(true);
        rvBuildings.setMotionEventSplittingEnabled(true);
        rvBuildings.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        rvBuildings.setLayoutManager(llm);
        rvBuildings.setAdapter(mBuildingsAdapter);
    }

    @Override
    public void onEditBuilding(Building building, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(BuildingDetailsFragment.newInstance(building.getId(), building.getDisplayAddress(getContext())));
    }

    @Override
    public void onBuildingDetail(Building building, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(ApartmentFragment.newInstance(building.getId(), building.getDisplayAddress(getContext())));
    }

    @OnClick(R.id.iv_add)
    public void addBuilding() {
        NotificationCenter.getInstance().notifyOpenPage(BuildingDetailsFragment.newInstance(-1, null));
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_BUILDING && resultCode == Activity.RESULT_OK) {
            mBuildingsAdapter.clear();
            showDummyData();
            Integer buildingId = data.getExtras().getInt(ConstKeys.TAG_BUILDING);
            Intent intent = new Intent(getContext(), ApartmentActivity.class);
            intent.putExtra(ConstKeys.TAG_BUILDING, buildingId);
            intent.putExtra(ConstKeys.TAG_ACTION, Actions.VIEW_APARTMENTS);
            startActivityForResult(intent, REQUEST_APARTMENTS);
            getActivity().setResult(Activity.RESULT_OK);
        } else if (requestCode == REQUEST_EDIT_BUILDING && resultCode == Activity.RESULT_OK) {
            mBuildingsAdapter.clear();
            showDummyData();
            getActivity().setResult(Activity.RESULT_OK);
        } else if (requestCode == REQUEST_APARTMENTS && resultCode == Activity.RESULT_OK) {
            mBuildingsAdapter.clear();
            showDummyData();
            getActivity().setResult(Activity.RESULT_OK);
        }
    }
}
