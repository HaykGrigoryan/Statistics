package com.constantlab.statistics.network;

import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.HistoryForSend;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.send.TaskSend;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.GetKeyRequest;
import com.constantlab.statistics.network.model.GetReferenceRequest;
import com.constantlab.statistics.network.model.LoginKey;
import com.constantlab.statistics.network.model.TaskItem;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by Hayk on 29/12/2017.
 */

public interface RTService {
    @POST("get_key")
    Call<BasicSingleDataResponse<String>> getKey(@Body() GetKeyRequest getKeyRequest);

    @POST("get_task_list")
    Call<BasicMultipleDataResponse<TaskItem>> getTaskList(@Body TaskRequest taskRequest);

    @POST("get_reference")
    Call<BasicMultipleDataResponse<StreetType>> getStreetTypes(@Body GetReferenceRequest getReferenceRequest);

    @POST("get_reference")
    Call<BasicMultipleDataResponse<ChangeType>> getChangeTypes(@Body GetReferenceRequest getReferenceRequest);

    @POST("get_reference")
    Call<BasicMultipleDataResponse<BuildingType>> getBuildingTypes(@Body GetReferenceRequest getReferenceRequest);

    @POST("get_reference")
    Call<BasicMultipleDataResponse<BuildingStatus>> getBuildingStatusTypes(@Body GetReferenceRequest getReferenceRequest);

    @POST("get_reference")
    Call<BasicMultipleDataResponse<ApartmentType>> getApartmentTypes(@Body GetReferenceRequest getReferenceRequest);

//    @POST("add_changes")
//    Call<BasicSingleDataResponse<String>> addChangesJSON(@Body HistoryForSend history);

    @POST("add_changes")
    Call<BasicSingleDataResponse<String>> addChangesJSON(@Body List<TaskSend> history);
}
