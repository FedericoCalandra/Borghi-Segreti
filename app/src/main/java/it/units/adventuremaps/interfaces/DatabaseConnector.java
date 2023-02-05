package it.units.adventuremaps.interfaces;

import it.units.adventuremaps.models.Experience;

public interface DatabaseConnector {

    void addDataEventListener(DataEventListener listener);

    void setObjectiveExperienceOfUser(Experience experience);

    void setExperienceAsCompletedForUser(Experience experience);

}
