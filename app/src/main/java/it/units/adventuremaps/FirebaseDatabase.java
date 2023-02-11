package it.units.adventuremaps;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.units.adventuremaps.interfaces.DataEventListener;
import it.units.adventuremaps.interfaces.Database;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.models.Zone;
import it.units.adventuremaps.utils.ExperienceType;


public class FirebaseDatabase implements Database {

    private static final String TAG = "FIREBASE_DB_CONNECTOR";
    private final com.google.firebase.database.FirebaseDatabase database = com.google.firebase.database.FirebaseDatabase.getInstance("https://adventuremaps-1205-default-rtdb.europe-west1.firebasedatabase.app");
    private final FirebaseUser user;
    private DataEventListener listener;
    private boolean experienceRequestedFromObjectiveExp = false;
    private boolean experienceRequestedFromCompletedExp = false;
    private boolean completedExperiencesLoaded = false;
    private boolean objectiveExperienceLoaded = false;
    private boolean dataAreAvailableToClient = false;


    private ArrayList<Zone> zones;
    private ArrayList<Experience> experiences;
    private final ArrayList<Experience> completedExperiences;
    private Experience objectiveExperience;
    private int userPoints;

    public FirebaseDatabase(FirebaseUser currentUser) throws NullUserException {
        if (currentUser == null) {
            throw new NullUserException("current user is null");
        }
        this.user = currentUser;
        loadZones();
        loadExperiences();
        loadCompletedExperiences();
        loadObjectiveExperience();
        loadUserPoints();
        completedExperiences = new ArrayList<>();
    }

    @Override
    public void addDataEventListener(DataEventListener listener) {
        this.listener = listener;
    }

    @Override
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

    @Override
    public void setExperienceAsCompletedForUser(Experience completedExperience) {
        DatabaseReference completedRef = database.getReference().child("user_data").child(user.getUid()).child("completed_experiences");
        if (completedExperiences != null) {

            completedExperience.setFormattedDateOfCompletion(buildStringOfCurrentDate());
            completedExperiences.add(completedExperience);

            Map<String, String> valuesOfCompletedExperiences = new HashMap<>();
            for (Experience experience : completedExperiences) {
                valuesOfCompletedExperiences.put(experience.getId(), experience.getFormattedDateOfCompletion());
            }

            completedRef.setValue(valuesOfCompletedExperiences);

            DatabaseReference pointsRef = database.getReference().child("user_data").child(user.getUid()).child("points");
            pointsRef.setValue(userPoints + completedExperience.getPoints());
        }
    }

    private void loadZones() {
        DatabaseReference zonesRef = database.getReference().child("zones");

        zonesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                zones = new ArrayList<>();

                for (DataSnapshot dataZones : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ZONES");
                    String zoneName = dataZones.child("name").getValue(String.class);
                    Double latitude = dataZones.child("coordinates").child("latitude").getValue(Double.class);
                    Double longitude = dataZones.child("coordinates").child("longitude").getValue(Double.class);
                    LatLng zoneCoordinates = new LatLng(latitude, longitude);
                    zones.add(new Zone(zoneName, zoneCoordinates));
                }
                callListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "error: " + error.getMessage());
            }
        });
    }

    private void loadExperiences() {
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
                    Integer points = dataExperience.child("points").getValue(Integer.class);

                    LatLng coordinates = new LatLng(latitude, longitude);

                    if (points == null) {
                        points = 0;
                    }

                    experiences.add(new Experience(id, name, description, type, coordinates, points));
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

    private void loadCompletedExperiences() {
        DatabaseReference userRef = database.getReference().child("user_data").child(user.getUid())
                .child("completed_experiences");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedExperiences.clear();
                for (DataSnapshot dataExperience : snapshot.getChildren()) {
                    String experienceId = dataExperience.getKey();

                    if (experiences != null) {

                        for (Experience experience : experiences) {
                            if (experience.getId().equals(experienceId)) {
                                experience.setIsCompletedByUser(true);
                                experience.setFormattedDateOfCompletion(dataExperience.getValue(String.class));
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

    private void loadObjectiveExperience() {
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

    private void loadUserPoints() {
        DatabaseReference pointsRef = database.getReference().child("user_data").child(user.getUid()).child("points");

        pointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer points = snapshot.getValue(Integer.class);
                if (points != null) {
                    userPoints = points;
                } else {
                    userPoints = 0;
                }
                listener.onPointsUpdated(userPoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callListener() {
        if (listener != null && experiences != null && zones != null && completedExperiencesLoaded && objectiveExperienceLoaded) {
            listener.onDataAvailable(experiences, zones);
            dataAreAvailableToClient = true;
        }
    }

    private static String buildStringOfCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public static class NullUserException extends Exception {
        public NullUserException(String message) {
            super(message);
        }
    }

}