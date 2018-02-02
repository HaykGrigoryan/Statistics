package com.constantlab.statistics.background.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.background.NotifReceiver;
import com.constantlab.statistics.background.exception.FunctionalException;
import com.constantlab.statistics.background.receivers.SyncResultReceiver;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.network.Constants;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.ApartmentItem;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BuildingItem;
import com.constantlab.statistics.network.model.GeoItem;
import com.constantlab.statistics.network.model.StreetItem;
import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hayk on 01/02/2018.
 */

public class SyncService extends IntentService {
    private enum Actions {
        SYNC_FROM_SERVER
    }

    private enum PARAM {
        RESULT_RECEIVER
    }

    RTService rtService;

    public SyncService(String name) {
        super(name);
    }

    public SyncService() {
        super(SyncService.class.getName());
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
////        onTaskRemoved(intent);
//        ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM.RESULT_RECEIVER.name());
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (Actions.SYNC_FROM_SERVER.name().equals(action)) {
//                handleSync(resultReceiver);
//            }
//        }
//        return START_NOT_STICKY;
//    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM.RESULT_RECEIVER.name());
        if (intent != null) {
            final String action = intent.getAction();
            if (Actions.SYNC_FROM_SERVER.name().equals(action)) {
                handleSync(resultReceiver);
            }
        }
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleSync(ResultReceiver resultReceiver) {
        rtService = ServiceGenerator.createService(RTService.class);
        loadChangeTypes(resultReceiver);
    }

    private void handleSuccess(ResultReceiver resultReceiver) {
        SharedPreferencesManager.getInstance().setSyncing(this, false);
        Bundle bundle = new Bundle();
        int code = SyncResultReceiver.RESULT_CODE_OK;
        bundle.putSerializable(SyncResultReceiver.PARAM_RESULT, true);
        if (resultReceiver != null) {
            resultReceiver.send(code, bundle);
        }
        if (isAppIsInBackground(this)) {
            showNotification(this, getString(R.string.label_sync_from_server), getString(R.string.message_success_sync_from_server), new Intent(SyncService.this, MainActivity.class));
        }
    }

    private void handleError(ResultReceiver resultReceiver, String message) {
        SharedPreferencesManager.getInstance().setSyncing(this, false);
        Bundle bundle = new Bundle();
        int code = SyncResultReceiver.RESULT_CODE_ERROR;
        bundle.putSerializable(SyncResultReceiver.PARAM_EXCEPTION, new FunctionalException(message));
        if (resultReceiver != null) {
            resultReceiver.send(code, bundle);
        }
        if (isAppIsInBackground(this)) {
            showNotification(this, getString(R.string.label_sync_from_server), message, new Intent(SyncService.this, MainActivity.class));
        }
    }


    @Override
    public void onDestroy() {
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        SharedPreferencesManager.getInstance().setSyncing(this, false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        startService(restartServiceIntent);
//        super.onTaskRemoved(rootIntent);
//    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_sync)
                .setContentTitle(title)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void startServiceToSync(Context context, SyncResultReceiver.ResultReceiverCallBack resultReceiverCallBack) {
        SyncResultReceiver bankResultReceiver = new SyncResultReceiver(new Handler(context.getMainLooper()));
        bankResultReceiver.setReceiver(resultReceiverCallBack);
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(Actions.SYNC_FROM_SERVER.name());
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), bankResultReceiver);
        context.startService(intent);
    }

    public boolean isAppIsInBackground(final Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }

        return isInBackground;
    }

    //
    private void loadChangeTypes(ResultReceiver resultReceiver) {
//        Call<BasicMultipleDataResponse<ChangeType>> call = rtService.getChangeTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_CHANGE_TYPE);
//        call.enqueue(new Callback<BasicMultipleDataResponse<ChangeType>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<ChangeType>> call, Response<BasicMultipleDataResponse<ChangeType>> response) {
//                if (response.body().isSuccessNestedStatus()) {
//                    RealmManager.getInstance().clearLocalData();
//                    RealmManager.getInstance().insertTypes(response.body().getData());
//                    loadStreetTypes(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<ChangeType>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });
        try {
            Call<BasicMultipleDataResponse<ChangeType>> call = rtService.getChangeTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_CHANGE_TYPE);
            Response<BasicMultipleDataResponse<ChangeType>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().clearLocalData();
                RealmManager.getInstance().insertTypes(response.body().getData());
                loadStreetTypes(resultReceiver);
            } else {
                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void loadStreetTypes(ResultReceiver resultReceiver) {

//        Call<BasicMultipleDataResponse<StreetType>> call = rtService.getStreetTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_STREET_TYPE);
//        call.enqueue(new Callback<BasicMultipleDataResponse<StreetType>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<StreetType>> call, Response<BasicMultipleDataResponse<StreetType>> response) {
//                if (response.body().isSuccessNestedStatus()) {
//                    RealmManager.getInstance().insertTypes(response.body().getData());
//                    loadBuildingTypes(resultReceiver);
////                    handleSuccess(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<StreetType>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });

        try {
            Call<BasicMultipleDataResponse<StreetType>> call = rtService.getStreetTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_STREET_TYPE);
            Response<BasicMultipleDataResponse<StreetType>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().insertTypes(response.body().getData());
                loadBuildingTypes(resultReceiver);
            } else {
                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void loadBuildingTypes(ResultReceiver resultReceiver) {
//        Call<BasicMultipleDataResponse<BuildingType>> call = rtService.getBuildingTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_BUILDING_TYPE);
//        call.enqueue(new Callback<BasicMultipleDataResponse<BuildingType>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<BuildingType>> call, Response<BasicMultipleDataResponse<BuildingType>> response) {
//                if (response.body().isSuccessNestedStatus()) {
//                    RealmManager.getInstance().insertTypes(response.body().getData());
//                    loadBuildingStatusTypes(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<BuildingType>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });


        try {
            Call<BasicMultipleDataResponse<BuildingType>> call = rtService.getBuildingTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_BUILDING_TYPE);
            Response<BasicMultipleDataResponse<BuildingType>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().insertTypes(response.body().getData());
                loadBuildingStatusTypes(resultReceiver);
            } else {
                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void loadBuildingStatusTypes(ResultReceiver resultReceiver) {
//        Call<BasicMultipleDataResponse<BuildingStatus>> call = rtService.getBuildingStatusTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_BUILDING_STATUS);
//        call.enqueue(new Callback<BasicMultipleDataResponse<BuildingStatus>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<BuildingStatus>> call, Response<BasicMultipleDataResponse<BuildingStatus>> response) {
//                if (response.body().isSuccessNestedStatus()) {
//                    RealmManager.getInstance().insertTypes(response.body().getData());
//                    loadApartmentTypes(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<BuildingStatus>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });

        try {
            Call<BasicMultipleDataResponse<BuildingStatus>> call = rtService.getBuildingStatusTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_BUILDING_STATUS);
            Response<BasicMultipleDataResponse<BuildingStatus>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().insertTypes(response.body().getData());
                loadApartmentTypes(resultReceiver);
            } else {
                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void loadApartmentTypes(ResultReceiver resultReceiver) {
//        Call<BasicMultipleDataResponse<ApartmentType>> call = rtService.getApartmentTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_APARTMENT_TYPE);
//        call.enqueue(new Callback<BasicMultipleDataResponse<ApartmentType>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<ApartmentType>> call, Response<BasicMultipleDataResponse<ApartmentType>> response) {
//                if (response.body().isSuccessNestedStatus()) {
//                    RealmManager.getInstance().insertTypes(response.body().getData());
//                    syncData(resultReceiver);
////                    handleSuccess(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<ApartmentType>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });

        try {
            Call<BasicMultipleDataResponse<ApartmentType>> call = rtService.getApartmentTypes(SharedPreferencesManager.getInstance().getKey(this), Constants.REF_TYPE_APARTMENT_TYPE);
            Response<BasicMultipleDataResponse<ApartmentType>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().insertTypes(response.body().getData());
                syncData(resultReceiver);
            } else {

                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void syncData(ResultReceiver resultReceiver) {
//        Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskList(SharedPreferencesManager.getInstance().getKey(this));
//        call.enqueue(new Callback<BasicMultipleDataResponse<TaskItem>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<TaskItem>> call, Response<BasicMultipleDataResponse<TaskItem>> response) {
//                if (response.body().isSuccess()) {
//                    insertTasks(response.body().getData());
//                    SharedPreferencesManager.getInstance().setLastSyncFromServer(SyncService.this, Calendar.getInstance().getTimeInMillis());
//                    handleSuccess(resultReceiver);
//                } else {
//                    handleError(resultReceiver, getString(R.string.message_wrong_key));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<TaskItem>> call, Throwable t) {
//                handleError(resultReceiver, getString(R.string.message_connection_problem));
//            }
//        });
//
        try {
            Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskList(SharedPreferencesManager.getInstance().getKey(this));
            Response<BasicMultipleDataResponse<TaskItem>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccess()) {
                insertTasks(response.body().getData());
                NotificationCenter.getInstance().notifyOnSyncFromServer();
                SharedPreferencesManager.getInstance().setLastSyncFromServer(SyncService.this, Calendar.getInstance().getTimeInMillis());
                handleSuccess(resultReceiver);
            } else {
                handleError(resultReceiver, getString(R.string.message_wrong_key));
            }
        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void insertTasks(List<TaskItem> items) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            if (realm.where(Task.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    int taskId = 1;
                    int polygonId = 1;
                    int streetId = 1;
                    int buildingId = 1;
                    int apartmentId = 1;
                    for (TaskItem taskItem : items) {
                        Task task = Task.getTask(taskItem);
                        task.setKato(taskItem.getDetails().getKato());
                        realmObject.insert(task);
                        if (taskItem.getDetails() != null) {
                            for (GeoItem geoItem : taskItem.getDetails().getGeoItems()) {
                                GeoPolygon polygon = GeoPolygon.getGeoPolygon(task.getTaskId(), geoItem);
//                                Number currentIdNum = realmObject.where(GeoPolygon.class).max("local_id");
//                                int nextId;
//                                if (currentIdNum == null) {
//                                    nextId = 1;
//                                } else {
//                                    nextId = currentIdNum.intValue() + 1;
//                                }
                                polygon.setLocalId(polygonId++);
                                realmObject.insert(polygon);
                            }
                        }

                        for (StreetItem streetItem : taskItem.getDetails().getStreetItems()) {
                            Street street = Street.getStreet(streetItem);
                            street.setTaskId(taskItem.getTaskId());
                            street.setKato(taskItem.getDetails().getKato());
//                            Number currentIdNum = realmObject.where(Street.class).max("local_id");
//                            int nextId;
//                            if (currentIdNum == null) {
//                                nextId = 1;
//                            } else {
//                                nextId = currentIdNum.intValue() + 1;
//                            }
                            street.setLocalId(streetId++);
                            realmObject.insert(street);

                            if (streetItem.getAddressData() != null && streetItem.getAddressData().size() > 0) {
                                StreetItem.AddressData addressData = streetItem.getAddressData().get(0);
                                List<BuildingItem> buildingItems = addressData.getBuildingItems();
                                for (BuildingItem buildingItem : buildingItems) {
                                    Building building = Building.getBuilding(buildingItem);
                                    building.setStreetId(streetItem.getId());
                                    building.setTaskId(task.getTaskId());
                                    building.setKato(String.valueOf(street.getKato()));
                                    building.setStreetName(streetItem.getTitle());
                                    building.setStreetType(addressData.getStreetType());

//                                    currentIdNum = realmObject.where(Building.class).max("local_id");
//                                    if (currentIdNum == null) {
//                                        nextId = 1;
//                                    } else {
//                                        nextId = currentIdNum.intValue() + 1;
//                                    }
                                    building.setLocalId(buildingId++);

                                    realmObject.insert(building);

                                    for (ApartmentItem apartmentItem : buildingItem.getApartmentItems()) {
                                        Apartment apartment = Apartment.getApartment(apartmentItem);
                                        apartment.setBuildingId(buildingItem.getId());
                                        apartment.setTaskId(task.getTaskId());
//                                        currentIdNum = realmObject.where(Apartment.class).max("local_id");
//                                        if (currentIdNum == null) {
//                                            nextId = 1;
//                                        } else {
//                                            nextId = currentIdNum.intValue() + 1;
//                                        }
                                        apartment.setLocalId(apartmentId++);
                                        realmObject.insert(apartment);
                                    }

                                }
                            }
                        }
                    }

                });
            }
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}