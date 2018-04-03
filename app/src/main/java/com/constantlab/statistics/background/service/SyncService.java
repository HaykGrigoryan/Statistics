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
import com.constantlab.statistics.models.User;
import com.constantlab.statistics.network.Constants;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.TaskRequest;
import com.constantlab.statistics.network.model.ApartmentItem;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BuildingItem;
import com.constantlab.statistics.network.model.GeoItem;
import com.constantlab.statistics.network.model.GetReferenceRequest;
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
        rtService = ServiceGenerator.createService(RTService.class, SyncService.this);
        loadChangeTypes(resultReceiver);
    }

    private void handleSuccess(ResultReceiver resultReceiver, String message) {
        SharedPreferencesManager.getInstance().setSyncing(this, false);
        Bundle bundle = new Bundle();
        int code = SyncResultReceiver.RESULT_CODE_OK;
        bundle.putSerializable(SyncResultReceiver.PARAM_RESULT, true);
        bundle.putSerializable(SyncResultReceiver.PARAM_MESSAGE, message);
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

    private void loadChangeTypes(ResultReceiver resultReceiver) {
        try {
            Call<BasicMultipleDataResponse<ChangeType>> call = rtService.getChangeTypes(
                    new GetReferenceRequest(SharedPreferencesManager.getInstance().getUser(this).getKey(), Constants.REF_TYPE_CHANGE_TYPE));
            Response<BasicMultipleDataResponse<ChangeType>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                RealmManager.getInstance().clearLocalData(SharedPreferencesManager.getInstance().getUser(SyncService.this).getUserId());
                try {
                    SharedPreferencesManager.getInstance().clearGeoPoint(getApplicationContext());
                } catch (Exception e) {

                }

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
        try {
            Call<BasicMultipleDataResponse<StreetType>> call = rtService.getStreetTypes(
                    new GetReferenceRequest(SharedPreferencesManager.getInstance().getUser(this).getKey(), Constants.REF_TYPE_STREET_TYPE));
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


        try {
            Call<BasicMultipleDataResponse<BuildingType>> call = rtService.getBuildingTypes(
                    new GetReferenceRequest(SharedPreferencesManager.getInstance().getUser(this).getKey(), Constants.REF_TYPE_BUILDING_TYPE));
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

        try {
            Call<BasicMultipleDataResponse<BuildingStatus>> call = rtService.getBuildingStatusTypes(
                    new GetReferenceRequest(SharedPreferencesManager.getInstance().getUser(this).getKey(), Constants.REF_TYPE_BUILDING_STATUS));
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
        try {
            Call<BasicMultipleDataResponse<ApartmentType>> call = rtService.getApartmentTypes(
                    new GetReferenceRequest(SharedPreferencesManager.getInstance().getUser(this).getKey(), Constants.REF_TYPE_APARTMENT_TYPE));
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
        try {
            Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskList(new TaskRequest(SharedPreferencesManager.getInstance().getUser(this).getKey()));
            Response<BasicMultipleDataResponse<TaskItem>> response = call.execute();
            if (response.code() == 200 && response.isSuccessful() && response.body().isSuccessNestedStatus()) {
                insertTasks(response.body().getData());
                NotificationCenter.getInstance().notifyOnSyncFromServer();
                User user = SharedPreferencesManager.getInstance().getUser(SyncService.this);
                User realmUser = RealmManager.getInstance().getUser(user.getUsername(), user.getPassword());
                realmUser.setLastSyncFromServer(Calendar.getInstance().getTimeInMillis());
                RealmManager.getInstance().saveUser(realmUser);
                SharedPreferencesManager.getInstance().setUser(SyncService.this, realmUser);
                handleSuccess(resultReceiver, response.body().getMessage());
            } else if (response.body() != null && response.body().getMessage() != null) {
                handleError(resultReceiver, response.body().getMessage());
            } else {
                handleError(resultReceiver, getString(R.string.message_connection_problem));

            }


        } catch (IOException e) {
            handleError(resultReceiver, getString(R.string.message_connection_problem));
        }
    }

    private void insertTasks(List<TaskItem> items) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            User user = SharedPreferencesManager.getInstance().getUser(SyncService.this);
            Integer userId = user.getUserId();
            if (realm.where(Task.class).equalTo("user_id", userId).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    int taskId = 1;
                    Number currentLocalIdNum = realmObject.where(Task.class).max("local_id");
                    if (currentLocalIdNum != null) {
                        taskId = currentLocalIdNum.intValue() + 1;
                    }
                    int streetId = 1;
                    currentLocalIdNum = realmObject.where(Street.class).max("local_id");
                    if (currentLocalIdNum != null) {
                        streetId = currentLocalIdNum.intValue() + 1;
                    }
                    int buildingId = 1;
                    currentLocalIdNum = realmObject.where(Building.class).max("local_id");
                    if (currentLocalIdNum != null) {
                        buildingId = currentLocalIdNum.intValue() + 1;
                    }
                    int apartmentId = 1;
                    currentLocalIdNum = realmObject.where(Apartment.class).max("local_id");
                    if (currentLocalIdNum != null) {
                        apartmentId = currentLocalIdNum.intValue() + 1;
                    }
                    for (TaskItem taskItem : items) {
                        Task task = Task.getTask(taskItem);
                        task.setLocalId(taskId++);
                        task.setUserId(userId);
                        realmObject.insert(task);
//                        if (taskItem.getDetails() != null) {
//                            for (GeoItem geoItem : taskItem.getDetails().getGeoItems()) {
//                                GeoPolygon polygon = GeoPolygon.getGeoPolygon(task.getTaskId(), geoItem);
//                                polygon.setLocalId(polygonId++);
//                                realmObject.insert(polygon);
//                            }
//                        }

                        for (StreetItem streetItem : taskItem.getStreetItems()) {
                            Street street = Street.getStreet(streetItem);
                            street.setTaskId(task.getTaskId());
                            street.setKato(taskItem.getKatoCode());//taskItem.getDetails().getKato()
                            street.setLocalId(streetId++);
                            street.setUserId(userId);
                            int buildingsCount = 0;
                            int streetAptCount = 0;
                            int streetResidentsCount = 0;
                            if (streetItem.getBuildings() != null) {
                                for (BuildingItem buildingItem : streetItem.getBuildings()) {
                                    buildingsCount++;
                                    int bldAptCount = 0;
                                    int bldResidentsCount = 0;
                                    Building building = Building.getBuilding(buildingItem);
                                    building.setStreetId(streetItem.getId());
                                    building.setTaskId(task.getTaskId());
                                    building.setKato(String.valueOf(street.getKato()));
                                    building.setLocalId(buildingId++);
                                    building.setUserId(userId);

                                    for (ApartmentItem apartmentItem : buildingItem.getApartmentItems()) {
                                        streetAptCount++;
                                        bldAptCount++;
                                        bldResidentsCount += apartmentItem.getInhabitants();
                                        Apartment apartment = Apartment.getApartment(apartmentItem);
                                        apartment.setBuildingId(buildingItem.getId());
                                        apartment.setTaskId(task.getTaskId());
                                        apartment.setLocalId(apartmentId++);
                                        apartment.setUserId(userId);
                                        realmObject.insert(apartment);
                                    }
                                    if (!Building.isFlatLevelEnabled(building.getBuildingType(), building.getBuildingStatus())) {
                                        bldResidentsCount = building.getTemporaryInhabitants();
                                    }
                                    streetResidentsCount += bldResidentsCount;
                                    building.setApartmentCount(bldAptCount);
                                    building.setResidentsCount(bldResidentsCount);
                                    realmObject.insert(building);
                                }
                            }

                            street.setBuildingCount(buildingsCount);
                            street.setApartmentCount(streetAptCount);
                            street.setResidentsCount(streetResidentsCount);
                            realmObject.insert(street);
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