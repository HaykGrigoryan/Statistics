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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.apartments.ApartmentActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.Actions;

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
    Integer taskId;
    @BindView(R.id.rv_buildings)
    RecyclerView rvBuildings;
    @BindView(R.id.pb_buildings)
    ProgressBar pbBuildings;
    @BindView(R.id.tv_no_buildings)
    TextView tvNoBuildings;
    @BindView(R.id.iv_add)
    ImageView add;

    private BuildingsAdapter mBuildingsAdapter;


    public static BuildingsFragment newInstance(Integer taskId) {
        BuildingsFragment fragment = new BuildingsFragment();
        Bundle args = new Bundle();
        args.putInt(BuildingActivity.TASK_TAG, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(BuildingActivity.TASK_TAG);
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
            Task taskFirst = realm.where(Task.class).equalTo("id", taskId).findFirst();
            if (taskFirst != null && taskFirst.getBuildingList() != null) {
                buildingList = realm.copyFromRealm(taskFirst.getBuildingList());
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
        Intent intent = new Intent(getContext(), BuildingActivity.class);
        intent.putExtra(BuildingActivity.ACTION_TAG, Actions.EDIT_BUILDING);
        intent.putExtra(BuildingActivity.BUILDING_TAG, building.getId());
        startActivityForResult(intent, REQUEST_EDIT_BUILDING);
    }

    @Override
    public void onBuildingDetail(Building building, int adapterPosition) {
        Intent intent = new Intent(getContext(), ApartmentActivity.class);
        intent.putExtra(ApartmentActivity.BUILDING_TAG, building.getId());
        intent.putExtra(ApartmentActivity.ACTION_TAG, Actions.VIEW_APARTMENTS);
        startActivityForResult(intent, REQUEST_APARTMENTS);
    }

    @OnClick(R.id.iv_add)
    public void addBuilding() {
        Intent intent = new Intent(getContext(), BuildingActivity.class);
        intent.putExtra(BuildingActivity.ACTION_TAG, Actions.ADD_BUILDING);
        intent.putExtra(BuildingActivity.TASK_TAG, taskId);
        startActivityForResult(intent, REQUEST_ADD_BUILDING);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_BUILDING && resultCode == Activity.RESULT_OK) {
            mBuildingsAdapter.clear();
            showDummyData();
            Integer buildingId = data.getExtras().getInt(BuildingActivity.BUILDING_TAG);
            Intent intent = new Intent(getContext(), ApartmentActivity.class);
            intent.putExtra(ApartmentActivity.BUILDING_TAG, buildingId);
            intent.putExtra(ApartmentActivity.ACTION_TAG, Actions.VIEW_APARTMENTS);
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
