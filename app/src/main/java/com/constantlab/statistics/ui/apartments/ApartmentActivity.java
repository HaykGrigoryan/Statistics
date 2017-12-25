package com.constantlab.statistics.ui.apartments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.utils.Actions;
import com.constantlab.statistics.utils.ConstKeys;


/**
 * Created by Sunny Kinger on 13-12-2017.
 */

public class ApartmentActivity extends BaseActivity {

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
            if (action == Actions.VIEW_APARTMENTS) {
                Integer buildingId = extra.getInt(ConstKeys.TAG_BUILDING);
                showFragment(ApartmentFragment.newInstance(buildingId, ""), false);
            } else if (action == Actions.ADD_APARTMENT) {
                Integer buildingId = extra.getInt(ConstKeys.TAG_BUILDING);
                showFragment(AddApartmentFragment.newInstance(buildingId), false);
            } else if (action == Actions.EDIT_APARTMENT) {
                Integer apartmentId = extra.getInt(ConstKeys.TAG_APARTMENT);
                showFragment(EditApartmentFragment.newInstance(apartmentId), false);
            }
        }
    }
}
