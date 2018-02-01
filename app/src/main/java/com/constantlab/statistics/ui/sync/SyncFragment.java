package com.constantlab.statistics.ui.sync;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.background.receivers.SyncResultReceiver;
import com.constantlab.statistics.background.service.SyncService;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.ApartmentType;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.ChangeType;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.HistoryForSend;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.network.Constants;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.ApartmentItem;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.BuildingItem;
import com.constantlab.statistics.network.model.GeoItem;
import com.constantlab.statistics.network.model.StreetItem;
import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.login.LoginActivity;
import com.constantlab.statistics.utils.DateUtils;
import com.constantlab.statistics.utils.ISync;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hayk on 25/12/2017.
 */

public class SyncFragment extends BaseFragment implements ISync {
    @BindView(R.id.pb_sync)
    ProgressBar pbSync;
    @BindView(R.id.last_sync_to_server)
    TextView mLastSyncToServer;

    @BindView(R.id.btn_sync_with_server)
    Button btnSyncWithServer;

    @BindView(R.id.btn_sync_to_server)
    Button btnSyncToServer;

    @BindView(R.id.last_sync_from_server)
    TextView mLastSyncFromServer;

    @BindView(R.id.loaderSync)
    ProgressBar loaderSync;

    public static SyncFragment newInstance() {
        return new SyncFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, view);
        NotificationCenter.getInstance().addSyncListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NotificationCenter.getInstance().removeSyncListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateLastSyncToServerTime();
        updateLastSyncFromServerTime();
        loadButtonState();
    }

    private void loadButtonState() {
        loaderSync.setVisibility(SharedPreferencesManager.getInstance().isSyncing(getContext()) ? View.VISIBLE : View.INVISIBLE);
        btnSyncWithServer.setEnabled(!SharedPreferencesManager.getInstance().isSyncing(getContext()));
        btnSyncToServer.setEnabled(!SharedPreferencesManager.getInstance().isSyncing(getContext()));
    }

    @OnClick(R.id.btn_sync_with_server)
    public void getDataFromServer() {
//        if (SharedPreferencesManager.getInstance().isSyncing(getContext())) {
//            Toast.makeText(getContext(), getContext().getString(R.string.message_sync_in_progress), Toast.LENGTH_SHORT).show();
//        } else {
        if (History.getNotSyncedHistories().size() == 0) {
            startSync();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.dialog_title_attention));
            builder.setMessage(getString(R.string.dialog_not_all_sync_note));
            builder.setPositiveButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startSync();
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        }
//        }
    }


    private void syncData() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskList(SharedPreferencesManager.getInstance().getKey(getContext()));
        loading(true);
        call.enqueue(new Callback<BasicMultipleDataResponse<TaskItem>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<TaskItem>> call, Response<BasicMultipleDataResponse<TaskItem>> response) {
                if (response.code() == 200 && response.body().isSuccess()) {
                    insertTasks(response.body().getData());
                    NotificationCenter.getInstance().notifyOnSyncFromServer();
                    SharedPreferencesManager.getInstance().setLastSyncFromServer(getContext(), Calendar.getInstance().getTimeInMillis());
                    updateLastSyncFromServerTime();
                    Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_from_server), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                }
                loading(false);
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<TaskItem>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);

            }
        });
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        if (History.getNotSyncedHistories().size() == 0) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.dialog_title_attention));
            builder.setMessage(getString(R.string.dialog_logout_confirmation));
            builder.setPositiveButton(getString(R.string.label_logout), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferencesManager.getInstance().setKey(getContext(), null);
                    SharedPreferencesManager.getInstance().clearSyncTimeInfo(getContext());
                    RealmManager.getInstance().clearLocalData();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.dialog_title_warning));
            builder.setMessage(getString(R.string.dialog_logut_without_sync_message));
            builder.setPositiveButton(getString(R.string.label_sync), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendHistoryToServer();
                }
            }).setNegativeButton(getString(R.string.label_logout), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferencesManager.getInstance().setKey(getContext(), null);
                    RealmManager.getInstance().clearLocalData();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            builder.show();
        }

    }

    private void updateLastSyncToServerTime() {
        String date = "";
        long timeInMillis = SharedPreferencesManager.getInstance().getLastSyncToServer(getContext());
        if (timeInMillis != -1) {
            date = DateUtils.getSyncDate(timeInMillis);
        }

        mLastSyncToServer.setText(getResources().getString(R.string.label_last_sync, date));
    }

    private void updateLastSyncFromServerTime() {
        String date = "";
        long timeInMillis = SharedPreferencesManager.getInstance().getLastSyncFromServer(getContext());
        if (timeInMillis != -1) {
            date = DateUtils.getSyncDate(timeInMillis);
        }

        mLastSyncFromServer.setText(getResources().getString(R.string.label_last_sync, date));
    }

    @OnClick(R.id.btn_sync_to_server)
    public void sendHistoryToServer() {
        History history = History.getNotSyncedHistory();
        if (history != null) {
            final HistoryForSend historyForSend = HistoryForSend.getForSend(history);
            performSendHistory(historyForSend);
        } else {
            SharedPreferencesManager.getInstance().setLastSyncToServer(getContext(), Calendar.getInstance().getTimeInMillis());
            updateLastSyncToServerTime();
            Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
            loading(false);
        }
    }

    private void performSendHistory(HistoryForSend historyForSend) {
        loading(true);
        historyForSend.setKey(SharedPreferencesManager.getInstance().getKey(getContext()));
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicSingleDataResponse<String>> call = rtService.addChangesJSON(historyForSend);
        call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
            @Override
            public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    History history = historyForSend.setHistorySynced();
                    history.updateReferenceHisrories(getContext(), response.body().getTempId());
                    sendHistoryToServer();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            }

            @Override
            public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
                loading(false);
            }
        });
    }

