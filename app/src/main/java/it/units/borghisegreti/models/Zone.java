package it.units.borghisegreti.models;


import com.google.android.gms.maps.model.LatLng;

public class Zone {
    private String name;
    private LatLng coordinates;

    public Zone(String name, LatLng coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}
