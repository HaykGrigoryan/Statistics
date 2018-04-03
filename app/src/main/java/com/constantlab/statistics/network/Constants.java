package com.constantlab.statistics.network;

import android.content.Context;
import android.content.Intent;

import com.constantlab.statistics.utils.SharedPreferencesManager;

/**
 * Created by Hayk on 29/12/2017.
 */

public class Constants {
    //    public static final String SERVER_URL = "http://93.185.74.122/api/v1/";
    public static final String SERVER_URL = "http://93.185.74.122/api/v1/";

    public static final String SERVER_LOCAL_URL = "http://192.168.1.50/api/";

    public static final String REF_TYPE_STREET_TYPE = "street_type";
    public static final String REF_TYPE_CHANGE_TYPE = "change_type";
    public static final String REF_TYPE_APARTMENT_TYPE = "apartment_type";
    public static final String REF_TYPE_BUILDING_TYPE = "building_type";
    public static final String REF_TYPE_BUILDING_STATUS = "building_status_type";


    public static final Integer[] BUILDING_TYPE_SPECIAL_INSTITUTIONS = {19835496, 19823755, 19823756, 19824193, 19824220, 19824221, 19824225, 19824235, 19824239,
            19824238, 19824248, 19824272, 19824282, 19824299, 19824294, 19824314, 19824333, 19824334, 19824385, 19824396, 19824428, 19824487, 19824488, 19824489,
            19824491, 19824493, 19824511,
            19823761, 19823762, 19823763, 19824510};
    public static final Integer[] BUILDING_TYPE_CLOSED_INSTITUTIONS = {19824505, 19824506, 19824507, 19824508, 19824504, 19824503, 19824502};

    public static final Integer[] BUILDING_STATUS_INACTIVE = {19834655, 19834657, 19834642};

    public static String constructBaseURL(Context context) {
        String ip = SharedPreferencesManager.getInstance().getServerIP(context);
        String port = SharedPreferencesManager.getInstance().getServerPort(context);
        String preffix = "http://";
        String suffix = "/api/v1/";
        return preffix + ip + port + suffix;
    }

}
