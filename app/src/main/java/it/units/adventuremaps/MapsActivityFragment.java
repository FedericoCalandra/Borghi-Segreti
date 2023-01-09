package it.units.adventuremaps;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.internal.ContextUtils;

import java.util.ArrayList;
import java.util.Objects;


public class MapsActivityFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private static final int PERMISSION_ID = 44;
    private static final String TAG = "MY_LOCATION";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private Location overriddenUserLocation = new Location("");
    private Marker userLocationMarker;
    private boolean isTestModeEnabled;
    private boolean isMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        overriddenUserLocation.setLatitude(0);
        overriddenUserLocation.setLongitude(0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTestModeEnabled = sharedPreferences.getBoolean(getString(R.string.test_key), false);
        Log.d(TAG, "TestMode = " + isTestModeEnabled);

        getLastLocation();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        isMapReady = true;
        mMap.setOnMarkerClickListener(this);

        if (isTestModeEnabled) {
            mMap.setOnMapLongClickListener(this);
        }

        showUserLocation();
        ExperiencesLoader loader = new ExperiencesLoader();

        loader.addDataEventListener(new DataEventListener() {
            @Override
            public void onExperienceDataAvailable(ArrayList<Experience> experiences) {
                for (Experience experience : experiences) {
                    drawExperienceMarker(experience);
                }
            }
        });

    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16f));
        BottomSheetFragment blankFragment = new BottomSheetFragment();

        blankFragment.show(getSupportFragmentManager(),blankFragment.getTag());
        return true;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        overriddenUserLocation.setLatitude(point.latitude);
        overriddenUserLocation.setLongitude(point.longitude);
        updateUserLocation();
    }

    private void drawExperienceMarker(Experience experience) {
        Marker experienceMarker = mMap.addMarker(new MarkerOptions()
                .position(experience.getCoordinates())
                .title(experience.getName())
                .snippet(experience.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }

    public void showUserLocation() {
        if (userLocation != null) {
            LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            userLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(userCoordinates)
                    .title("User Location")
                    .icon(BitmapDescriptorFactory.fromAsset("icons/userLocationIcon.png")));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
        }
    }

    public void updateUserLocation() {
        LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        if (userLocationMarker != null) {
            userLocationMarker.setPosition(userCoordinates);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (isTestModeEnabled) {
            Log.d(TAG, "TestMode: overriding user location...");
            userLocation = overriddenUserLocation;
            Log.d(TAG, "TestMode: new user location = " + userLocation);
        } else {

            if (checkPermissions()) {

                if (isLocationEnabled()) {

                    fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                        Location location = task.getResult();
                        if (location != null) {
                            this.userLocation = location;
                            Log.d(TAG, "User localized @ " + userLocation);
                        }
                        requestNewLocationData();
                        if (isMapReady) {
                            showUserLocation();
                        }
                    });
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage(R.string.activate_location_message)
                            .setTitle(R.string.activate_location_title);

                    alertDialogBuilder.setPositiveButton(R.string.activate_location_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            });
                    alertDialogBuilder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int id) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();
                }
            } else {
                requestPermissions();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest.Builder builder = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5);
        builder.setMaxUpdateDelayMillis(0);
        LocationRequest locationRequest = builder.build();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            assert mLastLocation != null;
            userLocation = mLastLocation;
            Log.d(TAG, "User location updated");
            updateUserLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}