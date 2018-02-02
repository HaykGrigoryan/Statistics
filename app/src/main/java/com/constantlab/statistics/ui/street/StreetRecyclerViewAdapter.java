package com.constantlab.statistics.ui.street;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.ui.customviews.StreetView;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class StreetRecyclerViewAdapter extends RealmRecyclerViewAdapter<Street, StreetRecyclerViewAdapter.StreetHolder> implements Filterable {
    private InteractionListener interactionListener;
    private int mSortOrder;
    private Realm realm;
    private Integer taskId;

    StreetRecyclerViewAdapter(OrderedRealmCollection<Street> data, Realm realm, Integer taskId) {
        super(data, true);
        this.realm = realm;
        this.taskId = taskId;
        setHasStableIds(true);
    }
    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
        updateData(getData().sort("name", mSortOrder == 0?Sort.ASCENDING:Sort.DESCENDING));
//
//        notifyDataSetChanged();
    }

    public void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    @Override
    public StreetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.view_street, parent, false);
//        return new StreetHolder(itemView);
        StreetView view = new StreetView(parent.getContext());
        StreetHolder holder = new StreetHolder(view);
        ((StreetView) holder.itemView).setEditButtonListener(view1 -> {
            if (interactionListener != null) {
                interactionListener.onEditStreet(getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(view12 -> {
            if (interactionListener != null) {
                interactionListener.onStreetDetail(getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(StreetHolder holder, int position) {
//        final Street obj = getItem(position);
//        holder.data = obj;
//        holder.streetName.setText(obj.getName());
//        holder.buidingsCount
//                .setText(String.format(Locale.getDefault(), FORMAT, obj.getBuidingsCount()));
//        holder.apartmentsCount
//                .setText(String.format(Locale.getDefault(), FORMAT, obj.getApartmentCount()));
//        holder.residentsCount
//                .setText(String.format(Locale.getDefault(), FORMAT, obj.getResidentsCount()));
//        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (interactionListener != null) {
//                    interactionListener.onEditStreet(getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
//                }
//            }
//        });
        StreetView view = (StreetView) holder.itemView;
        Street street = getItem(position);
        view.setData(street);
    }

    @Override
    public Filter getFilter() {
        return new StreetFilter(this);
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        RealmQuery<Street> query = realm.where(Street.class).equalTo("task_id", taskId).sort("name",mSortOrder==0?Sort.ASCENDING:Sort.DESCENDING);

        if(!(text == null || "".equals(text))) {
            query.contains("nameLowerCase", text, Case.INSENSITIVE);
        }
        updateData(query.findAll());
    }

    private class StreetFilter
            extends Filter {
        private final StreetRecyclerViewAdapter adapter;

        private StreetFilter(StreetRecyclerViewAdapter adapter) {
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

    interface InteractionListener {
        void onStreetDetail(Street street, int adapterPosition);

        void onEditStreet(Street street, int adapterPosition);
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getLocalId();
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    static class StreetHolder extends RecyclerView.ViewHolder {
//        TextView streetName, buidingsCount, apartmentsCount, residentsCount;
//        AppCompatImageView btnEdit;
//        Street data;
//        StreetHolder(View view) {
//            super(view);
//            streetName = view.findViewById(R.id.tv_street_name);
//            buidingsCount = view.findViewById(R.id.tv_buildings);
//            apartmentsCount = view.findViewById(R.id.tv_apartments);
//            residentsCount = view.findViewById(R.id.tv_total_residents);
//            btnEdit = view.findViewById(R.id.btn_edit);
//        }
        StreetHolder(StreetView itemView) {
    super(itemView);
}
    }
}