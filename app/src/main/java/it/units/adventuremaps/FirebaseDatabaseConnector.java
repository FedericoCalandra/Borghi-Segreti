package it.units.adventuremaps;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FirebaseDatabaseConnector {

    private static final String TAG = "FIREBASE_DB_CONNECTOR";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://adventuremaps-1205-default-rtdb.europe-west1.firebasedatabase.app");
    private final FirebaseUser user;
    private DataEventListener listener;
    private boolean experienceRequestedFromObjectiveExp = false;
    private boolean experienceRequestedFromCompletedExp = false;
    private boolean completedExperiencesLoaded = false;
    private boolean objectiveExperienceLoaded = false;
    private boolean dataAreAvailableToClient = false;


    private ArrayList<Experience> experiences;
    private ArrayList<Experience> completedExperiences;
    private Experience objectiveExperience;

    public FirebaseDatabaseConnector(FirebaseUser currentUser) {
        this.user = currentUser;
        loadExperiences();
        loadCompletedExperiences();
        loadObjectiveExperience();
        completedExperiences = new ArrayList<>();
    }

    public void addDataEventListener(DataEventListener listener) {
        this.listener = listener;
    }

    public void loadExperiences() {
        DatabaseReference experiencesRef = database.getReference().child("experiences");

        experiencesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                experiences = new ArrayList<>();

                for (DataSnapshot dataExperience : snapshot.getChildren()) {
                    String id = dataExperience.getKey();
                    String name = dataExperience.child("name").getValue(String.class);
                    String description = dataExperience.child("description").getValue(String.class);
                    ExperienceType type = dataExperience.child("type").getValue(ExperienceType.class);
                    Double latitude = dataExperience.child("coordinates").child("latitude").getValue(Double.class);
                    Double longitude = dataExperience.child("coordinates").child("longitude").getValue(Double.class);

                    LatLng coordinates = new LatLng(latitude, longitude);

                    experiences.add(new Experience(id, name, description, type, coordinates));
                }

                callListener();

                if (experienceRequestedFromObjectiveExp) {
                    experienceRequestedFromObjectiveExp = false;
                    loadObjectiveExperience();
                }
                if (experienceRequestedFromCompletedExp) {
                    experienceRequestedFromCompletedExp = false;
                    loadCompletedExperiences();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "error: " + error.getMessage());
            }
        });
    }

    public void loadExperiences(Zone zone) {

    }

    public void loadCompletedExperiences() {
        DatabaseReference userRef = database.getReference().child("user_data").child(user.getUid())
                .child("completed_experiences");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataExperience : snapshot.getChildren()) {
                    String experienceId = dataExperience.getKey();

                    if (experiences != null) {

                        completedExperiences.clear();

                        for (Experience experience : experiences) {
                            if (experience.getId().equals(experienceId)) {
                                experience.setIsCompletedByUser(true);
                                completedExperiences.add(experience);
                            }
                        }

                    } else {
                        experienceRequestedFromCompletedExp = true;
                        loadExperiences();
                        break;
                    }
                }

                if (dataAreAvailableToClient) {
                    listener.onStatusExperiencesChanged(completedExperiences);
                } else if (!experienceRequestedFromCompletedExp) {
                    completedExperiencesLoaded = true;
                    callListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "error: " + error.getMessage());
            }
        });
    }

    public void loadObjectiveExperience() {
        DatabaseReference userRef = database.getReference().child("user_data").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String objectiveExperienceId = snapshot.child("objective").getValue(String.class);
                Experience oldObjectiveExperience = objectiveExperience;

                if (objectiveExperienceId != null) {

                    if (experiences != null) {

                        for (Experience experience : experiences) {
                            if (experience.getId().equals(objectiveExperienceId)) {
                                experience.setIsTheObjective(true);
                                objectiveExperience = experience;
                                break;
                            }
                        }

                    } else {
                        experienceRequestedFromObjectiveExp = true;
                        loadObjectiveExperience();
                    }

                } else if (objectiveExperience != null) {
                    objectiveExperience.setIsTheObjective(false);
                    objectiveExperience = null;
                }

                if (dataAreAvailableToClient) {
                    ArrayList<Experience> twoElementList = new ArrayList<>();
                    if (objectiveExperience != null) {
                        twoElementList.add(objectiveExperience);
                    }
                    if (oldObjectiveExperience != null) {
                        twoElementList.add(oldObjectiveExperience);
                    }
                    listener.onStatusExperiencesChanged(twoElementList);
                } else if (!experienceRequestedFromObjectiveExp) {
                    objectiveExperienceLoaded = true;
                    callListener();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "error: " + error.getMessage());
            }
        });
    }

    public void setObjectiveExperienceOfUser(Experience experience) {
        if (objectiveExperience != null) {
            objectiveExperience.setIsTheObjective(false);
        }
        DatabaseReference objectiveRef = database.getReference().child("user_data").child(user.getUid()).child("objective");
        if (experience != null) {
            objectiveRef.setValue(experience.getId());
        } else {
            objectiveRef.setValue(null);
        }
    }

    public void setExperienceAsCompletedForUser(Experience completedExperience) {
        DatabaseReference completedRef = database.getReference().child("user_data").child(user.getUid()).child("completed_experiences");
        Log.d(TAG, "setExperienceAsCompletedForUser: complExps = " + completedExperiences);
        if (completedExperiences != null) {

            completedExperiences.add(completedExperience);

            Map<String, String> valuesOfCompletedExperiences = new HashMap<>();
            for (Experience experience : completedExperiences) {
                valuesOfCompletedExperiences.put(experience.getId(), "true");
            }

            completedRef.setValue(valuesOfCompletedExperiences);
        }
    }

    private void callListener() {
        if (listener != null && experiences != null && completedExperiencesLoaded && objectiveExperienceLoaded) {
            listener.onDataAvailable(experiences);
            dataAreAvailableToClient = true;
        }
    }
}