package com.constantlab.statistics.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hayk on 03/01/2018.
 */

public class DateUtils {
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "dd.MM.yyyy";

    public static String getDisplayDate(String dateString) {
        if (dateString == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DB_DATE_FORMAT);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            return "";
        }
        format = new SimpleDateFormat(DISPLAY_FORMAT);
        return format.format(date);
    }

    public static String getSyncDate(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat(DB_DATE_FORMAT);
        Date date = new Date(timeInMillis);

        format = new SimpleDateFormat(DISPLAY_FORMAT);
        return format.format(date);
    }
}
