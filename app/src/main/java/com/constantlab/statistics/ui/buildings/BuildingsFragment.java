package com.constantlab.statistics.ui.buildings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
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
import com.constantlab.statistics.ui.EndlessRecyclerViewScrollListener;
import com.constantlab.statistics.ui.apartments.ApartmentFragment;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class BuildingsFragment extends BaseFragment implements BuildingRecyclerViewAdapter.InteractionListener {

    private static final int REQUEST_ADD_BUILDING = 23;
    private static final int REQUEST_EDIT_BUILDING = 24;
    private static final int REQUEST_APARTMENTS = 34;

    private final int ITEMS_PER_PAGE = 10;
    private int page = 0;

    Integer streetId;
    Integer taskId;
    Integer userId;
    String streetName;
    @BindView(R.id.rv_buildings)
    RecyclerView rvBuildings;
    @BindView(R.id.pb_buildings)
    ProgressBar pbBuildings;
    @BindView(R.id.tv_no_buildings)
    TextView tvNoBuildings;
    @BindView(R.id.iv_add)
    ImageView add;

    @BindView(R.id.title)
    TextView mToolbarTitle;


    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.sort_order)
    AppCompatImageView imSortOrder;
    private int mSortOrder = 0;

    private BuildingRecyclerViewAdapter mBuildingRecyclerViewAdapter;
    private Realm realm;

    public static BuildingsFragment newInstance(Integer streetId, String streetName, int taskId) {
        BuildingsFragment fragment = new BuildingsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_STREET, streetId);
        args.putInt(ConstKeys.TAG_TASK, taskId);
        args.putString(ConstKeys.TAG_STREET_NAME, streetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            streetId = getArguments().getInt(ConstKeys.TAG_STREET);
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
            streetName = getArguments().getString(ConstKeys.TAG_STREET_NAME);
            userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buildings, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        setupRecyclerView();
        return view;
    }

    @OnClick(R.id.sort_order)
    protected void updateSortOrder() {
        mSortOrder = (mSortOrder + 1) % 2;
        imSortOrder.setImageResource(mSortOrder == 0 ? R.drawable.sort_asc : R.drawable.sort_desc);
        mBuildingRecyclerViewAdapter.setSortOrder(mSortOrder);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (streetName != null) {
            mToolbarTitle.setText(streetName);
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
//                mStreetAdapter.getFilter().filter(editable.toString());
                mBuildingRecyclerViewAdapter.getFilter().filter(editable.toString());
            }
        });
    }

    private void setupRecyclerView() {
        OrderedRealmCollection<Building> buildings = realm.where(Building.class).equalTo("task_id", taskId).equalTo("street_id", streetId).equalTo("user_id", userId).findAll();
        mBuildingRecyclerViewAdapter = new BuildingRecyclerViewAdapter(buildings, realm, taskId, streetId, userId);
        mBuildingRecyclerViewAdapter.setInteractionListener(this);
        mBuildingRecyclerViewAdapter.setSortOrder(mSortOrder);
        rvBuildings.setHasFixedSize(true);
        rvBuildings.setMotionEventSplittingEnabled(true);
        rvBuildings.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBuildings.getContext(),
                llm.getOrientation());
        rvBuildings.addItemDecoration(dividerItemDecoration);
        rvBuildings.setLayoutManager(llm);
        rvBuildings.setAdapter(mBuildingRecyclerViewAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvBuildings.setAdapter(null);
        realm.close();
    }

    @Override
    public void onEditBuilding(Building building, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(BuildingDetailsFragment.newInstance(building.getId(), building.getDisplayAddress(userId), streetId, taskId));
    }

    @Override
    public void onBuildingDetail(Building building, int adapterPosition) {
        if (Building.isFlatLevelEnabled(building.getBuildingType(), building.getBuildingStatus())) {// !Building.isStatusInactive(building.getBuildingStatus()) && !Building.isTypeSpetialInstitution(building.getBuildingType()) && !Building.isTypeClosedInstitution(building.getBuildingType())) {
            NotificationCenter.getInstance().notifyOpenPage(ApartmentFragment.newInstance(building.getId(), building.getDisplayAddress(userId), building.getTaskId()));
        }
    }

    @OnClick(R.id.iv_add)
    public void addBuilding() {
        NotificationCenter.getInstance().notifyOpenPage(BuildingDetailsFragment.newInstance(-1, null, streetId, taskId));
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ADD_BUILDING && resultCode == Activity.RESULT_OK) {
//            mBuildingRecyclerViewAdapter.clear();
//            showData();
//            Integer buildingId = data.getExtras().getInt(ConstKeys.TAG_BUILDING);
//            Intent intent = new Intent(getContext(), ApartmentActivity.class);
//            intent.putExtra(ConstKeys.TAG_BUILDING, buildingId);
//            intent.putExtra(ConstKeys.TAG_ACTION, Actions.VIEW_APARTMENTS);
//            startActivityForResult(intent, REQUEST_APARTMENTS);
//            getActivity().setResult(Activity.RESULT_OK);
//        } else if (requestCode == REQUEST_EDIT_BUILDING && resultCode == Activity.RESULT_OK) {
//            mBuildingRecyclerViewAdapter.clear();
//            showData();
//            getActivity().setResult(Activity.RESULT_OK);
//        } else if (requestCode == REQUEST_APARTMENTS && resultCode == Activity.RESULT_OK) {
//            mBuildingRecyclerViewAdapter.clear();
//            showData();
//            getActivity().setResult(Activity.RESULT_OK);
//        }
//    }
}
