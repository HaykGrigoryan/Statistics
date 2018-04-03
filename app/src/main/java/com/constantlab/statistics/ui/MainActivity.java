package com.constantlab.statistics.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.SyncManager;
import com.constantlab.statistics.ui.base.BaseActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.ui.map.OSMMapFragment;
import com.constantlab.statistics.ui.sync.SyncFragment;
import com.constantlab.statistics.ui.tasks.TasksFragment;
import com.constantlab.statistics.utils.INavigation;
import com.constantlab.statistics.utils.ISync;
import com.constantlab.statistics.utils.NotificationCenter;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements INavigation, ISync, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.task_container)
    FrameLayout mTaskContainer;
    @BindView(R.id.mainContainer)
    RelativeLayout mContentView;
    BottomBar bottomBar;

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
        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.tab_tasks:
                    showFragment(SyncFragment.newInstance(), false);
                    showTaskContainer();
                    break;
                case R.id.tab_sync:
                    showFragment(SyncFragment.newInstance(), false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideTaskContainer();
                        }
                    }, 100);

                    break;
                case R.id.tab_map:
                    if (checkForPermission()) {
                        showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.VIEW), false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideTaskContainer();
                            }
                        }, 100);
                    }
                    break;
            }
        });

        NotificationCenter.getInstance().addNavigationListener(this);
        NotificationCenter.getInstance().addSyncListener(this);

        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mContentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mContentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    bottomBar.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomBar.setVisibility(View.VISIBLE);
                        }
                    }, 10);

                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermission();
        }
//        isStoragePermissionGranted();

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
        SharedPreferencesManager.getInstance().setSyncing(this, false);
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
    }

    @Override
    public void onSyncToServer() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        showTaskFragment(TasksFragment.newInstance(), false);
    }


    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (bottomBar.getCurrentTabId() == R.id.tab_map) {
//                showExtDirFilesCount();
                    showFragment(OSMMapFragment.newInstance(OSMMapFragment.MapAction.VIEW), false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideTaskContainer();
                        }
                    }, 100);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
