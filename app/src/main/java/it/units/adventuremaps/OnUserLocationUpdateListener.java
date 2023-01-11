package it.units.adventuremaps;

import android.location.Location;

import java.util.EventListener;

public interface OnUserLocationUpdateListener extends EventListener {
    void onUserLocationUpdate(Location userLocation);
}
