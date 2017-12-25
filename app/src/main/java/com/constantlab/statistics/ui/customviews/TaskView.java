package com.constantlab.statistics.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.models.Task;

import java.util.Locale;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class TaskView extends FrameLayout {

    private static final String FORMAT = "%d";
    TextView tvTaskName;
    TextView tvTotalBuildings;
    TextView tvTotalApartments;
    TextView tvTotalResidents;

    public TaskView(Context context) {
        super(context);
        init();
    }


    public TaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_task, this);
        tvTaskName = findViewById(R.id.tv_task_name);
//        tvTotalBuildings = findViewById(R.id.tv_total_buildings);
//        tvTotalApartments = findViewById(R.id.tv_total_rooms);
//        tvTotalResidents = findViewById(R.id.tv_total_residents);
    }

    public void setData(Task task) {
        tvTaskName.setText(task.getTaskName());
//        tvTotalResidents.setText(String.format(Locale.getDefault(), FORMAT, task.getTotalResidents()));
//        tvTotalApartments.setText(String.format(Locale.getDefault(), FORMAT, task.getTotalApartments()));
//        tvTotalBuildings.setText(String.format(Locale.getDefault(), FORMAT, task.getTotalBuildings()));
    }

}
