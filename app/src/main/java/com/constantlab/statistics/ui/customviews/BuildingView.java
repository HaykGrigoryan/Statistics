package com.constantlab.statistics.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;

import java.util.Locale;

/**
 * Created by Sunny Kinger on 06-12-2017.
 */

public class BuildingView extends RelativeLayout {
    private static final String FORMAT = "%d";
    private static final String ZERO = "0";
    TextView buildingName, apartmentsCount, residentsCount;
    AppCompatImageView btnEdit;

    public BuildingView(Context context) {
        super(context);
        init();
    }

    public BuildingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BuildingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BuildingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.view_building, this);
        buildingName = findViewById(R.id.tv_apartment_no);
        apartmentsCount = findViewById(R.id.tv_total_rooms);
        residentsCount = findViewById(R.id.tv_total_residents);
        btnEdit = findViewById(R.id.btn_edit);
    }

    public void setData(Building building) {
        buildingName.setText(building.getHouseNumber());//building.getUserId()
        apartmentsCount
                .setText(String.format(Locale.getDefault(), FORMAT, building.getApartmentCount()));//building.getApartmentCount(building.getUserId()) != null ? String.format(Locale.getDefault(), FORMAT, building.getApartmentCount(building.getUserId())) : ZERO
        int residents = 0;
        if (Building.isFlatLevelEnabled(building.getBuildingType(), building.getBuildingStatus())) {
            residents = building.getResidentsCount();// building.getApartmentInhabitantsCount(building.getUserId());
        } else {
            residents = building.getTemporaryInhabitants();
        }


        if (residents == 0) {
            residentsCount.setText(ZERO);
        } else {
            residentsCount.setText(String.format(Locale.getDefault(), FORMAT, residents));
        }

        btnEdit.setImageResource(building.isEdited() ? R.drawable.ic_edit_green : R.drawable.ic_edit);
    }

    public void setEditButtonListener(View.OnClickListener listener) {
        btnEdit.setOnClickListener(listener);
    }
}
