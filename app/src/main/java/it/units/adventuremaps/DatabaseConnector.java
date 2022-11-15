package it.units.adventuremaps;

import java.util.List;

public interface DatabaseConnector {

    public void initializeConnection();

    public List<Zone> getAllZones();

    public List<Experience> getAllExperiences(Zone zone);

}
