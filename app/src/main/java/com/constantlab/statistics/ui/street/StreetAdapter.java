package com.constantlab.statistics.ui.street;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.ui.customviews.BuildingView;
import com.constantlab.statistics.ui.customviews.StreetView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hayk on 26/12/2017.
 */

public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.StreetHolder> implements Filterable {

    private List<Street> streetList;
    private List<Street> streetFilteredList;
    private StreetAdapter.InteractionListener interactionListener;
    private int mSortOrder;
    private Context mContext;

    public StreetAdapter() {

    }

    public StreetAdapter(Context context) {
        mContext = context;
    }

    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
        Collections.sort(streetFilteredList, mSortOrder == 0 ? Street.getAscComparator() : Street.getDescComparator());
        notifyDataSetChanged();
    }


    public void setInteractionListener(StreetAdapter.InteractionListener listener) {
        interactionListener = listener;
    }

    public void setStreetList(List<Street> streetList) {
        this.streetList = streetList;
        this.streetFilteredList = streetList;
        notifyDataSetChanged();
    }

    @Override
    public StreetAdapter.StreetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StreetView view = new StreetView(parent.getContext());
        StreetAdapter.StreetHolder holder = new StreetAdapter.StreetHolder(view);
        ((StreetView) holder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditStreet(streetFilteredList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(view12 -> {
            if (interactionListener != null) {
                interactionListener.onStreetDetail(streetFilteredList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(StreetAdapter.StreetHolder holder, int position) {
        StreetView view = (StreetView) holder.itemView;
        Street street = streetFilteredList.get(position);
        view.setData(street);
    }

    public void clear() {
        if (streetList != null) {
            streetList.clear();
            streetFilteredList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return streetFilteredList != null ? streetFilteredList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new StreetFilter();
    }

    interface InteractionListener {
        void onStreetDetail(Street street, int adapterPosition);

        void onEditStreet(Street street, int adapterPosition);
    }

    static class StreetHolder extends RecyclerView.ViewHolder {

        StreetHolder(StreetView itemView) {
            super(itemView);
        }
    }

    private class StreetFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Street> filterList = new ArrayList<Street>();
                for (int i = 0; i < streetList.size(); i++) {
                    if ((streetList.get(i).getDisplayName(mContext).toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        filterList.add(streetList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = streetList.size();
                results.values = streetList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            streetFilteredList = (ArrayList<Street>) results.values;
            notifyDataSetChanged();
        }

    }
}
