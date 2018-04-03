package com.constantlab.statistics.ui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.GeoPolygon;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OSMMapFragment extends BaseFragment {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 43;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARS_LOCATION = 44;

    private Double latitude;
    private Double longitude;
    private Integer taskId;
    private Integer userId;
    private List<GeoPolygon> geoPolygons;

    MapView mMap;
    OSMMapFragment.MapAction mMapAction = OSMMapFragment.MapAction.VIEW;
    @BindView(R.id.tv_select)
    TextView tvSelect;

    @BindView(R.id.icMyLocation)
    View lMyLocation;
    private ItemizedIconOverlay mInitialIcon;

    private GeoPoint mCurrentLocation;
    private ItemizedIconOverlay mCurrentLocationIcon;
    private OverlayItem mCurrentLocationOverlayItem;
    private boolean mLocationPermissionGranted;
    private Toast mToast;

    GPSTracker gps;

    public static OSMMapFragment newInstance(OSMMapFragment.MapAction action, Integer taskId) {
        OSMMapFragment fragment = new OSMMapFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.KEY_MAP_ACTION, action.ordinal());
        args.putInt(ConstKeys.KEY_TASK_ID, taskId);

        fragment.setArguments(args);
        return fragment;
    }

    public static OSMMapFragment newInstance(OSMMapFragment.MapAction action, Double lat, Double lon) {
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
        if (getArguments() != null) {
            mMapAction = OSMMapFragment.MapAction.values()[getArguments().getInt(ConstKeys.KEY_MAP_ACTION)];
            taskId = getArguments().getInt(ConstKeys.KEY_TASK_ID);
            latitude = getArguments().getDouble(ConstKeys.KEY_LATITUDE, Double.NaN);
            longitude = getArguments().getDouble(ConstKeys.KEY_LONGITUDE, Double.NaN);
            userId = SharedPreferencesManager.getInstance().getUser(getContext()).getUserId();
            if (mMapAction == OSMMapFragment.MapAction.SHOW_POLYGON) {
                geoPolygons = RealmManager.getInstance().getTaskGeoPolygons(taskId, userId);
            }
        }

        mLocationListener = new MyLocationListener();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osm_map, container, false);
        ButterKnife.bind(this, view);
        mToast = Toast.makeText(getContext(), R.string.msg_waiting_location, Toast.LENGTH_SHORT);

        mMap = view.findViewById(R.id.map);


