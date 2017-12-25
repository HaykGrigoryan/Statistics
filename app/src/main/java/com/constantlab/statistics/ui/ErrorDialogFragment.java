package com.constantlab.statistics.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.constantlab.statistics.R;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class ErrorDialogFragment extends DialogFragment {

    public static final String MESSAGE = "MESSAGE";

    /**
     * Displays a simple dialog message alert.
     *
     * @param message the message to be shown
     */
    public static void showErrorDialog(FragmentManager fragmentManager, String message) {
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ErrorDialogFragment.MESSAGE, message);
        errorDialogFragment.setArguments(bundle);
        errorDialogFragment.show(fragmentManager, "ErrorDialogFragment");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        String message = bundle.getString(MESSAGE);
        if (message == null) {
            message = getActivity().getString(R.string.error_default);
        }
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dismiss());
        return builder.create();
    }
}

