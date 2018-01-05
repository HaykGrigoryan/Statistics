package com.constantlab.statistics.ui.street;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.ui.customviews.BuildingView;
import com.constantlab.statistics.ui.customviews.StreetView;

import java.util.List;

/**
 * Created by Hayk on 26/12/2017.
 */

public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.StreetHolder> {

    private List<Street> streetList;
    private StreetAdapter.InteractionListener interactionListener;

    public StreetAdapter() {

    }


    public void setInteractionListener(StreetAdapter.InteractionListener listener) {
        interactionListener = listener;
    }

    public void setStreetList(List<Street> streetList) {
        this.streetList = streetList;
        notifyDataSetChanged();
    }

    @Override
    public StreetAdapter.StreetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StreetView view = new StreetView(parent.getContext());
        StreetAdapter.StreetHolder holder = new StreetAdapter.StreetHolder(view);
        ((StreetView) holder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditStreet(streetList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(view12 -> {
            if (interactionListener != null) {
                interactionListener.onStreetDetail(streetList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(StreetAdapter.StreetHolder holder, int position) {
        StreetView view = (StreetView) holder.itemView;
        Street street = streetList.get(position);
        view.setData(street);
    }

    public void clear() {
        if (streetList != null) {
            streetList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return streetList != null ? streetList.size() : 0;
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
}
