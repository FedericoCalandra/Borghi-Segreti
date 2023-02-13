package it.units.borghisegreti.interfaces;

import it.units.borghisegreti.models.Experience;

public interface Database {

    void addDataEventListener(DataEventListener listener);

    void setObjectiveExperienceOfUser(Experience experience);

    void setExperienceAsCompletedForUser(Experience experience);

}
