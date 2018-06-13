package com.constantlab.statistics.background;

import com.constantlab.statistics.R;
import com.constantlab.statistics.background.receivers.SyncResultReceiver;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.utils.NotificationCenter;

import java.lang.ref.WeakReference;

/**
 * Created by Hayk on 25/04/2018.
 */

public class SyncDataResultReceiver implements SyncResultReceiver.ResultReceiverCallBack<String> {
    private WeakReference<MainActivity> activityRef;

    public SyncDataResultReceiver(MainActivity activity) {
        activityRef = new WeakReference<MainActivity>(activity);
    }

    @Override
    public void onSuccess(String message) {
        if (activityRef != null && activityRef.get() != null) {
            if (message != null) {
                activityRef.get().showMessage(message);
            } else {
                activityRef.get().showMessage(activityRef.get().getString(R.string.message_success_sync_from_server));
            }
            NotificationCenter.getInstance().notifyOnSyncFromServer();
        }
    }

    @Override
    public void onError(Exception exception) {
        if (activityRef != null && activityRef.get() != null) {
            activityRef.get().showMessage(exception != null ? exception.getMessage() : "Error");
            NotificationCenter.getInstance().notifyOnSyncFromServer();
        }
    }
}