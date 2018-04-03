package com.constantlab.statistics.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Hayk on 13/03/2018.
 */

public class UIUtils {
    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }
}
