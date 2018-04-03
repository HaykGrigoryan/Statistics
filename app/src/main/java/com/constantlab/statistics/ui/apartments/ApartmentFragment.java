package com.constantlab.statistics.ui.apartments;

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
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.buildings.BuildingRecyclerViewAdapter;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;

/**
 * Created by Sunny Kinger on 13-12-2017.
 */

public class ApartmentFragment extends BaseFragment implements ApartmentRecyclerViewAdapter.InteractionListener {

    private static final int REQUEST_ADD_APARTMENT = 89;
    private static final int REQUEST_EDIT_APARTMENT = 90;
    Integer buildingId;
    Integer taskId;
    Integer userId;
    String buildingName;
    @BindView(R.id.rv_apartments)
    RecyclerView rvApartments;
    @BindView(R.id.pb_apartments)
    ProgressBar pbApartments;
    @BindView(R.id.tv_no_apartments)
    TextView tvNoApartments;
    @BindView(R.id.iv_add)
    ImageView add;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.sort_order)
    AppCompatImageView imSortOrder;
    private int mSortOrder = 0;

    private Realm realm;

    private ApartmentRecyclerViewAdapter mApartmentRecyclerViewAdapter;


    public static ApartmentFragment newInstance(Integer buildingId, String buildingName, int taskId) {
        ApartmentFragment fragment = new ApartmentFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_BUILDING, buildingId);
        args.putInt(ConstKeys.TAG_TASK, taskId);
        args.putString(ConstKeys.TAG_BUILDING_NAME, buildingName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getInt(ConstKeys.TAG_BUILDING);
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
            buildingName = getArguments().getString(ConstKeys.TAG_BUILDING_NAME);
            userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        setupRecyclerView();
        return view;
    }

    @OnClick(R.id.sort_order)
    protected void updateSortOrder() {
        mSortOrder = (mSortOrder + 1) % 2;
        imSortOrder.setImageResource(mSortOrder == 0 ? R.drawable.sort_asc : R.drawable.sort_desc);
        mApartmentRecyclerViewAdapter.setSortOrder(mSortOrder);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (buildingName != null) {
            mToolbarTitle.setText(buildingName);
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
                mApartmentRecyclerViewAdapter.getFilter().filter(editable.toString());
            }
        });
    }

//    private void showDummyData() {
//        List<Apartment> apartmentList = getDummyList();
//        if (apartmentList != null && apartmentList.size() > 0) {
//            tvNoApartments.setVisibility(View.INVISIBLE);
//            mApartmentRecyclerViewAdapter.setApartmentList(apartmentList);
//            mApartmentRecyclerViewAdapter.setInteractionListener(this);
//        } else {
//            tvNoApartments.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private List<Apartment> getDummyList() {
//        List<Apartment> apartmentList = null;
//        Realm realm = null;
//        try {
//            realm = Realm.getDefaultInstance();
//            apartmentList = realm.copyFromRealm(realm.where(Apartment.class).equalTo("building_id", buildingId).equalTo("task_id", taskId).equalTo("user_id", userId).sort("apartmentNumber").findAll());
//            return apartmentList;
//        } finally {
//            if (realm != null)
//                realm.close();
//        }
//    }

    private void setupRecyclerView() {
        OrderedRealmCollection<Apartment> apartments = realm.where(Apartment.class).equalTo("task_id", taskId).equalTo("building_id", buildingId).equalTo("user_id", userId).findAll();
        mApartmentRecyclerViewAdapter = new ApartmentRecyclerViewAdapter(apartments, realm, taskId, buildingId, userId);
        mApartmentRecyclerViewAdapter.setInteractionListener(this);
        mApartmentRecyclerViewAdapter.setSortOrder(mSortOrder);
        rvApartments.setHasFixedSize(true);
        rvApartments.setMotionEventSplittingEnabled(true);
        rvApartments.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        rvApartments.setLayoutManager(llm);
        rvApartments.setAdapter(mApartmentRecyclerViewAdapter);

//        rvApartments.setHasFixedSize(true);
//        rvApartments.setMotionEventSplittingEnabled(true);
//        rvApartments.setItemAnimator(new DefaultItemAnimator());
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        llm.setSmoothScrollbarEnabled(true);
//        rvApartments.setLayoutManager(llm);
//        rvApartments.setAdapter(mApartmentRecyclerViewAdapter);
    }


    @OnClick(R.id.iv_add)
    public void addApartment() {
        NotificationCenter.getInstance().notifyOpenPage(ApartmentDetailsFragment.newInstance(-1, null, buildingId, taskId));

    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onEditApartment(Apartment apartment, int adapterPosition) {
        NotificationCenter.getInstance().notifyOpenPage(ApartmentDetailsFragment.newInstance(apartment.getId(), apartment.getDisplayName(getContext()), buildingId, taskId));
    }
}
