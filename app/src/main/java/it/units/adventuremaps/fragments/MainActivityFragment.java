package it.units.adventuremaps.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import it.units.adventuremaps.interfaces.DataEventListener;
import it.units.adventuremaps.interfaces.DatabaseConnector;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.FirebaseDatabaseConnector;
import it.units.adventuremaps.R;
import it.units.adventuremaps.activities.UserProfileActivity;
import it.units.adventuremaps.activities.InitialActivity;
import it.units.adventuremaps.activities.CompletedExperiencesActivity;

public class MainActivityFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        getCurrentObjectiveAndSetUserPoints();

        CardView mapsBtn = view.findViewById(R.id.map_button);
        mapsBtn.setOnClickListener(mapBtnClickListener);

        CardView achievementButton = view.findViewById(R.id.completed_exp_button);
        achievementButton.setOnClickListener(completedExperienceBtnClickListener);

        CardView userProfileBtn = view.findViewById(R.id.user_button);
        userProfileBtn.setOnClickListener(userBtnClickListener);

        CardView logOutBtn = view.findViewById(R.id.logout_button);
        logOutBtn.setOnClickListener(logOutBtnClickListener);

        return view;
    }

    private void drawUserPoints(int points) {
        TextView pointsView = view.findViewById(R.id.points);
        pointsView.setText(String.valueOf(points));
    }

    private void getCurrentObjectiveAndSetUserPoints() {
        DatabaseConnector databaseConnector = new FirebaseDatabaseConnector(FirebaseAuth.getInstance().getCurrentUser());
        databaseConnector.addDataEventListener(new DataEventListener() {
            @Override
            public void onDataAvailable(ArrayList<Experience> experiences) {
                findObjective(experiences);
            }

            @Override
            public void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences) {
                findObjective(changedExperiences);
            }

            @Override
            public void onPointsUpdated(int points) {
                drawUserPoints(points);
            }
        });
    }

    private void findObjective(ArrayList<Experience> experiences) {
        for (Experience experience : experiences) {
            if (experience.getIsTheObjective()) {
                drawObjectiveView(experience);
                return;
            }
        }
        drawNoObjectiveView();
    }

    private void drawNoObjectiveView() {
        View noObjectiveLayout = view.findViewById(R.id.no_objective_view);
        noObjectiveLayout.setVisibility(View.VISIBLE);
        View objectiveLayout = view.findViewById(R.id.objective_view);
        objectiveLayout.setVisibility(View.GONE);
    }

    private void drawObjectiveView(Experience objective) {
        View objectiveLayout = view.findViewById(R.id.objective_view);
        objectiveLayout.setVisibility(View.VISIBLE);
        View noObjectiveLayout = view.findViewById(R.id.no_objective_view);
        noObjectiveLayout.setVisibility(View.GONE);


        TextView experienceTitle = view.findViewById(R.id.experience_title_main_fragment);
        TextView experienceDescription = view.findViewById(R.id.experience_description_main_fragment);

        experienceTitle.setText(objective.getName());
        experienceDescription.setText(objective.getDescription());
    }

    private final OnClickListener mapBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mapsIntent = new Intent(getActivity(), MapsActivityFragment.class);
            startActivity(mapsIntent);
        }
    };

    private final OnClickListener completedExperienceBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), CompletedExperiencesActivity.class);
            startActivity(intent);
        }
    };

    private final OnClickListener userBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(intent);
        }
    };

    private final OnClickListener logOutBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.logout_confirmation)
                    .setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Toast.makeText(getActivity(), "User signed out", Toast.LENGTH_SHORT).show();
                    Intent firstIntent = new Intent(getActivity(), InitialActivity.class);
                    Handler handler = new Handler();
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(firstIntent);
                                }
                            }
                            , 200);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

}
