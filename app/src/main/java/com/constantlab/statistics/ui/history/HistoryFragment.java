package com.constantlab.statistics.ui.history;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hayk on 27/12/2017.
 */

public class HistoryFragment extends BaseFragment {
    Integer taskId;
    String taskName;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.pb_history)
    ProgressBar pbHistory;
    @BindView(R.id.tv_no_history)
    TextView tvNoHistory;

    @BindView(R.id.title)
    TextView mToolbarTitle;

    private HistoryAdapter mHistoryAdapter;

    public static HistoryFragment newInstance(Integer streetId, String streetName) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_TASK, streetId);
        args.putString(ConstKeys.TAG_TASK_NAME, streetName);
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
        mHistoryAdapter = new HistoryAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
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
        List<History> historyList = getDummyHistoryList();
        if (historyList != null && historyList.size() > 0) {
            mHistoryAdapter.setHistoryList(historyList);
        } else {
            tvNoHistory.setVisibility(View.VISIBLE);
        }
    }

    private List<History> getDummyHistoryList() {
        List<History> historyList = new ArrayList<>();
        History history = new History();
        history.setTitle("Добавлено здание");
        history.setMessage("улиц Иманова");


        historyList.add(history);
        return historyList;
    }

    private void setupRecyclerView() {
        rvHistory.setHasFixedSize(true);
        rvHistory.setMotionEventSplittingEnabled(true);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setSmoothScrollbarEnabled(true);
        rvHistory.setLayoutManager(llm);
        rvHistory.setAdapter(mHistoryAdapter);
    }


    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
