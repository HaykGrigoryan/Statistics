package com.constantlab.statistics.ui.sync;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.HistoryForSend;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.ApartmentItem;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.BuildingItem;
import com.constantlab.statistics.network.model.LoginKey;
import com.constantlab.statistics.network.model.StreetItem;
import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.login.LoginActivity;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hayk on 25/12/2017.
 */

public class SyncFragment extends BaseFragment {
    @BindView(R.id.pb_sync)
    ProgressBar pbSync;

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
        return view;
    }

    @OnClick(R.id.btn_sync_with_server)
    public void getDataFromServer() {
        if (History.getNotSyncedHistory().size() == 0) {
            syncData();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.dialog_title_attention));
            builder.setMessage(getString(R.string.dialog_not_all_sync_note));
            builder.setPositiveButton(getString(R.string.label_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    syncData();
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        }
    }

    private void syncData() {
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskList(SharedPreferencesManager.getInstance().getKey(getContext()));
        loading(true);
        call.enqueue(new Callback<BasicMultipleDataResponse<TaskItem>>() {
            @Override
            public void onResponse(Call<BasicMultipleDataResponse<TaskItem>> call, Response<BasicMultipleDataResponse<TaskItem>> response) {
                if (response.body().isSuccess()) {
                    RealmManager.getInstance().clearLocalData();
                    insertTasks(response.body().getData());
                    NotificationCenter.getInstance().notifyOnSyncFromServer();
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
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.dialog_title_attention));
        builder.setMessage(getString(R.string.dialog_logout_confirmation));
        builder.setPositiveButton(getString(R.string.label_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferencesManager.getInstance().setKey(getContext(), null);
                RealmManager.getInstance().clearLocalData();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        builder.setNegativeButton(getString(R.string.label_cancel), null);
        builder.show();

    }

    private int tempSyncCount = 0;

    @OnClick(R.id.btn_sync_to_server)
    public void sendHistoryToServer() {
        tempSyncCount = 0;
        List<History> historyList = History.getNotSyncedHistory();
        final int syncItemsCount = historyList.size();
        for (History history : historyList) {
            final HistoryForSend historyForSend = HistoryForSend.getForSend(history);
            historyForSend.setKey(SharedPreferencesManager.getInstance().getKey(getContext()));
            RTService rtService = ServiceGenerator.createService(RTService.class);
//            Call<BasicSingleDataResponse<String>> call = rtService.addChanges(SharedPreferencesManager.getInstance().getKey(getContext()),
//                    history.getTaskId(), history.getObjectType(), history.getObjectId(), history.getChangeType(), history.getNewData());
            Call<BasicSingleDataResponse<String>> call = rtService.addChangesJSON(historyForSend);
            Gson gson = new Gson();
            gson.toJson(historyForSend);
            loading(true);

            call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
                @Override
                public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
                    historyForSend.setHistorySynced(history);
                    tempSyncCount++;
                    if (tempSyncCount == syncItemsCount) {
                        Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
                        loading(false);
                        NotificationCenter.getInstance().notifyOnSyncToServer();
                    }

//                    if (response.body().isSuccessNestedStatus()) {
//                        Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_key), Toast.LENGTH_SHORT).show();
//                    }

                }

                @Override
                public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();

                    loading(false);
                }
            });
        }


    }

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

}
