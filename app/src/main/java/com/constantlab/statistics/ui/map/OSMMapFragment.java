package com.constantlab.statistics.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class OSMMapFragment extends BaseFragment {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 43;

    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Double latitude;
    private Double longitude;
    private Integer taskId;
    private List<GeoPolygon> geoPolygons;

    MapView mMap;
//    MapView mapView;
    MapFragment.MapAction mMapAction = MapFragment.MapAction.VIEW;

//    RelativeLayout mMapContainer;

    public static OSMMapFragment newInstance(MapFragment.MapAction action, Integer taskId) {
        OSMMapFragment fragment = new OSMMapFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
        args.putInt(ConstKeys.KEY_TASK_ID, taskId);

        fragment.setArguments(args);
        return fragment;
    }

    public static OSMMapFragment newInstance(MapFragment.MapAction action, Double lat, Double lon) {
        OSMMapFragment fragment = new OSMMapFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
        if (lat != null && !lat.isNaN() && lon != null && !lon.isNaN()) {
            args.putDouble(ConstKeys.KEY_LATITUDE, lat);
            args.putDouble(ConstKeys.KEY_LONGITUDE, lon);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public static OSMMapFragment newInstance(OSMMapFragment.MapAction action) {
        OSMMapFragment fragment = new OSMMapFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (getArguments() != null) {
            mMapAction = MapFragment.MapAction.values()[getArguments().getInt(ConstKeys.KEY_MAP_ACTION)];
            taskId = getArguments().getInt(ConstKeys.KEY_TASK_ID);
            latitude = getArguments().getDouble(ConstKeys.KEY_LATITUDE, Double.NaN);
            longitude = getArguments().getDouble(ConstKeys.KEY_LONGITUDE, Double.NaN);

            if (mMapAction == MapFragment.MapAction.SHOW_POLYGON) {
                geoPolygons = RealmManager.getInstance().getTaskGeoPolygons(taskId);
            }
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osm_map, container, false);
        ButterKnife.bind(this, view);
        mMap = view.findViewById(R.id.map);
//        mMapContainer = view.findViewById(R.id.mapContainer);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(ConstKeys.KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(ConstKeys.KEY_CAMERA_POSITION);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMap.setClickable(true);
        loadMap("http://93.185.74.122:8080/styles/osm-bright/");
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);
        mMap.setUseDataConnection(false);

//        mMap.setTileSource(new XYTileSource(
//                "kazakhstan",
//                0,
//                18,
//                256,
//                ".jpg",
//                new String[]{"http://93.185.74.122:8080/styles/osm-bright/"}///{z}/{x}/{y}.png
//        ));
//        mMap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        IMapController mapViewController = mMap.getController();
        mapViewController.setZoom(5);
//        mapViewController.setCenter(BERLIN);
//        loadMapType();
    }

//    private void createMap() {
//        // Create the mapView with an MBTileProvider
//        OfflineTileProvider provider = getTileProvider();
//
//        mapView = new MapView(getContext(), provider);
//        mapView.setUseDataConnection(false);
//        // Set the MapView as the root View for this Activity; done!
////        setContentView(mapView);
//        mapView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mMapContainer.addView(mapView);
//        mapView.setBuiltInZoomControls(true);
//
//        MapController controller = (MapController) mapView.getController();
//
//        //desired zoom level (here we use maximum)
//        controller.setZoom(provider.getTileSource().getMaximumZoomLevel());
//        mapView.invalidate();
//    }

    private void loadMap(String source) {
        //first we'll look at the default location for tiles that we support
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/");
        if (f.exists()) {

            File[] list = f.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        continue;
                    }
                    String name = list[i].getName().toLowerCase();
                    if (!name.contains(".")) {
                        continue; //skip files without an extension
                    }
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    //narrow it down to only sqlite tiles
                    if (ArchiveFileFactory.isFileExtensionRegistered(name) &&
                            name.equals("mbtiles")) {
                        try {

                            //ok found a file we support and have a driver for the format, for this demo, we'll just use the first one

                            //create the offline tile provider, it will only do offline file archives
                            //again using the first file
                            OfflineTileProvider tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(getActivity()),
                                    new File[]{list[i]});
                            //tell osmdroid to use that provider instead of the default rig which is (asserts, cache, files/archives, online
                            mMap.setTileProvider(tileProvider);
                            this.mMap.setTileSource(FileBasedTileSource.getSource(source));
                            //this bit enables us to find out what tiles sources are available. note, that this action may take some time to run
                            //and should be ran asynchronously. we've put it inline for simplicity

//                            String source = "";
//                            IArchiveFile[] archives = tileProvider.getArchives();
//                            if (archives.length > 0) {
//                                //cheating a bit here, get the first archive file and ask for the tile sources names it contains
//                                Set<String> tileSources = archives[0].getTileSources();
//                                //presumably, this would be a great place to tell your users which tiles sources are available
//                                if (!tileSources.isEmpty()) {
//                                    //ok good, we found at least one tile source, create a basic file based tile source using that name
//                                    //and set it. If we don't set it, osmdroid will attempt to use the default source, which is "MAPNIK",
//                                    //which probably won't match your offline tile source, unless it's MAPNIK
//                                    source = tileSources.iterator().next();
//                                    this.mMap.setTileSource(FileBasedTileSource.getSource(source));
//                                } else {
//                                    this.mMap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
//                                }
//
//                            } else {
//                                this.mMap.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
//                            }

                            Toast.makeText(getActivity(), "Using " + list[i].getAbsolutePath() + " " + source, Toast.LENGTH_LONG).show();
                            this.mMap.invalidate();
                            return;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            Toast.makeText(getActivity(), f.getAbsolutePath() + " did not have any files I can open! Try using MOBAC", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), f.getAbsolutePath() + " dir not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private OfflineTileProvider getTileProvider() {
        //first we'll look at the default location for tiles that we support
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/");
        if (f.exists()) {

            File[] list = f.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        continue;
                    }
                    String name = list[i].getName().toLowerCase();
                    if (!name.contains(".")) {
                        continue; //skip files without an extension
                    }
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    //narrow it down to only sqlite tiles
                    if (ArchiveFileFactory.isFileExtensionRegistered(name) &&
                            name.equals("mbtiles")) {
                        try {

                            //ok found a file we support and have a driver for the format, for this demo, we'll just use the first one

                            //create the offline tile provider, it will only do offline file archives
                            //again using the first file
                            OfflineTileProvider tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(getActivity()),
                                    new File[]{list[i]});

                            return tileProvider;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }


    List<MapFragment.MapType> mapTypes;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
//            outState.putParcelable(ConstKeys.KEY_CAMERA_POSITION, mMap.getCameraPosition());
//            outState.putParcelable(ConstKeys.KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @OnClick(R.id.tv_select)
    public void selectLocation() {
        if (latitude != null && longitude != null && !latitude.isNaN() && !longitude.isNaN()) {
            Intent intent = new Intent();
            intent.putExtra(MapActivity.LATITUDE_TAG, latitude);
            intent.putExtra(MapActivity.LONGITUDE_TAG, longitude);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            showToast(getString(R.string.select_location));
        }
    }


//    private void drawPolygon(List<LatLng> points, LatLngBounds.Builder bounds) {
//        PolylineOptions rectOptions = new PolylineOptions();
//        for (LatLng point : points) {
//            rectOptions.add(point);
//            bounds.include(point);
//        }
//
//        Polyline polyline = mMap.addPolyline(rectOptions);
//
//    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
//                        mMap.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
//                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public enum MapAction {
        VIEW, PICK_LOCATION, SHOW_POLYGON;
    }

    public class MapType {
        int type;
        String name;

        public MapType(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
