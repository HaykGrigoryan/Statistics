package com.constantlab.statistics.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Sunny Kinger on 11-12-2017.
 */

public class Actions {
    public static final int VIEW_BUILDINGS = 1;
    public static final int ADD_BUILDING = 3;
    public static final int EDIT_BUILDING = 2;
    public static final int VIEW_APARTMENTS = 4;
    public static final int ADD_APARTMENT = 5;
    public static final int EDIT_APARTMENT = 6;


    @IntDef({VIEW_BUILDINGS, ADD_BUILDING, EDIT_BUILDING, VIEW_APARTMENTS, ADD_APARTMENT, EDIT_APARTMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Action {
    }
}
