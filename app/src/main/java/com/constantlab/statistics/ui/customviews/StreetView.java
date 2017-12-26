package com.constantlab.statistics.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.Street;

import java.util.Locale;

/**
 * Created by Hayk on 26/12/2017.
 */

public class StreetView extends RelativeLayout {
    private static final String FORMAT = "%d";
    private static final String ZERO = "0";
    TextView streetName, buidingsCount, apartmentsCount, residentsCount;
    AppCompatImageView btnEdit;

    public StreetView(Context context) {
        super(context);
        init();
    }

    public StreetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StreetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StreetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.view_street, this);
        streetName = findViewById(R.id.tv_street_name);
        buidingsCount = findViewById(R.id.tv_buildings);
        apartmentsCount = findViewById(R.id.tv_apartments);
        residentsCount = findViewById(R.id.tv_total_residents);
    }

    public void setData(Street street) {
        streetName.setText(getContext().getString(R.string.label_street_short) + " " + street.getName());
        buidingsCount
                .setText(String.format(Locale.getDefault(), FORMAT, street.getBuidingsCount()));
        apartmentsCount
                .setText(String.format(Locale.getDefault(), FORMAT, street.getApartmentCount()));
        residentsCount
                .setText(String.format(Locale.getDefault(), FORMAT, street.getResidentsCount()));

    }
}