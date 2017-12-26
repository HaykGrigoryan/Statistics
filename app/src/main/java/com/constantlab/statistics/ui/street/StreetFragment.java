package com.constantlab.statistics.ui.street;

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
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.apartments.ApartmentActivity;
import com.constantlab.statistics.ui.apartments.ApartmentFragment;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.buildings.BuildingDetailsFragment;
import com.constantlab.statistics.ui.buildings.BuildingsAdapter;
import com.constantlab.statistics.ui.buildings.BuildingsFragment;
import com.constantlab.statistics.utils.Actions;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Hayk on 26/12/2017.
 */

public class StreetFragment extends BaseFragment implements StreetAdapter.InteractionListener {
    Integer taskId;
    String taskName;
    @BindView(R.id.rv_streets)
    RecyclerView rvStreets;
    @BindView(R.id.pb_streets)
    ProgressBar pbStreets;
    @BindView(R.id.tv_no_streets)
    TextView tvNoStreets;
    @BindView(R.id.iv_map)
    ImageView btnMap;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    private StreetAdapter mStreetAdapter;


    public static StreetFragment newInstance(Integer taskId, String taskName) {
        StreetFragment fragment = new StreetFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_TASK, taskId);
        args.putString(ConstKeys.TAG_TASK_NAME, taskName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
            taskName = getArguments().getString(ConstKeys.TAG_TASK_NAME);
        }
        mStreetAdapter = new StreetAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streets, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        showDummyData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (taskName != null) {
            mToolbarTitle.setText(taskName);
        }
    }

    private void showDummyData() {
        List<Street> streetList = getDummyStreetList();
        if (streetList != null && streetList.size() > 0) {
            mStreetAdapter.setInteractionListener(this);
            mStreetAdapter.setStreetList(streetList);
        } else {
            tvNoStreets.setVisibility(View.VISIBLE);
        }
    }

    private List<Street> getDummyStreetList() {
        List<Street> streetList = null;
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Task taskFirst = realm.where(Task.class).equalTo("id", taskId).findFirst();
            if (taskFirst != null && taskFirst.getStreetList() != null) {
                streetList = realm.copyFromRealm(taskFirst.getStreetList());
            }
            return streetList;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void setupRecyclerView() {
        rvStreets.setHasFixedSize(true);
        rvStreets.setMotionEventSplittingEnabled(true);
        rvStreets.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        rvStreets.setLayoutManager(llm);
        rvStreets.setAdapter(mStreetAdapter);
    }

    @OnClick(R.id.iv_map)
    public void showMap() {
//        NotificationCenter.getInstance().notifyOpenPage(BuildingDetailsFragment.newInstance(-1, null));
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onStreetDetail(Street street, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(BuildingsFragment.newInstance(street.getId(), street.getDisplayName(getContext())));
    }
}