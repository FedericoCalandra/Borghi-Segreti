package it.units.borghisegreti.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import it.units.borghisegreti.R;
import it.units.borghisegreti.interfaces.DataEventListener;
import it.units.borghisegreti.models.Experience;
import it.units.borghisegreti.FirebaseDatabase;
import it.units.borghisegreti.models.Zone;
import it.units.borghisegreti.utils.IconBuilder;


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
                            addCardView(experience);
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

    private void addCardView(Experience experience) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.completed_experience_card, linearLayout, false);

        TextView titleView = cardView.findViewById(R.id.completed_exp_title);
        TextView dateView = cardView.findViewById(R.id.completed_exp_date);
        TextView pointsView = cardView.findViewById(R.id.gained_points);
        ImageView experienceIcon = cardView.findViewById(R.id.completed_exp_image);

        titleView.setText(experience.getName());
        dateView.setText(experience.getFormattedDateOfCompletion());
        pointsView.setText(String.format(Locale.getDefault(), getString(R.string.gained_points), experience.getPoints()));
        InputStream iconImage;
        try {
            IconBuilder builder = new IconBuilder(this, experience);
            iconImage = builder.getExperienceIcon();
            Drawable iconDrawable = Drawable.createFromStream(iconImage, null);
            experienceIcon.setImageDrawable(iconDrawable);
        } catch (IOException e) {
            Log.e(TAG, "onCreateView: ", e);;
        }

        linearLayout.addView(cardView);
    }

}
