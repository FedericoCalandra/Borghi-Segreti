package it.units.adventuremaps;


import com.google.android.gms.maps.model.LatLng;

public class Experience {
    private String name;
    private String description;
    private ExperienceType type;
    private LatLng coordinates;

    public Experience(String name, String description, ExperienceType type, LatLng coordinates) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ExperienceType getType() {
        return type;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }


}
