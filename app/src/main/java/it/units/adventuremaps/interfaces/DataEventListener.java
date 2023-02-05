package it.units.adventuremaps.interfaces;

import java.util.ArrayList;
import java.util.EventListener;

import it.units.adventuremaps.models.Experience;

public interface DataEventListener extends EventListener {

    void onDataAvailable(ArrayList<Experience> experiences);

    void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences);

    void onPointsUpdated(int points);
}
