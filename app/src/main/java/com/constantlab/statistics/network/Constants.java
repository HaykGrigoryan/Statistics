package com.constantlab.statistics.network;

import android.content.Intent;

/**
 * Created by Hayk on 29/12/2017.
 */

public class Constants {
    public static final String SERVER_URL = "http://5.63.159.121/api/";

    public static final String REF_TYPE_STREET_TYPE = "street_type";
    public static final String REF_TYPE_CHANGE_TYPE = "change_type";
    public static final String REF_TYPE_APARTMENT_TYPE = "apartment_type";
    public static final String REF_TYPE_BUILDING_TYPE = "building_type";
    public static final String REF_TYPE_BUILDING_STATUS = "building_status_type";


    public static final Integer[] BUILDING_TYPE_SPECIAL_INSTITUTIONS = {19823761, 19823762, 19823763, 19824510};
    public static final Integer[] BUILDING_TYPE_CLOSED_INSTITUTIONS = {19824505, 19824506, 19824508, 19824504, 19824503, 19824502};

    public static final Integer[] BUILDING_STATUS_INACTIVE = {19834655, 19834657, 19834642};

}
