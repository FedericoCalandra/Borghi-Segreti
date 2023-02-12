package it.units.adventuremaps.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import it.units.adventuremaps.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String username = "Username:   " + Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        String email = "Email:   " + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        String userId = "ID: " + Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(username);
        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(email);
        TextView userIdView = findViewById(R.id.user_id);
        userIdView.setText(userId);
    }

}
