package it.units.adventuremaps;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MapsActivityFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private static final int PERMISSION_ID = 44;
    private static final String TAG = "AM_LOCATION";
    private GoogleMap mMap;
    private Marker userLocationMarker;
    private boolean isTestModeEnabled;
    private boolean isMapReady = false;
    private ArrayList<Experience> experiences;
    private final Map<Marker, Experience> markerExperienceMap = new HashMap<>();
    private Marker objectiveMarker;
    private Experience objectiveExperience;
    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTestModeEnabled = sharedPreferences.getBoolean(getString(R.string.test_key), false);
        Log.d(TAG, "TestMode = " + isTestModeEnabled);

        locator = new Locator(this, isTestModeEnabled);
        locator.addOnUserLocationUpdateEventListener(this::drawUserLocationMarker);
        locator.addOnObjectiveCompletedEventListener(new OnObjectiveCompletedEventListener() {
            @Override
            public void onObjectiveCompleted() {
                Log.d(TAG, "Objective completed!");
            }
        });

        ExperiencesLoader loader = new ExperiencesLoader(FirebaseAuth.getInstance().getCurrentUser());
        loader.addDataEventListener(new DataEventListener() {
            @Override
            public void onExperienceDataAvailable(ArrayList<Experience> experiencesLoaded) {
                experiences = experiencesLoaded;
                for (Experience experience : experiences) {
                    if (experience.getIsTheObjective()) {
                        objectiveExperience = experience;
                    }
                }
                if(objectiveExperience != null) {
                    locator.setObjectiveExperience(objectiveExperience);
                }
                drawAllMarkerExperiences();
            }
        });
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

        drawAllMarkerExperiences();
    }

    private void drawAllMarkerExperiences() {
        if (isMapReady && experiences != null) {
            for (Experience experience : experiences) {
                Marker marker = drawExperienceMarker(experience);
                if (experience.getIsTheObjective()) {
                    objectiveMarker = marker;
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16f));
        Experience experience = markerExperienceMap.get(marker);
        BottomSheetFragment blankFragment = new BottomSheetFragment(experience, marker, this);

        blankFragment.show(getSupportFragmentManager(),blankFragment.getTag());
        return true;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Location overriddenUserLocation = new Location("");
        overriddenUserLocation.setLatitude(point.latitude);
        overriddenUserLocation.setLongitude(point.longitude);
        locator.overrideUserLocation(overriddenUserLocation);
    }

    public void drawObjectiveExperienceMarker(Experience objectiveExperience) {
        if (objectiveMarker != null) {
            objectiveMarker.remove();
        }
        objectiveMarker = drawExperienceMarker(objectiveExperience);
        this.objectiveExperience = objectiveExperience;
    }

    public Marker drawExperienceMarker(Experience experience) {
        BitmapDescriptor descriptor;

        if (experience.getIsTheObjective()) {
            descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        } else {
            descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(experience.getCoordinates())
                .title(experience.getName())
                .snippet(experience.getDescription())
                .icon(descriptor));

        markerExperienceMap.put(marker, experience);

        return marker;
    }

    private void drawUserLocationMarker(Location userLocation) {
        if (isMapReady) {
            LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

            if (userLocationMarker != null) {
                userLocationMarker.setPosition(userCoordinates);
            } else {
                userLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(userCoordinates)
                        .title("User Location")
                        .icon(BitmapDescriptorFactory.fromAsset("icons/userLocationIcon.png")));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
            }
        }
    }

    protected void requestLocalizationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locator.permissionGranted();
            }
        }
    }

    protected ArrayList<Experience> getExperiences() {
        return experiences;
    }
}