package com.constantlab.statistics.ui.apartments;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.ui.customviews.ApartmentView;

import java.util.List;

/**
 * Created by Sunny Kinger on 18-12-2017.
 */

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ApartmentHolder> {

    private List<Apartment> apartmentList;
    private InteractionListener interactionListener;


    public void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    public void setApartmentList(List<Apartment> apartmentList) {
        this.apartmentList = apartmentList;
        notifyDataSetChanged();
    }

    @Override
    public ApartmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApartmentView view = new ApartmentView(parent.getContext());
        ApartmentHolder apartmentHolder = new ApartmentHolder(view);
        ((ApartmentView) apartmentHolder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditApartment(apartmentList.get(apartmentHolder.getAdapterPosition()), apartmentHolder.getAdapterPosition());
            }
        });
        return apartmentHolder;
    }

    @Override
    public void onBindViewHolder(ApartmentHolder holder, int position) {
        Apartment apartment = apartmentList.get(position);
        ApartmentView apartmentView = (ApartmentView) holder.itemView;
        apartmentView.setData(apartment);
    }

    public void clear() {
        if (apartmentList != null) {
            apartmentList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return apartmentList != null ? apartmentList.size() : 0;
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
