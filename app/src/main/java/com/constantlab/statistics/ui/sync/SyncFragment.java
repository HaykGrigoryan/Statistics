package com.constantlab.statistics.ui.sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by Hayk on 25/12/2017.
 */

public class SyncFragment extends BaseFragment {
    public static SyncFragment newInstance() {
        return new SyncFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
//        ButterKnife.bind(this, view);
        return view;
    }
}
