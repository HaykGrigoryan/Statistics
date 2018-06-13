package com.constantlab.statistics.ui.street;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.buildings.BuildingsFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by Hayk on 26/12/2017.
 */

public class StreetFragment extends BaseFragment implements StreetRecyclerViewAdapter.InteractionListener {
    Integer taskId;
    Integer userId;
    String taskName;
    @BindView(R.id.rv_streets)
    RecyclerView rvStreets;
    @BindView(R.id.pb_streets)
    ProgressBar pbStreets;
    @BindView(R.id.tv_no_streets)
    TextView tvNoStreets;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.sort_order)
    AppCompatImageView imSortOrder;
    private int mSortOrder = 0;
    private StreetRecyclerViewAdapter adapter;
    private Realm realm;

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
            userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streets, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        setUpRecyclerView();
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
//                mStreetAdapter.getFilter().filter(editable.toString());
                adapter.getFilter().filter(editable.toString());
            }
        });
    }

    @OnClick(R.id.sort_order)
    protected void updateSortOrder() {
        mSortOrder = (mSortOrder + 1) % 2;
        imSortOrder.setImageResource(mSortOrder == 0 ? R.drawable.sort_asc : R.drawable.sort_desc);
        adapter.setSortOrder(mSortOrder);
    }

    private void setUpRecyclerView() {
        OrderedRealmCollection<Street> streets = realm.where(Street.class).equalTo("task_id", taskId).equalTo("user_id", userId).sort("name", Sort.ASCENDING).findAll();
        adapter = new StreetRecyclerViewAdapter(streets, realm, taskId, userId);
        adapter.setInteractionListener(this);
        adapter.setSortOrder(mSortOrder);
        rvStreets.setHasFixedSize(true);
        rvStreets.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStreets.getContext(),
                llm.getOrientation());
        rvStreets.addItemDecoration(dividerItemDecoration);
        rvStreets.setLayoutManager(llm);
        rvStreets.setAdapter(adapter);
        rvStreets.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvStreets.setAdapter(null);
        realm.close();
    }


//    @OnClick(R.id.iv_map)
//    public void showMap() {
//        Intent intent = new Intent(getContext(), MapActivity.class);
//        intent.putExtra(ConstKeys.KEY_MAP_ACTION, OSMMapFragment.MapAction.SHOW_POLYGON.ordinal());
//        intent.putExtra(ConstKeys.KEY_TASK_ID, taskId);
//        startActivity(intent);
//    }

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
