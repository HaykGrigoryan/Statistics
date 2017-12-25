package com.constantlab.statistics.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Apartment;

import java.util.Locale;

/**
 * Created by Sunny Kinger on 18-12-2017.
 */

public class ApartmentView extends RelativeLayout {
    private static final String FORMAT = "%d";
    private static final String ZERO = "0";
    TextView tvApartmentNumber, tvTotalRooms, tvResidents;
    Button btnEdit;

    public ApartmentView(Context context) {
        super(context);
        init();
    }

    public ApartmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ApartmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ApartmentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_apartment, this);
        tvApartmentNumber = findViewById(R.id.tv_apartment_no);
        tvTotalRooms = findViewById(R.id.tv_total_rooms);
        tvResidents = findViewById(R.id.tv_total_residents);
//        tvArea = findViewById(R.id.tv_area);
        btnEdit = findViewById(R.id.btn_edit);
    }

    public void setData(Apartment apartment) {
        tvApartmentNumber.setText(apartment.getApartmentNumber());
//        tvArea.setText(String.format(Locale.getDefault(), FORMAT, apartment.getAreaSquare()));
        tvTotalRooms.setText(String.format(Locale.getDefault(), FORMAT, apartment.getTotalRooms()));
        if (apartment.getTotalInhabitants() != null)
            tvResidents.setText(String.format(Locale.getDefault(), FORMAT, apartment.getTotalInhabitants()));
        else
            tvResidents.setText(ZERO);
    }

    public void setEditButtonListener(View.OnClickListener listener) {
        btnEdit.setOnClickListener(listener);
    }
}
