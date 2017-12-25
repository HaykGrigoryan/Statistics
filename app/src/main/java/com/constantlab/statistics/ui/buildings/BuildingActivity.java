package com.constantlab.statistics.ui.buildings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.utils.Actions;


/**
 * Created by Sunny Kinger on 06-12-2017.
 */

public class BuildingActivity extends BaseActivity {
    public static final String TASK_TAG = "tasktag";
    public static final String BUILDING_TAG = "building_tag";
    public static final String ACTION_TAG = "action_tag";

    @Actions.Action
    int action;

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            action = extra.getInt(ACTION_TAG);
            if (action == Actions.VIEW_BUILDINGS) {
                Integer taskId = extra.getInt(TASK_TAG);
                showFragment(BuildingsFragment.newInstance(taskId), false);
            } else if (action == Actions.ADD_BUILDING) {
                Integer taskId = extra.getInt(TASK_TAG);
                showFragment(AddBuildingFragment.newInstance(taskId), false);
            } else if (action == Actions.EDIT_BUILDING) {
                Integer buildingId = extra.getInt(BUILDING_TAG);
                showFragment(EditBuildingFragment.newInstance(buildingId), false);
            }
        }
    }
}
