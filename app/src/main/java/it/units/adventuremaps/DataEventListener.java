package it.units.adventuremaps;

import java.util.ArrayList;
import java.util.EventListener;

public interface DataEventListener extends EventListener {
    void onExperienceDataAvailable(ArrayList<Experience> experiences);
}
