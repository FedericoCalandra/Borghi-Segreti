package it.units.borghisegreti.interfaces;

import java.util.ArrayList;
import java.util.EventListener;

import it.units.borghisegreti.models.Experience;
import it.units.borghisegreti.models.Zone;

public interface DataEventListener extends EventListener {

    void onDataAvailable(ArrayList<Experience> experiences, ArrayList<Zone> zones);

    void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences);

    void onPointsUpdated(int points);
}