//        mMapContainer = view.findViewById(R.id.mapContainer);
        // Retrieve location and camera position from saved instance state.


        return view;
    }

    MyLocationListener mLocationListener;
    LocationManager mLocationManager;
    ItemizedIconOverlay<OverlayItem> items = null;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLocationPermission();
        if (mMapAction != OSMMapFragment.MapAction.PICK_LOCATION) {
            tvSelect.setVisibility(View.GONE);
        }
        BoundingBox boundingBox = new BoundingBox(55.50799, 87.35359, 40.53480, 46.42304);
        mMap.setClickable(true);
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);
        mMap.setUseDataConnection(false);
        mMap.setTileSource(new XYTileSource(
                "Map", 1,
                18,
                256,
                ".png",
                new String[]{"http://93.185.74.122:8080/styles/osm-bright/"}///{z}/{x}/{y}.png
        ));
        mMap.setMinZoomLevel(1);
        mMap.setMaxZoomLevel(18);
        mMap.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {
            @Override
            public void onFirstLayout(View v, int left, int top, int right, int bottom) {
                IMapController mapViewController = mMap.getController();
                if (mMapAction == MapAction.PICK_LOCATION && !latitude.isNaN() && !longitude.isNaN()) {
                    mapViewController.setZoom(16);
                    mapViewController.animateTo(new GeoPoint(latitude, longitude));
                } else {
                    GeoPoint geoPoint = SharedPreferencesManager.getInstance().getGeoPoint(getContext());
                    if (geoPoint != null) {
                        mapViewController.setZoom(16);
                        mapViewController.animateTo(geoPoint);
                    } else {
                        mapViewController.setZoom(5);
                        mapViewController.zoomToSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpan());
                        mapViewController.setCenter(boundingBox.getCenter());
                        mMap.zoomToBoundingBox(boundingBox, false);
                    }
                }
                mMap.invalidate();
            }
        });

        if (mMapAction == OSMMapFragment.MapAction.PICK_LOCATION) {
            if (latitude != null && longitude != null && !latitude.isNaN() && !longitude.isNaN()) {
                ArrayList<OverlayItem> markers = new ArrayList<>();
                OverlayItem item = new OverlayItem("", "", new GeoPoint(latitude, longitude));
                item.setMarker(ContextCompat.getDrawable(getActivity(), R.drawable.map_marker));
                markers.add(item);
                mInitialIcon = new ItemizedIconOverlay<>(getActivity(), markers, null);
                mMap.getOverlays().add(mInitialIcon);
                mMap.invalidate();
            }

            Overlay overlay = new Overlay() {


                @Override
                public void draw(Canvas c, MapView osm, boolean shadow) {

                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                    Projection proj = mapView.getProjection();
                    GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());

                    addMarkerToMap(loc);
                    return true;
                }

            };

            mMap.getOverlays().add(overlay);
        } else if (mMapAction == MapAction.SHOW_POLYGON) {
            for (GeoPolygon geoPolygon : geoPolygons) {
                drawPolygon(geoPolygon.getPoints());
            }
//            List<GeoPoint> geoPoints = new ArrayList<>();
//            geoPoints.add(new GeoPoint(55.50799, 87.35359));
//            geoPoints.add(new GeoPoint(50.50799, 75.35359));
//            geoPoints.add(new GeoPoint(40.53480, 46.42304));
//            drawPolygon(geoPoints);

        } else if (mMapAction == MapAction.VIEW) {
            ArrayList<OverlayItem> markers = new ArrayList<>();
            List<Building> buildings = RealmManager.getInstance().getBuildingsWithPoints(userId);
            for (Building building : buildings) {
                OverlayItem item = new OverlayItem("", "", new GeoPoint(building.getLatitude(), building.getLongitude()));
                item.setMarker(ContextCompat.getDrawable(getActivity(), R.drawable.map_marker));
                markers.add(item);
//                mInitialIcon = new ItemizedIconOverlay<>(getActivity(), markers, null);
            }
            mMap.getOverlays().add(new ItemizedIconOverlay<>(getActivity(), markers, null));
            mMap.invalidate();
        }

        lMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMapController mapViewController = mMap.getController();
                if (mCurrentLocation != null) {
                    mapViewController.setZoom(16);
                    mapViewController.animateTo(mCurrentLocation);
                    if (mMapAction == MapAction.PICK_LOCATION) {
                        addMarkerToMap(mCurrentLocation);
                    }
                    mMap.invalidate();
                } else {
                    mToast.show();
                }
            }
        });

    }

    private void addMarkerToMap(GeoPoint geoPoint) {
        ArrayList<OverlayItem> markers = new ArrayList<>();
        OverlayItem item = new OverlayItem("", "", geoPoint);
        item.setMarker(ContextCompat.getDrawable(getActivity(), R.drawable.map_marker));
        markers.add(item);
        if (mInitialIcon != null) {
            mMap.getOverlays().remove(mInitialIcon);
        }
        if (items == null) {
            items = new ItemizedIconOverlay<>(getActivity(), markers, null);
            mMap.getOverlays().add(items);
            mMap.invalidate();
        } else {
            mMap.getOverlays().remove(items);
            mMap.invalidate();
            items = new ItemizedIconOverlay<>(getActivity(), markers, null);
            mMap.getOverlays().add(items);
        }

        latitude = geoPoint.getLatitude();
        longitude = geoPoint.getLongitude();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocationConfig();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationListener);
    }

    private void drawPolygon(List<GeoPoint> geoPoints) {
        //add your points here
        Polygon polygon = new Polygon();    //see note below
        polygon.setFillColor(Color.argb(75, 255, 0, 0));
        //        polygon.setStrokeColor(Color.argb(200, 255, 0, 0));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);
        polygon.setTitle("A sample polygon");

        //polygons supports holes too, points should be in a counter-clockwise order
        List<List<GeoPoint>> holes = new ArrayList<>();
        holes.add(geoPoints);
        polygon.setHoles(holes);

        mMap.getOverlayManager().add(polygon);
    }

    private void showMyLocation() {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(mMap);
        myLocationNewOverlay.enableMyLocation(); // not on by default
        myLocationNewOverlay.setDrawAccuracyEnabled(true);
        mMap.getOverlays().add(myLocationNewOverlay);
        mMap.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.tv_select)
    public void selectLocation() {
        if (latitude != null && longitude != null && !latitude.isNaN() && !longitude.isNaN()) {
            Intent intent = new Intent();
            intent.putExtra(MapActivity.LATITUDE_TAG, latitude);
            intent.putExtra(MapActivity.LONGITUDE_TAG, longitude);
            SharedPreferencesManager.getInstance().setGeoPoint(getContext(), new GeoPoint(latitude, longitude));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            showToast(getString(R.string.select_location));
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationConfig();

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @SuppressLint("MissingPermission")
    private void updateLocationConfig() {
        String provider = LocationManager.GPS_PROVIDER;
        mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            mCurrentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        }
        displayMyCurrentLocationOverlay();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationConfig();
                }
            }
        }
    }

    public enum MapAction {
        VIEW, PICK_LOCATION, SHOW_POLYGON;
    }

    private void displayMyCurrentLocationOverlay() {
        if (mCurrentLocation != null) {
            if (mCurrentLocationIcon != null) {
                mMap.getOverlays().remove(mCurrentLocationIcon);
            }

            ArrayList<OverlayItem> markers = new ArrayList<>();
            mCurrentLocationOverlayItem = new OverlayItem("My Location", "My Location", mCurrentLocation);
            mCurrentLocationOverlayItem.setMarker(ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_pin));
            markers.add(mCurrentLocationOverlayItem);
            mCurrentLocationIcon = new ItemizedIconOverlay<>(getContext(), markers, null);
            mMap.getOverlays().add(mCurrentLocationIcon);
        }
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            mCurrentLocation = new GeoPoint(location);
            displayMyCurrentLocationOverlay();
        }

        public void onProviderDisabled(String provider) {
            Log.d("LOCATION", "onProviderDisabled");
        }

        public void onProviderEnabled(String provider) {
            Log.d("LOCATION", "onProviderEnabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("LOCATION", "onStatusChanged");
        }
    }
}
