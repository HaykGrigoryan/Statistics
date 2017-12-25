package com.constantlab.statistics.ui.buildings;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.customviews.BuildingView;

import java.util.List;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class BuildingsAdapter extends RecyclerView.Adapter<BuildingsAdapter.BuildingHolder> {

    private List<Building> buildingList;
    private InteractionListener interactionListener;

    public BuildingsAdapter() {

    }


    public void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    public void setBuildingList(List<Building> buildingList) {
        this.buildingList = buildingList;
        notifyDataSetChanged();
    }

    @Override
    public BuildingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BuildingView view = new BuildingView(parent.getContext());
        BuildingHolder holder = new BuildingHolder(view);
        ((BuildingView) holder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditBuilding(buildingList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(view12 -> {
            if (interactionListener != null) {
                interactionListener.onBuildingDetail(buildingList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BuildingHolder holder, int position) {
        BuildingView view = (BuildingView) holder.itemView;
        Building building = buildingList.get(position);
        view.setData(building);
    }

    public void clear() {
        if (buildingList != null) {
            buildingList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return buildingList != null ? buildingList.size() : 0;
    }

    interface InteractionListener {
        void onEditBuilding(Building building, int adapterPosition);

        void onBuildingDetail(Building building, int adapterPosition);
    }

    static class BuildingHolder extends RecyclerView.ViewHolder {

        BuildingHolder(BuildingView itemView) {
            super(itemView);
        }
    }
}
