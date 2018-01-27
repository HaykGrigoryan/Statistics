package com.constantlab.statistics.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.StreetType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayk on 04/01/2018.
 */

public class GsonUtils {

//    public static List<ApartmentType> getApartmentTypeData(Context context) {
//        List<ApartmentType> apartmentTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("apartment_type.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<ApartmentType>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            apartmentTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return apartmentTypes;
//    }

//    public static List<BuildingType> getBuildingTypeData(Context context) {
//        List<BuildingType> apartmentTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("building_type.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<BuildingType>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            apartmentTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return apartmentTypes;
//    }

//    public static List<StreetType> getStreetTypeData(Context context) {
//        List<StreetType> apartmentTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("street_type.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<StreetType>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            apartmentTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return apartmentTypes;
//    }
//
//    public static List<ObjectType> getObjectTypeData(Context context) {
//        List<ObjectType> objectTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("object_type.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<ObjectType>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            objectTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return objectTypes;
//    }
//
//    public static List<BuildingStatus> getBuildingStatusData(Context context) {
//        List<BuildingStatus> objectTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("building_status.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<BuildingStatus>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            objectTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return objectTypes;
//    }
//
//    public static List<ChangeType> getChangeTypeData(Context context) {
//        List<ChangeType> objectTypes = new ArrayList<>();
//
//        try {
//            AssetManager assetManager = context.getAssets();
//            InputStream ims = assetManager.open("change_type.json");
//
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Reader reader = new InputStreamReader(ims);
//            Type listType = new TypeToken<List<ChangeType>>() {
//            }.getType();
//            Gson gson = gsonBuilder.create();
//            objectTypes = gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return objectTypes;
//    }
}
