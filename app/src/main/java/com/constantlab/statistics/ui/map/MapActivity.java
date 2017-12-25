package com.constantlab.statistics.ui.map;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;

/**
 * Created by Sunny Kinger on 11-12-2017.
 */

public class MapActivity extends BaseActivity {

    public static final String LATITUDE_TAG = "latitude";
    public static final String LONGITUDE_TAG = "longitude";

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);
        showFragment(MapFragment.newInstance(), false);
    }
}
