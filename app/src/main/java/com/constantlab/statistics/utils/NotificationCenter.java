package com.constantlab.statistics.utils;

import com.constantlab.statistics.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 25/12/2017.
 */

public class NotificationCenter {
    private static NotificationCenter _instance;

    private List<INavigation> mNavigations;
    private List<ISync> mSyncs;

    private NotificationCenter() {
        mNavigations = new ArrayList<>();
        mSyncs = new ArrayList<>();
    }

    public static NotificationCenter getInstance() {
        if (_instance == null)
            _instance = new NotificationCenter();
        return _instance;
    }

    public void addNavigationListener(INavigation navigation) {
        mNavigations.add(navigation);
    }

    public void removeNavigationListener(INavigation navigation) {
        mNavigations.remove(navigation);
    }

    public void notifyOpenPage(BaseFragment fragment) {
        for (INavigation navigation : mNavigations) {
            navigation.openPage(fragment);
        }
    }


    public void addSyncListener(ISync sync) {
        mSyncs.add(sync);
    }

    public void removeSyncListener(ISync sync) {
        mSyncs.remove(sync);
    }

    public void notifyOnSyncFromServer() {
        for (ISync sync : mSyncs) {
            sync.onSyncFromServer();
        }
    }

    public void notifyOnSyncToServer() {
        for (ISync sync : mSyncs) {
            sync.onSyncToServer();
        }
    }
}
