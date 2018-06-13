package com.constantlab.statistics.ui.sync;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.constantlab.statistics.app.SyncManager;
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
import com.constantlab.statistics.models.User;
import com.constantlab.statistics.models.send.TaskSend;
import com.constantlab.statistics.network.Constants;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.TaskRequest;
import com.constantlab.statistics.network.model.ApartmentItem;
import com.constantlab.statistics.network.model.BasicMultipleDataResponse;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.BuildingItem;
import com.constantlab.statistics.network.model.GeoItem;
import com.constantlab.statistics.network.model.GetReferenceRequest;
import com.constantlab.statistics.network.model.StreetItem;
import com.constantlab.statistics.network.model.TaskItem;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.login.LoginActivity;
import com.constantlab.statistics.utils.DateUtils;
import com.constantlab.statistics.utils.ISync;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    Integer userId;
    RTService rtService;

    public static SyncFragment newInstance() {
        return new SyncFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
        rtService = ServiceGenerator.createService(RTService.class, getContext(), true);
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
//        loaderSync.setVisibility(SharedPreferencesManager.getInstance().isSyncing(getContext()) ? View.VISIBLE : View.INVISIBLE);
//        btnSyncWithServer.setEnabled(!SharedPreferencesManager.getInstance().isSyncing(getContext()));
//        btnSyncToServer.setEnabled(!SharedPreferencesManager.getInstance().isSyncing(getContext()));
    }

    @OnClick(R.id.btn_sync_with_server)
    public void getDataFromServer() {
//        if (SharedPreferencesManager.getInstance().isSyncing(getContext())) {
//            Toast.makeText(getContext(), getContext().getString(R.string.message_sync_in_progress), Toast.LENGTH_SHORT).show();
//        } else {
        if (History.getNotSyncedHistories(userId).size() == 0) {
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


    @OnClick(R.id.btn_logout)
    public void logout() {
        if (History.getNotSyncedHistories(userId).size() == 0) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.dialog_title_attention));
            builder.setMessage(getString(R.string.dialog_logout_confirmation));
            builder.setPositiveButton(getString(R.string.label_logout), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logoutAction();
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
                    logoutAction();
                }
            });
            builder.show();
        }

    }

    private void logoutAction() {
        SharedPreferencesManager.getInstance().removeUser(getContext());
//        SharedPreferencesManager.getInstance().clearSyncTimeInfo(getContext());
//        SharedPreferencesManager.getInstance().clearGeoPoint(getContext());
//        RealmManager.getInstance().clearLocalData();
        getActivity().finish();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void updateLastSyncToServerTime() {
        String date = "";
        long timeInMillis = SharedPreferencesManager.getInstance().getUser(getContext()).getLastSyncToServer();
        if (timeInMillis != -1) {
            date = DateUtils.getSyncDate(timeInMillis);
        }

        mLastSyncToServer.setText(getResources().getString(R.string.label_last_sync, date));
    }

    private void updateLastSyncFromServerTime() {
        String date = "";
        long timeInMillis = SharedPreferencesManager.getInstance().getUser(getContext()).getLastSyncFromServer();
        if (timeInMillis != -1) {
            date = DateUtils.getSyncDate(timeInMillis);
        }

        mLastSyncFromServer.setText(getResources().getString(R.string.label_last_sync, date));
    }

    @OnClick(R.id.btn_sync_to_server)
    public void sendHistoryToServer() {
        List<History> histories = RealmManager.getInstance().getNotSyncedHistories(userId);
        if (histories.size() != 0) {
            List<TaskSend> dataForSend = SyncManager.construct(getContext(), histories, userId);
            try {
                createChangesLog(new Gson().toJson(dataForSend));
            } catch (Exception e) {
                e.printStackTrace();
            }

            performSendHistory(dataForSend);
        }
//        Gson gson = new Gson();
//        gson.toJson(SyncManager.construct(getContext(), histories, userId));
//        else {
//            Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
//        }

////        List<HistoryForSend> sends = HistoryForSend.getForDump(History.getAllHistories());
////        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().create();
////        gson.toJson(sends);
//        History history = History.getNotSyncedHistory();
//        if (history != null) {
//            final HistoryForSend historyForSend = HistoryForSend.getForSend(history);
//            performSendHistory(historyForSend);
//        } else {
//            SharedPreferencesManager.getInstance().setLastSyncToServer(getContext(), Calendar.getInstance().getTimeInMillis());
//            updateLastSyncToServerTime();
//            Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
//            loading(false);
//        }
    }

    private void performSendHistory(List<TaskSend> sendData) {
        loading(true);

        Call<BasicSingleDataResponse<String>> call = rtService.addChangesGzip(sendData);
        call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
            @Override
            public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
                if (response.code() == 200 && response.body().isSuccessNestedStatus()) {

                    RealmManager.getInstance().updateNotSyncHistories(userId);
                    User user = SharedPreferencesManager.getInstance().getUser(getContext());
                    User realmUser = RealmManager.getInstance().getUser(user.getUsername(), user.getPassword());
                    realmUser.setLastSyncToServer(Calendar.getInstance().getTimeInMillis());
                    RealmManager.getInstance().saveUser(realmUser);
                    SharedPreferencesManager.getInstance().setUser(getContext(), realmUser);
                    updateLastSyncToServerTime();
                    if (response.body().getMessage() == null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.message_success_sync_to_server), Toast.LENGTH_SHORT).show();
                    }
                }

                if (response.body() != null && response.body().getMessage() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
                }
                loading(false);
            }

            @Override
            public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
                loading(false);
            }
        });
    }

    private void loading(boolean show) {
        pbSync.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void startSync() {
        SharedPreferencesManager.getInstance().setSyncing(getContext(), 0);
        SyncService.startServiceToSync(getActivity(), new SyncDataResultReceiver((MainActivity) getActivity()),true);
        loadButtonState();
//        Call<BasicMultipleDataResponse<TaskItem>> call = rtService.getTaskListGzip(new TaskRequest("zj1e5IEubqNMsfYS"));
//        call.enqueue(new Callback<BasicMultipleDataResponse<TaskItem>>() {
//            @Override
//            public void onResponse(Call<BasicMultipleDataResponse<TaskItem>> call, Response<BasicMultipleDataResponse<TaskItem>> response) {
//                if (response.code() == 200 && response.body().isSuccessNestedStatus()) {
//
//                }
//
//                loading(false);
//            }
//
//            @Override
//            public void onFailure(Call<BasicMultipleDataResponse<TaskItem>> call, Throwable t) {
//                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
//                loading(false);
//            }
//        });
    }

    @Override
    public void onSyncFromServer() {
        loadButtonState();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateLastSyncFromServerTime();
                }
            });
        }

    }

    @Override
    public void onSyncToServer() {

    }

    private static class SyncDataResultReceiver implements SyncResultReceiver.ResultReceiverCallBack<String> {
        private WeakReference<MainActivity> activityRef;

        public SyncDataResultReceiver(MainActivity activity) {
            activityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onSuccess(String message) {
            if (activityRef != null && activityRef.get() != null) {
                if (message != null) {
                    activityRef.get().showMessage(message);
                } else {
                    activityRef.get().showMessage(activityRef.get().getString(R.string.message_success_sync_from_server));
                }
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

    private boolean createChangesLog(String changesJson) {
        String logFileName = "change_" + SharedPreferencesManager.getInstance().getUser(getContext()).getUsername() + "_" + DateUtils.getSyncDate(Calendar.getInstance().getTimeInMillis()) + ".json";
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/" + "StatisticsLog");
            dir.mkdirs();
            File file = new File(dir, logFileName);

            FileOutputStream fos = new FileOutputStream(file);
//            FileOutputStream fos = getContext().openFileOutput(logFileName, Context.MODE_PRIVATE);
            if (changesJson != null) {
                fos.write(changesJson.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

}
