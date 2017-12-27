package com.constantlab.statistics.ui.history;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.ui.buildings.BuildingsAdapter;
import com.constantlab.statistics.ui.customviews.BuildingView;
import com.constantlab.statistics.ui.customviews.HistoryView;

import java.util.List;

/**
 * Created by Hayk on 27/12/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private List<History> historyList;

    public HistoryAdapter() {

    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryView view = new HistoryView(parent.getContext());
        HistoryHolder holder = new HistoryHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        HistoryView view = (HistoryView) holder.itemView;
        History history = historyList.get(position);
        view.setData(history);
    }

    public void clear() {
        if (historyList != null) {
            historyList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    static class HistoryHolder extends RecyclerView.ViewHolder {

        HistoryHolder(HistoryView itemView) {
            super(itemView);
        }
    }
}
