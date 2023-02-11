package it.units.adventuremaps.interfaces;

import java.util.ArrayList;
import java.util.EventListener;

import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.models.Zone;

public interface DataEventListener extends EventListener {

    void onDataAvailable(ArrayList<Experience> experiences, ArrayList<Zone> zones);

    void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences);

    void onPointsUpdated(int points);
}
