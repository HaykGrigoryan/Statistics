package com.constantlab.statistics.ui.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.buildings.BuildingActivity;
import com.constantlab.statistics.ui.buildings.BuildingsFragment;
import com.constantlab.statistics.ui.history.HistoryFragment;
import com.constantlab.statistics.ui.street.StreetFragment;
import com.constantlab.statistics.utils.Actions;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class TasksFragment extends BaseFragment implements TasksAdapter.InteractionListener {

    private static final int REQUEST_BUILDINGS = 45;
    @BindView(R.id.rv_tasks)
    RecyclerView rvTasks;
    @BindView(R.id.pb_tasks)
    ProgressBar pbTasks;
    @BindView(R.id.tv_no_tasks)
    TextView tvNoTasks;
    private TasksAdapter mTaskAdapter;

    @BindView(R.id.lMenu)
    View mMenuIcon;

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskAdapter = new TasksAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
//        if (mTaskAdapter.isEmpty()) {
//            mTaskAdapter.setInteractionListener(this);
        refreshData();
//            showDummyData();
//        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getDrawer().openDrawer(Gravity.LEFT);
            }
        });
    }

    private void refreshData() {
        List<Task> taskList = getTaskList();
        if (taskList != null && taskList.size() > 0) {
            tvNoTasks.setVisibility(View.INVISIBLE);
            mTaskAdapter.setTaskList(taskList);
            mTaskAdapter.setInteractionListener(this);
        } else {
            tvNoTasks.setVisibility(View.VISIBLE);
        }
    }

    private List<Task> getTaskList() {
        List<Task> taskList;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Task> realmResults = realm.where(Task.class).equalTo("user_id", SharedPreferencesManager.getInstance().getUser(getContext()).getUserId()).findAll();
            taskList = realm.copyFromRealm(realmResults);
            return taskList;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupRecyclerView() {
        rvTasks.setHasFixedSize(true);
        rvTasks.setMotionEventSplittingEnabled(true);
        rvTasks.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTasks.getContext(),
                llm.getOrientation());
        rvTasks.addItemDecoration(dividerItemDecoration);
        rvTasks.setLayoutManager(llm);
        rvTasks.setAdapter(mTaskAdapter);
    }

    @Override
    public void onTaskSelected(Task task, int position) {
        int sync = SharedPreferencesManager.getInstance().isSyncing(getContext());
        if (sync == 0) {
            showSnackMessage(getString(R.string.message_sync_in_progress));
        } else {
            NotificationCenter.getInstance().notifyOpenPage(StreetFragment.newInstance(task.getTaskId(), task.getDisplayName()));//Actions.VIEW_BUILDINGS
        }
    }

    @Override
    public void onTaskHistory(Task task, int position) {
        int sync = SharedPreferencesManager.getInstance().isSyncing(getContext());
        if (sync == 0) {
            showSnackMessage(getString(R.string.message_sync_in_progress));
        } else {
            NotificationCenter.getInstance().notifyOpenPage(HistoryFragment.newInstance(task.getTaskId(), task.getDisplayName()));//Actions.VIEW_BUILDINGS
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BUILDINGS && resultCode == Activity.RESULT_OK) {
//            refreshCount();
            mTaskAdapter.clear();
//            showDummyData();
        }
    }

//    private void refreshCount() {
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            realm.executeTransaction(realmObject -> {
//                RealmResults<Task> realmResults = realmObject.where(Task.class).findAll();
//                for (Task task : realmResults) {
//                    int totalApartments = 0;
//                    int totalResidents = 0;
//
//                    //Count Apartments
//                    for (Street street : task.getStreetList()) {
//                        for (Building building : street.getBuildingList()) {
//                            if (building.getApartmentList() != null) {
//                                totalApartments += building.getApartmentList().size();
//                                for (Apartment apartment : building.getApartmentList()) {
//                                    totalResidents += apartment.getTotalInhabitants();
//                                }
//                            }
//                        }
//                    }
//                    task.setTotalApartments(totalApartments);
//                    task.setTotalResidents(totalResidents);
//
//                }
//            });
//        } finally {
//            if (realm != null) {
//                realm.close();
//            }
//        }
//    }
}
