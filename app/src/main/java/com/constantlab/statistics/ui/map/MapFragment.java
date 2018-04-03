package com.constantlab.statistics.ui.map;

//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.constantlab.statistics.R;
//import com.constantlab.statistics.app.RealmManager;
//import com.constantlab.statistics.models.ApartmentType;
//import com.constantlab.statistics.models.GeoPolygon;
//import com.constantlab.statistics.ui.base.BaseFragment;
//import com.constantlab.statistics.utils.ConstKeys;
//import com.constantlab.statistics.utils.GsonUtils;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.tasks.Task;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//import static android.content.ContentValues.TAG;

/**
 * Created by Sunny Kinger on 12-12-2017.
 */

public class MapFragment {
//        extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
//
//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 43;
//
//    private static final int DEFAULT_ZOOM = 15;
//    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
//    @BindView(R.id.tv_select)
//    TextView tvSelect;
//    private boolean mLocationPermissionGranted;
//    private Location mLastKnownLocation;
//    private GoogleMap mMap;
//    private CameraPosition mCameraPosition;
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private Double latitude;
//    private Double longitude;
//    private Integer taskId;
//    private List<GeoPolygon> geoPolygons;
//
//    @BindView(R.id.sp_map_type)
//    Spinner spMapType;
//
//    MapAction mMapAction = MapAction.VIEW;
//
//    public static MapFragment newInstance(MapAction action, Integer taskId) {
//        MapFragment fragment = new MapFragment();
//        Bundle args = new Bundle();
//        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
//        args.putInt(ConstKeys.KEY_TASK_ID, taskId);
//
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public static MapFragment newInstance(MapAction action, Double lat, Double lon) {
//        MapFragment fragment = new MapFragment();
//        Bundle args = new Bundle();
//        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
//        if (lat != null && !lat.isNaN() && lon != null && !lon.isNaN()) {
//            args.putDouble(ConstKeys.KEY_LATITUDE, lat);
//            args.putDouble(ConstKeys.KEY_LONGITUDE, lon);
//        }
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public static MapFragment newInstance(MapAction action) {
//        MapFragment fragment = new MapFragment();
//        Bundle args = new Bundle();
//        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//        if (getArguments() != null) {
//            mMapAction = MapAction.values()[getArguments().getInt(ConstKeys.KEY_MAP_ACTION)];
//            taskId = getArguments().getInt(ConstKeys.KEY_TASK_ID);
//            latitude = getArguments().getDouble(ConstKeys.KEY_LATITUDE, Double.NaN);
//            longitude = getArguments().getDouble(ConstKeys.KEY_LONGITUDE, Double.NaN);
//
//            if (mMapAction == MapAction.SHOW_POLYGON) {
//                geoPolygons = RealmManager.getInstance().getTaskGeoPolygons(taskId);
//            }
//        }
//
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
//        ButterKnife.bind(this, view);
//        // Retrieve location and camera position from saved instance state.
//        if (savedInstanceState != null) {
//            mLastKnownLocation = savedInstanceState.getParcelable(ConstKeys.KEY_LOCATION);
//            mCameraPosition = savedInstanceState.getParcelable(ConstKeys.KEY_CAMERA_POSITION);
//        }
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (mMapAction != MapAction.PICK_LOCATION) {
//            tvSelect.setVisibility(View.GONE);
//        }
//        loadMapType();
//    }
//
//    List<MapType> mapTypes;
//
//    private void loadMapType() {
//        mapTypes = new ArrayList<>();
//        mapTypes.add(new MapType(GoogleMap.MAP_TYPE_SATELLITE, getString(R.string.map_type_satellite)));
//        mapTypes.add(new MapType(GoogleMap.MAP_TYPE_NORMAL, getString(R.string.map_type_normal)));
//        ArrayAdapter<MapType> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_map, mapTypes);
//        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//        spMapType.setAdapter(arrayAdapter);
//
//        spMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (mMap != null) {
//                    mMap.setMapType(mapTypes.get(i).getType());
//                }
//            }
//
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                return;
//            }
//        });
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        if (mMap != null) {
//            outState.putParcelable(ConstKeys.KEY_CAMERA_POSITION, mMap.getCameraPosition());
//            outState.putParcelable(ConstKeys.KEY_LOCATION, mLastKnownLocation);
//            super.onSaveInstanceState(outState);
//        }
//    }
//
//    @OnClick(R.id.tv_select)
//    public void selectLocation() {
//        if (latitude != null && longitude != null && !latitude.isNaN() && !longitude.isNaN()) {
//            Intent intent = new Intent();
//            intent.putExtra(MapActivity.LATITUDE_TAG, latitude);
//            intent.putExtra(MapActivity.LONGITUDE_TAG, longitude);
//            getActivity().setResult(Activity.RESULT_OK, intent);
//            getActivity().finish();
//        } else {
//            showToast(getString(R.string.select_location));
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        mMap.setOnMapClickListener(this);
//        // Prompt the user for permission.
//        getLocationPermission();
//
//        // Turn on the My Location layer and the related control on the map.
//        updateLocationUI();
//
//        // Get the current location of the device and set the position of the map.
//        getDeviceLocation();
//        if (mMapAction == MapAction.SHOW_POLYGON) {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            int pointsCount = 0;
//            for (GeoPolygon geoPolygon : geoPolygons) {
//                pointsCount += geoPolygon.getPoints().size();
//                drawPolygon(geoPolygon.getPoints(), builder);
//            }
//            if (pointsCount != 0) {
//                mMap.moveCamera(CameraUpdateFactory
//                        .newLatLngBounds(builder.build(), 20));
//            }
//        } else {
//            if (latitude != null && !latitude.isNaN() && longitude != null && !longitude.isNaN()) {
//                LatLng latLng = new LatLng(latitude, longitude);
//                addMarker(latLng);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        latLng, DEFAULT_ZOOM));
//            }
//        }
//    }
//
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
//
//    private void getDeviceLocation() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//
//                locationResult.addOnCompleteListener(getActivity(), task -> {
//                    if (task.isSuccessful()) {
//                        // Set the map's camera position to the current location of the device.
//                        mLastKnownLocation = task.getResult();
//                        if (mLastKnownLocation != null) {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        }
//                    } else {
//                        Log.d(TAG, "Current location is null. Using defaults.");
//                        Log.e(TAG, "Exception: %s", task.getException());
//                        mMap.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                    }
//                });
//            }
//        } catch (SecurityException e) {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }
//
//
//    private void updateLocationUI() {
//        if (mMap == null) {
//            return;
//        }
//        try {
//            if (mLocationPermissionGranted) {
//                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e) {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }
//
//    private void getLocationPermission() {
//        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
//    }
//
//
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//            }
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//        if (mMapAction == MapAction.PICK_LOCATION) {
//            mMap.clear();
//            latitude = latLng.latitude;
//            longitude = latLng.longitude;
//            addMarker(latLng);
//        }
//    }
//
//    private void addMarker(LatLng latLng) {
//        mMap.addMarker(new MarkerOptions().position(latLng));
//    }
//
//    public enum MapAction {
//        VIEW, PICK_LOCATION, SHOW_POLYGON;
//    }
//
//    public class MapType {
//        int type;
//        String name;
//
//        public MapType(int type, String name) {
//            this.type = type;
//            this.name = name;
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public String toString() {
//            return name;
//        }
//    }
}
