package it.units.adventuremaps.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import it.units.adventuremaps.activities.MainActivity;
import it.units.adventuremaps.interfaces.DataEventListener;
import it.units.adventuremaps.interfaces.Database;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.FirebaseDatabase;
import it.units.adventuremaps.R;
import it.units.adventuremaps.activities.UserProfileActivity;
import it.units.adventuremaps.activities.SignInActivity;
import it.units.adventuremaps.activities.CompletedExperiencesActivity;
import it.units.adventuremaps.models.Zone;
import it.units.adventuremaps.utils.IconBuilder;

public class MainActivityFragment extends Fragment {

    private static final String TAG = "MAIN_ACTIVITY";
    private View view;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        try {
            database = new FirebaseDatabase(FirebaseAuth.getInstance().getCurrentUser());
            getCurrentObjectiveAndSetUserPoints();

            CardView mapsBtn = view.findViewById(R.id.map_button);
            mapsBtn.setOnClickListener(mapBtnClickListener);

            CardView achievementButton = view.findViewById(R.id.completed_exp_button);
            achievementButton.setOnClickListener(completedExperienceBtnClickListener);

            CardView userProfileBtn = view.findViewById(R.id.user_button);
            userProfileBtn.setOnClickListener(userBtnClickListener);

            CardView logOutBtn = view.findViewById(R.id.logout_button);
            logOutBtn.setOnClickListener(logOutBtnClickListener);
        } catch (FirebaseDatabase.NullUserException e) {
            Log.d(TAG, "onCreateView: ", e);
            MainActivity mainActivity = (MainActivity)getActivity();
            Objects.requireNonNull(mainActivity).startSignInActivity();
        }
        return view;
    }

    private void drawUserPoints(int points) {
        TextView pointsView = view.findViewById(R.id.points);
        pointsView.setText(String.valueOf(points));
    }

    private void getCurrentObjectiveAndSetUserPoints() {
        database.addDataEventListener(new DataEventListener() {
            @Override
            public void onDataAvailable(ArrayList<Experience> experiences, ArrayList<Zone> zones) {
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
        ImageView experienceImage = view.findViewById(R.id.experience_icon);

        experienceTitle.setText(objective.getName());
        experienceDescription.setText(objective.getDescription());
        InputStream iconImage;
        try {
            IconBuilder builder = new IconBuilder(getContext(), objective);
            iconImage = builder.getExperienceIcon();
            Drawable iconDrawable = Drawable.createFromStream(iconImage, null);
            experienceImage.setImageDrawable(iconDrawable);
        } catch (IOException e) {
            Log.e(TAG, "onCreateView: ", e);;
        }
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
                    Toast.makeText(getActivity(), getString(R.string.user_signed_out_toast), Toast.LENGTH_SHORT).show();
                    Intent firstIntent = new Intent(getActivity(), SignInActivity.class);
                    Handler handler = new Handler();
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(firstIntent);
                                    getActivity().finish();
                                }
                            }
                            , 200);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

}
