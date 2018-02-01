package com.constantlab.statistics.ui.street;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.constantlab.statistics.ui.map.MapActivity;
import com.constantlab.statistics.ui.map.MapFragment;
import com.constantlab.statistics.utils.Actions;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;

import java.util.ArrayList;
import java.util.Collections;
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

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.sort_order)
    AppCompatImageView imSortOrder;
    private int mSortOrder = 0;
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
        mStreetAdapter = new StreetAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streets, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        showData();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (taskName != null) {
            mToolbarTitle.setText(taskName);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mStreetAdapter.getFilter().filter(editable.toString());
            }
        });
    }

    @OnClick(R.id.sort_order)
    protected void updateSortOrder() {
        mSortOrder = (mSortOrder + 1) % 2;
        imSortOrder.setImageResource(mSortOrder == 0 ? R.drawable.sort_asc : R.drawable.sort_desc);
        mStreetAdapter.setSortOrder(mSortOrder);
    }

    List<Street> mStreetList;

    private void showData() {
        mStreetList = getStreetList();
        if (mStreetList != null && mStreetList.size() > 0) {
            mStreetAdapter.setInteractionListener(this);
            mStreetAdapter.setStreetList(mStreetList);
            mStreetAdapter.setSortOrder(mSortOrder);
        } else {
            tvNoStreets.setVisibility(View.VISIBLE);
        }
    }

    private List<Street> getStreetList() {
        List<Street> streetList = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            streetList = realm.copyFromRealm(realm.where(Street.class).equalTo("task_id", taskId).findAll());
//            Task taskFirst = realm.where(Task.class).equalTo("task_id", taskId).findFirst();
//            if (taskFirst != null && taskFirst.getStreetList() != null) {
//                streetList = realm.copyFromRealm(taskFirst.getStreetList());
//            }
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
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra(ConstKeys.KEY_MAP_ACTION, MapFragment.MapAction.SHOW_POLYGON.ordinal());
        intent.putExtra(ConstKeys.KEY_TASK_ID, taskId);
        startActivity(intent);
    }

    @OnClick(R.id.iv_add)
    public void addStreet() {
        NotificationCenter.getInstance().notifyOpenPage(StreetDetailsFragment.newInstance(-1, null, taskId));
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onStreetDetail(Street street, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(BuildingsFragment.newInstance(street.getId(), street.getDisplayName(getContext()), street.getTaskId()));
    }

    @Override
    public void onEditStreet(Street street, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(StreetDetailsFragment.newInstance(street.getId(), street.getDisplayName(getContext()), taskId));
    }
}
