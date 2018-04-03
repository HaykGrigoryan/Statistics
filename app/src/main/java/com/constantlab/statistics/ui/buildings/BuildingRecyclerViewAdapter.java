package com.constantlab.statistics.ui.buildings;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.customviews.BuildingView;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by Sunny Kinger on 05-12-2017.
 */

public class BuildingRecyclerViewAdapter extends RealmRecyclerViewAdapter<Building, BuildingRecyclerViewAdapter.BuildingHolder> implements Filterable {
    private int mSortOrder;
    private Realm realm;
    private Integer userId;
    private Integer taskId;
    private Integer streetId;
//    private List<Building> buildingList;
    private InteractionListener interactionListener;

    public BuildingRecyclerViewAdapter(OrderedRealmCollection<Building> data, Realm realm, Integer taskId, Integer streetId, Integer userId) {
        super(data, true);
        this.realm = realm;
        this.taskId = taskId;
        this.streetId = streetId;
        this.userId = userId;
        setHasStableIds(true);
    }


    public void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
        updateData(getData().sort("houseNumber", mSortOrder == 0 ? Sort.ASCENDING : Sort.DESCENDING));
//
//        notifyDataSetChanged();
    }

//    public void addBuildingItems(List<Building> buildingList) {
//        this.buildingList.addAll(new ArrayList<Building>(buildingList));
//        notifyDataSetChanged();
//    }
//
//    public void setBuildingList(List<Building> buildingList) {
//        this.buildingList = buildingList;
//        notifyDataSetChanged();
//    }

    @Override
    public BuildingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BuildingView view = new BuildingView(parent.getContext());
        BuildingHolder holder = new BuildingHolder(view);
        ((BuildingView) holder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditBuilding(getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(view12 -> {
            if (interactionListener != null) {
                interactionListener.onBuildingDetail(getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BuildingHolder holder, int position) {
        BuildingView view = (BuildingView) holder.itemView;
        Building building = getItem(position);
        view.setData(building);
    }

    @Override
    public Filter getFilter() {
        return new BuildingRecyclerViewAdapter.BuildingFilter(this);
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        RealmQuery<Building> query = realm.where(Building.class).equalTo("task_id", taskId).equalTo("user_id", userId).equalTo("street_id", streetId).sort("houseNumber", mSortOrder == 0 ? Sort.ASCENDING : Sort.DESCENDING);

        if (!(text == null || "".equals(text))) {
            query.contains("houseNumberLowerCase", text, Case.INSENSITIVE);
        }
        updateData(query.findAll());
    }

    private class BuildingFilter
            extends Filter {
        private final BuildingRecyclerViewAdapter adapter;

        private BuildingFilter(BuildingRecyclerViewAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }
    }

//    public void clear() {
//        if (buildingList != null) {
//            buildingList.clear();
//            notifyDataSetChanged();
//        }
//    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getLocalId();
    }

    @Override
    public int getItemCount() {
        return getData().size();
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
