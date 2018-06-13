package com.constantlab.statistics.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.app.SyncManager;
import com.constantlab.statistics.background.SyncDataResultReceiver;
import com.constantlab.statistics.background.service.SyncService;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.User;
import com.constantlab.statistics.models.send.TaskSend;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.login.LoginActivity;
import com.constantlab.statistics.ui.map.OSMMapFragment;
import com.constantlab.statistics.ui.sync.SyncFragment;
import com.constantlab.statistics.ui.tasks.TasksFragment;
import com.constantlab.statistics.utils.DateUtils;
import com.constantlab.statistics.utils.INavigation;
import com.constantlab.statistics.utils.ISync;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.constantlab.statistics.utils.UIUtils;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements INavigation, ISync, ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.task_container)
    FrameLayout mTaskContainer;
    @BindView(R.id.drawer_layout)
    DrawerLayout mContentView;


    RelativeLayout lPbSync;
    TextView tvState;
    TextView tvName;
    TextView tvUserName;

    TextView tvSyncTime;
    TextView tvSendTime;

    User mUser;
    RTService rtService;

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected int getTaskFragmentContainerId() {
        return R.id.task_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showTaskFragment(TasksFragment.newInstance(), false);
        showTaskContainer();
        rtService = ServiceGenerator.createService(RTService.class, this, true);
//        bottomBar = findViewById(R.id.bottom_navigation);
//        bottomBar.setOnTabSelectListener(tabId -> {
//            switch (tabId) {
//                case R.id.tab_tasks:
//                    showFragment(SyncFragment.newInstance(), false);
//                    showTaskContainer();
//                    break;
//                case R.id.tab_sync:
//                    showFragment(SyncFragment.newInstance(), false);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            hideTaskContainer();
//                        }
//                    }, 100);
//
//                    break;
//                case R.id.tab_map:
//                    if (checkForPermission()) {
//                        showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.VIEW), false);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                hideTaskContainer();
//                            }
//                        }, 100);
//                    }
//                    break;
//            }
//        });

        NotificationCenter.getInstance().addNavigationListener(this);
        NotificationCenter.getInstance().addSyncListener(this);
        mContentView.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                Rect r = new Rect();
//                mContentView.getWindowVisibleDisplayFrame(r);
//                int screenHeight = mContentView.getRootView().getHeight();
//
//                // r.bottom is the position above soft keypad or device button.
//                // if keypad is shown, the r.bottom is smaller than that before.
//                int keypadHeight = screenHeight - r.bottom;
//
//                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
//                    bottomBar.setVisibility(View.GONE);
//                } else {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            bottomBar.setVisibility(View.VISIBLE);
//                        }
//                    }, 10);
//
//                }
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermission();
        }
//        isStoragePermissionGranted();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        tvSyncTime = navigationView.getHeaderView(0).findViewById(R.id.tvSyncTime);
        tvSendTime = navigationView.getHeaderView(0).findViewById(R.id.tvSendTime);
        lPbSync = navigationView.getHeaderView(0).findViewById(R.id.lPbSync);
        tvState = navigationView.getHeaderView(0).findViewById(R.id.tvState);
        mUser = SharedPreferencesManager.getInstance().getUser(this);
        loadHeaderData();
    }

    public DrawerLayout getDrawer() {
        return mContentView;
    }

    private void loadHeaderData() {
        tvName.setText(mUser.getUsername());
        tvUserName.setText(mUser.getUsername());

        String syncDate = "---";
        long timeInMillis = SharedPreferencesManager.getInstance().getUser(this).getLastSyncFromServer();
        if (timeInMillis != -1) {
            syncDate = DateUtils.getSyncDate(timeInMillis);
        }

        tvSyncTime.setText(syncDate);

        String sendDate = "---";
        timeInMillis = SharedPreferencesManager.getInstance().getUser(this).getLastSyncToServer();
        if (timeInMillis != -1) {
            sendDate = DateUtils.getSyncDate(timeInMillis);
        }

        tvSendTime.setText(sendDate);
        int syncState = SharedPreferencesManager.getInstance().isSyncing(this);
        tvState.setText(syncState == 0 ? getString(R.string.state_sync) : getString(R.string.state_send));
        lPbSync.setVisibility(syncState != -1 ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean checkForPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(mContentView, getString(R.string.msg_grand_body), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.label_grand), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestStoragePermission();
                            }
                        })
                        .show();
            } else {
                requestStoragePermission();
            }
            return false;
        } else {
            return true;
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_PERMISSION);
    }
