package it.units.adventuremaps;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String s = "Username = " + mAuth.getCurrentUser().getDisplayName();

        TextView textView = findViewById(R.id.usernameTextView);
        textView.setText(s);
    }

}
