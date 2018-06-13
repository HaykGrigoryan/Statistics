package com.constantlab.statistics.ui.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.widget.LinearLayout;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.utils.ConstKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sunny Kinger on 11-12-2017.
 */

public class MapActivity extends BaseActivity {

    public static final String LATITUDE_TAG = "latitude";
    public static final String LONGITUDE_TAG = "longitude";
    @BindView(R.id.contentView)
    LinearLayout mContentView;

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
        ButterKnife.bind(this);
        if (getIntent() != null) {
            OSMMapFragment.MapAction action = OSMMapFragment.MapAction.values()[getIntent().getIntExtra(ConstKeys.KEY_MAP_ACTION, 0)];
            Double lat = getIntent().getDoubleExtra(ConstKeys.KEY_LATITUDE, Double.NaN);
            Double lon = getIntent().getDoubleExtra(ConstKeys.KEY_LONGITUDE, Double.NaN);
            Integer taskid = getIntent().getIntExtra(ConstKeys.KEY_TASK_ID, -1);
            if (action == OSMMapFragment.MapAction.SHOW_POLYGON) {
                showFragment(OSMMapFragment.newInstance(action, taskid), false);
            } else {
                showFragment(OSMMapFragment.newInstance(action, lat, lon), false);
            }

        } else {
            showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.PICK_LOCATION), false);
        }


    }

    public void showSnackMessage(String message) {
        Snackbar.make(mContentView, message, Snackbar.LENGTH_LONG).show();
    }
}