//    public void sendHistoryToServer() {
//        tempSyncCount = 0;
//        List<History> historyList = History.getNotSyncedHistories();
//        final int syncItemsCount = historyList.size();
//        for (History history : historyList) {
//            final HistoryForSend historyForSend = HistoryForSend.getForSend(history);
//            historyForSend.setKey(SharedPreferencesManager.getInstance().getKey(getContext()));
//            RTService rtService = ServiceGenerator.createService(RTService.class);
////            Call<BasicSingleDataResponse<String>> call = rtService.addChanges(SharedPreferencesManager.getInstance().getKey(getContext()),
////                    history.getTaskId(), history.getObjectType(), history.getObjectId(), history.getChangeType(), history.getNewData());
//            Call<BasicSingleDataResponse<String>> call = rtService.addChangesJSON(historyForSend);
//            Gson gson = new Gson();
//            gson.toJson(historyForSend);
//            loading(true);
//
//            call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
//                @Override
//                public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
//                    historyForSend.setHistorySynced(history);
//                    tempSyncCount++;
//                    if (tempSyncCount == syncItemsCount) {
//                        Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
//                        loading(false);
//                        NotificationCenter.getInstance().notifyOnSyncToServer();
//                    }
//
////                    if (response.body().isSuccessNestedStatus()) {
////                        Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
////                    } else {
////                        Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
////                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
//                    Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
//
//                    loading(false);
//                }
//            });
//        }
//    }

    private void loading(boolean show) {
        pbSync.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void insertTasks(List<TaskItem> items) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            if (realm.where(Task.class).findAll().size() == 0) {
                realm.executeTransaction(realmObject -> {
                    for (TaskItem taskItem : items) {
                        Task task = Task.getTask(taskItem);
                        task.setKato(taskItem.getDetails().getKato());
                        realmObject.insert(task);
                        if (taskItem.getDetails() != null) {
                            for (GeoItem geoItem : taskItem.getDetails().getGeoItems()) {
                                GeoPolygon polygon = GeoPolygon.getGeoPolygon(task.getTaskId(), geoItem);
                                Number currentIdNum = realmObject.where(GeoPolygon.class).max("local_id");
                                int nextId;
                                if (currentIdNum == null) {
                                    nextId = 1;
                                } else {
                                    nextId = currentIdNum.intValue() + 1;
                                }
                                polygon.setLocalId(nextId);
                                realmObject.insert(polygon);
                            }
                        }

                        for (StreetItem streetItem : taskItem.getDetails().getStreetItems()) {
                            Street street = Street.getStreet(streetItem);
                            street.setTaskId(taskItem.getTaskId());
                            street.setKato(taskItem.getDetails().getKato());
                            Number currentIdNum = realmObject.where(Street.class).max("local_id");
                            int nextId;
                            if (currentIdNum == null) {
                                nextId = 1;
                            } else {
                                nextId = currentIdNum.intValue() + 1;
                            }
                            street.setLocalId(nextId);
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

                                    currentIdNum = realmObject.where(Building.class).max("local_id");
                                    if (currentIdNum == null) {
                                        nextId = 1;
                                    } else {
                                        nextId = currentIdNum.intValue() + 1;
                                    }
                                    building.setLocalId(nextId);

                                    realmObject.insert(building);

                                    for (ApartmentItem apartmentItem : buildingItem.getApartmentItems()) {
                                        Apartment apartment = Apartment.getApartment(apartmentItem);
                                        apartment.setBuildingId(buildingItem.getId());
                                        apartment.setTaskId(task.getTaskId());
                                        currentIdNum = realmObject.where(Apartment.class).max("local_id");
                                        if (currentIdNum == null) {
                                            nextId = 1;
                                        } else {
                                            nextId = currentIdNum.intValue() + 1;
                                        }
                                        apartment.setLocalId(nextId);
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


    private void loadChangeTypes() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<ChangeType>> call = rtService.getChangeTypes(SharedPreferencesManager.getInstance().getKey(getContext()), Constants.REF_TYPE_CHANGE_TYPE);
        call.enqueue(new Callback<BasicMultipleDataResponse<ChangeType>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<ChangeType>> call, Response<BasicMultipleDataResponse<ChangeType>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    RealmManager.getInstance().clearLocalData();
                    RealmManager.getInstance().insertTypes(response.body().getData());
                    loadStreetTypes();
                } else {
                    loading(false);
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<ChangeType>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);
            }
        });
    }

    private void loadStreetTypes() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<StreetType>> call = rtService.getStreetTypes(SharedPreferencesManager.getInstance().getKey(getContext()), Constants.REF_TYPE_STREET_TYPE);
        call.enqueue(new Callback<BasicMultipleDataResponse<StreetType>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<StreetType>> call, Response<BasicMultipleDataResponse<StreetType>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    RealmManager.getInstance().insertTypes(response.body().getData());
                    loadBuildingTypes();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<StreetType>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);
            }
        });
    }

    private void loadBuildingTypes() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<BuildingType>> call = rtService.getBuildingTypes(SharedPreferencesManager.getInstance().getKey(getContext()), Constants.REF_TYPE_BUILDING_TYPE);
        call.enqueue(new Callback<BasicMultipleDataResponse<BuildingType>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<BuildingType>> call, Response<BasicMultipleDataResponse<BuildingType>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    RealmManager.getInstance().insertTypes(response.body().getData());
                    loadBuildingStatusTypes();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<BuildingType>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);
            }
        });
    }

    private void loadBuildingStatusTypes() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<BuildingStatus>> call = rtService.getBuildingStatusTypes(SharedPreferencesManager.getInstance().getKey(getContext()), Constants.REF_TYPE_BUILDING_STATUS);

        call.enqueue(new Callback<BasicMultipleDataResponse<BuildingStatus>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<BuildingStatus>> call, Response<BasicMultipleDataResponse<BuildingStatus>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    RealmManager.getInstance().insertTypes(response.body().getData());
                    loadApartmentTypes();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<BuildingStatus>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);
            }
        });
    }

    private void loadApartmentTypes() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<ApartmentType>> call = rtService.getApartmentTypes(SharedPreferencesManager.getInstance().getKey(getContext()), Constants.REF_TYPE_APARTMENT_TYPE);

        call.enqueue(new Callback<BasicMultipleDataResponse<ApartmentType>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<ApartmentType>> call, Response<BasicMultipleDataResponse<ApartmentType>> response) {
                if (response.body().isSuccessNestedStatus()) {
                    RealmManager.getInstance().insertTypes(response.body().getData());
                    syncData();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            }

            @Override
            public void onFailure(Call<BasicMultipleDataResponse<ApartmentType>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                loading(false);
            }
        });
    }

    private void loadData() {
        loading(true);
        loadChangeTypes();
    }

    private void startSync() {
        SharedPreferencesManager.getInstance().setSyncing(getContext(), true);
        SyncService.startServiceToSync(getActivity(), new SyncDataResultReceiver((MainActivity) getActivity()));
        loadButtonState();
    }

    @Override
    public void onSyncFromServer() {
        loadButtonState();
//        updateLastSyncFromServerTime();
    }

    @Override
    public void onSyncToServer() {

    }

    private static class SyncDataResultReceiver implements SyncResultReceiver.ResultReceiverCallBack<Boolean> {
        private WeakReference<MainActivity> activityRef;

        public SyncDataResultReceiver(MainActivity activity) {
            activityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onSuccess(Boolean data) {
            if (activityRef != null && activityRef.get() != null) {
                activityRef.get().showMessage(activityRef.get().getString(R.string.message_success_sync_from_server));
                NotificationCenter.getInstance().notifyOnSyncFromServer();
            }
        }

        @Override
        public void onError(Exception exception) {
            if (activityRef != null && activityRef.get() != null) {
                activityRef.get().showMessage(exception != null ? exception.getMessage() : "Error");

                NotificationCenter.getInstance().notifyOnSyncFromServer();
            }
        }
    }

}
