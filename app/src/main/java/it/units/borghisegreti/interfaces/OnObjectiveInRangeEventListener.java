package it.units.borghisegreti.interfaces;

import java.util.EventListener;

public interface OnObjectiveInRangeEventListener extends EventListener {
    void onObjectiveInRange();

    void onObjectiveOutOfRange();
}
