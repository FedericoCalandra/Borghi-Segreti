package it.units.adventuremaps;


import com.google.android.gms.maps.model.LatLng;

public class Zone {
    private String name;
    private LatLng coordinates;

    public Zone(String name, LatLng coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
}
