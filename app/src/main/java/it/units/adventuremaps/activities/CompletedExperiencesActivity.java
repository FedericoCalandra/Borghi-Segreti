package it.units.adventuremaps.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

import it.units.adventuremaps.interfaces.DataEventListener;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.FirebaseDatabase;
import it.units.adventuremaps.R;
import it.units.adventuremaps.models.Zone;


public class CompletedExperiencesActivity extends AppCompatActivity {

    private static final String TAG = "COMPLETED_EXPERIENCES";
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_experiences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = findViewById(R.id.linear_layout_cards);

        FirebaseDatabase databaseConnector = null;
        try {
            databaseConnector = new FirebaseDatabase(FirebaseAuth.getInstance().getCurrentUser());
            databaseConnector.addDataEventListener(new DataEventListener() {
                @Override
                public void onDataAvailable(ArrayList<Experience> experiences, ArrayList<Zone> zones) {
                    for (Experience experience : experiences) {
                        if (experience.getIsCompletedByUser()) {
                            addCardView(experience.getName(), experience.getFormattedDateOfCompletion(), experience.getPoints());
                        }
                    }
                }

                @Override
                public void onStatusExperiencesChanged(ArrayList<Experience> changedExperiences) {}

                @Override
                public void onPointsUpdated(int points) {}
            });
        } catch (FirebaseDatabase.NullUserException e) {
            Log.e(TAG, "onCreate: ", e);
        }

    }

    private void addCardView(String experienceTitle, String date, int points) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.completed_experience_card, linearLayout, false);

        TextView titleView = cardView.findViewById(R.id.completed_exp_title);
        TextView dateView = cardView.findViewById(R.id.completed_exp_date);
        TextView pointsView = cardView.findViewById(R.id.gained_points);

        titleView.setText(experienceTitle);
        dateView.setText(date);
        pointsView.setText(String.format(Locale.getDefault(), getString(R.string.gained_points), points));

        linearLayout.addView(cardView);
    }

}
