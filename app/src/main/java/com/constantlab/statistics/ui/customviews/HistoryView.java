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
import com.constantlab.statistics.models.History;

import java.util.Locale;

/**
 * Created by Hayk on 27/12/2017.
 */

public class HistoryView extends RelativeLayout {
    private static final String FORMAT = "%d";
    private static final String ZERO = "0";
    TextView historyName, historyMessage;

    public HistoryView(Context context) {
        super(context);
        init();
    }

    public HistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.view_history, this);
        historyName = findViewById(R.id.tv_history_name);
        historyMessage = findViewById(R.id.tv_history_message);
    }

    public void setData(History history) {
        historyName.setText(history.getTitle());
        historyMessage.setText(history.getMessage());
    }
}
