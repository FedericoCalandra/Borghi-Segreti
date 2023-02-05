package it.units.adventuremaps.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import it.units.adventuremaps.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String s = "Username = " + Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        TextView textView = findViewById(R.id.usernameTextView);
        textView.setText(s);
    }

}