//
//
//    private void refreshBottomNavigationSize(BottomNavigationView bottomNavigationView) {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView)
//                bottomNavigationView.getChildAt(0);
//        for (int i = 0; i < menuView.getChildCount(); i++) {
//            final View iconView =
//                    menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
//            final ViewGroup.LayoutParams layoutParams =
//                    iconView.getLayoutParams();
//            final DisplayMetrics displayMetrics =
//                    getResources().getDisplayMetrics();
//            layoutParams.height = (int) getResources().getDimensionPixelSize(R.dimen.botomBar_img_size);
////                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.botomBar_img_size),
////                            displayMetrics);
//            layoutParams.width = (int) getResources().getDimensionPixelSize(R.dimen.botomBar_img_size);
////                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.botomBar_img_size),
////                            displayMetrics);
//            iconView.setLayoutParams(layoutParams);
//        }
//    }

    private void showTaskContainer() {
        mFragmentContainer.setVisibility(View.GONE);
        mTaskContainer.setVisibility(View.VISIBLE);
    }

    private void hideTaskContainer() {
        mFragmentContainer.setVisibility(View.VISIBLE);
        mTaskContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getInstance().removeNavigationListener(this);
        NotificationCenter.getInstance().removeSyncListener(this);
        SharedPreferencesManager.getInstance().setSyncing(this, -1);
        super.onDestroy();
    }

    @Override
    public void openPage(BaseFragment fragment) {
        showTaskFragment(fragment, true);
    }

    @Override
    public void onSyncFromServer() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        showTaskFragment(TasksFragment.newInstance(), false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadHeaderData();
            }
        });

    }

    @Override
    public void onSyncToServer() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        showTaskFragment(TasksFragment.newInstance(), false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadHeaderData();
            }
        });

    }


    public void showMessage(String msg) {
        showSnackMessage(msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                if (bottomBar.getCurrentTabId() == R.id.tab_map) {
////                showExtDirFilesCount();
//                    showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.VIEW), false);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            hideTaskContainer();
//                        }
//                    }, 100);
//                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int isSync = SharedPreferencesManager.getInstance().isSyncing(this);
        if (id == R.id.nav_task) {
            showFragment(SyncFragment.newInstance(), false);
            showTaskContainer();
            drawer.closeDrawer(GravityCompat.START);
            // Handle the wallets action
        } else if (id == R.id.nav_map) {
            if (checkForPermission()) {
                showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.VIEW), false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideTaskContainer();
                    }
                }, 100);
            }
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_download) {
            if (isSync == -1) {
                getDataFromServer();
            } else {
                showSnackMessage(getString(R.string.message_sync_in_progress));
            }
        } else if (id == R.id.nav_upload) {
//            sendHistoryToServer();
            if (isSync == -1) {
                startSend();
            } else {
                showSnackMessage(getString(R.string.message_sync_in_progress));
            }
        } else if (id == R.id.nav_logout) {
            if (History.getNotSyncedHistories(mUser.getUserId()).size() == 0) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
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
                        new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_title_warning));
                builder.setMessage(getString(R.string.dialog_logut_without_sync_message));
                builder.setPositiveButton(getString(R.string.label_sync), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sendHistoryToServer();
                    }
                }).setNegativeButton(getString(R.string.label_logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logoutAction();
                    }
                });
                builder.show();
            }
            drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }

    public void getDataFromServer() {
        if (History.getNotSyncedHistories(mUser.getUserId()).size() == 0) {
            startSync();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
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
    }

    private void startSync() {
        SharedPreferencesManager.getInstance().setSyncing(this, 0);
        SyncService.startServiceToSync(this, new SyncDataResultReceiver(this), true);
        loadHeaderData();
    }

    private void startSend() {
        SharedPreferencesManager.getInstance().setSyncing(this, 1);
        SyncService.startServiceToSync(this, new SyncDataResultReceiver(this), false);
        loadHeaderData();
    }

    public void sendHistoryToServer() {
        List<History> histories = RealmManager.getInstance().getNotSyncedHistories(mUser.getUserId());
        if (histories.size() != 0) {
            List<TaskSend> dataForSend = SyncManager.construct(this, histories, mUser.getUserId());
            try {
                createChangesLog(new Gson().toJson(dataForSend));
            } catch (Exception e) {
                e.printStackTrace();
            }

            performSendHistory(dataForSend);
        } else {
            showSnackMessage(getString(R.string.msg_no_changes));
        }
    }

    private boolean createChangesLog(String changesJson) {
        String logFileName = "change_" + SharedPreferencesManager.getInstance().getUser(this).getUsername() + "_" + DateUtils.getSyncDate(Calendar.getInstance().getTimeInMillis()) + ".json";
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

    private void performSendHistory(List<TaskSend> sendData) {
        loading(true);

        Call<BasicSingleDataResponse<String>> call = rtService.addChangesGzip(sendData);
        call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
            @Override
            public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
                if (response.code() == 200 && response.body().isSuccessNestedStatus()) {

                    RealmManager.getInstance().updateNotSyncHistories(mUser.getUserId());
                    User user = SharedPreferencesManager.getInstance().getUser(MainActivity.this);
                    User realmUser = RealmManager.getInstance().getUser(user.getUsername(), user.getPassword());
                    realmUser.setLastSyncToServer(Calendar.getInstance().getTimeInMillis());
                    RealmManager.getInstance().saveUser(realmUser);
                    SharedPreferencesManager.getInstance().setUser(MainActivity.this, realmUser);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadHeaderData();
                        }
                    });
                    if (response.body().getMessage() == null) {
                        showSnackMessage(getString(R.string.message_success_sync_to_server));
                    }
                }

                if (response.body() != null && response.body().getMessage() != null) {
                    showSnackMessage(response.body().getMessage());
                } else {
                    showSnackMessage(getString(R.string.message_connection_problem));
                }
                loading(false);
            }

            @Override
            public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
                showSnackMessage(getString(R.string.message_connection_problem));
                loading(false);
            }
        });
    }

    private void loading(boolean show) {
        SharedPreferencesManager.getInstance().setSyncing(this, show ? 1 : -1);
        loadHeaderData();
    }

    private void logoutAction() {
        SharedPreferencesManager.getInstance().removeUser(this);
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void showSnackMessage(String message) {
        Snackbar.make(mContentView, message, Snackbar.LENGTH_LONG).show();
    }
}
