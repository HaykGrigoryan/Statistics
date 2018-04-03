package com.constantlab.statistics.ui.apartments;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.ui.buildings.BuildingRecyclerViewAdapter;
import com.constantlab.statistics.ui.customviews.ApartmentView;

import java.util.List;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by Sunny Kinger on 18-12-2017.
 */

public class ApartmentRecyclerViewAdapter extends RealmRecyclerViewAdapter<Apartment, ApartmentRecyclerViewAdapter.ApartmentHolder> implements Filterable {

    private int mSortOrder;
    private Realm realm;
    private Integer userId;
    private Integer taskId;
    private Integer buildingId;
    //    private List<Building> buildingList;
    private InteractionListener interactionListener;

    public ApartmentRecyclerViewAdapter(OrderedRealmCollection<Apartment> data, Realm realm, Integer taskId, Integer buildingId, Integer userId) {
        super(data, true);
        this.realm = realm;
        this.taskId = taskId;
        this.buildingId = buildingId;
        this.userId = userId;
        setHasStableIds(true);
    }

    public void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
        updateData(getData().sort("apartmentNumber", mSortOrder == 0 ? Sort.ASCENDING : Sort.DESCENDING));
    }

    @Override
    public ApartmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApartmentView view = new ApartmentView(parent.getContext());
        ApartmentHolder apartmentHolder = new ApartmentHolder(view);
        ((ApartmentView) apartmentHolder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditApartment(getItem(apartmentHolder.getAdapterPosition()), apartmentHolder.getAdapterPosition());
            }
        });
        return apartmentHolder;
    }

    @Override
    public void onBindViewHolder(ApartmentHolder holder, int position) {
        Apartment apartment = getItem(position);
        ApartmentView apartmentView = (ApartmentView) holder.itemView;
        apartmentView.setData(apartment);
    }


    @Override
    public Filter getFilter() {
        return new ApartmentRecyclerViewAdapter.ApartmentFilter(this);
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        RealmQuery<Apartment> query = realm.where(Apartment.class).equalTo("task_id", taskId).equalTo("user_id", userId).equalTo("building_id", buildingId).sort("apartmentNumber", mSortOrder == 0 ? Sort.ASCENDING : Sort.DESCENDING);

        if (!(text == null || "".equals(text))) {
            query.contains("apartmentNumberLowerCase", text, Case.INSENSITIVE);
        }
        updateData(query.findAll());
    }

    private class ApartmentFilter
            extends Filter {
        private final ApartmentRecyclerViewAdapter adapter;

        private ApartmentFilter(ApartmentRecyclerViewAdapter adapter) {
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

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getLocalId();
    }

    interface InteractionListener {
        void onEditApartment(Apartment apartment, int adapterPosition);
    }

    static class ApartmentHolder extends RecyclerView.ViewHolder {

        ApartmentHolder(ApartmentView itemView) {
            super(itemView);
        }
    }
}
