package com.constantlab.statistics.ui.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.constantlab.statistics.app.Statistics;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public abstract class BaseActivity extends AppCompatActivity {


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected Statistics statistics;

    /**
     * The need go to prev fragment.
     */
    protected boolean needGoToPrevFragment = false;
    /**
     * The prev fragment tag.
     */
    protected String prevFragmentTag = "";


    protected abstract @IdRes
    int getFragmentContainerId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statistics = (Statistics) getApplicationContext();
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (needGoToPrevFragment) {
            if (TextUtils.isEmpty(prevFragmentTag)) {
                goToPrevFragmentIfExist();
            } else {
                goToPrevFragment(prevFragmentTag);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (goToPrevFragmentIfExist()) return;
        finish();
        super.onBackPressed();

    }

    /**
     * Go to prev fragment.
     *
     * @param tag the tag
     */
    public void goToPrevFragment(String tag) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack(tag, 0);
            fm.executePendingTransactions();
            needGoToPrevFragment = false;
            prevFragmentTag = "";
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
            Log.e("exception", "IllegalStateException " + ignored);
            needGoToPrevFragment = true;
            prevFragmentTag = tag;
        }
    }

    /**
     * Go to prev fragment if exist boolean.
     *
     * @return true, if prev fragment exist and went to prev, false, if prev fragment not exist and stay in present state
     */
    public boolean goToPrevFragmentIfExist() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 1) {
            try {
                fm.popBackStack();
                fm.executePendingTransactions();
                needGoToPrevFragment = false;
                prevFragmentTag = "";
                return true;
            } catch (IllegalStateException ignored) {
                // There's no way to avoid getting this if saveInstanceState has already been called.
                Log.e("exception", "IllegalStateException " + ignored);
                needGoToPrevFragment = true;
                prevFragmentTag = "";
                return false;
            }
        }
        return false;
    }

    /**
     * Gets current fragment.
     *
     * @return the current fragment
     */
    public BaseFragment getCurrentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(getFragmentContainerId());
        return (BaseFragment) currentFragment;
    }

    /**
     * Show fragment.
     *
     * @param newFragment    the new fragment
     * @param addToBackStack the add to back stack
     */
    public void showFragment(BaseFragment newFragment, boolean addToBackStack) {
        if (newFragment == null) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (addToBackStack) {
            fragmentTransaction.add(getFragmentContainerId(), newFragment);
            fragmentTransaction.addToBackStack(newFragment.getTagForStack());
        } else {
            fragmentTransaction.replace(getFragmentContainerId(), newFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}
