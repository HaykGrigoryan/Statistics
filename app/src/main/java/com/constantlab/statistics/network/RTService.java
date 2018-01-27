package com.constantlab.statistics.network;

import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.HistoryForSend;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.LoginKey;
import com.constantlab.statistics.network.model.TaskItem;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by Hayk on 29/12/2017.
 */

public interface RTService {
    @FormUrlEncoded
    @POST("get_key")
    Call<BasicSingleDataResponse<LoginKey>> getKey(@Field("user") String user, @Field("password") String password);

    @FormUrlEncoded
    @POST("get_task_list")
    Call<BasicMultipleDataResponse<TaskItem>> getTaskList(@Field("key") String key);

    @FormUrlEncoded
    @POST("get_reference")
    Call<BasicMultipleDataResponse<StreetType>> getStreetTypes(@Field("key") String key, @Field("ref_type") String refType);

    @FormUrlEncoded
    @POST("get_reference")
    Call<BasicMultipleDataResponse<ChangeType>> getChangeTypes(@Field("key") String key, @Field("ref_type") String refType);

    @FormUrlEncoded
    @POST("get_reference")
    Call<BasicMultipleDataResponse<BuildingType>> getBuildingTypes(@Field("key") String key, @Field("ref_type") String refType);

    @FormUrlEncoded
    @POST("get_reference")
    Call<BasicMultipleDataResponse<BuildingStatus>> getBuildingStatusTypes(@Field("key") String key, @Field("ref_type") String refType);

    @FormUrlEncoded
    @POST("get_reference")
    Call<BasicMultipleDataResponse<ApartmentType>> getApartmentTypes(@Field("key") String key, @Field("ref_type") String refType);

    @FormUrlEncoded
    @POST("add_changes")
    Call<BasicSingleDataResponse<String>> addChanges(@Field("key") String key, @Field("task_id") int task_id,
                                                     @Field("object_type") int object_type, @Field("object_id") int object_id,
                                                     @Field("change_type") int change_type, @Field("new_data") String newData);

    @POST("add_changes")
    Call<BasicSingleDataResponse<String>> addChangesJSON(@Body HistoryForSend history);
}
