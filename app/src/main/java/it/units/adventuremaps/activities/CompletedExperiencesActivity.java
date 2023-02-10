package it.units.adventuremaps.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import it.units.adventuremaps.interfaces.DataEventListener;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.FirebaseDatabase;
import it.units.adventuremaps.R;


public class CompletedExperiencesActivity extends AppCompatActivity {

    private static final String TAG = "COMPLETED_EXPERIENCES";
    private ViewGroup mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_experiences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLinearLayout = (ViewGroup) findViewById(R.id.completedActivityLayout);

        FirebaseDatabase databaseConnector = null;
        try {
            databaseConnector = new FirebaseDatabase(FirebaseAuth.getInstance().getCurrentUser());
            databaseConnector.addDataEventListener(new DataEventListener() {
                @Override
                public void onDataAvailable(ArrayList<Experience> experiences) {
                    for (Experience experience : experiences) {
                        if (experience.getIsCompletedByUser()) {
                            addCardView(experience.getName(), "01/01/2023");
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

    private void addCardView(String experienceTitle, String date) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.completed_experience_card, mLinearLayout, false);

        TextView titleView = (TextView) cardView.findViewById(R.id.completed_exp_title);
        TextView dateView = (TextView) cardView.findViewById(R.id.completed_exp_date);

        titleView.setText(experienceTitle);
        dateView.setText(date);

        mLinearLayout.addView(cardView);
    }

}
