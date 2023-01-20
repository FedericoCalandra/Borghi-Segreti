package it.units.adventuremaps;

import java.util.ArrayList;
import java.util.EventListener;

public interface DataEventListener extends EventListener {

    void onDataAvailable(ArrayList<Experience> experiences);

    void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences);

    void onPointsUpdated(int points);
}
