package com.constantlab.statistics.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
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
    TextView tvTaskName, tvTaskDate, tvTaskKato, tvTaskComment;
    AppCompatImageView btnHistory;

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
        tvTaskKato = findViewById(R.id.tv_task_date);
        tvTaskDate = findViewById(R.id.tv_task_kato);
        tvTaskComment = findViewById(R.id.tv_task_comment);
        btnHistory = findViewById(R.id.btn_history);
    }

    public void setData(Task task) {
        tvTaskName.setText(task.getDisplayName());
        tvTaskKato.setText(task.getDisplayKato(getContext()));
        tvTaskDate.setText(task.getDisplayDateBegin(getContext()));
        tvTaskComment.setText(task.getComment());
    }

    public void setHistoryButtonListener(View.OnClickListener listener) {
        btnHistory.setOnClickListener(listener);
    }

}
