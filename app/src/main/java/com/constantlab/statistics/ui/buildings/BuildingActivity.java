package com.constantlab.statistics.ui.buildings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.utils.Actions;
import com.constantlab.statistics.utils.ConstKeys;


/**
 * Created by Sunny Kinger on 06-12-2017.
 */

public class BuildingActivity extends BaseActivity {

    @Actions.Action
    int action;

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected int getTaskFragmentContainerId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            action = extra.getInt(ConstKeys.TAG_ACTION);
            if (action == Actions.VIEW_BUILDINGS) {
                Integer taskId = extra.getInt(ConstKeys.TAG_TASK);
                showFragment(BuildingsFragment.newInstance(taskId, "Task"), false);
            } else if (action == Actions.ADD_BUILDING) {
                Integer taskId = extra.getInt(ConstKeys.TAG_TASK);
                showFragment(AddBuildingFragment.newInstance(taskId), false);
            } else if (action == Actions.EDIT_BUILDING) {
                Integer buildingId = extra.getInt(ConstKeys.TAG_BUILDING);
                showFragment(EditBuildingFragment.newInstance(buildingId), false);
            }
        }
    }
}
