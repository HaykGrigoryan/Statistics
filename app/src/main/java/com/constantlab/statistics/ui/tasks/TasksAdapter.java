package com.constantlab.statistics.ui.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.ui.customviews.TaskView;

import java.util.List;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskHolder> {

    private List<Task> mTaskList;
    private InteractionListener interactionListener;

    TasksAdapter() {

    }

    void setTaskList(List<Task> taskList) {
        this.mTaskList = taskList;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mTaskList != null) {
            mTaskList.clear();
            notifyDataSetChanged();
        }
    }


    void setInteractionListener(InteractionListener listener) {
        interactionListener = listener;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskView taskView = new TaskView(parent.getContext());
        TaskHolder holder = new TaskHolder(taskView);
        holder.itemView.setOnClickListener(view -> {
            if (interactionListener != null) {
                interactionListener.onTaskSelected(mTaskList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        return holder;
    }

    boolean isEmpty() {
        return mTaskList == null || mTaskList.size() == 0;
    }


    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        TaskView taskView = (TaskView) holder.itemView;
        Task task = mTaskList.get(position);
        taskView.setData(task);
    }

    @Override
    public int getItemCount() {
        return mTaskList != null ? mTaskList.size() : 0;
    }

    public interface InteractionListener {
        void onTaskSelected(Task task, int position);
    }

    static class TaskHolder extends RecyclerView.ViewHolder {
        TaskHolder(TaskView itemView) {
            super(itemView);
        }
    }
}
 