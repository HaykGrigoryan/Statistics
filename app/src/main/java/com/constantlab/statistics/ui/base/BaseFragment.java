package com.constantlab.statistics.ui.base;


import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.ErrorDialogFragment;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public abstract class BaseFragment extends Fragment {

    protected ProgressDialog dialog;


    public void onNavigationBackClicked() {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity == null) return;
            activity.onBackPressed();
        }
    }

    /**
     * Displays a simple dialog message alert.
     *
     * @param messageResourceId message to be shown;
     */
    protected void showErrorDialog(int messageResourceId) {
        ErrorDialogFragment.showErrorDialog(getFragmentManager(), getString(messageResourceId));
    }

    /**
     * Displays a simple dialog message alert.
     *
     * @param message message to be shown;
     */
    public void showErrorDialog(String message) {
        ErrorDialogFragment.showErrorDialog(getFragmentManager(), message);
    }

    /**
     * Displays / Hides progress dialog.
     *
     * @param show Show dialog if true, hides it if false.
     */
    public void showLoader(boolean show) {
        if (show) {
            dialog = ProgressDialog
                    .show(getActivity(), null, getString(R.string.please_wait), true);
        } else if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * Displays a simple toast message.
     *
     * @param message message to be shown;
     */
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    public void showSnackMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Displays a simple toast message.
     *
     * @param messageResourceId message to be shown;
     */
    public void showToast(int messageResourceId) {
        Toast.makeText(getActivity(), getString(messageResourceId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showLoader(false);
    }


    /**
     * Gets tag for stack.
     *
     * @return the tag for stack
     */
    public String getTagForStack() {
        return this.getClass().getCanonicalName();
    }
}
