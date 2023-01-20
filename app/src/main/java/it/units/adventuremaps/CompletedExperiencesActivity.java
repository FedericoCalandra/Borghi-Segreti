package it.units.adventuremaps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class CompletedExperiencesActivity extends AppCompatActivity {

    private ViewGroup mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_experiences);
        mLinearLayout = (ViewGroup) findViewById(R.id.completedActivityLayout);

        FirebaseDatabaseConnector databaseConnector = new FirebaseDatabaseConnector(FirebaseAuth.getInstance().getCurrentUser());

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
